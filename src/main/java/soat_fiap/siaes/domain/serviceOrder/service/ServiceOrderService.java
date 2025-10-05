package soat_fiap.siaes.domain.serviceOrder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.application.event.ServiceOrder.ServiceOrderAwaitingApprovalEvent;
import soat_fiap.siaes.domain.partStock.model.PartStock;
import soat_fiap.siaes.domain.partStock.repository.PartStockRepository;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrderItem.model.ServiceOrderItem;
import soat_fiap.siaes.domain.serviceOrderItemSupply.model.ServiceOrderItemSupply;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.repository.VehicleRepository;
import soat_fiap.siaes.infrastructure.persistence.ServiceLabor.ServiceLaborRepository;
import soat_fiap.siaes.infrastructure.persistence.serviceOrder.ServiceOrderRepository;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderResponse;
import soat_fiap.siaes.interfaces.user.document.DocumentFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServiceOrderService {
    private final ServiceOrderRepository repository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceLaborRepository serviceLaborRepository;
    private final PartStockRepository partStockRepository;
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
        User user = userRepository.findByDocument(DocumentFactory.fromString(request.userDocument()))
                .orElseThrow(() -> new EntityNotFoundException("Usuário com documento " + request.userDocument() + " não encontrado"));

        //Buscar veículo
        Vehicle vehicle = vehicleRepository.findByPlateIgnoreCase(request.vehiclePlate())
                .orElseThrow(() -> new EntityNotFoundException("Veículo com placa " + request.vehiclePlate() + " não encontrado"));

        //Criar ordem de serviço
        ServiceOrder order = new ServiceOrder();
        order.setUser(user);
        order.setVehicle(vehicle);
        order.setOrderStatusEnum(ServiceOrderStatusEnum.RECEBIDA);
        order.setStartTime(LocalDateTime.now()); // startTime definido automaticamente

        List<ServiceOrderItem> items = new ArrayList<>();

        //Criar itens de serviço
        for (var itemReq : request.items()) {
            ServiceLabor serviceLabor = serviceLaborRepository.findById(itemReq.serviceLaborId())
                    .orElseThrow(() -> new EntityNotFoundException("Serviço de mão de obra com ID " + itemReq.serviceLaborId() + " não encontrado"));

            ServiceOrderItem item = new ServiceOrderItem();
            item.setServiceOrder(order);
            item.setServiceLabor(serviceLabor);

            List<ServiceOrderItemSupply> supplies = new ArrayList<>();

            //Criar insumos para o item
            for (var supplyReq : itemReq.supplies()) {
                PartStock part = partStockRepository.findById(supplyReq.partStockId())
                        .orElseThrow(() -> new EntityNotFoundException("Insumo com ID " + supplyReq.partStockId() + " não encontrado"));

                ServiceOrderItemSupply supply = new ServiceOrderItemSupply();
                supply.setServiceOrderItem(item);
                supply.setPartStock(part);
                supply.setQuantity(supplyReq.quantity());
                supply.setUnitPrice(new BigDecimal(part.getUnitPrice())); // pega preço atual do estoque
                supplies.add(supply);
            }

            item.setSupplies(supplies);
            items.add(item);
        }
        order.setItems(items);

        // Salvar ordem (cascade salva itens e insumos)
        ServiceOrder savedOrder = repository.save(order);

        return new ServiceOrderResponse(savedOrder);
    }

    @Transactional
    public ServiceOrderResponse updateStatus(UUID orderId, ServiceOrderStatusEnum status) {
        ServiceOrder order = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de serviço com ID " + orderId + " não encontrada"));

        order.setOrderStatusEnum(status);

        // Se a ordem for finalizada, registra o endTime
        if (status == ServiceOrderStatusEnum.FINALIZADA) {
            order.setEndTime(LocalDateTime.now());
        }

        ServiceOrder updatedOrder = repository.save(order);

        // Se status for AGUARDANDO_APROVACAO, envia link
        if (updatedOrder.getOrderStatusEnum() == ServiceOrderStatusEnum.AGUARDANDO_APROVACAO) {
            eventPublisher.publishEvent(
                    new ServiceOrderAwaitingApprovalEvent(updatedOrder)
            );
        }

        return new ServiceOrderResponse(updatedOrder);
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
