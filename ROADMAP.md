# 🚀 Toycell Backend - Project Roadmap

**Project:** Digital Wallet Fintech Application  
**Tech Stack:** Java 21 + Spring Boot 3.2.x + Gradle Multi-Module + Oracle 21c XE  
**Architecture:** Microservices with Manual Compensation (No Full Saga)  
**Started:** 2026-02-03

---

## 📦 Phase 0: Foundation & Setup

### Gradle Multi-Module Setup
- [ ] Configure root `settings.gradle` with all modules
- [ ] Configure root `build.gradle` with `subprojects {}` block
- [ ] Create shared library modules:
  - [ ] `common-domain`
  - [ ] `common-exception`
  - [ ] `common-encrypt`
- [ ] Create microservice modules:
  - [ ] `service-auth` (Port: 8081)
  - [ ] `service-account` (Port: 8082)
  - [ ] `service-balance` (Port: 8083)
  - [ ] `service-fee` (Port: 8084)
  - [ ] `service-transfer` (Port: 8085)
  - [ ] `service-transaction` (Port: 8086)
  - [ ] `api-gateway` (Port: 8080)

### Database Setup (Oracle 21c XE)
- [ ] Create schema `TOYCELL_AUTH` (tables: USERS, REFRESH_TOKENS)
- [ ] Create schema `TOYCELL_ACCOUNT` (tables: USER_PROFILES)
- [ ] Create schema `TOYCELL_BALANCE` (tables: WALLETS)
- [ ] Create schema `TOYCELL_FEE` (tables: FEE_RULES)
- [ ] Create schema `TOYCELL_TRANSFER` (tables: TRANSFER_REQUESTS)
- [ ] Create schema `TOYCELL_TRANSACTION` (tables: TRANSACTION_HISTORY)
- [ ] Grant necessary privileges to each schema
- [ ] Prepare connection properties for each service

### Version Control
- [ ] Initialize Git repository
- [ ] Create `.gitignore` (exclude build/, .gradle/, .idea/)
- [ ] Initial commit with base structure

---

## 📚 Phase 1: Core Libraries

### `common-domain` Module
- [ ] Create `BaseEntity` (id, createdAt, updatedAt)
- [ ] Create Enums:
  - [ ] `Currency` (TRY, USD, EUR)
  - [ ] `TransactionType` (DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT)
  - [ ] `TransactionStatus` (PENDING, SUCCESS, FAILED, ROLLED_BACK)
- [ ] Create `ApiResponse<T>` wrapper (success, message, data, timestamp)
- [ ] Create `PageResponse<T>` for pagination

### `common-exception` Module
- [ ] Create `BusinessException` (message, errorCode)
- [ ] Create `ErrorCode` enum (AUTH_FAILED, INSUFFICIENT_BALANCE, USER_NOT_FOUND, etc.)
- [ ] Create `GlobalExceptionHandler` (@RestControllerAdvice)
- [ ] Add validation exception handlers (MethodArgumentNotValidException)

### `common-encrypt` Module
- [ ] Implement `AesEncryptionUtil` class:
  - [ ] AES-256/CBC/PKCS5Padding
  - [ ] Generate random IV per encryption
  - [ ] Prepend IV to encrypted data (Base64 encoded)
  - [ ] Read key from `encryption.secret.key` property (configurable)
- [ ] Create JPA `@Converter` class `EncryptedStringConverter`:
  - [ ] `convertToDatabaseColumn()` → encrypts
  - [ ] `convertToEntityAttribute()` → decrypts
- [ ] Add default dev key: "ToycellSecretKey2026" (32 bytes for AES-256)

---

## 🔐 Phase 2: Base Services (Auth, Account, Gateway)

### `service-auth` (Authentication Service)
- [ ] Setup Spring Boot application (port 8081)
- [ ] Configure Oracle connection to `TOYCELL_AUTH` schema
- [ ] Create Entity: `User` (id, username, email, passwordHash, role, createdAt)
- [ ] Create Entity: `RefreshToken` (optional, for future)
- [ ] Implement DTOs:
  - [ ] `RegisterRequest` (username, email, password)
  - [ ] `LoginRequest` (email, password)
  - [ ] `AuthResponse` (token, expiresIn, username)
- [ ] Implement `AuthService`:
  - [ ] Register logic (BCrypt password hashing)
  - [ ] Login logic (validate credentials)
  - [ ] JWT generation (24h expiration, using `io.jsonwebtoken.jjwt`)
- [ ] Create `AuthController`:
  - [ ] POST `/api/auth/register`
  - [ ] POST `/api/auth/login`
- [ ] Add JWT secret key to `application.properties`
- [ ] **Test with Postman:** Register → Login → Verify token

### `service-account` (User Profile Service)
- [ ] Setup Spring Boot application (port 8082)
- [ ] Configure Oracle connection to `TOYCELL_ACCOUNT` schema
- [ ] Add dependency on `common-encrypt`
- [ ] Create Entity: `UserProfile`:
  - [ ] id, userId (from Auth), firstName, lastName
  - [ ] `@Convert(converter = EncryptedStringConverter.class) String identityNumber` (TCKN)
  - [ ] `@Convert(converter = EncryptedStringConverter.class) String phoneNumber`
  - [ ] birthDate, createdAt, updatedAt
- [ ] Implement DTOs:
  - [ ] `CreateProfileRequest`
  - [ ] `ProfileResponse` (decrypted data for display)
- [ ] Implement `ProfileService`:
  - [ ] Create profile (linked to userId from JWT)
  - [ ] Get profile by userId
  - [ ] Update profile
- [ ] Create `ProfileController`:
  - [ ] POST `/api/profiles` (secured with JWT)
  - [ ] GET `/api/profiles/me`
  - [ ] PUT `/api/profiles/me`
- [ ] Implement JWT validation filter (shared security config)
- [ ] **Test with Postman:** Create profile → Retrieve (verify encryption/decryption)

### `api-gateway` (Spring Cloud Gateway)
- [ ] Setup Spring Cloud Gateway project (port 8080)
- [ ] Configure routes in `application.yml`:
  - [ ] `/auth/**` → `http://localhost:8081`
  - [ ] `/profiles/**` → `http://localhost:8082`
  - [ ] `/balance/**` → `http://localhost:8083`
  - [ ] `/fees/**` → `http://localhost:8084`
  - [ ] `/transfers/**` → `http://localhost:8085`
  - [ ] `/transactions/**` → `http://localhost:8086`
- [ ] Add CORS configuration (for future frontend)
- [ ] Add Global JWT authentication filter (except `/auth/**`)
- [ ] **Test with Postman:** All requests through Gateway (port 8080)

---

## 💰 Phase 3: Business Engine (Balance, Fee)

### `service-balance` (Wallet Service)
- [ ] Setup Spring Boot application (port 8083)
- [ ] Configure Oracle connection to `TOYCELL_BALANCE` schema
- [ ] Create Entity: `Wallet`:
  - [ ] id, userId, currency (TRY/USD/EUR), balance, isActive, createdAt, updatedAt
  - [ ] Add constraint: one wallet per userId per currency
- [ ] Implement DTOs:
  - [ ] `CreateWalletRequest` (userId, currency)
  - [ ] `WalletResponse` (id, currency, balance)
  - [ ] `AdjustBalanceRequest` (walletId, amount, type: CREDIT/DEBIT)
- [ ] Implement `BalanceService`:
  - [ ] Create wallet (initial balance = 0)
  - [ ] Get wallet by userId and currency
  - [ ] **Adjust balance (ATOMIC):** Use `@Transactional` + optimistic locking
  - [ ] Validate sufficient funds before debit
- [ ] Create `BalanceController`:
  - [ ] POST `/api/balance/wallets` (create wallet)
  - [ ] GET `/api/balance/wallets/{userId}`
  - [ ] POST `/api/balance/adjust` (internal endpoint for service-transfer)
- [ ] **Test with Postman:** Create wallet → Adjust balance (credit/debit)

### `service-fee` (Fee Calculation Service)
- [ ] Setup Spring Boot application (port 8084)
- [ ] Configure Oracle connection to `TOYCELL_FEE` schema
- [ ] Create Entity: `FeeRule`:
  - [ ] id, transactionType, minAmount, maxAmount, feeType (FIXED/PERCENTAGE), feeValue, isActive
  - [ ] Example: TRANSFER, 0-1000 TRY → 2.5 TRY fixed fee
- [ ] Implement DTOs:
  - [ ] `CalculateFeeRequest` (transactionType, amount, currency)
  - [ ] `FeeResponse` (feeAmount, totalAmount)
- [ ] Implement `FeeService`:
  - [ ] Calculate fee based on active rules
  - [ ] Default fee if no rule found (e.g., 1%)
- [ ] Create `FeeController`:
  - [ ] POST `/api/fees/calculate`
  - [ ] GET `/api/fees/rules` (admin endpoint)
  - [ ] POST `/api/fees/rules` (admin: create fee rule)
- [ ] Seed default fee rules (via data.sql or migration)
- [ ] **Test with Postman:** Calculate fee for different amounts

---

## 🔄 Phase 4: Orchestration & Transaction Logic

### `service-transfer` (Transfer Orchestration with Manual Compensation)
- [ ] Setup Spring Boot application (port 8085)
- [ ] Configure Oracle connection to `TOYCELL_TRANSFER` schema
- [ ] Add Spring Cloud OpenFeign dependency
- [ ] Create Feign Clients:
  - [ ] `BalanceClient` → `service-balance`
  - [ ] `FeeClient` → `service-fee`
  - [ ] `TransactionClient` → `service-transaction`
- [ ] Create Entity: `TransferRequest`:
  - [ ] id, fromUserId, toUserId, amount, currency, feeAmount, status, createdAt, completedAt
- [ ] Implement DTOs:
  - [ ] `InitiateTransferRequest` (fromUserId, toUserId, amount, currency)
  - [ ] `TransferResponse` (transferId, status, message)
- [ ] Implement `TransferOrchestrationService`:
  - [ ] **Step 1:** Validate users exist (call account service)
  - [ ] **Step 2:** Calculate fee (call fee service)
  - [ ] **Step 3:** Check sender balance (call balance service)
  - [ ] **Step 4:** Debit sender wallet (call balance service)
  - [ ] **Step 5:** Credit receiver wallet (call balance service) - **IF FAILS → ROLLBACK**
  - [ ] **Step 6:** Log transaction (call transaction service)
  - [ ] **Manual Compensation Logic:**
    - [ ] Wrap credit in try-catch
    - [ ] On failure: reverse debit (credit sender back)
    - [ ] Update transfer status to ROLLED_BACK
    - [ ] Log compensation action
- [ ] Create `TransferController`:
  - [ ] POST `/api/transfers` (initiate transfer)
  - [ ] GET `/api/transfers/{id}` (get transfer status)
- [ ] **Test with Postman:** Successful transfer + Forced failure scenario

### `service-transaction` (Transaction History)
- [ ] Setup Spring Boot application (port 8086)
- [ ] Configure Oracle connection to `TOYCELL_TRANSACTION` schema
- [ ] Create Entity: `TransactionHistory`:
  - [ ] id, userId, transactionType, amount, currency, balanceBefore, balanceAfter, relatedUserId, status, createdAt
- [ ] Implement DTOs:
  - [ ] `LogTransactionRequest`
  - [ ] `TransactionHistoryResponse`
- [ ] Implement `TransactionService`:
  - [ ] Log transaction (called by transfer service)
  - [ ] Get user transaction history (paginated)
  - [ ] Filter by date range, type, status
- [ ] Create `TransactionController`:
  - [ ] POST `/api/transactions/log` (internal endpoint)
  - [ ] GET `/api/transactions/user/{userId}` (get history)
- [ ] **Test with Postman:** Perform transfer → Verify transaction logs

---

## 🎨 Phase 5: Testing, Documentation & Future Prep

### Integration Testing
- [ ] Create Postman Collection with all endpoints:
  - [ ] Auth Flow (Register → Login)
  - [ ] Profile Management (Create → Get → Update)
  - [ ] Wallet Operations (Create → Adjust)
  - [ ] Fee Calculation
  - [ ] Transfer Flow (Happy Path)
  - [ ] Transfer Rollback Scenario
  - [ ] Transaction History
- [ ] Test all scenarios through API Gateway (port 8080)
- [ ] Document expected request/response examples

### Documentation
- [ ] Generate Swagger UI for each service (Springdoc OpenAPI)
- [ ] Access Swagger: `http://localhost:808X/swagger-ui.html`
- [ ] Create `API_DOCUMENTATION.md` with:
  - [ ] Service architecture diagram
  - [ ] Database schema ERD
  - [ ] API endpoint summary
  - [ ] JWT token usage guide
  - [ ] Error code reference

### Code Quality & Best Practices
- [ ] Add input validation (`@Valid`, `@NotNull`, `@Size`, etc.)
- [ ] Add proper logging (SLF4J) in all services
- [ ] Externalize configurations (use `application-dev.properties`, `application-prod.properties`)
- [ ] Add health check endpoints (`/actuator/health`)
- [ ] Review exception handling consistency

### Deployment Preparation
- [ ] Create `docker-compose.yml` (optional: for Oracle + all services)
- [ ] Create `README.md` with:
  - [ ] Setup instructions
  - [ ] How to run each service
  - [ ] Database schema creation scripts
  - [ ] Environment variables documentation
- [ ] Prepare for frontend integration:
  - [ ] CORS configuration verified
  - [ ] Token refresh mechanism (if needed)
  - [ ] WebSocket support planning (for real-time notifications)

---

## 🎯 Success Criteria

- ✅ All services start independently without errors
- ✅ API Gateway routes requests correctly
- ✅ JWT authentication works end-to-end
- ✅ Sensitive data (TCKN, phone) encrypted in database
- ✅ Money transfer completes successfully
- ✅ Rollback mechanism works on transfer failure
- ✅ All APIs tested via Postman
- ✅ Swagger documentation accessible
- ✅ Transaction history accurately logged

---

## 📝 Notes & Decisions

- **No Eureka:** Direct URL-based service discovery via application.properties
- **No Full Saga:** Manual try-catch compensation in transfer orchestration
- **Database:** Separate schemas per service (schema-per-service pattern)
- **Encryption:** AES-256-CBC with random IV prepended to ciphertext
- **JWT:** 24-hour expiration for development simplicity

---

**Last Updated:** 2026-02-03  
**Status:** 🚧 In Progress - Phase 0
