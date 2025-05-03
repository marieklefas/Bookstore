package com.example.Bookstore.Controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping("/orders")
    public String ordersPage() {
        return "Profile/orders";
    }

    @GetMapping("/favorites")
    public String favoritesPage() {
        return "Profile/favorites";
    }

    @GetMapping("/personalData")
    public String personalDataPage() {
        return "Profile/personalData";
    }

    @GetMapping("/purchased")
    public String purchasedPage() {
        return "Profile/purchased";
    }

    @GetMapping("/cart")
    public String cartPage() {
        return "Profile/cart";
    }
}
