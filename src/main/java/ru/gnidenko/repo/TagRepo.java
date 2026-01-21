package ru.gnidenko.repo;

import org.hibernate.Session;
import ru.gnidenko.model.Tag;

import java.util.List;
import java.util.Optional;

public class TagRepo implements Repo<Tag> {

    @Override
    public Tag save(Tag tag, Session session) {
        session.persist(tag);
        return tag;
    }

    @Override
    public Optional<Tag> findById(Long id, Session session) {
        return Optional.ofNullable(session.find(Tag.class, id));
    }

    @Override
    public void delete(Long id, Session session) {
        String query = "delete from Tag t where t.id = :id";
        session.createQuery(query, TagRepo.class)
            .setParameter("id", id)
            .executeUpdate();
    }

    @Override
    public List<Tag> findAll(Session session) {
        return session.createQuery("from Tag", Tag.class).list();
    }
}
