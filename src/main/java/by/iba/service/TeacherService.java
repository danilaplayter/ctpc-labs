package by.iba.service;

import by.iba.exception.RepositoryException;
import by.iba.exception.ServiceException;
import by.iba.model.Teacher;
import by.iba.repository.RepositoryCreator;
import by.iba.repository.TeacherRepository;
import java.util.List;

public class TeacherService {
    public List<Teacher> findAll() throws ServiceException {
        try (RepositoryCreator repositoryCreator = new RepositoryCreator()) {
            TeacherRepository repository = repositoryCreator.getTeacherRepository();
            return repository.findAll();
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void save(Teacher teacher) throws ServiceException {
        try (RepositoryCreator repositoryCreator = new RepositoryCreator()) {
            TeacherRepository repository = repositoryCreator.getTeacherRepository();
            repository.save(teacher);
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
