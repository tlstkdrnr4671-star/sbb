// ══════════════════════════════════════════════════════════════════════
// [UserSecurityService = 로그인할 때 회원 정보를 찾아 주는 서비스] (v3.07 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 로그인 흐름 (이 클래스가 맡는 부분은 ②번입니다)
//   ① 로그인 화면에서 아이디와 비밀번호를 입력하고 전송
//   ② 스프링 시큐리티가 이 클래스의 loadUserByUsername(아이디) 를 호출 → 회원 정보와 권한을 돌려받음
//   ③ 스프링이 입력한 비밀번호를 암호화해서, 회원 정보의 암호문과 비교
//   ④ 같으면 로그인 성공, 다르면 실패
//
// 이 클래스는 "UserDetailsService" 라는 규격을 지키는 구현체입니다.
// 스프링 시큐리티는 "아이디로 사용자 정보를 찾아 주는 누군가"가 있다고만 알고, 그 방법은 우리가 정합니다.
// ══════════════════════════════════════════════════════════════════════
// 회원 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.user;

// [ArrayList] 권한 목록을 담을 자료형입니다.
import java.util.ArrayList;
// [List] 권한 여러 개를 담는 목록 자료형입니다.
import java.util.List;
// [Optional] 아이디로 찾은 회원이 없을 수도 있으니 상자에 담아 받습니다.
import java.util.Optional;

// [GrantedAuthority] 사용자가 가진 권한 한 개를 나타내는 규격입니다.
import org.springframework.security.core.GrantedAuthority;
// [SimpleGrantedAuthority] "ROLE_USER" 같은 문자열을 GrantedAuthority 로 만드는 간단한 도구입니다.
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// [User] 스프링 시큐리티용 사용자 정보 객체입니다. 아이디, 암호문, 권한 목록을 담습니다. (우리의 SiteUser 와는 다른 클래스입니다)
import org.springframework.security.core.userdetails.User;
// [UserDetails] 사용자 정보의 규격(인터페이스)입니다. User 가 이 규격을 구현합니다.
import org.springframework.security.core.userdetails.UserDetails;
// [UserDetailsService] "아이디로 사용자 정보를 찾는 서비스"의 규격입니다. 이 클래스가 이 규격을 구현합니다.
import org.springframework.security.core.userdetails.UserDetailsService;
// [UsernameNotFoundException] 아이디에 해당하는 회원이 없을 때 던지는 예외입니다. 시큐리티는 이를 "로그인 실패"로 봅니다.
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// [@Service] 업무 처리 담당 부품이라는 표시 꼬리표입니다.
import org.springframework.stereotype.Service;

// [Lombok] 생성자 자동 생성 꼬리표를 가져옵니다.
import lombok.RequiredArgsConstructor;

// final 필드(userRepository)를 받는 생성자를 만듭니다. 스프링이 저장소를 넣어 줍니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 업무 부품으로 스프링에 등록합니다.
@Service
// ─ 규격 구현 선언 ─
// implements UserDetailsService : "이 규격의 메서드를 반드시 구현하겠다"는 약속입니다. 구현하지 않으면 컴파일 에러가 납니다.
public class UserSecurityService implements UserDetailsService {

	// 회원 DB 저장소입니다. 아이디로 회원을 찾을 때 씁니다.
	private final UserRepository userRepository;

	// ─ 재정의 표시 ─
	// "부모 규격의 메서드를 내가 구현(재정의)했다"는 표시입니다. 이름이나 매개변수를 틀리면 컴파일러가 알려 줍니다.
	@Override
	// ─ loadUserByUsername(아이디) ─
	// 로그인하려는 아이디를 받아 그 회원의 정보(UserDetails)를 돌려주는 메서드입니다.
	// 스프링 시큐리티가 로그인 과정에서 자동으로 호출합니다. 우리가 직접 호출하지 않습니다.
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// ─ 회원 찾기 ─
		// findByusername(아이디) : 아이디가 같은 회원을 DB 에서 찾습니다. 없으면 빈 상자가 돌아옵니다.
		// 변수 이름 앞의 _ 는 "상자(Optional)"라는 것을 드러내려고 붙인 관례적 표시입니다.
		Optional<SiteUser> _siteUser = this.userRepository.findByusername(username);
		// ─ 회원이 없을 때 ─
		// 상자가 비어 있으면 아이디가 틀린 것입니다. 예외를 던져 로그인을 실패시킵니다.
		if (_siteUser.isEmpty()) {
			// 예외 메시지입니다. 로그인 실패 화면에는 보통 일반 문구를 보여 주므로 이 메시지는 개발자용입니다.
			throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
		}
		// ─ 회원 꺼내기 ─
		// 앞의 isEmpty 검사를 통과했으므로 안전하게 회원 객체를 꺼냅니다.
		SiteUser siteUser = _siteUser.get();
		// ─ 권한 목록 만들기 ─
		// 이 회원이 가진 권한들을 담을 빈 목록입니다.
		List<GrantedAuthority> authorities = new ArrayList<>();
		// ─ 권한 결정 (실습용 규칙) ─
		// 아이디가 admin 이면 관리자 권한을 줍니다. 글자 비교는 == 가 아니라 equals 를 씁니다.
		// ※ 실습을 위한 간단한 규칙입니다. 실제 서비스에서는 권한을 DB 에 저장해 두고 그 값을 읽습니다.
		if ("admin".equals(username)) {
			// 관리자 권한 "ROLE_ADMIN" 을 목록에 넣습니다. UserRole.ADMIN.getValue() 는 "ROLE_ADMIN" 글자입니다.
			authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
		} else {
			// 그 밖의 회원은 일반 권한 "ROLE_USER" 를 받습니다.
			authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
		}
		// ─ 결과 돌려주기 ─
		// 아이디, 암호문 비밀번호, 권한 목록을 담은 User 객체를 돌려줍니다.
		// 비밀번호 대조는 스프링이 입력값을 암호화해서 여기 든 암호문과 비교하는 방식으로 진행합니다.
		return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);
	}
}
