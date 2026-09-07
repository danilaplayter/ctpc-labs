package by.iba.service;

import by.iba.exception.RepositoryException;
import by.iba.exception.ServiceException;
import by.iba.model.Person;
import by.iba.repository.PersonRepository;
import by.iba.repository.RepositoryCreator;
import java.util.List;

public class PersonService {
    public List<Person> findAll() throws ServiceException {
        try (RepositoryCreator repositoryCreator = new RepositoryCreator()) {
            PersonRepository repository = repositoryCreator.getPersonRepository();
            return repository.findAll();
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void save(Person person) throws ServiceException {
        try (RepositoryCreator repositoryCreator = new RepositoryCreator()) {
            PersonRepository repository = repositoryCreator.getPersonRepository();
            repository.save(person);
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
