# Eventified API Documentation

**Version:** 1.0  
**Author:** Eventified Team  
**Last Updated:** December 8, 2025

---

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Base URL & Headers](#base-url--headers)
4. [Response Codes](#response-codes)
5. [API Endpoints](#api-endpoints)
   - [Authentication Endpoints](#authentication-endpoints)
   - [User Endpoints](#user-endpoints)
   - [Event Endpoints](#event-endpoints)
   - [Notification Endpoints](#notification-endpoints)
   - [Friendship Endpoints](#friendship-endpoints)
   - [Recommendation Endpoints](#recommendation-endpoints)
   - [Admin Endpoints](#admin-endpoints)
   - [Development Endpoints](#development-endpoints)
6. [Data Models](#data-models)
7. [Error Handling](#error-handling)

---

## Overview

Eventified is a sports event management platform that allows users to create, discover, and participate in sports events. This API provides endpoints for user management, event operations, notifications, friend requests, and personalized recommendations.

---

## Authentication

The API uses **JWT (JSON Web Token)** based authentication. After successful login, include the JWT token in the Authorization header for all protected endpoints.

### Authentication Flow:
1. Register a new account via `/auth/register`
2. Verify email via the link sent to your email
3. Login via `/auth/login` to receive a JWT token
4. Include the token in subsequent requests: `Authorization: Bearer <token>`

---

## Base URL & Headers

### Base URL
```
https://your-domain.com/api
```

### Required Headers
```http
Content-Type: application/json
Authorization: Bearer <your-jwt-token>  # For protected endpoints
```

---

## Response Codes

| Code | Description |
|------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource successfully created |
| 400 | Bad Request - Invalid input or validation error |
| 401 | Unauthorized - Missing or invalid authentication |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 500 | Internal Server Error - Server error occurred |

---

## API Endpoints

### Authentication Endpoints

#### 1. Register User
**POST** `/auth/register`

Creates a new user account. After registration, an email verification link is sent.

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

**Success Response (201 Created):**
```json
"User registered successfully"
```

**Error Response (400 Bad Request):**
```json
"Username already exists" 
// or "Email already registered"
```

---

#### 2. Verify Email
**GET** `/auth/verify?token={verification_token}`

Verifies user email address using the token sent via email.

**Query Parameters:**
- `token` (string, required): Email verification token

**Success Response (200 OK):**
Returns HTML page confirming successful verification

**Error Response:**
Returns error HTML page if token is invalid or expired

---

#### 3. Login
**POST** `/auth/login`

Authenticates user and returns JWT token.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error Response (401 Unauthorized):**
```json
"Invalid credentials"
```

---

### User Endpoints

All user endpoints require authentication.

#### 4. Get Current User Profile
**GET** `/user/profile`

Retrieves the authenticated user's profile information.

**Success Response (200 OK):**
```json
{
  "username": "john_doe",
  "rating": 4.5,
  "sports": [
    {
      "sport": 1,
      "skillLevel": 3
    }
  ],
  "isVerified": true
}
```

---

#### 5. Get Other User Info
**GET** `/user/info/{userId}`

Retrieves public profile information of another user.

**Path Parameters:**
- `userId` (UUID, required): User's unique identifier

**Success Response (200 OK):**
```json
{
  "username": "jane_smith",
  "rating": 4.8,
  "sports": [
    {
      "sport": 2,
      "skillLevel": 4
    }
  ],
  "isVerified": true
}
```

---

#### 6. Join Event
**POST** `/user/event/join?eventId={eventId}`

Allows authenticated user to join a specific event.

**Query Parameters:**
- `eventId` (UUID, required): Event identifier

**Success Response (200 OK):**
```json
"Successfully joined the event."
```

**Error Response (400 Bad Request):**
```json
"User has already joined the event."
```

---

#### 7. Leave Event
**DELETE** `/user/event/leave?eventId={eventId}`

Allows authenticated user to leave an event they previously joined.

**Query Parameters:**
- `eventId` (UUID, required): Event identifier

**Success Response (200 OK):**
```json
"Successfully left the event."
```

**Error Response (500 Internal Server Error):**
```json
"Failed to leave the event."
```

---

#### 8. Delete User Account
**DELETE** `/user/profile/delete`

Permanently deletes the authenticated user's account and all associated data.

⚠️ **Warning:** This operation cannot be undone.

**Success Response (200 OK):**
```json
"User deleted successfully."
```

**Error Response (500 Internal Server Error):**
```json
"Failed to delete user."
```

---

#### 9. Add Preferred Sport
**POST** `/user/sport/add`

Adds a sport preference to user's profile with skill level.

**Request Body:**
```json
{
  "sport": 1,
  "skillLevel": 3
}
```

**Sport IDs:**
- 1: Football/Soccer
- 2: Basketball
- 3: Tennis
- (Check with backend team for complete list)

**Skill Levels:**
- 1: Beginner
- 2: Intermediate
- 3: Advanced
- 4: Expert
- 5: Professional

**Success Response (200 OK):**
```json
{
  "id": "uuid",
  "sport": 1,
  "skillLevel": 3,
  "userId": "uuid"
}
```

**Error Response (500 Internal Server Error):**
```json
"Failed to add sport: Sport already exists for this user"
```

---

#### 10. Remove Preferred Sport
**DELETE** `/user/sport/remove`

Removes a sport preference from user's profile.

**Request Body:**
```json
{
  "sport": 1,
  "skillLevel": 3
}
```

**Success Response (200 OK):**
Empty response with status 200

**Error Response (500 Internal Server Error):**
```json
"Failed to remove sport: Sport not found"
```

---

### Event Endpoints

#### 11. Create Event
**POST** `/event/create`

Creates a new event with the authenticated user as the organizer.

**Request Body:**
```json
{
  "title": "Weekend Football Match",
  "sport": 1,
  "address": "Central Park, New York",
  "skillLevel": 3,
  "startTime": "2025-12-15T10:00:00",
  "endTime": "2025-12-15T12:00:00",
  "capacity": 10,
  "latitude": 40.785091,
  "longitude": -73.968285
}
```

**Field Descriptions:**
- `title`: Event name (must be unique for the user)
- `sport`: Sport type ID (integer)
- `address`: Event location address
- `skillLevel`: Required skill level (1-5)
- `startTime`: ISO 8601 datetime format
- `endTime`: ISO 8601 datetime format
- `capacity`: Maximum number of participants
- `latitude`: Decimal latitude coordinate
- `longitude`: Decimal longitude coordinate

**Success Response (201 Created):**
```json
"Event created successfully"
```

**Error Response (400 Bad Request):**
```json
"Event with this title already exists for this user"
```

---

#### 12. Get All Events
**GET** `/event/all`

Retrieves all events in the system.

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Weekend Football Match",
    "sport": 1,
    "address": "Central Park, New York",
    "startTime": "2025-12-15T10:00:00",
    "capacity": 10,
    "skillLevel": 3
  }
]
```

**Error Response (404 Not Found):**
```json
"No events found"
```

---

#### 13. Get My Upcoming Hosted Events
**GET** `/event/hosted/upcoming`

Retrieves all upcoming events organized by the authenticated user.

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Weekend Football Match",
    "sport": 1,
    "address": "Central Park, New York",
    "startTime": "2025-12-15T10:00:00",
    "capacity": 10,
    "skillLevel": 3
  }
]
```

**Error Response (500 Internal Server Error):**
```json
"No upcoming events found"
```

---

#### 14. Get My Past Hosted Events
**GET** `/event/hosted/past`

Retrieves all past events organized by the authenticated user.

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Last Week's Match",
    "sport": 1,
    "address": "Central Park, New York",
    "startTime": "2025-12-01T10:00:00",
    "capacity": 10,
    "skillLevel": 3
  }
]
```

**Error Response (500 Internal Server Error):**
```json
"No past events found"
```

---

#### 15. Get My Past Attended Events
**GET** `/event/attended/past`

Retrieves all past events where the authenticated user was a participant.

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Basketball Tournament",
    "sport": 2,
    "address": "Sports Complex",
    "startTime": "2025-11-20T14:00:00",
    "capacity": 8,
    "skillLevel": 4
  }
]
```

**Error Response (500 Internal Server Error):**
```json
"No past attended events found"
```

---

#### 16. Get My Upcoming Attended Events
**GET** `/event/attended/upcoming`

Retrieves all upcoming events where the authenticated user is a participant.

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Tennis Match",
    "sport": 3,
    "address": "Tennis Courts",
    "startTime": "2025-12-20T09:00:00",
    "capacity": 4,
    "skillLevel": 3
  }
]
```

**Error Response (500 Internal Server Error):**
```json
"No upcoming attended events found"
```

---

#### 17. Get Event Details
**GET** `/event/details/{eventId}`

Retrieves detailed information about a specific event including participants.

**Path Parameters:**
- `eventId` (UUID, required): Event identifier

**Success Response (200 OK):**
```json
{
  "title": "Weekend Football Match",
  "sport": 1,
  "skillLevel": 3,
  "address": "Central Park, New York",
  "startTime": "2025-12-15T10:00:00",
  "endTime": "2025-12-15T12:00:00",
  "capacity": 10,
  "occupied": 5,
  "participants": [
    {
      "username": "john_doe"
    },
    {
      "username": "jane_smith"
    }
  ],
  "latitude": 40.785091,
  "longitude": -73.968285
}
```

**Error Response (500 Internal Server Error):**
```json
"Event not found"
```

---

#### 18. Filter Events by Sport
**GET** `/event/filter/by/sport/{sport}`

Retrieves all events for a specific sport.

**Path Parameters:**
- `sport` (integer, required): Sport ID

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Football Match",
    "sport": 1,
    "address": "Stadium",
    "startTime": "2025-12-15T10:00:00",
    "capacity": 22,
    "skillLevel": 3
  }
]
```

**Error Response (500 Internal Server Error):**
```json
"No events for this sport found"
```

---

#### 19. Filter Events by Skill Level
**GET** `/event/filter/by/skillLevel/{skillLevel}`

Retrieves all events for a specific skill level.

**Path Parameters:**
- `skillLevel` (integer, required): Skill level (1-5)

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Beginner Tennis",
    "sport": 3,
    "address": "Tennis Club",
    "startTime": "2025-12-16T11:00:00",
    "capacity": 4,
    "skillLevel": 1
  }
]
```

**Error Response (500 Internal Server Error):**
```json
"No events for this skill level found"
```

---

#### 20. Filter Events by Start Time After
**GET** `/event/filter/by/startTimeAfter/{dateTime}`

Retrieves events starting after a specific date and time.

**Path Parameters:**
- `dateTime` (string, required): ISO 8601 datetime (e.g., "2025-12-15T10:00:00")

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Future Event",
    "sport": 2,
    "address": "Location",
    "startTime": "2025-12-20T14:00:00",
    "capacity": 8,
    "skillLevel": 2
  }
]
```

**Error Response (500 Internal Server Error):**
```json
"No events found after this date"
```

---

#### 21. Filter Events by End Time Before
**GET** `/event/filter/by/startTimeBefore/{dateTime}`

Retrieves events ending before a specific date and time.

**Path Parameters:**
- `dateTime` (string, required): ISO 8601 datetime

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Past Event",
    "sport": 1,
    "address": "Location",
    "startTime": "2025-12-01T10:00:00",
    "capacity": 10,
    "skillLevel": 3
  }
]
```

**Error Response (500 Internal Server Error):**
```json
"No events found before this date"
```

---

#### 22. Filter Events by Free Slots
**GET** `/event/filter/by/freeSlots/{freeSlots}`

Retrieves events with at least the specified number of free spots.

**Path Parameters:**
- `freeSlots` (integer, required): Minimum number of free spots

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Event with Space",
    "sport": 2,
    "address": "Venue",
    "startTime": "2025-12-18T15:00:00",
    "capacity": 12,
    "skillLevel": 2
  }
]
```

**Error Response (500 Internal Server Error):**
```json
"No events found with that number of free slots"
```

---

#### 23. Advanced Event Filter
**GET** `/event/filter`

Filters events by multiple optional criteria. All parameters are optional.

**Query Parameters:**
- `sports` (array of integers, optional): List of sport IDs
- `skillLevels` (array of integers, optional): List of skill levels
- `startTimeAfter` (string, optional): ISO 8601 datetime
- `endTimeBefore` (string, optional): ISO 8601 datetime
- `freeSlots` (integer, optional): Minimum free spots

**Example Request:**
```
GET /event/filter?sports=1,2&skillLevels=2,3&startTimeAfter=2025-12-15T00:00:00&freeSlots=3
```

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Filtered Event",
    "sport": 1,
    "address": "Location",
    "startTime": "2025-12-16T10:00:00",
    "capacity": 10,
    "skillLevel": 2
  }
]
```

**Error Response (500 Internal Server Error):**
```json
"No events found with your criteria selected"
```

**Error Response (400 Bad Request):**
```json
"Invalid filtered parameters: [error details]"
```

---

### Notification Endpoints

All notification endpoints require authentication.

#### 24. Get All User Notifications
**GET** `/notifications`

Retrieves all notifications for the authenticated user, ordered by newest first.

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "typeOfNotification": 5,
    "title": "New Player Joined",
    "messageOfNotification": "john_doe has joined the event you are participating in",
    "isRead": false,
    "createdAt": "2025-12-08T14:30:00"
  },
  {
    "id": "uuid",
    "typeOfNotification": 4,
    "title": "Event Reminder",
    "messageOfNotification": "Your event 'Weekend Match' starts in 1 hour",
    "isRead": true,
    "createdAt": "2025-12-08T09:00:00"
  }
]
```

**Notification Types:**
- 1: New Event Recommendation
- 2: Event Cancelled
- 3: Rate Participants
- 4: Event Reminder
- 5: New Player Joined
- 6: Event Updated
- 7: Player Left
- 8: Friend Request

**Error Response (500 Internal Server Error):**
```json
"No notifications found for user"
```

---

#### 25. Get Unread Notifications
**GET** `/notifications/unread`

Retrieves only unread notifications for the authenticated user.

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "typeOfNotification": 5,
    "title": "New Player Joined",
    "messageOfNotification": "john_doe has joined your event",
    "isRead": false,
    "createdAt": "2025-12-08T14:30:00"
  }
]
```

**Error Response (500 Internal Server Error):**
```json
"No unread notifications found for user"
```

---

#### 26. Get Unread Notification Count
**GET** `/notifications/count`

Gets the count of unread notifications for the authenticated user.

**Success Response (200 OK):**
```json
5
```

---

#### 27. Mark Notification as Read
**PUT** `/notifications/read/{id}`

Marks a specific notification as read.

**Path Parameters:**
- `id` (UUID, required): Notification identifier

**Success Response (200 OK):**
```json
"Notification marked as read"
```

**Error Response (500 Internal Server Error):**
```json
"Failed to mark notification as read"
```

---

#### 28. Mark All Notifications as Read
**PUT** `/notifications/read/all`

Marks all notifications for the authenticated user as read.

**Success Response (200 OK):**
```json
"All notifications were marked as read"
```

**Error Response (500 Internal Server Error):**
```json
"Failed to mark all notifications as read"
```

---

### Friendship Endpoints

All friendship endpoints require authentication.

#### 29. Send Friend Request
**POST** `/friend-request/send/{addresseeId}`

Sends a friend request to another user.

**Path Parameters:**
- `addresseeId` (UUID, required): ID of the user to send friend request to

**Success Response (200 OK):**
```json
"Friend request sent"
```

**Error Response (500 Internal Server Error):**
```json
"Failed to send friend request"
```

---

#### 30. Accept Friend Request
**POST** `/friend-request/accept/{requesterId}`

Accepts a friend request from another user.

**Path Parameters:**
- `requesterId` (UUID, required): ID of the user who sent the friend request

**Success Response (200 OK):**
```json
"Friend request accepted"
```

**Error Response (500 Internal Server Error):**
```json
"Failed to accept friend request"
```

---

#### 31. Decline Friend Request
**POST** `/friend-request/decline/{requesterId}`

Declines a friend request from another user.

**Path Parameters:**
- `requesterId` (UUID, required): ID of the user who sent the friend request

**Success Response (200 OK):**
```json
"Friend request declined"
```

**Error Response (500 Internal Server Error):**
```json
"Failed to decline friend request"
```

---

### Recommendation Endpoints

#### 32. Get Event Recommendations
**GET** `/recommendations/events?limit={limit}`

Gets personalized event recommendations based on user's sport preferences and skill level.

**Query Parameters:**
- `limit` (integer, optional): Maximum number of recommendations (default: 10, min: 1, max: 50)

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Recommended Football Match",
    "sport": 1,
    "address": "Nearby Field",
    "startTime": "2025-12-16T15:00:00",
    "capacity": 10,
    "skillLevel": 3
  }
]
```

---

### Admin Endpoints

**Note:** All admin endpoints require the user to have ADMIN role.

#### 33. Get All Users (Admin Only)
**GET** `/admin/users`

Retrieves detailed information for all users in the system.

**Required Role:** ADMIN

**Success Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "username": "john_doe",
    "email": "john@example.com",
    "trustScore": 50,
    "numberOfReviews": 10,
    "isVerified": true,
    "role": "USER",
    "createdAt": "2025-11-01T10:00:00"
  }
]
```

**Error Response (403 Forbidden):**
```json
"Access denied"
```

**Error Response (500 Internal Server Error):**
```json
"No users found"
```

---

### Development Endpoints

**Note:** These endpoints are only available when security is disabled (development mode).

#### 34. Get Development Status
**GET** `/dev/status`

Returns the current status of the backend in development mode.

**Success Response (200 OK):**
```json
{
  "status": "OK",
  "message": "Backend is running in development mode",
  "profile": "dev",
  "security": "DISABLED",
  "timestamp": 1702036800000
}
```

---

#### 35. Get Available Endpoints (Dev)
**GET** `/dev/endpoints`

Returns information about available API endpoints in development mode.

**Success Response (200 OK):**
```json
{
  "message": "All endpoints are accessible in development mode",
  "swagger_ui": "/swagger-ui.html",
  "api_docs": "/v3/api-docs",
  "available_endpoints": {
    "Auth": "/api/auth/*",
    "Users": "/api/user/*",
    "Events": "/api/events/*",
    "Admin": "/api/admin/*",
    "Development": "/api/dev/*"
  }
}
```

---

## Data Models

### RegisterRequest
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

### LoginRequest
```json
{
  "username": "string",
  "password": "string"
}
```

### LoginResponse
```json
{
  "token": "string (JWT)"
}
```

### UserProfileDTO
```json
{
  "username": "string",
  "rating": "float (0.0 - 5.0)",
  "sports": [
    {
      "sport": "integer",
      "skillLevel": "integer (1-5)"
    }
  ],
  "isVerified": "boolean"
}
```

### EventRequest
```json
{
  "title": "string",
  "sport": "integer",
  "address": "string",
  "skillLevel": "integer (1-5)",
  "startTime": "ISO 8601 datetime string",
  "endTime": "ISO 8601 datetime string",
  "capacity": "integer",
  "latitude": "decimal",
  "longitude": "decimal"
}
```

### EventPoolDTO
```json
{
  "id": "UUID string",
  "title": "string",
  "sport": "integer",
  "address": "string",
  "startTime": "ISO 8601 datetime string",
  "capacity": "integer",
  "skillLevel": "integer (1-5)"
}
```

### EventDetailsDTO
```json
{
  "title": "string",
  "sport": "integer",
  "skillLevel": "integer (1-5)",
  "address": "string",
  "startTime": "ISO 8601 datetime string",
  "endTime": "ISO 8601 datetime string",
  "capacity": "integer",
  "occupied": "integer",
  "participants": [
    {
      "username": "string"
    }
  ],
  "latitude": "decimal",
  "longitude": "decimal"
}
```

### SportDTO
```json
{
  "sport": "integer",
  "skillLevel": "integer (1-5)"
}
```

### Notification
```json
{
  "id": "UUID string",
  "typeOfNotification": "integer (1-8)",
  "title": "string",
  "messageOfNotification": "string",
  "isRead": "boolean",
  "createdAt": "ISO 8601 datetime string"
}
```

---

## Error Handling

### Common Error Response Format

Most endpoints return plain text error messages. Here are common error scenarios:

### Validation Errors (400 Bad Request)
```json
"Invalid input: [specific error message]"
```

### Authentication Errors (401 Unauthorized)
```json
"Invalid credentials"
```
or
```json
"Token expired"
```

### Authorization Errors (403 Forbidden)
```json
"Access denied"
```

### Not Found Errors (404 Not Found)
```json
"Resource not found"
```

### Server Errors (500 Internal Server Error)
```json
"Internal server error: [error details]"
```

---

## Testing Tips

### 1. Authentication Testing
- Register a new user
- Verify email (check email inbox for verification link)
- Login to get JWT token
- Store token for use in protected endpoints

### 2. Event Creation Testing
- Ensure startTime is before endTime
- Use valid coordinates for latitude/longitude
- Capacity must be a positive integer
- Sport and skillLevel must be valid IDs

### 3. Datetime Format
Always use ISO 8601 format for datetime fields:
```
2025-12-15T10:00:00
```

### 4. UUID Format
UUIDs are represented as strings in the format:
```
550e8400-e29b-41d4-a716-446655440000
```

### 5. Testing Filters
When testing filter endpoints with multiple parameters, ensure proper URL encoding:
```
/event/filter?sports=1,2&skillLevels=2,3&startTimeAfter=2025-12-15T00:00:00
```

---

## Support

For questions or issues, contact the Eventified Team.

**GitHub Repository:** UntitledWebAppBE  
**Owner:** Dobrovsky007  
**Current Branch:** dev

---

**End of Documentation**
