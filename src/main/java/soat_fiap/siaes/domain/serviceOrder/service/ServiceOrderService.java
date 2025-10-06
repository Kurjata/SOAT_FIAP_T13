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
import soat_fiap.siaes.domain.partStock.model.PartStock;
import soat_fiap.siaes.domain.partStock.repository.PartStockRepository;
import soat_fiap.siaes.domain.partStock.service.PartStockService;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.service.ServiceLaborService;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrderItem.model.ServiceOrderItem;
import soat_fiap.siaes.domain.serviceOrderItemSupply.model.ServiceOrderItemSupply;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.service.UserService;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.service.VehicleService;
import soat_fiap.siaes.infrastructure.persistence.ServiceLabor.ServiceLaborRepository;
import soat_fiap.siaes.infrastructure.persistence.serviceOrder.ServiceOrderRepository;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderResponse;

import java.math.BigDecimal;
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
    private final PartStockService partStockService;
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


    @Transactional
    public ServiceOrderResponse createServiceOrder(ServiceOrderRequest request) {
        //Buscar usuário
        User user = this.userService.findByDocument(request.userDocument());
        //Buscar veículo
        Vehicle vehicle = vehicleService.findByPlateIgnoreCase(request.vehiclePlate());
        //Criar ordem de serviço
        ServiceOrder order = new ServiceOrder(user, vehicle, ServiceOrderStatusEnum.RECEBIDA, LocalDateTime.now());
        List<ServiceOrderItem> items = new ArrayList<>();

        //Criar itens de serviço
        for (var itemReq : request.items()) {
            ServiceLabor serviceLabor = serviceLaborService.findByUUID(itemReq.serviceLaborId());

            ServiceOrderItem item = new ServiceOrderItem(order, serviceLabor);

            List<ServiceOrderItemSupply> supplies = new ArrayList<>();

            //Criar insumos para o item
            for (var supplyReq : itemReq.supplies()) {
                PartStock part = partStockService.findById(supplyReq.partStockId());
                supplies.add(new ServiceOrderItemSupply(item, part, supplyReq.quantity(), new BigDecimal(part.getUnitPrice())));
            }

            item.setSupplies(supplies);
            items.add(item);
        }
        order.setItems(items);

        // Salvar ordem (cascade salva itens e insumos)
        ServiceOrder savedOrder = this.save(order);

        return new ServiceOrderResponse(savedOrder);
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
        if (order.getItems() != null) {
            order.getItems().forEach(item -> {
                if (item.getSupplies() != null) {
                    item.getSupplies().clear();
                }
            });
            order.getItems().clear();
        }

        repository.delete(order);
    }
}
