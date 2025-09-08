package
 com.prenotazioni.controller.auth;

import com.prenotazioni.model.Utente;
import com.prenotazioni.service.AuthService;
import com.prenotazioni.service.JwtService;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest request) {
        Utente utente = authService.login(request.getEmail(), request.getPassword());
        if (utente == null) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Credenziali non valide"),
                HttpStatus.UNAUTHORIZED
            );
        }
        return new ResponseEntity<>(
            Collections.singletonMap("token", jwtService.generateToken(utente)),
            HttpStatus.OK
        );
    }
}
