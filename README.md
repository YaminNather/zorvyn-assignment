# Finance Dashboard Backend - Assignment

This repository contains the backend implementation for a Finance Dashboard system, focusing on role-based access control, financial data processing, and aggregated insights.

## Project Architecture

### Modular Monolith
The application is structured as a **Modular Monolith**, ensuring a clean separation between logical modules while maintaining the simplicity of a single deployment unit. This approach prevents tight coupling and allows for future scalability into microservices if needed.

- **`iam`**: Identity & Access Management. Handles user lifecycle, roles, permissions, and session management.
- **`finance`**: Finance Management. Handles financial records lifecycle (CRUD) and analytic summaries.
- **`sharedkernel`**: Shared core functionality including cross-cutting concerns like custom exceptions, permission guards, and utility primitives used by multiple modules.

### Clean Architecture
Each module follows **Clean Architecture** principles to separate business logic from external frameworks:

- **Domain Layer**: Contains pure business logic, entities (e.g., `User`, `Record`, `Role`), and domain-driven exceptions. It defines repository interfaces to remain infrastructure-agnostic.
- **Application Layer**: Orchestrates use cases via **Commands** (for state changes) and **Queries** (for data retrieval). This separation ensures that models used for creation (e.g., including passwords) remain distinct from read-only query models (which omit sensitive data).
- **Infrastructure Layer**: Implements repository interfaces using **Exposed (R2DBC)** for asynchronous database interactions and provides concrete implementations for external services (e.g., JWT providers, password hashers).
- **Presentation Layer**: Thin controllers built on **Ktor** that handle routing, request parsing, and validation, delegating all logic to the Application layer.

---

## Identity & Access Management (IAM)

The system enforces strict **Role-Based Access Control (RBAC)**. Permissions are assigned to roles, and roles are assigned to users. For performance and statelessness, user permissions are embedded directly into the **JWT Access Token**.

### Roles and Permissions
| Role | Permissions | Description |
| :--- | :--- | :--- |
| **Admin** | `USERS_MANAGE`, `RECORDS_MANAGE`, `RECORDS_VIEW`, `DASHBOARD_VIEW` | Full access to users and financial data. |
| **Analyst** | `RECORDS_VIEW`, `DASHBOARD_VIEW` | Can view all financial details and the summary dashboard. |
| **Viewer** | `DASHBOARD_VIEW` | Limited to viewing aggregated dashboard insights. |

### Authentication
- **Stateless JWT**: Access tokens are issued upon login and validated using a custom `withPermission` guard.
- **Security**: Passwords are securely hashed using **BCrypt** before storage.

---

## Finance Module

The finance module provides robust management of financial records with a focus on data integrity, high query flexibility, and analytical efficiency.

### Flexible Querying and Filtering
Both the **Record List** and **Summary** endpoints support a wide array of dynamic filters, allowing clients to slice financial data precisely. Supported filters include:
- **Date Range**: Filter transactions between `startDate` and `endDate`.
- **Amount Thresholds**: Query records based on `minAmount` and `maxAmount`.
- **Categorization**: Multi-select filtering for specific categories.

### Pagination and Soft Deletion
- **Built-in Pagination**: The record list endpoint supports customizable pagination (`page` and `pageSize`), ensuring optimal performance even with large datasets.
- **Native Soft Deletion**: To preserve data auditability, deleted records are marked with a `deleted_at` timestamp rather than being physically removed. All listing and summary queries natively filter out these records by default.

### Dynamic Dashboard Summaries
The `summary` dashboard endpoint goes beyond basic CRUD by providing real-time calculations based on the same flexible filters as the records list.
- **Optimized SQL Aggregation**: Leveraging **SQL aggregate functions** (`SUM`, `COUNT`) with conditional logic (`CASE WHEN`), the system calculates total income, total expenses, net balance, and transaction counts in a **single database trip**.
- **Requirement-Driven Insights**: This approach allows the summary to adapt dynamically to provided filters (e.g., getting the net balance for a specific category within a specific date range), providing accurate insights for any dashboard view.

---

## Shared Kernel & Cross-Cutting Concerns

- **Standardized Error Handling**: The API follows the **Problem JSON (RFC 7807)** specification. All errors (validation, authentication, domain) are returned in a consistent, machine-readable format. The `type` field uses status-identifying enums (e.g., `validation-failed`, `user-not-found`) for easier client-side handling.
- **Permission Guards**: Custom Ktor route-scoped plugins handle authorization by verifying embedded JWT permissions before a request reaches the controller.
- **Rate Limiting**: To protect against brute-force and abuse, a global rate limit of **50 requests per 60 seconds** is enforced across all endpoints.

---

## Testing
Core business logic is validated via **Unit Tests** for domain entities (e.g., `User`, `Record`, `Role`). These tests ensure that domain invariant rules—such as valid name formats or correct balance calculations—are reliably enforced.

---

## API Documentation
Detailed documentation for all available endpoints, including request/response examples and required headers, can be found in [api-documentation.md](./api-documentation.md).
