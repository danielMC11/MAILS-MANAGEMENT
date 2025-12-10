package com.project.service;


import com.project.entity.PasswordResetToken;
import com.project.entity.Usuario;

public interface PasswordResetTokenService {

    String createTokenForUser(Usuario user, int minutes);

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(Usuario user);

    boolean isTokenExpired(PasswordResetToken passwordResetToken);


    void deleteToken(PasswordResetToken passwordResetToken);

}
