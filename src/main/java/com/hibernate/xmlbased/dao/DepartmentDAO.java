package com.hibernate.xmlbased.dao;

import com.hibernate.xmlbased.config.SessionConfig;
import com.hibernate.xmlbased.model.Department;
import com.hibernate.xmlbased.model.Developer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class DepartmentDAO {

    private final SessionFactory sessionFactory;

    public DepartmentDAO() {
        this.sessionFactory = SessionConfig.getInstance().getSessionFactory();
    }

    public DepartmentDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Department> getDepartments() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            List<Department> deps =
                    session.createQuery("FROM Department", Department.class).getResultList();
            tx.commit();
            return deps;
        }
    }

    public Set<Department> getDepartmentWithWorkers() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Set<Department> departments =
                    new HashSet<>(
                            session.createQuery(
                                            "FROM Department d LEFT JOIN FETCH d.developers",
                                            Department.class)
                                    .getResultList());
            tx.commit();
            return departments;
        }
    }

    public List<Developer> getDevelopersByDepartment(Department department) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Department dep = session.get(Department.class, department.getDepartmentId());
            Hibernate.initialize(dep.getDevelopers());
            List<Developer> devs = dep.getDevelopers();
            tx.commit();
            return devs;
        }
    }

    public Department findDepartmentByID(String departmentID) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Department department = session.get(Department.class, departmentID);
            tx.commit();
            return department;
        }
    }

    public Department addDepartament(Department dep) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(dep);
            tx.commit();
            return dep;
        }
    }

    public void deleteDepartment(String ID) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Department department = session.get(Department.class, ID);
            if (department != null) {
                session.remove(department);
            }
            tx.commit();
        }
    }

    public List<Department> findDepartmentByLetterInID(char letter) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            String hql = "FROM Department d WHERE d.departmentId LIKE :pattern";
            List<Department> deps =
                    session.createQuery(hql, Department.class)
                            .setParameter("pattern", letter + "%")
                            .getResultList();
            tx.commit();
            return deps;
        }
    }
}
