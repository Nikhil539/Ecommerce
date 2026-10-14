package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.UserResponse;
import com.example.backend.model.User;
import com.example.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")

    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
            ){
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/me")
//    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
//
//        return ResponseEntity.ok(
//                "Logged in as: " + authentication.getName()
//        );
//    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        return ResponseEntity.ok(
                "User: " + authentication.getName()
                        + " | Authorities: " + authentication.getAuthorities()
        );
    }


    @PostMapping("/create-admin")

    public ResponseEntity<User> createAdmin(@RequestBody RegisterRequest request){
        User admin = authService.createAdmin(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }
}
