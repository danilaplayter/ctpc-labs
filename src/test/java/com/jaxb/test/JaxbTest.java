package com.jaxb.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.jaxb.model.Department;
import com.jaxb.model.Employee;
import com.jaxb.model.Organization;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JaxbTest {

    @TempDir Path tempDir;

    @Test
    void shouldMarshalAndUnmarshal() throws Exception {
        File xml = tempDir.resolve("org.xml").toFile();

        Employee e1 = new Employee("E01", "Tom", null);
        Employee e2 = new Employee("E02", "Mary", "E01");
        Department dept = new Department("D01", "ACCOUNTING", "NY");
        dept.setEmployees(List.of(e1, e2));
        Organization org = new Organization("MyCo", List.of(dept));

        JAXBContext ctx = JAXBContext.newInstance(Organization.class);

        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(org, xml);

        Unmarshaller um = ctx.createUnmarshaller();
        Organization read = (Organization) um.unmarshal(xml);

        assertThat(read.getName()).isEqualTo("MyCo");
        assertThat(read.getDepartments()).hasSize(1);
        assertThat(read.getDepartments().get(0).getEmployees()).hasSize(2);
        assertThat(read.getDepartments().get(0).getEmployees().get(0).getEmployeeName())
                .isEqualTo("Tom");
    }
}
