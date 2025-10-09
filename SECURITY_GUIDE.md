# Eventified Backend - Security Configuration Guide

This project supports **three different security modes** to accommodate different development and testing scenarios:

## 🛠️ Available Profiles

### 1. **Development Mode** (`dev` profile) - **Security DISABLED**
**Use this for:** Backend development and testing without authentication hassles

```bash
# Run with development profile
set SPRING_PROFILES_ACTIVE=dev && mvn spring-boot:run

# Or on Linux/Mac
export SPRING_PROFILES_ACTIVE=dev && mvn spring-boot:run

# Or use the batch script on Windows
start.cmd
```

**Features:**
- ✅ All endpoints accessible without authentication
- ✅ Swagger UI available at: `http://localhost:8080/swagger-ui.html`
- ✅ Development status endpoint: `http://localhost:8080/api/dev/status`
- ✅ Database SQL logging enabled
- ✅ No JWT token required

### 2. **Frontend Mode** (`frontend` profile) - **Security ENABLED**
**Use this for:** Frontend team testing with JWT authentication

```bash
# Run with frontend profile
set SPRING_PROFILES_ACTIVE=frontend && mvn spring-boot:run
```

**Features:**
- 🔐 JWT authentication enabled
- ✅ CORS configured for common frontend ports (3000, 3001, 4200, 5173)
- ✅ Longer JWT expiration (24 hours) for easier testing
- ✅ Swagger UI available with JWT authentication
- ✅ Test JWT tokens via `/api/auth/login`

### 3. **Production Mode** (`prod` profile) - **Security ENABLED**
**Use this for:** Production deployment

```bash
# Run with production profile
set SPRING_PROFILES_ACTIVE=prod && mvn spring-boot:run
```

**Features:**
- 🔐 Full JWT security
- 🔒 Shorter JWT expiration (1 hour)
- 🔒 Production database configuration
- 🔒 SQL logging disabled

## 🚀 Quick Start Guide

### For Backend Developers (You)
```bash
# 1. Start in development mode (no authentication)
set SPRING_PROFILES_ACTIVE=dev && mvn spring-boot:run

# 2. Test your endpoints freely
curl http://localhost:8080/api/dev/status
curl http://localhost:8080/api/user/...
curl http://localhost:8080/api/events/...

# 3. Use Swagger UI for testing
# Open: http://localhost:8080/swagger-ui.html
```

### For Frontend Developers (Your Colleagues)
```bash
# 1. Start in frontend mode (with JWT)
set SPRING_PROFILES_ACTIVE=frontend && mvn spring-boot:run

# 2. Register a test user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "email": "test@example.com", "password": "password123"}'

# 3. Login to get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'

# 4. Use the JWT token in subsequent requests
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 🔧 Environment Variables

Create a `.env` file or set these environment variables:

```env
# Database Configuration
DB_USER=your_db_username
DB_PASS=your_db_password
DB_HOST=localhost  # for prod
DB_PORT=5432       # for prod

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-here-at-least-256-bits
JWT_EXPIRATION=3600000  # 1 hour in milliseconds
```

## 📋 Security Endpoints Overview

### Public Endpoints (Always accessible):
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login (returns JWT)
- `GET /swagger-ui.html` - API documentation
- `GET /v3/api-docs` - OpenAPI specification

### Protected Endpoints (JWT required in frontend/prod modes):
- `GET /api/user/**` - User operations (USER or ADMIN role)
- `GET /api/events/**` - Event operations (USER or ADMIN role)
- `GET /api/admin/**` - Admin operations (ADMIN role only)

### Development-only Endpoints (dev mode only):
- `GET /api/dev/status` - Backend status
- `GET /api/dev/endpoints` - Available endpoints info

## 🔍 Testing JWT Authentication

### Using Swagger UI (Frontend/Prod modes):
1. Go to `http://localhost:8080/swagger-ui.html`
2. Click on "Authorize" button (🔒 icon)
3. Enter your JWT token (without "Bearer " prefix)
4. Test protected endpoints

### Using curl:
```bash
# Get JWT token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}' | jq -r '.token')

# Use token in requests
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer $TOKEN"
```

## 🐛 Troubleshooting

### JWT Token Issues:
- Ensure token is included in `Authorization: Bearer <token>` header
- Check token expiration (1 hour in prod, 24 hours in frontend mode)
- Verify user exists and is verified in database

### CORS Issues:
- Frontend mode includes common development ports
- Add your frontend URL to `application-frontend.yml` if needed

### Database Issues:
- Ensure PostgreSQL is running
- Check database credentials in environment variables
- Run Flyway migrations: `mvn flyway:migrate`

## 📝 Notes

- **Development mode** is perfect for backend API development and testing
- **Frontend mode** simulates production security while being frontend-friendly
- **Production mode** should be used for actual deployment
- JWT tokens contain user roles (USER/ADMIN) for authorization
- All passwords are hashed with BCrypt
- Sessions are stateless (JWT-based)