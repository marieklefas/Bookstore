package com.example.Bookstore.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * Конфигурационный класс безопасности для приложения Bookstore.
 * Настраивает аутентификацию, авторизацию и обработку ошибок входа.
 *
 * <p>Основные функции класса:
 * <ul>
 *   <li>Определяет доступ к различным URL в зависимости от ролей пользователей</li>
 *   <li>Настраивает форму входа с кастомной страницей логина и обработчиком ошибок</li>
 *   <li>Предоставляет кодировщик паролей (BCrypt)</li>
 *   <li>Обрабатывает различные сценарии ошибок аутентификации</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Настраивает цепочку фильтров безопасности для HTTP-запросов.
     *
     * @param http объект HttpSecurity для настройки
     * @return сконфигурированная цепочка фильтров безопасности
     * @throws Exception если возникает ошибка при настройке
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/", "/home", "/books/**", "/search/**", "/catalog/**", "/login", "/register", "/location", "/stores", "/deliveryAndPaymentInfo", "/aboutAuthor").permitAll()
                        .requestMatchers("/profile/**", "/cart/**").authenticated()
                        .requestMatchers("/managing/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .failureHandler(authenticationFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                );
        return http.build();
    }

    /**
     * Предоставляет кодировщик паролей (BCrypt) для хеширования паролей.
     *
     * @return экземпляр BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создает обработчик ошибок аутентификации, который:
     * <ul>
     *   <li>Устанавливает сообщение об ошибке в сессию</li>
     *   <li>Перенаправляет на страницу входа с параметром error</li>
     *   <li>Обрабатывает различные типы исключений аутентификации</li>
     * </ul>
     *
     * @return кастомный обработчик ошибок аутентификации
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String errorMessage = "Неверное имя пользователя или пароль";

            if (exception instanceof DisabledException) {
                errorMessage = exception.getMessage();
            } else if (exception instanceof InternalAuthenticationServiceException
                    && exception.getCause() instanceof DisabledException) {
                errorMessage = exception.getCause().getMessage();
            }

            request.getSession().setAttribute("errorMessage", errorMessage);
            response.sendRedirect("/login?error");
        };
    }
}
