package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.exception.DuplicateEmailException;
import org.example.tintuctacgia.exception.UserNotFoundException;
import org.example.tintuctacgia.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    // REGISTER
    public User register(User user) {

        // Check email tồn tại
        if (userRepository.existsByEmail(user.getEmail())) {

            throw new DuplicateEmailException(
                    "Email đã được sử dụng: " + user.getEmail()
            );
        }

        log.info(
                "Registering user: {}",
                user.getEmail()
        );

        // Encode password
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }

    // DELETE USER
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(id)
                );

        log.info(
                "Deleting user id: {}",
                id
        );

        userRepository.delete(user);
    }

    // UPDATE USER
    public User updateUser(
            Long id,
            User updatedUser
    ) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(id)
                );

        // Check email duplicate
        if (!user.getEmail().equals(updatedUser.getEmail())
                && userRepository.existsByEmail(
                updatedUser.getEmail()
        )) {

            throw new DuplicateEmailException(
                    "Email đã được sử dụng: "
                            + updatedUser.getEmail()
            );
        }

        log.info(
                "Updating user id: {}",
                id
        );

        // Update fields
        user.setName(updatedUser.getName());

        user.setEmail(updatedUser.getEmail());

        user.setDateOfBirth(
                updatedUser.getDateOfBirth()
        );

        // Optional update password
        if (updatedUser.getPassword() != null
                && !updatedUser.getPassword().isEmpty()) {

            user.setPassword(
                    passwordEncoder.encode(
                            updatedUser.getPassword()
                    )
            );
        }

        return userRepository.save(user);
    }
}