package com.dom.services.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.dom.services.SearchXML;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SearchXMLTest {

    @TempDir Path tempDir;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final PrintStream origOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void restore() {
        System.setOut(origOut);
        System.setIn(System.in);
    }

    @Test
    void shouldFindEmployee() throws Exception {
        File xml = tempDir.resolve("employees.xml").toFile();
        createXml(xml);

        String input = "2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setProperty("employee.xml.path", xml.getAbsolutePath());

        SearchXML.main(new String[0]);

        assertThat(out.toString()).contains("id=2");
        assertThat(out.toString()).contains("Bob");
    }

    @Test
    void shouldNotFindEmployee() throws Exception {
        File xml = tempDir.resolve("employees.xml").toFile();
        createXml(xml);

        String input = "999\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setProperty("employee.xml.path", xml.getAbsolutePath());

        SearchXML.main(new String[0]);

        assertThat(out.toString()).contains("не найден");
    }

    @Test
    void testParseEmployee() throws Exception {
        var doc =
                javax.xml.parsers.DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .newDocument();
        var element = doc.createElement("Employee");
        element.setAttribute("id", "10");
        element.appendChild(createNode(doc, "name", "Test"));
        element.appendChild(createNode(doc, "gender", "M"));
        element.appendChild(createNode(doc, "age", "40"));
        element.appendChild(createNode(doc, "role", "Lead"));
        element.appendChild(createNode(doc, "email", "t@t"));
        element.appendChild(createNode(doc, "phone", "789"));
        element.appendChild(createNode(doc, "address", "Addr"));

        var method = SearchXML.class.getDeclaredMethod("parseEmployee", org.w3c.dom.Element.class);
        method.setAccessible(true);
        var emp = (com.dom.model.Employee) method.invoke(null, element);
        assertThat(emp.getId()).isEqualTo(10);
        assertThat(emp.getName()).isEqualTo("Test");
    }

    private org.w3c.dom.Node createNode(org.w3c.dom.Document doc, String tag, String value) {
        var el = doc.createElement(tag);
        el.setTextContent(value);
        return el;
    }

    private void createXml(File file) throws Exception {
        String xml =
                "<?xml version=\"1.0\"?><Employees><Employee"
                    + " id=\"1\"><name>Alice</name><gender>F</gender><age>25</age><role>E</role><email>a@a</email><phone>111</phone><address>Addr</address></Employee><Employee"
                    + " id=\"2\"><name>Bob</name><gender>M</gender><age>30</age><role>M</role><email>b@b</email><phone>222</phone><address>Addr2</address></Employee></Employees>";
        java.nio.file.Files.write(file.toPath(), xml.getBytes());
    }
}
