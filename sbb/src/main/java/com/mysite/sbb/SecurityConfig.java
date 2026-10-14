package com.mysite.sbb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// [AuthenticationManager] 로그인 인증(아이디와 비밀번호 확인)을 실제로 실행하는 관리자 객체의 규격입니다. (v3.07 추가)
import org.springframework.security.authentication.AuthenticationManager;
// [AuthenticationConfiguration] 스프링이 미리 조립해 둔 인증 설정을 가져오는 도구입니다. (v3.07 추가)
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// [EnableMethodSecurity] 메서드 단위 권한 검사(@PreAuthorize)를 켜는 꼬리표를 가져옵니다. (v3.08 추가)
//   · 지금까지는 "주소" 단위로만 막았습니다. 이제는 "메서드 실행 직전"에도 검사할 수 있습니다.
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
// ─ 메서드 보안 켜기 (v3.08 추가) ─
// prePostEnabled = true : @PreAuthorize("isAuthenticated()") 같은 검사를 메서드 실행 전에 적용합니다.
//   · 로그인하지 않은 사람이 그 메서드를 부르면, 메서드 안의 코드는 한 줄도 실행되지 않고 막힙니다.
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
				.csrf((csrf) -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
				.headers((headers) -> headers.addHeaderWriter(
						new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
				// ─ 폼 로그인 설정 (v3.07 추가) ─
				// formLogin : 아이디와 비밀번호를 입력받는 방식의 로그인을 켭니다.
				//   · loginPage("/user/login") : 로그인 화면 주소입니다. 이 주소의 GET 은 UserController.login 이 처리합니다.
				//   · defaultSuccessUrl("/")   : 로그인에 성공하면 메인(/)으로 이동합니다.
				//   · 로그인 처리(POST /user/login)는 스프링 시큐리티의 필터가 가로채서 처리합니다. 우리 코드에는 그 메서드가 없습니다.
				.formLogin((formLogin) -> formLogin.loginPage("/user/login").defaultSuccessUrl("/"))
				// ─ 로그아웃 설정 (v3.07 추가) ─
				// logoutRequestMatcher("/user/logout") : /user/logout 주소로 요청하면 로그아웃을 처리합니다.
				.logout((logout) -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
						// logoutSuccessUrl("/") : 로그아웃 후 메인으로 이동합니다.
						// invalidateHttpSession(true) : 서버에 남은 로그인 정보(세션)를 지워서 완전히 로그아웃시킵니다.
						.logoutSuccessUrl("/").invalidateHttpSession(true));
		return http.build();
	}
	
	@Bean
    // ─ 암호화 도구 등록 (v3.06 추가) ─
    // passwordEncoder() : BCrypt 암호화 도구를 만들어 돌려줍니다. 반환형은 규격인 PasswordEncoder 입니다.
	PasswordEncoder passwordEncoder() {
        // new BCryptPasswordEncoder() : BCrypt 방식 암호화 도구입니다. UserService 가 이 도구를 받아 씁니다.
		return new BCryptPasswordEncoder();
	}


	// ─ 인증 관리자 등록 (v3.07 추가) ─
	// 아래 메서드가 돌려준 AuthenticationManager 를 스프링이 보관합니다. 로그인 검사를 실행하는 부품입니다.
	@Bean
	// authenticationManager() : 스프링이 준비한 인증 설정에서 인증 관리자를 꺼내 돌려줍니다.
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			// throws Exception : 설정 과정에서 문제가 생기면 예외를 던질 수 있다는 표시입니다. (filterChain 과 같은 이유)
			throws Exception {
		// 설정에서 인증 관리자 객체를 꺼내 돌려줍니다.
		return authenticationConfiguration.getAuthenticationManager();
	}
}
