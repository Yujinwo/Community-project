package com.example.community.service;

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
import org.springframework.security.core.userdetails.UserDetails;
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
    public Boolean idcheck(String email) {
        // 이메일을 이용해서 회원을 조회한다
        Optional<Member> member = memberRepository.findByEmail(email);
        // 회원이 존재할 경우
        if(member.isPresent()){
            // Member Entity를 return 한다
            return true;
        }
        else{
            return false;
        }

    }
    @Transactional(readOnly = true)
    public Boolean nickcheck(String usernick) {
        // 닉네임 중복 체크
        Optional<Member> byusernick = memberRepository.findByusernick(usernick);

        if(byusernick.isPresent()){
            return true;
        }
        else {
            return false;
        }


    }
    @Transactional
    public ResponseEntity<Map<String, String>> updateUser(updateMemberDto requestDto) {
        Member user = authenticationUtil.getCurrentMember();

        Optional<Member> byEmail = memberRepository.findByEmail(user.getEmail());
        if(byEmail.isPresent())
        {
            Map<String, String> resultdata = new HashMap<>();
            //닉네임이 같으면
            if( byEmail.get().getUsernick().equals(requestDto.getUsernick()) ) {
                if(passwordEncoder.matches(requestDto.getUserpw(),user.getUserpw()) ){
                    resultdata.put("result","현재 사용하고 있는 비밀번호입니다.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultdata);
                }
                //비밀번호 변경
                byEmail.get().changeUserPw(passwordEncoder.encode(requestDto.getUserpw()));
                resultdata.put("result","회원 정보 수정이 완료되었습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(resultdata);
            }
            else {
                if(this.nickcheck(requestDto.getUsernick())){
                    resultdata.put("result","닉네임 중복체크를 해주세요.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultdata);
                }
                else if(passwordEncoder.matches(requestDto.getUserpw(),user.getUserpw()) ){
                    resultdata.put("result","현재 사용하고 있는 비밀번호입니다.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultdata);
                }
                else {
                    // 닉네임과 비밀번호 변경
                    byEmail.get().changeUserNick(requestDto.getUsernick());
                    byEmail.get().changeUserPw(passwordEncoder.encode(requestDto.getUserpw()));
                    em.flush();
                    em.clear();
                    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    userDetails.changeMyProfile(byEmail.get().getUsernick(),byEmail.get().getUserpw());
                    resultdata.put("result","회원 정보 수정이 완료되었습니다.");
                    return ResponseEntity.status(HttpStatus.OK).body(resultdata);
                }
            }
        }
        return null;
    }
}
