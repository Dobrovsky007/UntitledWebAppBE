# Eventified Backend - Security Guide

Simple guide for running your Spring Boot backend with different security modes.

## Quick Start

### Backend Development Mode
```powershell
# JWT authentication enabled with longer token expiry for easier development
$env:SPRING_PROFILES_ACTIVE="dev"
mvn spring-boot:run
```
- JWT authentication enabled (required for user identification)
- Longer token expiration (24 hours) for easier development
- Debug logging enabled
- SQL query logging enabled

### Frontend Testing Mode  
```powershell
# JWT authentication enabled for frontend team testing
$env:SPRING_PROFILES_ACTIVE="frontend" 
mvn spring-boot:run
```
- JWT authentication enabled
- CORS configured for frontend development ports
- 24-hour token expiration for easier testing
- Optimized for frontend integration

### Production Mode
```powershell
# Full security configuration
$env:SPRING_PROFILES_ACTIVE="prod"
mvn spring-boot:run
```
- JWT authentication enabled
- 1-hour token expiration
- Production database settings
- Minimal logging

## Testing with Postman

### Step 1: Register User
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com", 
  "password": "password123"
}
```

### Step 2: Login and Get Token
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```
**Response:** `{"token": "eyJhbGciOiJIUzUxMiJ9..."}`

### Step 3: Use Token in Requests
```
POST http://localhost:8080/api/user/sport/add
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json

{
  "sport": 1,
  "skillLevel": 2
}
```

## Profile Comparison

| Mode | JWT Required | Token Expiry | Best For |
|------|--------------|--------------|----------|
| dev | Yes | 24 hours | Backend development |
| frontend | Yes | 24 hours | Frontend team testing |
| prod | Yes | 1 hour | Production |

## Available Endpoints

### Public Endpoints (no token required):
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - Get JWT token
- `GET /swagger-ui.html` - API documentation

### Protected Endpoints (JWT token required):
- `POST /api/user/sport/add` - Add sport to user
- `GET /api/user/profile` - Get user profile
- `DELETE /api/user/sport/remove` - Remove user sport
- All `/api/user/**` endpoints
- All `/api/admin/**` endpoints (admin role required)

## Common Issues

**401 Unauthorized Error:**
- Check Authorization header format: `Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...`
- Ensure there's a space after "Bearer"
- Token might be expired - login again

**500 Internal Server Error:**
- Check backend console logs for detailed error message
- Verify database connection
- Check if required data exists (sports table)

**Token Expired:**
- dev/frontend modes: 24 hour expiration
- prod mode: 1 hour expiration
- Login again to get fresh token

**Wrong Profile Active:**
- Check startup logs for: "The following 1 profile is active: [profile_name]"
- If wrong profile, stop app and restart with correct environment variable

## Environment Setup

Your `.env` file is already configured with:
- Database connection (PostgreSQL)
- JWT secret key
- Token expiration settings

All modes require JWT authentication because your backend uses JWT tokens to identify users for database operations.

## Testing Tips

- Use Swagger UI for quick testing: `http://localhost:8080/swagger-ui.html`
- In Swagger, click "Authorize" and enter: `Bearer YOUR_TOKEN`
- Always check startup logs to confirm active profile
- Keep your JWT token handy for repeated testing
- Use Postman collections to save and reuse requests