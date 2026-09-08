package com.jaxb.model;

import jakarta.xml.bind.annotation.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "department")
@XmlAccessorType(XmlAccessType.FIELD)
public class Department {
    private String departmentNumber;
    private String departmentName;
    private String location;

    public Department(String departmentNumber, String departmentName, String location) {
        this.departmentNumber = departmentNumber;
        this.departmentName = departmentName;
        this.location = location;
    }

    @XmlElementWrapper(name = "employees")
    @XmlElement(name = "employee")
    private List<Employee> employees;
}
