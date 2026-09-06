package com.dom.services;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DeleteEmployee {

    public static void main(String[] args) throws Exception {
        String path = System.getProperty("employee.xml.path", "src/main/resources/employee.xml");
        File file = new File(path);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);

        System.out.print("Введите id для удаления: ");
        int id = Integer.parseInt(input.readLine());

        NodeList list = document.getElementsByTagName("Employee");
        boolean deleted = false;
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("id").equals(String.valueOf(id))) {
                    Node parent = element.getParentNode();
                    parent.removeChild(element);
                    document.normalize();
                    deleted = true;
                    break;
                }
            }
        }

        if (deleted) {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
            System.out.println("Сотрудник удалён.");
        } else {
            System.out.println("Сотрудник с id=" + id + " не найден.");
        }
    }
}
