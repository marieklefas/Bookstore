package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BookController {
    @Autowired private BookRepository bookRepository;

    @GetMapping("/books/{id}")
    public String showBookDetails(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id).orElseThrow();

        // Устанавливаем дефолтные значения для null полей
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
