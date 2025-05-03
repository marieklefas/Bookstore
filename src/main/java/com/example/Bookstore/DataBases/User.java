package com.example.Bookstore.DataBases;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Users")
public class User {
    public User() {}

    public User(long id, String firstName, String lastName, int age, String email, String username, String password, Set<String> role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    @Column(name= "id")
    private Long id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "age")
    private int age;

    @Column(name = "email")
    private String email;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> role;

    @ManyToMany
    @JoinTable(
            name = "user-favorites",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> favorites = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user-cart",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> cart = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user-purchase",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> purchase = new ArrayList<>();



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<String> getRole() { return role; }
    public void setRole(Set<String> role) { this.role = role; }

    public List<Book> getFavorites() { return favorites; }
    public void setFavorites(List<Book> favorites) { this.favorites = favorites; }

    public List<Book> getCart() { return cart; }
    public void setCart(List<Book> cart) { this.cart = cart; }

    public List<Book> getPurchase() { return purchase; }
    public void setPurchase(List<Book> purchase) { this.purchase = purchase; }
}
