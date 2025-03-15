package ua.ihor.p1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.ihor.p1.domain.EnergyMeter;
import ua.ihor.p1.dto.CreateMeterDto;
import ua.ihor.p1.dto.CreateReadingDto;
import ua.ihor.p1.service.EnergyMeterService;

import java.util.List;

@RestController
@RequestMapping("/energy-meter")
@RequiredArgsConstructor
public class EnergyMeterController {

    private final EnergyMeterService energyMeterService;

    @GetMapping
    public List<EnergyMeter> findAll() {
        return energyMeterService.findAll();
    }

    @GetMapping("/{name}")
    public EnergyMeter findByName(@PathVariable("name") String name) {
        return energyMeterService.findByName(name);
    }

    @PostMapping
    public EnergyMeter createEnergyMeter(@RequestBody CreateMeterDto createMeterDto) {
        EnergyMeter energyMeter = new EnergyMeter();
        energyMeter.setName(createMeterDto.getName());
        return energyMeterService.save(energyMeter);
    }

    @PostMapping("/{name}/reading")
    public EnergyMeter createEnergyMeterReading(@PathVariable String name,
                                                @RequestBody CreateReadingDto createReadingDto) {
        return energyMeterService.saveReading(
                name,
                createReadingDto.getDayReading(),
                createReadingDto.getNightReading()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        energyMeterService.delete(id);
    }
}
