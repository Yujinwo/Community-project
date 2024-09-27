package com.example.community.service;

import com.example.community.config.CustomOAuth2User;
import com.example.community.config.CustomUserDetails;
import com.example.community.dto.MemberDto;
import com.example.community.dto.updateMemberDto;
import com.example.community.entity.Member;
import com.example.community.repository.MemberRepository;
import com.example.community.util.AuthenticationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationUtil authenticationUtil;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager em;

    // 회원 생성
    @Transactional
    public Optional<Object> addUser(MemberDto requestDto) {
        Optional<Member> member = Optional.ofNullable(memberRepository.save(requestDto.toEntity()));
        if(member.isEmpty())
        {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(member);
    }

    // ID 조회
    @Transactional(readOnly = true)
    public Boolean idcheck(String email) {
        // 이메일을 이용해서 회원 조회
        Optional<Member> member = memberRepository.findByEmail(email);
        // 회원이 존재할 경우
        if(member.isPresent()){
            return true;
        }
        else{
            return false;
        }

    }
    // 닉네임 조회
    @Transactional(readOnly = true)
    public Boolean nickcheck(String usernick) {
        // 닉네임 조회
        Optional<Member> byusernick = memberRepository.findByusernick(usernick);
        // 닉네임이 존재하면
        if(byusernick.isPresent()){
            return true;
        }
        else {
            return false;
        }

    }
    // 회원 정보 수정
    @Transactional
    public Optional<Object> updateUser(updateMemberDto requestDto) {
        // 인증된 Member Entity 가져오기
        Member user = authenticationUtil.getCurrentMember();
        if(user == null)
        {
            return Optional.ofNullable(null);
        }

        Optional<Member> byEmail = memberRepository.findByEmail(user.getEmail());
        if(byEmail.isEmpty())
        {
            return Optional.ofNullable(null);
        }

        //소셜 로그인 이면
        if(!user.getSocial().equals("normal")) {
                // 닉네임만 변경
                byEmail.get().changeUserNick(requestDto.getUsernick());
                em.flush();
                em.clear();
                // 인증 유저 정보를 갱신
                CustomOAuth2User userDetails = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                userDetails.changeMyProfile(byEmail.get().getUsernick());
                return Optional.ofNullable(byEmail);
        }
        //닉네임이 같으면
        else if( byEmail.get().getUsernick().equals(requestDto.getUsernick()) ) {
                //비밀번호 변경
                byEmail.get().changeUserPw(passwordEncoder.encode(requestDto.getUserpw()));
                em.flush();
                em.clear();
                // 인증 유저 정보를 갱신
                CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                userDetails.changeMyProfile(byEmail.get().getUsernick(),byEmail.get().getUserpw());
                return Optional.ofNullable(byEmail);
        }
        // 닉네임이 다르면
        else {
                    // 닉네임과 비밀번호 변경
                    byEmail.get().changeUserNick(requestDto.getUsernick());
                    byEmail.get().changeUserPw(passwordEncoder.encode(requestDto.getUserpw()));
                    em.flush();
                    em.clear();
                    // 인증 유저 정보를 갱신
                    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    userDetails.changeMyProfile(byEmail.get().getUsernick(),byEmail.get().getUserpw());
                    return Optional.ofNullable(byEmail);
        }
    }
}
