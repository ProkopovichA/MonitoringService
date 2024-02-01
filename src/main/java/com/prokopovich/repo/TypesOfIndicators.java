/**
 * Класс "список" типов приборов учета
 * хранит в себе коллекцию обектов "типы показаний"
 * реализован как синглтон
 */

package com.prokopovich.repo;

import com.prokopovich.model.IndicatorType;

import java.util.ArrayList;

public class TypesOfIndicators {
    private static TypesOfIndicators instance;
    private ArrayList<IndicatorType> typeOfIndicators;

    private TypesOfIndicators() {
        this.typeOfIndicators = new ArrayList<>();
    }

    public static TypesOfIndicators getInstance() {
        if (instance == null) {
            instance = new TypesOfIndicators();
        }
        return instance;
    }

    public ArrayList<IndicatorType> getTypeOfIndicators() {
        return typeOfIndicators;
    }

    public void addTypeOfIndicator(IndicatorType typeOfIndicator) {
        typeOfIndicators.add(typeOfIndicator);
    }

}