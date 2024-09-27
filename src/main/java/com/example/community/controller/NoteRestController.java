package com.example.community.controller;


import com.example.community.dto.NoteBlockRequestDto;
import com.example.community.dto.NoteRequestDto;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class NoteRestController {

    private final MemberRepository memberRepository;
    private final NoteService noteService;
    private final AuthenticationUtil authenticationUtil;
    // 쪽지 작성
    @PostMapping("/api/notes")
    public ResponseEntity<String> saveNote(@Valid @RequestBody NoteRequestDto noteRequestDto, BindingResult bindingResult) {

        // noteSaveRequestDto 유효성 검증 한 뒤 오류 발생할 시
        if(bindingResult.hasErrors())
        {
            StringBuilder errors = new StringBuilder();
            // 에러 메세지를 가져와서 추가
            bindingResult.getFieldErrors().forEach(error -> {
                errors.append(error.getDefaultMessage()).append(". ").append("\n");
            });
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errors.toString());
        }

        // 받는 사람 유저 정보 가져오기
        Optional<Member> byemail = memberRepository.findByEmail(noteRequestDto.getReceiver_email());
        // 유저 정보가 있을 시
        if(byemail.isPresent()){
            // 받는 사람이 수신 거부일 시
            if(byemail.get().getNoteblockd() == true || byemail.get().getTemporaryblockdate().isAfter(LocalDateTime.now()))
            {
                return ResponseEntity.status(HttpStatus.MULTI_STATUS).body("수신 거부 상태이므로 쪽지를 발송할 수 없습니다.");
            }
            // 받는 사람이 내 자신일 시
            if(byemail.get().getId().equals(authenticationUtil.getCurrentMember().getId()))
            {
                return ResponseEntity.status(HttpStatus.MULTI_STATUS).body("자신에게 쪽지를 발송할 수 없습니다.");
            }
            Note SavedNote = noteService.saveNote(byemail.get(), noteRequestDto.getMessage());
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
    // 쪽지 거부 수정
    @PatchMapping("/api/noteblocks")
    public ResponseEntity<String> updateNoteBlock(@RequestBody NoteBlockRequestDto noteBlockRequestDto) {
        if(noteBlockRequestDto.getBlock_type().equals("temporary")) {
            // 임시 거부 설정
            Long userid =  noteService.setTemporaryBlockDate();
            if(userid != null){
                return ResponseEntity.status(HttpStatus.OK).body("임시 거부 설정 완료 되었습니다.");

            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("로그인 정보가 일치하지 않습니다.");
            }
        }
        else if(noteBlockRequestDto.getBlock_type().equals("permanent")) {
            // 영구 거부 설정
            Long userid = noteService.setPermanentBlockd();
            if(userid != null){
                return ResponseEntity.status(HttpStatus.OK).body("영구 거부 설정 완료 되었습니다.");
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("로그인 정보가 일치하지 않습니다.");
            }
        }
        else {
            // 영구 거부 해제
            Long userid = noteService.removePermanentBlockd();
            if(userid != null){
                return ResponseEntity.status(HttpStatus.OK).body("영구 거부 해제 완료 되었습니다.");
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("로그인 정보가 일치하지 않습니다.");
            }
        }

    }
}
