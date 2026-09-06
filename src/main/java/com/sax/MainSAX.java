package com.sax;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

public class MainSAX {
    public static void main(String[] args) {
        int filterYear = 2015;
        int filterMileage = 50000;
        String path = "cars.xml";

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            CarXMLHandler handler = new CarXMLHandler();
            parser.parse(new File(path), handler);

            CarCatalog catalog = handler.getCatalog();
            System.out.println("Все автомобили:\n" + catalog);

            List<Car> filtered =
                    catalog.getCars().stream()
                            .filter(c -> c.getYear() > filterYear && c.getMileage() < filterMileage)
                            .collect(Collectors.toList());

            System.out.println(
                    "\nАвтомобили после "
                            + filterYear
                            + " г. с пробегом < "
                            + filterMileage
                            + " км:");
            filtered.forEach(System.out::println);

        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }
}
