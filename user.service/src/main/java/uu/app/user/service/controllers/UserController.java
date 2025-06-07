
package uu.app.user.service.controllers;

import uu.app.user.service.entity.UserEntity;
import uu.app.user.service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity user) {
        log.info("Create User {}", user);
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        log.info("Get All Users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        Optional<UserEntity> user = userService.getUserById(id);
        log.info("Get User {}", user);
        return user.map(ResponseEntity::ok).orElseGet(() ->
                ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id,
                                                 @RequestBody UserEntity user) {
        UserEntity updatedUser = userService.updateUser(id, user);
        log.info("Update User {}", user);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        log.info("Delete User with Id {}", id);
        return ResponseEntity.noContent().build();
    }
}