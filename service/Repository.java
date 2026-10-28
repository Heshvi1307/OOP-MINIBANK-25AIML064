
package service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Repository<T> {

    private final Map<String, T> storage = new LinkedHashMap<>();

    public synchronized T save(T entity) {
        Objects.requireNonNull(entity, "Entity cannot be null.");

        String id = extractId(entity);
        T existing = storage.get(id);

        if (existing != null && existing != entity) {
            throw new IllegalArgumentException(
                    "An entity with ID " + id + " already exists.");
        }

        storage.put(id, entity);
        return entity;
    }

    @SafeVarargs
    public final synchronized void saveAll(T... items) {
        Objects.requireNonNull(items, "Items cannot be null.");

        for (T item : items) {
            save(item);
        }
    }

    public synchronized T findById(String id) {
        if (id == null) {
            return null;
        }

        return storage.get(id.trim());
    }

    public synchronized ArrayList<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public synchronized boolean delete(String id) {
        return id != null && storage.remove(id.trim()) != null;
    }

    public synchronized int count() {
        return storage.size();
    }

    public synchronized boolean containsId(String id) {
        return id != null && storage.containsKey(id.trim());
    }

    private String extractId(T entity) {
        Class<?> type = entity.getClass();

        for (Class<?> current = type; current != null; current = current.getSuperclass()) {

            for (Field field : current.getDeclaredFields()) {
                if (hasIdAnnotation(field)) {
                    return readStringField(field, entity);
                }
            }
        }

        for (Class<?> current = type; current != null; current = current.getSuperclass()) {

            for (Field field : current.getDeclaredFields()) {
                String name = field.getName().toLowerCase();

                if (field.getType() == String.class
                        && (name.equals("id") || name.endsWith("id"))) {
                    return readStringField(field, entity);
                }
            }
        }

        throw new IllegalArgumentException(
                "No usable @Id or ID field found in "
                        + type.getName());
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

            if (value instanceof String id && !id.isBlank()) {
                return id.trim();
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
        Objects.requireNonNull(values, "Values cannot be null.");

        if (values.length == 0) {
            throw new IllegalArgumentException(
                    "Cannot calculate the average of an empty array.");
        }

        double sum = 0;

        for (N value : values) {
            sum += Objects.requireNonNull(
                    value, "Values cannot contain null.")
                    .doubleValue();
        }

        return sum / values.length;
    }
}
