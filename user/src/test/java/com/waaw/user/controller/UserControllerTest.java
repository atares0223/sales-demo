package com.waaw.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waaw.common.exception.BusinessException;
import com.waaw.user.User;
import com.waaw.user.UserApplication;
import com.waaw.user.service.UserService;

@ActiveProfiles("test")
@DisplayName("UserController Tests")
@SpringBootTest(classes = UserApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;
    private ObjectMapper objectMapper;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // Clean up database before each test
        try {
            List<User> allUsers = userService.findAll();
            for (User user : allUsers) {
                userService.deleteById(user.getId());
            }
        } catch (Exception e) {
            // Ignore if no users exist
        }

        // Setup test data without IDs (they will be auto-generated)
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

        // Save test users to database
        testUser = userService.save(testUser);
        testUser2 = userService.save(testUser2);
    }

    @Test
    @DisplayName("Should get user by id successfully")
    void testGetUserById_Success() throws Exception {
        // Given
        Long userId = testUser.getId();

        // When & Then
        mockMvc.perform(get("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data.id").value(testUser.getId()))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.age").value(30))
                .andExpect(jsonPath("$.data.phone").value("1234567890"))
                .andExpect(jsonPath("$.data.address").value("123 Main St"))
                .andExpect(jsonPath("$.data.city").value("New York"))
                .andExpect(jsonPath("$.data.state").value("NY"))
                .andExpect(jsonPath("$.data.zip").value("10001"))
                .andExpect(jsonPath("$.data.country").value("USA"));
    }

    @Test
    @DisplayName("Should return error when user not found")
    void testGetUserById_NotFound() throws Exception {
        // Given
        Long userId = 999L;

        // When & Then
        mockMvc.perform(get("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // BusinessException is handled by GlobalExceptionHandler
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsers_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(testUser.getId()))
                .andExpect(jsonPath("$.data[0].name").value("John Doe"))
                .andExpect(jsonPath("$.data[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data[0].age").value(30))
                .andExpect(jsonPath("$.data[1].id").value(testUser2.getId()))
                .andExpect(jsonPath("$.data[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$.data[1].email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.data[1].age").value(25));
    }

    @Test
    @DisplayName("Should get empty list when no users exist")
    void testGetAllUsers_EmptyList() throws Exception {
        // Given - clean database from setUp

        // When & Then
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser_Success() throws Exception {
        // Given
        User userToCreate = new User();
        userToCreate.setName("New User");
        userToCreate.setAge(28);
        userToCreate.setEmail("new.user@example.com");
        userToCreate.setPhone("1111111111");
        userToCreate.setAddress("789 New St");
        userToCreate.setCity("New City");
        userToCreate.setState("NC");
        userToCreate.setZip("11111");
        userToCreate.setCountry("New Country");
        userToCreate.setPassword("password789");
        userToCreate.setConfirmPassword("password789");

        User createdUser = new User();
        createdUser.setId(3L);
        createdUser.setName("New User");
        createdUser.setAge(28);
        createdUser.setEmail("new.user@example.com");
        createdUser.setPhone("1111111111");
        createdUser.setAddress("789 New St");
        createdUser.setCity("New City");
        createdUser.setState("NC");
        createdUser.setZip("11111");
        createdUser.setCountry("New Country");
        createdUser.setPassword("password789");
        createdUser.setConfirmPassword("password789");

        when(userService.save(any(User.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data.id").value(3L))
                .andExpect(jsonPath("$.data.name").value("New User"))
                .andExpect(jsonPath("$.data.email").value("new.user@example.com"))
                .andExpect(jsonPath("$.data.age").value(28))
                .andExpect(jsonPath("$.data.phone").value("1111111111"))
                .andExpect(jsonPath("$.data.address").value("789 New St"))
                .andExpect(jsonPath("$.data.city").value("New City"))
                .andExpect(jsonPath("$.data.state").value("NC"))
                .andExpect(jsonPath("$.data.zip").value("11111"))
                .andExpect(jsonPath("$.data.country").value("New Country"));

        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should create user with minimal data")
    void testCreateUser_MinimalData() throws Exception {
        // Given
        User userToCreate = new User();
        userToCreate.setName("Minimal User");
        userToCreate.setEmail("minimal@example.com");
        userToCreate.setPassword("password");
        userToCreate.setConfirmPassword("password");

        User createdUser = new User();
        createdUser.setId(4L);
        createdUser.setName("Minimal User");
        createdUser.setEmail("minimal@example.com");
        createdUser.setPassword("password");
        createdUser.setConfirmPassword("password");

        when(userService.save(any(User.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data.id").value(4L))
                .andExpect(jsonPath("$.data.name").value("Minimal User"))
                .andExpect(jsonPath("$.data.email").value("minimal@example.com"));

        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser_Success() throws Exception {
        // Given
        Long userId = 1L;
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

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");
        updatedUser.setAge(35);
        updatedUser.setEmail("updated.email@example.com");
        updatedUser.setPhone("9876543210");
        updatedUser.setAddress("456 Updated St");
        updatedUser.setCity("Updated City");
        updatedUser.setState("UC");
        updatedUser.setZip("54321");
        updatedUser.setCountry("Updated Country");
        updatedUser.setPassword("newpassword123");
        updatedUser.setConfirmPassword("newpassword123");

        when(userService.update(eq(userId), any(User.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.data.email").value("updated.email@example.com"))
                .andExpect(jsonPath("$.data.age").value(35))
                .andExpect(jsonPath("$.data.phone").value("9876543210"))
                .andExpect(jsonPath("$.data.address").value("456 Updated St"))
                .andExpect(jsonPath("$.data.city").value("Updated City"))
                .andExpect(jsonPath("$.data.state").value("UC"))
                .andExpect(jsonPath("$.data.zip").value("54321"))
                .andExpect(jsonPath("$.data.country").value("Updated Country"));

        verify(userService, times(1)).update(eq(userId), any(User.class));
    }

    @Test
    @DisplayName("Should update user with partial data")
    void testUpdateUser_PartialData() throws Exception {
        // Given
        Long userId = 1L;
        User updateData = new User();
        updateData.setName("Partial Update");
        updateData.setAge(40);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Partial Update");
        updatedUser.setAge(40);
        updatedUser.setEmail("john.doe@example.com"); // Original email preserved

        when(userService.update(eq(userId), any(User.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Partial Update"))
                .andExpect(jsonPath("$.data.age").value(40))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));

        verify(userService, times(1)).update(eq(userId), any(User.class));
    }

    @Test
    @DisplayName("Should return error when updating non-existent user")
    void testUpdateUser_NotFound() throws Exception {
        // Given
        Long userId = 999L;
        User updateData = new User();
        updateData.setName("Updated Name");

        when(userService.update(eq(userId), any(User.class)))
                .thenThrow(new BusinessException("User not found with id: " + userId));

        // When & Then
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk()) // BusinessException is handled by GlobalExceptionHandler
                .andExpect(jsonPath("$.status").exists());

        verify(userService, times(1)).update(eq(userId), any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser_Success() throws Exception {
        // Given
        Long userId = 1L;
        doNothing().when(userService).deleteById(userId);

        // When & Then
        mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data").value("User deleted successfully"));

        verify(userService, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Should return error when deleting non-existent user")
    void testDeleteUser_NotFound() throws Exception {
        // Given
        Long userId = 999L;
        doThrow(new BusinessException("User not found with id: " + userId))
                .when(userService).deleteById(userId);

        // When & Then
        mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // BusinessException is handled by GlobalExceptionHandler
                .andExpect(jsonPath("$.status").exists());

        verify(userService, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Should return bad request for invalid create user request")
    void testCreateUser_InvalidRequest() throws Exception {
        // Given - Invalid user data (missing required fields)
        User invalidUser = new User();
        // Missing required fields like name, email, etc.

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()); // Validation error

        verify(userService, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid update user request")
    void testUpdateUser_InvalidRequest() throws Exception {
        // Given
        Long userId = 1L;
        User invalidUser = new User();
        // Missing required fields

        // When & Then
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()); // Validation error

        verify(userService, never()).update(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("Should handle malformed JSON in create user request")
    void testCreateUser_MalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))
                .andExpect(status().isBadRequest()); // Malformed JSON

        verify(userService, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle malformed JSON in update user request")
    void testUpdateUser_MalformedJson() throws Exception {
        // Given
        Long userId = 1L;

        // When & Then
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))
                .andExpect(status().isBadRequest()); // Malformed JSON

        verify(userService, never()).update(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("Should handle different content types")
    void testCreateUser_WrongContentType() throws Exception {
        // Given
        User userToCreate = new User();
        userToCreate.setName("Test User");
        userToCreate.setEmail("test@example.com");

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("plain text content"))
                .andExpect(status().isUnsupportedMediaType());

        verify(userService, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle very long user data")
    void testCreateUser_LongData() throws Exception {
        // Given
        User userToCreate = new User();
        userToCreate.setName("Very Long Name That Might Cause Issues In Some Systems But Should Work Fine");
        userToCreate.setEmail("very.long.email.address.that.might.be.problematic@example.com");
        userToCreate.setPhone("123456789012345678901234567890");
        userToCreate.setAddress("Very Long Address That Goes On And On And On And Should Still Work");
        userToCreate.setCity("Very Long City Name That Might Cause Issues");
        userToCreate.setState("LongState");
        userToCreate.setZip("123456789012345678901234567890");
        userToCreate.setCountry("Very Long Country Name That Might Cause Issues In Some Systems");
        userToCreate.setPassword("verylongpasswordthatmightcauseissues");
        userToCreate.setConfirmPassword("verylongpasswordthatmightcauseissues");

        User createdUser = new User();
        createdUser.setId(5L);
        createdUser.setName(userToCreate.getName());
        createdUser.setEmail(userToCreate.getEmail());
        createdUser.setPhone(userToCreate.getPhone());
        createdUser.setAddress(userToCreate.getAddress());
        createdUser.setCity(userToCreate.getCity());
        createdUser.setState(userToCreate.getState());
        createdUser.setZip(userToCreate.getZip());
        createdUser.setCountry(userToCreate.getCountry());
        createdUser.setPassword(userToCreate.getPassword());
        createdUser.setConfirmPassword(userToCreate.getConfirmPassword());

        when(userService.save(any(User.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data.id").value(5L))
                .andExpect(jsonPath("$.data.name").value(userToCreate.getName()))
                .andExpect(jsonPath("$.data.email").value(userToCreate.getEmail()));

        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle special characters in user data")
    void testCreateUser_SpecialCharacters() throws Exception {
        // Given
        User userToCreate = new User();
        userToCreate.setName("José María ñoño");
        userToCreate.setEmail("jose.maria@example.com");
        userToCreate.setPhone("+1-555-123-4567");
        userToCreate.setAddress("123 Main St. #4B");
        userToCreate.setCity("México City");
        userToCreate.setState("CA");
        userToCreate.setZip("90210-1234");
        userToCreate.setCountry("México");
        userToCreate.setPassword("p@ssw0rd!");
        userToCreate.setConfirmPassword("p@ssw0rd!");

        User createdUser = new User();
        createdUser.setId(6L);
        createdUser.setName(userToCreate.getName());
        createdUser.setEmail(userToCreate.getEmail());
        createdUser.setPhone(userToCreate.getPhone());
        createdUser.setAddress(userToCreate.getAddress());
        createdUser.setCity(userToCreate.getCity());
        createdUser.setState(userToCreate.getState());
        createdUser.setZip(userToCreate.getZip());
        createdUser.setCountry(userToCreate.getCountry());
        createdUser.setPassword(userToCreate.getPassword());
        createdUser.setConfirmPassword(userToCreate.getConfirmPassword());

        when(userService.save(any(User.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data.id").value(6L))
                .andExpect(jsonPath("$.data.name").value("José María ñoño"))
                .andExpect(jsonPath("$.data.email").value("jose.maria@example.com"))
                .andExpect(jsonPath("$.data.phone").value("+1-555-123-4567"))
                .andExpect(jsonPath("$.data.address").value("123 Main St. #4B"))
                .andExpect(jsonPath("$.data.city").value("México City"))
                .andExpect(jsonPath("$.data.country").value("México"));

        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle null values in optional fields")
    void testCreateUser_NullOptionalFields() throws Exception {
        // Given
        User userToCreate = new User();
        userToCreate.setName("Test User");
        userToCreate.setEmail("test@example.com");
        userToCreate.setPassword("password");
        userToCreate.setConfirmPassword("password");
        // Leaving optional fields null

        User createdUser = new User();
        createdUser.setId(7L);
        createdUser.setName("Test User");
        createdUser.setEmail("test@example.com");
        createdUser.setPassword("password");
        createdUser.setConfirmPassword("password");

        when(userService.save(any(User.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data.id").value(7L))
                .andExpect(jsonPath("$.data.name").value("Test User"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        verify(userService, times(1)).save(any(User.class));
    }
}