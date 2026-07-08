Here's a detailed assignment description for the "Simple Library Management System" task, including instructions, requirements, endpoints, data models, and evaluation criteria.

---

**Assignment: Simple Library Management System**

**Objective:**
Design and implement a simple Library Management System using Spring Boot. This application should allow users to manage books and their copies.

**Instructions:**

1. Create a new Spring Boot application using your preferred IDE.
1. Implement the required features and endpoints as described below.
1. Ensure your application is well-structured, modular, and follows best practices.
1. Document your API using Swagger or any other API documentation tool.
1. Provide clear instructions on how to run your application.
1. Push your code to a public GitHub repository and share the link with us.

**Requirements:**

1. Use Spring Boot (latest stable version) and Spring Data JPA for database interactions.
1. Implement basic validation for the input data.
1. Implement the required RESTful API endpoints.
1. Handle exceptions and provide meaningful error messages.
1. Ensure your application is testable and includes unit tests for the services and controllers.

**Data Models:**

1. **Book**
    * ID (Long, primary key, auto-generated)
    * Title (String, not null, unique)
    * Author (String, not null)
    * ISBN (String, not null, unique, follow standard ISBN format)
    * Published Year (Integer, not null, should be a valid year)
2. **BookCopy**
    * ID (Long, primary key, auto-generated)
    * Book ID (Long, foreign key referencing Book ID, not null)
    * Available (Boolean, not null, default value: true)

**API Endpoints:**

1. **Get all books**
    * Endpoint: `GET /api/books`
    * Response: List of books with their IDs, titles, authors, ISBNs, and published years.
    * Example response:
```json
[
  {
    "id": 1,
    "title": "Effective Java",
    "author": "Joshua Bloch",
    "isbn": "978-0134685991",
    "publishedYear": 2018
  },
  ...
]
```
2. **Add a new book**
    * Endpoint: `POST /api/books`
    * Request body:
```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "publishedYear": 2008
}
```
	* Response: The created book object with its ID.
3. **Get a book by ID**
    * Endpoint: `GET /api/books/{id}`
    * Response: The book object with the given ID, including its copies.
    * Example response:
```json
{
  "id": 1,
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "isbn": "978-0134685991",
  "publishedYear": 2018,
  "copies": [
    {
      "id": 1,
      "available": true
    },
    {
      "id": 2,
      "available": false
    }
  ]
}
```
4. **Update a book by ID**
    * Endpoint: `PUT /api/books/{id}`
    * Request body (only the fields to be updated should be included):
```json
{
  "title": "Effective Java (3rd Edition)",
  "publishedYear": 2018
}
```
	* Response: The updated book object.
5. **Delete a book by ID**
    * Endpoint: `DELETE /api/books/{id}`
    * Response: No content (status code 204).
6. **Get available copies for a book**
    * Endpoint: `GET /api/books/{id}/copies`
    * Response: List of book copies with their IDs and availability status for the given book ID.
    * Example response:
```json
[
  {
    "id": 1,
    "available": true
  },
  {
    "id": 2,
    "available": false
  }
]
```
7. **Add a copy for a book**
    * Endpoint: `POST /api/books/{id}/copies`
    * Response: The created book copy object with its ID.
8. **Update the availability status of a book copy**
    * Endpoint: `PUT /api/books/{id}/copies/{copyId}`
    * Request body:
```json
{
  "available": false
}
```
	* Response: The updated book copy object.

**Bonus:**

1. Implement pagination and sorting for the `GET /api/books` endpoint.
1. Add a simple frontend using Thymeleaf or any other template engine to display books and their copies.

**Evaluation Criteria:**

1. Code quality and organization.
1. Adherence to the requirements and API specifications.
1. Proper use of Spring Boot features and best practices.
1. Exception handling and error messages.
1. API documentation.
1. Unit tests and test coverage.
1. Bonus features implementation (if any).

**Submission:**

1. Push your code to a public GitHub repository.
1. Provide clear instructions on how to run your application in the repository's README file.
1. Share the GitHub repository link with us.

Good luck with your assignment! We look forward to reviewing your submission.