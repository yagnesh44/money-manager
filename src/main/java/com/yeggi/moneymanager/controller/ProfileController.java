package com.yeggi.moneymanager.controller;

import com.yeggi.moneymanager.dto.AuthDTO;
import com.yeggi.moneymanager.dto.CategoryDTO;
import com.yeggi.moneymanager.dto.ProfileDTO;
import com.yeggi.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean activated = profileService.activateProfile(token);
        if (activated) {
            return ResponseEntity.ok("Profile activated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid activation token.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
       try{
           if(!profileService.isAccountActive(authDTO.getEmail())){
               return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                       Map.of("error","Account is not activated. Please check your email for activation link.")
               );
           }
           Map<String,Object> response = profileService.authenticateAndGenerateToken(authDTO);
           return ResponseEntity.ok(response);
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                   Map.of("error","Invalid email or password")
           );
       }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("The application is running successfully.");
    }
}
