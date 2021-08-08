package com.cos.security01.controller;

import com.cos.security01.auth.PrincipalDetails;
import com.cos.security01.model.User;
import com.cos.security01.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @ResponseBody
    @GetMapping("/test/login")
    public String loginTest(Authentication authentication, @AuthenticationPrincipal UserDetails userDetails){
        System.out.println("test/login===========" );
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println(principalDetails.getUsername());

        System.out.println("userDetails: " + userDetails.getUsername());
        return "세션정보 확인";
    }

    @ResponseBody
    @GetMapping("/test/oauth/login")
    public String testOAuthLogin(Authentication authentication,
                                 @AuthenticationPrincipal OAuth2User oauth){
        System.out.println("test/login===========" );
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        //Authentication 객체가 가질 수 있는 2가지타입.
        System.out.println("authentication: "+ oAuth2User.getAttributes());
        System.out.print("oauth2User: " + oauth.getAttributes());

        return "세션정보 확인";
    }

    @GetMapping({"", "/"})
    public String index(){
        //머스테치 기본 폴더 src/main/resources/
        return "index";
    }

    @GetMapping("/user")
    @ResponseBody
    public String user(@AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        System.out.println("pricipalDetails: " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

    @GetMapping("/manager")
    public String manager(){
        return "manager";
    }

    //스프링 시큐리티가 해당주소를 낚아챈다.
    @GetMapping("/loginForm")
    public String login(){
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }

    /*
        * 회원가입 구현화면 *
        - 유저 ROLE 저장.
        - 비밀번호 인코딩 전환해야함.
        - 인코딩 전환후 DB에 저장.
        - 저장 후 loginForm redirect.
    */
    @PostMapping("/join")
    public String join(User user){
        System.out.println(user);
        user.setRole("ROLE_USER");

        /*
        -> 회원가입은 잘되나, 비밀번호가 1234로 저장됨.
        그러면 시큐리티로 로그인 할 수 없음. 이유는 패스워드가 암호화가 안되었기 때문에.
        */
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")  // ROLE_ADMIN 일 경우에만 /info 접근 허용가능
    @ResponseBody
    @GetMapping("/info")
    public String info(){
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')or hasRole('ROLE_ADMIN')") // ROLE_MANAGER or ROLE_ADMIN 일 경우 /data 접근 허용가능(복수 허용)
    @GetMapping("/data")
    @ResponseBody
    public String data(){
        return "데이터 정보";
    }


}