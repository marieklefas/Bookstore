package com.example.Bookstore.DataBases;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Books")
public class Book {
    public Book() {}

    public Book(String title, String description, String coverType,
                Integer pageNumber, String ageLimit, Integer publishingYear,
                Integer availableAmount) {
        this.title = title;
        this.description = description;
        this.coverType = coverType;
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

    @Column(name = "cover_img", nullable = false)
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

    @Column(name = "cover_type")
    private String coverType;

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

    @ManyToMany(mappedBy = "favorites")
    private List<User> userFavorites = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<UserCartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<OrderItems> orderItems = new ArrayList<>();


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

    public String getCoverType() { return coverType; }
    public void setCoverType(String coverType) { this.coverType = coverType; }

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

    public List<User> getUserFavorites() { return userFavorites; }
    public void setUserFavorites(List<User> userFavorites) { this.userFavorites = userFavorites; }

    public List<UserCartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<UserCartItem> cartItems) { this.cartItems = cartItems; }

    public List<OrderItems> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItems> orderItems) { this.orderItems = orderItems; }


    public int getUserCartCount(String username) {
        return cartItems.stream()
                .filter(item -> item.getUser().getUsername().equals(username))
                .findFirst()
                .map(UserCartItem::getQuantity)
                .orElse(0);
    }
}
