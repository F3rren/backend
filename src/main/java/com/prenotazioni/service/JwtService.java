package com.prenotazioni.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import com.prenotazioni.model.Utente;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
	private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private final long EXPIRATION = 1000 * 60 * 60; // 1 ora

	public String generateToken(Utente utente) {
		return Jwts.builder()
				.setSubject(utente.getEmail())
				.claim("id", utente.getId())
				.claim("ruolo", utente.getRuolo())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
				.signWith(key)
				.compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getEmailFromToken(String token) {
		return Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
