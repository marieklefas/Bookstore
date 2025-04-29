package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.*;
import com.example.Bookstore.Services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Controller
public class BookController {
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
        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("languages", languageRepository.findAll());
        model.addAttribute("publishers", publisherRepository.findAll());
        return "BookManaging/addBook";
    }

    @PostMapping("/addbook")
    public String addBook(@RequestParam String title,
                          @RequestParam String description,
                          @RequestParam("coverImg") MultipartFile coverImg,
                          @RequestParam int pageNumber,
                          @RequestParam String ageLimit,
                          @RequestParam int publishingYear,
                          @RequestParam int availableAmount,
                          @RequestParam String coverType,
                          @RequestParam String language,
                          @RequestParam String publisher,
                          @RequestParam List<String> authors,
                          @RequestParam List<String> genres,
                          @RequestParam List<String> tags) throws IOException {
        byte[] imageBytes = coverImg.isEmpty() ? null : coverImg.getBytes();
        bookService.createBook(title, description, imageBytes, pageNumber, ageLimit, publishingYear, availableAmount,
                language, publisher, CoverType.valueOf(coverType), new HashSet<>(authors), new HashSet<>(genres), new HashSet<>(tags));
        return "redirect:/addbook";
    }

    @GetMapping("/viewbooks")
    public String getAllBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "BookManaging/viewBooks";
    }
}
