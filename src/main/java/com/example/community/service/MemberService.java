package com.example.community.service;

import com.example.community.dto.MemberDto;
import com.example.community.entity.Member;
import com.example.community.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberService {


    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    public MemberService(MemberRepository memberRepository, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.memberRepository = memberRepository;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    // 회원 생성
    @Transactional
    public Member addUser(MemberDto requestDto) {
        // MemberDto를 받아와서 패스워드를 암호화하고 회원을 생성한다
        Member member = memberRepository.save(requestDto.toEntity());
        // Member Entity가 null 일시 예외 상황 발생
        if (member == null) {
            throw  new RuntimeException("회원가입에 실패했습니다.");
        }
        return member;
    }
    // ID 중복 체크
    @Transactional(readOnly = true)
    public Member idcheck(String email) {
        // 이메일을 이용해서 회원을 조회한다
        Optional<Member> member = memberRepository.findByEmail(email);
        // 회원이 존재할 경우
        if(member.isPresent()){
            // Member Entity를 return 한다
            return member.get();
        }
        else{
            return null;
        }

    }
    @Transactional(readOnly = true)
    // 회원 조회
    public Member findByEmail(String email) {
        // 이메일을 이용해서 회원을 조회한다
        Optional<Member> member = memberRepository.findByEmail(email);
        // 회원이 존재할 경우
        if(member.isPresent()){
            // Member Entity를 return 한다
            return member.get();
        }
        else{
            return null;
        }

    }

}
