package by.iba.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ExceptionsTest {

    @Test
    void incorrectDataExceptionConstructors() {
        assertThat(new IncorrectDataException("msg").getMessage()).isEqualTo("msg");
        Throwable cause = new RuntimeException("cause");
        assertThat(new IncorrectDataException("msg", cause).getCause()).isEqualTo(cause);
        assertThat(new IncorrectDataException(cause).getCause()).isEqualTo(cause);
    }

    @Test
    void repositoryExceptionConstructors() {
        assertThat(new RepositoryException("msg").getMessage()).isEqualTo("msg");
        Throwable cause = new RuntimeException("cause");
        assertThat(new RepositoryException("msg", cause).getCause()).isEqualTo(cause);
        assertThat(new RepositoryException(cause).getCause()).isEqualTo(cause);
    }

    @Test
    void serviceExceptionConstructors() {
        assertThat(new ServiceException("msg").getMessage()).isEqualTo("msg");
        Throwable cause = new RuntimeException("cause");
        assertThat(new ServiceException("msg", cause).getCause()).isEqualTo(cause);
        assertThat(new ServiceException(cause).getCause()).isEqualTo(cause);
    }
}
