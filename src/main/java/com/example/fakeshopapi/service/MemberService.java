package com.example.fakeshopapi.service;

import com.example.fakeshopapi.domain.Member;
import com.example.fakeshopapi.domain.Role;
import com.example.fakeshopapi.repository.MemberRepository;
import com.example.fakeshopapi.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
    }

    public Member addMember(Member member) {
        Optional<Role> role = roleRepository.findByName("ROLE_USER");
        member.addRole(role.get());
        Member save = memberRepository.save(member);
        return save;
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
