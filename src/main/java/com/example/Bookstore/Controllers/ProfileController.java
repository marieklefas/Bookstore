package com.example.Bookstore.Controllers;


import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.*;
import com.example.Bookstore.Services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserService userService;

    @GetMapping
    public String profilePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<Order> allOrders = orderRepository.findByUser(user);
        List<Book> favorites = new ArrayList<>(user.getFavorites());
        List<Book> purchasedBooks = new ArrayList<>();
        for (Order order : allOrders) {
            for (OrderItems orderItem : order.getItems()) {
                purchasedBooks.add(orderItem.getBook());
            }
        }

        Set<Book> allBooksForStats = new HashSet<>(favorites);
        allBooksForStats.addAll(purchasedBooks);
        List<Book> combinedBooks = new ArrayList<>(allBooksForStats);

        // Calculate statistics for charts
        // Genres
        Map<String, Long> genreCounts = combinedBooks.stream()
                .flatMap(book -> book.getGenres().stream())
                .collect(Collectors.groupingBy(
                        genre -> genre.getName() != null ? genre.getName() : "Неизвестный жанр",
                        Collectors.counting()
                ));

        // Tags
        Map<String, Long> tagCounts = combinedBooks.stream()
                .flatMap(book -> book.getTags().stream())
                .collect(Collectors.groupingBy(
                        tag -> tag.getName() != null ? tag.getName() : "Неизвестный тег",
                        Collectors.counting()
                ));

        // Authors
        Map<String, Long> authorCounts = combinedBooks.stream()
                .flatMap(book -> book.getAuthors().stream())
                .collect(Collectors.groupingBy(
                        author -> author.getName() != null ? author.getName() : "Неизвестный автор",
                        Collectors.counting()
                ));

        // Languages
        Map<String, Long> languageCounts = combinedBooks.stream()
                .map(Book::getLanguage)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        language -> language.getName() != null ? language.getName() : "Неизвестный язык",
                        Collectors.counting()
                ));

        // Cover Types
        Map<String, Long> coverTypeCounts = combinedBooks.stream()
                .map(Book::getCoverType)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        coverType -> coverType,
                        Collectors.counting()
                ));

        // Add statistics to model
        model.addAttribute("genreCounts", genreCounts);
        model.addAttribute("tagCounts", tagCounts);
        model.addAttribute("authorCounts", authorCounts);
        model.addAttribute("languageCounts", languageCounts);
        model.addAttribute("coverTypeCounts", coverTypeCounts);

        // Основные счетчики
        model.addAttribute("ordersCount", allOrders.size());
        model.addAttribute("favoritesCount", favorites.size());
        model.addAttribute("purchasedCount", purchasedBooks.stream().count());

        return "Profile/profile";
    }

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
        model.addAttribute("favoritesCount", favorites.size());
        return "Profile/favorites";
    }

    @GetMapping("/personalData")
    public String personalDataPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow();
        model.addAttribute("user", user);
        return "Profile/personalData";
    }

    @PostMapping("/personalData")
    public String updatePersonalData(@ModelAttribute("user") User formUser,
                                     @RequestParam(required = false) String currentPassword,
                                     @RequestParam(required = false) String newPassword,
                                     @RequestParam(required = false) String repeatPassword,
                                     Model model,
                                     @AuthenticationPrincipal UserDetails userDetails){

        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow();

        boolean valid = true;

        if (formUser.getFirstName() != null) user.setFirstName(formUser.getFirstName());
        if (formUser.getLastName() != null) user.setLastName(formUser.getLastName());
        if (formUser.getBirthDate() != null) {
            LocalDate bd = formUser.getBirthDate();
            int age = Period.between(bd, LocalDate.now()).getYears();
            if (age < 12 || age > 120) {
                model.addAttribute("birthDateError", "Возраст должен быть от 12 до 120 лет");
                valid = false;
            } else {
                user.setBirthDate(bd);
            }
        }
        if (formUser.getEmail() != null) user.setEmail(formUser.getEmail());

        if (currentPassword != null && !currentPassword.isBlank()) {
            if (!userService.checkPassword(user, currentPassword)) {
                model.addAttribute("passwordError", "Неверный текущий пароль");
                valid = false;
            } else {
                if (newPassword == null || newPassword.isBlank()) {
                    model.addAttribute("newPasswordError", "Новый пароль не может быть пустым");
                    valid = false;
                } else if (!newPassword.equals(repeatPassword)) {
                    model.addAttribute("repeatPasswordError", "Пароли не совпадают");
                    valid = false;
                } else {
                    user.setPassword(userService.encodePassword(newPassword));
                }
            }
        } else if ((newPassword != null && !newPassword.isBlank()) ||
                (repeatPassword != null && !repeatPassword.isBlank())) {
            model.addAttribute("passwordError", "Введите текущий пароль для смены пароля");
            valid = false;
        }

        if (valid) {
            userRepository.save(user);
            model.addAttribute("successMessage", "Изменения сохранены");
        }

        model.addAttribute("user", user);
        return "Profile/personalData";
    }

    @PostMapping("/delete")
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails,
                                HttpServletRequest request) throws ServletException {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setStatus("Удален");
        userRepository.save(user);
        request.logout();
        return "redirect:/login?accountDeleted";
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
        model.addAttribute("purchasesCount", purchasedBooksList.size());
        return "Profile/purchased";
    }


    @PostMapping("/toggle-favorite/{id}")
    @ResponseBody
    public ResponseEntity<?> toggleFavorite(@PathVariable Long id, Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Book book = bookRepository.findById(id).orElseThrow();

        if (user.getFavorites().contains(book)) {
            user.getFavorites().remove(book);
        } else {
            user.getFavorites().add(book);
        }

        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
