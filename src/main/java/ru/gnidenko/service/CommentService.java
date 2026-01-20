package ru.gnidenko.service;

import lombok.RequiredArgsConstructor;
import ru.gnidenko.model.Comment;
import ru.gnidenko.model.Page;
import ru.gnidenko.model.User;
import ru.gnidenko.repo.CommentRepo;
import ru.gnidenko.repo.PageRepo;
import ru.gnidenko.repo.UserRepo;
import ru.gnidenko.util.TransactionManager;

import java.util.List;

import static ru.gnidenko.util.Checker.*;

@RequiredArgsConstructor
public class CommentService implements Service<Comment> {
    private final TransactionManager manager;
    private final UserRepo userRepo;
    private final PageRepo pageRepo;
    private final CommentRepo repo;

    public Comment create(Comment comment) {
        checkIsNotNull(comment, "comment");
        checkCommentFields(comment);

        return manager.executeInTransaction(session -> {

            User user = userRepo.findById(comment.getUser().getId(), session)
                .orElseThrow(() -> new RuntimeException("user not found"));
            Page page = pageRepo.findById(comment.getPage().getId(), session)
                .orElseThrow(() -> new RuntimeException("page not found"));

            comment.setUser(user);
            comment.setPage(page);

            return repo.save(comment, session);
        });

    }

    @Override
    public Comment update(Long id, Comment comment) {
        checkIsNotNull(comment, "comment");
        checkIsNotNull(id, "id");

        return manager.executeInTransaction(session -> {

            Comment commentToUpdate = repo.findById(id,session)
                .orElseThrow(() -> new RuntimeException("comment not found"));

            if (comment.getComment() != null && !comment.getComment().isBlank()) {
                commentToUpdate.setComment(comment.getComment());
            }

            return commentToUpdate;
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
    public List<Comment> getAll() {
        return manager.executeInTransaction(repo::findAll);
    }

    @Override
    public Comment getById(Long id) {
        return manager.executeInTransaction(session -> {
            return repo.findById(id, session)
                .orElseThrow(() -> new RuntimeException("comment not found"));
        });
    }
}
