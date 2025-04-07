package app.quantun.springaimcp.service.impl;

import app.quantun.springaimcp.model.entity.Role;
import app.quantun.springaimcp.model.entity.User;
import app.quantun.springaimcp.repository.UserRepository;
import app.quantun.springaimcp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
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
    @Tool(description = "Find all users with pagination")
    public Page<User> findAllUsers(@ToolParam(description = "Pagination settings") Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Tool(description = "Find user by ID")
    public User findUserById(@ToolParam(description = "User ID") Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    @Override
    @Tool(description = "Find user by email")
    public User findUserByEmail(@ToolParam(description = "User email") String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));
    }



    @Override
    @Tool(description = "Find user by username")
    public User findUserByUsername(@ToolParam(description = "Username") String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));
    }

    @Override
    @Tool(description = "Find user by role")
    public Page<User> findUsersByRole(@ToolParam(description = "Role Name")  Role role, @ToolParam(description = "Pagination settings pageable")  Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }


    @Override
    @Tool(description = "Create a new user")
    public User registerUser(@ToolParam(description = "User object with details to save") User user) {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + user.getEmail());
        }

        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken: " + user.getUsername());
        }

        // Encode password
        user.setPassword(user.getPassword());

        return userRepository.save(user);
    }



    @Override
    @Tool(description = "Update an existing user")
    public User updateUser(
            @ToolParam(description = "ID of the user to update") Long id, 
            @ToolParam(description = "User object with updated details") User userDetails) {
        User user = findUserById(id);
        
        // Check email uniqueness if changed
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + userDetails.getEmail());
        }
        
        // Check username uniqueness if changed
        if (!user.getUsername().equals(userDetails.getUsername()) && 
            userRepository.findByUsername(userDetails.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken: " + userDetails.getUsername());
        }
        

        user.setEmail(userDetails.getEmail());
        user.setUsername(userDetails.getUsername());
        
        // Only update password if provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword());
        }
        
        return userRepository.save(user);
    }

    @Override
    @Tool(description = "Delete a user by ID")
    public void deleteUser(@ToolParam(description = "ID of the user to delete") Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Tool(description = "Add a role to a user")
    public User addRoleToUser(
            @ToolParam(description = "ID of the user") Long userId,
            @ToolParam(description = "Role to add") Role role) {
        User user = findUserById(userId);

        if (user.getRoles().contains(role)) {
            throw new IllegalArgumentException("User already has the specified role");
        }

        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Override
    @Tool(description = "Remove a role from a user")
    public User removeRoleFromUser(
            @ToolParam(description = "ID of the user") Long userId,
            @ToolParam(description = "Role to remove") Role role) {
        User user = findUserById(userId);

        if (!user.getRoles().contains(role)) {
            throw new IllegalArgumentException("User does not have the specified role");
        }

        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    @Tool(description = "Check if a user exists by ID")
    @Override
    public boolean existsById(@ToolParam(description = "ID of the user to check") Long id) {
        return userRepository.existsById(id);
    }
    
    @Tool(description = "Check if an email is already registered")
    @Override
    public boolean existsByEmail(@ToolParam(description = "Email to check") String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    @Override
    @Tool(description = "Check if a username is already taken")
    public boolean existsByUsername(@ToolParam(description = "Username to check") String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
