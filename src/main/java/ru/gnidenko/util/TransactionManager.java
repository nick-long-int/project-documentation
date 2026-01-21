package ru.gnidenko.util;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class TransactionManager {
    private final SessionFactory sessionFactory;

    public <T> T executeInTransaction(Function<Session, T> operation) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            T result = operation.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            session.close();
        }
    }

    public void executeInTransaction(Consumer<Session> operation) {
        executeInTransaction(session -> {
            operation.accept(session);
            return null;
        });
    }
}
