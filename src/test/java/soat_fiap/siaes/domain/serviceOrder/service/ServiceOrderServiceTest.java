package soat_fiap.siaes.domain.serviceOrder.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import soat_fiap.siaes.application.useCase.HelperUseCase;
import soat_fiap.siaes.domain.inventory.model.Item;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;
import soat_fiap.siaes.domain.inventory.service.ItemService;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.service.ServiceLaborService;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatus;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.repository.ServiceOrderRepository;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.model.document.Document;
import soat_fiap.siaes.domain.user.service.UserService;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.service.VehicleService;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderResponse;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.CreateOrderActivityRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.CreateOrderItemRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ServiceOrderServiceTest {

    private ServiceOrderService service;
    private ServiceOrderRepository repository;
    private UserService userService;
    private VehicleService vehicleService;
    private ServiceLaborService serviceLaborService;
    private ItemService itemService;
    private HelperUseCase helperUseCase;
    private User user;
    private Vehicle vehicle;
    private ServiceLabor serviceLabor;
    private Item item;

    @BeforeEach
    void setUp() {
        repository = mock(ServiceOrderRepository.class);
        userService = mock(UserService.class);
        vehicleService = mock(VehicleService.class);
        serviceLaborService = mock(ServiceLaborService.class);
        itemService = mock(ItemService.class);
        helperUseCase = mock(HelperUseCase.class);

        user = new User("Vinicius", "vinicius", "123456", RoleEnum.ADMIN, "930.488.160-97", "vinicius@email.com");
        vehicle = new Vehicle("ABC-1234", "Honda", "Civic", 2022);
        serviceLabor = new ServiceLabor("Troca de óleo", new BigDecimal("150.00"));
        item = new Part("Parafuso", new BigDecimal("2.50"), UnitMeasure.UNIT, 100, 0, "1234567890123", "ABC Indústria", 10);

        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(vehicle, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(serviceLabor, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(item, "id", UUID.randomUUID());

        service = new ServiceOrderService(
                repository,
                userService,
                vehicleService,
                serviceLaborService,
                itemService,
                helperUseCase
        );
    }

    @Test
    void findByUUID__should_return_service_order_when_exists() {
        UUID id = UUID.randomUUID();
        ServiceOrder order = mock(ServiceOrder.class);
        when(repository.findById(id)).thenReturn(Optional.of(order));

        ServiceOrder result = service.findByUUID(id);

        assertThat(result).isEqualTo(order);
    }

    @Test
    void findByUUID__should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByUUID(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Ordem de serviço com ID " + id + " não encontrado");
    }

    @Test
    void findById__should_return_response_when_found() {
        UUID id = UUID.randomUUID();
        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getId()).thenReturn(id);
        when(repository.findById(id)).thenReturn(Optional.of(order));

        ServiceOrderResponse response = service.findById(id);

        assertThat(response).isNotNull();
    }

    @Test
    void findAll__should_return_page_of_service_orders() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID id = UUID.randomUUID();
        ServiceOrder serviceOrder = mock(ServiceOrder.class);
        List<ServiceOrder> list = List.of(serviceOrder);
        Page<ServiceOrder> page = new PageImpl<>(list, pageable, list.size());
        when(repository.findAll(pageable)).thenReturn(page);
        when(serviceOrder.getId()).thenReturn(id);

        Page<ServiceOrderResponse> result = service.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(repository).findAll(pageable);
    }

    @Test
    void findByUserDocument__should_return_page_of_responses() {
        Pageable pageable = PageRequest.of(0, 10);
        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getId()).thenReturn(UUID.randomUUID());
        Page<ServiceOrder> page = new PageImpl<>(List.of(order));
        when(repository.findByUserDocumentValue(any(Document.class), eq(pageable))).thenReturn(page);

        Page<ServiceOrderResponse> result = service.findByUserDocument("930.488.160-97", pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findByUserDocument__should_return_empty_when_none_found() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findByUserDocumentValue(any(Document.class), eq(pageable))).thenReturn(Page.empty());

        Page<ServiceOrderResponse> result = service.findByUserDocument("930.488.160-97", pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByUserId__should_return_page_of_responses() {
        Pageable pageable = PageRequest.of(0, 10);
        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getId()).thenReturn(UUID.randomUUID());
        when(repository.findByUserId(user.getId(), pageable)).thenReturn(new PageImpl<>(List.of(order)));

        Page<ServiceOrderResponse> result = service.findByUserId(user.getId(), pageable);

        assertThat(result).isNotEmpty();
    }

    @Test
    void findByVehicleId__should_return_page_of_responses() {
        Pageable pageable = PageRequest.of(0, 10);
        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getId()).thenReturn(UUID.randomUUID());
        UUID vehicleId = UUID.randomUUID();
        when(repository.findByVehicleId(vehicleId, pageable)).thenReturn(new PageImpl<>(List.of(order)));

        Page<ServiceOrderResponse> result = service.findByVehicleId(vehicleId, pageable);

        assertThat(result).isNotEmpty();
    }

    @Test
    void findByVehiclePlate__should_return_page_of_responses() {
        Pageable pageable = PageRequest.of(0, 10);
        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getId()).thenReturn(UUID.randomUUID());
        when(repository.findByVehiclePlateIgnoreCase("ABC-1234", pageable))
                .thenReturn(new PageImpl<>(List.of(order)));

        Page<ServiceOrderResponse> result = service.findByVehiclePlate("ABC-1234", pageable);

        assertThat(result).isNotEmpty();
    }

    @Test
    void createServiceOrder__should_create_and_return_response() {
        CreateOrderActivityRequest activityRequest = new CreateOrderActivityRequest(UUID.randomUUID(), List.of(new CreateOrderItemRequest(UUID.randomUUID(), 2)));
        ServiceOrderRequest request = new ServiceOrderRequest("12345678900", "ABC-1234", List.of(activityRequest));

        when(userService.findByDocument(request.userDocument())).thenReturn(user);
        when(vehicleService.findByPlateIgnoreCase(request.vehiclePlate())).thenReturn(vehicle);
        when(serviceLaborService.findEntityById(activityRequest.serviceLaborId())).thenReturn(serviceLabor);
        when(itemService.findById(any())).thenReturn(item);
        when(repository.save(any(ServiceOrder.class))).thenAnswer(invocation -> {
            ServiceOrder saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", UUID.randomUUID());
            return saved;
        });

        ServiceOrderResponse response = service.createServiceOrder(request);

        assertThat(response).isNotNull();
        assertThat(response.vehiclePlate()).isEqualTo(vehicle.getPlate());
        assertThat(response.userName()).isEqualTo(user.getName());
        verify(repository).save(any(ServiceOrder.class));
    }

    @Test
    void createServiceOrder__should_create_even_with_empty_activities() {
        ServiceOrderRequest request = new ServiceOrderRequest("12345678900", "ABC-1234", List.of());

        when(userService.findByDocument(request.userDocument())).thenReturn(user);
        when(vehicleService.findByPlateIgnoreCase(request.vehiclePlate())).thenReturn(vehicle);
        when(repository.save(any(ServiceOrder.class))).thenAnswer(invocation -> {
            ServiceOrder saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", UUID.randomUUID());
            return saved;
        });

        ServiceOrderResponse response = service.createServiceOrder(request);

        assertThat(response).isNotNull();
        verify(repository).save(any(ServiceOrder.class));
    }

    @Test
    void createServiceOrder__should_create_activity_with_empty_items() {
        CreateOrderActivityRequest activityRequest = new CreateOrderActivityRequest(UUID.randomUUID(), List.of());
        ServiceOrderRequest request = new ServiceOrderRequest("12345678900", "ABC-1234", List.of(activityRequest));

        when(userService.findByDocument(anyString())).thenReturn(user);
        when(vehicleService.findByPlateIgnoreCase(anyString())).thenReturn(vehicle);
        when(serviceLaborService.findEntityById(any())).thenReturn(serviceLabor);
        when(repository.save(any(ServiceOrder.class))).thenAnswer(invocation -> {
            ServiceOrder saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", UUID.randomUUID());
            return saved;
        });

        ServiceOrderResponse response = service.createServiceOrder(request);

        assertThat(response).isNotNull();
        verify(repository).save(any(ServiceOrder.class));
    }

    @Test
    void updateStatus__should_update_status_when_valid() {
        UUID orderId = UUID.randomUUID();
        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getOrderStatus()).thenReturn(ServiceOrderStatus.APROVADO_CLIENTE);
        when(order.getId()).thenReturn(orderId);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(helperUseCase.carregarUsuarioEximioJWT()).thenReturn(user);
        when(repository.save(order)).thenReturn(order);

        ServiceOrderResponse result = service.updateStatus(orderId, ServiceOrderStatus.EM_EXECUCAO);

        assertThat(result).isNotNull();
        verify(order).updateStatus(ServiceOrderStatus.EM_EXECUCAO);
        verify(repository).save(order);
    }

    @Test
    void updateStatus__should_throw_when_order_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(id, ServiceOrderStatus.EM_EXECUCAO))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete__should_delete_when_exists() {
        UUID id = UUID.randomUUID();
        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getId()).thenReturn(id);
        order.setOrderActivities(List.of(new OrderActivity(order, mock(ServiceLabor.class))));

        when(repository.findById(id)).thenReturn(Optional.of(order));

        service.delete(id);

        verify(repository).delete(order);
        assertThat(order.getOrderActivities()).isEmpty();
    }

    @Test
    void delete__should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Ordem de serviço com ID " + id + " não encontrada");
    }

    @Test
    void delete__should_handle_null_orderActivities() {
        UUID id = UUID.randomUUID();
        ServiceOrder order = new ServiceOrder();
        ReflectionTestUtils.setField(order, "id", id);
        order.setOrderActivities(null);

        when(repository.findById(id)).thenReturn(Optional.of(order));

        service.delete(id);

        verify(repository).delete(order);
    }


    @Test
    void findAllMe__should_return_orders_for_logged_user() {
        Pageable pageable = PageRequest.of(0, 5);

        Page<ServiceOrderResponse> page = mock(Page.class);

        when(helperUseCase.carregarUsuarioEximioJWT()).thenReturn(user);
        when(repository.findByUserId(user.getId(), pageable)).thenReturn(mock(Page.class));

        service.findAllMe(pageable);

        verify(repository).findByUserId(user.getId(), pageable);
    }

    @Test
    void findAllMe__should_return_empty_page_when_user_has_no_orders() {
        Pageable pageable = PageRequest.of(0, 5);
        when(helperUseCase.carregarUsuarioEximioJWT()).thenReturn(user);
        when(repository.findByUserId(user.getId(), pageable)).thenReturn(Page.empty());

        Page<ServiceOrderResponse> result = service.findAllMe(pageable);

        assertThat(result).isEmpty();
    }
}