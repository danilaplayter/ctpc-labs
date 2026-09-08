package com.jaxb.test;

import com.jaxb.model.Department;
import com.jaxb.model.Employee;
import com.jaxb.model.Organization;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestJAXB {
    private static final String XML_FILE = "organization.xml";

    public static void main(String[] args) throws Exception {
        Employee emp1 = new Employee("E01", "Tom", null);
        Employee emp2 = new Employee("E02", "Mary", "E01");
        Employee emp3 = new Employee("E03", "John", null);

        List<Employee> employees = new ArrayList<>();
        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);

        Department dept = new Department("D01", "ACCOUNTING", "NEW YORK");
        dept.setEmployees(employees);

        List<Department> departments = new ArrayList<>();
        departments.add(dept);
        Organization org = new Organization("MyCompany", departments);

        JAXBContext context = JAXBContext.newInstance(Organization.class);

        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(org, System.out);
        marshaller.marshal(org, new File(XML_FILE));

        System.out.println("Записано в файл: " + new File(XML_FILE).getAbsolutePath());

        Unmarshaller unmarshaller = context.createUnmarshaller();
        Organization readOrg = (Organization) unmarshaller.unmarshal(new File(XML_FILE));

        System.out.println("\nПрочитано из файла:");
        System.out.println("Организация: " + readOrg.getName());
        for (Department d : readOrg.getDepartments()) {
            System.out.println("  Департамент: " + d.getDepartmentName());
            for (Employee e : d.getEmployees()) {
                System.out.println("    Сотрудник: " + e.getEmployeeName());
            }
        }
    }
}
