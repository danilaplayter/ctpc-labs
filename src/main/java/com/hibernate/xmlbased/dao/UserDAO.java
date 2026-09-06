package com.hibernate.xmlbased.dao;

import com.hibernate.xmlbased.config.SessionConfig;
import com.hibernate.xmlbased.model.Developer;
import com.hibernate.xmlbased.model.User;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class UserDAO {

    private final SessionFactory sessionFactory;

    public UserDAO() {
        this.sessionFactory = SessionConfig.getInstance().getSessionFactory();
    }

    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User AuthUser(String username, String password) throws NoResultException {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cr = cb.createQuery(User.class);
            Root<User> root = cr.from(User.class);
            cr.where(
                    cb.and(
                            cb.equal(root.get("username"), username),
                            cb.equal(root.get("password"), password)));
            User user = session.createQuery(cr).getSingleResult();
            Hibernate.initialize(user.getDeveloper());
            tx.commit();
            return user;
        }
    }

    public void updatePassword(Integer id, String password) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("UPDATE User u SET u.password = :pass WHERE u.userId = :id")
                    .setParameter("pass", password)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    public User getUserById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, id);
            tx.commit();
            return user;
        }
    }

    public List<User> getUsers() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            List<User> users = session.createQuery("FROM User", User.class).getResultList();
            tx.commit();
            return users;
        }
    }

    public User getUserByUsername(String username) throws NoResultException {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cr = cb.createQuery(User.class);
            Root<User> root = cr.from(User.class);
            cr.where(cb.equal(root.get("username"), username));
            User user = session.createQuery(cr).getSingleResult();
            tx.commit();
            return user;
        }
    }

    public User addUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Developer developer = user.getDeveloper();
            if (developer == null) {
                throw new RuntimeException("Developer is not set for user");
            }
            Developer managedDeveloper = session.get(Developer.class, developer.getId());
            if (managedDeveloper == null) {
                throw new RuntimeException("Developer with id " + developer.getId() + " not found");
            }
            user.setDeveloper(managedDeveloper);
            user.setUserId(0);
            session.persist(user);
            tx.commit();
            return user;
        }
    }
}
