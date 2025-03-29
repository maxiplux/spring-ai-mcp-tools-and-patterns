package app.quantun.springaimcp.controller;

import app.quantun.springaimcp.model.entity.Role;
import app.quantun.springaimcp.model.entity.User;
import app.quantun.springaimcp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "API for user management")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns a paginated list of users")
    public ResponseEntity<Page<User>> getAllUsers(
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.findAllUsers(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns a user by their ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserById(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.findUserById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Returns a user by their username")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "Username", required = true) @PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.findUserByUsername(username));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Returns a user by their email address")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Email address", required = true) @PathVariable String email) {
        try {
            return ResponseEntity.ok(userService.findUserByEmail(email));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Returns a paginated list of users with the specified role")
    public ResponseEntity<Page<User>> getUsersByRole(
            @Parameter(description = "User role", required = true) @PathVariable Role role,
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.findUsersByRole(role, pageable));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user data or username/email already exists")
    })
    public ResponseEntity<User> registerUser(
            @Parameter(description = "User registration details", required = true) @Valid @RequestBody User user) {
        try {
            return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user", description = "Updates an existing user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated user details", required = true) @Valid @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, user));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user by their ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "Add role to user", description = "Adds a role to an existing user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role added successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> addRoleToUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @Parameter(description = "Role to add", required = true) @RequestParam Role role) {
        try {
            return ResponseEntity.ok(userService.addRoleToUser(id, role));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/roles")
    @Operation(summary = "Remove role from user", description = "Removes a role from an existing user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role removed successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> removeRoleFromUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @Parameter(description = "Role to remove", required = true) @RequestParam Role role) {
        try {
            return ResponseEntity.ok(userService.removeRoleFromUser(id, role));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 