package org.valdon.abtests.controller.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.valdon.abtests.dto.user.UserResponse;
import org.valdon.abtests.security.UserPrincipal;
import org.valdon.abtests.service.user.UserService;

@RestController
@Validated
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/me")
    public UserResponse getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return userService.getUserById(userPrincipal.getId());
    }

}
