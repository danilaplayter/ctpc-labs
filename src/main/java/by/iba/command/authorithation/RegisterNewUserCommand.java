package by.iba.command.authorithation;

import static by.iba.command.authorithation.constants.AuthConstants.ERROR_MESSAGE_TEXT;
import static by.iba.command.authorithation.constants.AuthConstants.NAME_FOR_REGISTER;
import static by.iba.command.authorithation.constants.AuthConstants.PASSWORD_FOR_REGISTER;
import static by.iba.command.authorithation.constants.AuthConstants.REGISTER_ERROR;
import static by.iba.command.authorithation.constants.AuthConstants.REGISTER_ERROR_MESSAGE_IF_EXIST;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import by.iba.command.Command;
import by.iba.command.CommandResult;
import by.iba.exception.ServiceException;
import by.iba.model.User;
import by.iba.service.UserService;
import by.iba.util.HashPassword;
import by.iba.util.pages.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegisterNewUserCommand implements Command {

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response)
            throws ServiceException {
        Optional<String> login = of(request).map(r -> r.getParameter(NAME_FOR_REGISTER));
        Optional<String> password = of(request).map(r -> r.getParameter(PASSWORD_FOR_REGISTER));
        if (isEmpty(login.get()) || isEmpty(password.get())) {
            return forwardToRegisterWithError(request, REGISTER_ERROR, ERROR_MESSAGE_TEXT);
        }
        byte[] pass = HashPassword.getHash(password.get());
        User user = new User(login.get(), pass);
        UserService userService = new UserService();
        int userCount = userService.save(user);
        if (userCount != 0) {
            log.info("user registered: login={}", login);
            return new CommandResult(Page.LOGIN_PAGE.getPage(), false);
        }
        log.info("registration failed, login already exists: {}", login);
        return forwardToRegisterWithError(request, REGISTER_ERROR, REGISTER_ERROR_MESSAGE_IF_EXIST);
    }

    private CommandResult forwardToRegisterWithError(
            HttpServletRequest request, String key, String message) {
        request.setAttribute(key, message);
        return new CommandResult(Page.REGISTER_PAGE.getPage(), false);
    }
}
