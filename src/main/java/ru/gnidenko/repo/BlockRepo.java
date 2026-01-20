package ru.gnidenko.repo;

import org.hibernate.Session;
import ru.gnidenko.model.Block;

import java.util.List;
import java.util.Optional;

public class BlockRepo implements Repo<Block> {
    @Override
    public Block save(Block block, Session session) {
        session.persist(block);
        return block;
    }

    @Override
    public Optional<Block> findById(Long id, Session session) {
        return Optional.ofNullable(session.find(Block.class, id));
    }

    @Override
    public void delete(Long id, Session session) {
        String query = "delete from Block where id = :id";
        session.createQuery(query, Block.class)
            .setParameter("id", id)
            .executeUpdate();
    }

    @Override
    public List<Block> findAll(Session session) {
        return session.createQuery("from Block", Block.class).list();
    }
}
