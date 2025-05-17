package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.Book;
import com.example.Bookstore.DataBases.PromoCode;
import com.example.Bookstore.DataBases.User;
import com.example.Bookstore.DataBases.UserCartItem;
import com.example.Bookstore.Repositories.BookRepository;
import com.example.Bookstore.Repositories.PromoCodeRepository;
import com.example.Bookstore.Repositories.UserCartItemRepository;
import com.example.Bookstore.Repositories.UserRepository;
import com.example.Bookstore.Services.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserCartItemRepository userCartItemRepository;
    @Autowired private PromoCodeRepository promoCodeRepository;
    @Autowired private OrderService orderService;

    private PromoCode activePromo;

    @GetMapping("")
    public String cartPage(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<UserCartItem> cartItems = userCartItemRepository.findByUser(user);

        int  totalPrice = (int) Math.round(cartItems.stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum());

        double discount = 0;
        if (activePromo != null && LocalDate.now().isAfter(activePromo.getStartDate())
                && LocalDate.now().isBefore(activePromo.getEndDate().plusDays(1))) {
            if (activePromo.getDiscountType().equalsIgnoreCase("Фиксированное значение")) {
                discount = activePromo.getDiscountValue();
            } else if (activePromo.getDiscountType().equalsIgnoreCase("Процентное значение")) {
                discount = totalPrice * activePromo.getDiscountValue() / 100;
            }
        }

        double finalPrice = Math.max(0, totalPrice - discount);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("itemCount", cartItems.size());
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("discount", discount);
        model.addAttribute("finalPrice", finalPrice);

        if (!model.containsAttribute("promoStatus")) model.addAttribute("promoStatus", null);
        if (!model.containsAttribute("promoCode")) model.addAttribute("promoCode", "");


        return "Profile/cart";
    }

    @PostMapping("/cart-add/{bookId}")
    @ResponseBody
    public ResponseEntity<?> addToCart(@PathVariable Long bookId, Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Book book = bookRepository.findById(bookId).orElseThrow();

        UserCartItem item = userCartItemRepository.findByUserAndBook(user, book)
                .orElse(new UserCartItem(user, book, 0));

        if (item.getQuantity() < book.getAvailableAmount()) {
            item.setQuantity(item.getQuantity() + 1);
            userCartItemRepository.save(item);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/cart-increase/{bookId}")
    @ResponseBody
    public ResponseEntity<?> increaseCart(@PathVariable Long bookId, Principal principal) {
        return addToCart(bookId, principal); // Повторно используем
    }

    @PostMapping("/cart-decrease/{bookId}")
    @ResponseBody
    public ResponseEntity<?> decreaseCart(@PathVariable Long bookId, Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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

        return ResponseEntity.ok().build();
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

    @Transactional
    @PostMapping("/cart-clear")
    public String clearCart(Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        userCartItemRepository.deleteAllByUser(user);
        return "redirect:/cart";
    }

    @PostMapping("/apply-promocode")
    public String applyPromocode(@RequestParam String promoCode, Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<UserCartItem> cartItems = userCartItemRepository.findByUser(user);

        String promoStatus;
        Optional<PromoCode> promo = promoCodeRepository.findByCodeIgnoreCase(promoCode.trim());

        if (promo.isPresent()) {
            PromoCode code = promo.get();
            if (LocalDate.now().isBefore(code.getStartDate()) || LocalDate.now().isAfter(code.getEndDate())) {
                promoStatus = "expired";
                activePromo = null;
            } else {
                activePromo = code;
                promoStatus = "ok";
            }
        } else {
            promoStatus = "not_found";
            activePromo = null;
        }

        model.addAttribute("promoStatus", promoStatus);
        model.addAttribute("promoCode", promoCode);

        return cartPage(model, principal);
    }

    @Transactional
    @PostMapping("/placeorder")
    public String checkout(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        orderService.createOrderFromCart(user, activePromo);

        userCartItemRepository.deleteAllByUser(user);
        activePromo = null;

        return "redirect:/profile/orders";
    }
}
