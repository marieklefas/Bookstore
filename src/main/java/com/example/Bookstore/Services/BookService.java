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

//    public Book createBook(String title,
//                           String description,
//                           byte[] coverImg,
//                           int pageNumber,
//                           String ageLimit,
//                           int publishingYear,
//                           int availableAmount,
//                           String languageName,
//                           String publisherName,
//                           CoverType coverType,
//                           Set<String> authorNames,
//                           Set<String> genreNames,
//                           Set<String> tagNames) {
//
//        Book book = new Book(title, description, coverImg, pageNumber, ageLimit, publishingYear, availableAmount, coverType);
//
//        if (languageName == null || languageName.isBlank()) {
//            throw new IllegalArgumentException("Язык книги не может быть пустым");
//        }
//        if (publisherName == null || publisherName.isBlank()) {
//            throw new IllegalArgumentException("Издательство книги не может быть пустым");
//        }
//
//        Language language = languageRepository.findByName(languageName)
//                .orElseGet(() -> languageRepository.save(new Language(languageName)));
//
//        Publisher publisher = publisherRepository.findByName(publisherName)
//                .orElseGet(() -> publisherRepository.save(new Publisher(publisherName)));
//
//        System.out.print(language);
//        System.out.print(publisher);
//
//        book.setLanguage(language);
//        book.setPublisher(publisher);
//
//        for (String authorName : authorNames) {
//            Author author = authorRepository.findByName(authorName)
//                    .orElseGet(() -> authorRepository.save(new Author(authorName)));
//            book.addAuthor(author);
//        }
//
//        for (String genreName : genreNames) {
//            Genre genre = genreRepository.findByName(genreName)
//                    .orElseGet(() -> genreRepository.save(new Genre(genreName)));
//            book.addGenre(genre);
//        }
//
//        for (String tagName : tagNames) {
//            Tag tag = tagRepository.findByName(tagName)
//                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));
//            book.addTag(tag);
//        }
//
//        return bookRepository.save(book);
//    }
//
//    public List<Book> findBooksByAuthor(String authorName) {
//        return bookRepository.findByAuthorsName(authorName);
//    }
//
//    public List<Book> findBooksByGenre(String genreName) {
//        return bookRepository.findByGenresName(genreName);
//    }
//
//    public void decreaseAvailableAmount(Long bookId) {
//        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Книга не найдена"));
//        if (book.getAvailableAmount() > 0) {
//            book.setAvailableAmount(book.getAvailableAmount() - 1);
//            bookRepository.save(book);
//        } else {
//            throw new IllegalStateException("Нет экземпляров в наличии");
//        }
//    }

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
