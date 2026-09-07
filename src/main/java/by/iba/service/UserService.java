package by.iba.service;

import by.iba.exception.RepositoryException;
import by.iba.exception.ServiceException;
import by.iba.model.User;
import by.iba.repository.RepositoryCreator;
import by.iba.repository.SQLHelper;
import by.iba.repository.UserRepository;
import by.iba.repository.specification.UserByLogin;
import by.iba.repository.specification.UserByLoginPassword;
import java.util.Optional;

public class UserService {
    public Optional<User> login(String login, byte[] password) throws ServiceException {
        try (RepositoryCreator repositoryCreator = new RepositoryCreator()) {
            UserRepository repository = repositoryCreator.getUserRepository();
            UserByLoginPassword params = new UserByLoginPassword(login, password);
            return repository.queryForSingleResult(SQLHelper.SQL_GET_USER, params);
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public Integer save(User user) throws ServiceException {
        try (RepositoryCreator repositoryCreator = new RepositoryCreator()) {
            UserRepository repository = repositoryCreator.getUserRepository();
            UserByLogin param = new UserByLogin(user.getLogin());
            if (repository.queryForSingleResult(SQLHelper.SQL_CHECK_LOGIN, param).isPresent()) {
                return 0;
            }
            return repository.save(user);
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
