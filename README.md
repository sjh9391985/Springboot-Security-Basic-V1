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