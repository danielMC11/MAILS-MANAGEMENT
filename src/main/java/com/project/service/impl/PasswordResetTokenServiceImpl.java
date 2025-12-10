package com.project.service.impl;

import com.project.entity.PasswordResetToken;
import com.project.entity.Usuario;
import com.project.repository.PasswordResetTokenRepository;
import com.project.service.PasswordResetTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

	private PasswordResetTokenRepository passwordResetTokenRepository;


	public String createTokenForUser(Usuario user, int minutes) {

			PasswordResetToken passwordResetToken = passwordResetTokenRepository.save(
				PasswordResetToken.builder()
					.user(user)
					.token(UUID.randomUUID().toString())
					.expirationDate(LocalDateTime.now().plusMinutes(minutes))
					.build()
			);
			return passwordResetToken.getToken();
	}


	public PasswordResetToken findByToken(String token){
		return passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("TOKEN NO REGISTRADO"));
	}


	public PasswordResetToken findByUser(Usuario user){
		return passwordResetTokenRepository.findByUser(user).orElse(null);
	}

	public boolean isTokenExpired(PasswordResetToken passwordResetToken){
		if(passwordResetToken != null)
			return passwordResetToken.getExpirationDate().isBefore(LocalDateTime.now());
		return false;
	}


	public void deleteToken(PasswordResetToken passwordResetToken){
		passwordResetTokenRepository.delete(passwordResetToken);
	}


}
