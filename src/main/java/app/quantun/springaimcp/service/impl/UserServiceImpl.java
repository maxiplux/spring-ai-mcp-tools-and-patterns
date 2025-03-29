package app.quantun.springaimcp.service.impl;

import app.quantun.springaimcp.model.entity.Role;
import app.quantun.springaimcp.model.entity.User;
import app.quantun.springaimcp.repository.UserRepository;
import app.quantun.springaimcp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));
    }

    @Override
    public Page<User> findUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    @Override
    public User registerUser(User user) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Encode password
        user.setPassword(user.getPassword());

        // Set default role if none provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.addRole(Role.USER);
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User user = findUserById(id);

        // Update basic info
        user.setEmail(userDetails.getEmail());

        // Only update password if provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword());
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User addRoleToUser(Long userId, Role role) {
        User user = findUserById(userId);
        user.addRole(role);
        return userRepository.save(user);
    }

    @Override
    public User removeRoleFromUser(Long userId, Role role) {
        User user = findUserById(userId);
        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
} 