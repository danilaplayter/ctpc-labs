package by.iba.testsupport;

import java.lang.reflect.Field;

public final class ReflectionUtils {
    private ReflectionUtils() {}

    public static void resetStaticField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
