# LoanTracker - Enterprise-Grade Loan Management Application

## 📋 Project Overview

**LoanTracker** is a production-ready loan management application built with Java 17, featuring comprehensive loan tracking, payment management, and reporting capabilities. The application demonstrates enterprise-grade architecture patterns and best practices.

**Version:** 1.0.0  
**Status:** Active Development  
**Repository:** [saurabhbijalwang/loantracker](https://github.com/saurabhbijalwang/loantracker)

---

## 🎯 Key Features

### Core Functionality
- ✅ **Loan Management** - Create, read, update, and delete loans
- ✅ **Payment Tracking** - Record and manage loan payments
- ✅ **Status Management** - Track loan status (ACTIVE, CLOSED, DEFAULTED)
- ✅ **Database Persistence** - H2 embedded database with schema management
- ✅ **Connection Pooling** - HikariCP for optimized database connections
- ✅ **Comprehensive Validation** - Input validation at API boundary
- ✅ **Error Handling** - Custom exception hierarchy with error codes
- ✅ **Structured Logging** - SLF4J + Logback with rotation

### Enterprise Patterns
- Repository/DAO Pattern for data access
- Service layer for business logic
- Data Transfer Objects (DTOs) for API contracts
- Dependency Injection ready architecture
- Thread-safe singleton implementations
- Resource management with try-with-resources

---

## 🏗️ Architecture & Design

### Project Structure

```
loantracker/
├── src/
│   ├── main/
│   │   ├── java/com/loantracker/
│   │   │   ├── config/
│   │   │   │   └── AppConfig.java           # Centralized configuration
│   │   │   ├── exception/
│   │   │   │   ├── LoanTrackerException.java # Custom exception
│   │   │   │   └── ErrorCode.java            # Error code enumeration
│   │   │   ├── util/
│   │   │   │   ├── ValidationUtils.java     # Input validation utilities
│   │   │   │   └── ConnectionPoolManager.java # HikariCP pool management
│   │   │   ├── dto/
│   │   │   │   ├── CreateLoanRequest.java   # Loan creation request
│   │   │   │   └── LoanResponse.java        # Loan response DTO
│   │   │   ├── model/
│   │   │   │   └── Loan.java                # Loan domain entity
│   │   │   ├── repository/
│   │   │   │   └── LoanRepository.java      # Data access layer
│   │   │   ├── service/
│   │   │   │   └── LoanService.java         # Business logic layer
│   │   │   ├── LoanTrackerApp.java          # Application entry point
│   │   │   └── LoanTrackerService.java      # Service orchestrator
│   │   └── resources/
│   │       └── logback.xml                  # Logging configuration
│   ├── test/
│   │   └── java/com/loantracker/
│   │       └── LoanTrackerAppTest.java      # Comprehensive unit tests
├── db/
│   └── schema.sql                           # Database schema
├── scripts/
│   └── prepare_git.bat                      # Git initialization script
├── .gitignore                               # Git ignore rules
├── pom.xml                                  # Maven configuration
├── README.md                                # This file
└── ARCHITECTURE.md                          # Detailed architecture docs

```

### Layered Architecture

```
┌─────────────────────────────────────────────────┐
│           LoanTrackerApp (Entry Point)          │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│        Service Layer (LoanService)              │
│    - Business logic orchestration               │
│    - Input validation coordination              │
│    - Error handling and recovery                │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│      Repository Layer (LoanRepository)          │
│    - Data Access Object (DAO) pattern           │
│    - SQL execution and transaction management   │
│    - ResultSet mapping                          │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│         Database Layer (H2 + HikariCP)          │
│    - Connection pooling                         │
│    - Schema management                          │
│    - Data persistence                           │
└─────────────────────────────────────────────────┘
```

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17 (LTS) |
| Build Tool | Maven | 3.11.0+ |
| Database | H2 | 2.2.224 |
| Connection Pool | HikariCP | 5.1.0 |
| Logging | SLF4J + Logback | 2.0.9 / 1.4.11 |
| JSON Processing | Gson | 2.10.1 |
| Testing | JUnit | 4.13.2 |

---

## 📦 Dependencies

### Core Dependencies
```xml
<!-- Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.2.224</version>
</dependency>

<!-- Connection Pool -->
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.1.0</version>
</dependency>

<!-- Logging -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.9</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.11</version>
</dependency>

<!-- JSON -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17 JDK (Oracle JDK, OpenJDK, or Temurin)
- Maven 3.6+
- Git

### Installation & Setup

#### 1. Clone Repository
```bash
git clone https://github.com/saurabhbijalwang/loantracker.git
cd loantracker
```

#### 2. Build Project
```bash
mvn clean package
```

#### 3. Run Application
```bash
java -jar target/loantracker-1.0.0.jar
```

#### 4. Run Tests
```bash
mvn test
```

#### 5. Open in IntelliJ IDEA
- **File → Open**
- Select `loantracker` directory
- IntelliJ automatically recognizes Maven project
- Right-click `LoanTrackerApp.java` → **Run**

---

## 📊 Database Schema

### Loans Table
```sql
CREATE TABLE loans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    borrower_name VARCHAR(255) NOT NULL,
    principal_amount DECIMAL(10, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    loan_term_months INT NOT NULL,
    start_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CLOSED', 'DEFAULTED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_borrower (borrower_name),
    INDEX idx_created (created_at)
);
```

### Payments Table
```sql
CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL,
    payment_amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE,
    INDEX idx_loan_id (loan_id),
    INDEX idx_payment_date (payment_date),
    INDEX idx_created (created_at)
);
```

---

## ✅ Code Quality & Testing

### Test Coverage
- **20+ Comprehensive Unit Tests**
- Validation layer testing
- Business logic verification
- Error handling verification
- Exception code mapping

### Test Categories
1. **Validation Tests** - Input validation and constraints
2. **Service Tests** - Business logic operations
3. **Error Handling Tests** - Exception handling and mapping
4. **Integration Tests** - Database operations

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=LoanTrackerAppTest

# With verbose output
mvn test -X
```

---

## 📝 Configuration

### Application Configuration (`AppConfig.java`)
```java
// Database
DB_URL = "jdbc:h2:./data/loantracker"
DB_USER = "sa"
DB_PASSWORD = ""

// Connection Pool
DB_POOL_SIZE = 10
DB_POOL_MAX_LIFETIME = 1800000 ms (30 min)
DB_POOL_IDLE_TIMEOUT = 600000 ms (10 min)
DB_POOL_CONNECTION_TIMEOUT = 30000 ms (30 sec)

// Validation
MAX_BORROWER_NAME_LENGTH = 255
MIN_LOAN_TERM_MONTHS = 1
MAX_LOAN_TERM_MONTHS = 360 (30 years)
```

### Logging Configuration (`logback.xml`)
- **Console Appender** - Real-time log output
- **File Appender** - Persistent logs with rotation
- **File Size Policy** - Max 10MB per file
- **Retention Policy** - 30 days
- **Total Cap** - 1GB maximum

---

## 🔐 Error Handling

### Error Code System

| Code | Category | Description |
|------|----------|-------------|
| DB_001 | Database | Failed to establish connection |
| DB_002 | Database | Database operation failed |
| DB_003 | Database | Record not found |
| VAL_001 | Validation | Invalid loan data |
| VAL_002 | Validation | Invalid payment data |
| VAL_003 | Validation | Borrower name required |
| VAL_004 | Validation | Invalid principal amount |
| VAL_005 | Validation | Invalid interest rate |
| VAL_006 | Validation | Invalid loan term |
| BIZ_001 | Business | Loan not found |
| BIZ_002 | Business | Loan not active |
| BIZ_003 | Business | Payment exceeds remaining |
| SYS_001 | System | Initialization failed |
| SYS_999 | System | Internal server error |

---

## 💻 API Usage Examples

### Create a Loan
```java
CreateLoanRequest request = new CreateLoanRequest(
    "John Doe",
    new BigDecimal("50000.00"),
    new BigDecimal("5.5"),
    60,
    LocalDate.of(2026, 1, 1)
);

LoanService service = new LoanService();
LoanResponse response = service.createLoan(request);
```

### Retrieve a Loan
```java
LoanResponse loan = service.getLoan(1);
System.out.println("Borrower: " + loan.getBorrowerName());
System.out.println("Amount: " + loan.getPrincipalAmount());
```

### Get All Loans
```java
List<LoanResponse> loans = service.getAllLoans();
loans.forEach(loan -> System.out.println(loan.getBorrowerName()));
```

### Close a Loan
```java
service.closeLoan(1);
```

### Get Summary
```java
String summary = service.getSummary();
System.out.println(summary); // "Active Loans: 5, Closed Loans: 2"
```

---

## 📈 Performance Considerations

### Connection Pooling
- **HikariCP** for optimized connection management
- Connection leak detection enabled
- Automatic idle connection cleanup
- Maximum 10 concurrent connections

### Database Optimization
- **Indexes** on frequently queried columns
- **Constraints** for data integrity
- **Foreign Keys** for referential integrity
- **Batch operations** support

### Logging Performance
- Lazy evaluation of log messages
- Appropriate log levels (DEBUG, INFO, WARN, ERROR)
- Async logging for high-throughput scenarios

---

## 🔄 Development Workflow

### Branch Strategy
- `main` - Production-ready code
- `develop` - Development branch
- `feature/*` - Feature branches
- `bugfix/*` - Bug fix branches

### Commit Message Format
```
[TYPE] Subject line (max 50 chars)

Detailed description (optional)
- Point 1
- Point 2

Closes #123
```

Types: feat, fix, docs, style, refactor, test, chore

### Example Commits
```bash
git add .
git commit -m "feat: add loan service with validation"
git commit -m "test: add comprehensive unit tests"
git commit -m "docs: add architecture documentation"
git push origin feature/loan-service
```

---

## 📚 Design Patterns Used

| Pattern | Location | Purpose |
|---------|----------|---------|
| Singleton | ConnectionPoolManager | Ensure single pool instance |
| Repository/DAO | LoanRepository | Abstract data access |
| Service Locator | LoanService | Coordinate business logic |
| DTO | CreateLoanRequest, LoanResponse | API contracts |
| Exception Translation | Custom Exceptions | Consistent error handling |
| Template Method | Database operations | Reduce code duplication |

---

## 🎓 SOLID Principles

- **S**ingle Responsibility - Each class has one reason to change
- **O**pen/Closed - Open for extension, closed for modification
- **L**iskov Substitution - Derived classes are substitutable
- **I**nterface Segregation - Client-specific interfaces
- **D**ependency Inversion - Depend on abstractions, not concretions

---

## 🚦 Build & Deployment

### Build Stages
1. **Compile** - Compile Java source code
2. **Test** - Run unit tests
3. **Package** - Create JAR with dependencies
4. **Artifact** - Upload to repository

### Build Command
```bash
mvn clean package
```

### Output Artifacts
- `target/loantracker-1.0.0.jar` - Executable JAR
- `target/surefire-reports/` - Test reports

---

## 📋 Checklist for Contributors

- [ ] Code follows Java conventions
- [ ] All tests pass (`mvn test`)
- [ ] Code compiles without warnings (`mvn clean package`)
- [ ] Added JavaDoc comments for public methods
- [ ] No hardcoded strings (use AppConfig)
- [ ] Proper exception handling
- [ ] Logging at appropriate levels
- [ ] Resource cleanup (try-with-resources)
- [ ] Input validation
- [ ] No SQL injection vulnerabilities

---

## 📞 Support & Troubleshooting

### Common Issues

#### "mvn: command not found"
**Solution:** Install Maven or add to PATH
```bash
# Windows
set PATH=%PATH%;C:\apache-maven\bin

# Linux/Mac
export PATH=$PATH:/usr/local/maven/bin
```

#### "Java 17 not found"
**Solution:** Install Java 17 and set JAVA_HOME
```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Linux/Mac
export JAVA_HOME=/usr/libexec/java_home -v 17
```

#### "Connection pool failed"
**Solution:** Check database permissions and path
```bash
# Ensure data directory exists
mkdir data
chmod 755 data
```

---

## 📄 License

This project is provided as-is for educational and commercial use.

---

## 👥 Contributors

- **Saurabh Bijalwang** - Lead Developer
- GitHub: [@saurabhbijalwang](https://github.com/saurabhbijalwang)

---

## 🗂️ Related Documentation

- [Architecture Design Document](./ARCHITECTURE.md)
- [API Reference](./API.md)
- [Testing Guide](./TESTING.md)
- [Deployment Guide](./DEPLOYMENT.md)

---

**Last Updated:** June 14, 2026  
**Status:** Production Ready
