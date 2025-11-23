package org.example.repository;

import org.example.model.User;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class UserRepository {
    private final List<User> users = new ArrayList<>();

    private final AtomicLong currentId = new AtomicLong(1);

    public List<User> findAll() {
        return Collections.unmodifiableList(users);
    }
    public User findById(long id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public void save(User user) {
        if (user.getId() != 0) {
            throw new IllegalArgumentException("User já possui ID.");
        }

        User newUserWithId = new User(currentId.getAndIncrement(), user.getName(), user.getEmail());
        users.add(newUserWithId);
    }

    public void update(User user) {
        OptionalInt index = IntStream.range(0, users.size())
                .filter(i -> users.get(i).getId() == user.getId())
                .findFirst();

        if (index.isPresent()) {

            users.set(index.getAsInt(), user);
        }
    }
    public void delete(long id) {
        users.removeIf(u -> u.getId() == id);
    }
}