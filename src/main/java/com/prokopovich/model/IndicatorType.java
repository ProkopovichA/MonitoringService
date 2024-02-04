/**
 *Класс типы показаний (типы приборов учета)
 */

package com.prokopovich.model;

import lombok.Getter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IndicatorType {
    @Getter
    private int id;

    @Getter
    private String typeName;

    @Override
    public String toString() {
        return typeName;
    }
}
