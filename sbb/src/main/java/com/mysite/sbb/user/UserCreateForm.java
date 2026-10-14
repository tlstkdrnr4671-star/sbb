// ══════════════════════════════════════════════════════════════════════
// [UserCreateForm = 회원가입 폼의 "전달 상자"] (v3.06 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 회원가입 화면의 입력값(아이디, 비밀번호 두 번, 이메일)을 담고, 각 칸의 검사 규칙을 적습니다.
// SiteUser(DB 표와 연결된 엔티티)와 따로 두는 이유는 AnswerForm, QuestionForm 과 같습니다.
//   · 화면 입력 규칙을 바꿔도 DB 구조(SiteUser)는 그대로 둘 수 있습니다.
//   · 비밀번호 확인 칸은 DB 에 저장할 값이 아니라서 SiteUser 에는 없습니다.
// ══════════════════════════════════════════════════════════════════════
// 회원 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.user;

// [@Email] 이메일 형식(예: abc@test.com)인지 검사하는 꼬리표입니다.
import jakarta.validation.constraints.Email;
// [@NotEmpty] 비어 있으면 안 되는 값에 붙이는 검사 꼬리표입니다.
import jakarta.validation.constraints.NotEmpty;
// [@Size] 글자 수 범위를 정하는 검사 꼬리표입니다.
import jakarta.validation.constraints.Size;

// [Lombok @Getter] getter 를 자동으로 만들기 위해 가져옵니다.
import lombok.Getter;
// [Lombok @Setter] setter 를 자동으로 만들기 위해 가져옵니다. 스프링이 입력값을 넣을 때 씁니다.
import lombok.Setter;

// 모든 필드의 getter 를 자동으로 만듭니다.
@Getter
// 모든 필드의 setter 를 자동으로 만듭니다.
@Setter
// 회원가입 폼 전용 객체입니다. @Entity 가 없으므로 DB 와는 연결되지 않습니다.
public class UserCreateForm {
	// ─ 아이디 길이 규칙 ─
	// min = 3 : 최소 3글자, max = 25 : 최대 25글자입니다. 너무 짧거나 긴 아이디를 막습니다.
	@Size(min = 3, max = 25)
	// ─ 아이디 비움 금지 ─
	// 여러 검사 꼬리표를 한 칸에 함께 붙일 수 있습니다. 둘 다 통과해야 합니다.
	@NotEmpty(message = "사용자ID는 필수항목입니다.")
	// 아이디 입력값입니다.
	private String username;

	// 첫 번째 비밀번호 입력은 비우면 안 됩니다.
	@NotEmpty(message = "비밀번호는 필수항목입니다.")
	// 비밀번호 첫 번째 입력값입니다. 이 값을 암호화해서 저장합니다.
	private String password1;

	// 비밀번호 확인 칸도 비우면 안 됩니다.
	@NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
	// 비밀번호 두 번째 입력값입니다. 첫 번째 값과 같은지는 컨트롤러가 비교합니다.
	private String password2;

	// 이메일도 필수입니다.
	@NotEmpty(message = "이메일은 필수항목입니다.")
	// ─ 이메일 형식 검사 ─
	// "abc@test.com" 처럼 @ 와 도메인이 있는 형식인지 확인합니다.
	@Email
	// 이메일 입력값입니다.
	private String email;
}
