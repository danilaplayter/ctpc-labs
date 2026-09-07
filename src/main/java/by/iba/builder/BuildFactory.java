package by.iba.builder;

public class BuildFactory {

    private static final String USER = "users";
    private static final String PERSON = "persons";
    private static final String TEACHER = "teachers";
    private static final String MESSAGE = "Unknown Builder name!";

    public static Builder<?> create(String builderName) {
        switch (builderName) {
            case USER:
                return new UserBuilder();
            case PERSON:
                return new PersonBuilder();
            case TEACHER:
                return new TeacherBuilder();
            default:
                throw new IllegalArgumentException(MESSAGE);
        }
    }
}
