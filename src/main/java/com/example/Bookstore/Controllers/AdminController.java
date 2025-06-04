package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.*;
import com.example.Bookstore.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * Контроллер для административной панели управления книжным магазином.
 * Обеспечивает функционал управления книгами, авторами, жанрами, языками, тегами,
 * издательствами, промокодами, заказами и пользователями через web-интерфейс.
 *
 * <p>Основные функции контроллера:
 * <ul>
 *   <li>Добавление и редактирование книг с поддержкой загрузки обложек</li>
 *   <li>Управление справочниками (авторы, жанры, теги, языки, издательства)</li>
 *   <li>Создание и управление промокодами</li>
 *   <li>Просмотр и обработка заказов</li>
 *   <li>Управление пользователями и сотрудниками</li>
 * </ul>
 *
 * <p>Все методы доступны только пользователям с ролью ADMIN.
 */
@Controller
@RequestMapping("/managing")
public class AdminController {
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private GenreRepository genreRepository;
    @Autowired private LanguageRepository languageRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private PublisherRepository publisherRepository;
    @Autowired private PromoCodeRepository promoCodeRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    /**
     * Отображает форму добавления нового автора.
     *
     * @param model Модель для передачи данных в представление
     * @return Имя представления для добавления автора
     */
    @GetMapping("/addauthor")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("repository", authorRepository);
        return "Managing/addAuthor";
    }

    /**
     * Обрабатывает добавление нового автора.
     *
     * @param name Имя автора
     * @return Перенаправление на форму добавления автора
     */
    @PostMapping("/addauthor")
    public String addAuthor(@RequestParam String name) {
        authorRepository.save(new Author(name));
        return "redirect:/managing/addauthor";
    }

    /**
     * Отображает форму добавления нового жанра.
     *
     * @param model Модель для передачи данных в представление
     * @return Имя представления для добавления жанра
     */
    @GetMapping("/addgenre")
    public String showAddGenreForm(Model model) {
        model.addAttribute("repository", genreRepository);
        return "Managing/addGenre";
    }

    /**
     * Обрабатывает добавление нового жанра.
     *
     * @param name Название жанра
     * @return Перенаправление на форму добавления жанра
     */
    @PostMapping("/addgenre")
    public String addGenre(@RequestParam String name) {
        genreRepository.save(new Genre(name));
        return "redirect:/managing/addgenre";
    }

    /**
     * Отображает форму добавления нового языка.
     *
     * @param model Модель для передачи данных в представление
     * @return Имя представления для добавления языка
     */
    @GetMapping("/addlanguage")
    public String showAddLanguageForm(Model model) {
        model.addAttribute("repository", languageRepository);
        return "Managing/addLanguage";
    }

    /**
     * Обрабатывает добавление нового языка (если он еще не существует).
     *
     * @param name Название языка
     * @return Перенаправление на форму добавления языка
     */
    @PostMapping("/addlanguage")
    public String addLanguage(@RequestParam String name) {
        languageRepository.findByName(name)
                .orElseGet(() -> languageRepository.save(new Language(name)));
        return "redirect:/managing/addlanguage";
    }

    /**
     * Отображает форму добавления нового тега.
     *
     * @param model Модель для передачи данных в представление
     * @return Имя представления для добавления тега
     */
    @GetMapping("/addtag")
    public String showAddTagForm(Model model) {
        model.addAttribute("repository", tagRepository);
        return "Managing/addTag";
    }

    /**
     * Обрабатывает добавление нового тега (если он еще не существует).
     *
     * @param name Название тега
     * @return Перенаправление на форму добавления тега
     */
    @PostMapping("/addtag")
    public String addTag(@RequestParam String name) {
        tagRepository.findByName(name)
                .orElseGet(() -> tagRepository.save(new Tag(name)));
        return "redirect:/managing/addtag";
    }

    /**
     * Отображает форму добавления нового издательства.
     *
     * @param model Модель для передачи данных в представление
     * @return Имя представления для добавления издательства
     */
    @GetMapping("/addpublisher")
    public String showAddPublisherForm(Model model) {
        model.addAttribute("repository", publisherRepository);
        return "Managing/addPublisher";
    }

    /**
     * Обрабатывает добавление нового издательства (если он еще не существует).
     *
     * @param name Название издателя
     * @return Перенаправление на форму добавления издательства
     */
    @PostMapping("/addpublisher")
    public String addPublisher(@RequestParam String name) {
        publisherRepository.findByName(name)
                .orElseGet(() -> publisherRepository.save(new Publisher(name)));
        return "redirect:/managing/addpublisher";
    }

    /**
     * Отображает форму добавления нового промокода.
     *
     * @param model Модель для передачи данных в представление
     * @return Имя представления для добавления промокода
     */
    @GetMapping("/addpromocode")
    public String showAddPromoCodeForm(Model model) {
        model.addAttribute("promoCodeRepository", promoCodeRepository);
        return "Managing/addPromoCode";
    }

    /**
     * Обрабатывает добавление нового промокода с валидацией данных.
     *
     * @param name Название промокода
     * @param type Тип промокода (процентный или фиксированный)
     * @param value Значение промокода
     * @param startDate Дата начала действия
     * @param endDate Дата окончания действия
     * @param model Модель для передачи данных и ошибок
     * @return Перенаправление на форму добавления или возврат с ошибками
     */
    @PostMapping("/addpromocode")
    public String addPromoCode(@RequestParam String name,
                               @RequestParam String type,
                               @RequestParam int value,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                               Model model) {

        boolean hasError = false;

        if ("Процентное значение".equals(type) && (value < 1 || value > 100)) {
            model.addAttribute("error", "Процентное значение должно быть от 1 до 100");
            hasError = true;
        } else if (value <= 0) {
            model.addAttribute("error", "Значение должно быть положительным числом");
            hasError = true;
        } else if (startDate.isAfter(endDate)) {
            model.addAttribute("error", "Дата начала не может быть позже даты окончания");
            hasError = true;
        }

        if (hasError) {
            model.addAttribute("title", name);
            model.addAttribute("type", type);
            model.addAttribute("value", value);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            return "Managing/addPromoCode";
        }

        PromoCode promoCode = new PromoCode(name, type, value, startDate, endDate);
        promoCodeRepository.save(promoCode);
        return "redirect:/managing/addpromocode";
    }

    /**
     * Отображает форму добавления новой книги.
     *
     * @param model Модель для передачи данных в представление
     * @return Имя представления для добавления книги
     */
    @GetMapping("/addbook")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("languages", languageRepository.findAll());
        model.addAttribute("publishers", publisherRepository.findAll());
        return "Managing/addBook";
    }

    /**
     * Обрабатывает добавление новой книги с возможностью загрузки обложки.
     *
     * @param book Данные книги
     * @param coverFile Файл обложки (если загружается с устройства)
     * @param coverUrl URL обложки (если загружается по ссылке)
     * @param coverSourceType Тип источника обложки (file/url)
     * @param authorIds Список ID авторов
     * @param genreIds Список ID жанров
     * @param tagIds Список ID тегов
     * @param languageId ID языка
     * @param publisherId ID издателя
     * @param model Модель для передачи данных и ошибок
     * @return Перенаправление на форму добавления или возврат с ошибками
     * @throws IOException При ошибках загрузки файла
     */
    @PostMapping("/addbook")
    public String addBook(@ModelAttribute Book book,
                          @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                          @RequestParam(value = "coverUrl", required = false) String coverUrl,
                          @RequestParam("coverSourceType") String coverSourceType,
                          @RequestParam("authorIds") List<Long> authorIds,
                          @RequestParam("genreIds") List<Long> genreIds,
                          @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
                          @RequestParam("languageId") Long languageId,
                          @RequestParam("publisherId") Long publisherId,
                          Model model) throws IOException {

        if (("file".equals(coverSourceType) && (coverFile == null || coverFile.isEmpty()))) {
            model.addAttribute("error", "Обложка книги обязательна для загрузки");
            return populateModelAndReturn(model, book, authorIds, genreIds, tagIds, languageId, publisherId);
        } else if ("url".equals(coverSourceType) && (coverUrl == null || coverUrl.isEmpty())) {
            model.addAttribute("error", "URL обложки обязателен");
            return populateModelAndReturn(model, book, authorIds, genreIds, tagIds, languageId, publisherId);
        }

        if ("file".equals(coverSourceType)) {
            String originalFilename = coverFile.getOriginalFilename().replaceAll("\\s+", "_");
            String filename = UUID.randomUUID() + "_" + originalFilename;
            Path uploadPath = Paths.get("uploads/images/");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            Files.copy(coverFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            book.setCoverImg("/images/" + filename);
        } else {
            try {
                String filename = UUID.randomUUID() + ".jpg";
                Path uploadPath = Paths.get("uploads/images/");
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(filename);

                URL url = new URL(coverUrl);
                try (InputStream in = url.openStream()) {
                    Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                book.setCoverImg("/images/" + filename);
            } catch (Exception e) {
                model.addAttribute("error", "Не удалось загрузить изображение по указанному URL");
                return populateModelAndReturn(model, book, authorIds, genreIds, tagIds, languageId, publisherId);
            }
        }

        List<Author> authors = authorRepository.findAllById(authorIds);
        book.setAuthors(authors);

        List<Genre> genres = genreRepository.findAllById(genreIds);
        book.setGenres(genres);

        List<Tag> tags = (tagIds != null && !tagIds.isEmpty())
                ? tagRepository.findAllById(tagIds)
                : new ArrayList<>();
        book.setTags(tags);

        languageRepository.findById(languageId).ifPresent(book::setLanguage);
        publisherRepository.findById(publisherId).ifPresent(book::setPublisher);

        book.setStatus("Активна");
        bookRepository.save(book);
        return "redirect:/managing/addbook";
    }

    /**
     * Вспомогательный метод для заполнения модели перед возвратом к форме.
     */
    private String populateModelAndReturn(Model model, Book book, List<Long> authorIds,
                                          List<Long> genreIds, List<Long> tagIds,
                                          Long languageId, Long publisherId) {
        model.addAttribute("book", book);
        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("languages", languageRepository.findAll());
        model.addAttribute("publishers", publisherRepository.findAll());

        model.addAttribute("selectedAuthorIds", authorIds);
        model.addAttribute("selectedGenreIds", genreIds);
        model.addAttribute("selectedTagIds", tagIds != null ? tagIds : new ArrayList<Long>());
        model.addAttribute("selectedLanguageId", languageId);
        model.addAttribute("selectedPublisherId", publisherId);

        return "Managing/addBook";
    }

    /**
     * Отображает форму редактирования существующей книги.
     *
     * @param id ID книги для редактирования
     * @param model Модель для передачи данных
     * @return Имя представления для редактирования книги
     * @throws IllegalArgumentException Если книга не найдена
     */
    @GetMapping("/editbook/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

        model.addAttribute("book", book);
        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("languages", languageRepository.findAll());
        model.addAttribute("publishers", publisherRepository.findAll());

        return "Managing/editBook";
    }

    /**
     * Обрабатывает обновление данных книги.
     *
     * @param id ID редактируемой книги
     * @param book Обновленные данные книги
     * @param coverFile Новый файл обложки (если изменяется)
     * @param coverUrl Новый URL обложки (если изменяется)
     * @param coverSourceType Тип источника обложки
     * @param authorIds Список ID авторов
     * @param genreIds Список ID жанров
     * @param tagIds Список ID тегов
     * @param languageId ID языка
     * @param publisherId ID издателя
     * @param model Модель для передачи ошибок
     * @return Перенаправление на страницу книги или форму редактирования
     * @throws IOException При ошибках загрузки файла
     * @throws IllegalArgumentException Если книга не найдена
     */
    @PostMapping("/editbook/{id}")
    public String editBook(@PathVariable Long id,
                           @ModelAttribute Book book,
                           @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                           @RequestParam(value = "coverUrl", required = false) String coverUrl,
                           @RequestParam("coverSourceType") String coverSourceType,
                           @RequestParam("authorIds") List<Long> authorIds,
                           @RequestParam("genreIds") List<Long> genreIds,
                           @RequestParam("tagIds") List<Long> tagIds,
                           @RequestParam("languageId") Long languageId,
                           @RequestParam("publisherId") Long publisherId,
                           Model model) throws IOException {

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

        existingBook.setTitle(book.getTitle());
        existingBook.setCoverType(book.getCoverType());
        existingBook.setPublishingYear(book.getPublishingYear());
        existingBook.setAgeLimit(book.getAgeLimit());
        existingBook.setPageNumber(book.getPageNumber());
        existingBook.setAvailableAmount(book.getAvailableAmount());
        existingBook.setPrice(book.getPrice());
        existingBook.setDescription(book.getDescription());

        if ("file".equals(coverSourceType) && coverFile != null && !coverFile.isEmpty()) {
            String originalFilename = coverFile.getOriginalFilename().replaceAll("\\s+", "_");
            String filename = UUID.randomUUID() + "_" + originalFilename;
            Path uploadPath = Paths.get("uploads/images/");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            Files.copy(coverFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            existingBook.setCoverImg("/images/" + filename);
        } else if ("url".equals(coverSourceType) && coverUrl != null && !coverUrl.isEmpty()) {
            try {
                String filename = UUID.randomUUID() + ".jpg";
                Path uploadPath = Paths.get("uploads/images/");
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(filename);

                URL url = new URL(coverUrl);
                try (InputStream in = url.openStream()) {
                    Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                existingBook.setCoverImg("/images/" + filename);
            } catch (Exception e) {
                model.addAttribute("error", "Не удалось загрузить изображение по указанному URL");
                return "redirect:/managing/editbook/" + id;
            }
        }

        List<Author> authors = authorRepository.findAllById(authorIds);
        existingBook.setAuthors(authors);

        List<Genre> genres = genreRepository.findAllById(genreIds);
        existingBook.setGenres(genres);

        List<Tag> tags = tagRepository.findAllById(tagIds);
        existingBook.setTags(tags);

        languageRepository.findById(languageId).ifPresent(existingBook::setLanguage);
        publisherRepository.findById(publisherId).ifPresent(existingBook::setPublisher);

        bookRepository.save(existingBook);
        return "redirect:/books/" + id;
    }

    /**
     * Помечает книгу как удаленную (изменяет статус).
     *
     * @param id ID книги для удаления
     * @return Перенаправление на список всех книг
     */
    @PostMapping("/deletebook/{id}")
    public String deleteBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        book.setStatus("Удалена");
        bookRepository.save(book);
        System.out.print("Status is " + book.getStatus());
        return "redirect:/managing/allbooks";
    }

    /**
     * Отображает список всех книг.
     *
     * @param model Модель для передачи списка книг
     * @return Имя представления со списком книг
     */
    @GetMapping("/allbooks")
    public String showAllBooks(Model model) {
        List<Book> allBooks = bookRepository.findAll();
        model.addAttribute("allBooks", allBooks);
        return "Managing/allBooks";
    }

    /**
     * Отображает список всех книг в табличном виде.
     *
     * @param model Модель для передачи списка книг
     * @return Имя представления с таблицей книг
     */
    @GetMapping("/allbooks/tableview")
    public String showAllBooksTableView(Model model) {
        List<Book> allBooks = bookRepository.findAll();
        model.addAttribute("allBooks", allBooks);
        return "Managing/allBooksTableView";
    }

    /**
     * Восстанавливает удаленную книгу (изменяет статус на "Активна").
     *
     * @param id ID книги для восстановления
     * @return Перенаправление на список всех книг
     * @throws IllegalArgumentException Если книга не найдена
     */
    @PostMapping("allbooks/restorebook/{id}")
    public String restoreBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Не найдена книга с id: " + id));

        if ("Удалена".equals(book.getStatus())) {
            book.setStatus("Активна");
            bookRepository.save(book);
        }

        return "redirect:/managing/allbooks";
    }

    /**
     * Восстанавливает удаленную книгу из табличного представления.
     *
     * @param id ID книги для восстановления
     * @return Перенаправление на табличное представление книг
     * @throws IllegalArgumentException Если книга не найдена
     */
    @PostMapping("allbooks/tableview/restorebook/{id}")
    public String restoreBookTableView(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Не найдена книга с id: " + id));

        if ("Удалена".equals(book.getStatus())) {
            book.setStatus("Активна");
            bookRepository.save(book);
        }

        return "redirect:/managing/allbooks/tableview";
    }

    /**
     * Отображает список всех заказов.
     *
     * @param model Модель для передачи списка заказов
     * @return Имя представления со списком заказов
     */
    @GetMapping("/manageorders")
    public String showAllOrders(Model model) {
        List<Order> allOrders = orderRepository.findAllByOrderByOrderDateDesc();
        model.addAttribute("allOrders", allOrders);
        return "Managing/manageOrders";
    }

    /**
     * Отображает детали конкретного заказа.
     *
     * @param id ID заказа
     * @param model Модель для передачи данных заказа
     * @return Имя представления с деталями заказа
     * @throws IllegalArgumentException Если заказ не найден
     */
    @GetMapping("/manageorders/order/{id}")
    public String showOrderDetails(@PathVariable String id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Не найден заказ с id: " + id));
        model.addAttribute("order", order);
        return "Managing/manageOrderDetails";
    }

    /**
     * Помечает заказ как завершенный.
     *
     * @param id ID заказа
     * @return Перенаправление на страницу заказа
     * @throws IllegalArgumentException Если заказ не найден
     */
    @PostMapping("/manageorders/complete/{id}")
    public String completeOrder(@PathVariable String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Не найден заказ с id: " + id));

        if (!"Завершен".equals(order.getStatus())) {
            order.setStatus("Завершен");
            orderRepository.save(order);
        }

        return "redirect:/managing/manageorders/order/" + id;
    }

    /**
     * Отображает форму добавления нового сотрудника.
     *
     * @param model Модель для передачи данных
     * @return Имя представления для добавления сотрудника
     */
    @GetMapping("/addemployee")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("user", new User());
        return "Managing/addEmployee";
    }

    /**
     * Обрабатывает добавление нового сотрудника с валидацией данных.
     *
     * @param user Данные пользователя
     * @param confirmPassword Подтверждение пароля
     * @param model Модель для передачи ошибок и успеха
     * @return Перенаправление или возврат к форме с ошибками
     */
    @PostMapping("/addemployee")
    public String showAddEmployeeForm(@ModelAttribute("user") User user, @RequestParam("confirmPassword") String confirmPassword, Model model) {
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Пароли не совпадают");
            return "Managing/addEmployee";
        }

        LocalDate birthDate = user.getBirthDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 12 || age > 120) {
            model.addAttribute("error", "Возраст должен быть от 12 до 120 лет");
            return "Managing/addEmployee";
        }

        if (userService.registerUser(user)) {
            user.setStatus("Активен");
            user.setRole(Collections.singleton("ROLE_ADMIN"));
            userRepository.save(user);
            model.addAttribute("success", "Сотрудник успешно добавлен");
            model.addAttribute("user", new User());
            return "redirect:/managing/addemployee";
        } else {
            model.addAttribute("error", "Пользователь уже существует");
            return "Managing/addEmployee";
        }
    }

    /**
     * Отображает список всех пользователей.
     *
     * @param model Модель для передачи списка пользователей
     * @return Имя представления со списком пользователей
     */
    @GetMapping("/manageusers")
    public String showAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("countUsers", users.size());
        return "Managing/manageUsers";
    }

    /**
     * Обновляет статус и роль пользователя.
     *
     * @param id ID пользователя
     * @param status Новый статус
     * @param role Новая роль
     * @param model Модель для передачи данных
     * @return Перенаправление на список пользователей
     */
    @PostMapping("/manageusers/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String status,
                             @RequestParam String role,
                             Model model) {
        User user = userRepository.findById(id).orElse(null);
        user.setStatus(status);
        Set<String> roles = new HashSet<>();
        roles.add(role);
        user.setRole(roles);
        userRepository.save(user);
        return "redirect:/managing/manageusers";
    }
}
