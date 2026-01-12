# Eventified API Testing Guide

**Quick Reference for QA & Testing Teams**

---

## Quick Start

### 1. Setup Test User
```bash
# Register
POST /auth/register
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "Test123!@#"
}

# Verify email (check inbox for link)
GET /auth/verify?token={token_from_email}

# Login
POST /auth/login
{
  "username": "testuser",
  "password": "Test123!@#"
}

# Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2. Use Token in Requests
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## API Endpoint Summary

### Authentication (No auth required)
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/auth/register` | Create new account |
| GET | `/auth/verify?token=` | Verify email |
| POST | `/auth/login` | Get JWT token |

### User Profile (Auth required)
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/user/profile` | Get my profile |
| GET | `/user/info/{userId}` | Get other user's profile |
| POST | `/user/sport/add` | Add sport preference |
| DELETE | `/user/sport/remove` | Remove sport preference |
| DELETE | `/user/profile/delete` | Delete account |

### Event Participation (Auth required)
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/user/event/join?eventId=` | Join event |
| DELETE | `/user/event/leave?eventId=` | Leave event |

### Event Management (Auth required)
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/event/create` | Create new event |
| GET | `/event/all` | Get all events |
| GET | `/event/hosted/upcoming` | My upcoming events |
| GET | `/event/hosted/past` | My past events |
| GET | `/event/attended/upcoming` | Events I'm joining |
| GET | `/event/attended/past` | Past events I joined |
| GET | `/event/details/{eventId}` | Event details |

### Event Filters (Auth required)
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/event/filter/by/sport/{sport}` | Filter by sport |
| GET | `/event/filter/by/skillLevel/{level}` | Filter by skill |
| GET | `/event/filter/by/startTimeAfter/{date}` | Future events |
| GET | `/event/filter/by/startTimeBefore/{date}` | Past events |
| GET | `/event/filter/by/freeSlots/{slots}` | By availability |
| GET | `/event/filter?params` | Advanced filter |

### Notifications (Auth required)
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/notifications` | All notifications |
| GET | `/notifications/unread` | Unread only |
| GET | `/notifications/count` | Unread count |
| PUT | `/notifications/read/{id}` | Mark one as read |
| PUT | `/notifications/read/all` | Mark all as read |

### Friendships (Auth required)
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/friend-request/send/{userId}` | Send request |
| POST | `/friend-request/accept/{userId}` | Accept request |
| POST | `/friend-request/decline/{userId}` | Decline request |

### Recommendations (Auth required)
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/recommendations/events?limit=10` | Get recommendations |

### Admin (Admin role required)
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/admin/users` | Get all users |

---

## Test Scenarios

### Scenario 1: User Registration & Login Flow
```
1. POST /auth/register (new user)
   ✓ Should return 201 Created
   
2. POST /auth/register (same username)
   ✓ Should return 400 "Username already exists"
   
3. GET /auth/verify?token={valid_token}
   ✓ Should return success HTML page
   
4. POST /auth/login (verified user)
   ✓ Should return JWT token
   
5. POST /auth/login (wrong password)
   ✓ Should return 401 Unauthorized
```

### Scenario 2: Event Creation & Management
```
1. POST /event/create
   Body: {
     "title": "Test Match",
     "sport": 1,
     "address": "Test Stadium",
     "skillLevel": 3,
     "startTime": "2025-12-20T10:00:00",
     "endTime": "2025-12-20T12:00:00",
     "capacity": 10,
     "latitude": 40.7128,
     "longitude": -74.0060
   }
   ✓ Should return 201 "Event created successfully"
   
2. GET /event/all
   ✓ Should include the newly created event
   
3. GET /event/hosted/upcoming
   ✓ Should show the event for the creator
   
4. POST /user/event/join?eventId={eventId} (different user)
   ✓ Should return 200 "Successfully joined"
   
5. GET /event/details/{eventId}
   ✓ Should show both organizer and participant
```

### Scenario 3: Notification Flow
```
1. User A creates event
   
2. User B joins event
   ✓ User A should receive notification (type 5: New Player Joined)
   
3. GET /notifications (User A)
   ✓ Should have unread notification
   
4. GET /notifications/count (User A)
   ✓ Should return 1
   
5. PUT /notifications/read/{id}
   ✓ Should mark as read
   
6. GET /notifications/count
   ✓ Should return 0
```

### Scenario 4: Event Filtering
```
1. Create events with different sports
   
2. GET /event/filter/by/sport/1
   ✓ Should only return football events
   
3. GET /event/filter?sports=1,2&skillLevels=2,3&freeSlots=5
   ✓ Should return events matching all criteria
   
4. GET /event/filter/by/startTimeAfter/2025-12-20T00:00:00
   ✓ Should only return future events
```

### Scenario 5: Sport Preferences
```
1. POST /user/sport/add
   Body: {"sport": 1, "skillLevel": 3}
   ✓ Should add sport preference
   
2. POST /user/sport/add (same sport)
   ✓ Should return error
   
3. GET /user/profile
   ✓ Should include the new sport
   
4. DELETE /user/sport/remove
   Body: {"sport": 1, "skillLevel": 3}
   ✓ Should remove sport
   
5. GET /user/profile
   ✓ Sport should be removed
```

### Scenario 6: Friend Request Flow
```
1. User A sends friend request to User B
   POST /friend-request/send/{userB_id}
   ✓ Should return "Friend request sent"
   
2. User B accepts request
   POST /friend-request/accept/{userA_id}
   ✓ Should return "Friend request accepted"
   
3. User B should receive notification (type 8: Friend Request)
```

---

## Common Test Data

### Sport IDs
```json
1: Football/Soccer
2: Basketball
3: Tennis
4: Volleyball
5: Swimming
```

### Skill Levels
```json
1: Beginner
2: Intermediate
3: Advanced
4: Expert
5: Professional
```

### Notification Types
```json
1: New Event Recommendation
2: Event Cancelled
3: Rate Participants
4: Event Reminder
5: New Player Joined
6: Event Updated
7: Player Left
8: Friend Request
```

---

## Sample Valid Payloads

### Create Event
```json
{
  "title": "Weekend Soccer Match",
  "sport": 1,
  "address": "Central Park Soccer Field 3",
  "skillLevel": 3,
  "startTime": "2025-12-20T14:00:00",
  "endTime": "2025-12-20T16:00:00",
  "capacity": 12,
  "latitude": 40.785091,
  "longitude": -73.968285
}
```

### Add Sport Preference
```json
{
  "sport": 1,
  "skillLevel": 3
}
```

### Register User
```json
{
  "username": "test_player_123",
  "email": "testplayer123@example.com",
  "password": "SecurePass123!@#"
}
```

### Login
```json
{
  "username": "test_player_123",
  "password": "SecurePass123!@#"
}
```

---

## Edge Cases to Test

### 1. Event Creation
- [ ] Title too long (>255 chars)
- [ ] Duplicate event title for same user
- [ ] StartTime after EndTime
- [ ] Past event creation
- [ ] Capacity = 0 or negative
- [ ] Invalid coordinates
- [ ] Missing required fields

### 2. Event Joining
- [ ] Join already full event
- [ ] Join own event
- [ ] Join same event twice
- [ ] Join deleted event
- [ ] Leave event not joined

### 3. Authentication
- [ ] Expired JWT token
- [ ] Invalid JWT format
- [ ] Missing Authorization header
- [ ] Email already registered
- [ ] Username with special characters
- [ ] Weak password

### 4. Notifications
- [ ] Mark non-existent notification as read
- [ ] Access other user's notifications
- [ ] Notification for deleted event

### 5. Filtering
- [ ] Invalid datetime format
- [ ] Sport ID that doesn't exist
- [ ] Skill level out of range (< 1 or > 5)
- [ ] Multiple conflicting filters

### 6. Profile Operations
- [ ] Delete account with active events
- [ ] Add sport with invalid ID
- [ ] Remove sport not in profile
- [ ] View deleted user profile

---

## Response Code Checklist

| Code | When to Expect |
|------|----------------|
| 200 | Successful GET/PUT/DELETE |
| 201 | Successful POST (creation) |
| 400 | Invalid input/validation error |
| 401 | Missing/invalid token |
| 403 | Insufficient permissions (e.g., admin endpoints) |
| 404 | Resource not found |
| 500 | Server error |

---

## Postman Collection Template

### Collection Variables
```
base_url: http://localhost:8080/api
auth_token: {{token}}
test_user_id: {{userId}}
test_event_id: {{eventId}}
```

### Pre-request Script (for authenticated requests)
```javascript
pm.request.headers.add({
    key: 'Authorization',
    value: 'Bearer ' + pm.environment.get('auth_token')
});
```

### Test Script Template
```javascript
// Check status code
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// Check response time
pm.test("Response time is less than 500ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(500);
});

// Save token after login
if (pm.response.code === 200 && pm.request.url.toString().includes('/auth/login')) {
    var jsonData = pm.response.json();
    pm.environment.set('auth_token', jsonData.token);
}

// Save event ID after creation
if (pm.response.code === 201 && pm.request.url.toString().includes('/event/create')) {
    // Extract from response if available
}
```

---

## Performance Testing Checkpoints

### Load Testing Scenarios
1. **100 concurrent users** browsing events
2. **50 users** creating events simultaneously
3. **200 users** joining the same event
4. **1000 notifications** generated at once

### Expected Response Times
- Authentication: < 200ms
- Event listing: < 300ms
- Event creation: < 400ms
- Notification retrieval: < 250ms
- Profile operations: < 200ms

---

## Security Testing Checklist

- [ ] SQL Injection in search/filter fields
- [ ] XSS in event titles and descriptions
- [ ] JWT token manipulation
- [ ] Access other users' private data
- [ ] CSRF protection
- [ ] Rate limiting on authentication
- [ ] Password strength validation
- [ ] Email verification bypass
- [ ] Authorization bypass (access admin endpoints as user)
- [ ] Mass assignment vulnerabilities

---

## Database State Verification

After each test scenario, verify:
1. User records are created correctly
2. Events are linked to correct organizers
3. EventParticipant relationships are accurate
4. Notifications are generated for correct users
5. Timestamps are recorded properly
6. Cascade deletes work correctly

---

## Known Issues / Notes

⚠️ **Important Notes:**
1. Email verification requires actual email delivery (configure SMTP)
2. DateTime must be in ISO 8601 format
3. UUIDs are auto-generated, not user-provided
4. Admin role must be set in database manually
5. Dev endpoints only work when `app.security.enabled=false`

---

## Automation Test Skeleton

```javascript
describe('Event API Tests', () => {
  let authToken;
  let testEventId;
  
  beforeAll(async () => {
    // Register and login
    const loginResponse = await login('testuser', 'password');
    authToken = loginResponse.token;
  });
  
  test('Create event successfully', async () => {
    const response = await createEvent({
      title: 'Test Event',
      sport: 1,
      // ... other fields
    }, authToken);
    
    expect(response.status).toBe(201);
    expect(response.body).toBe('Event created successfully');
  });
  
  test('Get all events', async () => {
    const response = await getAllEvents(authToken);
    
    expect(response.status).toBe(200);
    expect(Array.isArray(response.body)).toBe(true);
  });
  
  // ... more tests
});
```

---

**Happy Testing! 🚀**

For detailed API documentation, see `API_DOCUMENTATION.md`
