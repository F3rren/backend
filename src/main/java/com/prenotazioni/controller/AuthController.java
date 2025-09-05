package
 com.prenotazioni.controller;

import com.prenotazioni.model.Utente;
import com.prenotazioni.service.AuthService;
import com.prenotazioni.service.JwtService;
import com.prenotazioni.dto.RegisterRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    
    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @PostMapping("/register")
    public Object register(@RequestBody RegisterRequest request) {
        Utente utente = authService.register(request);
        if (utente == null) {
            java.util.Map<String, Object> error = new java.util.HashMap<>();
            error.put("success", false);
            error.put("message", "Email o username già esistenti");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        java.util.Map<String, Object> ok = new java.util.HashMap<>();
        ok.put("success", true);
        ok.put("message", "Registrazione avvenuta con successo");
        return new ResponseEntity<>(ok, HttpStatus.CREATED);
    }
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
        public Object login(@RequestBody LoginRequest request) {
            Utente utente = authService.login(request.getEmail(), request.getPassword());
            if (utente == null) {
                return new ResponseEntity<>(
                    java.util.Collections.singletonMap("error", "Credenziali non valide"),
                    HttpStatus.UNAUTHORIZED
                );
            }
            String token = jwtService.generateToken(utente);
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("token", token);
            
            return response;
    }

}
