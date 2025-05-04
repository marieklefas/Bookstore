package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.*;
import com.example.Bookstore.Services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/managingbooks")
public class BookController {
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private GenreRepository genreRepository;
    @Autowired private LanguageRepository languageRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private PublisherRepository publisherRepository;
    @Autowired private PromoCodeRepository promoCodeRepository;


    @GetMapping("/addauthor")
    public String showAddAuthorForm() {
        return "BookManaging/addAuthor";
    }

    @PostMapping("/addauthor")
    public String addAuthor(@RequestParam String name) {
        authorRepository.save(new Author(name));
        return "redirect:/managingbooks/addauthor";
    }

    @GetMapping("/addgenre")
    public String showAddGenreForm() {
        return "BookManaging/addGenre";
    }

    @PostMapping("/addgenre")
    public String addGenre(@RequestParam String name) {
        genreRepository.save(new Genre(name));
        return "redirect:/managingbooks/addgenre";
    }

    @GetMapping("/addlanguage")
    public String showAddLanguageForm() {
        return "BookManaging/addLanguage";
    }

    @PostMapping("/addlanguage")
    public String addLanguage(@RequestParam String name) {
        languageRepository.findByName(name)
                .orElseGet(() -> languageRepository.save(new Language(name)));
        return "redirect:/managingbooks/addlanguage";
    }

    @GetMapping("/addtag")
    public String showAddTagForm() {
        return "BookManaging/addTag";
    }

    @PostMapping("/addtag")
    public String addTag(@RequestParam String name) {
        tagRepository.findByName(name)
                .orElseGet(() -> tagRepository.save(new Tag(name)));
        return "redirect:/managingbooks/addtag";
    }

    @GetMapping("/addpublisher")
    public String showAddPublisherForm() {
        return "BookManaging/addPublisher";
    }

    @PostMapping("/addpublisher")
    public String addPublisher(@RequestParam String name) {
        publisherRepository.findByName(name)
                .orElseGet(() -> publisherRepository.save(new Publisher(name)));
        return "redirect:/managingbooks/addpublisher";
    }


    @GetMapping("/addpromocode")
    public String showAddPromoCodeForm() {
        return "BookManaging/addPromoCode";
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
            return "BookManaging/addPromoCode";
        }

        PromoCode promoCode = new PromoCode(name, type, value, startDate, endDate);
        promoCodeRepository.save(promoCode);
        return "redirect:/managingbooks/addpromocode";
    }


    @GetMapping("/addbook")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());

        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("languages", languageRepository.findAll());
        model.addAttribute("publishers", publisherRepository.findAll());
        model.addAttribute("coverTypes");
        model.addAttribute("ageLimits");
        return "BookManaging/addBook";
    }

    @PostMapping("/addbook")
    public String addBook(@ModelAttribute Book book,
                          @RequestParam("coverFile") MultipartFile coverFile,
                          @RequestParam("authorIds") List<Long> authorIds,
                          @RequestParam("genreIds") List<Long> genreIds,
                          @RequestParam("tagIds") List<Long> tagIds,
                          @RequestParam("languageId") Long languageId,
                          @RequestParam("publisherId") Long publisherId) throws IOException {

        if (!coverFile.isEmpty()) {
            String originalFilename = coverFile.getOriginalFilename().replaceAll("\\s+", "_");
            String filename = UUID.randomUUID() + "_" + originalFilename;
            Path uploadPath = Paths.get("uploads/images/");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            Files.copy(coverFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            book.setCoverImg("/images/" + filename);
        }

        List<Author> authors = authorRepository.findAllById(authorIds);
        book.setAuthors(authors);

        List<Genre> genres = genreRepository.findAllById(genreIds);
        book.setGenres(genres);

        List<Tag> tags = tagRepository.findAllById(tagIds);
        book.setTags(tags);

        languageRepository.findById(languageId).ifPresent(book::setLanguage);
        publisherRepository.findById(publisherId).ifPresent(book::setPublisher);

        bookRepository.save(book);
        return "redirect:/managingbooks/addbook";
    }

    @GetMapping("/editbooks")
    public String getAllBooks(Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        return "BookManaging/editBooks";
    }
}
