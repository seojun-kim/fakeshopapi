package com.example.fakeshopapi.controller;

import com.example.fakeshopapi.domain.Member;
import com.example.fakeshopapi.domain.RefreshToken;
import com.example.fakeshopapi.domain.Role;
import com.example.fakeshopapi.dto.*;
import com.example.fakeshopapi.security.jwt.util.JwtTokenizer;
import com.example.fakeshopapi.service.MemberService;
import com.example.fakeshopapi.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/members")
public class MemberController {

    private final JwtTokenizer jwtTokenizer;
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody @Valid MemberSignupDTO memberSignupDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Member member = new Member();
        member.setName(memberSignupDTO.getName());
        member.setEmail(memberSignupDTO.getEmail());
        member.setPassword(passwordEncoder.encode(memberSignupDTO.getPassword()));

        Member saveMember = memberService.addMember(member);

        MemberSignupResponseDto memberSignupResponseDto = new MemberSignupResponseDto();
        memberSignupResponseDto.setMemberId(saveMember.getId());
        memberSignupResponseDto.setName(saveMember.getName());
        memberSignupResponseDto.setRegdate(saveMember.getRegdate());
        memberSignupResponseDto.setEmail(saveMember.getEmail());

        return new ResponseEntity(memberSignupResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid MemberLoginDTO memberLoginDTO) {

        Member member = memberService.findByEmail(memberLoginDTO.getEmail());
        if(!passwordEncoder.matches(memberLoginDTO.getPassword(), member.getPassword())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        String accessToken = jwtTokenizer.createAccessToken(member.getId(), member.getEmail(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.getId(), member.getEmail(), roles);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setMemberId(member.getId());
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        MemberLoginResponseDTO memberLoginResponseDTO = MemberLoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getId())
                .nickname(member.getName())
                .build();

        return new ResponseEntity(memberLoginResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        refreshTokenService.deleteRefreshToken(refreshTokenDTO.getRefreshToken());
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity requestRefresh(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        RefreshToken refreshToken = refreshTokenService.findRefreshToken(refreshTokenDTO.getRefreshToken()).orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken.getValue());

        Long userId = jwtTokenizer.getUserIdFormToken(refreshToken.getValue());

        Member member = memberService.getMember(userId).orElseThrow(() -> new IllegalArgumentException("Member Not found"));

        List roles = (List) claims.get("roles");
        String email = claims.getSubject();

        String accessToken = jwtTokenizer.createAccessToken(userId, email, roles);

        MemberLoginResponseDTO memberLoginResponseDTO = MemberLoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenDTO.getRefreshToken())
                .memberId(userId)
                .nickname(member.getName())
                .build();

        return new ResponseEntity(memberLoginResponseDTO, HttpStatus.OK);
    }
}
