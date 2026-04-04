# Finance Data Processing and Access Control Backend Assignment Description

## Objective
To evaluate backend development skills through a practical assignment focused on:
- API design  
- Data modeling  
- Business logic  
- Access control  

This assignment assesses how you:
- Structure backend architecture  
- Design data flow  
- Organize application logic  
- Build maintainable and reliable systems  

> If you already have a similar project, you may submit it with proper explanation and links.

---

## Key Instructions

- No fixed project structure — design it your way  
- Focus on **correctness, clarity, and maintainability**  
- Make reasonable assumptions and document them  
- Prefer **simple, clean solutions** over complex ones  

---

## Flexibility

You are free to choose:

- Any backend language, framework, or library  
- Any database (or even in-memory storage)  
- Your own schema and architecture  
- REST, GraphQL, or any backend interface  
- Mock authentication if needed  

---

## Scenario

Build a backend for a **finance dashboard system** where:

- Different users interact with financial data  
- Access is controlled based on roles  
- The system provides both raw data and aggregated insights  

---

## Core Requirements

### 1. User and Role Management

Support:
- User creation and management  
- Role assignment  
- User status (active/inactive)  
- Role-based access restrictions  

#### Suggested Roles
- **Viewer** → View-only access  
- **Analyst** → View records + insights  
- **Admin** → Full access (CRUD + user management)  

> You may define your own role model.

---

### 2. Financial Records Management

Each record may include:
- Amount  
- Type (income / expense)  
- Category  
- Date  
- Notes / description  

#### Required Operations
- Create records  
- View records  
- Update records  
- Delete records  
- Filter records (date, category, type, etc.)  

---

### 3. Dashboard Summary APIs

Provide aggregated data such as:
- Total income  
- Total expenses  
- Net balance  
- Category-wise totals  
- Recent activity  
- Monthly / weekly trends  

> Focus on **aggregation logic**, not just CRUD.

---

### 4. Access Control Logic

Enforce role-based permissions:

- **Viewer** → Cannot modify data  
- **Analyst** → Read + analytics  
- **Admin** → Full control  

#### Implementation Options
- Middleware  
- Guards  
- Decorators  
- Policy checks  

---

### 5. Validation and Error Handling

Ensure proper backend behavior:

- Input validation  
- Meaningful error responses  
- Correct HTTP status codes  
- Protection against invalid operations  

---

### 6. Data Persistence

Choose any persistence strategy:

- Relational DB (PostgreSQL, MySQL, etc.)  
- NoSQL DB  
- SQLite  
- In-memory (if documented clearly)  

---

## Optional Enhancements

(Not required, but valuable)

- Authentication (JWT / sessions)  
- Pagination  
- Search functionality  
- Soft deletes  
- Rate limiting  
- Unit / integration tests  
- API documentation  

---

## Evaluation Criteria

### 1. Backend Design
- Structure of routes, services, models  
- Separation of concerns  

### 2. Logical Thinking
- Business rules implementation  
- Access control clarity  

### 3. Functionality
- Correctness and consistency of APIs  

### 4. Code Quality
- Readability  
- Maintainability  
- Naming conventions  

### 5. Data Modeling
- Efficient and appropriate schema design  

### 6. Validation & Reliability
- Handling invalid input and edge cases  

### 7. Documentation
- README clarity  
- Setup instructions  
- API explanation  
- Assumptions & trade-offs  

### 8. Additional Thoughtfulness
- Any improvements that enhance usability or design  

---

## Important Note

This is **not a production system**.

The goal is to demonstrate:
- Backend engineering thinking  
- Design decisions  
- Implementation clarity  

> A well-structured, simple, and thoughtful solution is more valuable than unnecessary complexity.


# Implementation Plan
## Technologies
- Backend Framework - Ktor
- Dependency Injection Framework - Koin
- Database - PostgreSQL
- ORM Library - Exposed

## Architecture
### Modular Monolith
The application needs to be structured as a modular monolith with the following modules:
- iam - Handles User management, Roles and Permissions and Authentication.
- finance - Handles Records as described in the assignment.

A shared kernel gradle subproject contains cross-cutting concerns for shared code like permission-gating guards for endpoints, etc.

### Clean Architecture
The application follow a general clean architecture like format. The applation is split into the following layers:
- presentation - Contains Rest API concerns. Defines endpoints, handlers for the endpoints, network request vaildation, etc. It  should be a thin layer and delegate to Commands and Queries and not perform non-network and Rest API layer related operations by itself.
- application - Contains Commands and Queries and other code components that orchestrates the code that performs the backend functionality.
- domain - Contains entities and interfaces to their repositories.
- infrastructure - Provides implementation of interfaces from higher layers to allow switching infrastructure components more flexibly.

### Module Details
Since this is a Modular Monolith application, each module is part of a whole server and need to be connected to the application, unlike Microservices. Every module exposes a configuration from the cross-cutting module sharedkernel that would allow it to be connected to the server as a whole.

#### IAM 
Admins adds users with a password that users can change later.

Stateless authentication with JWT access tokens issued at login. Access tokens have the id of the user as the subject and permissions that the user is granted embedded into it, among other standard properties.

#### Finance
Provides endpoints for performing actions on Records like adding, removing, modifying, etc.

Also provides aggregated data as mentioned in the documentation.

## Testing
Domain Entities and Use Cases need to be thoroughly tested with Unit Tests.

Since Controllers and Repositories are hard to test with Unit Tests, I will inform when they need to have tests included on an individual basis.

## Rest API Errors
Errors returned by the API should follow the Problem JSON specification, so that consumers of the API have a standardized way of recognizing and handling errors.
Primitives for allowing the API to efficiently 