package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.*;
import com.example.Bookstore.Services.*;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Controller
public class BookController {
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private GenreRepository genreRepository;
    @Autowired private BookService bookService;
    @Autowired private LanguageRepository languageRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private PublisherRepository publisherRepository;


    @GetMapping("/addauthor")
    public String showAddAuthorForm() {
        return "BookManaging/addAuthor";
    }

    @PostMapping("/addauthor")
    public String addAuthor(@RequestParam String name) {
        authorRepository.save(new Author(name));
        return "redirect:/addauthor";
    }

    @GetMapping("/addgenre")
    public String showAddGenreForm() {
        return "BookManaging/addGenre";
    }

    @PostMapping("/addgenre")
    public String addGenre(@RequestParam String name) {
        genreRepository.save(new Genre(name));
        return "redirect:/addgenre";
    }

    @GetMapping("/addlanguage")
    public String showAddLanguageForm() {
        return "BookManaging/addLanguage";
    }

    @PostMapping("/addlanguage")
    public String addLanguage(@RequestParam String name) {
        languageRepository.findByName(name)
                .orElseGet(() -> languageRepository.save(new Language(name)));
        return "redirect:/addlanguage";
    }

    @GetMapping("/addtag")
    public String showAddTagForm() {
        return "BookManaging/addTag";
    }

    @PostMapping("/addtag")
    public String addTag(@RequestParam String name) {
        tagRepository.findByName(name)
                .orElseGet(() -> tagRepository.save(new Tag(name)));
        return "redirect:/addtag";
    }

    @GetMapping("/addpublisher")
    public String showAddPublisherForm() {
        return "BookManaging/addPublisher";
    }

    @PostMapping("/addpublisher")
    public String addPublisher(@RequestParam String name) {
        publisherRepository.findByName(name)
                .orElseGet(() -> publisherRepository.save(new Publisher(name)));
        return "redirect:/addpublisher";
    }


    @GetMapping("/addbook")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());

        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("languages", languageRepository.findAll());
        model.addAttribute("publishers", publisherRepository.findAll());
        model.addAttribute("coverTypes", List.of("Мягкая", "Твердая"));
        model.addAttribute("ageLimits", List.of("0+", "6+", "12+", "16+", "18+"));
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

        // Обложка
        if (!coverFile.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + coverFile.getOriginalFilename();
            Path uploadPath = Paths.get("src/main/resources/static/images/");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            Files.copy(coverFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            book.setCoverImg("/images/" + filename);
        }

        // Автор(ы)
        List<Author> authors = authorRepository.findAllById(authorIds);
        book.setAuthors(authors);

        // Жанры
        List<Genre> genres = genreRepository.findAllById(genreIds);
        book.setGenres(genres);

        // Теги
        List<Tag> tags = tagRepository.findAllById(tagIds);
        book.setTags(tags);

        // Язык
        languageRepository.findById(languageId).ifPresent(book::setLanguage);

        // Издательство
        publisherRepository.findById(publisherId).ifPresent(book::setPublisher);

        System.out.println("Title: " + book.getTitle());
        System.out.println("Authors: " + authorIds);
        System.out.println("Genres: " + genreIds);
        System.out.println("Tags: " + tagIds);
        System.out.println("Cover: " + (coverFile != null ? coverFile.getOriginalFilename() : "null"));

        bookRepository.save(book);
        return "redirect:/addbook";
    }

    @GetMapping("/viewbooks")
    public String getAllBooks(Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        return "BookManaging/viewBooks";
    }
}
