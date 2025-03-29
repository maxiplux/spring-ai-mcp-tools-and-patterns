package app.quantun.springaimcp.service;

import app.quantun.springaimcp.model.entity.Role;
import app.quantun.springaimcp.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<User> findAllUsers(Pageable pageable);

    User findUserById(Long id);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    Page<User> findUsersByRole(Role role, Pageable pageable);

    User registerUser(User user);

    User updateUser(Long id, User userDetails);

    void deleteUser(Long id);

    User addRoleToUser(Long userId, Role role);

    User removeRoleFromUser(Long userId, Role role);

    boolean existsById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
} 