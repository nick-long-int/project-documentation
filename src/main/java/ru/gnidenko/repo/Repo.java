package ru.gnidenko.repo;

import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public interface Repo<T> {
    T save(T t, Session session);
    Optional<T> findById(Long id, Session session);
    void delete(Long id, Session session);
    List<T> findAll(Session session);
}
