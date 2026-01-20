package ru.gnidenko.repo;

import org.hibernate.Session;
import ru.gnidenko.model.User;

import java.util.List;
import java.util.Optional;

public class UserRepo {

    public User save(User user, Session session) {
        session.persist(user);
        return user;
    }

    public Optional<User> findById(Long id, Session session) {
        return Optional.ofNullable(session.find(User.class, id));
    }

    public void delete(Long id, Session session) {
        String query = "delete from User where id = :id";
        session.createQuery(query, User.class)
            .setParameter("id", id)
            .executeUpdate();
    }

    public List<User> findAll(Session session) {
        String query = "from User";
        return session.createQuery(query, User.class).list();
    }

}
