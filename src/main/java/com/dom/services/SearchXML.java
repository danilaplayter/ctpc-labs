package com.dom.services;

import com.dom.model.Employee;
import java.io.File;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SearchXML {

    public static void main(String[] args) throws Exception {
        String path = System.getProperty("employee.xml.path", "src/main/resources/employee.xml");
        File file = new File(path);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите id сотрудника: ");
        int id = scanner.nextInt();

        Employee emp = findEmployee(document, id);
        if (emp == null) {
            System.out.println("Сотрудник с id=" + id + " не найден");
        } else {
            System.out.println(emp);
        }
        scanner.close();
    }

    private static Employee findEmployee(Document document, int id) {
        NodeList list = document.getElementsByTagName("Employee");
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("id").equals(String.valueOf(id))) {
                    return parseEmployee(element);
                }
            }
        }
        return null;
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
        return list.getLength() == 0 ? "" : list.item(0).getTextContent();
    }
}
