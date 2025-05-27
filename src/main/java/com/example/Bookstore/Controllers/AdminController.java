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


    @GetMapping("/addauthor")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("repository", authorRepository);
        return "Managing/addAuthor";
    }

    @PostMapping("/addauthor")
    public String addAuthor(@RequestParam String name) {
        authorRepository.save(new Author(name));
        return "redirect:/managing/addauthor";
    }

    @GetMapping("/addgenre")
    public String showAddGenreForm(Model model) {
        model.addAttribute("repository", genreRepository);
        return "Managing/addGenre";
    }

    @PostMapping("/addgenre")
    public String addGenre(@RequestParam String name) {
        genreRepository.save(new Genre(name));
        return "redirect:/managing/addgenre";
    }

    @GetMapping("/addlanguage")
    public String showAddLanguageForm(Model model) {
        model.addAttribute("repository", languageRepository);
        return "Managing/addLanguage";
    }

    @PostMapping("/addlanguage")
    public String addLanguage(@RequestParam String name) {
        languageRepository.findByName(name)
                .orElseGet(() -> languageRepository.save(new Language(name)));
        return "redirect:/managing/addlanguage";
    }

    @GetMapping("/addtag")
    public String showAddTagForm(Model model) {
        model.addAttribute("repository", tagRepository);
        return "Managing/addTag";
    }

    @PostMapping("/addtag")
    public String addTag(@RequestParam String name) {
        tagRepository.findByName(name)
                .orElseGet(() -> tagRepository.save(new Tag(name)));
        return "redirect:/managing/addtag";
    }

    @GetMapping("/addpublisher")
    public String showAddPublisherForm(Model model) {
        model.addAttribute("repository", publisherRepository);
        return "Managing/addPublisher";
    }

    @PostMapping("/addpublisher")
    public String addPublisher(@RequestParam String name) {
        publisherRepository.findByName(name)
                .orElseGet(() -> publisherRepository.save(new Publisher(name)));
        return "redirect:/managing/addpublisher";
    }


    @GetMapping("/addpromocode")
    public String showAddPromoCodeForm(Model model) {
        model.addAttribute("promoCodeRepository", promoCodeRepository);
        return "Managing/addPromoCode";
    }


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
            // Возвращаем введённые значения обратно в форму
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
            // Загрузка файла с устройства
            String originalFilename = coverFile.getOriginalFilename().replaceAll("\\s+", "_");
            String filename = UUID.randomUUID() + "_" + originalFilename;
            Path uploadPath = Paths.get("uploads/images/");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            Files.copy(coverFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            book.setCoverImg("/images/" + filename);
        } else {
            // Загрузка по URL
            try {
                String filename = UUID.randomUUID() + ".jpg";
                Path uploadPath = Paths.get("uploads/images/");
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(filename);

                // Скачиваем изображение по URL
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

        // Обновляем поля книги
        existingBook.setTitle(book.getTitle());
        existingBook.setCoverType(book.getCoverType());
        existingBook.setPublishingYear(book.getPublishingYear());
        existingBook.setAgeLimit(book.getAgeLimit());
        existingBook.setPageNumber(book.getPageNumber());
        existingBook.setAvailableAmount(book.getAvailableAmount());
        existingBook.setPrice(book.getPrice());
        existingBook.setDescription(book.getDescription());

        // Обновляем обложку, если загружена новая
        if ("file".equals(coverSourceType) && coverFile != null && !coverFile.isEmpty()) {
            // Загрузка файла с устройства
            String originalFilename = coverFile.getOriginalFilename().replaceAll("\\s+", "_");
            String filename = UUID.randomUUID() + "_" + originalFilename;
            Path uploadPath = Paths.get("uploads/images/");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            Files.copy(coverFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            existingBook.setCoverImg("/images/" + filename);
        } else if ("url".equals(coverSourceType) && coverUrl != null && !coverUrl.isEmpty()) {
            // Загрузка по URL
            try {
                String filename = UUID.randomUUID() + ".jpg";
                Path uploadPath = Paths.get("uploads/images/");
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(filename);

                // Скачиваем изображение по URL
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

        // Обновляем связанные сущности
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

    @PostMapping("/deletebook/{id}")
    public String deleteBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        book.setStatus("Удалена");
        bookRepository.save(book);
        System.out.print("Status is " + book.getStatus());
        return "redirect:/managing/allbooks";
    }


    @GetMapping("/allbooks")
    public String showAllBooks(Model model) {
        List<Book> allBooks = bookRepository.findAll();
        model.addAttribute("allBooks", allBooks);
        return "Managing/allBooks";
    }

    @PostMapping("/restorebook/{id}")
    public String restoreBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Не найдена книга с id: " + id));

        if ("Удалена".equals(book.getStatus())) {
            book.setStatus("Активна");
            bookRepository.save(book);
        }

        return "redirect:/managing/allbooks";
    }

    @GetMapping("/manageorders")
    public String showAllOrders(Model model) {
        List<Order> allOrders = orderRepository.findAllByOrderByOrderDateDesc();
        model.addAttribute("allOrders", allOrders);
        return "Managing/manageOrders";
    }

    @GetMapping("/manageorders/order/{id}")
    public String showOrderDetails(@PathVariable String id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Не найден заказ с id: " + id));
        model.addAttribute("order", order);
        return "Managing/manageOrderDetails";
    }

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

    @GetMapping("/addemployee")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("user", new User());
        return "Managing/addEmployee";
    }

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

    @GetMapping("/manageusers")
    public String showAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("countUsers", users.size());
        return "Managing/manageUsers";
    }

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
