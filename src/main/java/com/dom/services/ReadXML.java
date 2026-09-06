package com.dom.services;

import com.dom.model.Employee;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReadXML {

    public static void main(String[] args) {
        String path = System.getProperty("employee.xml.path", "src/main/resources/employee.xml");
        File file = new File(path);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            System.out.println("Корневой элемент: " + document.getDocumentElement().getNodeName());
            List<Employee> employees = getEmployees(document);
            for (Employee emp : employees) {
                System.out.println(emp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> getEmployees(Document document) {
        NodeList list = document.getElementsByTagName("Employee");
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                employees.add(parseEmployee((Element) node));
            }
        }
        return employees;
    }

    private static Employee parseEmployee(Element element) {
        Employee emp = new Employee();
        emp.setId(Integer.parseInt(element.getAttribute("id")));
        emp.setName(getTagValue(element, "name"));
        emp.setGender(getTagValue(element, "gender"));
        emp.setAge(Integer.parseInt(getTagValue(element, "age")));
        emp.setRole(getTagValue(element, "role"));
        emp.setEmail(getTagValue(element, "email"));
        emp.setPhone(getTagValue(element, "phone"));
        emp.setAddress(getTagValue(element, "address"));
        return emp;
    }

    private static String getTagValue(Element element, String tag) {
        NodeList list = element.getElementsByTagName(tag);
        if (list.getLength() == 0) return "";
        return list.item(0).getTextContent();
    }
}
