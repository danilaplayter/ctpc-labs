package by.iba.command.grouppersons;

import static by.iba.command.grouppersons.constant.GroupConstant.ERROR_MESSAGE;
import static by.iba.command.grouppersons.constant.GroupConstant.ERROR_MESSAGE_TEXT;
import static by.iba.command.grouppersons.constant.GroupConstant.LISTGROUP;
import static by.iba.command.grouppersons.constant.GroupConstant.NEWEMAIL;
import static by.iba.command.grouppersons.constant.GroupConstant.NEWNAME;
import static by.iba.command.grouppersons.constant.GroupConstant.NEWPHONE;
import static by.iba.command.grouppersons.constant.GroupConstant.TEACHERS_LIST;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import by.iba.command.Command;
import by.iba.command.CommandResult;
import by.iba.exception.ServiceException;
import by.iba.model.Person;
import by.iba.service.PersonService;
import by.iba.service.TeacherService;
import by.iba.util.pages.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddNewPersonCommand implements Command {
    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response)
            throws ServiceException {
        PersonService personService = new PersonService();
        Optional<String> newName = of(request).map(r -> r.getParameter(NEWNAME));
        Optional<String> newPhone = of(request).map(r -> r.getParameter(NEWPHONE));
        Optional<String> newEmail = of(request).map(r -> r.getParameter(NEWEMAIL));
        if (isEmpty(newName.get()) || isEmpty(newPhone.get()) || isEmpty(newEmail.get())) {
            log.info("missing parameter for new person");
            request.setAttribute(ERROR_MESSAGE, ERROR_MESSAGE_TEXT);
        } else {
            personService.save(new Person(newName.get(), newPhone.get(), newEmail.get()));
        }
        List<Person> persons = personService.findAll();
        if (!persons.isEmpty()) {
            request.setAttribute(LISTGROUP, persons);
        }
        List<?> teachers = new TeacherService().findAll();
        if (!teachers.isEmpty()) {
            request.setAttribute(TEACHERS_LIST, teachers);
        }
        return new CommandResult(Page.WELCOME_PAGE.getPage(), false);
    }
}
