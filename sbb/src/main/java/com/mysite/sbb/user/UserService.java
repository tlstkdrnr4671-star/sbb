// ══════════════════════════════════════════════════════════════════════
// [UserService = 회원 업무 담당] (v3.06 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 회원을 저장할 때 지켜야 할 규칙을 모아 둔 클래스입니다. 대표 규칙은 "비밀번호는 반드시 암호화해서 저장"입니다.
// 창구(UserController)는 폼 값을 받아 여기에 넘기기만 하고, 암호화와 저장은 이 클래스가 합니다.
// ══════════════════════════════════════════════════════════════════════
// 회원 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.user;

// [PasswordEncoder] 비밀번호 암호화 도구의 규격(인터페이스)입니다. 실제 구현은 SecurityConfig 에서 등록합니다.
import org.springframework.security.crypto.password.PasswordEncoder;
// [@Service] 업무 처리 담당 부품이라는 표시 꼬리표입니다.
import org.springframework.stereotype.Service;

// [Lombok] 생성자 자동 생성 꼬리표를 가져옵니다.
import lombok.RequiredArgsConstructor;

// ─ 생성자 주입 ─
// final 필드 두 개(userRepository, passwordEncoder)를 받는 생성자를 Lombok 이 만듭니다.
// 스프링이 두 부품을 찾아 넣어 줍니다. (앞 단계와 같은 방식)
@RequiredArgsConstructor
// 업무 부품으로 스프링에 등록합니다.
@Service
// 회원 업무 담당 클래스입니다.
public class UserService {

	// 회원 DB 저장소입니다.
	private final UserRepository userRepository;
	// 비밀번호 암호화 도구입니다. SecurityConfig 에서 만든 BCryptPasswordEncoder 가 들어옵니다.
	private final PasswordEncoder passwordEncoder;

	// ─ create() : 회원 저장 ─
	//   · 매개변수: 아이디, 이메일, 비밀번호(원본 글자) 세 가지입니다.
	//   · 반환형 SiteUser: 저장된 회원 객체를 돌려줍니다. (호출한 쪽에서 쓸 수도 있습니다)
	public SiteUser create(String username, String email, String password) {
		// 빈 회원 객체를 만듭니다. 아직 DB 에는 없습니다.
		SiteUser user = new SiteUser();
		// 아이디를 채웁니다.
		user.setUsername(username);
		// 이메일을 채웁니다.
		user.setEmail(email);
		// ─ 비밀번호 암호화 (보안의 핵심) ─
		//   · passwordEncoder.encode(원본) : 원본 글자를 BCrypt 해시(암호문)로 바꿉니다.
		//   · 같은 비밀번호도 매번 다른 암호문이 나옵니다. (솔트라는 임의 값이 섞이기 때문)
		//   · 암호문으로는 원래 비밀번호를 되돌릴 수 없습니다. 로그인할 때는 입력값을 다시 암호화해서 비교합니다.
		user.setPassword(passwordEncoder.encode(password));
		// ─ 저장 ─
		// save : INSERT 가 실행되어 site_user 표에 한 줄이 추가됩니다. 아이디나 이메일이 중복이면 DB 가 막고 예외를 던집니다.
		this.userRepository.save(user);
		// 저장된 회원 객체를 돌려줍니다.
		return user;
	}
}
