package repository;

import java.util.*;

public class Repository<T> {
    protected Map<String, T> storage;

    public Repository() {
        this.storage = new HashMap<>();
    }

    public void add(String id, T item) {
        storage.put(id, item);
    }

    public void remove(String id) {
        storage.remove(id);
    }

    public Optional<T> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public boolean exists(String id) {
        return storage.containsKey(id);
    }
}