# 🏦 Toycell Backend - Digital Wallet Fintech Application

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.10.2-blue.svg)](https://gradle.org/)
[![Oracle](https://img.shields.io/badge/Oracle-21c%20XE-red.svg)](https://www.oracle.com/database/technologies/xe-downloads.html)

## 📋 Quick Start

### Prerequisites
- Java 17 (Amazon Corretto)
- Oracle 21c XE
- DBeaver (recommended for database management)

### 1. Database Setup

**⚠️ IMPORTANT: Database setup file is not in repository for security reasons**

Create schemas manually in DBeaver:

```sql
-- Connect as SYSTEM user and execute:

CREATE USER TOYCELL_AUTH IDENTIFIED BY "YourPassword1";
GRANT CONNECT, RESOURCE TO TOYCELL_AUTH;
GRANT CREATE SESSION TO TOYCELL_AUTH;
GRANT CREATE TABLE TO TOYCELL_AUTH;
GRANT CREATE SEQUENCE TO TOYCELL_AUTH;
GRANT CREATE VIEW TO TOYCELL_AUTH;
ALTER USER TOYCELL_AUTH QUOTA UNLIMITED ON USERS;

CREATE USER TOYCELL_ACCOUNT IDENTIFIED BY "YourPassword2";
GRANT CONNECT, RESOURCE TO TOYCELL_ACCOUNT;
GRANT CREATE SESSION TO TOYCELL_ACCOUNT;
GRANT CREATE TABLE TO TOYCELL_ACCOUNT;
GRANT CREATE SEQUENCE TO TOYCELL_ACCOUNT;
GRANT CREATE VIEW TO TOYCELL_ACCOUNT;
ALTER USER TOYCELL_ACCOUNT QUOTA UNLIMITED ON USERS;

-- Repeat for: TOYCELL_BALANCE, TOYCELL_FEE, TOYCELL_TRANSFER, TOYCELL_TRANSACTION
```

### 2. Environment Variables Setup

Copy and configure environment variables:

```cmd
# Windows CMD
copy .env.example .env
# Edit .env with your actual passwords

# Set environment variables
set DB_PASSWORD_AUTH=YourPassword1
set DB_PASSWORD_ACCOUNT=YourPassword2
set JWT_SECRET_KEY=YourJWTSecretAtLeast32CharsLong
set ENCRYPTION_SECRET_KEY=YourEncryptionKey32BytesLong_
```

**⚠️ Never commit .env file to git!**

### 3. Running Services

```cmd
launcher.bat
# Choose option 4 to start all services
```

Services will be available at:
- **API Gateway**: http://localhost:8080
- **Auth Service**: http://localhost:8081
- **Account Service**: http://localhost:8082

### 4. Test API

```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "test123",
  "email": "test@toycell.com",
  "password": "Test123!"
}
```

---

## 🔒 Security Notes

### Why Environment Variables?

1. **Never commit passwords to Git** - Environment variables keep secrets out of version control
2. **Different passwords per environment** - Dev, staging, production can have different credentials
3. **Easy rotation** - Change passwords without modifying code
4. **Team security** - Each developer uses their own local passwords

### Default Values (INSECURE!)

Files like `application.properties` have placeholder values (`CHANGE_ME`). These will cause errors if you don't set environment variables - **this is intentional for security!**

**DO:**
- Set environment variables before running services
- Use strong, unique passwords (minimum 12 characters)
- Keep `.env` file in `.gitignore`

**DON'T:**
- Commit real passwords to Git
- Use default/example passwords in production
- Share your `.env` file with others

---

## 📦 Project Structure

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 (Amazon Corretto) | Programming Language |
| Spring Boot | 3.4.2 | Application Framework |
| Spring Cloud Gateway | 2023.0.0 | API Gateway |
| Spring Cloud OpenFeign | - | Service Communication |
| Spring Data JPA | - | Data Access Layer |
| Spring Security | - | Authentication & Authorization |
| Oracle Database | 21c XE | Relational Database |
| Gradle | 8.10.2 | Build Tool |
| Lombok | 1.18.30 | Boilerplate Code Reduction |
| JJWT | 0.12.3 | JWT Token Management |

---

## 📦 Microservices

### 🔐 service-auth (Port 8081)
- User registration & login
- JWT token generation
- Password hashing with BCrypt

**Endpoints:**
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/auth/health` - Health check

### 👤 service-account (Port 8082)
- User profile management
- **Encrypted fields:** Identity Number (TCKN), Phone Number
- Profile CRUD operations

**Endpoints:**
- `POST /api/profiles` - Create profile (requires JWT)
- `GET /api/profiles/me` - Get my profile
- `PUT /api/profiles/me` - Update my profile

### 💰 service-balance (Port 8083)
- Wallet management per currency (TRY, USD, EUR)
- Atomic balance adjustments
- Optimistic locking for concurrency

**Endpoints:**
- *(To be implemented in next phase)*

### 💵 service-fee (Port 8084)
- Dynamic fee calculation based on rules
- Support for fixed and percentage fees

**Endpoints:**
- *(To be implemented in next phase)*

### 🔄 service-transfer (Port 8085)
- Orchestrates money transfers between users
- **Manual compensation logic** for rollback scenarios
- Calls: Balance, Fee, Transaction services via OpenFeign

**Endpoints:**
- *(To be implemented in next phase)*

### 📜 service-transaction (Port 8086)
- Transaction history logging
- Paginated transaction queries

**Endpoints:**
- *(To be implemented in next phase)*

### 🌐 api-gateway (Port 8080)
- Single entry point for all client requests
- Route configuration to microservices
- CORS handling

---

## 📂 Module Structure

```
toycell-backend/
├── common-domain/          # Shared entities, enums, response wrappers
├── common-exception/       # Global exception handling
├── common-encrypt/         # AES-256-CBC encryption utilities
├── service-auth/           # Authentication service
├── service-account/        # Account management service
├── service-balance/        # Wallet service
├── service-fee/            # Fee calculation service
├── service-transfer/       # Transfer orchestration service
├── service-transaction/    # Transaction history service
├── api-gateway/            # Spring Cloud Gateway
└── database/               # SQL scripts for schema setup
```

---

## 🛠️ Setup Instructions

### Prerequisites

- **JDK 17** (Amazon Corretto recommended)
- **Gradle 8.10.2** (included via wrapper)
- **Oracle 21c XE** installed and running
- **Postman** or **cURL** for API testing

### 1. Database Setup

Execute the SQL script as `SYSTEM` user in Oracle:

```bash
sqlplus system/your_password@localhost:1521/XE
@database/01-create-schemas.sql
```

This will create 6 schemas:
- `TOYCELL_AUTH`
- `TOYCELL_ACCOUNT`
- `TOYCELL_BALANCE`
- `TOYCELL_FEE`
- `TOYCELL_TRANSFER`
- `TOYCELL_TRANSACTION`

### 2. Build the Project

```bash
cd C:\projects\toycell-be
.\gradlew.bat clean build
```

### 3. Run Services

**Option A: Run individually**

```bash
# Terminal 1 - Auth Service
.\gradlew.bat :service-auth:bootRun

# Terminal 2 - Account Service
.\gradlew.bat :service-account:bootRun

# Terminal 3 - API Gateway
.\gradlew.bat :api-gateway:bootRun
```

**Option B: Run all via IDE**
- Open project in IntelliJ IDEA
- Run each `*Application.java` file

---

## 🧪 Testing with Postman

### 1. Register a User

```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "expiresIn": 86400,
    "userId": 1,
    "username": "johndoe",
    "email": "john@example.com"
  },
  "timestamp": "2026-02-03T14:30:00"
}
```

### 2. Login

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

### 3. Create Profile (with JWT token)

```http
POST http://localhost:8080/api/profiles
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "identityNumber": "12345678901",
  "phoneNumber": "+905551234567",
  "birthDate": "1990-01-15"
}
```

**Note:** `identityNumber` and `phoneNumber` will be **encrypted** in the database using AES-256-CBC.

### 4. Get My Profile

```http
GET http://localhost:8080/api/profiles/me
Authorization: Bearer <your-jwt-token>
```

---

## 🔒 Security Features

### 1. Password Security
- **BCrypt** hashing with salt (10 rounds)
- Passwords never stored in plain text

### 2. JWT Authentication
- **Algorithm:** HS256
- **Expiration:** 24 hours
- **Claims:** userId, email, username, role
- Token validated on every protected endpoint

### 3. Data Encryption (AES-256-CBC)
- **Sensitive fields:** TCKN, Phone Number
- **Algorithm:** AES/CBC/PKCS5Padding
- **Key Size:** 256 bits
- **IV:** Random 16-byte IV prepended to ciphertext
- **Encoding:** Base64
- **Automatic:** JPA `@Converter` handles encryption/decryption

**Example Database Value:**
```
// Plain: 12345678901
// Encrypted: a3F2d1B3ZjRlNS4uLnJhbmRvbUlW...base64data...
```

---

## ⚙️ Configuration

### Environment Variables (Optional)

You can override default configurations:

```bash
# JWT Secret (must be 256+ bits)
JWT_SECRET_KEY=YourCustomSecretKeyHere...

# Encryption Key (must be 32 bytes for AES-256)
ENCRYPTION_SECRET_KEY=YourCustomEncryptionKey______

# Database Password
DB_PASSWORD_AUTH=ToyAuth2026!
```

### application.properties Files

Each service has its own configuration:

```
service-auth/src/main/resources/application.properties
service-account/src/main/resources/application.properties
api-gateway/src/main/resources/application.properties
```

---

## 📊 Database Schema

### service-auth (TOYCELL_AUTH)

**Table: USERS**
| Column | Type | Description |
|--------|------|-------------|
| id | NUMBER | Primary Key |
| username | VARCHAR2(50) | Unique username |
| email | VARCHAR2(100) | Unique email |
| password_hash | VARCHAR2(255) | BCrypt hashed password |
| role | VARCHAR2(20) | USER / ADMIN |
| active | NUMBER(1) | Is account active? |
| created_at | TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | Last update timestamp |

### service-account (TOYCELL_ACCOUNT)

**Table: USER_PROFILES**
| Column | Type | Description |
|--------|------|-------------|
| id | NUMBER | Primary Key |
| user_id | NUMBER | FK to auth.users |
| first_name | VARCHAR2(100) | First name |
| last_name | VARCHAR2(100) | Last name |
| identity_number | VARCHAR2(500) | **Encrypted** TCKN |
| phone_number | VARCHAR2(500) | **Encrypted** phone |
| birth_date | DATE | Date of birth |
| is_verified | NUMBER(1) | Is profile verified? |
| created_at | TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | Last update timestamp |

---

## 🎯 Development Status

### ✅ Phase 0: Foundation (COMPLETED)
- [x] Gradle multi-module setup
- [x] Common libraries structure
- [x] Git repository initialization

### ✅ Phase 1: Core Libraries (COMPLETED)
- [x] `common-domain` - BaseEntity, Enums, Response wrappers
- [x] `common-exception` - GlobalExceptionHandler, ErrorCodes
- [x] `common-encrypt` - AES-256-CBC encryption with IV

### ✅ Phase 2: Base Services (COMPLETED)
- [x] `service-auth` - Register, Login, JWT
- [x] `service-account` - Profile CRUD with encryption
- [x] `api-gateway` - Route configuration

### 🚧 Phase 3: Business Engine (IN PROGRESS)
- [ ] `service-balance` - Wallet management
- [ ] `service-fee` - Fee calculation

### 📋 Phase 4: Orchestration (PLANNED)
- [ ] `service-transfer` - Transfer orchestration + manual rollback
- [ ] `service-transaction` - Transaction logging

### 📋 Phase 5: Testing & Documentation (PLANNED)
- [ ] Postman collection
- [ ] Swagger UI integration
- [ ] Integration tests

---

## 🐛 Troubleshooting

### Issue: "Connection refused" when calling services

**Solution:** Ensure the service is running on the correct port:
```bash
netstat -ano | findstr :8081
```

### Issue: "Invalid or expired token"

**Solution:** 
1. Check that JWT secret keys match in all services
2. Verify token hasn't expired (24h default)
3. Ensure `Authorization: Bearer <token>` header is correct

### Issue: "Encryption failed"

**Solution:**
1. Verify `encryption.secret.key` is set (min 32 chars)
2. Check that `common-encrypt` is in ComponentScan
3. Ensure `@Convert(converter = EncryptedStringConverter.class)` is on the field

---

## 📝 License

This project is created for **educational purposes** as part of an internship program.

---

## 👥 Contributors

- **Developer:** Intern at [Company Name]
- **Mentor:** [Mentor Name]
- **AI Assistant:** GitHub Copilot

---

## 📞 Contact

For questions or issues, please create an issue in the repository or contact the development team.

---

**Last Updated:** 2026-02-03  
**Version:** 1.0-SNAPSHOT  
**Status:** 🚧 Active Development
