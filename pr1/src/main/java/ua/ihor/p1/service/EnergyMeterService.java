package ua.ihor.p1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.ihor.p1.domain.EnergyMeter;
import ua.ihor.p1.domain.Reading;
import ua.ihor.p1.exception.NotFoundException;
import ua.ihor.p1.repository.EnergyMeterRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnergyMeterService {

    @Value("${energy.tariff.day}")
    private double dayTariff;
    @Value("${energy.tariff.night}")
    private double nightTariff;
    @Value("${energy.additional.day}")
    private double dayAdditional;
    @Value("${energy.additional.night}")
    private double nightAdditional;

    private final EnergyMeterRepository energyMeterRepository;

    public List<EnergyMeter> findAll() {
        return energyMeterRepository.findAll();
    }

    public EnergyMeter findByName(String name) {
        return energyMeterRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Meter " + name + " not found"));
    }

    public EnergyMeter save(EnergyMeter energyMeter) {
        return energyMeterRepository.save(energyMeter);
    }

    public void delete(Long id) {
        EnergyMeter energyMeter = energyMeterRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Meter " + id + " not found"));
        energyMeterRepository.delete(energyMeter);
    }

    public EnergyMeter saveReading(String meterName, double dayReading, double nightReading) {
        EnergyMeter energyMeter = findByName(meterName);

        List<Reading> readings = energyMeter.getReadings();

        double previousDayReading = energyMeter.getCurrentDayReading();
        double previousNightReading = energyMeter.getCurrentNightReading();

        if (dayReading < previousDayReading) {
            dayReading = previousDayReading + dayAdditional;
        }
        if (nightReading < previousNightReading) {
            nightReading = previousNightReading + nightAdditional;
        }

        double dayDifference = dayReading - previousDayReading;
        double nightDifference = nightReading - previousNightReading;

        double bill = (dayDifference * dayTariff) + (nightDifference * nightTariff);

        Reading newReading = new Reading();
        newReading.setDayReading(dayDifference);
        newReading.setNightReading(nightDifference);
        newReading.setBill(bill);
        newReading.setDate(LocalDateTime.now());
        newReading.setMeter(energyMeter);

        readings.add(newReading);
        energyMeter.setCurrentDayReading(dayReading);
        energyMeter.setCurrentNightReading(nightReading);
        energyMeter.setReadings(readings);

        return energyMeterRepository.save(energyMeter);
    }
}
