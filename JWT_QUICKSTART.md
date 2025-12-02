# JWT Authentication Quick Start Guide

## Prerequisites
- Maven installed
- Java 21+
- PostgreSQL running
- Postman or similar API testing tool

## Step 1: Build the Project
```bash
cd AirticketingSystem
mvn clean install
```

## Step 2: Run Database Migrations
The application will automatically create the `users` and `refresh_tokens` tables on first run due to `spring.jpa.hibernate.ddl-auto=update` in `application.properties`.

## Step 3: Start the Application
```bash
mvn spring-boot:run
```

You should see:
```
Started AirticketingSystemApplication in X.XXX seconds
```

The application will be running at: `http://localhost:8080`

## Step 4: Test Authentication

### Option A: Using Postman

#### 1. Register New User
- **URL:** `POST http://localhost:8080/api/auth/register`
- **Body (JSON):**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "phone": "+250788123456"
}
```
- **Expected Response:** 201 Created with tokens

#### 2. Login
- **URL:** `POST http://localhost:8080/api/auth/login`
- **Body (JSON):**
```json
{
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```
- **Expected Response:** 200 OK with tokens
- **Copy the `accessToken` from response**

#### 3. Get Current User
- **URL:** `GET http://localhost:8080/api/auth/me`
- **Headers:**
  - `Authorization: Bearer YOUR_ACCESS_TOKEN_HERE`
- **Expected Response:** 200 OK with user info

### Option B: Using cURL

#### 1. Register
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

#### 2. Save the Response
Save the `accessToken` from the response

#### 3. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePassword123!"
  }'
```

#### 4. Get Current User
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

## Step 5: Verify Role-Based Access

### As CUSTOMER (default for new registrations):
- ✅ Can view/create bookings
- ✅ Can view/create passengers
- ✅ Can view/create payments
- ❌ Cannot create flights (needs AGENT/ADMIN role)
- ❌ Cannot manage users (needs ADMIN role)

### As ADMIN (create manually or via admin user):
- ✅ Can do everything
- ✅ Can manage users
- ✅ Can manage flights
- ✅ Can manage airports

## Common Issues & Solutions

### Issue 1: "Unsupported Media Type" Error
**Solution:** Add this header to all requests:
```
Content-Type: application/json
```

### Issue 2: "Unauthorized" on Protected Endpoints
**Solution:** 
1. Register/Login first
2. Copy the `accessToken` from response
3. Add header: `Authorization: Bearer YOUR_TOKEN`

### Issue 3: Token Expired (after 24 hours)
**Solution:**
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN_HERE"
  }'
```

### Issue 4: Password Validation Failed
**Password must have:**
- Minimum 8 characters
- At least 1 UPPERCASE letter
- At least 1 lowercase letter
- At least 1 digit (0-9)
- At least 1 special character (@$!%*?&)

**Example valid password:** `SecurePassword123!`

### Issue 5: Database Connection Error
**Solution:**
1. Ensure PostgreSQL is running
2. Check database URL in `application.properties`
3. Verify database credentials
4. Default:
   - URL: `jdbc:postgresql://localhost:5432/airticketing_db`
   - User: `postgres`
   - Password: `1234567890`

## Testing All Endpoints

### 1. Test Public Endpoints (No Auth Required)
```bash
# Search flights
curl http://localhost:8080/api/flights/search?departureDate=2024-01-15

# Get airports
curl http://localhost:8080/api/airports?page=0&size=10

# Get locations
curl http://localhost:8080/api/locations?page=0&size=10
```

### 2. Test Customer Endpoints (With Auth)
```bash
# Get all bookings
curl -X GET http://localhost:8080/api/bookings \
  -H "Authorization: Bearer YOUR_TOKEN"

# Create booking
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "flightId": 1,
    "userId": 1,
    "passengers": [...]
  }'
```

### 3. Test Admin Endpoints (ADMIN Role Required)
```bash
# Get all users
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer ADMIN_TOKEN"

# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Agent User",
    "email": "agent@example.com",
    "phone": "+250788987654",
    "gender": "Male",
    "role": "AGENT"
  }'
```

## Postman Collection Setup

### 1. Create Environment
- Name: `AirTicking Development`
- Variables:
  - `base_url` = `http://localhost:8080`
  - `accessToken` = (empty, auto-populated)
  - `refreshToken` = (empty, auto-populated)

### 2. Create Requests
- POST Register → Tests script to save tokens
- POST Login → Tests script to save tokens
- GET Current User → Uses `{{accessToken}}`
- GET Bookings → Uses `{{accessToken}}`

### 3. Tests Script (Add to Login/Register)
```javascript
if (pm.response.code === 200 || pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("accessToken", response.accessToken);
    pm.environment.set("refreshToken", response.refreshToken);
}
```

## Key Endpoints Reference

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/api/auth/register` | ❌ | - | Register new user |
| POST | `/api/auth/login` | ❌ | - | Login user |
| POST | `/api/auth/refresh` | ❌ | - | Refresh access token |
| POST | `/api/auth/logout` | ✅ | - | Logout user |
| GET | `/api/auth/me` | ✅ | - | Get current user |
| POST | `/api/auth/change-password` | ✅ | - | Change password |
| GET | `/api/flights/search` | ❌ | - | Search flights |
| GET | `/api/airports` | ❌ | - | Get airports |
| GET | `/api/locations` | ❌ | - | Get locations |
| GET | `/api/bookings` | ✅ | CUSTOMER+ | Get bookings |
| POST | `/api/bookings` | ✅ | CUSTOMER+ | Create booking |
| GET | `/api/users` | ✅ | ADMIN | Get all users |
| POST | `/api/users` | ✅ | ADMIN | Create user |
| PUT | `/api/flights` | ✅ | AGENT+ | Update flight |
| DELETE | `/api/flights/{id}` | ✅ | ADMIN | Delete flight |

## Next Steps

1. ✅ Application is running with JWT authentication
2. ✅ Test all endpoints using Postman/cURL
3. 📖 Read `POSTMAN_TESTING_GUIDE.md` for detailed endpoint documentation
4. 📖 Read `JWT_IMPLEMENTATION_SUMMARY.md` for technical details
5. 🔐 Configure CORS for your frontend
6. 🚀 Deploy to production with secure JWT secret
7. 📧 Add email verification (optional)
8. 🔒 Add account lockout protection (optional)

## Support Resources

- **JWT Implementation Details:** See `JWT_IMPLEMENTATION_SUMMARY.md`
- **Complete Testing Guide:** See `POSTMAN_TESTING_GUIDE.md`
- **API Documentation:** Visit `http://localhost:8080/swagger-ui.html`
- **Database:** PostgreSQL at `localhost:5432`

## Performance Tips

1. Use `accessToken` for all requests (24-hour expiration)
2. Only use `refreshToken` when access token expires
3. Use pagination on list endpoints (e.g., `?page=0&size=10`)
4. Set `spring.jpa.show-sql=false` in production

## Security Reminders

⚠️ **Never:**
- Share your tokens publicly
- Commit tokens to version control
- Send tokens in URL parameters
- Store tokens in plain text

✅ **Always:**
- Use HTTPS in production
- Store tokens securely in frontend
- Use Bearer token scheme
- Refresh tokens before expiration
- Logout to invalidate tokens

---

**Ready to test?** Start with the `/api/auth/register` endpoint above! 🚀
