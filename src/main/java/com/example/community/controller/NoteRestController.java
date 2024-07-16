package com.example.community.controller;


import com.example.community.dto.NoteSaveRequestDto;
import com.example.community.entity.Member;
import com.example.community.entity.Note;
import com.example.community.repository.MemberRepository;
import com.example.community.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class NoteRestController {

    private final MemberRepository memberRepository;
    private final NoteService noteService;

    @PostMapping("/api/SaveNote")
    public ResponseEntity<String> saveNote(@RequestBody NoteSaveRequestDto noteSaveRequestDto) {
        // 받는 사람 멤버 정보 가져오기
        Optional<Member> byemail = memberRepository.findByEmail(noteSaveRequestDto.getReceiver_email());
        if(byemail.isPresent()){
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
