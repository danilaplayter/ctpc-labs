package com.hibernate.xmlbased.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "developers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "specialty")
    private String specialty;

    @Column(name = "experience")
    private int experience;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
