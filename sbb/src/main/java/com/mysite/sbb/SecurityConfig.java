// ══════════════════════════════════════════════════════════════════════
// [SecurityConfig = 웹 보안 규칙을 정하는 설정 클래스] (v3.05 추가: Spring Security 도입)
// ══════════════════════════════════════════════════════════════════════
//
// 이 파일이 하는 일 한 줄 요약
//   "어떤 주소를 누가 열 수 있는지", "보안 공격을 어떻게 막는지"를 한 곳에 적어 둔 설정입니다.
//
// ── 용어 정리 ─────────────────────────────────────────────────────────
//   인증(Authentication) : "너 누구야?" 확인 (아이디와 비밀번호 검사 - 다음 단계들에서 다룸)
//   인가(Authorization)  : "너 이거 해도 돼?" 확인 (권한 검사)
//   필터(Filter)        : 요청이 컨트롤러에 도착하기 전과 응답이 나가기 전에 거치는 검문소 한 칸
//   CSRF 토큰            : 우리 사이트 화면에서만 보낼 수 있는 임의의 값. 남이 몰래 요청하는 것을 막습니다.
//
// ── 요청이 처리되는 길 (검문소 개념) ──────────────────────────────────────
//   [브라우저] 요청
//      ▼
//   [톰캣] → [보안 검문소 묶음 = SecurityFilterChain]   ← 이 파일의 filterChain() 이 만든 규칙
//      · 헤더를 붙이는 검문, CSRF 토큰 검사, 권한 검사 ... 순서대로 통과
//      ▼
//   [컨트롤러] → [화면]
//
// ── 이 단계에서 정한 규칙 ─────────────────────────────────────────────
//   ① 모든 주소(/**)는 누구나 접근 허용 (permitAll) - 아직 로그인 기능이 없으므로
//   ② /h2-console/** 는 CSRF 검사에서 제외 (H2 DB 콘솔 화면이 이 검사와 맞지 않으므로)
//   ③ 응답에 X-Frame-Options 헤더를 붙여 "같은 사이트에서만 화면 삽입 허용"
// ══════════════════════════════════════════════════════════════════════
package com.mysite.sbb;

// [@Bean] 메서드가 돌려준 객체를 스프링이 보관(부품 등록)하게 하는 꼬리표입니다.
import org.springframework.context.annotation.Bean;
// [@Configuration] 설정 클래스라는 표시입니다. (SbbApplication 과 같은 종류)
import org.springframework.context.annotation.Configuration;
// [HttpSecurity] 보안 규칙을 쌓아 올리는 설계 도구(빌더)입니다. http.xxx().yyy() 처럼 이어서 씁니다.
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// [@EnableWebSecurity] 웹 보안 기능을 켜는 꼬리표입니다. 이게 있어야 아래 규칙이 적용됩니다.
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// [SecurityFilterChain] 검문소들이 줄지어 있는 "필터 체인" 타입입니다. 이 타입을 돌려주면 규칙이 실제로 쓰입니다.
import org.springframework.security.web.SecurityFilterChain;
// [XFrameOptionsHeaderWriter] X-Frame-Options 라는 응답 헤더를 쓰는 도구입니다. (화면 삽입 허용 범위 제어)
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
// [AntPathRequestMatcher] 주소 패턴("/**", "/h2-console/**")과 요청을 비교하는 도구입니다.
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// ─ 설정 클래스 표시 ─
// 스프링이 시작할 때 이 클래스를 읽고, 안의 @Bean 메서드를 실행해서 규칙을 만듭니다.
@Configuration
// ─ 웹 보안 켜기 ─
// 스프링 웹 보안(필터 체인 전체)을 활성화합니다. 이 꼬리표가 없으면 아래 설정은 무시됩니다.
@EnableWebSecurity
// 웹 보안 규칙을 모아 둔 설정 클래스입니다.
public class SecurityConfig {
	// ─ 필터 체인을 부품으로 등록 ─
	// 아래 메서드가 돌려준 SecurityFilterChain 을 스프링이 보관하고, 모든 요청에 적용합니다.
	@Bean
	// ─ filterChain() 메서드 ─
	//   · HttpSecurity http : 규칙을 적을 설계 도구입니다. 스프링이 넣어 줍니다.
	//   · throws Exception : 설정 중 문제가 생기면 예외가 나올 수 있다는 표시입니다.
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// ─ 요청 권한 규칙 ─
		// authorizeHttpRequests : "요청마다 누가 열 수 있는지" 정하는 부분입니다.
		// (authorizeHttpRequests) -> ... 는 람다식입니다. "이 값을 받아서 아래 규칙을 적용해라"라는 짧은 함수 표기입니다.
		http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				// ─ 규칙 ①: 모든 주소 허용 ─
				// requestMatchers("/**") : 모든 주소(/** 는 전체)를 가리킵니다. permitAll() : 누구나 접근 허용입니다.
				// 아직 로그인 기능이 없으므로, 지금은 모든 화면이 로그인 없이 열립니다.
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
				// ─ 규칙 ②: CSRF 보호 ─
				// CSRF(사이트 간 요청 위조)는 "다른 사이트가 내 로그인 상태를 몰래 이용해 요청을 보내는 공격"입니다.
				//   · 스프링은 우리 화면의 form 마다 숨은 토큰(_csrf)을 넣고, 전송 때 맞는지 검사합니다.
				//   · ignoringRequestMatchers("/h2-console/**") : H2 콘솔은 이 검사가 맞지 않으므로 예외로 둡니다.
				.csrf((csrf) -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
				// ─ 규칙 ③: 응답 헤더 ─
				// 응답에 붙는 부가 정보(헤더)를 설정합니다.
				.headers((headers) -> headers.addHeaderWriter(
						// ─ X-Frame-Options: SAMEORIGIN ─
						// "같은 사이트 안에서만 이 화면을 frame 에 넣을 수 있다"는 뜻입니다.
						// H2 콘솔 화면이 frame 으로 표시되므로 이 허용이 있어야 콘솔이 보입니다. 다른 사이트의 삽입은 막습니다.
						new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)));
		// ─ 완성 ─
		// http.build() : 지금까지 적은 규칙을 하나의 SecurityFilterChain 으로 완성해서 돌려줍니다.
		return http.build();
	}
}
