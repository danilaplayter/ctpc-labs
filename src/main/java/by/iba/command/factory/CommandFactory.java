package by.iba.command.factory;

import by.iba.command.Command;
import by.iba.command.LoginPageCommand;
import by.iba.command.RegisterPageCommand;
import by.iba.command.authorithation.LoginCommand;
import by.iba.command.authorithation.RegisterNewUserCommand;
import by.iba.command.authorithation.SignOutCommand;
import by.iba.command.grouppersons.AddNewPersonCommand;
import by.iba.command.grouppersons.AddNewTeacherCommand;
import by.iba.command.grouppersons.WelcomeCommand;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class CommandFactory {

    private static final Map<CommandType, Supplier<Command>> COMMANDS =
            new EnumMap<>(CommandType.class);

    static {
        COMMANDS.put(CommandType.LOGIN, LoginCommand::new);
        COMMANDS.put(CommandType.REGISTER_NEW_USER, RegisterNewUserCommand::new);
        COMMANDS.put(CommandType.SIGN_OUT, SignOutCommand::new);
        COMMANDS.put(CommandType.ADD_NEW_PERSON, AddNewPersonCommand::new);
        COMMANDS.put(CommandType.ADD_NEW_TEACHER, AddNewTeacherCommand::new);
        COMMANDS.put(CommandType.LOGIN_PAGE, LoginPageCommand::new);
        COMMANDS.put(CommandType.WELCOME, WelcomeCommand::new);
        COMMANDS.put(CommandType.REGISTRATION_PAGE, RegisterPageCommand::new);
    }

    public static Command create(String command) {
        CommandType commandType = CommandType.valueOf(command.toUpperCase());
        return COMMANDS.get(commandType).get();
    }
}
