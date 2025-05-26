package com.example.Bookstore.Controllers;

import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StatisticController {
    @Autowired private OrderRepository orderRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PromoCodeRepository promoCodeRepository;

    @GetMapping("/managing/statistics")
    public String statisticsPage(@RequestParam(required = false, defaultValue = "all") String period, Model model) {
        // Определяем период для фильтрации
        LocalDateTime startDate = getStartDateForPeriod(period);

        // Получаем все заказы за выбранный период
        List<Order> orders = startDate == null
                ? orderRepository.findAll()
                : orderRepository.findByOrderDateAfter(startDate);

        // Основные показатели
        model.addAttribute("totalBooksSold", calculateTotalBooksSold(orders));
        model.addAttribute("totalRevenue", calculateTotalRevenue(orders));
        model.addAttribute("activeCustomers", calculateActiveCustomers(orders));

        // Статистика по книгам
        model.addAttribute("topBooks", getTopBooksStats(orders));
        model.addAttribute("genresStats", getGenresStats(orders));
        model.addAttribute("authorsStats", getAuthorsStats(orders));

        // Динамика продаж
        model.addAttribute("salesTrend", getSalesTrend(orders, period));

        // Использование промокодов
        model.addAttribute("promoCodesUsage", getPromoCodesUsage(orders));

        return "Managing/statistics";
    }

    private LocalDateTime getStartDateForPeriod(String period) {
        return switch (period) {
            case "year" -> LocalDateTime.now().minusYears(1);
            case "month" -> LocalDateTime.now().minusMonths(1);
            case "week" -> LocalDateTime.now().minusWeeks(1);
            default -> null; // all time
        };
    }

    private int calculateTotalBooksSold(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .mapToInt(OrderItems::getQuantity)
                .sum();
    }

    private double calculateTotalRevenue(List<Order> orders) {
        return orders.stream()
                .mapToDouble(Order::getFinalPrice)
                .sum();
    }

    private long calculateActiveCustomers(List<Order> orders) {
        return orders.stream()
                .map(Order::getUser)
                .filter(user -> "Активен".equals(user.getStatus()))
                .map(User::getId)
                .distinct()
                .count();
    }

    private Map<String, Integer> getTopBooksStats(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getBook().getTitle(),
                        Collectors.summingInt(OrderItems::getQuantity)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private Map<String, Integer> getGenresStats(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .flatMap(item -> item.getBook().getGenres().stream())
                .collect(Collectors.groupingBy(
                        Genre::getName,
                        Collectors.summingInt(g -> 1) // можно улучшить для учета количества
                ));
    }

    private Map<String, Integer> getAuthorsStats(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .flatMap(item -> item.getBook().getAuthors().stream())
                .collect(Collectors.groupingBy(
                        Author::getName,
                        Collectors.summingInt(a -> 1) // можно улучшить для учета количества
                ));
    }

    private Map<String, Integer> getSalesTrend(List<Order> orders, String period) {
        // Группировка по дням/неделям/месяцам в зависимости от периода
        // Упрощенная реализация - можно доработать
        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOrderDate().toLocalDate().toString(),
                        Collectors.summingInt(o -> 1)
                ));
    }

    private Map<String, Integer> getPromoCodesUsage(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getPromoCode() != null)
                .collect(Collectors.groupingBy(
                        o -> o.getPromoCode().getCode(),
                        Collectors.summingInt(o -> 1)
                ));
    }

    @GetMapping("/statistics/data")
    @ResponseBody
    public Map<String, Object> getStatsData(@RequestParam String period) {
        Map<String, Object> response = new HashMap<>();

        LocalDateTime startDate = getStartDateForPeriod(period);
        List<Order> orders = startDate == null
                ? orderRepository.findAll()
                : orderRepository.findByOrderDateAfter(startDate);

        response.put("totalBooksSold", calculateTotalBooksSold(orders));
        response.put("totalRevenue", calculateTotalRevenue(orders));
        response.put("activeCustomers", calculateActiveCustomers(orders));
        response.put("topBooks", getTopBooksStats(orders));
        response.put("genresStats", getGenresStats(orders));
        response.put("authorsStats", getAuthorsStats(orders));
        response.put("salesTrend", getSalesTrend(orders, period));
        response.put("promoCodesUsage", getPromoCodesUsage(orders));

        return response;
    }
}
