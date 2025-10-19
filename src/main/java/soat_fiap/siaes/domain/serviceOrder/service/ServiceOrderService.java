package soat_fiap.siaes.domain.serviceOrder.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.application.event.Part.UpdateStockEvent;
import soat_fiap.siaes.application.useCase.HelperUseCase;
import soat_fiap.siaes.domain.inventory.enums.StockOperation;
import soat_fiap.siaes.domain.inventory.model.Item;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.service.ItemService;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.service.ServiceLaborService;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrder.model.OrderItem;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.service.UserService;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.service.VehicleService;
import soat_fiap.siaes.domain.serviceOrder.repository.ServiceOrderRepository;
import soat_fiap.siaes.shared.BusinessException;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderResponse;
import soat_fiap.siaes.interfaces.serviceOrder.dto.OrderActivityRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServiceOrderService {
    private final ServiceOrderRepository repository;
    private final UserService userService;
    private final VehicleService vehicleService;
    private final ServiceLaborService serviceLaborService;
    private final ItemService itemService;
    private final ApplicationEventPublisher eventPublisher;
    private final HelperUseCase helperUseCase;

    public ServiceOrderResponse findById(UUID id) {
        return new ServiceOrderResponse(this.findByUUID(id));
    }

    public Page<ServiceOrderResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(ServiceOrderResponse::new);
    }

    private ServiceOrder findByUUID(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de serviço com ID " + id + " não encontrado"));
    }

    public Page<ServiceOrderResponse> findByUserDocument(String cpfCnpj, Pageable pageable) {
        return repository.findByUserDocumentValue(cpfCnpj, pageable)
                .map(ServiceOrderResponse::new);
    }

    public Page<ServiceOrderResponse> findByUserId(UUID userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable)
                .map(ServiceOrderResponse::new);
    }

    public Page<ServiceOrderResponse> findByVehicleId(UUID vehicleId, Pageable pageable) {
        return repository.findByVehicleId(vehicleId, pageable)
                .map(ServiceOrderResponse::new);
    }

    public Page<ServiceOrderResponse> findByVehiclePlate(String plate, Pageable pageable) {
        return repository.findByVehiclePlateIgnoreCase(plate, pageable)
                .map(ServiceOrderResponse::new);
    }

    @Transactional
    public ServiceOrderResponse createServiceOrder(ServiceOrderRequest request) {
        User user = this.userService.findByDocument(request.userDocument());
        Vehicle vehicle = vehicleService.findByPlateIgnoreCase(request.vehiclePlate());

        ServiceOrder order = new ServiceOrder(user, vehicle, ServiceOrderStatusEnum.RECEBIDA);
        List<OrderActivity> orderActivities = buildOrderActivities(request, order);
        order.setOrderActivities(orderActivities);

        // Salvar ordem (cascade salva itens e insumos)
        ServiceOrder savedOrder = this.save(order);

        return new ServiceOrderResponse(savedOrder);
    }

    private List<OrderActivity> buildOrderActivities(ServiceOrderRequest serviceOrderRequest, ServiceOrder order) {
        List<OrderActivity> orderActivities = new ArrayList<>();
        serviceOrderRequest.orderActivities().forEach(activityReq -> {
            ServiceLabor labor = serviceLaborService.findEntityById(activityReq.serviceLaborId());
            OrderActivity orderActivity = new OrderActivity(order, labor);

            List<OrderItem> orderItems = buildActivityItems(activityReq, orderActivity);
            orderActivity.setOrderItems(orderItems);

            orderActivities.add(orderActivity);
        });

        return orderActivities;
    }
    private List<OrderItem> buildActivityItems(OrderActivityRequest activityReq, OrderActivity activity) {
        List<OrderItem> items = new ArrayList<>();
        activityReq.items().forEach(itemReq -> {
            Item part = itemService.findById(itemReq.itemId());
            items.add(new OrderItem(activity, part, itemReq.quantity(), part.getUnitPrice()));
        });
        return items;
    }

    @Transactional
    public ServiceOrderResponse updateStatus(UUID orderId, ServiceOrderStatusEnum status) {
        ServiceOrder order = this.findByUUID(orderId);
        User usuarioLogado = helperUseCase.carregarUsuarioEximioJWT();

        validatePermissionForStatus(order, status, usuarioLogado.getRole());
        order.setUpdateStatus(status);

        order = this.save(order);

        switch (order.getOrderStatusEnum()) {
            case APROVADO_CLIENTE -> {
                try {
                    eventPublisher.publishEvent(new UpdateStockEvent(order, StockOperation.RESERVE_STOCK));
                } catch (BusinessException e) {
                    order.setUpdateStatus(ServiceOrderStatusEnum.AGUARDANDO_ESTOQUE);
                    this.save(order);
                    throw e; // garante rollback da transação
                }
            }
            case REPROVADO_CLIENTE -> {
                boolean
                        hasReservedItems = order.getOrderActivities().stream()
                        .flatMap(activity -> activity.getOrderItems().stream())
                        .map(OrderItem::getPartStock)
                        .filter(item -> item instanceof Part)
                        .map(item -> (Part) item)
                        .anyMatch(part -> part.getReservedQuantity() != null && part.getReservedQuantity() > 0);

                if (status == ServiceOrderStatusEnum.REPROVADO_CLIENTE && hasReservedItems) {
                    eventPublisher.publishEvent(new UpdateStockEvent(order, StockOperation.CANCEL_RESERVATION));
                }
            }
            case EM_EXECUCAO -> eventPublisher.publishEvent(new UpdateStockEvent(order, StockOperation.CONFIRM_RESERVATION));
        }

        return new ServiceOrderResponse(order);
    }

    private void validatePermissionForStatus(ServiceOrder order, ServiceOrderStatusEnum novoStatus, RoleEnum role) {
        ServiceOrderStatusEnum.validatePermissionForStatus(order, novoStatus, role);
    }

    @Transactional
    public ServiceOrder save(ServiceOrder serviceOrder) {
        try {
            return repository.save(serviceOrder);
        } catch (DataIntegrityViolationException e) {
            // Lançada quando há violação de chave única, FK, NOT NULL etc.
            throw new DataIntegrityViolationException(
                    "Erro de integridade dos dados ao salvar a ordem de serviço. Verifique se todos os campos obrigatórios foram preenchidos e se não há duplicações.",
                    e
            );
        } catch (ConstraintViolationException e) {
            // Lançada quando validações do Bean Validation falham
            throw new ConstraintViolationException(
                    "Violação de restrição ao salvar a ordem de serviço. Alguns campos podem estar inválidos.",
                    e.getConstraintViolations()
            );
        } catch (EntityNotFoundException e) {
            // Caso alguma entidade referenciada não exista
            throw new EntityNotFoundException(
                    "Uma entidade relacionada à ordem de serviço não foi encontrada. Verifique usuário, veículo ou itens."
            );
        } catch (Exception e) {
            // Qualquer outro erro inesperado
            throw new RuntimeException(
                    "Erro inesperado ao salvar a ordem de serviço. Detalhes: " + e.getMessage(),
                    e
            );
        }
    }

    @Transactional
    public void delete(UUID orderId) {
        ServiceOrder order = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de serviço com ID " + orderId + " não encontrada"));

        // Excluir todos os itens e insumos da ordem
        if (order.getOrderActivities() != null) {
            order.getOrderActivities().forEach(item -> {
                if (item.getOrderItems() != null) {
                    item.getOrderItems().clear();
                }
            });
            order.getOrderActivities().clear();
        }

        repository.delete(order);
    }

    public Page<ServiceOrderResponse> findAllMe(Pageable pageable) {
        User usuarioLogado = helperUseCase.carregarUsuarioEximioJWT();
        return this.findByUserId(usuarioLogado.getId(), pageable);
    }
}
