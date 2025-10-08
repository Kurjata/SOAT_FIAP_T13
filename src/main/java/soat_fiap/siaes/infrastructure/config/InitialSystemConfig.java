package soat_fiap.siaes.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.partStock.model.Part;
import soat_fiap.siaes.domain.partStock.model.UnitMeasure;
import soat_fiap.siaes.domain.partStock.repository.PartRepository;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.repository.VehicleRepository;
import soat_fiap.siaes.infrastructure.persistence.ServiceLabor.ServiceLaborRepository;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class InitialSystemConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceLaborRepository serviceLaborRepository;
    private final VehicleRepository vehicleRepository;
    private final PartRepository partRepository;

    @Override
    @Transactional
    public void run(String... args) {
        //Criar usuários
        createUserIfNotExist("admin", "Administrator", "admin", RoleEnum.ADMIN, "239.413.650-29", "admin@email.com");
        createUserIfNotExist("collaborator", "Collaborator da silva", "collaborator", RoleEnum.COLLABORATOR, "27.295.338/0001-74", "collaborator@email.com");
        createUserIfNotExist("elisa.goncalves", "Elisa Mirella Daniela Gonçalves", "123", RoleEnum.CLIENT, "382.282.180-28", "elisa.goncalves@email.com");
        createUserIfNotExist("alicia.monteiro", "Alícia Laura Emanuelly Monteiro", "123", RoleEnum.CLIENT, "711.172.882-34", "alicia.monteiro@email.com");

        //Criar serviços
        createServiceLaborIfNotExist("Troca de Óleo", new BigDecimal("120.00"));
        createServiceLaborIfNotExist("Alinhamento e Balanceamento", new BigDecimal("180.00"));
        createServiceLaborIfNotExist("Substituição de Pastilhas de Freio", new BigDecimal("250.00"));


        //Criar veículos
        createVehicleIfNotExist("ABC1234", "Toyota", "Corolla", 2020);
        createVehicleIfNotExist("DEF5678", "Volkswagen", "Golf", 2019);
        createVehicleIfNotExist("GHI9012", "Honda", "CR-V", 2021);

        //Criar insumos
        createPartStockIfNotExist("7891234567890", "Filtro de Óleo Motor", 120, 20, BigDecimal.valueOf(35.0));
        createPartStockIfNotExist("7899876543210", "Velas de Ignição", 80, 15, BigDecimal.valueOf(12.5));
        createPartStockIfNotExist("7894561237890", "Pastilhas de Freio Dianteira", 50, 10, BigDecimal.valueOf(95.0));

    }

    private void createUserIfNotExist(String login, String name, String password, RoleEnum role, String document, String email) {
        userRepository.findByLogin(login)
                .orElseGet(() -> {
                    User user = new User(name, login, passwordEncoder.encode(password), role, document, email);
                    return userRepository.save(user);
                });
    }

    private void createVehicleIfNotExist(String plate, String brand, String model, Integer year) {
        vehicleRepository.findByPlateIgnoreCase(plate)
                .orElseGet(() -> {
                    Vehicle vehicle = new Vehicle(plate, brand, model, year);
                    return vehicleRepository.save(vehicle);
                });
    }

    private void createServiceLaborIfNotExist(String description, BigDecimal laborCost) {
        serviceLaborRepository.findByDescription(description)
                .orElseGet(() -> {
                    ServiceLabor labor = new ServiceLabor();
                    labor.setDescription(description);
                    labor.setLaborCost(laborCost);
                    return serviceLaborRepository.save(labor);
                });
    }

    private void createPartStockIfNotExist(String ean, String name, Integer stockQuantity, Integer minimumStock, BigDecimal unitPrice) {
        partRepository.findByEan(ean)
                .orElseGet(() -> {
                    Part partStock = new Part(name, unitPrice, UnitMeasure.UNIT, stockQuantity, 0, ean, "teste", minimumStock);
                    return partRepository.save(partStock);
                });
    }
}
