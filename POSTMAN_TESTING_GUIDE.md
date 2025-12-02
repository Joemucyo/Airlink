# Air Ticketing System - JWT Authentication API Testing Guide

## Overview
This guide provides comprehensive instructions for testing the JWT authentication endpoints using Postman.

## Base URL
```
http://localhost:8080
```

## 1. Authentication Endpoints

### 1.1 Register New User
**Endpoint:** POST `/api/auth/register`

**Request Body:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "phone": "+250788123456"
}
```

**Response (201 Created):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com",
    "phone": "+250788123456",
    "gender": null,
    "role": "CUSTOMER"
  }
}
```

**Password Requirements:**
- Minimum 8 characters
- At least one uppercase letter (A-Z)
- At least one lowercase letter (a-z)
- At least one digit (0-9)
- At least one special character (@$!%*?&)

---

### 1.2 Login
**Endpoint:** POST `/api/auth/login`

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com",
    "phone": "+250788123456",
    "gender": null,
    "role": "CUSTOMER"
  }
}
```

---

### 1.3 Refresh Token
**Endpoint:** POST `/api/auth/refresh`

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com",
    "phone": "+250788123456",
    "gender": null,
    "role": "CUSTOMER"
  }
}
```

---

### 1.4 Logout
**Endpoint:** POST `/api/auth/logout`

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Response (204 No Content)**

---

### 1.5 Get Current User
**Endpoint:** GET `/api/auth/me`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Response (200 OK):**
```json
{
  "id": 1,
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": "+250788123456",
  "gender": null,
  "role": "CUSTOMER"
}
```

---

### 1.6 Change Password
**Endpoint:** POST `/api/auth/change-password`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Request Body:**
```json
{
  "oldPassword": "SecurePassword123!",
  "newPassword": "NewPassword456@",
  "confirmNewPassword": "NewPassword456@"
}
```

**Response (204 No Content)**

---

## 2. Protected Endpoints (Require Authentication)

### 2.1 Create Booking (Customer/Agent/Admin)
**Endpoint:** POST `/api/bookings`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json
```

**Request Body:**
```json
{
  "flightId": 1,
  "userId": 1,
  "passengers": [
    {
      "firstName": "John",
      "lastName": "Doe",
      "passportNumber": "AB123456",
      "gender": "Male",
      "dateOfBirth": "1990-01-15"
    }
  ]
}
```

---

### 2.2 Get All Bookings (Customer/Agent/Admin)
**Endpoint:** GET `/api/bookings?page=0&size=10`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

### 2.3 Get User Bookings (Customer/Agent/Admin)
**Endpoint:** GET `/api/bookings/user/{userId}?page=0&size=10`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

### 2.4 Create Payment (Customer/Agent/Admin)
**Endpoint:** POST `/api/payments`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Request Body:**
```json
{
  "bookingId": 1,
  "amount": 500.00,
  "paymentMethod": "CREDIT_CARD"
}
```

---

## 3. Role-Based Access Control

### CUSTOMER Role Can:
- Register and login
- View own bookings
- Create bookings
- Make payments
- View flights (search only)
- View airports and locations

### AGENT Role Can:
- Do everything CUSTOMER can do
- Create flights
- Update flights
- View all bookings

### ADMIN Role Can:
- Do everything AGENT can do
- Delete flights
- Manage all users
- Create, update, delete airports
- Create, update, delete locations

---

## 4. Public Endpoints (No Authentication Required)

### 4.1 Search Flights
**Endpoint:** GET `/api/flights/search`

**Query Parameters:**
```
?originId=1&destinationId=2&departureDate=2024-01-15&page=0&size=10&sortBy=departureTime&sortDir=ASC
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "flightNumber": "KQ001",
      "departureTime": "2024-01-15T08:00:00",
      "arrivalTime": "2024-01-15T10:30:00",
      "availableSeats": 50,
      "status": "SCHEDULED"
    }
  ],
  "totalPages": 5,
  "totalElements": 50,
  "size": 10,
  "number": 0
}
```

---

### 4.2 Get All Airports
**Endpoint:** GET `/api/airports?page=0&size=10`

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Kigali International Airport",
      "code": "KGL",
      "city": "Kigali",
      "country": "Rwanda",
      "type": "INTERNATIONAL"
    }
  ],
  "totalPages": 1,
  "totalElements": 5,
  "size": 10,
  "number": 0
}
```

---

### 4.3 Search Airports
**Endpoint:** GET `/api/airports/search?query=kigali&page=0&size=10`

---

### 4.4 Get All Locations
**Endpoint:** GET `/api/locations?page=0&size=10`

---

## 5. Admin-Only Endpoints

### 5.1 Create User (Admin)
**Endpoint:** POST `/api/users`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Request Body:**
```json
{
  "fullName": "Agent User",
  "email": "agent@example.com",
  "phone": "+250788987654",
  "gender": "Male",
  "role": "AGENT",
  "locationId": 1
}
```

---

### 5.2 Get All Users (Admin)
**Endpoint:** GET `/api/users?page=0&size=10`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

### 5.3 Update User (Admin)
**Endpoint:** PUT `/api/users/{id}`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Request Body:**
```json
{
  "fullName": "Updated Name",
  "phone": "+250788111222",
  "gender": "Female",
  "role": "AGENT"
}
```

---

### 5.4 Delete User (Admin)
**Endpoint:** DELETE `/api/users/{id}`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

## 6. Postman Setup Instructions

### Step 1: Create Environment Variable
1. Go to **Environments**
2. Click **Create New**
3. Add variables:
   - `base_url`: `http://localhost:8080`
   - `accessToken`: (leave empty, will be auto-populated)
   - `refreshToken`: (leave empty, will be auto-populated)

### Step 2: Create Pre-Request Script for Login
Add this to the Pre-request Script tab of your Login request:
```javascript
// This will be executed before the request
```

### Step 3: Create Tests Script for Token Storage
Add this to the Tests tab of your Login request:
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("accessToken", response.accessToken);
    pm.environment.set("refreshToken", response.refreshToken);
    console.log("Tokens saved successfully");
}
```

### Step 4: Use Token in Requests
In the Authorization tab of other requests, select **Bearer Token** and enter:
```
{{accessToken}}
```

---

## 7. Common Error Responses

### 401 Unauthorized
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/bookings"
}
```

### 403 Forbidden
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You do not have permission to access this resource",
  "path": "/api/users"
}
```

### 400 Bad Request (Invalid Credentials)
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid email or password",
  "path": "/api/auth/login"
}
```

### 400 Bad Request (Validation)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "fieldErrors": {
    "email": "Email should be valid",
    "password": "Password must be at least 8 characters..."
  }
}
```

---

## 8. Token Expiration

- **Access Token Expiration**: 24 hours
- **Refresh Token Expiration**: 7 days

When your access token expires, use the refresh token to get a new access token without re-logging in.

---

## 9. Testing Workflow

1. **Register** → Get initial access and refresh tokens
2. **Use access token** → Make authenticated requests
3. **Token expires** → Use refresh token to get new access token
4. **Logout** → Invalidate refresh token
5. **Login** → Get new tokens and start fresh

---

## 10. Notes

- Always include the word "Bearer" before the token in Authorization header
- Tokens should not be exposed in logs or public repositories
- Use HTTPS in production
- Change the `jwt.secret` in application.properties to a strong random string for production
