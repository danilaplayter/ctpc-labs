package com.dom.services;

import com.dom.model.Employee;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AddEmployee {

    public static void main(String[] args) throws Exception {
        String path = System.getProperty("employee.xml.path", "src/main/resources/employee.xml");
        File file = new File(path);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);

        System.out.print("Имя: ");
        String name = input.readLine();
        System.out.print("Пол: ");
        String gender = input.readLine();
        System.out.print("Возраст: ");
        int age = Integer.parseInt(input.readLine());
        System.out.print("Должность: ");
        String role = input.readLine();
        System.out.print("Email: ");
        String email = input.readLine();
        System.out.print("Телефон: ");
        String phone = input.readLine();
        System.out.print("Адрес: ");
        String address = input.readLine();

        Employee emp = new Employee();
        emp.setName(name);
        emp.setGender(gender);
        emp.setAge(age);
        emp.setRole(role);
        emp.setEmail(email);
        emp.setPhone(phone);
        emp.setAddress(address);

        NodeList list = document.getElementsByTagName("Employee");
        int newId = list.getLength() + 1;

        Element empElement = createEmployeeElement(emp, newId, document);
        document.getDocumentElement().appendChild(empElement);

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        System.out.println("Сотрудник добавлен.");
    }

    private static Element createEmployeeElement(Employee emp, int id, Document doc) {
        Element element = doc.createElement("Employee");
        element.setAttribute("id", String.valueOf(id));
        element.setAttribute("department", "IT");

        element.appendChild(createPropertyNode("name", emp.getName(), doc));
        element.appendChild(createPropertyNode("gender", emp.getGender(), doc));
        element.appendChild(createPropertyNode("age", String.valueOf(emp.getAge()), doc));
        element.appendChild(createPropertyNode("role", emp.getRole(), doc));
        element.appendChild(createPropertyNode("email", emp.getEmail(), doc));
        element.appendChild(createPropertyNode("phone", emp.getPhone(), doc));
        element.appendChild(createPropertyNode("address", emp.getAddress(), doc));

        return element;
    }

    private static Element createPropertyNode(String tag, String value, Document doc) {
        Element el = doc.createElement(tag);
        el.setTextContent(value);
        return el;
    }
}
