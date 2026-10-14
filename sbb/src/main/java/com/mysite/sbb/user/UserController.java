// ══════════════════════════════════════════════════════════════════════
// [UserController = 회원가입 요청 창구] (v3.06 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 회원가입 흐름
//   GET  /user/signup : 빈 가입 폼 화면을 보여 줌
//   POST /user/signup : 입력 검사 → 비밀번호 두 칸 비교 → 저장 → 성공하면 메인으로, 실패하면 같은 화면에 오류 표시
//
// 이 메서드는 "검사 단계가 여러 개"라서 return 이 여러 번 나옵니다. 각 return 의 이유를 아래에 적었습니다.
// ══════════════════════════════════════════════════════════════════════
// 회원 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.user;

// [DataIntegrityViolationException] DB 규칙(중복 금지 등)을 어기면 던져지는 예외 종류입니다. 중복 가입을 잡을 때 씁니다.
import org.springframework.dao.DataIntegrityViolationException;
// [@Controller] 웹 요청 창구 꼬리표입니다.
import org.springframework.stereotype.Controller;
// [BindingResult] 입력 검사 결과 보관함입니다.
import org.springframework.validation.BindingResult;
// [@GetMapping] GET 요청을 메서드와 연결합니다.
import org.springframework.web.bind.annotation.GetMapping;
// [@PostMapping] POST 요청을 메서드와 연결합니다.
import org.springframework.web.bind.annotation.PostMapping;
// [@RequestMapping] 클래스 공통 주소 "/user" 를 정합니다.
import org.springframework.web.bind.annotation.RequestMapping;

// [@Valid] 가입 폼 검사 규칙을 실행하라는 꼬리표입니다.
import jakarta.validation.Valid;
// [Lombok] 생성자 자동 생성 꼬리표를 가져옵니다.
import lombok.RequiredArgsConstructor;

// final 필드(userService)를 받는 생성자를 자동으로 만듭니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 웹 요청 창구임을 스프링에게 알립니다.
@Controller
// 이 클래스의 모든 주소 앞에 /user 를 붙입니다. 따라서 /signup 은 /user/signup 이 됩니다.
@RequestMapping("/user")
// 회원 관련 요청을 담당하는 창구 클래스입니다.
public class UserController {

	// 회원 업무 담당입니다.
	private final UserService userService;

	// [GET /user/signup] 빈 가입 폼 화면을 보여 줍니다.
	@GetMapping("/signup")
	// 같은 이름 signup 이지만 매개변수가 달라서 다른 메서드입니다. (오버로딩)
	// 이 메서드는 빈 폼 객체만 받아서 화면과 연결합니다.
	public String signup(UserCreateForm userCreateForm) {
		// 가입 화면(signup_form.html)을 그립니다.
		return "signup_form";
	}

	// [POST /user/signup] 가입 버튼을 누르면 실행되는 처리 메서드입니다.
	@PostMapping("/signup")
	// @Valid 가 입력 규칙을 검사하고, 결과는 bindingResult 에 담깁니다.
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
		// ─ 1단계 검사: 기본 규칙 ─
		// 아이디 길이, 빈 칸, 이메일 형식 중 하나라도 어기면 여기서 멈춥니다.
		if (bindingResult.hasErrors()) {
			// 입력값과 오류 메시지를 보여 주며 같은 화면을 다시 그립니다.
			return "signup_form";
		}

		// ─ 2단계 검사: 비밀번호 두 칸이 같은가? ─
		// equals 는 글자 내용이 같은지 비교합니다. (== 는 같은 객체인지 비교하므로 글자 비교에 쓰면 안 됩니다)
		if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			// rejectValue : password2 칸에 오류를 직접 추가합니다. (칸 이름, 오류 코드, 보여 줄 문장)
			bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			// 비밀번호 불일치 메시지와 함께 같은 화면을 다시 그립니다.
			return "signup_form";
		}

		// ─ 3단계: 저장 ─
		// try 블록 안에서 저장하다가 생기는 오류를 아래 catch 가 붙잡습니다.
		try {
			// 서비스에게 회원 저장을 맡깁니다. 비밀번호는 암호화되어 저장됩니다.
			userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
		// ─ 중복 가입 처리 ─
		// 같은 아이디나 이메일이 이미 있으면 DB 의 unique 규칙이 저장을 막고 이 예외가 던져집니다.
		} catch (DataIntegrityViolationException e) {
			// printStackTrace : 오류가 어디서 났는지 콘솔에 자세히 출력합니다. 개발할 때 원인을 찾는 데 씁니다.
			e.printStackTrace();
			// reject : 특정 칸이 아니라 폼 전체에 오류 메시지를 추가합니다.
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			// 중복 메시지와 함께 가입 화면을 다시 그립니다.
			return "signup_form";
		// ─ 그 밖의 모든 오류 ─
		// Exception 은 모든 예외의 부모입니다. 위에서 잡지 못한 예외를 여기서 한꺼번에 잡습니다.
		} catch (Exception e) {
			// 예상하지 못한 오류의 원인을 콘솔에 출력합니다.
			e.printStackTrace();
			// e.getMessage() : 예외가 가진 설명 문장을 화면 오류 메시지로 보여 줍니다.
			bindingResult.reject("signupFailed", e.getMessage());
			// 오류 메시지와 함께 가입 화면을 다시 그립니다.
			return "signup_form";
		}

		// ─ 성공 ─
		// 가입이 끝나면 메인 주소(/)로 이동합니다. redirect 라서 새로고침해도 가입이 두 번 되지 않습니다.
		return "redirect:/";
	}
}
