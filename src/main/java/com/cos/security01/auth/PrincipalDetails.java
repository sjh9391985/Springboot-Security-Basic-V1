package com.cos.security01.auth;

/*
    시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킴
    로그인을 진행이 완료가 되면 시큐리티 session을 만들어준다.(security contextholder)
    오브젝트 => Authentication 타입 객체를 넣어줘야함.
    Authentication안에 User 정보가 있어야함.
    User 오브젝트의 타입은 => UserDetails 타입 객체.
*/

import com.cos.security01.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// Security Session => Authentication => UserDetails
public class PrincipalDetails implements UserDetails {

    private User user;

    public PrincipalDetails(User user){
        this.user = user;
    }

    // 해당 유저의 권한을 리턴하는곳.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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

        // 1년동안 사이트 로그인을 하지 않았다면 휴먼계정으로 전환.
        // 현재시간 - 로그인 시간 > 1년 초과시 return false로 전환.

        return true;
    }
}
