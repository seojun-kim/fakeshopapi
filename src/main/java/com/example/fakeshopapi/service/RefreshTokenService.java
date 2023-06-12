package com.example.fakeshopapi.service;

import com.example.fakeshopapi.domain.RefreshToken;
import com.example.fakeshopapi.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken addRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByValue(refreshToken).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 refreshToken 입니다."));
        refreshTokenRepository.delete(token);
    }

    public Optional<RefreshToken> findRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByValue(refreshToken);
    }
}
