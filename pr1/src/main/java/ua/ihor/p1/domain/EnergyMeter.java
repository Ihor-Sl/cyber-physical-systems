package ua.ihor.p1.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "energy_meter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyMeter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double currentDayReading;
    private double currentNightReading;
    @OneToMany(mappedBy = "meter", cascade = CascadeType.ALL)
    private List<Reading> readings;
}
