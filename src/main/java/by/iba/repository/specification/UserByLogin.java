package by.iba.repository.specification;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserByLogin implements Parameter {
    private final String login;

    @Override
    public List<Object> getParameters() {
        return Arrays.asList(login);
    }
}
