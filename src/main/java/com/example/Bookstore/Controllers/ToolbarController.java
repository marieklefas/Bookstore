package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.Book;
import com.example.Bookstore.Repositories.BookRepository;
import com.example.Bookstore.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ToolbarController {
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping("/")
    public String startPage() {
        return "redirect:/home";
    }

//    @GetMapping("/home")
//    public String homePage() {
//        return "home";
//    }

    @GetMapping("/home")
    public String getAllBooks(Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        return "home";
    }

    @GetMapping("/city")
    public String cityPage() {
        return "city";
    }

    @GetMapping("/stores")
    public String storesPage() {
        return "storesInfo";
    }

    @GetMapping("/deliveryandpaymentinfo")
    public String dandpPage() {
        return "deliveryAndPaymentInfo";
    }

    @GetMapping("/managingbooks")
    public String managingBooksPage() {
        return "BookManaging/managing";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    @GetMapping("/catalog")
    public String catalogPage() {
        return "catalog";
    }

    @GetMapping("/search")
    public String searchPage() {
        return "search";
    }

    @GetMapping("/profile")
    public String accountPage() {
        return "Profile/profile";
    }

}