package by.iba.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Teacher implements Serializable {
    private int id;
    private String name;
    private String subject;
    private String phone;

    public Teacher(String name, String subject, String phone) {
        this.name = name;
        this.subject = subject;
        this.phone = phone;
    }
}
