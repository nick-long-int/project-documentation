package ru.gnidenko.service;

import lombok.RequiredArgsConstructor;
import ru.gnidenko.model.Block;
import ru.gnidenko.model.Page;
import ru.gnidenko.repo.BlockRepo;
import ru.gnidenko.repo.PageRepo;
import ru.gnidenko.util.TransactionManager;

import java.util.List;

import static ru.gnidenko.util.Checker.*;

@RequiredArgsConstructor
public class BlockService implements Service<Block> {
    private final TransactionManager manager;
    private final BlockRepo repo;
    private final PageRepo pageRepo;


    @Override
    public Block create(Block block) {
        checkIsNotNull(block, "block");
        checkBlockFields(block);

        return manager.executeInTransaction(session -> {

            if (block.getTitle() == null || block.getTitle().isEmpty()) {
                block.setTitle("New Block");
            }
            if (block.getContent() == null || block.getContent().isEmpty()) {
                block.setContent("Example content");
            }
            Page page = block.getPage();
            block.setPage(pageRepo.findById(page.getId(), session)
                .orElseThrow(() -> new NullPointerException("Page not found")));

            return repo.save(block, session);
        });
    }

    @Override
    public Block update(Long id, Block block) {
        checkIsNotNull(block, "block");
        checkIsNotNull(id, "id");

        return manager.executeInTransaction(session -> {

            Block blockToUpdate = repo.findById(id, session)
                .orElseThrow(() -> new NullPointerException("Can't find block with id:" + id));

            if (block.getTitle() != null && !block.getTitle().isEmpty()) {
                blockToUpdate.setTitle(block.getTitle());
            }
            if (block.getContent() != null && !block.getContent().isEmpty()) {
                blockToUpdate.setContent(block.getContent());
            }
            return blockToUpdate;
        });
    }

    @Override
    public void delete(Long id) {
        manager.executeInTransaction(session -> {
            repo.delete(id, session);
        });
    }

    @Override
    public List<Block> getAll() {
        return manager.executeInTransaction(repo::findAll);
    }

    @Override
    public Block getById(Long id) {
        return manager.executeInTransaction(session -> {
            return repo.findById(id, session)
                .orElseThrow(() -> new NullPointerException("Can't find block with id: " + id));
        });
    }
}
