package ru.gnidenko.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.gnidenko.model.Block;
import ru.gnidenko.model.Comment;
import ru.gnidenko.model.Page;
import ru.gnidenko.model.Tag;
import ru.gnidenko.model.User;

public class HibernateConfig {
    public static SessionFactory getSessionFactory() {
        return new Configuration()
            .addAnnotatedClasses(User.class)
            .addAnnotatedClass(Page.class)
            .addAnnotatedClass(Block.class)
            .addAnnotatedClass(Comment.class)
            .addAnnotatedClass(Tag.class)
            .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
            .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
            .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5433/documents")
            .setProperty("hibernate.connection.username", "admin")
            .setProperty("hibernate.connection.password", "admin")
            .setProperty("hibernate.hbm2ddl.auto", "update")
            .setProperty("hibernate.show_sql", "true")
            .buildSessionFactory();
    }
}
