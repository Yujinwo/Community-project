package com.example.community.service;


import com.example.community.dto.NoteSaveRequestDto;
import com.example.community.entity.Member;
import com.example.community.entity.Note;
import com.example.community.repository.MemberRepository;
import com.example.community.repository.NoteRespository;
import com.example.community.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final AuthenticationUtil authenticationUtil;
    private final NoteRespository noteRespository;
    private final MemberRepository memberRepository;


    @Transactional
    public Note saveNote(Member byemail, String message) {


            Member member = authenticationUtil.getCurrentMember();
            if(member != null)
            {
                Note saveNote = Note.builder().receiver(byemail).writer(member).message(message).read(false).build();
                Note SavedNote = noteRespository.save(saveNote);
                return SavedNote;
            }
            else {
                return null;
            }


    }
    @Transactional(readOnly = true)
    public Page<Note> findNotes(Pageable pageable){
        Member member = authenticationUtil.getCurrentMember();
        if(member != null)
        {
            Page<Note> notelists = noteRespository.findByNote(member,pageable);
            return notelists;
        }
        else {
            return null;
        }
    }
    @Transactional
    public Long setTemporaryBlockDate() {
       if(authenticationUtil.getCurrentMember() != null)
       {
           Long userid = authenticationUtil.getCurrentMember().getId();
           Optional<Member> user = memberRepository.findById(userid);
           if(user.isPresent()) {
               LocalDateTime now = LocalDateTime.now();
               LocalDateTime blockEndTime = now.plusHours(24); // 현재 시간에 24시간을 더함
               user.get().setTemporaryblockdate(blockEndTime);
               return userid;
           }
           else {
               return null;
           }
       }
       else {
           return null;
       }

    }
    @Transactional
    public Long setPermanentBlockd() {
        if(authenticationUtil.getCurrentMember() != null)
        {
            Long userid = authenticationUtil.getCurrentMember().getId();
            Optional<Member> user = memberRepository.findById(userid);
            if(user.isPresent()) {
                user.get().setNoteblockd(true);
                return userid;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }
    @Transactional
    public Long removePermanentBlockd() {
        if(authenticationUtil.getCurrentMember() != null)
        {
            Long userid = authenticationUtil.getCurrentMember().getId();
            Optional<Member> user = memberRepository.findById(userid);
            if(user.isPresent()) {
                user.get().setNoteblockd(false);
                return userid;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }
}
