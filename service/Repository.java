
package service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Repository<T> {

    private final HashMap<String, T> storage = new HashMap<>();

    public T save(T entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        storage.put(extractId(entity), entity);
        return entity;
    }

    @SafeVarargs
    public final void saveAll(T... items) {
        Objects.requireNonNull(items, "Items cannot be null");

        for (T item : items) {
            save(item);
        }
    }

    public T findById(String id) {
        return storage.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public boolean delete(String id) {
        return storage.remove(id) != null;
    }

    public int count() {
        return storage.size();
    }

    private String extractId(T entity) {
        Class<?> type = entity.getClass();

        // First, search for the @Id annotation.
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {

            for (Field field : current.getDeclaredFields()) {
                if (hasIdAnnotation(field)) {
                    String value = readStringField(field, entity);
                    if (value != null) {
                        return value;
                    }
                }
            }
        }

        // Fallback: find a String field named id or ending in Id.
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {

            for (Field field : current.getDeclaredFields()) {
                String name = field.getName().toLowerCase();

                if (field.getType() == String.class
                        && (name.equals("id") || name.endsWith("id"))) {
                    String value = readStringField(field, entity);

                    if (value != null) {
                        return value;
                    }
                }
            }
        }

        throw new IllegalArgumentException(
                "No usable @Id or ID field found in " + type.getName());
    }

    private boolean hasIdAnnotation(Field field) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
            if (annotation.annotationType().getName()
                    .equals("model.annotation.Id")) {
                return true;
            }
        }
        return false;
    }

    private String readStringField(Field field, T entity) {
        try {
            field.setAccessible(true);
            Object value = field.get(entity);

            if (value instanceof String && !((String) value).trim().isEmpty()) {
                return (String) value;
            }

            throw new IllegalArgumentException(
                    "ID field must contain a non-empty String: "
                            + field.getName());

        } catch (IllegalAccessException e) {
            throw new IllegalStateException(
                    "Cannot access ID field: " + field.getName(), e);
        }
    }

    public static <N extends Number> double average(N[] values) {
        Objects.requireNonNull(values, "Values cannot be null");

        if (values.length == 0) {
            throw new IllegalArgumentException(
                    "Cannot calculate average of an empty array");
        }

        double sum = 0;

        for (N value : values) {
            sum += Objects.requireNonNull(
                    value, "Values cannot contain null").doubleValue();
        }

        return sum / values.length;
    }
}
