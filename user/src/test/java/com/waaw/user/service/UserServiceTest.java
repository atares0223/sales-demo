package com.waaw.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.waaw.common.exception.BusinessException;
import com.waaw.user.User;
import com.waaw.user.UserApplication;

@ActiveProfiles("test")
@DisplayName("UserService Tests")
@SpringBootTest(classes = UserApplication.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        try {
            List<User> allUsers = userService.findAll();
            for (User user : allUsers) {
                userService.deleteById(user.getId());
            }
        } catch (Exception e) {
            // Ignore if no users exist
        }

        // Create test users without IDs (they will be auto-generated)
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
    @DisplayName("Should find user by id when user exists")
    void testFindById_WhenUserExists() {
        // Given
        User savedUser = userService.save(testUser);
        Long userId = savedUser.getId();

        // When
        User result = userService.findById(userId);

        // Then
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getAge(), result.getAge());
        assertEquals(testUser.getPhone(), result.getPhone());
        assertEquals(testUser.getAddress(), result.getAddress());
        assertEquals(testUser.getCity(), result.getCity());
        assertEquals(testUser.getState(), result.getState());
        assertEquals(testUser.getZip(), result.getZip());
        assertEquals(testUser.getCountry(), result.getCountry());
        assertEquals(testUser.getPassword(), result.getPassword());
        assertEquals(testUser.getConfirmPassword(), result.getConfirmPassword());
    }

    @Test
    @DisplayName("Should throw BusinessException when user not found")
    void testFindById_WhenUserNotFound() {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.findById(nonExistentId);
        });

        assertEquals("User not found with id: " + nonExistentId, exception.getMessage());
    }

    @Test
    @DisplayName("Should return all users")
    void testFindAll() {
        // Given
        User savedUser1 = userService.save(testUser);
        User savedUser2 = userService.save(testUser2);

        // When
        List<User> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getId().equals(savedUser1.getId())));
        assertTrue(result.stream().anyMatch(u -> u.getId().equals(savedUser2.getId())));
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testFindAll_WhenNoUsers() {
        // Given - database is clean from setUp

        // When
        List<User> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should save user successfully")
    void testSave_Success() {
        // Given
        User userToSave = new User();
        userToSave.setName("John Doe");
        userToSave.setAge(30);
        userToSave.setEmail("john.doe@example.com");
        userToSave.setPassword("password123");
        userToSave.setConfirmPassword("password123");

        // When
        User result = userService.save(userToSave);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(userToSave.getName(), result.getName());
        assertEquals(userToSave.getEmail(), result.getEmail());
        assertEquals(userToSave.getAge(), result.getAge());
        assertEquals(userToSave.getPassword(), result.getPassword());
        assertEquals(userToSave.getConfirmPassword(), result.getConfirmPassword());
    }

    @Test
    @DisplayName("Should update user successfully when user exists")
    void testUpdate_WhenUserExists() {
        // Given
        User savedUser = userService.save(testUser);
        Long userId = savedUser.getId();

        User updateData = new User();
        updateData.setName("Updated Name");
        updateData.setAge(35);
        updateData.setEmail("updated.email@example.com");
        updateData.setPhone("9876543210");
        updateData.setAddress("456 Updated St");
        updateData.setCity("Updated City");
        updateData.setState("UC");
        updateData.setZip("54321");
        updateData.setCountry("Updated Country");
        updateData.setPassword("newpassword123");
        updateData.setConfirmPassword("newpassword123");

        // When
        User result = userService.update(userId, updateData);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals(35, result.getAge());
        assertEquals("updated.email@example.com", result.getEmail());
        assertEquals("9876543210", result.getPhone());
        assertEquals("456 Updated St", result.getAddress());
        assertEquals("Updated City", result.getCity());
        assertEquals("UC", result.getState());
        assertEquals("54321", result.getZip());
        assertEquals("Updated Country", result.getCountry());
        assertEquals("newpassword123", result.getPassword());
        assertEquals("newpassword123", result.getConfirmPassword());
    }

    @Test
    @DisplayName("Should throw BusinessException when updating non-existent user")
    void testUpdate_WhenUserNotFound() {
        // Given
        Long nonExistentId = 999L;
        User updateData = new User();
        updateData.setName("Updated Name");

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.update(nonExistentId, updateData);
        });

        assertEquals("User not found with id: " + nonExistentId, exception.getMessage());
    }

    @Test
    @DisplayName("Should delete user successfully when user exists")
    void testDeleteById_WhenUserExists() {
        // Given
        User savedUser = userService.save(testUser);
        Long userId = savedUser.getId();

        // When
        userService.deleteById(userId);

        // Then
        assertThrows(BusinessException.class, () -> userService.findById(userId));
    }

    @Test
    @DisplayName("Should throw BusinessException when deleting non-existent user")
    void testDeleteById_WhenUserNotFound() {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.deleteById(nonExistentId);
        });

        assertEquals("User not found with id: " + nonExistentId, exception.getMessage());
    }



    @Test
    @DisplayName("Should update all user fields correctly")
    void testUpdate_AllFieldsUpdated() {
        // Given
        User savedUser = userService.save(testUser);
        Long userId = savedUser.getId();

        User updateData = new User();
        updateData.setName("New Name");
        updateData.setAge(40);
        updateData.setEmail("new.email@example.com");
        updateData.setPhone("1111111111");
        updateData.setAddress("999 New St");
        updateData.setCity("New City");
        updateData.setState("NS");
        updateData.setZip("99999");
        updateData.setCountry("New Country");
        updateData.setPassword("newpassword");
        updateData.setConfirmPassword("newpassword");

        // When
        User result = userService.update(userId, updateData);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("New Name", result.getName());
        assertEquals(40, result.getAge());
        assertEquals("new.email@example.com", result.getEmail());
        assertEquals("1111111111", result.getPhone());
        assertEquals("999 New St", result.getAddress());
        assertEquals("New City", result.getCity());
        assertEquals("NS", result.getState());
        assertEquals("99999", result.getZip());
        assertEquals("New Country", result.getCountry());
        assertEquals("newpassword", result.getPassword());
        assertEquals("newpassword", result.getConfirmPassword());
    }

}