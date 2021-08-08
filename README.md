### 1. project structure

```
    src/main/java 
               |--- com.cos.security01
                                |--- auth
                                |--- config
                                |--- controller
                                |--- model
                                |--- repository
    src/main/resources
                 |--- static
                 |--- templates
                 |--- application.yml                 
```

### 2. security
```
    2-1. login 구현시 controller에 처리하는것이아니라 아래의 코드에서 처리를 해줌
    ↓          ↓          ↓
    [ SecurityConfig.java ]
    
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
    
```

### 3. JWT 학습 전 사전지식
``` JWT를 알기전 배경지식
#1. session
- req,res 방식
    1-1. client 가 server에 request 할 경우 세션ID생성
    1-2. 세션ID를 만들고 server에서 client 로 request할 경우 세션ID 를 돌려준다.
    1-3. server에서 받은 세션ID를 client는 저장을 한다.
    1-4. 로그인 요청 시 server는 DB에 값이 있는지 확인.
    1-5. 정상일 경우 세션ID에 DB의 정보를 저장한다.
    1-6. 메인페이지 return 함.

#2. TCP 통신
- TCP 통신은 UDP통신에 비해 속도가 느리다.
- 신뢰성있는 통신기법
- 웹은 TCP 통신을 한다.

#3. CIA(CIA란 기밀성(Confidentiality),무결성(Integrity),가용성(Availability)의 약자)
- 기밀성이란 주고받는 정보가 비밀스러워야 한다.
    * 기밀성 유지방법
    - 문서를 보낼때 암호화를 하여 보냄(가용성이 깨질 수 있음.)

- 무결성이란 데이터가 변하면 안된다.
    * 무결성 유지방법
    - 데이터를 다른 사람이 들고 가지 못하게 막아야한다. 
    
- 가용성은 항상 특정한 방식에 의해 사용 가능하여야 한다.
    * 가용성 유지방법
    - 문서를 보낼때 강력한 무언가를 함께 보냄.
   
- 보안 문제 해결
    1. 문서를 어떻게 전달하여 보낼지
    2. 문서(데이터)를 누구로부터 전달받았는지 확인하기
    => 이것들을 파악하면 보안의 이슈를 어느정도 해결이 가능함.

#4. RSA
1. public key(공개키)

2. private key(개인키)    

간단한 설명1)
1. A가 B에게 "메시지"를 보냄.
2. 이때 A는 공개키로 B에게 보내는데 A,B둘다 공개키, 개인키를 가지고 있는 조건에서 보내는것.
3. B의 공개키로 A의 메시지를 잠궜다. B의 공개키는 공유가 되어있어서 A가 다운받아서 잠금.
4. 중간에 해커가 메시지를 가로챘지만 메시지를 읽을 수 없다. B의 공개키로 잠겼기 때문에.
5. 이것을 열 수 있는 것은 B의 개인키를 가지고 있는 사람만이 열 수 있다.
6. 그렇기에 해커는 메시지 내용을 알 수 없고 B는 개인키를 가지고 A가 보낸 메시지의 내용을 읽을 수 있다.
==> 이것들로 해결할 수 있는 문제가 
        첫번째로 "데이터를 어떻게 보낼지"의 문제를 해결할 수 있다.

간단한 설명2)
1. A가 B에게 메시지를 A의 개인키로 보냄.
2. 중간에 해커는 A의 개인키로 보낸 메시지를 확인할 수 있음
3. B는 A의 메시지를 A의 공개키로 열어서 확인함.
4. 그 결과 B는 A의 메시지를 보고 A가 적었다는 사실을 확인
==> 결론은 A가 적었다는 사실을 확인하는것으로 전자문서의 서명-> 인증방식에서 이용을 한다.

결론)
1. 공개키로 잠금 -> 개인키로 열 수 있음
    : 암호화를 의미
    
2. 개인키로 잠금 -> 공개키로 열 수 있음
    : 전자 서명을 의미 (개인키로 잠그면 인증문제를 해결가능)
    
이것이 RSA이다.

+ 보내는 방식
B의 공개키로 잠궈서 보내면 해커가 중간에 메시지를 삭제 후 이상한것을 보낼 수 있다.
그렇기에 B의 공개키로 잠군것에 한번 더 A의 개인키로 잠금을 한다.
그러면 B는 문서를 열 때 A의 공개키로 열어본다. 열리면 인증이 해결, 안 열리면 인증이 되어있지 않기에 문서를 볼 필요가 없다.
열리면 B의 개인키로 열어서 메시지를 확인한다.
```

#4. JWT(Json Web Token)
```
     1. client에서 server로 로그인을 할때 id, pw를 server로 보내준다.
     2. 기존의 로그인 요청시에는 session을 만들었지만 JWT를 사용하면 token을 만들어준다.
     3. 이때 server가 header, payload, signiture를 만들어준다.
     4. header에는 hs256으로 서명을 했다는 정보가 있고
     5. payload에는 예를들어 username이 있다.
     6. signiture에는 Header + payload + "서버만 알고 있는 정보" 를 더하여 hs256으로 암호화한다.
     7. header, payload, signiture 를 base64로 인코딩해줌.
     8. hsa256이 아닌 RSA로 해주면 header에 RSA 명시, payload에 username, signiture를 만들때는 header와 payload를 개인키로 잠궈서 만들고 이 토큰을 돌려준다.
     9. 돌려준 토큰을 client가 받고 서버에 요청시 서버는 공개키로 signiture를 열어 보기만 하면 된다.
     10. HS256방식도 있고 RSA방식도 있는데 RSA방식을 더 많이 사용한다.
```