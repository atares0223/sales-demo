package com.waaw.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waaw.common.exception.BusinessException;
import com.waaw.user.User;
import com.waaw.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save( User user) {
        return userRepository.save(user);
    }

    public User update( Long id,  User user) {
        User existingUser = findById(id);
        existingUser.setName(user.getName());
        existingUser.setAge(user.getAge());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setAddress(user.getAddress());
        existingUser.setCity(user.getCity());
        existingUser.setState(user.getState());
        existingUser.setZip(user.getZip());
        existingUser.setCountry(user.getCountry());
        existingUser.setPassword(user.getPassword());
        existingUser.setConfirmPassword(user.getConfirmPassword());
        return userRepository.save(existingUser);
    }

    public void deleteById( Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}

