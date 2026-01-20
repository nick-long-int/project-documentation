package ru.gnidenko.service;

import java.util.List;

public interface Service<T> {
    T create(T t);
    T update(Long id, T t);
    void delete(Long id);
    List<T> getAll();
    T getById(Long id);
}
