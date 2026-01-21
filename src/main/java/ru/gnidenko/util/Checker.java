package ru.gnidenko.util;

import ru.gnidenko.model.Block;
import ru.gnidenko.model.Comment;
import ru.gnidenko.model.Page;
import ru.gnidenko.model.Tag;
import ru.gnidenko.model.User;

public class Checker {
    public static <T> void checkIsNotNull(T obj, String msg) {
        if (obj == null) {
            throw new IllegalArgumentException(msg + " must not be null");
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

    public static void checkTagFields(Tag tag) {
        if (tag.getTag() == null || tag.getTag().isEmpty()) {
            throw new IllegalArgumentException("tag must not be empty");
        }
    }

    public static void checkBlockFields(Block block) {
        if (block.getPage() == null || block.getPage().getId() == null) {
            throw new IllegalArgumentException("block must not be empty");
        }
    }

    public static void checkCommentFields(Comment comment) {
        if (comment.getComment() == null || comment.getComment().isEmpty()) {
            throw new IllegalArgumentException("comment must not be empty");
        }
        if (comment.getUser() == null || comment.getUser().getId() == null) {
            throw new IllegalArgumentException("user must not be empty");
        }
        if (comment.getPage() == null || comment.getPage().getId() == null) {
            throw new IllegalArgumentException("page must not be empty");
        }
    }

    public static void checkPageFields(Page page) {
        if (page.getUsers() == null || page.getUsers().isEmpty()) {
            throw new IllegalArgumentException("users must not be empty");
        }
    }

}
