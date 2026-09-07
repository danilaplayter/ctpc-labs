package by.iba.command.grouppersons;

import static by.iba.command.grouppersons.constant.GroupConstant.LISTGROUP;
import static by.iba.command.grouppersons.constant.GroupConstant.TEACHERS_LIST;

import by.iba.command.Command;
import by.iba.command.CommandResult;
import by.iba.exception.ServiceException;
import by.iba.model.Person;
import by.iba.model.Teacher;
import by.iba.service.PersonService;
import by.iba.service.TeacherService;
import by.iba.util.pages.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public class WelcomeCommand implements Command {
    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response)
            throws ServiceException {
        PersonService personService = new PersonService();
        TeacherService teacherService = new TeacherService();

        List<Person> persons = personService.findAll();
        if (!persons.isEmpty()) {
            request.setAttribute(LISTGROUP, persons);
        }

        List<Teacher> teachers = teacherService.findAll();
        if (!teachers.isEmpty()) {
            request.setAttribute(TEACHERS_LIST, teachers);
        }
        return new CommandResult(Page.WELCOME_PAGE.getPage(), false);
    }
}
