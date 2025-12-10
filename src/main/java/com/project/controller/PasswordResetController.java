package com.project.controller;


import com.project.dto.PasswordResetRequest;
import com.project.entity.PasswordResetToken;
import com.project.entity.Usuario;
import com.project.repository.UsuarioRepository;
import com.project.service.PasswordResetTokenService;
import com.project.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RequestMapping("/api/v1/reset-password")
@RequiredArgsConstructor
@RestController
public class PasswordResetController {


	private final UsuarioService usuarioService;
	private final PasswordResetTokenService passwordResetTokenService;


	@PostMapping
	public ResponseEntity<?> handlePasswordReset(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {


		PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(passwordResetRequest.token());

		if(passwordResetTokenService.isTokenExpired(passwordResetToken)){
			passwordResetTokenService.deleteToken(passwordResetToken);
			throw new RuntimeException("EL TOKEN DE RESTABLECIMIENTO ESTÁ VENCIDO, SOLICITE UN NUEVO CORREO DE RECUPERACIÓN");
		}

		Usuario user = passwordResetToken.getUser();
		usuarioService.actualizarPassword(passwordResetRequest.password(), user.getId());

		passwordResetTokenService.deleteToken(passwordResetToken);

		return ResponseEntity.ok().build();
	}

}
