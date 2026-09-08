package com.dom.services.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.dom.model.Employee;
import com.dom.services.DeleteEmployee;
import com.dom.services.ReadXML;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

class DeleteEmployeeTest {

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
    void shouldDeleteExistingEmployee() throws Exception {
        File xml = tempDir.resolve("employees.xml").toFile();
        createXmlWithTwoEmployees(xml);

        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setProperty("employee.xml.path", xml.getAbsolutePath());

        DeleteEmployee.main(new String[0]);

        List<Employee> employees = readEmployees(xml);
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getId()).isEqualTo(2);
        assertThat(out.toString()).contains("Сотрудник удалён.");
    }

    @Test
    void shouldNotDeleteIfIdNotFound() throws Exception {
        File xml = tempDir.resolve("employees.xml").toFile();
        createXmlWithTwoEmployees(xml);

        String input = "999\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setProperty("employee.xml.path", xml.getAbsolutePath());

        DeleteEmployee.main(new String[0]);

        List<Employee> employees = readEmployees(xml);
        assertThat(employees).hasSize(2);
        assertThat(out.toString()).contains("не найден");
    }

    @Test
    void shouldRemoveEmptyTextNodeBeforeEmployee() throws Exception {
        File xml = tempDir.resolve("employees.xml").toFile();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().newDocument();
        Element root = doc.createElement("Employees");
        doc.appendChild(root);

        Text textNode = doc.createTextNode("  ");
        root.appendChild(textNode);

        Element emp1 = doc.createElement("Employee");
        emp1.setAttribute("id", "1");
        root.appendChild(emp1);
        addChild(doc, emp1, "name", "Alice");
        addChild(doc, emp1, "gender", "F");
        addChild(doc, emp1, "age", "25");
        addChild(doc, emp1, "role", "Dev");
        addChild(doc, emp1, "email", "a@a");
        addChild(doc, emp1, "phone", "111");
        addChild(doc, emp1, "address", "Addr");

        Element emp2 = doc.createElement("Employee");
        emp2.setAttribute("id", "2");
        root.appendChild(emp2);
        addChild(doc, emp2, "name", "Bob");
        addChild(doc, emp2, "gender", "M");
        addChild(doc, emp2, "age", "30");
        addChild(doc, emp2, "role", "Mgr");
        addChild(doc, emp2, "email", "b@b");
        addChild(doc, emp2, "phone", "222");
        addChild(doc, emp2, "address", "Addr2");

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(xml));

        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setProperty("employee.xml.path", xml.getAbsolutePath());
        DeleteEmployee.main(new String[0]);

        List<Employee> employees = readEmployees(xml);
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getId()).isEqualTo(2);
    }

    private void addChild(Document doc, Element parent, String tag, String value) {
        Element child = doc.createElement(tag);
        child.setTextContent(value);
        parent.appendChild(child);
    }

    private void createXmlWithTwoEmployees(File file) throws Exception {
        String xml =
                "<?xml version=\"1.0\"?><Employees><Employee"
                    + " id=\"1\"><name>Alice</name><gender>F</gender><age>25</age><role>E</role><email>a@a</email><phone>111</phone><address>Addr</address></Employee><Employee"
                    + " id=\"2\"><name>Bob</name><gender>M</gender><age>30</age><role>M</role><email>b@b</email><phone>222</phone><address>Addr2</address></Employee></Employees>";
        java.nio.file.Files.write(file.toPath(), xml.getBytes());
    }

    private List<Employee> readEmployees(File file) throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var doc = builder.parse(file);
        return ReadXML.getEmployees(doc);
    }
}
