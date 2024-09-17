package com.example.community.service;

import com.example.community.domain.member.Role;
import com.example.community.dto.NoteResponseDto;
import com.example.community.dto.NoteResultDto;
import com.example.community.entity.Member;
import com.example.community.entity.Note;
import com.example.community.repository.MemberRepository;
import com.example.community.repository.NoteRespository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class NoteServiceTest {

    @Autowired
    NoteRespository noteRespository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("쪽지 저장")
    void saveNote() {
        //given
        Member writer = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("보내는자").role(Role.USER).noteblockd(false).build();
        Member receiver = Member.builder().email("dbwlsdn0124").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("받는자").role(Role.USER).noteblockd(false).build();
        memberRepository.save(writer);
        memberRepository.save(receiver);

        Note saveNote = Note.builder().receiver(receiver).writer(writer).message("안녕하세요").build();
        //when
        Note SavedNote = noteRespository.save(saveNote);

        //then
        assertEquals(SavedNote,saveNote);
    }

    @Test
    @DisplayName("쪽지 조회")
    void findNotes() {
        //given
        Member writer = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("보내는자").role(Role.USER).noteblockd(false).build();
        Member receiver = Member.builder().email("dbwlsdn0124").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("받는자").role(Role.USER).noteblockd(false).build();
        memberRepository.save(writer);
        memberRepository.save(receiver);
        Note saveNote = Note.builder().receiver(receiver).writer(writer).message("안녕하세요").build();
        Note SavedNote = noteRespository.save(saveNote);
        em.flush();
        em.clear();
        Member member = memberRepository.findByEmail("dbwlsdn0124").get();
        PageRequest pageRequest = PageRequest.of(0, 10);
        //when
        Page<NoteResponseDto> notelists = noteRespository.findByNote(member,pageRequest).map(Note::changeNoteDto);

       //then
        assertEquals(notelists.getTotalElements(),1);
    }

    @Test
    @DisplayName("쪽지 임시 차단")
    void setTemporaryBlockDate() {
        //given
        Member member = Member.builder().email("dbwlsdn0124").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("받는자").role(Role.USER).noteblockd(false).build();
        Member savedmember = memberRepository.save(member);

        //when
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime blockEndTime = now.plusHours(24); // 현재 시간에 24시간을 더함
        savedmember.setTemporaryblockdate(blockEndTime);
        em.flush();

        // then
        assertEquals(savedmember.getTemporaryblockdate(),blockEndTime);
    }

    @Test
    @DisplayName("쪽지 영구 차단")
    void setPermanentBlockd() {
        //given
        Member member = Member.builder().email("dbwlsdn0124").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("받는자").role(Role.USER).noteblockd(false).build();
        Member savedmember = memberRepository.save(member);

        //when
        savedmember.changeNoteBlockd(true);
        em.flush();

        // then
        assertEquals(savedmember.getNoteblockd(),true);
    }

    @Test
    @DisplayName("쪽지 영구 차단 해제")
    void removePermanentBlockd() {
        //given
        Member member = Member.builder().email("dbwlsdn0124").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("받는자").role(Role.USER).noteblockd(false).build();
        Member savedmember = memberRepository.save(member);
        savedmember.changeNoteBlockd(true);
        em.flush();

        //when
        savedmember.changeNoteBlockd(false);
        em.flush();

        // then
        assertEquals(savedmember.getNoteblockd(),false);
    }
}