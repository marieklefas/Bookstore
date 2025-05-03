package com.example.Bookstore.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.*;

import java.util.List;
import java.util.Set;

@Service
public class BookService {
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private GenreRepository genreRepository;
    @Autowired private LanguageRepository languageRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private PublisherRepository publisherRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> findBooksByAuthor(String authorName) {
        return bookRepository.findByAuthorsName(authorName);
    }

    public List<Book> findBooksByGenre(String genreName) {
        return bookRepository.findByGenresName(genreName);
    }

    public void decreaseAvailableAmount(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Книга не найдена"));
        if (book.getAvailableAmount() > 0) {
            book.setAvailableAmount(book.getAvailableAmount() - 1);
            bookRepository.save(book);
        } else {
            throw new IllegalStateException("Нет экземпляров в наличии");
        }
    }
}
