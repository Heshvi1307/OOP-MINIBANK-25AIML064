# MiniBank — Java Object-Oriented Banking System

<p align="center">
  A console-based banking application developed as a semester-long Java OOP project.
  <br />
  <strong>Account Management · Persistence · Transaction Processing · Concurrency · Unit Testing</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-OOP-orange?style=flat-square&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Build-Apache%20Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white" alt="Apache Maven" />
  <img src="https://img.shields.io/badge/Testing-JUnit%205-25A162?style=flat-square" alt="JUnit 5" />
  <img src="https://img.shields.io/badge/Platform-Console%20Application-23395B?style=flat-square" alt="Console application" />
</p>

---

## Overview

**MiniBank** is a Java-based banking management application built incrementally while learning and applying Object-Oriented Programming concepts.

The project brings together account and customer management, deposits, withdrawals, fund transfers, input validation, exception handling, file-based persistence, generic repositories, transaction processing, and multithreading demonstrations within one interactive console application.

Beyond implementing individual Java concepts, the project focuses on organizing them into reusable components, testing important behaviours, and packaging the application into a runnable JAR.

## Features

| Feature | Description |
|---|---|
| Interactive console menu | Navigate banking operations and project demonstrations. |
| Account management | Open, list, search, and manage bank accounts. |
| Customer management | Register and manage customer records. |
| Deposits and withdrawals | Update balances with validation and banking rules. |
| Fund transfers | Transfer money between accounts. |
| Account statements | Display account and statement information. |
| Account types | Model specialized account behaviours using inheritance and polymorphism. |
| Input validation | Validate user input and reject invalid operations. |
| Custom exceptions | Handle account-related errors and unsuccessful transactions. |
| Generic repositories | Reuse storage and retrieval operations across entity types. |
| File-based persistence | Load saved account records at startup and preserve changes on exit. |
| Reporting and logging | Generate reports and maintain supporting transaction-log files. |
| Batch transaction processing | Process multiple deposit and withdrawal operations. |
| Concurrent operations | Demonstrate multithreading, synchronization, and concurrent deposits. |
| Deadlock demonstrations | Explore deadlock detection and consistent lock ordering. |
| Collections and generics | Perform collection operations and a generic average-calculation exercise. |
| Automated testing | Test deposits, withdrawal-limit enforcement, and concurrent deposits. |
| Maven packaging | Compile, test, and package the application into a runnable JAR. |

## Technology Stack

- **Java:** Core language and Object-Oriented Programming.
- **JDK:** Java Development Kit, compatible with the project's Maven configuration.
- **Apache Maven:** Build automation, dependency management, testing, and packaging.
- **JUnit 5:** Automated unit testing.
- **Git and GitHub:** Version control and project history.
- **PowerShell:** Development commands and application execution on Windows.

## Application Architecture

MiniBank separates domain models, banking operations, reusable data access, validation, and supporting utilities into distinct components.

| Component | Responsibility |
|---|---|
| `model/` | Represents accounts, customers, specialized account types, and related contracts. |
| `service/` | Coordinates banking operations, application workflows, transaction processing, and persistence. |
| `exception/` | Defines custom exceptions for banking errors and invalid operations. |
| `util/` | Provides validation, command parsing, statement formatting, and supporting utilities. |
| `src/test/` | Contains automated tests for important service behaviours. |
| `data/` | Stores account data, reports, and supporting log files. |

This separation of concerns keeps the console interface distinct from reusable banking logic and supporting components.

## Project Structure

The following tree represents the project's source architecture and important existing files. Generated build directories and compiled classes are omitted.

```text
OOP-MINIBANK-25AIML064/
│
├── exception/
│   ├── AccountNotFoundException.java
│   ├── BankException.java
│   ├── InsufficientFundsException.java
│   └── InvalidAmountException.java
│
├── model/
│   ├── Account.java
│   ├── CurrentAccount.java
│   ├── Customer.java
│   ├── FixedDepositAccount.java
│   ├── InterestBearing.java
│   ├── Premium.java
│   ├── SavingsAccount.java
│   ├── Transactable.java
│   ├── WithdrawRule.java
│   └── annotation/
│       ├── Id.java
│       ├── MaxLength.java
│       └── Positive.java
│
├── service/
│   ├── Main.java
│   ├── MiniBank.java
│   ├── BankService.java
│   ├── Repository.java
│   ├── AccountStore.java
│   ├── AccountWorker.java
│   ├── DeadlockDemo.java
│   ├── ReportGenerator.java
│   ├── StatePersister.java
│   ├── TransactionBuffer.java
│   ├── TransactionLog.java
│   └── TransactionProcessor.java
│
├── util/
│   ├── AnnotationValidator.java
│   ├── BankSession.java
│   ├── Command.java
│   ├── CommandParser.java
│   ├── StatementFormatter.java
│   ├── TransactionType.java
│   └── Validator.java
│
├── src/
│   └── test/
│       └── java/
│           └── service/
│               └── BankServiceTest.java
│
├── data/
│   ├── accounts.dat
│   ├── end-of-day-report.txt
│   └── logs/
│       ├── empty.log
│       ├── notes.txt
│       └── transactions.log
│
├── META-INF/
│   └── MANIFEST.MF
│
├── .gitignore
├── manifest.mf
├── MiniBank.jar
├── pom.xml
├── README.md
└── sources.txt
```

**Build artifacts:** Maven generates compiled classes, test reports, and the packaged application under `target/`. The existing `out/` directory contains compiled classes from the project's earlier build workflow.

## Object-Oriented Programming Concepts

MiniBank applies the following Java concepts throughout the project:

- **Classes and objects:** Represent accounts, customers, and banking components.
- **Encapsulation:** Control access to object data through class members and methods.
- **Constructors and constructor chaining:** Initialize objects consistently.
- **Static and final members:** Represent shared state and constants where appropriate.
- **Inheritance and polymorphism:** Support specialized account types and behaviours.
- **Method overriding:** Customize behaviour in derived classes.
- **Interfaces and enums:** Define contracts and represent fixed sets of values.
- **Records:** Represent structured data where used.
- **Annotations and reflection:** Explore metadata-driven validation and Java reflection.
- **Regular expressions:** Validate input formats.
- **Collections and generics:** Build reusable, type-safe data operations.
- **Exception handling:** Represent and manage invalid operations.
- **File handling:** Store and recover application data.
- **Multithreading and synchronization:** Coordinate concurrent operations.
- **Executor services:** Manage groups of concurrent tasks.
- **JUnit testing:** Verify expected application behaviour.
- **Maven and JAR packaging:** Automate the build and prepare the executable application.

## Banking Services and Transaction Processing

The `BankService` component provides reusable methods for core banking operations, including opening accounts, deposits, withdrawals, transfers, statements, and account listing.

The console application connects menu operations to the banking service, keeping important banking logic separate from user interaction.

The project also includes transaction-processing components for working with batches of deposit and withdrawal operations. Supporting demonstrations explore thread coordination, concurrent balance updates, and deadlock prevention through consistent lock ordering.

These components provide practical examples of service-layer design and Java concurrency. They are educational implementations rather than a claim of production-grade banking infrastructure.

## Data Persistence and Reporting

MiniBank supports loading saved account records during startup and saving account data through its persistence and exit workflows.

The project also includes reporting and transaction-log components for supporting application records.

Persistence makes it possible to retain account information between application runs instead of keeping all account data only in memory.

**Important:** Keep the runtime data files in their expected project-relative locations. Back up important data before testing account deletion or other operations that modify saved records.

## Automated Testing

JUnit tests are located at:

`src/test/java/service/BankServiceTest.java`

The current test suite covers:

1. **Deposit:** Verifies that a deposit increases the account balance.
2. **Withdrawal validation:** Verifies that a withdrawal beyond the permitted limit throws an exception.
3. **Concurrent deposits:** Verifies that multiple concurrent deposits produce the expected final balance.

Run the tests from the project root:

```powershell
mvn test
```

**Previously verified result:** 3 tests run, 0 failures, 0 errors, and 0 skipped tests.

## Getting Started

### Prerequisites

Install:

- A JDK compatible with the project's Maven configuration.
- Apache Maven.
- A terminal such as PowerShell on Windows.

Verify your installations:

```powershell
java -version
mvn -version
```

### Build and Test

Open PowerShell in the project root directory and run:

```powershell
mvn test
mvn package
```

Maven compiles the project, runs the configured tests, and packages the application if the build succeeds.

### Run the Application

Launch the packaged JAR:

```powershell
java -jar ".\target\oop-minibank-14.0.0.jar"
```

The application opens its console menu, allowing you to access banking operations and the available project demonstrations.

### Typical Workflow

1. Launch the application.
2. Select an operation from the console menu.
3. Enter the requested account details or transaction values.
4. Review the result displayed by the application.
5. Use the available reporting or persistence workflow when needed.
6. Exit through the save-and-exit option to preserve account data.

## Build Verification

The project has previously passed the following checks:

| Check | Result |
|---|---|
| Maven validation | Successful |
| Compilation | Successful |
| JUnit test suite | 3 passed; 0 failures |
| Maven packaging | Successful |
| Runnable JAR launch | Successful |
| Account persistence across restarts | Verified |

These results describe the checks completed during development; run the commands again after making further changes to confirm the current working tree.

## Project Outcome

MiniBank brings together semester-long Java programming exercises in one application. It demonstrates how object-oriented design, banking operations, reusable repositories, validation, persistence, transaction processing, concurrency, automated testing, and executable packaging can work together within a structured software project.

The result is a practical foundation for further improvements in software design, testing, and application reliability.

## Future Enhancements

Potential areas for further development include:

- Database-backed storage.
- A graphical user interface.
- More extensive integration and persistence tests.
- Stronger transaction rollback and recovery mechanisms.
- Expanded audit logging.
- Improved separation between demonstration exercises and core banking workflows.

---

<p align="center">
  <strong>MiniBank · Java OOP Semester Project</strong>
  <br />
  <sub>Built incrementally · Tested with JUnit · Packaged with Maven</sub>
</p>
