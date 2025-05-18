package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.*;
import com.example.Bookstore.Services.BookService;
import jakarta.persistence.criteria.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;

import java.util.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class ToolbarController {
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private GenreRepository genreRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private LanguageRepository languageRepository;
    @Autowired private PublisherRepository publisherRepository;

    @Autowired private BookService bookService;

    @GetMapping("/")
    public String startPage() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String showMainPage(Model model) {
        List<Tag> allTags = tagRepository.findAll();

        Map<Tag, List<Book>> tagBookMap = new LinkedHashMap<>();
        for (Tag tag : allTags) {
            List<Book> books = tag.getBooks().stream()
                    .sorted(Comparator.comparing(Book::getTitle)) // сортировка по названию
                    .limit(5)
                    .toList();
            if (!books.isEmpty()) {
                tagBookMap.put(tag, books);
            }
        }

        model.addAttribute("tags", tagBookMap);
        return "Toolbar/mainPage";
    }

    @GetMapping("/location")
    public String cityPage() {
        return "Toolbar/location";
    }

    @GetMapping("/stores")
    public String storesPage() {
        return "Toolbar/storesInfo";
    }

    @GetMapping("/deliveryAndPaymentInfo")
    public String dandpPage() {
        return "Toolbar/deliveryAndPaymentInfo";
    }

    @GetMapping("/managingBooks")
    public String managingBooksPage() {
        return "Managing/managing";
    }

    @GetMapping("/aboutAuthor")
    public String aboutPage() {
        return "Toolbar/aboutAuthor";
    }

    @GetMapping("/catalog")
    public String catalogPage(
            @RequestParam(required = false) Long tag,
            @RequestParam(required = false) List<Long> authors,
            @RequestParam(required = false) List<Long> genres,
            @RequestParam(required = false) List<Long> tags,
            @RequestParam(required = false) List<Long> languages,
            @RequestParam(required = false) List<Long> publishers,
            @RequestParam(required = false) List<String> coverTypes,
            @RequestParam(required = false) List<String> ageLimits,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Model model) {

        List<Long> allSelectedTags = new ArrayList<>();
        if (tags != null) {
            allSelectedTags.addAll(tags);
        }
        if (tag != null && !allSelectedTags.contains(tag)) {
            allSelectedTags.add(tag);
        }

        // Получаем все возможные значения для фильтров
        model.addAttribute("allAuthors", authorRepository.findAll());
        model.addAttribute("allGenres", genreRepository.findAll());
        model.addAttribute("allTags", tagRepository.findAll());
        model.addAttribute("allLanguages", languageRepository.findAll());
        model.addAttribute("allPublishers", publisherRepository.findAll());
        model.addAttribute("allCoverTypes", bookRepository.findDistinctCoverTypes());
        model.addAttribute("allAgeLimits", bookRepository.findDistinctAgeLimits());

        // Установка значений по умолчанию для слайдеров
        Integer minYearValue = minYear != null ? minYear : bookRepository.findMinPublishingYear();
        Integer maxYearValue = maxYear != null ? maxYear : bookRepository.findMaxPublishingYear();
        Double minPriceValue = minPrice != null ? minPrice : bookRepository.findMinPrice();
        Double maxPriceValue = maxPrice != null ? maxPrice : bookRepository.findMaxPrice();

        model.addAttribute("minYearAvailable", bookRepository.findMinPublishingYear());
        model.addAttribute("maxYearAvailable", bookRepository.findMaxPublishingYear());
        model.addAttribute("minPriceAvailable", bookRepository.findMinPrice());
        model.addAttribute("maxPriceAvailable", bookRepository.findMaxPrice());
        model.addAttribute("selectedMinYear", minYearValue);
        model.addAttribute("selectedMaxYear", maxYearValue);
        model.addAttribute("selectedMinPrice", minPriceValue);
        model.addAttribute("selectedMaxPrice", maxPriceValue);

        // Фильтрация книг
        Specification<Book> spec = Specification.where(null);

        if (authors != null && !authors.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Author> authorJoin = root.join("authors");
                return authorJoin.get("id").in(authors);
            });
        }

        if (genres != null && !genres.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Genre> genreJoin = root.join("genres");
                return genreJoin.get("id").in(genres);
            });
        }

        if (!allSelectedTags.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Tag> tagJoin = root.join("tags");
                return tagJoin.get("id").in(allSelectedTags);
            });
        }

        if (languages != null && !languages.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Language> languageJoin = root.join("language");
                return languageJoin.get("id").in(languages);
            });
        }

        if (publishers != null && !publishers.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Publisher> publisherJoin = root.join("publisher");
                return publisherJoin.get("id").in(publishers);
            });
        }

        if (coverTypes != null && !coverTypes.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("coverType").in(coverTypes));
        }

        if (ageLimits != null && !ageLimits.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("ageLimit").in(ageLimits));
        }

        if (minYear != null || maxYear != null) {
            spec = spec.and((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (minYear != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("publishingYear"), minYear));
                }
                if (maxYear != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("publishingYear"), maxYear));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        }

        if (minPrice != null || maxPrice != null) {
            spec = spec.and((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (minPrice != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
                }
                if (maxPrice != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        }

        List<Book> books = bookRepository.findAll(spec);
        model.addAttribute("books", books);

        // Передаем выбранные фильтры для отображения
        model.addAttribute("selectedAuthors", authors);
        model.addAttribute("selectedGenres", genres);
        model.addAttribute("selectedTags", allSelectedTags);
        model.addAttribute("selectedLanguages", languages);
        model.addAttribute("selectedPublishers", publishers);
        model.addAttribute("selectedCoverTypes", coverTypes);
        model.addAttribute("selectedAgeLimits", ageLimits);

        return "Toolbar/catalog";
    }

    @GetMapping("/search")
    public String searchPage(@RequestParam String q,
                             @RequestParam(required = false) List<Long> authors,
                             @RequestParam(required = false) List<Long> genres,
                             @RequestParam(required = false) List<Long> tags,
                             @RequestParam(required = false) List<Long> languages,
                             @RequestParam(required = false) List<Long> publishers,
                             @RequestParam(required = false) List<String> coverTypes,
                             @RequestParam(required = false) List<String> ageLimits,
                             @RequestParam(required = false) Integer minYear,
                             @RequestParam(required = false) Integer maxYear,
                             @RequestParam(required = false) Double minPrice,
                             @RequestParam(required = false) Double maxPrice,
                             Model model) {

        model.addAttribute("searchQuery", q);
        List<Book> initialBooks  = bookRepository.findBySearchQuery(q);

        model.addAttribute("searchResults", initialBooks.stream()
                .flatMap(book -> Stream.concat(
                        Stream.of(book.getTitle()),
                        book.getAuthors().stream().map(Author::getName)
                ))
                .distinct()
                .limit(10)
                .collect(Collectors.toList()));

        // Получаем все возможные значения для фильтров
        model.addAttribute("allAuthors", authorRepository.findAll());
        model.addAttribute("allGenres", genreRepository.findAll());
        model.addAttribute("allTags", tagRepository.findAll());
        model.addAttribute("allLanguages", languageRepository.findAll());
        model.addAttribute("allPublishers", publisherRepository.findAll());
        model.addAttribute("allCoverTypes", bookRepository.findDistinctCoverTypes());
        model.addAttribute("allAgeLimits", bookRepository.findDistinctAgeLimits());

        // Установка значений по умолчанию для слайдеров
        Integer minYearValue = minYear != null ? minYear : bookRepository.findMinPublishingYear();
        Integer maxYearValue = maxYear != null ? maxYear : bookRepository.findMaxPublishingYear();
        Double minPriceValue = minPrice != null ? minPrice : bookRepository.findMinPrice();
        Double maxPriceValue = maxPrice != null ? maxPrice : bookRepository.findMaxPrice();

        model.addAttribute("minYearAvailable", bookRepository.findMinPublishingYear());
        model.addAttribute("maxYearAvailable", bookRepository.findMaxPublishingYear());
        model.addAttribute("minPriceAvailable", bookRepository.findMinPrice());
        model.addAttribute("maxPriceAvailable", bookRepository.findMaxPrice());
        model.addAttribute("selectedMinYear", minYearValue);
        model.addAttribute("selectedMaxYear", maxYearValue);
        model.addAttribute("selectedMinPrice", minPriceValue);
        model.addAttribute("selectedMaxPrice", maxPriceValue);

        // Фильтрация книг
        Specification<Book> spec = Specification.where(null);

        // Базовый фильтр по поисковому запросу
        spec = spec.and((root, query, cb) -> {
            Join<Book, Author> authorJoin = root.join("authors", JoinType.LEFT);
            return cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + q.toLowerCase() + "%"),
                    cb.like(cb.lower(authorJoin.get("name")), "%" + q.toLowerCase() + "%")
            );
        });

        if (authors != null && !authors.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Author> authorJoin = root.join("authors");
                return authorJoin.get("id").in(authors);
            });
        }

        if (genres != null && !genres.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Genre> genreJoin = root.join("genres");
                return genreJoin.get("id").in(genres);
            });
        }

        if (tags != null && !tags.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Tag> tagJoin = root.join("tags");
                return tagJoin.get("id").in(tags);
            });
        }

        if (languages != null && !languages.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Language> languageJoin = root.join("language");
                return languageJoin.get("id").in(languages);
            });
        }

        if (publishers != null && !publishers.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Publisher> publisherJoin = root.join("publisher");
                return publisherJoin.get("id").in(publishers);
            });
        }

        if (coverTypes != null && !coverTypes.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("coverType").in(coverTypes));
        }

        if (ageLimits != null && !ageLimits.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("ageLimit").in(ageLimits));
        }

        if (minYear != null || maxYear != null) {
            spec = spec.and((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (minYear != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("publishingYear"), minYear));
                }
                if (maxYear != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("publishingYear"), maxYear));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        }

        if (minPrice != null || maxPrice != null) {
            spec = spec.and((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (minPrice != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
                }
                if (maxPrice != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        }

        List<Book> books = bookRepository.findAll(spec);
        model.addAttribute("books", books);

        // Передаем выбранные фильтры для отображения
        model.addAttribute("selectedAuthors", authors);
        model.addAttribute("selectedGenres", genres);
        model.addAttribute("selectedTags", tags);
        model.addAttribute("selectedLanguages", languages);
        model.addAttribute("selectedPublishers", publishers);
        model.addAttribute("selectedCoverTypes", coverTypes);
        model.addAttribute("selectedAgeLimits", ageLimits);

        return "Toolbar/catalog";
    }

    @GetMapping("/profile")
    public String accountPage() {
        return "Profile/profile";
    }

}