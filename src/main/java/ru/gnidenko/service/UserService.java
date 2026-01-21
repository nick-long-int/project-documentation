package ru.gnidenko.service;

import lombok.RequiredArgsConstructor;
import ru.gnidenko.model.Page;
import ru.gnidenko.model.User;
import ru.gnidenko.model.UserRole;
import ru.gnidenko.repo.PageRepo;
import ru.gnidenko.repo.UserRepo;
import ru.gnidenko.util.TransactionManager;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.gnidenko.util.Checker.*;

@RequiredArgsConstructor
public class UserService implements Service<User> {
    private final TransactionManager manager;
    private final PageRepo pageRepo;
    private final UserRepo repo;

    @Override
    public User create(User user) {
        checkIsNotNull(user, "user");
        checkUserFields(user);

        return manager.executeInTransaction(session -> {
            user.setRole(UserRole.READ);
            return repo.save(user, session);
        });
    }

    @Override
    public User update(Long id, User user) {
        checkIsNotNull(user, "user");
        checkIsNotNull(id, "id");

        return manager.executeInTransaction(session -> {

            User userToUpdate = repo.findById(id, session)
                .orElseThrow(()->new NullPointerException("User with id " + id + " not found")
            );

            if (user.getUsername() != null && !user.getUsername().isBlank()) {
                userToUpdate.setUsername(user.getUsername());
            }
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                userToUpdate.setPassword(user.getPassword());
            }
            if (user.getRole() != null) {
                userToUpdate.setRole(user.getRole());
            }
            if (user.getPages() != null && !user.getPages().isEmpty()) {
                Set<Page> pagesToUpdate = userToUpdate.getPages();
                pagesToUpdate.addAll(user.getPages()
                    .stream()
                    .map(page -> pageRepo.findById(page.getId(),session)
                        .orElseThrow(()->new NullPointerException("Page not found")))
                    .collect(Collectors.toSet())
                );
                userToUpdate.setPages(pagesToUpdate);
            }

            return userToUpdate;

        });
    }

    @Override
    public void delete(Long id) {
        checkIsNotNull(id, "id");
        manager.executeInTransaction(session -> {
            repo.delete(id, session);
        });
    }

    @Override
    public User getById(Long id) {
        checkIsNotNull(id, "id");
        return manager.executeInTransaction(session -> {
            return repo.findById(id, session)
                .orElseThrow(()->new NullPointerException("User with id " + id + " not found"));
        });
    }

    @Override
    public List<User> getAll() {
        return manager.executeInTransaction(repo::findAll);
    }
}
