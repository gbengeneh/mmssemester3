package com.semester3.user_service.controller;

import com.semester3.user_service.dto.AuthUserResponse;
import com.semester3.user_service.dto.RegisterRequest;
import com.semester3.user_service.entity.RefreshToken;
import com.semester3.user_service.entity.User;
import com.semester3.user_service.service.RefreshTokenService;
import com.semester3.user_service.service.UserService;
import com.semester3.user_service.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private UserService userService;
    private RefreshTokenService refreshTokenService;
    private JwtUtil jwtUtil;

    public AdminController(UserService userService, RefreshTokenService refreshTokenService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }
     @PostMapping("/create_user")
    public ResponseEntity<AuthUserResponse> createUser(@RequestBody RegisterRequest request){
        String roleName = request.getRole() !=null ? request.getRole(): "ROLE_USER";
        User created = userService.createUserWithRole(request, roleName);
        //Generate tokens
        String accessToken = jwtUtil.generateAccessToken(created.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(created);

        AuthUserResponse response = new AuthUserResponse(
                created.getId(),
                created.getUsername(),
                created.getEmail(),
                created.getRoles(),
                accessToken,
                refreshToken.getToken()
        );
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update-user-role/{userId}")
    public ResponseEntity<String> updateUserRole(@RequestBody Long userId, @RequestParam String roleName){
        userService.updateUserRole(userId, roleName);
        return ResponseEntity.ok("User role updated successfully");
    }
}