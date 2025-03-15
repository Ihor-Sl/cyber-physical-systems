package ua.ihor.p1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ua.ihor.p1.domain.EnergyMeter;
import ua.ihor.p1.domain.Reading;
import ua.ihor.p1.exception.NotFoundException;
import ua.ihor.p1.repository.EnergyMeterRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnergyMeterServiceTest {

    @Mock
    private EnergyMeterRepository energyMeterRepository;

    @InjectMocks
    private EnergyMeterService energyMeterService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(energyMeterService, "dayTariff", 0.15);
        ReflectionTestUtils.setField(energyMeterService, "nightTariff", 0.10);
        ReflectionTestUtils.setField(energyMeterService, "dayAdditional", 40);
        ReflectionTestUtils.setField(energyMeterService, "nightAdditional", 30);
    }

    @Test
    void testFindAll() {
        List<EnergyMeter> meters = List.of(new EnergyMeter(1L, "Meter1", 0, 0, new ArrayList<>()));
        when(energyMeterRepository.findAll()).thenReturn(meters);

        List<EnergyMeter> result = energyMeterService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Meter1");
    }

    @Test
    void testFindByName_Success() {
        EnergyMeter meter = new EnergyMeter(1L, "Meter1", 0, 0, new ArrayList<>());
        when(energyMeterRepository.findByName("Meter1")).thenReturn(Optional.of(meter));

        EnergyMeter result = energyMeterService.findByName("Meter1");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Meter1");
    }

    @Test
    void testFindByName_NotFound() {
        when(energyMeterRepository.findByName("MeterX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> energyMeterService.findByName("MeterX"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Meter MeterX not found");
    }

    @Test
    void testSave() {
        EnergyMeter meter = new EnergyMeter(null, "Meter1", 0, 0, new ArrayList<>());
        EnergyMeter savedMeter = new EnergyMeter(1L, "Meter1", 0, 0, new ArrayList<>());
        when(energyMeterRepository.save(meter)).thenReturn(savedMeter);

        EnergyMeter result = energyMeterService.save(meter);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Meter1");
    }

    @Test
    void testDelete_Success() {
        EnergyMeter meter = new EnergyMeter(1L, "Meter1", 0, 0, new ArrayList<>());
        when(energyMeterRepository.findById(1L)).thenReturn(Optional.of(meter));

        energyMeterService.delete(1L);

        verify(energyMeterRepository).delete(meter);
    }

    @Test
    void testDelete_NotFound() {
        when(energyMeterRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> energyMeterService.delete(2L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Meter 2 not found");
    }

    @Test
    void testSaveReading() {
        EnergyMeter meter = new EnergyMeter(1L, "Meter1", 0, 0, new ArrayList<>());
        when(energyMeterRepository.findByName("Meter1")).thenReturn(Optional.of(meter));
        when(energyMeterRepository.save(any(EnergyMeter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EnergyMeter result = energyMeterService.saveReading("Meter1", 100, 50);

        assertThat(result.getReadings()).hasSize(1);
        Reading reading = result.getReadings().getFirst();
        assertThat(reading.getDayReading()).isEqualTo(100);
        assertThat(reading.getNightReading()).isEqualTo(50);
        assertThat(reading.getBill()).isCloseTo((100 * 0.15) + (50 * 0.10), within(0.001));
    }

    @Test
    void testSaveReadingWithAdditional() {
        EnergyMeter meter = new EnergyMeter(1L, "Meter1", 100, 50, new ArrayList<>());
        Reading reading = new Reading(1L, 100, 50, 20.0, LocalDateTime.now(), meter);
        meter.getReadings().add(reading);
        when(energyMeterRepository.findByName("Meter1")).thenReturn(Optional.of(meter));
        when(energyMeterRepository.save(any(EnergyMeter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EnergyMeter result = energyMeterService.saveReading("Meter1", 5, 1);

        assertThat(result.getReadings()).hasSize(2);
        Reading resultReading = result.getReadings().get(1);
        assertThat(resultReading.getDayReading()).isEqualTo(40);
        assertThat(resultReading.getNightReading()).isEqualTo(30);
        assertThat(resultReading.getBill()).isCloseTo(9.0, within(0.001));
    }
}
