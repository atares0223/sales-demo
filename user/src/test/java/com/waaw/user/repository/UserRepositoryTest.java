package com.waaw.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.waaw.user.User;
import com.waaw.user.UserApplication;

@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
@SpringBootTest(classes = UserApplication.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Clear repository before each test
        userRepository.deleteAll();

        // Create test users
        testUser = new User();
        testUser.setName("John Doe");
        testUser.setAge(30);
        testUser.setEmail("john.doe@example.com");
        testUser.setPhone("1234567890");
        testUser.setAddress("123 Main St");
        testUser.setCity("New York");
        testUser.setState("NY");
        testUser.setZip("10001");
        testUser.setCountry("USA");
        testUser.setPassword("password123");
        testUser.setConfirmPassword("password123");

        testUser2 = new User();
        testUser2.setName("Jane Smith");
        testUser2.setAge(25);
        testUser2.setEmail("jane.smith@example.com");
        testUser2.setPhone("0987654321");
        testUser2.setAddress("456 Oak St");
        testUser2.setCity("Los Angeles");
        testUser2.setState("CA");
        testUser2.setZip("90210");
        testUser2.setCountry("USA");
        testUser2.setPassword("password456");
        testUser2.setConfirmPassword("password456");
    }

    @Test
    @DisplayName("Should save user successfully")
    void testSave_Success() {
        // When
        User savedUser = userRepository.save(testUser);

        // Then
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(testUser.getName(), savedUser.getName());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        assertEquals(testUser.getAge(), savedUser.getAge());
        assertEquals(testUser.getPhone(), savedUser.getPhone());
        assertEquals(testUser.getAddress(), savedUser.getAddress());
        assertEquals(testUser.getCity(), savedUser.getCity());
        assertEquals(testUser.getState(), savedUser.getState());
        assertEquals(testUser.getZip(), savedUser.getZip());
        assertEquals(testUser.getCountry(), savedUser.getCountry());
        assertEquals(testUser.getPassword(), savedUser.getPassword());
        assertEquals(testUser.getConfirmPassword(), savedUser.getConfirmPassword());
    }

    @Test
    @DisplayName("Should find user by id when user exists")
    void testFindById_WhenUserExists() {
        // Given
        User savedUser = userRepository.save(testUser);

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals(savedUser.getName(), foundUser.get().getName());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty optional when user not found")
    void testFindById_WhenUserNotFound() {
        // When
        Optional<User> foundUser = userRepository.findById(999L);

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find all users")
    void testFindAll() {
        // Given
        User user1 = userRepository.save(testUser);
        User user2 = userRepository.save(testUser2);

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(user1.getId())));
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(user2.getId())));
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testFindAll_WhenNoUsers() {
        // When
        List<User> users = userRepository.findAll();

        // Then
        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("Should check if user exists by id")
    void testExistsById() {
        // Given
        User savedUser = userRepository.save(testUser);

        // When & Then
        assertTrue(userRepository.existsById(savedUser.getId()));
        assertFalse(userRepository.existsById(999L));
    }

    @Test
    @DisplayName("Should delete user by id")
    void testDeleteById() {
        // Given
        User savedUser = userRepository.save(testUser);
        assertTrue(userRepository.existsById(savedUser.getId()));

        // When
        userRepository.deleteById(savedUser.getId());

        // Then
        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    @DisplayName("Should delete all users")
    void testDeleteAll() {
        // Given
        userRepository.save(testUser);
        userRepository.save(testUser2);

        assertEquals(2, userRepository.count());

        // When
        userRepository.deleteAll();

        // Then
        assertEquals(0, userRepository.count());
    }

    @Test
    @DisplayName("Should count users")
    void testCount() {
        // Given
        assertEquals(0, userRepository.count());

        userRepository.save(testUser);
        assertEquals(1, userRepository.count());

        userRepository.save(testUser2);
        assertEquals(2, userRepository.count());
    }

    @Test
    @DisplayName("Should save multiple users with different data")
    void testSave_MultipleUsers() {
        // Given
        User user3 = new User();
        user3.setName("Bob Johnson");
        user3.setAge(45);
        user3.setEmail("bob.johnson@example.com");
        user3.setPhone("5555555555");
        user3.setAddress("789 Pine St");
        user3.setCity("Chicago");
        user3.setState("IL");
        user3.setZip("60601");
        user3.setCountry("USA");
        user3.setPassword("password789");
        user3.setConfirmPassword("password789");

        // When
        User savedUser1 = userRepository.save(testUser);
        User savedUser2 = userRepository.save(testUser2);
        User savedUser3 = userRepository.save(user3);

        // Then
        assertNotNull(savedUser1.getId());
        assertNotNull(savedUser2.getId());
        assertNotNull(savedUser3.getId());

        List<User> allUsers = userRepository.findAll();
        assertEquals(3, allUsers.size());
    }

    @Test
    @DisplayName("Should handle updating existing user")
    void testSave_UpdateExistingUser() {
        // Given
        User savedUser = userRepository.save(testUser);
        savedUser.setName("Updated Name");
        savedUser.setAge(35);

        // When
        User updatedUser = userRepository.save(savedUser);

        // Then
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals(35, updatedUser.getAge());

        // Verify only one user exists
        assertEquals(1, userRepository.count());
    }
}