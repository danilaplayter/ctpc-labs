package com.hibernate.xmlbased;

import static org.assertj.core.api.Assertions.assertThat;

import com.hibernate.xmlbased.dao.DepartmentDAO;
import com.hibernate.xmlbased.dao.DeveloperDAO;
import com.hibernate.xmlbased.model.Department;
import com.hibernate.xmlbased.model.Developer;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DepartmentDAOTest extends BaseDaoTest {

    private final DepartmentDAO dao = new DepartmentDAO(sessionFactory);

    @Test
    void shouldAddAndFindDepartment() {
        Department dep = new Department("C01", "HR", "Moscow");
        dao.addDepartament(dep);

        Department found = dao.findDepartmentByID("C01");
        assertThat(found).isNotNull();
        assertThat(found.getDepartmentName()).isEqualTo("HR");
    }

    @Test
    void shouldReturnNullForNotFoundDepartment() {
        Department found = dao.findDepartmentByID("XXX");
        assertThat(found).isNull();
    }

    @Test
    void shouldGetAllDepartments() {
        dao.addDepartament(new Department("D01", "DevOps", "SPb"));
        dao.addDepartament(new Department("D02", "QA", "Msk"));

        List<Department> list = dao.getDepartments();
        assertThat(list).hasSize(2);
    }

    @Test
    void shouldDeleteDepartment() {
        Department dep = new Department("E01", "Sales", "NY");
        dao.addDepartament(dep);
        assertThat(dao.findDepartmentByID("E01")).isNotNull();

        dao.deleteDepartment("E01");
        assertThat(dao.findDepartmentByID("E01")).isNull();
    }

    @Test
    void shouldDeleteDepartmentAndCascadeDevelopers() {
        Department dep = new Department("F01", "R&D", "London");
        dao.addDepartament(dep);
        Developer dev = new Developer(0, "John", "Engineer", 5, dep);
        var devDao = new DeveloperDAO(sessionFactory);
        devDao.addDeveloper(dev);

        dao.deleteDepartment("F01");
        assertThat(dao.findDepartmentByID("F01")).isNull();
        assertThat(devDao.getDeveloperById(dev.getId())).isNull();
    }

    @Test
    void shouldGetDepartmentWithWorkers() {
        Department dep = new Department("G01", "Backend", "Berlin");
        dao.addDepartament(dep);
        Developer dev1 = new Developer(0, "Anna", "Java", 3, dep);
        Developer dev2 = new Developer(0, "Bob", "Kotlin", 2, dep);
        var devDao = new DeveloperDAO(sessionFactory);
        devDao.addDeveloper(dev1);
        devDao.addDeveloper(dev2);

        Set<Department> result = dao.getDepartmentWithWorkers();
        assertThat(result).hasSize(1);
        Department d = result.iterator().next();
        assertThat(d.getDevelopers()).hasSize(2);
    }

    @Test
    void shouldGetDevelopersByDepartment() {
        Department dep = new Department("H01", "Frontend", "Paris");
        dao.addDepartament(dep);
        Developer dev = new Developer(0, "Charlie", "React", 4, dep);
        new DeveloperDAO(sessionFactory).addDeveloper(dev);

        List<Developer> devs = dao.getDevelopersByDepartment(dep);
        assertThat(devs).hasSize(1);
        assertThat(devs.get(0).getName()).isEqualTo("Charlie");
    }

    @Test
    void shouldFindDepartmentByLetterInID() {
        dao.addDepartament(new Department("I01", "Dev", "City"));
        dao.addDepartament(new Department("I02", "Ops", "Town"));
        dao.addDepartament(new Department("J01", "Other", "Village"));

        List<Department> result = dao.findDepartmentByLetterInID('I');
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getDepartmentId().startsWith("I"));
    }
}
