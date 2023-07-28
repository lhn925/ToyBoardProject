package sky.board;


import static org.springframework.security.config.Customizer.withDefaults;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /**
         * cors
         * Cross-Origin Resource Sharing
         * 교차 출처의 개념 > 다른 출처
         * -> 다른 출처와 리소스를 공유하는 것
         *
         * csrf
         * cross-site Request forgey
         *
         * 요청을 위조하여 사용자의 권한을 이용해 서버에 대한 악성공격을 하는 것
         */
        http.csrf().disable().cors().disable()
            .authorizeHttpRequests(request ->
                request.
                    dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll().
                    anyRequest().
                    authenticated() // 어떠한 요청이라도 인증필요
            ).
            formLogin(login -> login. //form 방식 로그인 사용
                defaultSuccessUrl("/", true).
                permitAll() // 대시보드 이동이 막히면 안되므로 얘는 허용
            ).
            logout(withDefaults()); // 로그아웃은 기본설정으로 (/logout으로 인증해제)
        return http.build();
    }


}