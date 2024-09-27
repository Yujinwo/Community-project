package com.example.community.service;


import com.example.community.dto.NoteResponseDto;
import com.example.community.dto.NoteResultDto;
import com.example.community.entity.Member;
import com.example.community.entity.Note;
import com.example.community.repository.MemberRepository;
import com.example.community.repository.NoteRespository;
import com.example.community.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final AuthenticationUtil authenticationUtil;
    private final NoteRespository noteRespository;
    private final MemberRepository memberRepository;

    // 쪽지 작성
    @Transactional
    public Optional<Object> saveNote(Member byemail, String message) {
            Member member = authenticationUtil.getCurrentMember();
            if(member != null)
            {
                Note saveNote = Note.builder().receiver(byemail).writer(member).message(message).build();
                Optional<Note> SavedNote = Optional.of(noteRespository.save(saveNote));
                if(SavedNote.isEmpty())
                {
                    Optional.ofNullable(null);
                }
                return Optional.ofNullable(SavedNote);
            }
            else {
                return Optional.ofNullable(null);
            }
    }
    // 쪽지 조회
    @Transactional(readOnly = true)
    public NoteResultDto findNotes(Pageable pageable){
        Member member = authenticationUtil.getCurrentMember();
        if(member != null)
        {
            Page<NoteResponseDto> notelists = noteRespository.findByNote(member,pageable).map(Note::changeNoteDto);
            return NoteResultDto.createNoteResultDto(notelists);
        }
       return null;

    }
    // 쪽지 임시 거부
    @Transactional
    public Optional<Object> setTemporaryBlockDate() {
       if(authenticationUtil.getCurrentMember() != null)
       {
           Long userid = authenticationUtil.getCurrentMember().getId();
           Optional<Member> user = memberRepository.findById(userid);
           if(user.isPresent()) {
               // 현재시간에 + 24시간 동안 임시 거부 설정
               LocalDateTime now = LocalDateTime.now();
               LocalDateTime blockEndTime = now.plusHours(24); // 현재 시간에 24시간을 더함
               user.get().setTemporaryblockdate(blockEndTime);
               return Optional.ofNullable(user);
           }
       }
       return Optional.ofNullable(null);

    }
    // 쪽지 영구 거부
    @Transactional
    public Optional<Object> setPermanentBlockd() {
        if(authenticationUtil.getCurrentMember() != null)
        {
            Long userid = authenticationUtil.getCurrentMember().getId();
            Optional<Member> user = memberRepository.findById(userid);
            if(user.isPresent()) {
                user.get().changeNoteBlockd(true);
                return Optional.ofNullable(user);
            }
        }
        return Optional.ofNullable(null);
    }
    // 쪽지 영구 거부 해제
    @Transactional
    public Optional<Object> removePermanentBlockd() {
        if(authenticationUtil.getCurrentMember() != null)
        {
            Long userid = authenticationUtil.getCurrentMember().getId();
            Optional<Member> user = memberRepository.findById(userid);
            if(user.isPresent()) {
                user.get().changeNoteBlockd(false);
                return Optional.ofNullable(user);
            }
        }
        return Optional.ofNullable(null);
    }
}
