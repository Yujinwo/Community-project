package com.example.community.controller;


import com.example.community.dto.NoteBlockRequestDto;
import com.example.community.dto.NoteDeleteDto;
import com.example.community.dto.NoteRequestDto;
import com.example.community.entity.Member;
import com.example.community.repository.MemberRepository;
import com.example.community.service.NoteService;
import com.example.community.util.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class NoteRestController {

    private final MemberRepository memberRepository;
    private final NoteService noteService;
    private final AuthenticationUtil authenticationUtil;
    // 쪽지 작성
    @PostMapping("/api/notes")
    public ResponseEntity<Map<String, String>> saveNote(@Valid @RequestBody NoteRequestDto noteRequestDto, BindingResult bindingResult) {
        // json 메세지 생성
        Map<String, String> errorresultdata = new HashMap<>();
        // 받는 사람 유저 정보 가져오기
        Optional<Member> byemail = memberRepository.findByEmail(noteRequestDto.getReceiver_email());
        if(byemail.isEmpty())
        {
            errorresultdata.put("message","이메일이 존재하지 않습니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
        }
        // 받는 사람이 수신 거부일 시
        if(byemail.get().getNoteblock() == true || byemail.get().getTemporaryblockdate().isAfter(LocalDateTime.now()))
        {
            errorresultdata.put("message","수신 거부 상태이므로 쪽지를 발송할 수 없습니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
        }
        // 받는 사람이 내 자신일 시
        if(byemail.get().getId().equals(authenticationUtil.getCurrentMember().getId()))
        {
            errorresultdata.put("message","자신에게 쪽지를 발송할 수 없습니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
        }
        Optional<Object> SavedNote = noteService.saveNote(byemail.get(), noteRequestDto.getMessage());
        if(SavedNote.isPresent())
        {
            errorresultdata.put("message","전송이 완료 되었습니다");
            return ResponseEntity.status(HttpStatus.OK).body(errorresultdata);
        }
        errorresultdata.put("message","로그인 정보가 일치하지 않습니다");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);

    }
    // 쪽지 거부 수정
    @PatchMapping("/api/users/me/noteblock")
    public ResponseEntity<Map<String, String>> updateNoteBlock(@Valid @RequestBody NoteBlockRequestDto noteBlockRequestDto) {
        // json 메세지 생성
        Map<String, String> errorresultdata = new HashMap<>();
        if(noteBlockRequestDto.getBlock_type().equals("temporary")) {
            // 임시 거부 설정
            Optional<Object> optionalnote = noteService.setTemporaryBlockDate();
            if(optionalnote.isPresent()){
                errorresultdata.put("message","임시 거부 설정 완료 되었습니다");
                return ResponseEntity.status(HttpStatus.OK).body(errorresultdata);
            }
            else {
                errorresultdata.put("message","로그인 정보가 일치하지 않습니다");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            }
        }
        else if(noteBlockRequestDto.getBlock_type().equals("permanent")) {
            // 영구 거부 설정
            Optional<Object> optionalnote = noteService.setPermanentBlockd();
            if(optionalnote.isPresent()){
                errorresultdata.put("message","영구 거부 설정 완료 되었습니다");
                return ResponseEntity.status(HttpStatus.OK).body(errorresultdata);
            }
            else {
                errorresultdata.put("message","로그인 정보가 일치하지 않습니다");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            }
        }
        else if(noteBlockRequestDto.getBlock_type().equals("remove_permanent")) {
            // 영구 거부 해제
            Optional<Object> optionalnote = noteService.removePermanentBlockd();
            if(optionalnote.isPresent()){
                errorresultdata.put("message","영구 거부 해제 완료 되었습니다");
                return ResponseEntity.status(HttpStatus.OK).body(errorresultdata);
            }
            else {
                errorresultdata.put("message","로그인 정보가 일치하지 않습니다");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
            }
        }
        errorresultdata.put("message","비정상적인 데이터 입니다");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
    }
    // 쪽지 삭제
    @DeleteMapping("/api/notes")
    public ResponseEntity<Map<String, String>> deleteNote(@Valid @RequestBody NoteDeleteDto noteDeleteDto) {
        // json 메세지 생성
        Map<String, String> errorresultdata = new HashMap<>();
        if(noteDeleteDto.getSelectdIds() == null)
        {
            errorresultdata.put("message","데이터가 존재하지 않습니다");
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
        }

        Optional<Object> optionalnote = noteService.deleteNotes(noteDeleteDto);
        if(optionalnote.isEmpty()){
            errorresultdata.put("message","일치하지 않는 데이터입니다");
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorresultdata);
        }
        errorresultdata.put("message","쪽지 삭제 완료 했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(errorresultdata);
    }
}
