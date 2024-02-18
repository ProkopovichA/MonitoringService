/**
 * Класс "список" типов приборов учета
 * хранит в себе коллекцию обектов "типы показаний"
 * реализован как синглтон
 */

package com.prokopovich.repo;

import com.prokopovich.model.IndicatorType;

import java.util.ArrayList;

public interface IndicatorTypeRepo {
    ArrayList<IndicatorType> getTypeOfIndicators();

    IndicatorType findIndicatorTypeById(int id);

    boolean addTypeOfIndicator(IndicatorType typeOfIndicator);

}