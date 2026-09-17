# Project Statement

## 1. Problem Statement

Libraries need to manage books, members, lending transactions, reservations, returns, fines, and user access efficiently. Manual or fragmented management makes it difficult to track availability, overdue books, fines, and transaction history. This project addresses this problem through a desktop-based Library Management System.

## 2. Objectives

1. Provide role-based authentication for administrators, librarians, and clerks
2. Enable complete book lifecycle management (add, edit, search, delete)
3. Support member registration and management with auto-generated member numbers
4. Implement book issue and return workflows with validation
5. Track overdue loans and calculate fines automatically
6. Manage book reservations with auto-expiry
7. Maintain complete transaction history
8. Provide real-time library statistics dashboard

## 3. Scope

### Included
- User authentication and role management (Admin, Librarian, Clerk)
- Book management (CRUD operations, search by multiple criteria)
- Member management (registration, editing, deactivation)
- Book issue and return with validation (availability, loan limits, unpaid fines)
- Overdue tracking and automatic fine calculation
- Reservation management with expiry
- Transaction history and fine management
- Statistics dashboard with real-time metrics

### Not Included
- Barcode scanning integration
- Email/SMS notifications
- Book cover image management
- Multi-library/branch support
- Web-based interface
- Advanced reporting exports (PDF, Excel)

## 4. Target Users

- **Administrators** - Full system access, user management, system configuration
- **Librarians** - Book/member management, issue/return operations, reservations
- **Clerks** - Basic issue/return operations, member lookup, fine collection

## 5. High-Level Features

- Role-based authentication with SHA-256 password hashing
- Book CRUD operations with search (title, author, ISBN, publisher, category)
- Member management with auto-generated member numbers
- Book issue/return workflows with business rule validation
- Overdue tracking with automatic fine calculation
- Reservation system with auto-expiry
- Complete transaction history and fine tracking
- Real-time statistics dashboard