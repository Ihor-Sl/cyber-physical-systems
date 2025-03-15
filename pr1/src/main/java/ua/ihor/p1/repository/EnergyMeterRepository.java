package ua.ihor.p1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.ihor.p1.domain.EnergyMeter;

import java.util.Optional;

public interface EnergyMeterRepository extends JpaRepository<EnergyMeter, Long> {
    Optional<EnergyMeter> findByName(String name);
}
