package uu.app.user.service.controllers;

import uu.app.user.service.util.JwtUtil;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String role = user.get("role");
        String token = jwtUtil.generateToken(username, role);
        return Map.of("token", token);
    }
}
