# Library Management System

## Overview

A Java Swing desktop application for managing library operations. Built with a clean layered architecture separating models, database access, business logic, and user interface. Uses an embedded H2 database that requires zero configuration.

## Features

1. **Login/Authentication** - Role-based login (Admin, Librarian, Clerk) with SHA-256 password hashing
2. **Dashboard** - Real-time statistics cards showing books, members, loans, fines
3. **Book Management** - Add, edit, delete, search books (by title, author, ISBN, publisher)
4. **Member Management** - Add, edit, deactivate library members with auto-generated member numbers
5. **Issue Book** - Issue books with validation (availability, max limit, unpaid fines check)
6. **Return Book** - Process returns with automatic overdue fine calculation
7. **Reservation** - Reserve unavailable books with auto-expiry
8. **Transaction History** - View all loans and their statuses
9. **Fine Management** - Track and mark fines as paid
10. **Overdue Tracking** - Identify overdue loans and calculate penalties

## Technology Used

- **Java 21+** (tested with JDK 26)
- **Swing** for graphical user interface
- **H2 Database** (embedded, zero-configuration, auto-initializes)
- **Layered Architecture**: Model → Repository → Service → UI
- No external frameworks required

## Project Structure

```
src/
  app/
    LibraryApp.java          - Application entry point
    DatabaseManager.java     - DB connection and schema initialization
  model/
    User.java                - User entity (admin/librarian/clerk)
    Book.java                - Book entity
    Member.java              - Library member entity
    Loan.java                - Loan/issue record entity
    Reservation.java         - Book reservation entity
    Fine.java                - Fine record entity
    UserRole.java            - Enum: ADMIN, LIBRARIAN, CLERK
    BookCategory.java        - Enum: FICTION, SCIENCE, TECHNOLOGY, etc.
    LoanStatus.java          - Enum: ACTIVE, RETURNED, OVERDUE
    ReservationStatus.java   - Enum: PENDING, FULFILLED, CANCELLED, EXPIRED
  repository/
    UserRepository.java      - User database operations
    BookRepository.java      - Book database operations
    MemberRepository.java    - Member database operations
    LoanRepository.java      - Loan database operations
    ReservationRepository.java - Reservation database operations
    FineRepository.java      - Fine database operations
  service/
    AuthService.java         - Login, registration, password management
    BookService.java         - Book business logic and validation
    MemberService.java       - Member business logic
    LoanService.java         - Issue/return/fine logic
    ReservationService.java  - Reservation business logic
    FineService.java         - Fine tracking logic
    StatisticsService.java   - Dashboard statistics aggregation
  ui/
    LoginFrame.java          - Login window
    MainFrame.java           - Main window with tabbed navigation
    BookPanel.java           - Book management tab
    MemberPanel.java         - Member management tab
    LoanPanel.java           - Issue/Return/Overdue tabs
    ReservationPanel.java    - Reservation management tab
    TransactionPanel.java    - Transaction history and fines tabs
    StatisticsPanel.java     - Dashboard statistics tab
    CommonUI.java            - Shared UI utilities and styling
  util/
    PasswordUtil.java        - SHA-256 password hashing with salt
    DateUtils.java           - Date formatting utilities
    Validation.java          - Input validation helpers
  test/
    AppTest.java             - Automated test suite (33 tests)
lib/
  h2-2.2.224.jar            - H2 embedded database driver
```

## Database Design

| Table | Key Columns |
|-------|-------------|
| `users` | id, username (UNIQUE), password_hash, full_name, role, email, phone, address, active |
| `books` | id, isbn (UNIQUE), title, author, category, publisher, edition, total_copies, available_copies, location |
| `members` | id, member_number (UNIQUE), full_name, email, phone, address, membership_date, active |
| `loans` | id, book_id (FK), member_id (FK), issued_by (FK), issue_date, due_date, return_date, status |
| `reservations` | id, book_id (FK), member_id (FK), reservation_date, expiry_date, status |
| `fines` | id, loan_id (FK), member_id (FK), amount, reason, paid |
| `library_config` | config_key, config_value (fine rate, loan period, etc.) |

## Requirements

- JDK 21 or later
- No additional software required (H2 is embedded)

## Installation

1. Install JDK 21+
2. Clone this repository
3. Open in VS Code with Extension Pack for Java

## How to Run in VS Code

1. Open the project folder in VS Code
2. Install "Extension Pack for Java" extension
3. Open `src/app/LibraryApp.java`
4. Click the **Run** button above `main` method, or press **F5**

## How to Compile and Run from Terminal

```powershell
cd "Library-Management-System-JAVA"

# Compile
New-Item -ItemType Directory -Force -Path build | Out-Null
$files = Get-ChildItem -Path src -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
javac -d build -cp "lib/h2-2.2.224.jar" --release 21 $files

# Run
java -cp "build;lib/h2-2.2.224.jar" app.LibraryApp
```

## Default Login

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Administrator |

The admin account is created automatically on first run.

## Usage

1. **Login** with admin credentials
2. **Dashboard** shows library statistics at a glance
3. Use the **left sidebar tabs** to navigate between modules
4. **Books tab** - Add, search, edit, delete books
5. **Members tab** - Register, search, manage members
6. **Issue Book tab** - Select book and member to issue
7. **Return Book tab** - View active loans, process returns
8. **Reservations tab** - Reserve unavailable books
9. **Transactions tab** - View loan history and manage fines

## Running Tests

```powershell
# Compile (includes test)
New-Item -ItemType Directory -Force -Path build | Out-Null
$files = Get-ChildItem -Path src -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
javac -d build -cp "lib/h2-2.2.224.jar" --release 21 $files

# Run automated tests (33 tests)
java -ea -cp "build;lib/h2-2.2.224.jar" test.AppTest
```

## Future Improvements

- Barcode scanning for books
- Email notifications for due dates
- Book cover images
- Advanced reporting and export
- Unit tests with JUnit
