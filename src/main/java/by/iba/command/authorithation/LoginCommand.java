package by.iba.command.authorithation;

import static by.iba.command.authorithation.constants.AuthConstants.AUTHENTICATION_ERROR_TEXT;
import static by.iba.command.authorithation.constants.AuthConstants.COMMAND_WELCOME;
import static by.iba.command.authorithation.constants.AuthConstants.ERROR_MESSAGE;
import static by.iba.command.authorithation.constants.AuthConstants.ERROR_MESSAGE_TEXT;
import static by.iba.command.authorithation.constants.AuthConstants.LOGIN;
import static by.iba.command.authorithation.constants.AuthConstants.PASSWORD;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import by.iba.command.Command;
import by.iba.command.CommandResult;
import by.iba.command.session.SessionAttribute;
import by.iba.exception.ServiceException;
import by.iba.model.User;
import by.iba.service.UserService;
import by.iba.util.HashPassword;
import by.iba.util.pages.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCommand implements Command {

    private void setAttributesToSession(String name, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(SessionAttribute.NAME, name);
    }

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response)
            throws ServiceException {
        Optional<String> login = of(request).map(r -> r.getParameter(LOGIN));
        Optional<String> password = of(request).map(r -> r.getParameter(PASSWORD));
        if (isEmpty(login.get()) || isEmpty(password.get())) {
            return forwardLoginWithError(request, ERROR_MESSAGE, ERROR_MESSAGE_TEXT);
        }
        byte[] pass = HashPassword.getHash(password.get());
        boolean isUserFound = initializeUserIfExist(login.get(), pass, request);
        if (!isUserFound) {
            log.info("user with such login and password doesn't exist");
            return forwardLoginWithError(request, ERROR_MESSAGE, AUTHENTICATION_ERROR_TEXT);
        }
        log.info("user has been authorized: login={}", login);
        return new CommandResult(COMMAND_WELCOME, false);
    }

    private boolean initializeUserIfExist(String login, byte[] password, HttpServletRequest request)
            throws ServiceException {
        UserService userService = new UserService();
        Optional<User> user = userService.login(login, password);
        user.ifPresent(u -> setAttributesToSession(u.getLogin(), request));
        return user.isPresent();
    }

    private CommandResult forwardLoginWithError(
            HttpServletRequest request, String key, String message) {
        request.setAttribute(key, message);
        return new CommandResult(Page.LOGIN_PAGE.getPage(), false);
    }
}
