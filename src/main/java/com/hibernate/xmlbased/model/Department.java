package com.hibernate.xmlbased.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "developers")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Department {

    @Id
    @Column(name = "department_id", length = 3)
    @EqualsAndHashCode.Include
    private String departmentId;

    @Column(name = "department_name", unique = true, nullable = false, length = 100)
    private String departmentName;

    @Column(name = "location", nullable = false)
    private String location;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Developer> developers;

    public Department(String departmentId, String departmentName, String location) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.location = location;
    }
}
