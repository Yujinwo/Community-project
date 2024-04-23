package com.example.community.config;

import com.example.community.domain.member.Role;
import com.example.community.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
@Getter
public class CustomUserDetails implements UserDetails {

    private Long id;	// DB에서 PK 값
    private String email;		// ID
    private String password;	// 비밀번호
    private String role;         // 권한
    private boolean emailVerified;	  //이메일 인증 여부
    private boolean locked;	  //계정 잠김 여부
    private String usernick;	// 닉네임
    private Member member;       // User Entity
    private Collection<GrantedAuthority> authorities;	// 권한 목록

    // 권한 목록 Get
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    // 비밀번호 Get
    @Override
    public String getPassword() {
        return password;
    }
    // 닉네임 Get
    @Override
    public String getUsername() {
        return usernick;
    }
    // Member Entity Get
    public Member getUserEntity() {
        return member;
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
