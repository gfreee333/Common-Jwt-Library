---
# Common-jwt-library
---
## 1. Описание библиотеки

`Common-jwt-library` - это библиотека для валидации JWT-токенов, использующая асимметричное
шифрование RS256. Библиотека предоставляет унифицированный способ проверки токенов во всех микросервисах банковской
системы.

---
## 2. Технические компоненты

| Компоненты      |
|-----------------|
| Java 17         |
| JJWT 0.12.6     |
| Spring WEB Flux |

---
## 3. Основные возможности

| Возможности                                        | Статус реализации |
|----------------------------------------------------|-------------------|
| Валидация JWT                                      | ✅                 |
| Загрузка публичных ключей из classpath             | ✅                 |
| Загрузка публичных ключей из PEM-строки            | ✅                 |
| Извлечение userId из токена                        | ✅                 |
| Извлечение role из токена                          | ✅                 |
| Извлечение email из токена                         | ✅                 |
| Извлечение sessionId из токена                     | ✅                 |
| Извлечение статуса пользователя из токена          | ✅                 |
| Проверка срока жизни токена                        | ✅                 |
| Возможность использовать типизированные исключения | ✅                 |

---
## 4. Установка и конфигурация

### 4.1 Зависимость для pom
```
<dependency>
    <groupId>ru.bank</groupId>
    <artifactId>common-jwt-library</artifactId>
    <version>1.0.0</version>
</dependency>
```
### 4.2 Настройки в сервисе

```java
import ru.bank.jwt.JwtValidationFactory;
import ru.bank.jwt.JwtValidator;

@Configuration
public class JwtConfig {
    @Value("${jwt.public-key-path}")
    private String publicKeyPath;

    @Bean
    public JwtValidator jwtValidator() {
        return JwtValidationFactory.fromClasspath(publicKeyPath);
    }
}
```
### 4.3 Формат ключа 
```java
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9...
-----END PUBLIC KEY-----
```
### 4.4 Пример использования
```java
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtValidator jwtValidator;

    public void validateToken(String token) {
        // Проверка срока действия и подписи
        Claims claims = jwtValidator.validateToken(token);
        // Извлечение данных
        UUID userId = jwtValidator.getUserId(token);
        String role = jwtValidator.getRoleFromToken(token);
        String email = jwtValidator.getEmailFromToken(token);
        String sessionId = jwtValidator.getSessionIdFromToken(token);
        Long ttl = jwtValidator.getExpirationFromToken(token);
        System.out.println("userId: " + userId);
        System.out.println("role: " + role);
        System.out.println("email: " + email);
    }
}
```
### 4.5 Пример обработки исключений
```java 
try {
    jwtValidator.validateToken(token);
} catch (TokenExpirationException ex) {
    // Токен истек
    log.warn("Токен истек: {}", ex.getMessage());
} catch (InvalidSignatureException ex) {
    // Неверная подпись
    log.warn("Неверная подпись токена: {}", ex.getMessage());
} catch (JwtValidationException ex) {
    // Другая ошибка валидации
    log.warn("Ошибка валидации токена: {}", ex.getMessage());
}
```
### 4.6 Примеры использования в фильтре Api-Gateway 
```java 
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {
    private final JwtValidator jwtValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = extractToken(exchange);
        if (token != null) {
            try {
                UUID userId = jwtValidator.getUserId(token);
                String role = jwtValidator.getRoleFromToken(token);
                // Добавляем заголовки
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId.toString())
                    .header("X-User-Role", role)
                    .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (JwtValidationException ex) {
                return unauthorized(exchange);
            }
        }
        return chain.filter(exchange);
    }
}
```
---
## 5. Контактные данные

* Создатель: Чухманов Иван Викторович
* Telegram: @chuhmanov
* Email: chuhmanovivan2002@yandex.ru / chuhmanovivan2002@gmail.com
* Телефон: +7 902 369 3230

---