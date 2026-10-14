// ══════════════════════════════════════════════════════════════════════
// [UserRole = 사용자 권한 종류를 정해 둔 열거형(enum)] (v3.07 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 권한(Role) = "이 사람이 무엇을 할 수 있는가"를 나타내는 이름표입니다.
//   · ADMIN : 관리자 권한
//   · USER  : 일반 회원 권한
//
// 열거형(enum) = 정해진 선택지만 가질 수 있는 자료형입니다.
//   UserRole.ADMIN 또는 UserRole.USER 처럼 정해진 값 중 하나만 씁니다. 엉뚱한 값을 넣으면 컴파일 에러가 납니다.
// ══════════════════════════════════════════════════════════════════════
// 회원 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.user;

// [Lombok @Getter] getValue() 메서드를 자동으로 만들기 위해 가져옵니다.
import lombok.Getter;

// 필드 value 의 getter(getValue) 를 자동으로 만듭니다.
@Getter
// ─ 열거형 선언 ─
// enum 은 "정해진 값 목록"입니다. 여기서는 권한이 두 가지뿐입니다.
public enum UserRole {
	// ─ 선택지와 값 ─
	//   · ADMIN("ROLE_ADMIN") : 관리자 선택지이고, 뒤에 붙은 문자열 "ROLE_ADMIN" 이 실제 값입니다.
	//   · USER("ROLE_USER")   : 일반 회원 선택지입니다.
	//   · "ROLE_" 접두사는 스프링 시큐리티가 권한을 인식하는 표준 이름 규칙입니다. 이 규칙을 지켜야 시큐리티가 알아봅니다.
	ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

	// ─ 열거형의 생성자 ─
	// 선택지를 만들 때 값(value)을 함께 넣어 저장하는 생성자입니다. 열거형의 생성자는 public 을 쓰지 않습니다.
	UserRole(String value) {
		// 전달받은 값을 필드에 저장합니다.
		this.value = value;
	}

	// 권한 값을 담는 필드입니다. @Getter 덕분에 getValue() 로 꺼냅니다. (예: UserRole.ADMIN.getValue() → "ROLE_ADMIN")
	private String value;
}
