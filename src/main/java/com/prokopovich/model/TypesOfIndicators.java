/**
 * Класс "список" типов приборов учета
 * хранит в себе коллекцию обектов "типы показаний"
 * реализован как синглтон
 */

package com.prokopovich.model;

import java.util.ArrayList;

public class TypesOfIndicators {
    private static TypesOfIndicators instance;
    private ArrayList<TypeOfIndicators> typeOfIndicators;

    private TypesOfIndicators() {
        this.typeOfIndicators = new ArrayList<>();
    }

    public static TypesOfIndicators getInstance() {
        if (instance == null) {
            instance = new TypesOfIndicators();
        }
        return instance;
    }

    public ArrayList<TypeOfIndicators> getTypeOfIndicators() {
        return typeOfIndicators;
    }

    public void addTypeOfIndicator(TypeOfIndicators typeOfIndicator) {
        typeOfIndicators.add(typeOfIndicator);
    }

}