package com.example.community.controller;


import com.example.community.dto.NoteSaveRequestDto;
import com.example.community.entity.Member;
import com.example.community.entity.Note;
import com.example.community.repository.MemberRepository;
import com.example.community.service.NoteService;
import com.example.community.util.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class NoteRestController {

    private final MemberRepository memberRepository;
    private final NoteService noteService;
    private final AuthenticationUtil authenticationUtil;

    @PostMapping("/api/SaveNote")
    public ResponseEntity<String> saveNote(@Valid @RequestBody NoteSaveRequestDto noteSaveRequestDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors())
        {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> {
                errors.append(error.getDefaultMessage()).append(". ").append("\n");
            });
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errors.toString());
        }


        // 받는 사람 멤버 정보 가져오기
        Optional<Member> byemail = memberRepository.findByEmail(noteSaveRequestDto.getReceiver_email());

        if(byemail.isPresent()){
            if(byemail.get().getId().equals(authenticationUtil.getCurrentMember().getId()))
            {
                return ResponseEntity.status(HttpStatus.MULTI_STATUS).body("자신에게 쪽지를 발송할 수 없습니다.");
            }
            Note SavedNote = noteService.saveNote(byemail.get(),noteSaveRequestDto.getMessage());
            if(SavedNote != null)
            {
                return ResponseEntity.status(HttpStatus.OK).body("전송이 완료 되었습니다.");
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("로그인을 해주세요");
            }

        }
        else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("이메일이 존재하지 않습니다.");
        }

    }
}
