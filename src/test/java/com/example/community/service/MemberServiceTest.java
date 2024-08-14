package com.example.community.service;

import com.example.community.domain.member.Role;
import com.example.community.entity.Member;
import com.example.community.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("회원 저장")
    void addUser() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblockd(false).build();
        //when
        Member savedmember = memberRepository.save(member);
        //then
        assertEquals(savedmember,member);
    }

    @Test
    @DisplayName("회원 ID 존재")
    void id_check_true() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblockd(false).build();
        memberRepository.save(member);
        //when
        Optional<Member> member1 = memberRepository.findByEmail("dbwlsdn0125");

        //then
        assertEquals(member1.isPresent(),true);

    }
    @Test
    @DisplayName("회원 ID 미존재")
    void id_check_false() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblockd(false).build();
        memberRepository.save(member);
        //when
        Optional<Member> member1 = memberRepository.findByEmail("dbwlsdn0124");

        //then
        assertEquals(member1.isPresent(),false);

    }

    @Test
    @DisplayName("회원 닉네임 존재")
    void nick_check_true() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblockd(false).build();
        memberRepository.save(member);
        //when
        Optional<Member> member1 = memberRepository.findByusernick("테스트");

        //then
        assertEquals(member1.isPresent(),true);
    }
    @Test
    @DisplayName("회원 닉네임 미존재")
    void nick_check_false() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblockd(false).build();
        memberRepository.save(member);
        //when
        Optional<Member> member1 = memberRepository.findByusernick("테스트2");

        //then
        assertEquals(member1.isPresent(),false);
    }

    @Test
    @DisplayName("회원 비밀번호 / 닉네임 수정")
    void updateUser() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblockd(false).build();
        memberRepository.save(member);

        //when
        Member member1 = memberRepository.findByEmail("dbwlsdn0125").get();
        member1.changeUserNick("테스트2");
        String changeuserpw = passwordEncoder.encode("qwer1234");
        member1.changeUserPw(changeuserpw);
        em.flush();
        em.clear();
        Member member2 = memberRepository.findByEmail("dbwlsdn0125").get();

        //then
        assertEquals(member2.getUsernick(),"테스트2");
        assertEquals(member2.getUserpw(),changeuserpw);
    }
}