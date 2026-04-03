# GeekShop API

REST API for a geek-themed e-commerce platform. Built with Spring Boot and PostgreSQL.

---

## Tech stack

- **Java 17** + **Spring Boot 3.4.4**
- **Spring Security** + **JWT** for authentication
- **Spring Data JPA** + **Hibernate** for ORM
- **PostgreSQL** as database
- **Docker** for local database setup
- **Springdoc OpenAPI** for interactive documentation
- **JUnit 5** + **Mockito** for unit testing
- **Lombok** for boilerplate reduction

---

## Architecture

```
src/
├── config/          # Security, CORS, Swagger configuration
├── controller/      # REST endpoints
├── dto/
│   ├── request/     # Incoming request bodies
│   └── response/    # Outgoing response bodies
├── exception/       # Global exception handler
├── model/           # JPA entities
├── repository/      # Spring Data repositories
├── security/        # JWT filter and service
└── service/         # Business logic
```

The API follows a layered architecture:

```
Controller → Service → Repository → PostgreSQL
```

---

## Domain model

| Entity | Description |
|---|---|
| `User` | Customers and admins |
| `Category` | Product categories (e.g. shirts, 3D prints) |
| `Product` | Items for sale with stock management |
| `ProductImage` | Multiple images per product (Cloudinary URLs) |
| `Order` | Purchase orders with status lifecycle |
| `OrderItem` | Line items within an order |
| `Payment` | Payment records linked to MercadoPago |

---

## Running locally

### Requirements

- Java 17
- Maven 3.9+
- Docker Desktop

### 1. Clone the repo

```bash
git clone https://github.com/batial/geekshop-api.git
cd geekshop-api
```

### 2. Start the database

```bash
docker-compose up -d
```

This starts a PostgreSQL 16 container on port `5432` with database `geekshop`.

### 3. Configure environment

Edit `src/main/resources/application.properties` with your local values:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/geekshop
spring.datasource.username=postgres
spring.datasource.password=postgres

jwt.secret=your-secret-key-minimum-32-characters
jwt.expiration=86400000

cloudinary.cloud-name=your-cloud-name
cloudinary.api-key=your-api-key
cloudinary.api-secret=your-api-secret
```

### 4. Run the application

```bash
mvn spring-boot:run -DskipTests
```

The API will be available at `http://localhost:8080`.

---

## API documentation

Interactive Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

To test protected endpoints:
1. Call `POST /api/auth/login` to get a JWT token
2. Click **Authorize** in Swagger UI
3. Enter `Bearer <your-token>`

---

## Main endpoints

### Auth
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register new user |
| POST | `/api/auth/login` | Public | Login, returns JWT |
| GET | `/api/auth/me` | Auth | Current user profile |

### Products
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/products` | Public | List products (paginated) |
| GET | `/api/products/:id` | Public | Product detail |
| GET | `/api/products/category/:id` | Public | Products by category |
| POST | `/api/products` | Admin | Create product |
| PUT | `/api/products/:id` | Admin | Update product |
| DELETE | `/api/products/:id` | Admin | Soft delete product |

### Categories
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/categories` | Public | List categories |
| POST | `/api/categories` | Admin | Create category |
| PUT | `/api/categories/:id` | Admin | Update category |

### Orders
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/orders` | Auth | Create order from cart |
| GET | `/api/orders/my` | Auth | My orders |
| GET | `/api/orders/:id` | Auth | Order detail |
| GET | `/api/orders` | Admin | All orders |
| PUT | `/api/orders/:id/status` | Admin | Update order status |

### Payments
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/payments/create` | Auth | Initiate payment |
| POST | `/api/payments/webhook` | Public | MercadoPago webhook |
| GET | `/api/payments/order/:id` | Auth | Payment by order |

---

## Order status lifecycle

```
PENDING → CONFIRMED → SHIPPED → DELIVERED
              ↓
          CANCELLED
```

---

## Key technical decisions

**UUID as primary keys** — prevents sequential ID enumeration attacks common in e-commerce (e.g. `/orders/42` exposing other users' orders).

**Soft delete for products** — products are marked `active = false` instead of being deleted, preserving historical order data integrity.

**Unit price snapshot in OrderItem** — the price at purchase time is stored in `OrderItem.unitPrice`, so historical orders remain accurate even if product prices change later.

**Stateless authentication** — JWT tokens with no server-side sessions, enabling horizontal scaling.

**DTOs for all responses** — entities are never serialized directly, preventing circular reference issues and over-exposure of internal data.

---

## Running tests

```bash
mvn test
```

26 unit tests covering all service layer business logic.

---

## Pending integrations

- **Cloudinary** — image upload for products (service layer ready)
- **MercadoPago** — full payment flow (webhook endpoint ready)
- **Railway deploy** — production environment with environment variables

---

## Frontend

The frontend is built with Next.js + TypeScript and lives in a separate repository:

> [geekshop-frontend](https://github.com/batial/geekshop-frontend)

---

## Author

**Sebastian** — [GitHub](https://github.com/batial)
