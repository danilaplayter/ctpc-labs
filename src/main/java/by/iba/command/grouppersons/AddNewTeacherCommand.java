package by.iba.command.grouppersons;

import static by.iba.command.grouppersons.constant.GroupConstant.ERROR_MESSAGE;
import static by.iba.command.grouppersons.constant.GroupConstant.ERROR_MESSAGE_TEXT;
import static by.iba.command.grouppersons.constant.GroupConstant.LISTGROUP;
import static by.iba.command.grouppersons.constant.GroupConstant.NEW_TEACHER_NAME;
import static by.iba.command.grouppersons.constant.GroupConstant.NEW_TEACHER_PHONE;
import static by.iba.command.grouppersons.constant.GroupConstant.NEW_TEACHER_SUBJECT;
import static by.iba.command.grouppersons.constant.GroupConstant.TEACHERS_LIST;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import by.iba.command.Command;
import by.iba.command.CommandResult;
import by.iba.exception.ServiceException;
import by.iba.model.Teacher;
import by.iba.service.PersonService;
import by.iba.service.TeacherService;
import by.iba.util.pages.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddNewTeacherCommand implements Command {
    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response)
            throws ServiceException {
        TeacherService teacherService = new TeacherService();
        Optional<String> name = of(request).map(r -> r.getParameter(NEW_TEACHER_NAME));
        Optional<String> subject = of(request).map(r -> r.getParameter(NEW_TEACHER_SUBJECT));
        Optional<String> phone = of(request).map(r -> r.getParameter(NEW_TEACHER_PHONE));
        if (isEmpty(name.get()) || isEmpty(subject.get()) || isEmpty(phone.get())) {
            log.info("missing parameter for new teacher");
            request.setAttribute(ERROR_MESSAGE, ERROR_MESSAGE_TEXT);
        } else {
            teacherService.save(new Teacher(name.get(), subject.get(), phone.get()));
        }
        List<Teacher> teachers = teacherService.findAll();
        if (!teachers.isEmpty()) {
            request.setAttribute(TEACHERS_LIST, teachers);
        }
        List<?> persons = new PersonService().findAll();
        if (!persons.isEmpty()) {
            request.setAttribute(LISTGROUP, persons);
        }
        return new CommandResult(Page.WELCOME_PAGE.getPage(), false);
    }
}
