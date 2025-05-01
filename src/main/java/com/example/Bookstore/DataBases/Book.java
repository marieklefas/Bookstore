package com.example.Bookstore.DataBases;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Books")
public class Book {
    public Book() {}

    public Book(String title, String description,
                Integer pageNumber, String ageLimit, Integer publishingYear,
                Integer availableAmount) {
        this.title = title;
        this.description = description;
        this.pageNumber = pageNumber;
        this.ageLimit = ageLimit;
        this.publishingYear = publishingYear;
        this.availableAmount = availableAmount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id")
    private Long id;

    @Column(name= "title", nullable = false)
    private String title;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors = new ArrayList<>();

    @Column(name= "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "cover_img")
    private String coverImg;

    @ManyToMany
    @JoinTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "language")
    private Language language;

    @Column(name = "page_number", nullable = false)
    private Integer pageNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "cover_type")
    private CoverType coverType;

    @Column(name = "age_limit")
    private String ageLimit;

    @ManyToMany
    @JoinTable(
            name = "book_tag",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @Column(name = "publishing_year")
    private Integer publishingYear;

    @ManyToOne
    @JoinColumn(name = "publisher")
    private Publisher publisher;

    @Column(name = "available_amount")
    private Integer availableAmount;

    @Column(name = "price")
    private Double price;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<Author> getAuthors() { return authors; }
    public void setAuthors(List<Author> authors) { this.authors = authors; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImg() { return coverImg; }
    public void setCoverImg(String coverImg) { this.coverImg = coverImg; }

    public List<Genre> getGenres() { return genres; }
    public void setGenres(List<Genre> genres) { this.genres = genres; }

    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }

    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }

    public CoverType getCoverType() { return coverType; }
    public void setCoverType(CoverType coverType) { this.coverType = coverType; }

    public String getAgeLimit() { return ageLimit; }
    public void setAgeLimit(String ageLimit) { this.ageLimit = ageLimit; }

    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }

    public Integer getPublishingYear() { return publishingYear; }
    public void setPublishingYear(Integer publishingYear) { this.publishingYear = publishingYear; }

    public Publisher getPublisher() { return publisher; }
    public void setPublisher(Publisher publisher) { this.publisher = publisher; }

    public Integer getAvailableAmount() { return availableAmount; }
    public void setAvailableAmount(Integer availableAmount) { this.availableAmount = availableAmount; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

//    public void addAuthor(Author author) {
//        authors.add(author);
//        author.getBooks().add(this);
//    }
//
//    public void removeAuthor(Author author) {
//        authors.remove(author);
//        author.getBooks().remove(this);
//    }
//
//    public void addGenre(Genre genre) {
//        genres.add(genre);
//        genre.getBooks().add(this);
//    }
//
//    public void removeGenre(Genre genre) {
//        genres.remove(genre);
//        genre.getBooks().remove(this);
//    }
//
//    public void addTag(Tag tag) {
//        tags.add(tag);
//        tag.getBooks().add(this);
//    }
//
//    public void removeTag(Tag tag) {
//        tags.remove(tag);
//        tag.getBooks().remove(this);
//    }
//
//    public void decrementAvailableAmount() {
//        if (availableAmount != null && availableAmount > 0) {
//            availableAmount--;
//        }
//    }
}
