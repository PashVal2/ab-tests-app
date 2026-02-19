package org.valdon.abtests.controller.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.valdon.abtests.dto.UserRecord;
import org.valdon.abtests.service.user.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/{id}")
    public UserRecord getUserById(@PathVariable Long id) {
        userService.getUserById(1L);
        redisTemplate.opsForValue().set("a", Long.toString(id));
        return null;
    }

}
