//package com.prokopovich.model;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.time.LocalDate;
//import java.util.List;
//import static org.assertj.core.api.Assertions.assertThat;
//
//class ValuesOfMeteringDevicesTest {
//
//    private ValuesOfMeteringDevices valuesInstance;
//    private TypesOfIndicators typesOfIndicators;
//
//    @BeforeEach
//    void setUp() {
//
//        valuesInstance = ValuesOfMeteringDevices.getInstance();
//
//        typesOfIndicators = TypesOfIndicators.getInstance();
//        typesOfIndicators.addTypeOfIndicator(new TypeOfIndicators("ELECTRICITY"));
//
//    }
//
//    @Test
//    void testAddValueOfMeteringDevice_Success() {
//        User user = new User("testUser", "password123", UserRole.USER);
//        TypeOfIndicators type = typesOfIndicators.getTypeOfIndicators().get(0);
//        LocalDate currentDate = LocalDate.now();
//
//        ValueOfMeteringDevices value = new ValueOfMeteringDevices(user, type, currentDate, 100);
//
//        boolean result = valuesInstance.addValueOfMeteringDevice(value);
//
//        assertThat(result).isTrue();
//        assertThat(valuesInstance.getValueOfMeteringDevices(user)).containsExactly(value);
//    }
//
//    @Test
//    void testAddValueOfMeteringDevice_AlreadyAdded() {
//        User user = new User("testUser", "password123", UserRole.USER);
//        TypeOfIndicators type = typesOfIndicators.getTypeOfIndicators().get(0);
//        LocalDate currentDate = LocalDate.now();
//
//        ValueOfMeteringDevices value1 = new ValueOfMeteringDevices(user, type, currentDate, 100);
//        ValueOfMeteringDevices value2 = new ValueOfMeteringDevices(user, type, currentDate, 200);
//
//        valuesInstance.addValueOfMeteringDevice(value1);
//
//        boolean result = valuesInstance.addValueOfMeteringDevice(value2);
//
//        assertThat(result).isFalse();
//        assertThat(valuesInstance.getValueOfMeteringDevices(user)).containsExactly(value1);
//    }
//
//    @Test
//    void testGetValuesForMonthAndYear() {
//        User user = new User("testUser", "password123", UserRole.USER);
//        TypeOfIndicators type = typesOfIndicators.getTypeOfIndicators().get(0);
//        LocalDate currentDate = LocalDate.now();
//
//        ValueOfMeteringDevices value1 = new ValueOfMeteringDevices(user, type, currentDate, 100);
//        ValueOfMeteringDevices value2 = new ValueOfMeteringDevices(user, type, currentDate.minusMonths(1), 200);
//        ValueOfMeteringDevices value3 = new ValueOfMeteringDevices(user, type, currentDate.minusMonths(2), 300);
//
//        valuesInstance.addValueOfMeteringDevice(value1);
//        valuesInstance.addValueOfMeteringDevice(value2);
//        valuesInstance.addValueOfMeteringDevice(value3);
//
//        List<ValueOfMeteringDevices> result = valuesInstance.getValuesForMonthAndYear(user, currentDate.getYear(), currentDate.getMonthValue());
//
//        assertThat(result).containsExactly(value1);
//    }
//
//    @Test
//    void testGetLastValueForUserAndType() {
//        User user = new User("testUser", "password123", UserRole.USER);
//        TypeOfIndicators type = typesOfIndicators.getTypeOfIndicators().get(0);
//        LocalDate currentDate = LocalDate.now();
//
//        ValueOfMeteringDevices value1 = new ValueOfMeteringDevices(user, type, currentDate.minusMonths(1), 100);
//        ValueOfMeteringDevices value2 = new ValueOfMeteringDevices(user, type, currentDate, 200);
//
//        valuesInstance.addValueOfMeteringDevice(value1);
//        valuesInstance.addValueOfMeteringDevice(value2);
//
//        int result = valuesInstance.getLastValueForUserAndType(user, type);
//
//        assertThat(result).isEqualTo(200);
//    }
//}
