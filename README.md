# E-Commerce Backend

REST API for an e-commerce application built with Spring Boot.

## Tech Stack
Java 17, Spring Boot, Spring Security, JWT, Spring Data JPA, Hibernate, H2, Maven

## Features
- Register and login with JWT authentication (BCrypt password hashing)
- Role-based access: ADMIN manages products, CUSTOMER shops
- Product CRUD with validation and centralized exception handling
- Cart: add items, view cart, remove items
- Orders: transactional order placement, stock deduction, cancellation with stock restore

## Architecture
Controller -> Service -> Repository, with DTOs between the API and the entities.

## API Endpoints
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | /api/auth/register | Public |
| POST | /api/auth/login | Public |
| GET | /api/products, /api/products/{id} | Public |
| POST/PUT/DELETE | /api/products | ADMIN |
| GET/POST | /api/cart, /api/cart/items | Logged in |
| DELETE | /api/cart/items/{id} | Logged in |
| POST/GET | /api/orders | Logged in |
| PUT | /api/orders/{id}/cancel | Logged in |

## Run Locally
1. Install JDK 17
2. Clone the repo and open it in IntelliJ
3. Run `ShopApplication`
4. API runs at http://localhost:8080