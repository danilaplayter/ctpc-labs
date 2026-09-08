package com.sax;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CarCatalog {
    private List<Car> cars = new ArrayList<>();

    public void addCar(Car car) {
        cars.add(car);
    }
}
