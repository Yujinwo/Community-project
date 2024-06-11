package com.example.community.controller;

import com.example.community.dto.MemberDto;
import com.example.community.entity.Member;
import com.example.community.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MemberRestController {


    private final MemberService memberService;
    @Autowired
    public MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원가입 기능
    @PostMapping("/join")
    public ResponseEntity<Map<String,String>> userJoin(@Valid @RequestBody MemberDto requestDto) {
        // json 메세지 생성
        Map<String, String> resultdata = new HashMap<>();
        // 아이디 중복 체크
        if(memberService.idcheck(requestDto.getEmail()) != null)
        {

            resultdata.put("result","중복체크를 해주세요.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultdata);

        }
        // 회원 생성
        Member member = memberService.addUser(requestDto);

        resultdata.put("result","회원가입 완료 했습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(resultdata);
    }

    @PostMapping("/idcheck")
    public String useridcheck(@RequestBody Map<String,String> checkdata) {
             // json email 속성 값 가져오기
             String email =  checkdata.get("email");
             // email 길이 계산
             int length = email.length();
             // email 길이가 8~320자가 아닐시
             if(email.isEmpty() || length <= 8 || length >= 320 ) {
                 return "이메일은 8~320자 이내로 작성해주세요";
             }
             else{
                 // 아이디 중복 체크
                 Member member = memberService.idcheck(email);
                 if(member == null){
                     return "가입 할 수 있는 ID 입니다.";
                 }
                 else {

                     return "이미 존재하는 ID 입니다.";
                 }

             }
    }
}
