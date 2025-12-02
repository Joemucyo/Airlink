# Spring Security & JWT Implementation Summary

## Overview
A complete Spring Security with JWT (JSON Web Token) authentication system has been successfully implemented for the Air Ticketing System backend.

---

## What Was Implemented

### 1. **Maven Dependencies** ✅
Added to `pom.xml`:
- Spring Security (`spring-boot-starter-security`)
- JJWT for JWT handling:
  - `jjwt-api`
  - `jjwt-impl`
  - `jjwt-jackson`

### 2. **User Model Enhancements** ✅
Updated `User.java` with:
- `password` (String) - BCrypt encrypted
- `isActive` (Boolean) - Account status, default `true`
- `createdAt` (Date) - Timestamp, auto-set on creation
- `updatedAt` (Date) - Timestamp, updated on modification
- `refreshTokens` (List<RefreshToken>) - One-to-many relationship

### 3. **New Entity: RefreshToken** ✅
Created `RefreshToken.java` with:
- `id` (Long) - Primary key
- `token` (String) - Unique refresh token
- `user` (User) - ManyToOne relationship
- `expiryDate` (Instant) - Token expiration time
- `isExpired()` - Helper method to check expiration

### 4. **Security Data Transfer Objects** ✅
Created:
- `LoginRequest.java` - Email and password
- `AuthRegisterRequest.java` - Full name, email, password, confirmation
- `AuthResponse.java` - Access token, refresh token, expiration, user info
- `ChangePasswordRequest.java` - Old password, new password, confirmation
- `RefreshTokenRequest.java` - Refresh token for token refresh

### 5. **JWT Service** ✅
Created `JwtService.java` with methods:
- `generateToken(UserDetails)` - Generate access token (24 hours)
- `generateRefreshToken(UserDetails)` - Generate refresh token (7 days)
- `extractUsername(String token)` - Extract username from token
- `extractExpiration(String token)` - Extract expiration time
- `validateToken(String token)` - Validate token signature and expiration
- `getExpirationTime()` - Get access token expiration time
- `getRefreshTokenExpirationTime()` - Get refresh token expiration time

### 6. **Custom User Details Service** ✅
Created `CustomUserDetailsService.java`:
- Implements `UserDetailsService`
- Loads user by email
- Checks if user is active
- Converts User entity to Spring Security UserDetails
- Includes role-based authority

### 7. **JWT Authentication Filter** ✅
Created `JwtAuthenticationFilter.java`:
- Extends `OncePerRequestFilter`
- Extracts JWT from Authorization header
- Validates token
- Sets authentication in SecurityContext
- Executes once per request

### 8. **Exception Handlers** ✅
Created:
- `JwtAuthenticationEntryPoint.java` - Handles 401 Unauthorized responses
- `JwtAccessDeniedHandler.java` - Handles 403 Forbidden responses
- Both return JSON error responses

### 9. **Authentication Service** ✅
Created `AuthService.java` with methods:
- `register(AuthRegisterRequest)` - Register new user (CUSTOMER role)
  - Validates email uniqueness
  - Validates password confirmation
  - Encrypts password with BCrypt
  - Generates and saves refresh token
  - Returns AuthResponse with tokens and user info
  
- `login(LoginRequest)` - Authenticate user
  - Uses AuthenticationManager
  - Generates access and refresh tokens
  - Saves refresh token
  - Returns AuthResponse
  
- `refreshToken(RefreshTokenRequest)` - Get new access token
  - Validates refresh token exists and not expired
  - Checks user is active
  - Generates new access and refresh tokens
  - Returns AuthResponse
  
- `logout(String refreshToken)` - Invalidate refresh token
  - Deletes refresh token from database
  
- `getCurrentUser()` - Get authenticated user details
  - Retrieves user from security context
  - Returns UserResponseDTO
  
- `changePassword(String email, ChangePasswordRequest)` - Change user password
  - Validates old password
  - Validates new passwords match
  - Encrypts and updates password

### 10. **Authentication Controller** ✅
Created `AuthController.java` with endpoints:
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - Logout
- `GET /api/auth/me` - Get current user
- `POST /api/auth/change-password` - Change password

### 11. **Security Configuration** ✅
Created `SecurityConfig.java` with:
- Password encoder (BCrypt)
- Authentication provider setup
- CORS configuration (allows all origins, configurable)
- CSRF disabled (using JWT)
- Stateless session management
- Authorization rules:

#### Public Endpoints (No Auth Required):
- `/api/auth/**` - All auth endpoints
- `/api/flights/search**` - Flight search
- `/api/airports/**` - Airport information
- `/api/locations/**` - Location information
- `/swagger-ui/**`, `/api-docs/**` - API documentation

#### Customer Role (CUSTOMER, AGENT, ADMIN):
- `GET /api/bookings/**` - View bookings
- `POST /api/bookings` - Create bookings
- `GET /api/passengers/**` - View passengers
- `POST /api/passengers` - Create passengers
- `GET /api/payments/**` - View payments
- `POST /api/payments` - Create payments

#### Agent Role (AGENT, ADMIN):
- `POST /api/flights` - Create flights
- `PUT /api/flights/**` - Update flights
- `GET /api/bookings` - View all bookings

#### Admin Only (ADMIN):
- `DELETE /api/flights/**` - Delete flights
- `/api/users/**` - All user management
- `POST /api/airports` - Create airports
- `PUT /api/airports/**` - Update airports
- `DELETE /api/airports/**` - Delete airports

### 12. **JWT Configuration** ✅
Added to `application.properties`:
```properties
jwt.secret=JWTSecretKeyForAirtaketingSystemJWTSecretKeyForAirtaketingSystemJWTSecretKeyForAirtaketingSystem
jwt.expiration=86400000 (24 hours)
jwt.refresh-expiration=604800000 (7 days)
```

### 13. **Repository** ✅
Created `RefreshTokenRepository.java`:
- `findByToken(String token)` - Find refresh token by value
- `deleteByUser(User user)` - Delete all tokens for user
- `deleteByUserAndToken(User user, String token)` - Delete specific token

### 14. **Password Requirements** ✅
Implemented validation for:
- Minimum 8 characters
- At least one uppercase letter (A-Z)
- At least one lowercase letter (a-z)
- At least one digit (0-9)
- At least one special character (@$!%*?&)

### 15. **Exception Handling** ✅
Updated `GlobalExceptionHandler.java` to handle:
- `UsernameNotFoundException` - 401 Unauthorized
- `BadRequestException` - 400 Bad Request
- Validation errors - 400 Bad Request
- Generic exceptions - 500 Internal Server Error

---

## Security Features

✅ **JWT-based stateless authentication**
✅ **BCrypt password encryption**
✅ **Access token expiration (24 hours)**
✅ **Refresh token support (7 days)**
✅ **Role-based access control (ADMIN, AGENT, CUSTOMER)**
✅ **CORS configuration**
✅ **CSRF protection disabled (JWT-based)**
✅ **Stateless session management**
✅ **Custom authentication entry points**
✅ **Password strength validation**
✅ **Account active/inactive status**
✅ **Audit timestamps (createdAt, updatedAt)**
✅ **Secure token extraction from headers**

---

## Files Created/Modified

### Created Files:
1. `src/main/java/com/Airlink/AirticketingSystem/model/RefreshToken.java`
2. `src/main/java/com/Airlink/AirticketingSystem/repository/RefreshTokenRepository.java`
3. `src/main/java/com/Airlink/AirticketingSystem/dto/LoginRequest.java`
4. `src/main/java/com/Airlink/AirticketingSystem/dto/AuthRegisterRequest.java`
5. `src/main/java/com/Airlink/AirticketingSystem/dto/AuthResponse.java`
6. `src/main/java/com/Airlink/AirticketingSystem/dto/ChangePasswordRequest.java`
7. `src/main/java/com/Airlink/AirticketingSystem/dto/RefreshTokenRequest.java`
8. `src/main/java/com/Airlink/AirticketingSystem/security/JwtService.java`
9. `src/main/java/com/Airlink/AirticketingSystem/security/CustomUserDetailsService.java`
10. `src/main/java/com/Airlink/AirticketingSystem/security/JwtAuthenticationFilter.java`
11. `src/main/java/com/Airlink/AirticketingSystem/security/JwtAuthenticationEntryPoint.java`
12. `src/main/java/com/Airlink/AirticketingSystem/security/JwtAccessDeniedHandler.java`
13. `src/main/java/com/Airlink/AirticketingSystem/service/AuthService.java`
14. `src/main/java/com/Airlink/AirticketingSystem/controller/AuthController.java`
15. `src/main/java/com/Airlink/AirticketingSystem/config/SecurityConfig.java`
16. `POSTMAN_TESTING_GUIDE.md`

### Modified Files:
1. `pom.xml` - Added Spring Security and JJWT dependencies
2. `src/main/java/com/Airlink/AirticketingSystem/model/User.java` - Added security fields
3. `src/main/resources/application.properties` - Added JWT configuration
4. `src/main/java/com/Airlink/AirticketingSystem/exception/GlobalExceptionHandler.java` - Added auth exception handling

---

## How to Use

### 1. Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "SecurePassword123!",
    "confirmPassword": "SecurePassword123!",
    "phone": "+250788123456"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePassword123!"
  }'
```

### 3. Use Access Token
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 4. Refresh Token
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

### 5. Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

---

## Next Steps

1. **Database Migration** - Run your application to create tables for User (with new fields) and RefreshToken
2. **Testing** - Use the POSTMAN_TESTING_GUIDE.md provided
3. **Frontend Integration** - Pass tokens in Authorization header
4. **Production Security**:
   - Change `jwt.secret` to a strong random string
   - Use environment variables for sensitive data
   - Enable HTTPS
   - Configure CORS for your frontend origin
5. **Additional Features** (Optional):
   - Email verification for new registrations
   - Password reset functionality
   - Account lockout after failed attempts
   - Token blacklisting for logout
   - Rate limiting on auth endpoints

---

## Token Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     Authentication Flow                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  1. User Registration/Login                                  │
│     ↓                                                         │
│  2. AuthService validates credentials                        │
│     ↓                                                         │
│  3. Generate Access Token (24h) & Refresh Token (7d)         │
│     ↓                                                         │
│  4. Save Refresh Token to database                           │
│     ↓                                                         │
│  5. Return tokens to client                                  │
│     ↓                                                         │
│  6. Client includes Access Token in Authorization header     │
│     ↓                                                         │
│  7. JwtAuthenticationFilter validates token                  │
│     ↓                                                         │
│  8. If valid, set authentication in SecurityContext          │
│     ↓                                                         │
│  9. Access granted to protected resource                     │
│     ↓                                                         │
│  10. If Access Token expires, use Refresh Token              │
│      to get new Access Token (repeat from step 3)            │
│     ↓                                                         │
│  11. On Logout, delete Refresh Token                         │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## Configuration for Production

### 1. Update JWT Secret
In `application.properties`:
```properties
jwt.secret=${JWT_SECRET:your-very-secure-random-string-here}
```

Set environment variable:
```bash
export JWT_SECRET="your-very-long-random-secure-string-with-at-least-512-bits"
```

### 2. Update CORS
In `SecurityConfig.java`, update `corsConfigurationSource()`:
```java
configuration.setAllowedOriginPatterns(Arrays.asList("https://yourdomain.com"));
```

### 3. Use HTTPS
- Generate SSL certificate
- Configure in application.properties
- Redirect HTTP to HTTPS

### 4. Enable HTTPS Only
```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
```

---

## Troubleshooting

### "JWT token has been tampered with"
- Check that `jwt.secret` is the same in all instances
- Ensure tokens haven't been modified

### "User not found"
- Ensure user was registered correctly
- Check email in login matches registration

### "Refresh token has expired"
- Tokens expire after 7 days
- User needs to login again to get new tokens

### "You do not have permission"
- Check user role (ADMIN, AGENT, CUSTOMER)
- Ensure endpoint is allowed for that role
- Verify token includes correct role

---

## Security Checklist

- [ ] JWT secret is strong (at least 512 bits)
- [ ] HTTPS is enabled in production
- [ ] CORS is restricted to trusted domains
- [ ] Tokens are not logged in production
- [ ] Password requirements are enforced
- [ ] Failed login attempts are tracked (for rate limiting)
- [ ] Refresh tokens are stored securely
- [ ] Token expiration times are reasonable
- [ ] User can view their own tokens/sessions
- [ ] Logout invalidates tokens
- [ ] API responses don't expose sensitive information

---

## Support

For issues or questions:
1. Check POSTMAN_TESTING_GUIDE.md for endpoint examples
2. Review security logs
3. Verify JWT configuration in application.properties
4. Ensure database migrations have run (User and RefreshToken tables)

---

**Implementation Date:** December 4, 2025
**JWT Token Expiration:** 24 hours (access), 7 days (refresh)
**Status:** ✅ Complete and Ready for Testing
