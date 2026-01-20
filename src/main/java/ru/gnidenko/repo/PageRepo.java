package ru.gnidenko.repo;

import org.hibernate.Session;
import ru.gnidenko.model.Page;

import java.util.List;
import java.util.Optional;

public class PageRepo implements Repo<Page> {

    @Override
    public Page save(Page page, Session session) {
        session.persist(page);
        return page;
    }

    @Override
    public Optional<Page> findById(Long id, Session session) {
        return Optional.ofNullable(session.find(Page.class, id));
    }

    @Override
    public void delete(Long id, Session session) {
        String query = "delete from Page where id = :id";
        session.createQuery(query, Page.class)
            .setParameter("id", id)
            .executeUpdate();
    }

    @Override
    public List<Page> findAll(Session session) {
        return session.createQuery("from Page", Page.class).list();
    }
}
