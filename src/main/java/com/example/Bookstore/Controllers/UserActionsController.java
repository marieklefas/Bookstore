package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/book")
public class UserActionsController {
    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserCartItemRepository userCartItemRepository;

    @GetMapping("/toggle-favorite/{id}")
    public String toggleFavorite(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Book book = bookRepository.findById(id).orElseThrow();

        if (user.getFavorites().contains(book)) {
            user.getFavorites().remove(book);
        } else {
            user.getFavorites().add(book);
        }

        userRepository.save(user);
        return "redirect:/home";
    }

    @PostMapping("/cart-add/{bookId}")
    public String addToCart(@PathVariable Long bookId, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Book book = bookRepository.findById(bookId).orElseThrow();

        UserCartItem item = userCartItemRepository.findByUserAndBook(user, book)
                .orElse(new UserCartItem(user, book, 0));

        if (item.getQuantity() < book.getAvailableAmount()) {
            item.setQuantity(item.getQuantity() + 1);
            userCartItemRepository.save(item);
        }

        return "redirect:/home";
    }

    @PostMapping("/cart-increase/{bookId}")
    public String increaseCart(@PathVariable Long bookId, Principal principal) {
        return addToCart(bookId, principal); // Повторно используем
    }

    @PostMapping("/cart-decrease/{bookId}")
    public String decreaseCart(@PathVariable Long bookId, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Book book = bookRepository.findById(bookId).orElseThrow();

        userCartItemRepository.findByUserAndBook(user, book).ifPresent(item -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                userCartItemRepository.save(item);
            } else {
                userCartItemRepository.delete(item);
            }
        });

        return "redirect:/home";
    }
}
