package soat_fiap.siaes.domain.vehicle.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "vehicle_id", nullable = false, unique = true)
    @Getter
    private UUID id;

    @Getter
    @Setter
    @Column(nullable = false, unique = true)
    private String plate;

    @Getter
    @Setter
    @Column(nullable = false)
    private String brand;

    @Getter
    @Setter
    @Column(nullable = false)
    private String model;

    @Getter
    @Setter
    @Column(name = "factory_year", nullable = false)
    private int year;

    @Deprecated
    public Vehicle() {}

    public Vehicle(String plate, String brand, String model, int year) {
        setPlate(plate);
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public String getIdAsString() {
        return id.toString();
    }

    public void setPlate(String plate) {
        if (plate == null) throw new IllegalArgumentException("Placa não pode ser nula");
        this.plate = formatPlate(plate);
    }

    public String formatPlate(String plate) {
        return plate.replace("-", "").toUpperCase();
    }
}