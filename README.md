# GeekShop API

REST API for a geek e-commerce store. Built with Spring Boot and PostgreSQL.

---

## Tech stack

- **Java 17** + **Spring Boot 3.4.4**
- **Spring Security** + **JWT** for authentication
- **Spring Data JPA** + **Hibernate** for ORM
- **PostgreSQL** as database
- **Docker** for local database setup
- **Springdoc OpenAPI** for interactive documentation
- **JUnit 5** + **Mockito** for unit testing (35 tests)
- **Lombok** for boilerplate reduction

---

## Architecture

**Package structure:**
- `config/` - Security, CORS, Swagger configuration
- `controller/` - REST endpoints
- `dto/request/` - Incoming request bodies
- `dto/response/` - Outgoing response bodies
- `exception/` - Global exception handler
- `model/` - JPA entities
- `repository/` - Spring Data repositories
- `security/` - JWT filter and service
- `service/` - Business logic

**Layered architecture:**

```
Controller → Service → Repository → PostgreSQL
```

---

## Domain model

| Entity | Description |
|---|---|
| `User` | Customers and admins |
| `Category` | Flexible product categories with optional variant support |
| `Product` | Items for sale with stock management |
| `ProductVariant` | Size and color variants for clothing products |
| `ProductImage` | Multiple images per product (Cloudinary URLs) |
| `Order` | Purchase orders with status lifecycle |
| `OrderItem` | Line items within an order (includes variant details) |
| `Payment` | Payment records linked to MercadoPago |

### Category-based product organization

Categories are fully dynamic and managed from the admin panel. Each category has a `hasVariants` flag that determines whether its products support size/color variants:

- **Categories with variants** (`hasVariants: true`) — Clothing items like t-shirts, hoodies, jackets
- **Categories without variants** (`hasVariants: false`) — Simple products like 3D prints, accessories, posters

**Example structure:**
Category: "Remeras Anime" (hasVariants: true)
└─ Product: "Remera Naruto"
└─ Variants: S-Negro, M-Negro, L-Negro, M-Blanco, L-Blanco
Category: "Impresiones 3D" (hasVariants: false)
└─ Product: "Figura Goku SSJ"
└─ No variants (single SKU)

This architecture allows adding new product types (hoodies, jackets, etc.) without code changes — just create a new category from the admin panel.

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

The database runs automatically via Docker with the default credentials. Only update these if you have your own Cloudinary or MercadoPago accounts:

```properties
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

Interactive Swagger UI available at:
http://localhost:8080/swagger-ui/index.html

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
| GET | `/api/products` | Public | List products (paginated) — params: `page`, `size`, `sortBy`, `search` |
| GET | `/api/products/:id` | Public | Product detail with variants |
| GET | `/api/products/category/:id` | Public | Products by category ID |
| GET | `/api/products/category/slug/:slug` | Public | Products by category slug |
| POST | `/api/products` | Admin | Create product (with optional variants) |
| PUT | `/api/products/:id` | Admin | Update product |
| DELETE | `/api/products/:id` | Admin | Soft delete product |

### Categories
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/categories` | Public | List categories |
| GET | `/api/categories/:id` | Public | Category detail |
| POST | `/api/categories` | Admin | Create category (with `hasVariants` flag) |
| PUT | `/api/categories/:id` | Admin | Update category |

### Product Variants
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/variants/product/:id` | Public | List variants for a product |
| POST | `/api/variants` | Admin | Add variant to product |
| PUT | `/api/variants/:id` | Admin | Update variant |
| DELETE | `/api/variants/:id` | Admin | Delete variant |

### Orders
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/orders` | Auth | Create order (supports variant selection) |
| GET | `/api/orders/my` | Auth | My orders (paginated) |
| GET | `/api/orders/:id` | Auth | Order detail (includes variant info) |
| GET | `/api/orders` | Admin | All orders (paginated) |
| PUT | `/api/orders/:id/status` | Admin | Update order status |

### Payments
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/payments/create` | Auth | Initiate payment |
| POST | `/api/payments/webhook` | Public | MercadoPago webhook |
| GET | `/api/payments/order/:id` | Auth | Payment by order |

### Users (admin)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/users` | Admin | List all users |
| PUT | `/api/users/:id/role` | Admin | Update user role |

---

## Order status lifecycle
PENDING → CONFIRMED → SHIPPED → DELIVERED
↓
CANCELLED

---



## Key technical decisions

**UUID as primary keys** — prevents sequential ID enumeration attacks common in e-commerce.

**Soft delete for products** — products are marked `active = false` instead of being deleted, preserving historical order data integrity.

**Unit price snapshot in OrderItem** — the price at purchase time is stored in `OrderItem.unitPrice`, so historical orders remain accurate even if product prices change later.

**Variant details in OrderItem** — size and color are stored as strings in order items, ensuring order history is preserved even if variants are deleted.

**Stateless authentication** — JWT tokens with no server-side sessions, enabling horizontal scaling.

**DTOs for all responses** — entities are never serialized directly, preventing circular reference issues and over-exposure of internal data.

**Category-driven variant logic** — the `hasVariants` flag on categories determines product behavior, allowing new product types to be added without code changes.

---

## Running tests

```bash
mvn test
```

35 unit tests covering all service layer business logic, including variant management and order integration.

---

## Known technical debt

- JWT has no refresh token mechanism — expires after 24h
- Cloudinary and MercadoPago integrations are service-layer ready but not fully wired to production credentials

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