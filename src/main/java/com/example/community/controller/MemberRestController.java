package com.example.community.controller;

import com.example.community.dto.MemberDto;
import com.example.community.dto.updateMemberDto;
import com.example.community.entity.Member;
import com.example.community.entity.Notification;
import com.example.community.repository.MemberRepository;
import com.example.community.service.MemberService;
import com.example.community.service.NotificationService;
import com.example.community.util.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
            resultdata.put("result","ID 중복체크를 해주세요.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultdata);
        }
        // 회원 생성
        Optional<Object> member = memberService.addUser(requestDto);
        if(member.isEmpty())
        {
            resultdata.put("result","허용되지 않은 접근 입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultdata);
        }

        resultdata.put("result","회원가입 완료 했습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(resultdata);
    }

    // 회원정보 수정
    @PatchMapping("/api/users")
    public ResponseEntity<Map<String,String>> UpdateUser(@RequestBody updateMemberDto requestDto) {
        // json 메세지 생성
        Map<String, String> errorresultdata = new HashMap<>();
        if(!authenticationUtil.getCurrentMember().getSocial().equals("normal")) {
            if (requestDto.getUsernick().equals(authenticationUtil.getCurrentMember().getUsernick())) {
                // 닉네임이 같으면
                errorresultdata.put("result", "현재 사용하고 있는 닉네임입니다");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            } else if (requestDto.getUsernick().isEmpty()) {
                errorresultdata.put("result", "닉네임은 3~20자 이내로 작성해주세요");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            } else if (!requestDto.getUsernick().isEmpty()) {
                int length = requestDto.getUsernick().length();
                // 닉네임 길이가 3~20자가 아닐시
                if (length <= 3 || length >= 20) {
                    errorresultdata.put("result", "닉네임은 3~20자 이내로 작성해주세요");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
                }
            }
        }
        else {
            if (requestDto.getOriginaluserpw().isEmpty()) {
                errorresultdata.put("result", "원본 비밀번호는 10~15자 이내로 작성해주세요");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            }
            else if (requestDto.getUserpw().isEmpty()) {
                errorresultdata.put("result", "변경 비밀번호는 10~15자 이내로 작성해주세요");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            }
            // 비밀번호 길이가 10~15자가 아닐시
            else if (!requestDto.getUserpw().isEmpty()) {
                int length = requestDto.getUserpw().length();
                if (length <= 10 || length >= 15) {
                    errorresultdata.put("result", "변경 비밀번호는 10~15자 이내로 작성해주세요");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
                }
            } else if (!requestDto.getUserpw().equals(requestDto.getConfirmuserpw())) {
                errorresultdata.put("result", "변경 비밀번호와 재확인 비밀번호를 일치 시켜주세요.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            }
            else if(!passwordEncoder.matches(requestDto.getOriginaluserpw(),authenticationUtil.getCurrentMember().getUserpw()) ){
                errorresultdata.put("result","현재 사용하고 있는 비밀번호와 일치하지 않습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            }
            else if(passwordEncoder.matches(requestDto.getUserpw(),authenticationUtil.getCurrentMember().getUserpw()) ){
                errorresultdata.put("result","현재 사용하고 있는 비밀번호입니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            }
        }

        Optional<Object> optionalmember = memberService.updateUser(requestDto);
        if(optionalmember.isEmpty())
        {
            errorresultdata.put("result","허용되지 않은 접근 입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
        }
        errorresultdata.put("result","회원 정보 수정이 완료되었습니다.");
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
    public ResponseEntity<String> useridcheck(@RequestBody Map<String,String> checkdata) {
        // json email 속성 값 가져오기
        String email =  checkdata.get("email");
        if(!email.isEmpty())
             {
                 int length = email.length();
                 // email 길이가 8~320자가 아닐시
                 if(email.isEmpty() || length <= 8 || length >= 320 ) {
                     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일은 8~320자 이내로 작성해주세요");
                 }
                 else{
                     // 아이디 중복 체크
                     Boolean idchecked = memberService.idcheck(email);
                     if(idchecked){
                         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 ID 입니다.");
                     }
                     else {
                         return ResponseEntity.status(HttpStatus.OK).body("가입 할 수 있는 ID 입니다.");
                     }

                 }
             }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일을 작성해주세요");
    }
}
