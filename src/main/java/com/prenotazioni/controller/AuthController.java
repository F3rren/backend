package
 com.prenotazioni.controller;

import com.prenotazioni.model.Utente;
import com.prenotazioni.service.AuthService;
import com.prenotazioni.service.JwtService;
import com.prenotazioni.dto.RegisterRequest;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @PostMapping("/register")
    public Object register(@RequestBody RegisterRequest request) {
        Utente utente = authService.register(request);
        if (utente == null) {
            return new org.springframework.http.ResponseEntity<>(
                java.util.Collections.singletonMap("error", "Email o username già esistenti"),
                org.springframework.http.HttpStatus.BAD_REQUEST
            );
        }
        return utente;
    }
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
        public Object login(@RequestBody LoginRequest request) {
            Utente utente = authService.login(request.getEmail(), request.getPassword());
            if (utente == null) {
                return new org.springframework.http.ResponseEntity<>(
                    java.util.Collections.singletonMap("error", "Credenziali non valide"),
                    org.springframework.http.HttpStatus.UNAUTHORIZED
                );
            }
            String token = jwtService.generateToken(utente);
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("token", token);
            
            return response;
    }

    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
