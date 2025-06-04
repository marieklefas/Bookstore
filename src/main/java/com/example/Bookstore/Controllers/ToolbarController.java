package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.*;
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

/**
 * Контроллер для обработки запросов главного меню и навигации.
 * Обеспечивает отображение главной страницы, каталога, поиска и других разделов.
 */
@Controller
public class ToolbarController {
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private GenreRepository genreRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private LanguageRepository languageRepository;
    @Autowired private PublisherRepository publisherRepository;

    /**
     * Перенаправляет запрос с корневого URL на главную страницу.
     *
     * @return Перенаправление на /home
     */
    @GetMapping("/")
    public String startPage() {
        return "redirect:/home";
    }

    /**
     * Отображает главную страницу с подборкой книг по тегам.
     *
     * @param model Модель для передачи данных в представление
     * @return Главная страница
     */
    @GetMapping("/home")
    public String showMainPage(Model model) {
        List<Tag> allTags = tagRepository.findAll();

        Map<Tag, List<Book>> tagBookMap = new LinkedHashMap<>();
        for (Tag tag : allTags) {
            List<Book> books = tag.getBooks().stream()
                    .filter(book -> "Активна".equals(book.getStatus()))
                    .sorted(Comparator.comparing(Book::getTitle))
                    .limit(6)
                    .toList();
            if (!books.isEmpty()) {
                tagBookMap.put(tag, books);
            }
        }

        model.addAttribute("tags", tagBookMap);
        return "Toolbar/mainPage";
    }

    /**
     * Отображает страницу с информацией о местоположении магазина.
     *
     * @return Страница местоположения
     */
    @GetMapping("/location")
    public String cityPage() {
        return "Toolbar/location";
    }

    /**
     * Отображает страницу с информацией о магазинах.
     *
     * @return Страница информации о магазинах
     */
    @GetMapping("/stores")
    public String storesPage() {
        return "Toolbar/storesInfo";
    }

    /**
     * Отображает страницу с информацией о доставке и оплате.
     *
     * @return Страница доставки и оплаты
     */
    @GetMapping("/deliveryAndPaymentInfo")
    public String dandpPage() {
        return "Toolbar/deliveryAndPaymentInfo";
    }

    /**
     * Отображает страницу управления книгами (для администраторов).
     *
     * @return Страница управления
     */
    @GetMapping("/managing")
    public String managingBooksPage() {
        return "Managing/managing";
    }

    /**
     * Отображает страницу "Об авторе".
     *
     * @return Страница об авторе
     */
    @GetMapping("/aboutAuthor")
    public String aboutPage() {
        return "Toolbar/aboutAuthor";
    }

    /**
     * Отображает страницу каталога с возможностью фильтрации и сортировки книг.
     *
     * @param sortBy Способ сортировки (price_asc, price_desc, popular)
     * @param inStock Флаг наличия товара
     * @param tag ID тега для фильтрации
     * @param authors Список ID авторов для фильтрации
     * @param genres Список ID жанров для фильтрации
     * @param tags Список ID тегов для фильтрации
     * @param languages Список ID языков для фильтрации
     * @param publishers Список ID издателей для фильтрации
     * @param coverTypes Список типов обложки для фильтрации
     * @param ageLimits Список возрастных ограничений для фильтрации
     * @param minYear Минимальный год издания
     * @param maxYear Максимальный год издания
     * @param minPrice Минимальная цена
     * @param maxPrice Максимальная цена
     * @param model Модель для передачи данных в представление
     * @return Страница каталога
     */
    @GetMapping("/catalog")
    public String catalogPage(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean inStock,
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

        model.addAttribute("allAuthors", authorRepository.findAll());
        model.addAttribute("allGenres", genreRepository.findAll());
        model.addAttribute("allTags", tagRepository.findAll());
        model.addAttribute("allLanguages", languageRepository.findAll());
        model.addAttribute("allPublishers", publisherRepository.findAll());
        model.addAttribute("allCoverTypes", bookRepository.findDistinctCoverTypes());
        model.addAttribute("allAgeLimits", bookRepository.findDistinctAgeLimits());

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

        Specification<Book> spec = Specification.where((root, query, cb) ->
                cb.equal(root.get("status"), "Активна")
        );

        if (inStock != null && inStock) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThan(root.get("availableAmount"), 0)
            );
        }

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

        if (sortBy != null) {
            switch (sortBy) {
                case "price_asc":
                    books.sort(Comparator.comparing(Book::getPrice));
                    break;
                case "price_desc":
                    books.sort(Comparator.comparing(Book::getPrice).reversed());
                    break;
                case "popular":
                    books.sort((b1, b2) -> {
                        int b1Popularity = b1.getUserFavorites().size() + b1.getOrderItems().size();
                        int b2Popularity = b2.getUserFavorites().size() + b2.getOrderItems().size();
                        return Integer.compare(b2Popularity, b1Popularity);
                    });
                    break;
            }
        }

        model.addAttribute("books", books);
        model.addAttribute("inStock", inStock);
        model.addAttribute("selectedAuthors", authors);
        model.addAttribute("selectedGenres", genres);
        model.addAttribute("selectedTags", allSelectedTags);
        model.addAttribute("selectedLanguages", languages);
        model.addAttribute("selectedPublishers", publishers);
        model.addAttribute("selectedCoverTypes", coverTypes);
        model.addAttribute("selectedAgeLimits", ageLimits);

        return "Toolbar/catalog";
    }

    /**
     * Отображает страницу с результатами поиска книг с возможностью фильтрации.
     *
     * @param sortBy Способ сортировки (price_asc, price_desc, popular)
     * @param inStock Флаг наличия товара
     * @param q Поисковый запрос
     * @param authors Список ID авторов для фильтрации
     * @param genres Список ID жанров для фильтрации
     * @param tags Список ID тегов для фильтрации
     * @param languages Список ID языков для фильтрации
     * @param publishers Список ID издателей для фильтрации
     * @param coverTypes Список типов обложки для фильтрации
     * @param ageLimits Список возрастных ограничений для фильтрации
     * @param minYear Минимальный год издания
     * @param maxYear Максимальный год издания
     * @param minPrice Минимальная цена
     * @param maxPrice Максимальная цена
     * @param model Модель для передачи данных в представление
     * @return Страница каталога с результатами поиска
     */
    @GetMapping("/search")
    public String searchPage(@RequestParam(required = false) String sortBy,
                             @RequestParam(required = false) Boolean inStock,
                             @RequestParam String q,
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

        model.addAttribute("allAuthors", authorRepository.findAll());
        model.addAttribute("allGenres", genreRepository.findAll());
        model.addAttribute("allTags", tagRepository.findAll());
        model.addAttribute("allLanguages", languageRepository.findAll());
        model.addAttribute("allPublishers", publisherRepository.findAll());
        model.addAttribute("allCoverTypes", bookRepository.findDistinctCoverTypes());
        model.addAttribute("allAgeLimits", bookRepository.findDistinctAgeLimits());

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

        Specification<Book> spec = Specification.where((root, query, cb) ->
                cb.equal(root.get("status"), "Активна")
        );

        if (inStock != null && inStock) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThan(root.get("availableAmount"), 0)
            );
        }

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

        if (sortBy != null) {
            switch (sortBy) {
                case "price_asc":
                    books.sort(Comparator.comparing(Book::getPrice));
                    break;
                case "price_desc":
                    books.sort(Comparator.comparing(Book::getPrice).reversed());
                    break;
                case "popular":
                    books.sort((b1, b2) -> {
                        int b1Popularity = b1.getUserFavorites().size() + b1.getOrderItems().size();
                        int b2Popularity = b2.getUserFavorites().size() + b2.getOrderItems().size();
                        return Integer.compare(b2Popularity, b1Popularity);
                    });
                    break;
            }
        }

        model.addAttribute("books", books);
        model.addAttribute("inStock", inStock);
        model.addAttribute("selectedAuthors", authors);
        model.addAttribute("selectedGenres", genres);
        model.addAttribute("selectedTags", tags);
        model.addAttribute("selectedLanguages", languages);
        model.addAttribute("selectedPublishers", publishers);
        model.addAttribute("selectedCoverTypes", coverTypes);
        model.addAttribute("selectedAgeLimits", ageLimits);

        return "Toolbar/catalog";
    }
}