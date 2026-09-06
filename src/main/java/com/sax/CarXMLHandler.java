package com.sax;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class CarXMLHandler extends DefaultHandler {
    private CarCatalog catalog = new CarCatalog();
    private Car currentCar;
    private StringBuilder chars = new StringBuilder();

    public CarCatalog getCatalog() {
        return catalog;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        chars.setLength(0);
        if (qName.equalsIgnoreCase("car")) {
            currentCar = new Car();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        String content = chars.toString().trim();
        if (qName.equalsIgnoreCase("car")) {
            catalog.addCar(currentCar);
        } else if (qName.equalsIgnoreCase("brand")) {
            currentCar.setBrand(content);
        } else if (qName.equalsIgnoreCase("model")) {
            currentCar.setModel(content);
        } else if (qName.equalsIgnoreCase("year")) {
            currentCar.setYear(Integer.parseInt(content));
        } else if (qName.equalsIgnoreCase("price")) {
            currentCar.setPrice(Double.parseDouble(content));
        } else if (qName.equalsIgnoreCase("mileage")) {
            currentCar.setMileage(Integer.parseInt(content));
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        chars.append(ch, start, length);
    }
}
