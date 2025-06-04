package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с книгами в книжном магазине.
 * Обеспечивает отображение детальной информации о книгах и похожих книг.
 */
@Controller
public class BookController {
    @Autowired private BookRepository bookRepository;

    /**
     * Отображает детальную информацию о книге по её идентификатору.
     *
     * <p>Метод выполняет следующие действия:
     * <ul>
     *   <li>Находит книгу по ID в репозитории</li>
     *   <li>Устанавливает дефолтные значения для null-полей (количество и цена)</li>
     *   <li>Находит похожие книги по жанрам и тегам (ограничение - 6 книг)</li>
     *   <li>Добавляет книгу и похожие книги в модель</li>
     * </ul>
     *
     * @param id Идентификатор книги
     * @param model Модель для передачи данных в представление
     * @return Название представления "bookCard" с деталями книги
     * @throws ResponseStatusException Если книга не найдена (HTTP 404)
     */
    @GetMapping("/books/{id}")
    public String showBookDetails(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id).orElseThrow();

        if (book.getAvailableAmount() == null) {
            book.setAvailableAmount(0);
        }
        if (book.getPrice() == null) {
            book.setPrice(0.0);
        }

        model.addAttribute("book", book);

        List<Book> similarBooks = bookRepository.findSimilarBooks(
                book.getId(),
                book.getGenres().stream().map(Genre::getId).collect(Collectors.toList()),
                book.getTags().stream().map(Tag::getId).collect(Collectors.toList())
                ).stream()
                .limit(6)
                .collect(Collectors.toList()
        );

        model.addAttribute("similarBooks", similarBooks);

        return "bookCard";
    }
}
