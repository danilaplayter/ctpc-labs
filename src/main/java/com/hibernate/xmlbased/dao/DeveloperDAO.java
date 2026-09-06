package com.hibernate.xmlbased.dao;

import com.hibernate.xmlbased.config.SessionConfig;
import com.hibernate.xmlbased.model.Department;
import com.hibernate.xmlbased.model.Developer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class DeveloperDAO {

    private final SessionFactory sessionFactory;

    public DeveloperDAO() {
        this.sessionFactory = SessionConfig.getInstance().getSessionFactory();
    }

    public DeveloperDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addDeveloper(Developer developer) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(developer);
            tx.commit();
        }
    }

    public Developer getDeveloperById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Developer developer = session.get(Developer.class, id);
            tx.commit();
            return developer;
        }
    }

    public List<Developer> getDevelopers() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            List<Developer> developers =
                    session.createQuery("FROM Developer", Developer.class).list();
            tx.commit();
            return developers;
        }
    }

    public Developer updateDevelopersDepartment(Integer devId, Department department) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Developer developer = session.get(Developer.class, devId);
            if (developer != null) {
                developer.setDepartment(department);
                session.merge(developer);
            }
            tx.commit();
            return developer;
        }
    }

    public void removeDeveloper(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Developer developer = session.get(Developer.class, id);
            if (developer != null) {
                session.remove(developer);
            }
            tx.commit();
        }
    }

    public List<Developer> findByExperienceEqualCriteria(Integer experience) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Developer> cr = cb.createQuery(Developer.class);
            Root<Developer> root = cr.from(Developer.class);
            cr.where(cb.equal(root.get("experience"), experience));
            List<Developer> devs = session.createQuery(cr).getResultList();
            tx.commit();
            return devs;
        }
    }

    public List<Developer> findBySpecialty(String specialty) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            String hql = "FROM Developer WHERE specialty = :spec";
            List<Developer> devs =
                    session.createQuery(hql, Developer.class)
                            .setParameter("spec", specialty)
                            .getResultList();
            tx.commit();
            return devs;
        }
    }
}
