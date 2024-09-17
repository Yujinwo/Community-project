package com.example.community.service;

import com.example.community.config.CustomUserDetails;
import com.example.community.entity.Member;
import com.example.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Autowired
    public MyUserDetailService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email){
          // 이메일을 이용해서 회원을 조회한다.
          Member member = memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 id 입니다."));
          // 조회한 회원을 Userdetails에 저장한다.
          CustomUserDetails customUserDetails = new CustomUserDetails(member);
          return customUserDetails;
    }

}
