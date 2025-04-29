package com.example.Bookstore.DataBases;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Flow;

@Entity
@Table(name = "Books")
public class Book {
    public Book() {}

    public Book(String title, String description, byte[] coverImg,
                Integer pageNumber, String ageLimit, Integer publishingYear,
                Integer availableAmount, CoverType coverType) {
        this.title = title;
        this.description = description;
        this.coverImg = coverImg;
        this.pageNumber = pageNumber;
        this.ageLimit = ageLimit;
        this.publishingYear = publishingYear;
        this.availableAmount = availableAmount;
        this.coverType = coverType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id")
    private Long id;

    @Column(name= "title", nullable = false)
    private String title;

    @Column(name= "description", nullable = false)
    private String description;

    @Lob
    @Column(name = "cover_img", nullable = false)
    private byte[] coverImg;

    @Column(name = "page_number", nullable = false)
    private Integer pageNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "cover_type")
    private CoverType coverType;

    @Column(name = "age_limit")
    private String ageLimit;

    @Column(name = "publishing_year")
    private Integer publishingYear;

    @Column(name = "available_amount")
    private Integer availableAmount;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_tag",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Set<Author> getAuthors() { return authors; }
    public void setAuthors(Set<Author> authors) { this.authors = authors; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public byte[] getCoverImg() { return coverImg; }
    public void setCoverImg(byte[] coverImg) { this.coverImg = coverImg; }

    public Set<Genre> getGenres() { return genres; }
    public void setGenres(Set<Genre> genres) { this.genres = genres; }

    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }

    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }

    public CoverType getCoverType() { return coverType; }
    public void setCoverType(CoverType coverType) { this.coverType = coverType; }

    public String getAgeLimit() { return ageLimit; }
    public void setAgeLimit(String ageLimit) { this.ageLimit = ageLimit; }

    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    public Integer getPublishingYear() { return publishingYear; }
    public void setPublishingYear(Integer publishingYear) { this.publishingYear = publishingYear; }

    public Publisher getPublisher() { return publisher; }
    public void setPublisher(Publisher publishers) { this.publisher = publisher; }

    public Integer getAvailableAmount() { return availableAmount; }
    public void setAvailableAmount(Integer availableAmount) { this.availableAmount = availableAmount; }

    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
        author.getBooks().remove(this);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
        genre.getBooks().add(this);
    }

    public void removeGenre(Genre genre) {
        genres.remove(genre);
        genre.getBooks().remove(this);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getBooks().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getBooks().remove(this);
    }

    public void decrementAvailableAmount() {
        if (availableAmount != null && availableAmount > 0) {
            availableAmount--;
        }
    }
}
