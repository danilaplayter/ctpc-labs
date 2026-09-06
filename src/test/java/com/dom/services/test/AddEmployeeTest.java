package com.dom.services.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dom.model.Employee;
import com.dom.services.AddEmployee;
import com.dom.services.ReadXML;
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

class AddEmployeeTest {

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
    void shouldAddEmployeeToEmptyFile() throws Exception {
        File xml = tempDir.resolve("employees.xml").toFile();
        String emptyXml = "<?xml version=\"1.0\"?><Employees/>";
        java.nio.file.Files.write(xml.toPath(), emptyXml.getBytes());

        String input = "John\nMale\n25\nDev\njohn@mail.com\n123\nAddr\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setProperty("employee.xml.path", xml.getAbsolutePath());

        AddEmployee.main(new String[0]);

        List<Employee> employees = readEmployees(xml);
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getId()).isEqualTo(1);
        assertThat(employees.get(0).getName()).isEqualTo("John");
        assertThat(out.toString()).contains("Сотрудник добавлен.");
    }

    @Test
    void shouldAddEmployeeToExistingFile() throws Exception {
        File xml = tempDir.resolve("employees.xml").toFile();
        createInitialXml(xml);

        String input = "Jane\nFemale\n30\nQA\njane@mail.com\n456\nStreet\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setProperty("employee.xml.path", xml.getAbsolutePath());

        AddEmployee.main(new String[0]);

        List<Employee> employees = readEmployees(xml);
        assertThat(employees).hasSize(2);
        assertThat(employees.get(1).getId()).isEqualTo(2);
        assertThat(employees.get(1).getName()).isEqualTo("Jane");
    }

    @Test
    void shouldHandleNumberFormatExceptionForAge() throws Exception {
        File xml = tempDir.resolve("employees.xml").toFile();
        createInitialXml(xml);

        String input = "Test\nMale\nnotANumber\nDev\ntest@mail.com\n111\nAddr\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setProperty("employee.xml.path", xml.getAbsolutePath());

        assertThatThrownBy(() -> AddEmployee.main(new String[0]))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void testPrivateCreateEmployeeElement() throws Exception {
        var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Employee emp = new Employee();
        emp.setName("John");
        emp.setGender("M");
        emp.setAge(25);
        emp.setRole("Dev");
        emp.setEmail("j@j");
        emp.setPhone("123");
        emp.setAddress("Addr");

        Method method =
                AddEmployee.class.getDeclaredMethod(
                        "createEmployeeElement",
                        Employee.class,
                        int.class,
                        org.w3c.dom.Document.class);
        method.setAccessible(true);
        var element = (org.w3c.dom.Element) method.invoke(null, emp, 5, doc);
        assertThat(element.getAttribute("id")).isEqualTo("5");
        assertThat(element.getElementsByTagName("name").item(0).getTextContent()).isEqualTo("John");
    }

    private void createInitialXml(File file) throws Exception {
        String xml =
                "<?xml version=\"1.0\"?><Employees><Employee"
                    + " id=\"1\"><name>Alice</name><gender>F</gender><age>25</age><role>E</role><email>a@a</email><phone>111</phone><address>Addr</address></Employee></Employees>";
        java.nio.file.Files.write(file.toPath(), xml.getBytes());
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
