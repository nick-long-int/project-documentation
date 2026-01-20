package ru.gnidenko.service;

import ru.gnidenko.model.User;

public class UserChecker {
    public static void checkUserIsNotNull(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }
    }

    public static void checkUserFields(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("username must not be empty");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("password must not be empty");
        }
    }
}
