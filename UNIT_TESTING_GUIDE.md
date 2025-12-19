# 📚 Complete Unit Testing Guide for Java Spring Boot

## 🎯 The 5-Step Testing Pattern (MEMORIZE THIS!)

Every unit test should follow this structure:

```java
@Test
@DisplayName("Should do X when Y happens")
void methodName_scenario_expectedResult() {
    // 1️⃣ ARRANGE - Set up test data and mock behavior
    // 2️⃣ ACT - Execute the method you're testing
    // 3️⃣ ASSERT - Verify the results are correct
    // 4️⃣ VERIFY - Check mocks were called correctly
}
```

---

## 📖 Learning Example: verifyUser_success

Let me walk you through creating your first complete test:

```java
@Test
@DisplayName("Should successfully verify user with valid token")
void verifyUser_success() throws Exception {
    // ========================================
    // STEP 1: ARRANGE - Create test data
    // ========================================
    
    // Create the input we'll pass to the method
    String token = "valid-token-123";
    UUID userId = UUID.randomUUID();
    
    // Create a user who is NOT verified yet
    User user = new User("testuser", "test@example.com", "hashedpass");
    user.setId(userId);
    user.setVerified(false);  // ← Starting state
    
    // Create a valid, non-expired token
    SecureTokenEmail secureToken = new SecureTokenEmail();
    secureToken.setToken(token);
    secureToken.setUser(user);
    secureToken.setExpiresAt(LocalDateTime.now().plusHours(1));  // Expires in future
    
    // Configure mocks to return our test data
    when(secureTokenRepository.findByToken(token)).thenReturn(secureToken);
    when(userRepository.getOne(userId)).thenReturn(user);
    
    // ========================================
    // STEP 2: ACT - Call the method
    // ========================================
    boolean result = authService.verifyUser(token);
    
    // ========================================
    // STEP 3: ASSERT - Check results
    // ========================================
    assertTrue(result, "Verification should succeed");
    assertTrue(user.isVerified(), "User should be marked as verified");
    
    // ========================================
    // STEP 4: VERIFY - Check mock interactions
    // ========================================
    verify(secureTokenRepository).findByToken(token);
    verify(userRepository).getOne(userId);
    verify(userRepository).save(user);
    verify(secureTokenRepository).delete(secureToken);
}
```

---

## 🔑 Key Concepts Explained

### 1. What are Mocks?

Mocks are **fake objects** that replace real dependencies. Instead of calling a real database, you tell the mock what to return:

```java
// Create a fake repository
private UserRepository userRepository = mock(UserRepository.class);

// Tell it what to return when findByUsername is called
when(userRepository.findByUsername("john")).thenReturn(Optional.of(someUser));
```

**Why?** Tests should be fast and not depend on databases/networks.

---

### 2. when() vs verify()

**`when()`** - Sets up BEFORE calling your method:
```java
// "When findByEmail is called, return empty"
when(authRepository.findByEmail(email)).thenReturn(Optional.empty());
```

**`verify()`** - Checks AFTER calling your method:
```java
// "Did save() get called with any User?"
verify(authRepository).save(any(User.class));
```

---

### 3. Testing Exceptions

Use `assertThrows` to test error cases:

```java
@Test
void verifyUser_invalidToken_throwsException() {
    // ARRANGE
    String invalidToken = "bad-token";
    when(secureTokenRepository.findByToken(invalidToken)).thenReturn(null);
    
    // ACT & ASSERT - Expect an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> 
        authService.verifyUser(invalidToken)
    );
    
    // Check the error message
    assertEquals("Invalid or expired token", exception.getMessage());
    
    // Make sure nothing was saved (because it failed)
    verify(userRepository, never()).save(any(User.class));
}
```

---

### 4. verify() with Conditions

```java
// Verify it was called exactly once
verify(repository).save(user);

// Verify it was NEVER called
verify(repository, never()).delete(anything);

// Verify it was called with specific argument
verify(jwtutil).generateToken("john");

// Verify it was called with any User object
verify(repository).save(any(User.class));
```

---

### 5. assertAll() for Multiple Checks

Instead of:
```java
assertEquals(username, user.getUsername());
assertEquals(email, user.getEmail());
assertEquals(true, user.isVerified());
```

Use:
```java
assertAll(
    () -> assertEquals(username, user.getUsername()),
    () -> assertEquals(email, user.getEmail()),
    () -> assertTrue(user.isVerified())
);
```

**Why?** If the first assertion fails, you still see ALL failures, not just the first one.

---

## 🎓 Practice Exercise: Write Your Own Test

**Task:** Write a test for `leaveEvent_success` in UserService

**The Method Does:**
1. Find user by username
2. Find their participation record
3. Delete the participation
4. Return true

**Your Test Should:**
```java
@Test
@DisplayName("Should successfully remove user from event")
void leaveEvent_success() {
    // ARRANGE
    String username = "john";
    UUID eventId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    
    // Create a user
    User user = new User();
    user.setId(userId);
    
    // Create a participation record
    EventParticipant participant = new EventParticipant(userId, eventId);
    
    // Configure mocks
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(eventParticipantRepository.findByUserIdAndEventId(userId, eventId))
        .thenReturn(Optional.of(participant));
    
    // ACT
    boolean result = userService.leaveEvent(username, eventId);
    
    // ASSERT
    assertTrue(result);
    
    // VERIFY
    verify(userRepository).findByUsername(username);
    verify(eventParticipantRepository).findByUserIdAndEventId(userId, eventId);
    verify(eventParticipantRepository).delete(participant);
}
```

---

## ✅ Checklist for Every Test

Before you finish a test, ask yourself:

- [ ] Did I use `@DisplayName` to explain what this tests?
- [ ] Did I create all necessary test data in ARRANGE?
- [ ] Did I configure all mocks with `when()`?
- [ ] Did I call ONE method in ACT?
- [ ] Did I check the return value in ASSERT?
- [ ] Did I verify all important mock calls with `verify()`?
- [ ] Did I test what should NOT happen using `never()`?

---

## 🚀 Common Patterns

### Testing a Success Case
```java
@Test
void createSomething_success() {
    // ARRANGE: Set up valid data and happy path mocks
    when(repository.findByName(name)).thenReturn(Optional.empty());
    when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    
    // ACT: Call the method
    Result result = service.createSomething(params);
    
    // ASSERT: Check it worked
    assertNotNull(result);
    assertEquals(expected, result.getValue());
    
    // VERIFY: Check the right methods were called
    verify(repository).save(any());
}
```

### Testing a Failure Case
```java
@Test
void createSomething_alreadyExists_throwsException() {
    // ARRANGE: Set up the failure condition
    when(repository.findByName(name)).thenReturn(Optional.of(existing));
    
    // ACT & ASSERT: Expect an exception
    Exception ex = assertThrows(IllegalArgumentException.class, () ->
        service.createSomething(params)
    );
    
    assertEquals("Already exists", ex.getMessage());
    
    // VERIFY: Make sure it didn't try to save
    verify(repository, never()).save(any());
}
```

---

## 📝 Your Assignment

Add these 4 tests to AuthServiceTest.java:

1. ✅ `verifyUser_success` (shown above)
2. ✅ `verifyUser_invalidToken_throwsException`
3. ✅ `verifyUser_expiredToken_throwsException`
4. ✅ `login_unverifiedUser_throwsException`

**Study the patterns, then try creating tests for the other services yourself!**

---

## 🆘 Quick Reference

### Common Imports
```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
```

### Mock Setup
```java
@BeforeEach
void setUp() {
    repository = mock(SomeRepository.class);
    service = new SomeService(repository);
}
```

### Common Matchers
```java
any()              // Any object
any(User.class)    // Any User object
eq("value")        // Exact value
anyString()        // Any string
anyInt()           // Any integer
```

---

**Remember:** Testing is like teaching someone to use your code. If the test is confusing, your code might be too!
