package app.quantun.springaimcp.service;

import app.quantun.springaimcp.model.entity.Role;
import app.quantun.springaimcp.model.entity.User;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<User> findAllUsers(Pageable pageable);

    User findUserById(Long id);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    Page<User> findUsersByRole(Role role, Pageable pageable);




    User updateUser(Long id, User userDetails);

    void deleteUser(Long id);

    User addRoleToUser(Long userId, Role role);

    User removeRoleFromUser(Long userId, Role role);


    @Tool(description = "Check if a user exists by ID")
    boolean existsById(@ToolParam(description = "ID of the user to check") Long id);

    @Tool(description = "Check if an email is already registered")
    boolean existsByEmail(@ToolParam(description = "Email to check") String email);

    boolean existsByUsername(String username);


    User registerUser(User regularUser);
}