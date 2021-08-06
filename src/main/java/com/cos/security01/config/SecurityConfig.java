package com.cos.security01.config;

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
                .defaultSuccessUrl("/"); // 기본적으로 성공시 '/' url 로 간다.


    }

}
