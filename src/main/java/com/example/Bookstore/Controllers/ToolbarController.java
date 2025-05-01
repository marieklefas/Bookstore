package com.example.Bookstore.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ToolbarController {
    @GetMapping("/")
    public String startPage() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage() {
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

    @GetMapping("/mangingbooks")
    public String managingBooksPage() {
        return "mangingBooks";
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

    @GetMapping("/account")
    public String accountPage() {
        return "account";
    }

    @GetMapping("/orders")
    public String ordersPage() {
        return "orders";
    }

    @GetMapping("/favorites")
    public String favoritesPage() {
        return "favorites";
    }

    @GetMapping("/cart")
    public String cartPage() {
        return "cart";
    }


}
