package com.cos.security01.config;

import com.cos.security01.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)// secured 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    // security암호화.
    @Bean   // -> 해당 메서드의 리턴되는 오브젝트를 IOC로 등록해줌.
    public BCryptPasswordEncoder encodePwd(){
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); // csrf 허용X
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()// 인증만 되면 들어갈 수 있는 주소
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") // <= 이러한 ROLE가 있으면 접근이 가능함.
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") // <= 이러한 ROLE가 있으면 접근이 가능하다.
                .anyRequest().permitAll()// 그 밖의 요청은 접근 허용이 가서
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login") //login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행해 준다.
                .defaultSuccessUrl("/") // 기본적으로 성공시 '/' url 로 간다.
                .and()
                .oauth2Login() //oauth 로그인 화면 연결
                .loginPage("/loginForm") //구글 로그인이 완료된 후 후처리가 필요함. (Tip, 코드를 받는게 아니라 액세스 토큰 + 사용자 프로필 정보를 받음)
                .userInfoEndpoint()
                .userService(principalOauth2UserService);
                /*
                1. 코드받기(인증)
                2. 엑세스토큰(권한)
                3. 사용자 프로필 정보를 가져오고
                4. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함.
                4-1. 추가적인 정보 구성들은 추가적인 회원가입 창이 나와서 가입 진행을 해야한다.
                */


    }

}