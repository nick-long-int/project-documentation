package ru.gnidenko.service;

import lombok.RequiredArgsConstructor;
import ru.gnidenko.model.Page;
import ru.gnidenko.model.Tag;
import ru.gnidenko.repo.PageRepo;
import ru.gnidenko.repo.TagRepo;
import ru.gnidenko.util.TransactionManager;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.gnidenko.util.Checker.*;

@RequiredArgsConstructor
public class TagService implements Service<Tag> {
    private final TransactionManager manager;
    private final TagRepo repo;
    private final PageRepo pageRepo;

    @Override
    public Tag create(Tag tag) {
        checkIsNotNull(tag, "tag");
        checkTagFields(tag);

        return manager.executeInTransaction(session -> {
            if (tag.getPages() != null && !tag.getPages().isEmpty()) {
                Set<Page> pages = tag.getPages()
                    .stream()
                    .map(page -> pageRepo.findById(page.getId(), session)
                        .orElseThrow(() -> new NullPointerException("Page not found")))
                    .collect(Collectors.toSet());
                tag.setPages(pages);
            }
            return repo.save(tag, session);
        });
    }

    @Override
    public Tag update(Long id, Tag tag) {
        checkIsNotNull(tag, "tag");
        checkIsNotNull(id, "id");

        return manager.executeInTransaction(session -> {

            Tag tagToUpdate = repo.findById(id, session)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

            if (tag.getTag() != null && !tag.getTag().isBlank()){
                tagToUpdate.setTag(tag.getTag());
            }
            if (tag.getPages() != null && !tag.getPages().isEmpty()){
                Set<Page> pagesToUpdate = tagToUpdate.getPages();
                pagesToUpdate.addAll(
                    tag.getPages().stream()
                        .map(page -> pageRepo.findById(page.getId(), session)
                            .orElseThrow(() -> new NullPointerException("Page not found")))
                        .collect(Collectors.toSet())
                );
                tagToUpdate.setPages(pagesToUpdate);
            }

            return tagToUpdate;
        });

    }

    @Override
    public void delete(Long id) {
        checkIsNotNull(id, "id");
        manager.executeInTransaction(session -> {
            repo.delete(id, session);
        });
    }

    @Override
    public List<Tag> getAll() {
        return manager.executeInTransaction(repo::findAll);
    }

    @Override
    public Tag getById(Long id) {
        checkIsNotNull(id, "id");
        return manager.executeInTransaction(session -> {
                return repo.findById(id, session)
                    .orElseThrow(() -> new NullPointerException("Tag not found"));
            }
        );
    }
}
