# 🏦 Toycell Backend - Dijital Cüzdan Fintech Uygulaması

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-blue.svg)](https://gradle.org/)
[![Oracle](https://img.shields.io/badge/Oracle-21c%20XE-red.svg)](https://www.oracle.com/database/technologies/xe-downloads.html)

## 📖 Proje Hakkında

Toycell Backend, **mikroservis mimarisi** ile geliştirilmiş kapsamlı bir dijital cüzdan ve fintech uygulamasıdır. Kullanıcıların para yatırma, çekme, transfer işlemleri yapabildiği, komisyon hesaplama ve işlem geçmişi takibi yapılabilen güvenli bir platform sunar.

### 🎯 Temel Özellikler

- ✅ **Kullanıcı Yönetimi**: Kayıt, giriş, profil oluşturma ve KYC doğrulama
- 💼 **Dijital Cüzdan**: Çoklu para birimi desteği (TRY, USD, EUR)
- 💸 **Para İşlemleri**: Yatırma (deposit), çekme (withdraw), transfer
- 📊 **Komisyon Sistemi**: İşlem tipine ve tutara göre dinamik komisyon hesaplama
- 📜 **İşlem Geçmişi**: Detaylı transaction kayıtları ve sayfalama desteği
- 🔒 **Güvenlik**: JWT tabanlı kimlik doğrulama, AES-256 şifreleme

### 🏗️ Mimari Yapı

Proje mikroservis mimarisi kullanır ve 7 ana servisten oluşur:

```
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway :8080                       │
└──────────────┬──────────────────────────────────────────────┘
               │
    ┌──────────┼──────────┬──────────┬──────────┬─────────┐
    │          │          │          │          │         │
┌───▼───┐  ┌──▼───┐  ┌───▼───┐  ┌──▼───┐  ┌───▼────┐ ┌──▼────┐
│ Auth  │  │Account│ │Balance│  │ Fee  │  │Transfer│ │Transaction│
│ :8081 │  │ :8082 │ │ :8083 │  │:8084 │  │ :8087  │ │ :8086 │
└───────┘  └───────┘ └───────┘  └──────┘  └────────┘ └───────┘
```

## 📦 Servisler ve Endpointler

### 🔐 Service-Auth (Port 8081)
**Görev**: Kullanıcı kimlik doğrulama ve JWT token yönetimi

| Method | Endpoint | Açıklama | Auth Gerekli |
|--------|----------|----------|--------------|
| POST | `/api/auth/register` | Yeni kullanıcı kaydı | ❌ |
| POST | `/api/auth/login` | Kullanıcı girişi ve token alma | ❌ |
| GET | `/actuator/health` | Servis sağlık kontrolü | ❌ |

**Request Örnekleri:**

```json
// POST /api/auth/register
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "Test123!"
}

// POST /api/auth/login
{
  "email": "test@example.com",
  "password": "Test123!"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "expiresIn": 86400,
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

---

### 👤 Service-Account (Port 8082)
**Görev**: Kullanıcı profil yönetimi ve KYC bilgileri

| Method | Endpoint | Açıklama | Auth Gerekli |
|--------|----------|----------|--------------|
| POST | `/api/profile` | Profil oluşturma | ✅ |
| GET | `/api/profile/me` | Kendi profilini görüntüleme | ✅ |
| PUT | `/api/profile/me` | Profil güncelleme | ✅ |
| GET | `/actuator/health` | Servis sağlık kontrolü | ❌ |

**Request Örneği:**

```json
// POST /api/profile
{
  "firstName": "Ahmet",
  "lastName": "Yılmaz",
  "phoneNumber": "+905551234567",
  "identityNumber": "12345678901",
  "birthDate": "1990-05-15"
}
```

**Not**: `phoneNumber` ve `identityNumber` AES-256 ile şifrelenerek saklanır.

---

### 💰 Service-Balance (Port 8083)
**Görev**: Cüzdan yönetimi ve bakiye işlemleri

| Method | Endpoint | Açıklama | Auth Gerekli |
|--------|----------|----------|--------------|
| GET | `/api/wallets/my` | Kullanıcının tüm cüzdanlarını listele | ✅ |
| GET | `/api/wallets/my/{currency}` | Belirli para birimindeki cüzdan | ✅ |
| POST | `/api/wallets/deposit` | Para yatırma | ✅ |
| POST | `/api/wallets/withdraw` | Para çekme | ✅ |
| GET | `/api/wallets/internal/{walletId}` | İç servis çağrısı (internal) | ✅ |
| GET | `/actuator/health` | Servis sağlık kontrolü | ❌ |

**Request Örnekleri:**

```json
// POST /api/wallets/deposit
{
  "walletId": 1,
  "amount": 1000.00,
  "currency": "TRY",
  "description": "Maaş yatırımı"
}

// POST /api/wallets/withdraw
{
  "walletId": 1,
  "amount": 500.00,
  "currency": "TRY",
  "description": "ATM'den çekim"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "userId": 1,
    "currency": "TRY",
    "balance": 1500.00,
    "active": true,
    "createdAt": "2026-02-03T10:00:00",
    "updatedAt": "2026-02-03T14:30:00"
  }
}
```

---

### 💵 Service-Fee (Port 8084)
**Görev**: Komisyon kuralları ve hesaplama

| Method | Endpoint | Açıklama | Auth Gerekli |
|--------|----------|----------|--------------|
| POST | `/api/fees/rules` | Yeni komisyon kuralı oluştur | ✅ (Admin) |
| GET | `/api/fees/rules` | Tüm kuralları listele | ✅ |
| GET | `/api/fees/rules/{id}` | Belirli kuralı getir | ✅ |
| PUT | `/api/fees/rules/{id}` | Kural güncelle | ✅ (Admin) |
| GET | `/api/fees/calculate?amount={amount}&currency={currency}&type={type}` | Komisyon hesapla | ✅ |
| GET | `/actuator/health` | Servis sağlık kontrolü | ❌ |

**Request Örneği:**

```json
// POST /api/fees/rules
{
  "transactionType": "TRANSFER",
  "currency": "TRY",
  "feeType": "PERCENTAGE",
  "feeValue": 0.5,
  "minFee": 1.0,
  "maxFee": 10.0,
  "active": true
}
```

**Komisyon Hesaplama:**
```
GET /api/fees/calculate?amount=500&currency=TRY&type=TRANSFER

Response:
{
  "success": true,
  "data": {
    "amount": 500.00,
    "feeAmount": 2.50,
    "totalAmount": 502.50,
    "currency": "TRY",
    "appliedRule": {
      "feeType": "PERCENTAGE",
      "feeValue": 0.5
    }
  }
}
```

---

### 🔄 Service-Transfer (Port 8087)
**Görev**: Kullanıcılar arası para transferi orkestasyonu

| Method | Endpoint | Açıklama | Auth Gerekli |
|--------|----------|----------|--------------|
| POST | `/api/transfers` | Para transferi yap | ✅ |
| GET | `/api/transfers/my` | Transfer geçmişim | ✅ |
| GET | `/api/transfers/{id}` | Transfer detayı | ✅ |
| GET | `/actuator/health` | Servis sağlık kontrolü | ❌ |

**Request Örneği:**

```json
// POST /api/transfers
{
  "senderWalletId": 1,
  "receiverWalletId": 2,
  "amount": 500.00,
  "currency": "TRY",
  "description": "Borç ödeme"
}
```

**Transfer İşlem Akışı:**
1. ✅ Gönderen cüzdanı doğrula
2. ✅ Alıcı cüzdanı doğrula
3. 💵 Komisyon hesapla
4. 💸 Gönderende para çek (withdraw)
5. 💰 Alıcıya para yatır (deposit)
6. 📝 Transaction kayıtları oluştur
7. ❌ Hata durumunda manuel rollback

---

### 📜 Service-Transaction (Port 8086)
**Görev**: Tüm işlem kayıtları ve geçmiş

| Method | Endpoint | Açıklama | Auth Gerekli |
|--------|----------|----------|--------------|
| POST | `/api/transactions` | Yeni transaction kaydı | ✅ (Internal) |
| GET | `/api/transactions/my` | Kendi işlem geçmişim | ✅ |
| GET | `/api/transactions/my?page=0&size=20` | Sayfalı işlem geçmişi | ✅ |
| GET | `/api/transactions/{id}` | Belirli transaction detayı | ✅ |
| GET | `/actuator/health` | Servis sağlık kontrolü | ❌ |

**Response Örneği:**

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "walletId": 1,
        "type": "DEPOSIT",
        "amount": 1000.00,
        "currency": "TRY",
        "balanceBefore": 0.00,
        "balanceAfter": 1000.00,
        "description": "Para yatırma",
        "createdAt": "2026-02-03T10:00:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

### 🌐 API Gateway (Port 8080)
**Görev**: Tüm servislere tek giriş noktası

API Gateway, tüm istekleri ilgili mikroservislere yönlendirir:

```
http://localhost:8080/api/auth/*      → service-auth:8081
http://localhost:8080/api/profile/*   → service-account:8082
http://localhost:8080/api/wallets/*   → service-balance:8083
http://localhost:8080/api/fees/*      → service-fee:8084
http://localhost:8080/api/transfers/* → service-transfer:8087
http://localhost:8080/api/transactions/* → service-transaction:8086
```

---

## 🗂️ Ortak Modüller

### 📦 common-domain
Tüm servislerde kullanılan ortak domain sınıfları:

- `BaseEntity` - Temel entity sınıfı (id, createdAt, updatedAt)
- `Currency` - Para birimi enum (TRY, USD, EUR)
- `TransactionType` - İşlem tipi enum (DEPOSIT, WITHDRAW, TRANSFER)
- `ApiResponse<T>` - Standart API yanıt wrapper'ı

### 🔒 common-encrypt
AES-256-CBC şifreleme yardımcıları:

- `AesEncryptionUtil` - Şifreleme/deşifreleme işlemleri
- `EncryptedStringConverter` - JPA @Convert desteği

**Şifrelenen Alanlar:**
- TC Kimlik No (identityNumber)
- Telefon Numarası (phoneNumber)

### ⚠️ common-exception
Global hata yönetimi:

- `GlobalExceptionHandler` - Tüm servisler için merkezi exception handler
- `BusinessException` - İş kuralı hataları
- `ResourceNotFoundException` - Kaynak bulunamadı hataları
- `ValidationException` - Validasyon hataları
- `UnauthorizedException` - Yetkilendirme hataları

**Standart Hata Yanıtı:**
```json
{
  "success": false,
  "errorCode": "WALLET_001",
  "message": "Insufficient balance",
  "details": "Requested: 1000.00 TRY, Available: 500.00 TRY",
  "validationErrors": null,
  "timestamp": "2026-02-03T14:30:00"
}
```

---

## 🚀 Kurulum ve Çalıştırma

### 1️⃣ Gereksinimler

- ☕ **Java 17** (Amazon Corretto önerilir)
- 🗄️ **Oracle 21c XE**
- 🔧 **Gradle** (wrapper ile birlikte gelir)
- 🛠️ **DBeaver** (veritabanı yönetimi için)
- 📮 **Postman** (API testleri için)

### 2️⃣ Veritabanı Kurulumu

DBeaver'da **SYSTEM** kullanıcısı ile bağlanın ve şu SQL'leri çalıştırın:

```sql
-- Auth Servisi
CREATE USER TOYCELL_AUTH IDENTIFIED BY "Auth2026!";
GRANT CONNECT, RESOURCE TO TOYCELL_AUTH;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW TO TOYCELL_AUTH;
ALTER USER TOYCELL_AUTH QUOTA UNLIMITED ON USERS;

-- Account Servisi
CREATE USER TOYCELL_ACCOUNT IDENTIFIED BY "Account2026!";
GRANT CONNECT, RESOURCE TO TOYCELL_ACCOUNT;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW TO TOYCELL_ACCOUNT;
ALTER USER TOYCELL_ACCOUNT QUOTA UNLIMITED ON USERS;

-- Balance Servisi
CREATE USER TOYCELL_BALANCE IDENTIFIED BY "Balance2026!";
GRANT CONNECT, RESOURCE TO TOYCELL_BALANCE;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW TO TOYCELL_BALANCE;
ALTER USER TOYCELL_BALANCE QUOTA UNLIMITED ON USERS;

-- Fee Servisi
CREATE USER TOYCELL_FEE IDENTIFIED BY "Fee2026!";
GRANT CONNECT, RESOURCE TO TOYCELL_FEE;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW TO TOYCELL_FEE;
ALTER USER TOYCELL_FEE QUOTA UNLIMITED ON USERS;

-- Transaction Servisi
CREATE USER TOYCELL_TRANSACTION IDENTIFIED BY "Transaction2026!";
GRANT CONNECT, RESOURCE TO TOYCELL_TRANSACTION;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW TO TOYCELL_TRANSACTION;
ALTER USER TOYCELL_TRANSACTION QUOTA UNLIMITED ON USERS;
```

**Not**: Transfer servisi kendi tablosu olmadığı için schema oluşturmaya gerek yoktur.

### 3️⃣ Environment Variables

`.env.example` dosyasını `.env` olarak kopyalayın:

```cmd
copy .env.example .env
```

`.env` dosyasını düzenleyin:

```properties
# Veritabanı Şifreleri
DB_PASSWORD_AUTH=Auth2026!
DB_PASSWORD_ACCOUNT=Account2026!
DB_PASSWORD_BALANCE=Balance2026!
DB_PASSWORD_FEE=Fee2026!
DB_PASSWORD_TRANSACTION=Transaction2026!

# JWT Secret (En az 256 bit)
JWT_SECRET_KEY=YourSuperSecretJWTKeyAtLeast32CharsLong123456789

# Şifreleme Anahtarı (Tam 32 karakter)
ENCRYPTION_SECRET_KEY=YourEncryptionKey32Bytes_____
```

### 4️⃣ Servisleri Başlatma

**launcher.bat** kullanarak:

```cmd
launcher.bat
```

Menüden seçenekler:
- `1` - Auth servisini başlat
- `2` - Account servisini başlat
- `3` - Balance servisini başlat
- `4` - Fee servisini başlat
- `5` - Transaction servisini başlat
- `6` - Transfer servisini başlat
- `7` - API Gateway'i başlat
- `8` - Tüm servisleri başlat ⭐
- `0` - Çıkış

**Veya manuel olarak:**

```cmd
# Terminal 1
gradlew :service-auth:bootRun

# Terminal 2
gradlew :service-account:bootRun

# Terminal 3
gradlew :service-balance:bootRun

# Terminal 4
gradlew :service-fee:bootRun

# Terminal 5
gradlew :service-transaction:bootRun

# Terminal 6
gradlew :service-transfer:bootRun

# Terminal 7
gradlew :api-gateway:bootRun
```

### 5️⃣ Sağlık Kontrolü

Tüm servislerin çalıştığından emin olun:

```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
curl http://localhost:8086/actuator/health
curl http://localhost:8087/actuator/health
```

---

## 📝 Kullanım Senaryosu (End-to-End)

### Adım 1: Kullanıcı Kaydı

```bash
POST http://localhost:8080/api/auth/register
{
  "username": "ahmet123",
  "email": "ahmet@example.com",
  "password": "Ahmet123!"
}
```

**Token'ı kaydedin**: `eyJhbGciOiJIUzI1NiJ9...`

### Adım 2: Profil Oluşturma

```bash
POST http://localhost:8080/api/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
{
  "firstName": "Ahmet",
  "lastName": "Yılmaz",
  "phoneNumber": "+905551234567",
  "identityNumber": "12345678901",
  "birthDate": "1990-05-15"
}
```

### Adım 3: Cüzdanları Kontrol Et

```bash
GET http://localhost:8080/api/wallets/my
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Otomatik olarak TRY, USD, EUR cüzdanları oluşturulur.

### Adım 4: Para Yatır

```bash
POST http://localhost:8080/api/wallets/deposit
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
{
  "walletId": 1,
  "amount": 5000.00,
  "currency": "TRY",
  "description": "İlk para yatırma"
}
```

### Adım 5: Transfer Yap

```bash
POST http://localhost:8080/api/transfers
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
{
  "senderWalletId": 1,
  "receiverWalletId": 2,
  "amount": 1000.00,
  "currency": "TRY",
  "description": "Arkadaşa transfer"
}
```

### Adım 6: İşlem Geçmişi

```bash
GET http://localhost:8080/api/transactions/my?page=0&size=20
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 🔐 Güvenlik Özellikleri

### 1. JWT Authentication
- **Algoritma**: HS256
- **Token Süresi**: 24 saat
- **Claims**: userId, email, username, role
- Her korumalı endpoint'te token doğrulama

### 2. Password Security
- **BCrypt** hashing (10 rounds)
- Salt otomatik oluşturulur
- Plain text şifre asla saklanmaz

### 3. Data Encryption (AES-256-CBC)
- **Şifrelenen Alanlar**: TC Kimlik No, Telefon
- **IV**: Random 16-byte initialization vector
- **Encoding**: Base64
- **JPA Converter**: Otomatik encrypt/decrypt

### 4. CORS Policy
- API Gateway'de merkezi CORS yapılandırması
- Sadece belirli origin'lere izin

### 5. Input Validation
- `@Valid` ve `@Validated` annotations
- Custom validators (TCKN, telefon)
- SQL injection koruması

---

## 🗄️ Veritabanı Şeması

### USERS (TOYCELL_AUTH)
```sql
CREATE TABLE users (
    id NUMBER PRIMARY KEY,
    username VARCHAR2(50) UNIQUE NOT NULL,
    email VARCHAR2(100) UNIQUE NOT NULL,
    password_hash VARCHAR2(255) NOT NULL,
    role VARCHAR2(20) DEFAULT 'USER',
    active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### USER_PROFILES (TOYCELL_ACCOUNT)
```sql
CREATE TABLE user_profiles (
    id NUMBER PRIMARY KEY,
    user_id NUMBER UNIQUE NOT NULL,
    first_name VARCHAR2(100) NOT NULL,
    last_name VARCHAR2(100) NOT NULL,
    identity_number VARCHAR2(500) NOT NULL, -- ENCRYPTED
    phone_number VARCHAR2(500) NOT NULL,    -- ENCRYPTED
    birth_date DATE NOT NULL,
    is_verified NUMBER(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### WALLETS (TOYCELL_BALANCE)
```sql
CREATE TABLE wallets (
    id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    currency VARCHAR2(3) NOT NULL,
    balance NUMBER(19,2) DEFAULT 0.00,
    active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, currency)
);
```

### FEE_RULES (TOYCELL_FEE)
```sql
CREATE TABLE fee_rules (
    id NUMBER PRIMARY KEY,
    transaction_type VARCHAR2(20) NOT NULL,
    currency VARCHAR2(3) NOT NULL,
    fee_type VARCHAR2(20) NOT NULL,
    fee_value NUMBER(19,2) NOT NULL,
    min_fee NUMBER(19,2),
    max_fee NUMBER(19,2),
    active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### TRANSACTIONS (TOYCELL_TRANSACTION)
```sql
CREATE TABLE transactions (
    id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    wallet_id NUMBER NOT NULL,
    related_user_id NUMBER,
    type VARCHAR2(20) NOT NULL,
    amount NUMBER(19,2) NOT NULL,
    currency VARCHAR2(3) NOT NULL,
    balance_before NUMBER(19,2) NOT NULL,
    balance_after NUMBER(19,2) NOT NULL,
    reference_id VARCHAR2(100),
    description VARCHAR2(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
---

## 📊 Teknoloji Stack'i

| Katman | Teknoloji | Versiyon | Açıklama |
|--------|-----------|----------|----------|
| **Language** | Java | 17 | Amazon Corretto |
| **Framework** | Spring Boot | 3.2.2 | Ana framework |
| **Gateway** | Spring Cloud Gateway | 2023.0.0 | API Gateway |
| **Communication** | OpenFeign | 4.1.0 | Servisler arası iletişim |
| **Security** | Spring Security | 6.2.1 | Kimlik doğrulama |
| **JWT** | JJWT | 0.12.3 | Token yönetimi |
| **Database** | Oracle | 21c XE | İlişkisel veritabanı |
| **ORM** | Spring Data JPA | 3.2.2 | Veri erişim katmanı |
| **Encryption** | AES-256-CBC | - | Veri şifreleme |
| **Build Tool** | Gradle | 8.x | Proje yönetimi |
| **Utilities** | Lombok | 1.18.30 | Boilerplate azaltma |

---
