/**
 * Класс содержит запись показаний прибора учета
 */
package com.prokopovich.model;


import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@ToString
public class ValueOfMeteringDevices {
    @Getter
    private User user;
    @Getter
    private TypeOfIndicators typeOfIndicators;
    @Getter
    private LocalDate dateOfValue;
    @Getter
    private int value;
}
