// ══════════════════════════════════════════════════════════════════════
// [SiteUser = "회원" 한 명을 DB 표(site_user)의 한 줄로 나타낸 엔티티] (v3.06 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 질문(Question), 답변(Answer)과 같은 방식입니다. 클래스 하나 = 표 하나, 필드 하나 = 열 하나.
//   · 클래스 이름 SiteUser → 표 이름 site_user (자동 변환)
//   · 필드 username → 열 username, 필드 email → 열 email ...
//
// 이 표에 들어갈 값과 규칙
//   · username : 로그인할 때 쓰는 아이디. 중복되면 안 됨 (unique)
//   · password : 비밀번호. 화면에서 받은 글자를 그대로 저장하지 않고, 암호문으로 바꿔서 저장 (UserService 에서 처리)
//   · email    : 이메일 주소. 중복되면 안 됨 (unique)
//   · id       : 기본키 번호. 저장할 때 자동으로 1, 2, 3 ... 이 붙습니다.
// ══════════════════════════════════════════════════════════════════════
// 회원 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.user;

// [@Column] 열의 세부 규칙(중복 금지, 길이 등)을 정하는 꼬리표입니다.
import jakarta.persistence.Column;
// [@Entity] 이 클래스가 DB 표와 연결된다는 표시입니다.
import jakarta.persistence.Entity;
// [@GeneratedValue] 기본키 번호를 자동으로 매기는 방식을 정하는 꼬리표입니다.
import jakarta.persistence.GeneratedValue;
// [GenerationType] 자동 번호 매기기 방식의 선택지 목록입니다.
import jakarta.persistence.GenerationType;
// [@Id] 기본키 표시입니다. 회원을 구별하는 고유 번호입니다.
import jakarta.persistence.Id;

// [Lombok @Getter] getter 메서드를 자동으로 만들기 위해 가져옵니다.
import lombok.Getter;
// [Lombok @Setter] setter 메서드를 자동으로 만들기 위해 가져옵니다.
import lombok.Setter;

// 모든 필드의 getter(getUsername 등)를 자동으로 만듭니다.
@Getter
// 모든 필드의 setter(setUsername 등)를 자동으로 만듭니다.
@Setter
// 이 클래스는 DB 표(site_user)와 연결된 엔티티입니다.
@Entity
public class SiteUser {

	// ─ 기본키 ─ 회원마다 다른 번호입니다.
	@Id
	// DB 의 자동 증가 기능으로 번호를 매깁니다. (질문, 답변과 같은 방식)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// 기본키 번호입니다. Long 은 Integer 보다 더 큰 정수까지 담을 수 있어서 회원 수가 많아도 안전합니다.
	private Long id;

	// ─ 아이디 열 ─
	// unique = true : 같은 값이 두 줄에 들어갈 수 없다는 DB 규칙입니다. 같은 아이디로 두 번 가입하면 DB 가 막습니다.
	@Column(unique = true)
	// 로그인 아이디입니다.
	private String username;

	// ─ 비밀번호 열 ─
	// 이 칸에는 암호문(해시)이 저장됩니다. 사용자가 입력한 원래 글자는 여기에 들어가지 않습니다.
	// 그래서 DB 를 열어 봐도 원래 비밀번호는 알 수 없습니다. (UserService.create 에서 암호화)
	private String password;

	// ─ 이메일 열 ─
	// 이메일도 중복 가입을 막기 위해 unique 로 둡니다.
	@Column(unique = true)
	// 이메일 주소입니다.
	private String email;
}
