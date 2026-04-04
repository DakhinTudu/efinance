# Finance Data Processing and Access Control Backend

## Backend Developer Intern - Assignment

---

## Objective

To evaluate your backend development skills through a practical assignment centered around:

- API design  
- Data modeling  
- Business logic  
- Access control  

This assignment assesses how you:

- Structure backend architecture  
- Design data flow  
- Build clean, maintainable systems  
- Implement logical and reliable backend solutions  

> **Note:**  
If you have already built a similar backend project earlier, you may submit that project.  
Make sure to:
- Clearly explain how it matches this assignment  
- Share the repository link  
- Provide deployed API or documentation (if available)

---

## Key Instructions

- No fixed project structure — design it your way  
- Focus on:
  - Correctness  
  - Clarity  
  - Maintainability  
- Make reasonable assumptions if needed and document them  
- Prefer clean and well-designed solutions over large complex ones  

---

## Flexibility

You are free to:

- Use any backend language or framework  
- Choose any database (or even in-memory storage)  
- Define your own schema and architecture  
- Build REST or GraphQL APIs  
- Use mock authentication if needed  

---

## Scenario

You are building a **finance dashboard backend** where different users interact with financial records based on their roles.

The system should:

- Store and manage financial records  
- Handle user roles and permissions  
- Provide summary analytics  
- Serve clean APIs to a frontend dashboard  

---

## Core Requirements

### 1. User and Role Management

Support:

- Creating and managing users  
- Assigning roles  
- Managing user status (active/inactive)  
- Restricting actions based on roles  

Example roles:

- **Viewer** → Read-only access  
- **Analyst** → Read records + insights  
- **Admin** → Full access (CRUD + user management)  

---

### 2. Financial Records Management

Each record may include:

- Amount  
- Type (income / expense)  
- Category  
- Date  
- Notes / description  

Supported operations:

- Create records  
- View records  
- Update records  
- Delete records  
- Filter records:
  - By date  
  - By category  
  - By type  

---

### 3. Dashboard Summary APIs

Provide aggregated data such as:

- Total income  
- Total expenses  
- Net balance  
- Category-wise totals  
- Recent activity  
- Monthly/weekly trends  

> Focus on backend logic for aggregation, not just CRUD.

---

### 4. Access Control Logic

Enforce role-based permissions:

- Viewer → Cannot modify data  
- Analyst → Read + analytics  
- Admin → Full control  

Implementation options:

- Middleware  
- Guards  
- Decorators  
- Policy classes  

---

### 5. Validation and Error Handling

Your backend should include:

- Input validation  
- Proper error responses  
- Correct HTTP status codes  
- Handling invalid operations safely  

---

### 6. Data Persistence

Choose one:

- Relational DB (PostgreSQL, MySQL, etc.)  
- Document DB (MongoDB)  
- SQLite  
- In-memory storage (if simplified)  

> Clearly document your choice.

---

## Optional Enhancements

You may include:

- Authentication (JWT / sessions)  
- Pagination  
- Search functionality  
- Soft delete  
- Rate limiting  
- Unit / integration tests  
- API documentation (Swagger, Postman, etc.)  

---

## Evaluation Criteria

### 1. Backend Design
- Structure of routes, services, models  
- Separation of concerns  

### 2. Logical Thinking
- Business rules  
- Access control implementation  

### 3. Functionality
- APIs work correctly and consistently  

### 4. Code Quality
- Readability  
- Naming conventions  
- Maintainability  

### 5. Database Design
- Proper schema and relationships  

### 6. Validation & Reliability
- Handles bad input and edge cases  

### 7. Documentation
- Clear README  
- Setup steps  
- API explanation  
- Assumptions & tradeoffs  

### 8. Additional Thoughtfulness
- Extra features  
- Developer experience improvements  

---

## Important Note

This is **not a production system**.

Focus on:

- Clean architecture  
- Clear logic  
- Thoughtful design decisions  

A well-structured and reasoned solution is more valuable than unnecessary complexity.

---

## Goal

Demonstrate:

- Backend engineering thinking  
- Practical implementation skills  
- Ability to design scalable and maintainable systems  

---