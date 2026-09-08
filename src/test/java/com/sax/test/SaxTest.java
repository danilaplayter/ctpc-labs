package com.sax.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sax.Car;
import com.sax.CarCatalog;
import com.sax.CarXMLHandler;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.xml.sax.SAXException;

class SaxTest {

    @TempDir Path tempDir;

    @Test
    void shouldParseValidXml() throws Exception {
        File xml = tempDir.resolve("cars.xml").toFile();
        createCarXml(xml);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        CarXMLHandler handler = new CarXMLHandler();
        parser.parse(xml, handler);

        CarCatalog catalog = handler.getCatalog();
        assertThat(catalog.getCars()).hasSize(3);

        List<Car> filtered =
                catalog.getCars().stream()
                        .filter(c -> c.getYear() > 2015 && c.getMileage() < 50000)
                        .toList();
        assertThat(filtered).hasSize(2);
        assertThat(filtered)
                .extracting(Car::getModel)
                .containsExactlyInAnyOrder("Model X", "Model Z");
    }

    @Test
    void shouldThrowExceptionOnMalformedXml() throws Exception {
        File invalid = tempDir.resolve("invalid.xml").toFile();
        java.nio.file.Files.write(invalid.toPath(), "<cars><car>".getBytes());

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        CarXMLHandler handler = new CarXMLHandler();

        assertThatThrownBy(() -> parser.parse(invalid, handler)).isInstanceOf(SAXException.class);
    }

    private void createCarXml(File file) throws Exception {
        String xml =
                "<?xml version=\"1.0\"?><cars><car><brand>BMW</brand><model>Model"
                    + " X</model><year>2016</year><price>25000</price><mileage>30000</mileage></car><car><brand>Audi</brand><model>Model"
                    + " Y</model><year>2014</year><price>20000</price><mileage>60000</mileage></car><car><brand>Toyota</brand><model>Model"
                    + " Z</model><year>2018</year><price>30000</price><mileage>20000</mileage></car></cars>";
        java.nio.file.Files.write(file.toPath(), xml.getBytes());
    }
}
