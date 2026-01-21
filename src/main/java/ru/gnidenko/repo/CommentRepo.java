package ru.gnidenko.repo;

import org.hibernate.Session;
import ru.gnidenko.model.Comment;

import java.util.List;
import java.util.Optional;

public class CommentRepo implements Repo<Comment> {

    @Override
    public Comment save(Comment comment, Session session) {
        session.persist(comment);
        return comment;
    }

    @Override
    public Optional<Comment> findById(Long id, Session session) {
        return Optional.ofNullable(session.find(Comment.class, id));
    }

    @Override
    public void delete(Long id, Session session) {
        String query = "delete from Comment where id = :id";
        session.createQuery(query, Comment.class)
            .setParameter("id", id)
            .executeUpdate();
    }

    @Override
    public List<Comment> findAll(Session session) {
        return session.createQuery("from Comment", Comment.class).list();
    }

}
