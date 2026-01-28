package com.yeggi.moneymanager.service;

import com.yeggi.moneymanager.dto.AuthDTO;
import com.yeggi.moneymanager.dto.ProfileDTO;
import com.yeggi.moneymanager.entity.ProfileEntity;
import com.yeggi.moneymanager.repository.ProfileRepository;
import com.yeggi.moneymanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${money.manager.activation.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        // Registration logic to be implemented
        ProfileEntity newProfileEntity = toEntity(profileDTO);
        newProfileEntity.setActivationToken(UUID.randomUUID().toString());
        newProfileEntity=profileRepository.save(newProfileEntity);

        //send activation email logic can be added here
        String activationLink = activationURL+"/api/v1.0/activate?token=" + newProfileEntity.getActivationToken();
        String subject = "Activate your account";
        String body = "Please click the following link to activate your account: " + activationLink;
        emailService.sendMail(newProfileEntity.getEmail(), subject, body);

        ProfileDTO savedProfileDTO = toDTO(newProfileEntity);
        return savedProfileDTO;

    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .password(profileEntity.getPassword())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String token) {
        // Activation logic to be implemented
        ProfileEntity profileEntity = profileRepository.findByActivationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid activation token"));

        profileEntity.setIsActive(true);
        profileRepository.save(profileEntity);
        return true;
    }

    public boolean isAccountActive(String email) {
        ProfileEntity profileEntity = profileRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return profileEntity.getIsActive();
    }

    public ProfileEntity getCurrentProfile(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return profileRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found with email: " + email));
    }

    public ProfileDTO getPublicProfile(String email){
        ProfileEntity cuurentUser=null;
        if(email==null || email.isEmpty()){
            cuurentUser=getCurrentProfile();
        }
        else{
            cuurentUser=profileRepository.findByEmail(email)
                    .orElseThrow(()->new UsernameNotFoundException("User not found with email: " + email));
        }
        return ProfileDTO.builder()
                .id(cuurentUser.getId())
                .fullName(cuurentUser.getFullName())
                .email(cuurentUser.getEmail())
                .profileImageUrl(cuurentUser.getProfileImageUrl())
                .createdAt(cuurentUser.getCreatedAt())
                .updatedAt(cuurentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            // If authentication is successful, generate a token (e.g., JWT)
            String token=jwtUtil.generateToken(authDTO.getEmail());

            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDTO.getEmail())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}
