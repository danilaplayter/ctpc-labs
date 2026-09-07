package by.iba.filter;

import by.iba.command.session.SessionAttribute;
import by.iba.util.pages.Page;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebFilter(urlPatterns = "/controller")
public class LoginRequiredFilter implements Filter {
    private static final String COMMAND = "command";
    private static final String WELCOME = "welcome";
    private static final String ERROR_MESSAGE = "error_message";
    private static final String ERROR_TEXT = "Нет авторизации для выполнения данной команды";

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String command = request.getParameter(COMMAND);
        log.info("Filter is working {}={}", COMMAND, command);
        if (!WELCOME.equals(command)
                || request.getSession().getAttribute(SessionAttribute.NAME) != null) {
            chain.doFilter(req, resp);
        } else {
            request.setAttribute(ERROR_MESSAGE, ERROR_TEXT);
            request.getRequestDispatcher(Page.ERROR_PAGE.getPage()).forward(req, resp);
        }
    }

    @Override
    public void destroy() {}
}
