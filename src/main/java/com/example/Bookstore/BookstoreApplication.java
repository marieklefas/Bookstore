package com.example.Bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Главный класс приложения книжного магазина.
 * <p>
 * Служит точкой входа в Spring Boot приложение и содержит конфигурацию:
 * <ul>
 *   <li>Автоконфигурацию Spring Boot ({@code @SpringBootApplication})</li>
 *   <li>Включение механизма планирования задач ({@code @EnableScheduling})</li>
 * </ul>
 *
 * <p>При запуске инициализирует Spring контекст и все компоненты приложения.
 *
 * @see SpringBootApplication
 * @see EnableScheduling
 *
 * @author Marie
 * @version 1.0
 * @since 2025
 */
@SpringBootApplication
@EnableScheduling
public class BookstoreApplication {

	/**
	 * Точка входа в приложение книжного магазина.
	 *
	 * @param args аргументы командной строки, переданные при запуске приложения
	 */
	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}

}
