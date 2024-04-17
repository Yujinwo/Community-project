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
    private String email;		// 로그인용 ID 값
    private String password;	// 비밀번호
    private String role;
    private boolean emailVerified;	//이메일 인증 여부
    private boolean locked;	//계정 잠김 여부
    private String usernick;	//닉네임
    private Member member;
    private Collection<GrantedAuthority> authorities;	//권한 목록

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

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        //이메일이 인증되어 있고 계정이 잠겨있지 않으면 true
        return true;
    }
   public CustomUserDetails(Member memberEntity){
       this.id = memberEntity.getId();
       this.email = memberEntity.getEmail();
       this.password = memberEntity.getUserpw();
       this.usernick = memberEntity.getUsernick();
       this.role = memberEntity.getRole().name();
       this.member = memberEntity;
       Collection<GrantedAuthority> roles = new ArrayList<>();
       for (Role role : Role.getAllRoles()) {
           roles.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
       }

       this.authorities = roles;

   }


}
