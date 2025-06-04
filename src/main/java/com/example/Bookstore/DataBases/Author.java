package com.example.Bookstore.DataBases;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность, представляющая автора книг в системе.
 * Связана отношением многие-ко-многим с сущностью Book.
 */
@Entity
@Table(name = "Authors")
public class Author {
    public Author() {}

    public Author(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id")
    private Long id;

    @Column(name= "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<Book> getBooks() { return books; }
    public void setBooks(Set<Book> books) { this.books = books; }
}
