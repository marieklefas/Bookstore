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

/**
 * Контроллер для работы со статистикой магазина.
 * Предоставляет данные о продажах, популярных товарах, клиентах и промокодах.
 */
@Controller
public class StatisticController {
    @Autowired private OrderRepository orderRepository;

    /**
     * Отображает страницу статистики с возможностью фильтрации по периодам.
     *
     * @param period Период для фильтрации (all, year, month, week)
     * @param model Модель для передачи данных в представление
     * @return Страница статистики
     */
    @GetMapping("/managing/statistics")
    public String statisticsPage(@RequestParam(required = false, defaultValue = "all") String period, Model model) {
        LocalDateTime startDate = getStartDateForPeriod(period);

        List<Order> orders = startDate == null
                ? orderRepository.findAll()
                : orderRepository.findByOrderDateAfter(startDate);

        model.addAttribute("totalBooksSold", calculateTotalBooksSold(orders));
        model.addAttribute("totalRevenue", calculateTotalRevenue(orders));
        model.addAttribute("activeCustomers", calculateActiveCustomers(orders));

        model.addAttribute("topBooks", getTopBooksStats(orders));
        model.addAttribute("genresStats", getGenresStats(orders));
        model.addAttribute("authorsStats", getAuthorsStats(orders));

        model.addAttribute("salesTrend", getSalesTrend(orders, period));

        model.addAttribute("promoCodesUsage", getPromoCodesUsage(orders));

        return "Managing/statistics";
    }

    /**
     * Возвращает начальную дату для указанного периода.
     *
     * @param period Период (year, month, week, all)
     * @return Локальная дата и времени или null для всего периода
     */
    private LocalDateTime getStartDateForPeriod(String period) {
        return switch (period) {
            case "year" -> LocalDateTime.now().minusYears(1);
            case "month" -> LocalDateTime.now().minusMonths(1);
            case "week" -> LocalDateTime.now().minusWeeks(1);
            default -> null;
        };
    }

    /**
     * Вычисляет общее количество проданных книг.
     *
     * @param orders Список заказов
     * @return Общее количество проданных книг
     */
    private int calculateTotalBooksSold(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .mapToInt(OrderItems::getQuantity)
                .sum();
    }

    /**
     * Вычисляет общий доход от продаж.
     *
     * @param orders Список заказов
     * @return Общий доход
     */
    private double calculateTotalRevenue(List<Order> orders) {
        return orders.stream()
                .mapToDouble(Order::getFinalPrice)
                .sum();
    }

    /**
     * Подсчитывает количество активных покупателей.
     *
     * @param orders Список заказов
     * @return Количество уникальных активных пользователей
     */
    private long calculateActiveCustomers(List<Order> orders) {
        return orders.stream()
                .map(Order::getUser)
                .filter(user -> "Активен".equals(user.getStatus()))
                .map(User::getId)
                .distinct()
                .count();
    }

    /**
     * Формирует статистику по самым популярным книгам.
     *
     * @param orders Список заказов
     * @return Map с названиями книг и количеством продаж (топ-5)
     */
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

    /**
     * Формирует статистику по жанрам.
     *
     * @param orders Список заказов
     * @return Map с названиями жанров и количеством продаж
     */
    private Map<String, Integer> getGenresStats(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .flatMap(item -> item.getBook().getGenres().stream())
                .collect(Collectors.groupingBy(
                        Genre::getName,
                        Collectors.summingInt(g -> 1)
                ));
    }

    /**
     * Формирует статистику по авторам.
     *
     * @param orders Список заказов
     * @return Map с именами авторов и количеством продаж
     */
    private Map<String, Integer> getAuthorsStats(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .flatMap(item -> item.getBook().getAuthors().stream())
                .collect(Collectors.groupingBy(
                        Author::getName,
                        Collectors.summingInt(a -> 1)
                ));
    }

    /**
     * Анализирует динамику продаж за период.
     *
     * @param orders Список заказов
     * @param period Период анализа
     * @return Map с датами и количеством заказов
     */
    private Map<String, Integer> getSalesTrend(List<Order> orders, String period) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOrderDate().toLocalDate().toString(),
                        Collectors.summingInt(o -> 1)
                ));
    }

    /**
     * Формирует статистику использования промокодов.
     *
     * @param orders Список заказов
     * @return Map с кодами промокодов и количеством использований
     */
    private Map<String, Integer> getPromoCodesUsage(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getPromoCode() != null)
                .collect(Collectors.groupingBy(
                        o -> o.getPromoCode().getCode(),
                        Collectors.summingInt(o -> 1)
                ));
    }

    /**
     * Предоставляет статистические данные в формате JSON.
     *
     * @param period Период для фильтрации
     * @return Map с ключами: totalBooksSold, totalRevenue, activeCustomers,
     *         topBooks, genresStats, authorsStats, salesTrend, promoCodesUsage
     */
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
