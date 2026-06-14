# Architecture Design Document

## System Overview

LoanTracker is a three-tier enterprise application with clean separation of concerns:

```
┌─────────────────────────────────────────────────────────────────┐
│                     Application Layer                           │
│  LoanTrackerApp → LoanService → Validation → Error Handling     │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                    Business Logic Layer                          │
│  LoanService, LoanRepository, ValidationUtils                  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                      Data Access Layer                           │
│  LoanRepository, ConnectionPoolManager, SQL Execution          │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                      Infrastructure Layer                        │
│  H2 Database, HikariCP Connection Pool, File I/O               │
└─────────────────────────────────────────────────────────────────┘
```

## Component Breakdown

### 1. Configuration Management (`config/AppConfig.java`)
**Purpose:** Centralize all configuration constants
**Responsibility:**
- Database connection parameters
- Connection pool settings
- Application metadata
- Validation constraints
- Configuration logging

**Key Features:**
- No hardcoded values in code
- Environment-aware configuration ready
- DRY principle compliance
- Immutable constants

### 2. Exception Hierarchy

#### Custom Exception (`exception/LoanTrackerException.java`)
```java
public class LoanTrackerException extends Exception {
    private final ErrorCode errorCode;
    private final String userMessage;
}
```
**Purpose:** Consistent error handling across application
**Benefits:**
- Caught specifically by framework
- Error code for logging/monitoring
- User-friendly messages
- Root cause preservation

#### Error Codes (`exception/ErrorCode.java`)
**Strategy:** Enumeration-based error codes
- Prefix identifies error category (DB_, VAL_, BIZ_, SYS_)
- Numeric code for tracking
- Description for documentation
- Easy to monitor and alert on

### 3. Connection Pool Management (`util/ConnectionPoolManager.java`)
**Pattern:** Singleton with double-checked locking
**Responsibility:**
- Single HikariCP data source instance
- Thread-safe initialization
- Connection acquisition and release
- Graceful shutdown

**Configuration:**
```
- Maximum Pool Size: 10
- Max Lifetime: 30 minutes
- Idle Timeout: 10 minutes
- Connection Timeout: 30 seconds
- Leak Detection: 60 seconds
```

### 4. Validation Utilities (`util/ValidationUtils.java`)
**Pattern:** Utility class with static methods
**Responsibility:**
- Input validation logic
- Business rule enforcement
- Consistent error reporting

**Validates:**
- Borrower names
- Principal amounts
- Interest rates
- Loan terms
- Start dates
- Payment amounts

### 5. Data Transfer Objects (DTO)

#### CreateLoanRequest
**Purpose:** API boundary for loan creation
**Contains:**
- borrowerName
- principalAmount
- interestRate
- loanTermMonths
- startDate

#### LoanResponse
**Purpose:** Clean API contract for loan data
**Contains:**
- id
- borrowerName
- principalAmount
- interestRate
- loanTermMonths
- startDate
- status
- remainingBalance
- paymentsRemaining

### 6. Domain Model (`model/Loan.java`)
**Purpose:** Represents loan business entity
**Responsibility:**
- Encapsulate loan data
- Basic validation (not business logic)
- Immutability where appropriate

### 7. Repository/DAO (`repository/LoanRepository.java`)
**Pattern:** Data Access Object
**Responsibility:**
- All database operations
- SQL execution
- ResultSet mapping
- Transaction boundaries

**Operations:**
- `create(Loan)` - Insert new loan
- `findById(int)` - Retrieve by ID
- `findAll()` - Get all loans
- `updateStatus(int, String)` - Update status
- `delete(int)` - Delete loan
- `countByStatus(String)` - Count by status

**SQL Injection Prevention:**
- Parameterized queries
- No string concatenation
- PreparedStatement usage

### 8. Service Layer (`service/LoanService.java`)
**Pattern:** Service Locator / Facade
**Responsibility:**
- Orchestrate business logic
- Coordinate validation
- Handle errors and recovery
- DTO conversion

**Operations:**
- `createLoan(CreateLoanRequest)` - Create with validation
- `getLoan(int)` - Retrieve by ID
- `getAllLoans()` - Get all loans
- `closeLoan(int)` - Close active loan
- `getSummary()` - Get statistics

### 9. Logging Strategy
**Framework:** SLF4J + Logback
**Levels:**
- DEBUG - Detailed information for debugging
- INFO - High-level operational info
- WARN - Warning conditions
- ERROR - Error conditions

**Configuration:**
- Console appender for real-time visibility
- File appender with rolling policy
- Size-based rotation (10MB)
- Time-based rotation (daily)
- Retention period (30 days)

## Data Flow

### Create Loan Flow
```
User Input
    ↓
LoanTrackerApp.createLoan()
    ↓
LoanService.createLoan()
    ├─ ValidationUtils.validateBorrowerName()
    ├─ ValidationUtils.validatePrincipalAmount()
    ├─ ValidationUtils.validateInterestRate()
    ├─ ValidationUtils.validateLoanTerm()
    ├─ ValidationUtils.validateStartDate()
    ↓
LoanRepository.create()
    ├─ Get connection from pool
    ├─ Execute INSERT statement
    ├─ Retrieve generated ID
    ↓
Database (H2)
    ↓
Response DTO
    ↓
User Output
```

### Retrieve Loan Flow
```
User Input (Loan ID)
    ↓
LoanService.getLoan(id)
    ↓
LoanRepository.findById(id)
    ├─ Get connection from pool
    ├─ Execute SELECT query
    ├─ Map ResultSet to Loan object
    ↓
Optional<Loan>
    ├─ If present: Convert to DTO
    ├─ If empty: Throw exception
    ↓
Response DTO or Exception
    ↓
User Output
```

## Error Handling Strategy

### Validation Errors
- Occur at API boundary
- Caught by LoanService
- Converted to LoanTrackerException
- User-friendly messages

### Database Errors
- SQLException caught at repository
- Wrapped in LoanTrackerException
- Error code logged
- Root cause preserved

### Business Logic Errors
- Specific error codes
- Service layer checks
- Exception thrown immediately
- No silent failures

## Security Considerations

### SQL Injection Prevention
- PreparedStatements with parameters
- No string concatenation in queries
- Input validation at boundary

### Resource Management
- Try-with-resources for all database operations
- Connection cleanup guaranteed
- Prepared statement cleanup
- Result set cleanup

### Authentication & Authorization
- Future scope for JWT/OAuth
- Role-based access control ready
- Currently single-user mode

## Performance Optimizations

### Connection Pooling
- HikariCP with 10 connections
- Reduces connection overhead
- Automatic cleanup
- Leak detection

### Database Indexes
- Status index for filtering
- Borrower name index for searching
- Created date index for sorting
- Foreign key index for joins

### Lazy Loading
- DTOs loaded on demand
- Calculated fields (remainingBalance)
- Pagination ready for large datasets

## Scalability Considerations

### Horizontal Scaling
- Stateless service layer
- Connection pool per instance
- Shared database
- Load balancer friendly

### Vertical Scaling
- Configurable pool size
- Batch operation support
- Query optimization ready
- Caching layer ready

### Future Enhancements
- Distributed caching (Redis)
- API rate limiting
- Request queuing
- Async processing

## Testing Strategy

### Unit Tests (20+)
- Validation layer tests
- Service logic tests
- Error handling tests
- Integration tests

### Test Coverage Areas
- Happy path scenarios
- Validation failures
- Error conditions
- Edge cases
- Null handling

### Test Execution
- Maven Surefire plugin
- JUnit 4 framework
- Organized test sections
- Clear naming conventions

## Deployment Architecture

### Development
```
Local Machine
├── JDK 17
├── Maven
├── H2 Database (./data/loantracker)
├── Logs (./logs/)
└── IDE (IntelliJ)
```

### Production (Ready)
```
Server
├── Java Runtime (JRE 17)
├── Application JAR
├── External H2 Database
├── Log aggregation
├── Monitoring & Alerts
└── Health checks
```

## Monitoring & Observability

### Logging
- Application lifecycle events
- Database operations
- Validation failures
- Error stack traces

### Metrics (Future)
- Request count
- Response time
- Error rate
- Pool utilization

### Health Checks
- Database connectivity
- Connection pool status
- Disk space
- Memory usage

## Dependencies & Versions

| Library | Version | Purpose |
|---------|---------|---------|
| H2 Database | 2.2.224 | Embedded DB |
| HikariCP | 5.1.0 | Connection pooling |
| SLF4J | 2.0.9 | Logging abstraction |
| Logback | 1.4.11 | Logging implementation |
| Gson | 2.10.1 | JSON processing |
| JUnit | 4.13.2 | Testing framework |

## Design Decisions & Rationale

### Why HikariCP?
- Highest performance connection pool
- Proven in production
- Minimal overhead
- Excellent monitoring
- Active maintenance

### Why H2?
- Embedded database (no setup)
- JDBC standard compliance
- Development/testing friendly
- Production support available
- SQL compatibility

### Why Service Layer?
- Decouples business logic
- Testable in isolation
- Reusable across endpoints
- Future migration ready

### Why DTOs?
- API contract separation
- Validation boundary
- Version compatibility
- Type safety

### Why Custom Exceptions?
- Application-specific errors
- Consistent handling
- Error tracking
- User-friendly messages

## Future Enhancements

1. **REST API** - HTTP endpoints
2. **Authentication** - JWT/OAuth
3. **Pagination** - Large dataset handling
4. **Caching** - Redis integration
5. **Batch Operations** - Bulk processing
6. **Export** - PDF/Excel reports
7. **Async Processing** - Job queue
8. **Event Sourcing** - Audit trail
9. **Multi-tenancy** - Isolated data
10. **Advanced Analytics** - Reporting

---

**Last Updated:** June 14, 2026  
**Version:** 1.0.0  
**Status:** Production Ready
