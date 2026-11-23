# LibraryManagementAPI

REST API for a college library: books, students, issuing/returning books, overdue tracking.
Stack: Java 17, Spring Boot 3, Spring Data JPA (Hibernate), MySQL, Maven, Lombok.

## Architecture
Controller -> Service -> Repository -> Database (DTOs for request/response, entities never exposed directly).

## Setup
1. Install Java 17, Maven and MySQL.
2. Set credentials as environment variables (do NOT commit them):
   - `DB_USERNAME` (default `root`)
   - `DB_PASSWORD`
3. Run: `mvn spring-boot:run` (database `library_db` is created automatically).
4. API base URL: `http://localhost:8080`

## Endpoints
| Method | URL | Description |
|---|---|---|
| POST | /api/books | Add book |
| GET | /api/books | All books |
| GET | /api/books/{id} | Book by id |
| PUT | /api/books/{id} | Update book |
| DELETE | /api/books/{id} | Delete book (blocked if currently issued) |
| GET | /api/books/category/{category} | Books by category |
| POST | /api/students | Register student |
| GET | /api/students | All students |
| GET | /api/students/{id} | Student by id |
| PUT | /api/students/{id} | Update student |
| DELETE | /api/students/{id} | Delete student |
| POST | /api/issues/{studentId}/{bookId} | Issue a book (14-day loan) |
| GET | /api/issues | All issues |
| GET | /api/issues/student/{studentId} | Issues of a student |
| PUT | /api/issues/return/{issueId} | Return a book |
| GET | /api/issues/overdue | Overdue books |

## Sample bodies
Book: `{"title":"Clean Code","author":"Robert Martin","isbn":"9780132350884","category":"Programming","totalCopies":3}`

Student: `{"studentName":"Riya Sen","email":"riya@college.edu","department":"CSE","phoneNumber":"9876543210"}`

## Error codes
404 not found | 409 duplicate ISBN/email | 400 no copies / invalid data / business rule violation

bookmanagement
