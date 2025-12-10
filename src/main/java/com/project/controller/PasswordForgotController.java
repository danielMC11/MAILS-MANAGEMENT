package com.project.controller;


import com.project.dto.PasswordForgotRequest;
import com.project.entity.PasswordResetToken;
import com.project.entity.Usuario;
import com.project.repository.UsuarioRepository;
import com.project.service.EmailService;
import com.project.service.PasswordResetTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RequestMapping("/api/v1/forgot-password")
@RequiredArgsConstructor
@RestController
public class PasswordForgotController {


	private UsuarioRepository usuarioRepository;
	private PasswordResetTokenService passwordResetTokenService;
	private EmailService emailService;



	@PostMapping
	public ResponseEntity<?> processForgotPassword(@Valid @RequestBody PasswordForgotRequest passwordForgotRequest) {

		Usuario user = usuarioRepository.findByCorreo(passwordForgotRequest.email()).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST));

		PasswordResetToken passwordResetToken = passwordResetTokenService.findByUser(user);

		if(passwordResetToken != null){

			if(!passwordResetTokenService.isTokenExpired(passwordResetToken)){
				emailService.sendResetPasswordEmail(user.getCorreo(), "reset-password-email",passwordResetToken.getToken());
			return ResponseEntity.ok().build();
			}
			passwordResetTokenService.deleteToken(passwordResetToken);
		}

		String token = passwordResetTokenService.createTokenForUser(user, 5);

		emailService.sendResetPasswordEmail(user.getCorreo(), "reset-password-email", token);

		return ResponseEntity.ok().build();

	}

}
