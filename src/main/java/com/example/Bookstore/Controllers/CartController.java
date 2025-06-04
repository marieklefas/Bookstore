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

/**
 * Контроллер для управления корзиной покупок.
 * Обеспечивает функционал добавления, изменения количества, удаления товаров из корзины,
 * а также применение промокодов и оформление заказов.
 */
@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserCartItemRepository userCartItemRepository;
    @Autowired private PromoCodeRepository promoCodeRepository;
    @Autowired private OrderService orderService;

    private PromoCode activePromo;

    /**
     * Отображает страницу корзины с товарами пользователя.
     * Рассчитывает общую стоимость, скидки и итоговую цену с учетом промокода.
     *
     * @param model Модель для передачи данных в представление
     * @param principal Аутентифицированный пользователь
     * @return Страница корзины или перенаправление на страницу входа, если пользователь не аутентифицирован
     */
    @GetMapping("")
    public String cartPage(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<UserCartItem> cartItems = userCartItemRepository.findByUserOrderByBookAsc(user);

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

    /**
     * Добавляет книгу в корзину пользователя.
     * Увеличивает количество на 1, если книга уже есть в корзине.
     *
     * @param bookId ID книги для добавления
     * @param principal Аутентифицированный пользователь
     * @return ResponseEntity с HTTP статусом (200 OK при успехе, 401 UNAUTHORIZED если пользователь не аутентифицирован)
     */
    @PostMapping("/cart-add/{bookId}")
    @ResponseBody
    public ResponseEntity<?> addToCart(@PathVariable Long bookId, Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Book book = bookRepository.findById(bookId).orElseThrow();

        UserCartItem item = userCartItemRepository.findByUserAndBookOrderByBookAsc(user, book)
                .orElse(new UserCartItem(user, book, 0));

        if (item.getQuantity() < book.getAvailableAmount()) {
            item.setQuantity(item.getQuantity() + 1);
            userCartItemRepository.save(item);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Увеличивает количество книги в корзине на 1.
     * Фактически вызывает метод addToCart().
     *
     * @param bookId ID книги
     * @param principal Аутентифицированный пользователь
     * @return ResponseEntity с HTTP статусом
     */
    @PostMapping("/cart-increase/{bookId}")
    @ResponseBody
    public ResponseEntity<?> increaseCart(@PathVariable Long bookId, Principal principal) {
        return addToCart(bookId, principal);
    }

    /**
     * Уменьшает количество книги в корзине на 1.
     * Удаляет книгу из корзины, если количество становится 0.
     *
     * @param bookId ID книги
     * @param principal Аутентифицированный пользователь
     * @return ResponseEntity с HTTP статусом (200 OK при успехе, 401 UNAUTHORIZED если пользователь не аутентифицирован)
     */
    @PostMapping("/cart-decrease/{bookId}")
    @ResponseBody
    public ResponseEntity<?> decreaseCart(@PathVariable Long bookId, Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Book book = bookRepository.findById(bookId).orElseThrow();

        userCartItemRepository.findByUserAndBookOrderByBookAsc(user, book).ifPresent(item -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                userCartItemRepository.save(item);
            } else {
                userCartItemRepository.delete(item);
            }
        });

        return ResponseEntity.ok().build();
    }

    /**
     * Полностью удаляет книгу из корзины пользователя.
     *
     * @param id ID книги для удаления
     * @param principal Аутентифицированный пользователь
     * @return Перенаправление на страницу корзины или на страницу входа, если пользователь не аутентифицирован
     */
    @Transactional
    @PostMapping("/cart-remove/{id}")
    public String removeFromCart(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Book book = bookRepository.findById(id).orElseThrow();

        userCartItemRepository.deleteByUserAndBook(user, book);

        return "redirect:/cart";
    }

    /**
     * Очищает всю корзину пользователя.
     *
     * @param principal Аутентифицированный пользователь
     * @return Перенаправление на страницу корзины или на страницу входа, если пользователь не аутентифицирован
     */
    @Transactional
    @PostMapping("/cart-clear")
    public String clearCart(Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        userCartItemRepository.deleteAllByUser(user);
        return "redirect:/cart";
    }

    /**
     * Применяет промокод к корзине пользователя.
     * Проверяет валидность и срок действия промокода.
     *
     * @param promoCode Введенный промокод
     * @param model Модель для передачи статуса промокода
     * @param principal Аутентифицированный пользователь
     * @return Обновленная страница корзины с информацией о применении промокода
     */
    @PostMapping("/apply-promocode")
    public String applyPromocode(@RequestParam String promoCode, Model model, Principal principal) {
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

    /**
     * Оформляет заказ из товаров в корзине.
     * Создает заказ, очищает корзину и сбрасывает активный промокод.
     *
     * @param principal Аутентифицированный пользователь
     * @return Перенаправление на страницу заказов пользователя
     */
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
