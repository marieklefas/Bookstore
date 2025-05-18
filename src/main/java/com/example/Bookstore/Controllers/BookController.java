package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.Book;
import com.example.Bookstore.Repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class BookController {
    @Autowired private BookRepository bookRepository;

    @GetMapping("/books/{id}")
    public String showBookDetails(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Книга не найдена"
                ));

        // Устанавливаем дефолтные значения для null полей
        if (book.getAvailableAmount() == null) {
            book.setAvailableAmount(0);
        }
        if (book.getPrice() == null) {
            book.setPrice(0.0);
        }

        model.addAttribute("book", book);
        return "bookCard";
    }
}
