package by.iba.command;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommandResult {
    private String page;
    private boolean isRedirect;

    public CommandResult(String page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommandResult that = (CommandResult) o;
        return isRedirect == that.isRedirect && Objects.equals(page, that.page);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, isRedirect);
    }
}
