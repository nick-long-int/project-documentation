package ru.gnidenko.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.gnidenko.model.Page;
import ru.gnidenko.model.User;
import ru.gnidenko.repo.BlockRepo;
import ru.gnidenko.repo.CommentRepo;
import ru.gnidenko.repo.ElasticRepo;
import ru.gnidenko.repo.PageRepo;
import ru.gnidenko.repo.RedisRepo;
import ru.gnidenko.repo.TagRepo;
import ru.gnidenko.repo.UserRepo;
import ru.gnidenko.util.TransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.gnidenko.util.Checker.*;

@RequiredArgsConstructor
public class PageService implements Service<Page> {
    private final PageRepo repo;
    private final UserRepo userRepo;
    private final BlockRepo blockRepo;
    private final CommentRepo commentRepo;
    private final TagRepo tagRepo;
    private final TransactionManager manager;
    private final ElasticRepo elasticRepo;
    private final RedisRepo redisRepo;

    @Override
    public Page create(Page page) {
        checkIsNotNull(page, "page");
        checkPageFields(page);

        return manager.executeInTransaction(session -> {
            if (page.getTitle() == null || page.getTitle().isEmpty()) {
                page.setTitle("New page");
            }
            if (page.getDescription() == null || page.getDescription().isEmpty()) {
                page.setDescription("New page description");
            }
            page.setVersion(1L);
            page.setUrl(generateUrl(page.getTitle()));
            page.setParent(null);
            page.setTags(null);
            page.setBlocks(null);

            Set<User> users = page.getUsers()
                .stream()
                .map(user -> userRepo.findById(user.getId(), session)
                    .orElseThrow(() -> new NullPointerException("User not found")))
                .collect(Collectors.toSet());

            page.setUsers(users);
            Page savedPage = repo.save(page,session);
            try {
                String json = new ObjectMapper().writeValueAsString(savedPage);
                elasticRepo.addToIndex("pages", savedPage.getId().toString(), savedPage);
                redisRepo.addCache(savedPage.getId().toString(), json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            return savedPage;
        });
    }

    @Override
    public Page update(Long id, Page page) {
        checkIsNotNull(page, "page");
        checkIsNotNull(id, "id");
        return manager.executeInTransaction(session -> {

            Page pageToUpdate = repo.findById(id, session)
                .orElseThrow(() -> new NullPointerException("Page not found"));

            if (page.getTitle() != null && !page.getTitle().isEmpty()) {
                pageToUpdate.setTitle(page.getTitle());
            }
            if (page.getDescription() != null && !page.getDescription().isEmpty()) {
                pageToUpdate.setDescription(page.getDescription());
            }

            if (page.getUsers() != null && !page.getUsers().isEmpty()) {
                pageToUpdate.setUsers(page.getUsers().stream()
                    .map(user -> userRepo.findById(user.getId(),session)
                        .orElseThrow(()-> new NullPointerException("User not found")))
                    .collect(Collectors.toSet()));
            }

            if (page.getBlocks() != null && !page.getBlocks().isEmpty()) {
                pageToUpdate.setBlocks(page.getBlocks().stream()
                    .map(block -> blockRepo.findById(block.getId(),session)
                        .orElseThrow(()-> new NullPointerException("Block not found")))
                    .toList());
            }

            if (page.getComments() != null && !page.getComments().isEmpty()) {
                pageToUpdate.setComments(page.getComments().stream()
                    .map(comment -> commentRepo.findById(comment.getId(),session)
                        .orElseThrow(()-> new NullPointerException("Comment not found")))
                    .toList());
            }

            if (page.getTags() != null && !page.getTags().isEmpty()) {
                pageToUpdate.setTags(page.getTags().stream()
                    .map(tag -> tagRepo.findById(tag.getId(),session)
                        .orElseThrow(()-> new NullPointerException("Tag not found")))
                    .collect(Collectors.toSet()));
            }

            pageToUpdate.setVersion(page.getVersion()+1);
            pageToUpdate.setUrl(page.getUrl());
            pageToUpdate.setParent(pageToUpdate);

            return pageToUpdate;
        });
    }

    @Override
    public void delete(Long id) {
        checkIsNotNull(id, "id");
        manager.executeInTransaction(session -> {
           repo.delete(id, session);
           redisRepo.removeCache(id.toString());
        });
    }

    @Override
    public List<Page> getAll() {
        return manager.executeInTransaction(repo::findAll);
    }

    @Override
    public Page getById(Long id) {
        return manager.executeInTransaction(session -> {
            try {
                Page page = new ObjectMapper().readValue(redisRepo.getCache(id.toString()), Page.class);
                if (page != null) {
                    return page;
                }
                return repo.findById(id, session)
                    .orElseThrow(() -> new NullPointerException("Page not found"));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<?> getByTitle(String title){
        return elasticRepo.searchIntoIndex("pages", "title", title, new Page());
    }

    private String generateUrl(String title){
        Random random = new Random();
        StringBuilder url = new StringBuilder()
            .append("/proj-docs/")
            .append(random.nextInt(500))
            .append("/").append(LocalDateTime.now())
            .append("/").append(title);
        return url.toString();
    }
}
