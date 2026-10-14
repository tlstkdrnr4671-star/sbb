// ══════════════════════════════════════════════════════════════════════
// [AnswerController = 답변 등록 창구] (v3.08 갱신: 로그인한 회원만, 작성자 기록)
// ══════════════════════════════════════════════════════════════════════
//
// 흐름 (v3.08)
//   1) 로그인 여부 검사 : @PreAuthorize("isAuthenticated()") 가 메서드 실행 전에 확인합니다.
//   2) 입력 검사       : @Valid AnswerForm 과 BindingResult 가 내용을 검사합니다.
//   3) 질문 찾기       : 질문 번호로 Question 객체를 찾습니다.
//   4) 작성자 찾기     : Principal 로 지금 로그인한 아이디를 얻고, 그 아이디로 SiteUser 를 찾습니다.
//   5) 저장           : 내용 + 작성자를 AnswerService 에 맡깁니다.
//   6) 이동           : 질문 상세 화면으로 redirect 합니다.
// ══════════════════════════════════════════════════════════════════════
// 답변 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.answer;

// [Principal] 지금 로그인한 사용자 정보를 담는 객체입니다. principal.getName() 으로 아이디를 얻습니다. (v3.08 추가)
import java.security.Principal;

// [@PreAuthorize] 메서드 실행 전에 권한을 검사하는 꼬리표입니다. 조건이 거짓이면 메서드가 실행되지 않습니다. (v3.08 추가)
import org.springframework.security.access.prepost.PreAuthorize;
// [@Controller] 웹 요청 창구 클래스 꼬리표입니다.
import org.springframework.stereotype.Controller;
// [Model] 화면에 넘길 데이터 바구니입니다.
import org.springframework.ui.Model;
// [BindingResult] 입력 검사 결과 보관함입니다.
import org.springframework.validation.BindingResult;
// [@PathVariable] 주소의 {id} 값을 꺼내는 꼬리표입니다.
import org.springframework.web.bind.annotation.PathVariable;
// [@PostMapping] POST 요청을 메서드와 연결합니다.
import org.springframework.web.bind.annotation.PostMapping;
// [@RequestMapping] 클래스 공통 주소 "/answer" 를 정합니다.
import org.springframework.web.bind.annotation.RequestMapping;

// 질문 객체 타입입니다.
import com.mysite.sbb.question.Question;
// 질문을 찾는 업무 담당입니다.
import com.mysite.sbb.question.QuestionService;
// 작성자 회원 객체 타입입니다. (v3.08 추가)
import com.mysite.sbb.user.SiteUser;
// 지금 로그인한 회원을 찾아 주는 업무 담당입니다. (v3.08 추가)
import com.mysite.sbb.user.UserService;

// [@Valid] 입력값 검사를 실행하라는 꼬리표입니다.
import jakarta.validation.Valid;
// [Lombok] 생성자 자동 생성 꼬리표를 가져옵니다.
import lombok.RequiredArgsConstructor;

// 이 클래스의 모든 주소 앞에 /answer 를 붙입니다.
@RequestMapping("/answer")
// final 필드 세 개를 받는 생성자를 자동으로 만듭니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 웹 요청 창구임을 스프링에게 알립니다.
@Controller
// 답변 관련 요청을 담당하는 창구 클래스입니다.
public class AnswerController {

	// 질문 업무 담당입니다.
	private final QuestionService questionService;
	// 답변 업무 담당입니다.
	private final AnswerService answerService;
	// 회원 업무 담당입니다. 지금 로그인한 회원을 찾을 때 씁니다. (v3.08 추가)
	private final UserService userService;

	// ─ 로그인 필수 (v3.08 추가) ─
	// 로그인하지 않은 사람이 이 메서드를 부르면 메서드 안은 실행되지 않고 막힙니다.
	// 로그인하지 않은 상태에서 "답변등록"을 눌러도 저장되지 않는 이유입니다.
	@PreAuthorize("isAuthenticated()")
	// [POST /answer/create/{id}] 답변 등록 요청을 처리합니다.
	@PostMapping("/create/{id}")
	// createAnswer() 의 매개변수 다섯 개입니다. 뒤쪽에 Principal 이 추가되었습니다.
	//   · Principal principal : 지금 로그인한 사용자 정보입니다. 스프링 시큐리티가 자동으로 넣어 줍니다. (v3.08 추가)
	public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm,
			// BindingResult : 입력 검사 결과 보관함입니다.
			BindingResult bindingResult, Principal principal) {
		// 질문 번호로 질문 객체를 찾습니다.
		Question question = this.questionService.getQuestion(id);
		// ─ 작성자 찾기 (v3.08 추가) ─
		// principal.getName() : 로그인한 아이디 문자열입니다. 그 아이디로 회원 객체(SiteUser)를 찾습니다.
		// 로그인 정보는 화면이 아니라 시큐리티가 가지고 있어서, Principal 을 통해 받습니다.
		SiteUser siteUser = this.userService.getUser(principal.getName());
		// 입력 오류가 있으면 저장하지 않고 상세 화면을 다시 보여 줍니다.
		if (bindingResult.hasErrors()) {
			// 다시 그릴 때 질문 정보가 필요하므로 바구니에 담습니다.
			model.addAttribute("question", question);
			// 같은 상세 화면을 다시 그립니다.
			return "question_detail";
		}
		// ─ 저장 (v3.08 변경) ─
		// 내용과 작성자를 함께 서비스에 맡깁니다. 이제 답변에는 누가 썼는지도 기록됩니다.
		this.answerService.create(question, answerForm.getContent(), siteUser);
		// 저장 후 상세 화면으로 이동합니다.
		return String.format("redirect:/question/detail/%s", id);
	}
}
