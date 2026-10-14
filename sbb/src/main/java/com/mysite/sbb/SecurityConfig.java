package com.mysite.sbb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// [BCryptPasswordEncoder] BCrypt 알고리즘으로 비밀번호를 해시하는 구현체입니다. (v3.06 추가)
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// [PasswordEncoder] 비밀번호 암호화 규격(인터페이스)입니다. (v3.06 추가)
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
				.csrf((csrf) -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
				.headers((headers) -> headers.addHeaderWriter(
						new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)));
		return http.build();
	}
	
	@Bean
    // ─ 암호화 도구 등록 (v3.06 추가) ─
    // passwordEncoder() : BCrypt 암호화 도구를 만들어 돌려줍니다. 반환형은 규격인 PasswordEncoder 입니다.
    PasswordEncoder passwordEncoder() {
        // new BCryptPasswordEncoder() : BCrypt 방식 암호화 도구입니다. UserService 가 이 도구를 받아 씁니다.
        return new BCryptPasswordEncoder();
    }
}
