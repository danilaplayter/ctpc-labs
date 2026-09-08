package com.dom.services.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.dom.model.Employee;
import com.dom.services.ReadXML;
import com.dom.services.WriteXML;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WriteXMLTest {

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
    void shouldWriteZeroEmployees() throws Exception {
        File xml = tempDir.resolve("employees.xml").toFile();
        System.setProperty("employee.xml.path", xml.getAbsolutePath());

        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        WriteXML.main(new String[0]);

        assertThat(xml).exists();
        List<Employee> employees = readEmployees(xml);
        assertThat(employees).isEmpty();
        assertThat(out.toString()).contains("Данные сохранены");
    }

    @Test
    void shouldWriteMultipleEmployees() throws Exception {
        File xml = tempDir.resolve("employees.xml").toFile();
        System.setProperty("employee.xml.path", xml.getAbsolutePath());

        String input = "2\nAlice\nF\n28\nDev\na@a\n111\nAddr1\nBob\nM\n32\nMgr\nb@b\n222\nAddr2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        WriteXML.main(new String[0]);

        List<Employee> employees = readEmployees(xml);
        assertThat(employees).hasSize(2);
        assertThat(employees.get(0).getName()).isEqualTo("Alice");
        assertThat(employees.get(1).getId()).isEqualTo(2);
    }

    @Test
    void testPrivateCreateEmployeeElement() throws Exception {
        var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Employee emp = new Employee();
        emp.setName("Test");
        emp.setGender("M");
        emp.setAge(40);
        emp.setRole("Lead");
        emp.setEmail("t@t");
        emp.setPhone("999");
        emp.setAddress("Addr");

        Method method =
                WriteXML.class.getDeclaredMethod(
                        "createEmployeeElement",
                        Employee.class,
                        int.class,
                        org.w3c.dom.Document.class);
        method.setAccessible(true);
        var element = (org.w3c.dom.Element) method.invoke(null, emp, 7, doc);
        assertThat(element.getAttribute("id")).isEqualTo("7");
        assertThat(element.getAttribute("department")).isEqualTo("IT");
        assertThat(element.getElementsByTagName("name").item(0).getTextContent()).isEqualTo("Test");
    }

    @SuppressWarnings("unchecked")
    private List<Employee> readEmployees(File file) throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var doc = builder.parse(file);
        Method method = ReadXML.class.getDeclaredMethod("getEmployees", org.w3c.dom.Document.class);
        method.setAccessible(true);
        return (List<Employee>) method.invoke(null, doc);
    }
}
