package com.example.community.service;

import com.example.community.config.CustomUserDetails;
import com.example.community.entity.Member;
import com.example.community.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private final MemberRepository memberRepository;
    @Transactional
    public UserDetails loadUserByUsername(String email){
          Member member = memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 id 입니다."));
          CustomUserDetails customUserDetails = new CustomUserDetails(member);
          return customUserDetails;
    }

}
