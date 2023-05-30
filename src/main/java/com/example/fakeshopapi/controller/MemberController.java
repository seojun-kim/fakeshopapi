package com.example.fakeshopapi.controller;

import com.example.fakeshopapi.dto.MemberLoginDTO;
import com.example.fakeshopapi.dto.MemberLoginResponseDTO;
import com.example.fakeshopapi.security.jwt.util.JwtTokenizer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final JwtTokenizer jwtTokenizer;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid MemberLoginDTO memberLoginDTO) {

        Long memberId = 1L;
        String email = memberLoginDTO.getEmail();
        List<String> roles = List.of("ROLE_USER");

        String accessToken = jwtTokenizer.createAccessToken(memberId, email, roles);
        String refreshToken = jwtTokenizer.createRefreshToken(memberId, email, roles);

        MemberLoginResponseDTO memberLoginResponseDTO = MemberLoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(memberId)
                .nickname("nickname")
                .build();

        return new ResponseEntity(memberLoginResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String token) {
        // token repository에서 refresh Token에 해당하는 값을 삭제한다.
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
