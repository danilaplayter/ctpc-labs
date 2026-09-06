package com.hibernate.xmlbased;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hibernate.xmlbased.dao.DepartmentDAO;
import com.hibernate.xmlbased.dao.DeveloperDAO;
import com.hibernate.xmlbased.dao.UserDAO;
import com.hibernate.xmlbased.model.Department;
import com.hibernate.xmlbased.model.Developer;
import com.hibernate.xmlbased.model.Role;
import com.hibernate.xmlbased.model.User;
import jakarta.persistence.NoResultException;
import java.util.List;
import org.junit.jupiter.api.Test;

class UserDAOTest extends BaseDaoTest {

    private final DepartmentDAO depDao = new DepartmentDAO(sessionFactory);
    private final DeveloperDAO devDao = new DeveloperDAO(sessionFactory);
    private final UserDAO userDao = new UserDAO(sessionFactory);

    @Test
    void shouldAuthenticateUser() {
        Department dep = new Department("U01", "IT", "City");
        depDao.addDepartament(dep);
        Developer dev = new Developer(0, "Alice", "Dev", 2, dep);
        devDao.addDeveloper(dev);

        User user = new User("alice", "pass");
        user.setDeveloper(dev);
        userDao.addUser(user);

        User auth = userDao.AuthUser("alice", "pass");
        assertThat(auth).isNotNull();
        assertThat(auth.getUsername()).isEqualTo("alice");
        assertThat(auth.getDeveloper().getName()).isEqualTo("Alice");
    }

    @Test
    void shouldThrowOnInvalidCredentials() {
        assertThatThrownBy(() -> userDao.AuthUser("unknown", "wrong"))
                .isInstanceOf(NoResultException.class);
    }

    @Test
    void shouldUpdatePassword() {
        Department dep = new Department("U02", "QA", "SPb");
        depDao.addDepartament(dep);
        Developer dev = new Developer(0, "Bob", "Test", 1, dep);
        devDao.addDeveloper(dev);

        User user = new User("bob", "old");
        user.setDeveloper(dev);
        userDao.addUser(user);

        userDao.updatePassword(dev.getId(), "new");
        User updated = userDao.getUserById(dev.getId());
        assertThat(updated.getPassword()).isEqualTo("new");
    }

    @Test
    void shouldGetUserById() {
        Department dep = new Department("U03", "DevOps", "Msk");
        depDao.addDepartament(dep);
        Developer dev = new Developer(0, "Charlie", "DevOps", 3, dep);
        devDao.addDeveloper(dev);

        User user = new User("charlie", "pass");
        user.setDeveloper(dev);
        userDao.addUser(user);

        User found = userDao.getUserById(dev.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("charlie");
    }

    @Test
    void shouldGetAllUsers() {
        Department dep = new Department("U04", "HR", "London");
        depDao.addDepartament(dep);
        Developer dev1 = new Developer(0, "David", "HR", 1, dep);
        Developer dev2 = new Developer(0, "Eva", "Recruiter", 2, dep);
        devDao.addDeveloper(dev1);
        devDao.addDeveloper(dev2);

        User user1 = new User("david", "pass");
        user1.setDeveloper(dev1);
        userDao.addUser(user1);

        User user2 = new User("eva", "pass");
        user2.setDeveloper(dev2);
        userDao.addUser(user2);

        List<User> users = userDao.getUsers();
        assertThat(users).hasSize(2);
    }

    @Test
    void shouldGetUserByUsername() {
        Department dep = new Department("U05", "R&D", "Berlin");
        depDao.addDepartament(dep);
        Developer dev = new Developer(0, "Frank", "Research", 5, dep);
        devDao.addDeveloper(dev);

        User user = new User("frank", "pass");
        user.setDeveloper(dev);
        userDao.addUser(user);

        User found = userDao.getUserByUsername("frank");
        assertThat(found).isNotNull();
        assertThat(found.getUserId()).isEqualTo(dev.getId());
    }

    @Test
    void shouldThrowWhenUsernameNotFound() {
        assertThatThrownBy(() -> userDao.getUserByUsername("unknown"))
                .isInstanceOf(NoResultException.class);
    }

    @Test
    void shouldAddUser() {
        Department dep = new Department("U06", "Security", "NY");
        depDao.addDepartament(dep);
        Developer dev = new Developer(0, "Grace", "Security", 4, dep);
        devDao.addDeveloper(dev);

        User user = new User("grace", "secret");
        user.setDeveloper(dev);
        userDao.addUser(user);

        User found = userDao.getUserById(dev.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("grace");
        assertThat(found.getUserRole()).isEqualTo(Role.ROLE_USER);
    }
}
