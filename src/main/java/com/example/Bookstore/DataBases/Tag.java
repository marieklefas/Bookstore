package com.example.Bookstore.DataBases;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность, представляющая тег для категоризации книг.
 * Позволяет группировать книги по тематическим меткам.
 */
@Entity
@Table(name = "Tags")
public class Tag {
    public Tag() {}

    public Tag(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id")
    private Long id;

    @Column(name= "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Book> books = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<Book> getBooks() { return books; }
    public void setBooks(Set<Book> books) { this.books = books; }
}
