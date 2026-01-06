package com.waaw.user.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waaw.common.ApiResponse;
import com.waaw.user.User;
import com.waaw.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ApiResponse<User> getUserById( @PathVariable("id") Long id) {
        log.info("Finding user by id: {}", id);
        User user = userService.findById(id);
        return ApiResponse.success(user);
    }

    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        log.info("Finding all users");
        List<User> users = userService.findAll();
        return ApiResponse.success(users);
    }

    @PostMapping
    public ApiResponse<User> createUser( @RequestBody @Valid User user) {
        log.info("Creating user: {}", user);
        User savedUser = userService.save(user);
        return ApiResponse.success(savedUser);
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser( @PathVariable("id") Long id, 
                                         @RequestBody @Valid User user) {
        log.info("Updating user with id: {}", id);
        User updatedUser = userService.update(id, user);
        return ApiResponse.success(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser( @PathVariable("id") Long id) {
        log.info("Deleting user with id: {}", id);
        userService.deleteById(id);
        return ApiResponse.success("User deleted successfully");
    }
}

