package by.iba.repository.specification;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonInsert implements Parameter {
    private final String name;
    private final String phone;
    private final String email;

    @Override
    public List<Object> getParameters() {
        return Arrays.asList(name, phone, email);
    }
}
