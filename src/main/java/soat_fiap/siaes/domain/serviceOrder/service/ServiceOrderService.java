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
import soat_fiap.siaes.application.event.ServiceOrder.ServiceOrderAwaitingApprovalEvent;
import soat_fiap.siaes.domain.partStock.model.Item;
import soat_fiap.siaes.domain.partStock.service.ItemService;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.service.ServiceLaborService;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrderItem.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrderItemSupply.model.ActivityItem;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.service.UserService;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.service.VehicleService;
import soat_fiap.siaes.infrastructure.persistence.serviceOrder.ServiceOrderRepository;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderResponse;
import soat_fiap.siaes.interfaces.serviceOrderItem.dto.OrderActivityRequest;

import java.time.LocalDateTime;
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

    // ServiceOrder - Ordem de serviço
    // OrderActivity - Cada serviço executado dentro da OS
    // ActivityItem - Insumos ou peças usados no serviço
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
            ServiceLabor labor = serviceLaborService.findByUUID(activityReq.serviceLaborId());
            OrderActivity orderActivity = new OrderActivity(order, labor);

            List<ActivityItem> activityItems = buildActivityItems(activityReq, orderActivity);
            orderActivity.setActivityItems(activityItems);

            orderActivities.add(orderActivity);
        });

        return orderActivities;
    }
    private List<ActivityItem> buildActivityItems(OrderActivityRequest activityReq, OrderActivity activity) {
        List<ActivityItem> items = new ArrayList<>();
        activityReq.items().forEach(itemReq -> {
            Item part = itemService.findById(itemReq.itemId());
            items.add(new ActivityItem(activity, part, itemReq.quantity(), part.getUnitPrice()));
        });
        return items;
    }

    @Transactional
    public ServiceOrderResponse updateStatus(UUID orderId, ServiceOrderStatusEnum status) {
        ServiceOrder order = this.findByUUID(orderId);
        order.setOrderStatusEnum(status);

        // Se a ordem for finalizada, registra o endTime
        if (status == ServiceOrderStatusEnum.FINALIZADA) {
            order.setEndTime(LocalDateTime.now());
        }

        ServiceOrder updatedOrder = this.save(order);

        // Se status for AGUARDANDO_APROVACAO, envia link
        if (updatedOrder.getOrderStatusEnum() == ServiceOrderStatusEnum.AGUARDANDO_APROVACAO) {
            eventPublisher.publishEvent(
                    new ServiceOrderAwaitingApprovalEvent(updatedOrder)
            );
        }
        return new ServiceOrderResponse(updatedOrder);
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
                if (item.getActivityItems() != null) {
                    item.getActivityItems().clear();
                }
            });
            order.getOrderActivities().clear();
        }

        repository.delete(order);
    }
}
