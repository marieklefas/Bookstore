package com.example.Bookstore.Controllers;


import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserCartItemRepository userCartItemRepository;

    @GetMapping("/orders")
    public String ordersPage() {
        return "Profile/orders";
    }

    @GetMapping("/favorites")
    public String favoritesPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<Book> favorites = new ArrayList<>(user.getFavorites());
        Collections.reverse(favorites);

        model.addAttribute("favoriteBooks", favorites);
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
    public String cartPage(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<UserCartItem> cartItems = userCartItemRepository.findByUser(user);

        model.addAttribute("cartItems", cartItems);
        return "Profile/cart";
    }

    @Transactional
    @PostMapping("/cart-remove/{id}")
    public String removeFromCart(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Book book = bookRepository.findById(id).orElseThrow();

        userCartItemRepository.deleteByUserAndBook(user, book);

        return "redirect:/profile/cart";
    }
}
