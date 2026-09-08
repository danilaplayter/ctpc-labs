package com.hibernate.xmlbased;

import static org.assertj.core.api.Assertions.assertThat;

import com.hibernate.xmlbased.dao.DepartmentDAO;
import com.hibernate.xmlbased.dao.DeveloperDAO;
import com.hibernate.xmlbased.model.Department;
import com.hibernate.xmlbased.model.Developer;
import java.util.List;
import org.junit.jupiter.api.Test;

class DeveloperDAOTest extends BaseDaoTest {

    private final DepartmentDAO depDao = new DepartmentDAO(sessionFactory);
    private final DeveloperDAO devDao = new DeveloperDAO(sessionFactory);

    @Test
    void shouldAddAndGetDeveloper() {
        Department dep = new Department("D01", "IT", "Moscow");
        depDao.addDepartament(dep);
        Developer dev = new Developer(0, "Ivan", "Java", 3, dep);
        devDao.addDeveloper(dev);

        Developer found = devDao.getDeveloperById(dev.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Ivan");
        assertThat(found.getDepartment().getDepartmentId()).isEqualTo("D01");
    }

    @Test
    void shouldReturnNullForNonExistingDeveloper() {
        Developer found = devDao.getDeveloperById(999);
        assertThat(found).isNull();
    }

    @Test
    void shouldGetAllDevelopers() {
        Department dep = new Department("D02", "QA", "SPb");
        depDao.addDepartament(dep);
        devDao.addDeveloper(new Developer(0, "Alice", "Test", 2, dep));
        devDao.addDeveloper(new Developer(0, "Bob", "Automation", 3, dep));

        List<Developer> list = devDao.getDevelopers();
        assertThat(list).hasSize(2);
    }

    @Test
    void shouldUpdateDeveloperDepartment() {
        Department dep1 = new Department("D03", "Dev", "NY");
        Department dep2 = new Department("D04", "Ops", "LA");
        depDao.addDepartament(dep1);
        depDao.addDepartament(dep2);

        Developer dev = new Developer(0, "Tom", "DevOps", 4, dep1);
        devDao.addDeveloper(dev);

        devDao.updateDevelopersDepartment(dev.getId(), dep2);
        Developer updated = devDao.getDeveloperById(dev.getId());
        assertThat(updated.getDepartment().getDepartmentId()).isEqualTo("D04");
    }

    @Test
    void shouldRemoveDeveloper() {
        Department dep = new Department("D05", "HR", "London");
        depDao.addDepartament(dep);
        Developer dev = new Developer(0, "Eve", "Recruiter", 1, dep);
        devDao.addDeveloper(dev);

        devDao.removeDeveloper(dev.getId());
        assertThat(devDao.getDeveloperById(dev.getId())).isNull();
    }

    @Test
    void shouldFindByExperienceEqualCriteria() {
        Department dep = new Department("D06", "Backend", "Berlin");
        depDao.addDepartament(dep);
        devDao.addDeveloper(new Developer(0, "John", "Java", 5, dep));
        devDao.addDeveloper(new Developer(0, "Jane", "Scala", 5, dep));
        devDao.addDeveloper(new Developer(0, "Jim", "Go", 3, dep));

        List<Developer> result = devDao.findByExperienceEqualCriteria(5);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getExperience() == 5);
    }

    @Test
    void shouldFindBySpecialty() {
        Department dep = new Department("D07", "Frontend", "Paris");
        depDao.addDepartament(dep);
        devDao.addDeveloper(new Developer(0, "Anna", "React", 4, dep));
        devDao.addDeveloper(new Developer(0, "Ben", "Vue", 2, dep));

        List<Developer> result = devDao.findBySpecialty("React");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Anna");
    }
}
