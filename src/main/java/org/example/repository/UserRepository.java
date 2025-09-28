package org.example.repository;


import org.example.model.User;

import java.util.*;

public class UserRepository {
    private static final List<User> users = new ArrayList<>();
    private static int currentId = 1;

    public List<User> findAll() {
        return users;
    }

    public User findById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    public void save(User user) {
        user.setId(currentId++);
        users.add(user);
    }

    public void update(User user) {
        var existing = findById(user.getId());
        if (existing != null) {
            existing.setName(user.getName());
            existing.setEmail(user.getEmail());
        }
    }

    public void delete(int id) {
        users.removeIf(u -> u.getId() == id);
    }
    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}

