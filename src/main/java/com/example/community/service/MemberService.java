package com.example.community.service;

import com.example.community.dto.MemberDto;
import com.example.community.entity.Member;
import com.example.community.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Transactional
    public Member addUser(MemberDto requestDto) {
        Member member = memberRepository.save(requestDto.toEntity(passwordEncoder));
        if (member == null) {
            throw  new RuntimeException("회원가입에 실패했습니다.");
        }
        return member;
    }
    @Transactional
    public Member idcheck(String email) {

        Optional<Member> member = memberRepository.findByEmail(email);
        if(member.isPresent()){
            return member.get();
        }
        else{
            return null;
        }

    }
}
