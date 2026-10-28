
package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import model.annotation.Positive;
import model.annotation.MaxLength;

public class AnnotationValidator {

    public static String[] validate(Object obj) {
        List<String> errors = new ArrayList<>();

        if (obj == null) {
            return new String[] { "Object must not be null" };
        }

        Class<?> currentClass = obj.getClass();

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);

                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive annotation = field.getAnnotation(Positive.class);

                        if (value instanceof Number
                                && ((Number) value).doubleValue() <= 0) {
                            errors.add(field.getName() + ": "
                                    + annotation.message());
                        }
                    }

                    if (field.isAnnotationPresent(MaxLength.class)) {
                        MaxLength annotation = field.getAnnotation(MaxLength.class);

                        if (value instanceof String
                                && ((String) value).length() > annotation.value()) {
                            errors.add(field.getName()
                                    + ": maximum length is "
                                    + annotation.value());
                        }
                    }
                } catch (IllegalAccessException | RuntimeException e) {
                    errors.add(field.getName()
                            + ": unable to validate field");
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}
