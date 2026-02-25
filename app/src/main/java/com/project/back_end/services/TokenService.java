package com.yourpackage.service; // change package as needed

import com.yourpackage.repository.AdminRepository;
import com.yourpackage.repository.DoctorRepository;
import com.yourpackage.repository.PatientRepository;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secret;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // 1️⃣ Generate JWT Token (7 days expiry)
    public String generateToken(String identifier) {

        long expirationTime = 1000 * 60 * 60 * 24 * 7; // 7 days

        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2️⃣ Extract Identifier (username/email)
    public String extractIdentifier(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 3️⃣ Validate Token
    public boolean validateToken(String token, String userType) {

        try {
            String identifier = extractIdentifier(token);

            switch (userType.toLowerCase()) {

                case "admin":
                    return adminRepository.findByUsername(identifier) != null;

                case "doctor":
                    return doctorRepository.findByEmail(identifier) != null;

                case "patient":
                    return patientRepository.findByEmail(identifier) != null;

                default:
                    return false;
            }

        } catch (JwtException | IllegalArgumentException e) {
            return false; // Invalid or expired token
        }
    }

    // 4️⃣ Get Signing Key
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }
}