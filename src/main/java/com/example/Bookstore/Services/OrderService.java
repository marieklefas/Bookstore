package com.example.Bookstore.Services;

import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.BookRepository;
import com.example.Bookstore.Repositories.OrderItemRepository;
import com.example.Bookstore.Repositories.OrderRepository;
import com.example.Bookstore.Repositories.UserCartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserCartItemRepository userCartItemRepository;
    @Autowired private OrderItemRepository orderItemRepository;

    @Scheduled(cron = "0 0 * * * *") // каждый час
    public void updateOrderStatuses() {
        List<Order> activeOrders = orderRepository.findByStatus("Заказ активен");
        LocalDateTime now = LocalDateTime.now();

        for (Order order : activeOrders) {
            if (order.getOrderDate().plusDays(2).isBefore(now)) {
                order.setStatus("Завершен");
                orderRepository.save(order);
            }
        }
    }

    public void createOrderFromCart(User user, PromoCode promoCode) {
        List<UserCartItem> cartItems  = userCartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) return;

        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();

        double discount = 0;
        if (promoCode != null) {
            if (LocalDate.now().isBefore(promoCode.getStartDate()) || LocalDate.now().isAfter(promoCode.getEndDate())) {
                promoCode = null;
            } else if (promoCode.getDiscountType().equalsIgnoreCase("Фиксированное значение")) {
                discount = promoCode.getDiscountValue();
            } else if (promoCode.getDiscountType().equalsIgnoreCase("Процентное значение")) {
                discount = totalPrice * promoCode.getDiscountValue() / 100;
            }
        }

        double finalPrice = Math.max(0, totalPrice - discount);

        int userOrderCount = orderRepository.countByUser(user) + 1;
        String orderId = user.getId() + "-" + userOrderCount + "-" + LocalDate.now();

        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setPromoCode(promoCode);
        order.setTotalPrice(totalPrice);
        order.setDiscount(discount);
        order.setFinalPrice(finalPrice);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Активен");

        List<OrderItems> orderItems = new ArrayList<>();

        for (UserCartItem cartItem : cartItems) {
            OrderItems item = new OrderItems();
            item.setOrder(order);
            item.setBook(cartItem.getBook());
            item.setQuantity(cartItem.getQuantity());
            item.setPricePerUnit(cartItem.getBook().getPrice());
            orderItems.add(item);

            Book book = cartItem.getBook();
            int updatedAmount = book.getAvailableAmount() - cartItem.getQuantity();
            book.setAvailableAmount(Math.max(0, updatedAmount));
            bookRepository.save(book);
        }

        order.setItems(orderItems);
        orderRepository.save(order);
        userCartItemRepository.deleteAllByUser(user);
    }
}
