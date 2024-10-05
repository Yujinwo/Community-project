package com.example.community.controller;

import com.example.community.dto.IdCheckDto;
import com.example.community.dto.MemberDto;
import com.example.community.dto.updateMemberDto;
import com.example.community.entity.Member;
import com.example.community.service.MemberService;
import com.example.community.util.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;
    private final AuthenticationUtil authenticationUtil;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @PostMapping("/api/users")
    public ResponseEntity<Map<String,String>> joinUser(@Valid @RequestBody MemberDto requestDto) {
        // json 메세지 생성
        Map<String, String> resultdata = new HashMap<>();
        // 아이디 중복 체크
        if(memberService.idcheck(requestDto.getEmail()))
        {
            resultdata.put("message","ID 중복체크를 해주세요");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultdata);
        }
        // 회원 생성
        Optional<Object> member = memberService.addUser(requestDto);
        if(member.isEmpty())
        {
            resultdata.put("message","허용되지 않은 접근 입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultdata);
        }

        resultdata.put("message","회원가입 완료 했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(resultdata);
    }

    // 회원정보 수정
    @PatchMapping("/api/users")
    public ResponseEntity<Map<String,String>> UpdateUser(@Valid @RequestBody updateMemberDto requestDto) {
        // json 메세지 생성
        Map<String, String> errorresultdata = new HashMap<>();
        if(!authenticationUtil.getCurrentMember().getSocial().equals("normal")) {
            if (requestDto.getUsernick().equals(authenticationUtil.getCurrentMember().getUsernick())) {
                // 닉네임이 같으면
                errorresultdata.put("message", "현재 사용하고 있는 닉네임입니다");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            }
        }
        else {
            if (!requestDto.getUserpw().equals(requestDto.getConfirmuserpw())) {
                errorresultdata.put("message", "변경 비밀번호와 재확인 비밀번호를 일치 시켜주세요");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            }
            else if(!passwordEncoder.matches(requestDto.getOriginaluserpw(),authenticationUtil.getCurrentMember().getUserpw()) ){
                errorresultdata.put("message","현재 사용하고 있는 비밀번호와 일치하지 않습니다");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            }
            else if(passwordEncoder.matches(requestDto.getUserpw(),authenticationUtil.getCurrentMember().getUserpw()) ){
                errorresultdata.put("message","현재 사용하고 있는 비밀번호입니다");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            }
        }

        Optional<Object> optionalmember = memberService.updateUser(requestDto);
        if(optionalmember.isEmpty())
        {
            errorresultdata.put("message","허용되지 않은 접근 입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
        }
        errorresultdata.put("message","회원 정보 수정이 완료되었습니다");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
    }
    // 세션 조회
    @GetMapping("/api/sessions")
    public ResponseEntity<Map<String,Boolean>> ssesionCheck() {
        // 인증된 유저 정보 가져오기
        Member member = authenticationUtil.getCurrentMember();
        Map<String, Boolean> resultdata = new HashMap<>();
        if(member != null) {
            resultdata.put("state", true);
            return ResponseEntity.status(HttpStatus.OK).body(resultdata);
        }
        resultdata.put("state", false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultdata);
    }
    //  ID 조회
    @PostMapping("/api/userids")
    public ResponseEntity<Map<String, String>> useridcheck(@Valid @RequestBody IdCheckDto idCheckDto) {
         Map<String, String> resultdata = new HashMap<>();
         // 아이디 중복 체크
         Boolean idchecked = memberService.idcheck(idCheckDto.getEmail());
         if(idchecked){
            resultdata.put("message","이미 존재하는 ID 입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultdata);
         }
         resultdata.put("message","가입 할 수 있는 ID 입니다");
         return ResponseEntity.status(HttpStatus.OK).body(resultdata);

    }
}
