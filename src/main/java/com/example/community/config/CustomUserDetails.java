package com.example.community.config;

import com.example.community.domain.member.Role;
import com.example.community.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
@Getter
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private String role;
    private boolean emailVerified;
    private boolean locked;
    private String usernick;
    private Member member;
    private Collection<GrantedAuthority> authorities;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return usernick;
    }

    public Member getUserEntity() {
        return member;
    }


    public void changeMyProfile(String usernick,String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.member.setUsernick(usernick);
        this.member.setUserpw(passwordEncoder.encode(password));
    }
    public void changeNoteBlock(Boolean noteblock) {
        this.member.setNoteblock(noteblock);
    }
    public void changeTemporaryblockdate(LocalDateTime temporaryblockdate) {
        this.member.setTemporaryBlockDate(temporaryblockdate);
    }

    // 계정 만료 여부 확인
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    // 계정 잠김 여부 확인
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    // 자격 증명 만료 여부 확인
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    // 사용 가능 여부 확인
    @Override
    public boolean isEnabled() {
        return true;
    }

    // 인증된 Member 정보로 UserDetail 생성
    public CustomUserDetails(Member memberEntity){
       this.id = memberEntity.getId();
       this.email = memberEntity.getEmail();
       this.password = memberEntity.getUserpw();
       this.usernick = memberEntity.getUsernick();
       this.role = memberEntity.getRole().name();
       this.member = memberEntity;

       // 권한 목록 생성(ROLE_USER, ROLE_ADMIN)
       Collection<GrantedAuthority> roles = new ArrayList<>();
       for (Role role : Role.getAllRoles()) {
           roles.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
       }
       this.authorities = roles;

   }


}
