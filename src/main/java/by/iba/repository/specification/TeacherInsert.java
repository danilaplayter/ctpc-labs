package by.iba.repository.specification;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TeacherInsert implements Parameter {
    private final String name;
    private final String subject;
    private final String phone;

    @Override
    public List<Object> getParameters() {
        return Arrays.asList(name, subject, phone);
    }
}
