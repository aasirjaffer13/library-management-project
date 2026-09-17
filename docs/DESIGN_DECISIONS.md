# Design Decisions

## 1. Why Java Swing?
Java Swing was selected because the project requirements specify a desktop Java application. Swing provides the required GUI functionality without adding a large external framework dependency. It is part of the standard JDK, ensuring zero additional setup for evaluators.

## 2. Why H2 Database?
H2 was selected as an embedded relational database so that the evaluator can run the application locally without installing a separate database server. It supports standard SQL, has a small footprint, and initializes automatically on first run.

## 3. Why Layered Architecture?
The application is separated into four layers:
- **Model** - Domain entities and enums
- **Repository** - Database operations (CRUD, queries)
- **Service** - Business rules and validation
- **UI** - Swing components and user interaction

This separation ensures database access, business logic, and presentation code are not tightly coupled. Each layer has a single responsibility.

## 4. Why Repository Pattern?
Repository classes centralize all SQL and database operations. This prevents database logic from being distributed across UI classes or service classes. Each entity has a corresponding repository (UserRepository, BookRepository, etc.).

## 5. Why Service Layer?
Business rules belong in the service layer rather than directly inside the UI. Examples:
- Book availability checking before issue
- Maximum loan limit enforcement
- Unpaid fines blocking new loans
- Automatic fine calculation on return
- Reservation expiry logic

## 6. Why SHA-256 with Salt for Passwords?
Passwords are never stored in plain text. SHA-256 with a per-user salt provides reasonable security for an academic desktop application. The salt prevents rainbow table attacks.

## 7. Why Enum-Based Status Fields?
LoanStatus, ReservationStatus, UserRole, and BookCategory use Java enums. This provides type safety, prevents invalid values, and makes the code self-documenting.

## 8. Why Auto-Generated Member Numbers?
Member numbers are generated automatically (format: LM-{timestamp}) to ensure uniqueness without manual coordination and to provide a human-readable identifier.

## 9. Why Embedded JAR for H2?
The H2 driver JAR is committed to `lib/` so the project compiles and runs immediately without requiring Maven, Gradle, or internet access for dependency resolution.

## 10. Why PowerShell + Bash Instructions?
Both Windows and Unix-like shell instructions are provided to ensure the evaluator can run the project regardless of their operating system.