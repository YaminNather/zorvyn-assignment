# API Documentation

This document provides documentation for the backend API endpoints of the assignment.

## Base URL
All API requests should be made relative to the base URL of the deployed or local server. When running locally, it is usually `http://localhost:8080`.

## 1. IAM (Identity & Access Management) Module
For all protected endpoints in this module, pass the `Authorization` header with the JWT token: `Bearer <token>`.

### Login
*   **Target URL**: `POST /auth/login`
*   **Description**: Authenticates a user and returns a JWT token.
*   **Authentication**: None.
*   **Request Body** (`application/json`):
    ```json
    {
      "email": "user@example.com",
      "password": "password123"
    }
    ```
*   **Success Response** (200 OK):
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
    }
    ```
*   **Error Responses**:
    *   `400 Bad Request` (Missing credentials)
    *   `401 Unauthorized` (Invalid credentials)


### Setup Initial Admin
*   **Target URL**: `POST /setup/admin`
*   **Description**: Unprotected endpoint to initialize the admin user if the database is empty.
*   **Authentication**: None.
*   **Success Response** (201 Created):
    ```json
    {
      "message": "Admin created",
      "id": "123e4567-e89b-12d3-a456-426614174000"
    }
    ```

### Get Current User Details
*   **Target URL**: `GET /user/me`
*   **Description**: Retrieves the details of the currently authenticated user based on their JWT.
*   **Authentication**: Required (Valid JWT).
*   **Success Response** (200 OK):
    ```json
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "John Doe",
      "email": "user@example.com",
      "role": "ADMIN"
    }
    ```

### Create User
*   **Target URL**: `POST /user`
*   **Description**: Creates a new user. Requires `USERS_MANAGE` permission.
*   **Authentication**: Required (JWT with `USERS_MANAGE` permission).
*   **Request Body** (`application/json`):
    ```json
    {
      "name": "Jane Doe",
      "email": "jane@example.com",
      "password": "password123",
      "roleName": "USER"
    }
    ```
*   **Success Response** (201 Created):
    ```json
    {
      "id": "123e4567-e89b-12d3-a456-426614174001"
    }
    ```
*   **Error Responses**: `400 Bad Request` (Validation errors).

### List Users
*   **Target URL**: `GET /user`
*   **Description**: Lists all registered users. Requires `USERS_MANAGE` permission.
*   **Authentication**: Required.
*   **Success Response** (200 OK):
    ```json
    [
      {
        "id": "123e4567-e89b-12d3-a456-426614174000",
        "name": "John Doe",
        "email": "user@example.com",
        "role": "ADMIN"
      }
    ]
    ```

### Change User Name
*   **Target URL**: `PATCH /user/{id}/name`
*   **Description**: Changes a user's name.
*   **Authentication**: Required (JWT with `USERS_MANAGE` permission).
*   **Request Body** (`application/json`):
    ```json
    {
      "name": "New Name"
    }
    ```
*   **Success Response** (204 No Content)

### Change User Role
*   **Target URL**: `PATCH /user/{id}/role`
*   **Description**: Changes a user's assigned role.
*   **Authentication**: Required (JWT with `USERS_MANAGE` permission).
*   **Request Body** (`application/json`):
    ```json
    {
      "roleName": "ADMIN"
    }
    ```
*   **Success Response** (204 No Content)

---

## 2. Finance Module
For all endpoints in this module, pass the `Authorization` header with the JWT token: `Bearer <token>`.

### Create Record
*   **Target URL**: `POST /finance/records`
*   **Description**: Creates a financial record. Positive amounts indicate INCOME, negative amounts indicate EXPENSE (amounts are stored in Indian Paisa). Requires `RECORDS_MANAGE` permission.
*   **Authentication**: Required (JWT with `RECORDS_MANAGE` permission).
*   **Request Body** (`application/json`):
    ```json
    {
      "amount": 50000,
      "category": "Salary",
      "date": "2026-04-05T12:00:00Z",
      "description": "Monthly Salary"
    }
    ```
*   **Success Response** (201 Created):
    ```json
    {
      "id": "record-uuid-here"
    }
    ```

### Update Record
*   **Target URL**: `PATCH /finance/records/{id}`
*   **Description**: Updates an existing financial record.
*   **Authentication**: Required (JWT with `RECORDS_MANAGE` permission).
*   **Request Body** (`application/json`):
    ```json
    {
      "amount": 55000,
      "category": "Bonus",
      "description": "Updated bonus description"
    }
    ```
*   **Success Response** (204 No Content)

### Delete Record
*   **Target URL**: `DELETE /finance/records/{id}`
*   **Description**: Soft-deletes a financial record. 
*   **Authentication**: Required (JWT with `RECORDS_MANAGE` permission).
*   **Success Response** (204 No Content)

### Get Single Record
*   **Target URL**: `GET /finance/records/{id}`
*   **Description**: Retrieves a single financial record. Excludes soft-deleted records.
*   **Authentication**: Required (JWT with `RECORDS_VIEW` permission).
*   **Success Response** (200 OK):
    ```json
    {
      "id": "record-uuid",
      "amount": 50000,
      "category": "Salary",
      "date": "2026-04-05T12:00:00Z",
      "description": "Monthly Salary"
    }
    ```

### List Records
*   **Target URL**: `GET /finance/records`
*   **Description**: Retrieves a paginated list of financial records. Allows filtering by amount, category, and date. Excludes soft-deleted records.
*   **Authentication**: Required (JWT with `RECORDS_VIEW` permission).
*   **Query Parameters**:
    *   `minAmount` (optional): Long
    *   `maxAmount` (optional): Long
    *   `category` (optional, can be passed multiple times): String
    *   `startDate` (optional): ISO-8601 Instant
    *   `endDate` (optional): ISO-8601 Instant
    *   `page` (optional): Int (default 1)
    *   `pageSize` (optional): Int (default 20, max 100)
*   **Success Response** (200 OK):
    ```json
    {
      "items": [
        {
          "id": "record-uuid",
          "amount": 50000,
          "category": "Salary",
          "date": "2026-04-05T12:00:00Z",
          "description": "Monthly Salary"
        }
      ],
      "totalCount": 1,
      "page": 1,
      "pageSize": 20
    }
    ```

### Dashboard Financial Summary
*   **Target URL**: `GET /finance/summary`
*   **Description**: Retrieves aggregated financial summary data (total income, expenses, net balance, and transaction counts), supporting dynamic date and category filtering. Excludes soft-deleted records.
*   **Authentication**: Required (JWT with `DASHBOARD_VIEW` permission).
*   **Query Parameters**:
    *   `minAmount` (optional): Long
    *   `maxAmount` (optional): Long
    *   `category` (optional, can be passed multiple times): String
    *   `startDate` (optional): ISO-8601
    *   `endDate` (optional): ISO-8601
*   **Success Response** (200 OK):
    ```json
    {
      "totalIncome": 100000,
      "totalExpenses": 25000,
      "netBalance": 75000,
      "incomeCount": 2,
      "expenseCount": 1
    }
    ```
