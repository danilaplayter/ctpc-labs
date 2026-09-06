package org.jdbc_lab.prepared_statement.model;

import java.sql.Date;
import lombok.Data;

@Data
public class Enrollment {
    private Long id;
    private Long studentId;
    private Long courseId;
    private Integer grade; // 0-100
    private Date enrollmentDate;
}
