package by.iba.repository.specification;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserByLoginPassword implements Parameter {
    private final String login;
    private final byte[] password;

    @Override
    public List<Object> getParameters() {
        return Arrays.asList(login, password);
    }
}
