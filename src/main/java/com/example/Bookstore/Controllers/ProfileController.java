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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;

    private PromoCode activePromo;

    @GetMapping("/orders")
    public String ordersPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<Order> allOrders = orderRepository.findByUser(user);
        List<Order> activeOrders = allOrders.stream()
                .filter(o -> "Активен".equals(o.getStatus()))
                .toList();

        List<Order> completedOrders = allOrders.stream()
                .filter(o -> "Завершен".equals(o.getStatus()))
                .toList();

        model.addAttribute("ordersCount", allOrders.size());
        model.addAttribute("activeOrders", activeOrders);
        model.addAttribute("completedOrders", completedOrders);

        return "Profile/orders";
    }

    @GetMapping("/orders/{orderId}")
    public String OrderDetailsPage(@PathVariable String orderId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow();

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return "redirect:/orders?error=notfound";
        }

        Order order = optionalOrder.get();

        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/orders?error=unauthorized";
        }

        model.addAttribute("order", order);
        return "Profile/orderDetails";
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
    public String purchasedPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<Order> allOrders = orderRepository.findByUser(user);

        List<Book> purchasedBooks = new ArrayList<>();
        for (Order order : allOrders) {
            for (OrderItems orderItem : order.getItems()) {
                purchasedBooks.add(orderItem.getBook());
            }
        }

        List<Book> purchasedBooksList = new ArrayList<>(purchasedBooks);

        model.addAttribute("purchasedBooks", purchasedBooksList);
        return "Profile/purchased";
    }


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
}
