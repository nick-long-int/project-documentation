package ru.gnidenko.service;

import lombok.RequiredArgsConstructor;
import ru.gnidenko.model.User;
import ru.gnidenko.model.UserRole;
import ru.gnidenko.repo.UserRepo;
import ru.gnidenko.util.TransactionManager;

import java.util.List;

import static ru.gnidenko.service.UserChecker.*;

@RequiredArgsConstructor
public class UserService {
    private final TransactionManager manager;
    private final UserRepo userRepo;

    public User createUser(User user) {
        checkUserIsNotNull(user);
        checkUserFields(user);

        return manager.executeInTransaction(session -> {
            //При создание юзера он может только читать
            user.setRole(UserRole.READ);
            return userRepo.save(user, session);
        });
    }

    public User updateUser(Long id, User user) {
        checkUserIsNotNull(user);

        return manager.executeInTransaction(session -> {

            User userToUpdate = userRepo.findById(id, session)
                .orElseThrow(()->new NullPointerException("User with id " + id + " not found")
            );

            if (user.getUsername() != null && !user.getUsername().isBlank()) {
                userToUpdate.setUsername(user.getUsername());
            }
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                userToUpdate.setPassword(user.getPassword());
            }

            return userToUpdate;

        });
    }

    public void deleteUser(Long id) {
        manager.executeInTransaction(session -> {
            userRepo.delete(id, session);
        });
    }

    public User getUserById(Long id) {
        return manager.executeInTransaction(session -> {
            return userRepo.findById(id, session)
                .orElseThrow(()->new NullPointerException("User with id " + id + " not found"));
        });
    }

    public List<User> getAllUsers() {
        return manager.executeInTransaction(userRepo::findAll);
    }
}
