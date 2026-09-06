package org.jdbc_lab.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.jdbc_lab.utils.InputManager;

public class TestUtils {
    public static void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        InputManager.resetScanner(); // теперь сбрасываем scanner после подмены System.in
    }

    public static String captureOutput(Runnable action) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        action.run();
        System.setOut(original);
        return out.toString();
    }
}
