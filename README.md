# E-Commerce API

Spring Boot asosida yozilgan e-commerce backend API. Loyihada user, product va order modullari, JWT authentication, role-based access, PostgreSQL, H2 test database, Swagger/OpenAPI va Actuator health/metrics endpointlari bor.

## Texnologiyalar

- Java 21
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL
- Redis
- H2 database for tests
- MapStruct
- Lombok
- Gradle Kotlin DSL
- Springdoc OpenAPI
- Spring Boot Actuator
- RestAssured, JUnit 5

## Ishga Tushirish

Loyiha default holatda `dev` profil bilan ishlaydi:

```yaml
spring:
  profiles:
    active: dev
```

`dev` profil PostgreSQLga ulanadi:

```yaml
url: jdbc:postgresql://localhost:5432/e-commerce
username: postgres
password: <your-password>
```

Shuning uchun appni run qilishdan oldin PostgreSQL va Redis ishlayotgan bo'lishi, `e-commerce` database yaratilgan bo'lishi kerak.

```sql
CREATE DATABASE "e-commerce";
```

Redis refresh tokenlarni saqlash uchun ishlatiladi. Local run qilganda Redis `localhost:6379`da ishlashi kerak.

Docker orqali faqat Redisni ishga tushirish:

```powershell
docker compose up -d redis
```

Redis ishlayotganini tekshirish:

```powershell
docker exec -it redis-cache redis-cli ping
```

Javob `PONG` bo'lsa, Redis tayyor.

Run qilish:

```powershell
.\gradlew.bat bootRun
```

Agar database URL yoki parol boshqa bo'lsa, environment variable orqali berish mumkin:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/e-commerce"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="your-password"
.\gradlew.bat bootRun
```

App default port:

```text
http://localhost:8080
```

## Docker

Docker Compose PostgreSQL, Redis va Spring Boot appni ko'taradi:

```powershell
docker compose up -d --build
```

Kod o'zgargandan keyin Docker ichida yangi endpointlar ko'rinishi uchun avval jar build qilish kerak:

```powershell
.\gradlew.bat bootJar
docker compose up -d --build
```

Redisdagi refresh tokenlarni ko'rish:

```powershell
docker exec -it redis-cache redis-cli KEYS *
docker exec -it redis-cache redis-cli TTL refresh:user:admin
```

## Testlar

Testlar `test` profil bilan H2 in-memory database ishlatadi. Shuning uchun testlar asosiy PostgreSQL bazaga ta'sir qilmaydi.

Test ishga tushirish:

```powershell
.\gradlew.bat test
```

Toza test run:

```powershell
.\gradlew.bat cleanTest test
```

## Authentication

Auth flow access token va refresh token bilan ishlaydi:

- `accessToken` qisqa muddatli token. Protected endpointlarga shu token yuboriladi.
- `refreshToken` uzunroq muddatli token. Access token muddati tugaganda yangi token olish uchun ishlatiladi.
- Refresh token Redisda `refresh:user:{username}` key bilan saqlanadi.
- Logout qilinganda Redisdagi refresh token o'chiriladi.

Login endpoint:

```http
POST /api/auth/login
Content-Type: application/json
```

Body:

```json
{
  "username": "admin",
  "password": "<password>"
}
```

Response:

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "username": "admin",
  "role": "ADMIN"
}
```

Protected endpointlarga request yuborishda token headerga qo'yiladi:

```http
Authorization: Bearer <accessToken>
```

Access token muddati tugasa, refresh token orqali yangi token olinadi:

```http
POST /api/auth/refresh
Content-Type: application/json
```

Body:

```json
{
  "refreshToken": "..."
}
```

Response:

```json
{
  "accessToken": "...",
  "refreshToken": "..."
}
```

Logout:

```http
POST /api/auth/logout
Content-Type: application/json
```

Body:

```json
{
  "refreshToken": "..."
}
```

Logoutdan keyin Redisdagi refresh token o'chadi va u token bilan `/api/auth/refresh` ishlamaydi.

## Default Admin

App start bo'lganda `DbPopulator` orqali admin user yaratiladi:

```text
username: admin
password: application konfiguratsiyasidan yoki lokal sozlamadan olinadi
role: ADMIN
```

Agar admin oldin mavjud bo'lsa qayta yaratilmaydi.

## Role Va Security

Public endpointlar:

- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `POST /api/users/register`
- Swagger endpointlari
- `GET /actuator/health`

Admin endpointlar:

- `POST /api/users/createAdmin`
- `GET /api/users`
- `GET /api/users/{userId}`

Qolgan endpointlar authentication talab qiladi.

Muhim eslatma: `hasRole("ADMIN")` ishlashi uchun authority `ROLE_ADMIN` bo'lishi kerak. Agar JWT filter authorityni faqat `ADMIN` qilib bersa, admin route ishlamaydi. To'g'ri variant:

```java
new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
```

## User Endpointlari

Register:

```http
POST /api/users/register
```

Get all users:

```http
GET /api/users
```

Get by id:

```http
GET /api/users/{userId}
```

Create admin:

```http
POST /api/users/createAdmin
```

Example body:

```json
{
  "fullName": "John Doe",
  "username": "johndoe",
  "password": "johndoe123"
}
```

## Product Endpointlari

Get all products:

```http
GET /api/products?page=0&size=10
```

Get by id:

```http
GET /api/products/{productId}
```

Create:

```http
POST /api/products
```

Update:

```http
PUT /api/products/{productId}
```

Delete:

```http
DELETE /api/products/{productId}
```

Search:

```http
GET /api/products/search?name=apple&category=Fruit
```

Example body:

```json
{
  "name": "Apple",
  "price": 12000,
  "stock": 10,
  "category": "Fruit"
}
```

## Order Endpointlari

Get all:

```http
GET /api/order
```

Get by id:

```http
GET /api/order/{orderId}
```

Create:

```http
POST /api/order
```

Update status:

```http
PUT /api/order/{orderId}/status?status=CONFIRMED
```

Delete:

```http
DELETE /api/order/{orderId}
```

Get by customer email:

```http
GET /api/order/customer/{email}
```

Example create body:

```json
{
  "customerName": "Jasur",
  "customerEmail": "jasur@example.com",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

Order status values:

- `PENDING`
- `CONFIRMED`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

## Swagger

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI docs:

```text
http://localhost:8080/v3/api-docs
```

## Health Va Metrics

Health endpoint public:

```http
GET /actuator/health
```

Example:

```json
{
  "status": "UP"
}
```

Metrics endpoint token talab qiladi:

```http
GET /actuator/metrics
Authorization: Bearer <token>
```

Example metric:

```http
GET /actuator/metrics/jvm.memory.used
Authorization: Bearer <token>
```

`metrics`ni public qilish tavsiya qilinmaydi, chunki u ichki runtime ma'lumotlarini ko'rsatadi.

## Ko'p Uchraydigan Xatolar

### App run bo'lmayapti, PostgreSQL connection refused

Sabab: PostgreSQL ishlamayapti yoki `localhost:5432`da emas.

Tekshirish:

```powershell
.\gradlew.bat bootRun
```

Logda shunaqa xato chiqsa:

```text
Connection to localhost:5432 refused
```

PostgreSQLni ishga tushiring yoki datasource URLni to'g'rilang.

### Login yoki refresh vaqtida Redis connection refused

Sabab: Redis ishlamayapti yoki app noto'g'ri host/portga ulanmoqda.

Local run uchun Redis `localhost:6379`da bo'lishi kerak:

```powershell
docker compose up -d redis
```

Tekshirish:

```powershell
docker exec -it redis-cache redis-cli ping
```

Docker ichida app Redisga service nomi orqali ulanadi:

```yaml
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379
```

### Refresh token Redisga yozilganini qanday tekshiraman?

Login qilingandan keyin:

```powershell
docker exec -it redis-cache redis-cli KEYS *
```

TTL ko'rish:

```powershell
docker exec -it redis-cache redis-cli TTL refresh:user:admin
```

### Testlar asosiy bazaga ta'sir qiladimi?

Yo'q. Testlar `application-test.yml` orqali H2 database ishlatadi:

```yaml
url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
```

### Admin route ishlamayapti

`hasRole("ADMIN")` uchun authority `ROLE_ADMIN` bo'lishi kerak. JWT filterda authority shu formatda berilganini tekshiring.

## Build

Jar build:

```powershell
.\gradlew.bat clean build
```

Testlarsiz build:

```powershell
.\gradlew.bat clean build -x test
```
