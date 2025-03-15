package ua.ihor.p1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReadingDto {
    private double dayReading;
    private double nightReading;
}
