package ru.gnidenko;

import ru.gnidenko.config.HibernateConfig;
import ru.gnidenko.model.Block;
import ru.gnidenko.model.Comment;
import ru.gnidenko.model.Page;
import ru.gnidenko.model.Tag;
import ru.gnidenko.model.User;
import ru.gnidenko.repo.BlockRepo;
import ru.gnidenko.repo.CommentRepo;
import ru.gnidenko.repo.ElasticRepo;
import ru.gnidenko.repo.PageRepo;
import ru.gnidenko.repo.RedisRepo;
import ru.gnidenko.repo.S3Repo;
import ru.gnidenko.repo.TagRepo;
import ru.gnidenko.repo.UserRepo;
import ru.gnidenko.service.BlockService;
import ru.gnidenko.service.CommentService;
import ru.gnidenko.service.PageService;
import ru.gnidenko.service.TagService;
import ru.gnidenko.service.UserService;
import ru.gnidenko.util.TransactionManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        TransactionManager transactionManager = new TransactionManager(HibernateConfig.getSessionFactory());


        PageRepo pageRepo = new PageRepo();
        BlockRepo blockRepo = new BlockRepo();
        CommentRepo commentRepo = new CommentRepo();
        S3Repo s3Repo = new S3Repo("documents");
        TagRepo tagRepo = new TagRepo();
        UserRepo userRepo = new UserRepo();
        ElasticRepo elasticRepo = new ElasticRepo();
        RedisRepo redisRepo = new RedisRepo(20000);

        PageService pageService = new PageService(pageRepo, userRepo, blockRepo, commentRepo, tagRepo, transactionManager, elasticRepo, redisRepo);
        CommentService commentService = new CommentService(transactionManager, userRepo, pageRepo, commentRepo);
        BlockService blockService = new BlockService(transactionManager, s3Repo, blockRepo, pageRepo);
        TagService tagService = new TagService(transactionManager, tagRepo, pageRepo);
        UserService userService = new UserService(transactionManager, pageRepo, userRepo);

        //1. Создание пользователя
        User user = new User();
        user.setUsername("nikolay");
        user.setPassword("password");
        User returnedUser = userService.create(user);

        //2. Пользователь создает страницу
        Page page = new Page();
        Set<User> users = new HashSet<>();
        users.add(user);
        page.setUsers(users);
        Page returnedPage = pageService.create(page);
        Set<Page> pages = new HashSet<>();
        pages.add(returnedPage);
        User toUpdateUser = new User();
        toUpdateUser.setPages(pages);
        userService.update(returnedUser.getId(), user);

        //3.Пользователь добавляет блоки на страницу
        Block block = new Block();
        block.setPage(returnedPage);
        Block block2 = new Block();
        block2.setPage(returnedPage);

        Block returnedBlock1 = blockService.create(block);
        Block returnedBlock2 = blockService.create(block2);

        //4. Пользователь добавляет теги к странице
        Tag tag = new Tag();
        tag.setTag("new");
        tag.setPages(pages);
        Tag returnedTag = tagService.create(tag);

        //5. Пользователь добавляет коммент к странице
        Comment comment = new Comment();
        comment.setComment("Some comment");
        comment.setUser(returnedUser);
        comment.setPage(returnedPage);

        Comment returnedComment = commentService.create(comment);

        //6.Пользователь загружает картинку к первому блоку
        Block blockWithImage = blockService.uploadImage(returnedBlock1, "hi.jpg");
        System.out.println(blockWithImage.getKey());

        //7. Получение заголовка страницы из кеша
        System.out.println(pageService.getById(returnedPage.getId()).getTitle());

        //8. Получение из elastic по заголовку
        pageService.getByTitle("Add").forEach(p -> System.out.println(p.toString()));
        elasticRepo.closeClient();
    }
}