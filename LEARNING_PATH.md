# 🎯 Your Learning Path - Unit Testing Step-by-Step

## 📚 What I've Created for You

1. **[UNIT_TESTING_GUIDE.md](UNIT_TESTING_GUIDE.md)** - Complete guide with theory and examples
2. **[AuthServiceTestExample.java](src/test/java/UnitTests/Service/AuthServiceTestExample.java)** - Working code examples

---

## ✅ Step 1: Study the Materials (30 minutes)

### Read in this order:

1. Open **UNIT_TESTING_GUIDE.md**
   - Read the "5-Step Testing Pattern" section
   - Understand ARRANGE, ACT, ASSERT, VERIFY
   
2. Open **AuthServiceTestExample.java**
   - Read all the comments carefully
   - Run the tests to see them pass
   - Understand what each test does

3. Compare with your current **AuthServiceTest.java**
   - See what's different
   - Notice the patterns

---

## ✅ Step 2: Add Missing Tests to AuthService (15 minutes)

### Copy these 4 tests from `AuthServiceTestExample.java` to `AuthServiceTest.java`:

At the end of AuthServiceTest.java, add:

```java
@Test
@DisplayName("Should successfully verify user with valid token")
void verifyUser_success() throws Exception {
    // ... copy from example file ...
}

@Test
@DisplayName("Should throw exception when token is invalid (not found)")
void verifyUser_invalidToken_throwsException() {
    // ... copy from example file ...
}

@Test
@DisplayName("Should throw exception when token is expired")
void verifyUser_expiredToken_throwsException() {
    // ... copy from example file ...
}

@Test
@DisplayName("Should throw exception when logging in with unverified account")
void login_unverifiedUser_throwsException() {
    // ... copy from example file ...
}
```

### Then update your @BeforeEach to include the new repositories:

```java
@BeforeEach
void setUp() {
    authRepository = mock(AuthRepository.class);
    secureTokenRepository = mock(SecureTokenRepository.class);
    userRepository = mock(UserRepository.class);
    passwordEncoder = new BCryptPasswordEncoder();
    jwtutil = mock(JWTutil.class);
    authService = new AuthService(authRepository, passwordEncoder, jwtutil, null, secureTokenRepository, userRepository, null);
}
```

### Run the tests
```bash
mvn test -Dtest=AuthServiceTest
```

---

## ✅ Step 3: Practice Writing Your First Test Alone (20 minutes)

### Task: Add `leaveEvent_success` to UserServiceTest

**What the method does:** (look at UserService.java line 156-178)
1. Find user by username
2. Find their EventParticipant record
3. Get event details
4. Get organizer
5. Send notification
6. Delete participation
7. Return true

**Your test should:**

```java
@Test
@DisplayName("Should successfully remove user from event")
void leaveEvent_success() {
    // ARRANGE
    String username = "testuser";
    UUID eventId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID organizerId = UUID.randomUUID();
    
    // Create test objects
    User user = new User();
    user.setId(userId);
    user.setUsername(username);
    
    User organizer = new User();
    organizer.setId(organizerId);
    
    Event event = new Event();
    event.setId(eventId);
    event.setOrganizer(organizer);
    
    EventParticipant participant = new EventParticipant(userId, eventId);
    
    // Configure mocks
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(eventParticipantRepository.findByUserIdAndEventId(userId, eventId))
        .thenReturn(Optional.of(participant));
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
    
    // ACT
    boolean result = userService.leaveEvent(username, eventId);
    
    // ASSERT
    assertTrue(result, "Should successfully leave event");
    
    // VERIFY
    verify(userRepository).findByUsername(username);
    verify(eventParticipantRepository).findByUserIdAndEventId(userId, eventId);
    verify(eventRepository, times(2)).findById(eventId);  // Called twice in the method
    verify(notificationService).notifyPlayerLeft(event, organizer, username);
    verify(eventParticipantRepository).delete(participant);
}
```

---

## ✅ Step 4: Write Failure Tests (15 minutes)

Now add the failure scenarios for `leaveEvent`:

```java
@Test
@DisplayName("Should throw exception when user not found")
void leaveEvent_userNotFound_throwsException() {
    // ARRANGE
    String username = "unknown";
    UUID eventId = UUID.randomUUID();
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
    
    // ACT & ASSERT
    Exception ex = assertThrows(IllegalArgumentException.class, () ->
        userService.leaveEvent(username, eventId)
    );
    
    assertEquals("User not found", ex.getMessage());
    verify(eventParticipantRepository, never()).delete(any());
}

@Test
@DisplayName("Should throw exception when user is not participant")
void leaveEvent_userNotParticipant_throwsException() {
    // ARRANGE
    String username = "testuser";
    UUID eventId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    
    User user = new User();
    user.setId(userId);
    
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(eventParticipantRepository.findByUserIdAndEventId(userId, eventId))
        .thenReturn(Optional.empty());
    
    // ACT & ASSERT
    Exception ex = assertThrows(IllegalArgumentException.class, () ->
        userService.leaveEvent(username, eventId)
    );
    
    assertEquals("User is not a participant of the event", ex.getMessage());
    verify(eventParticipantRepository, never()).delete(any());
}
```

---

## ✅ Step 5: Continue with Other Missing Tests

### In UserServiceTest, add:

- `addPreferredSport_success`
- `addPreferredSport_userNotFound_throwsException`
- `removePreferredSport_success`
- `removePreferredSport_sportNotFound_throwsException`
- `getAllUserInfoAdmin_returnsAllUsers`

### In EventServiceTest, add:

- `getAllEvents_returnsAllEvents`
- `getHostedEventsUpcoming_success`
- `cancelEvent_success`
- `updateEvent_success`
- etc.

---

## 📝 Quick Checklist for Each Test

Before moving to the next test, check:

- [ ] Test has `@Test` annotation
- [ ] Test has `@DisplayName` with clear description
- [ ] Test name follows `methodName_scenario_expectedResult` pattern
- [ ] ARRANGE section creates all needed data
- [ ] All mocks configured with `when()`
- [ ] ACT section calls exactly ONE method
- [ ] ASSERT checks the return value/state
- [ ] VERIFY checks all important mock calls
- [ ] VERIFY checks what DIDN'T happen (using `never()`)

---

## 🎓 Learning Milestones

### Beginner (You are here)
- ✅ Understand the 5-step pattern
- ✅ Can read and understand existing tests
- ⏳ Can copy and modify test examples
- ⏳ Can write simple success tests alone

### Intermediate (Your goal)
- Write both success and failure tests
- Use `verify()` effectively
- Test edge cases
- Understand when to mock vs use real objects

### Advanced (Future)
- Write integration tests
- Use ArgumentCaptor for complex verification
- Test async/concurrent code
- Design testable code

---

## 🆘 When You're Stuck

1. **Look at the service method** - What does it do?
2. **Check UNIT_TESTING_GUIDE.md** - Find similar example
3. **Look at AuthServiceTestExample** - Copy the pattern
4. **Check your checklist** - Did you miss a step?

---

## 📊 Track Your Progress

Current Status:
```
AuthService Tests: 6/10 complete ✅ (after adding the 4 new ones)
EventService Tests: 3/21 complete 🔄
UserService Tests: 6/13 complete 🔄
```

Target for Today:
- ✅ Add 4 tests to AuthService
- ⏳ Add 3 tests to UserService (leaveEvent scenarios)

---

## 🚀 Next Steps

After you've added the tests mentioned above:

1. **Run all tests:** `mvn test`
2. **Check coverage:** See how much code you're testing
3. **Continue with EventService** - It has the most missing tests
4. **Create tests for other services** - NotificationService, FriendshipService, etc.

---

**Remember:** Testing is a skill. You'll get better with each test you write!

**Your mantra:** "Arrange, Act, Assert, Verify" 🔄
