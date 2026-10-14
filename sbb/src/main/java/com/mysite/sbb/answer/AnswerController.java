// ══════════════════════════════════════════════════════════════════════
// [AnswerController = 답변 등록 창구] (v2.16 갱신: 입력 검사 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 흐름 (v2.16)
//   1) 답변 폼에서 "답변등록" → POST /answer/create/{id}
//   2) 스프링이 폼 값을 AnswerForm 에 담음 (데이터 바인딩)
//   3) @Valid 가 AnswerForm 의 규칙(@NotEmpty)을 검사하고, 결과를 BindingResult 에 기록
//   4-a) 오류가 있으면 : 저장하지 않고 질문 상세 화면을 다시 그림 (오류 메시지 표시)
//   4-b) 오류가 없으면 : 답변을 저장하고 상세 화면으로 redirect
// ══════════════════════════════════════════════════════════════════════
// 답변 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.answer;

// [@Controller] 웹 요청 창구 클래스 꼬리표입니다.
import org.springframework.stereotype.Controller;
// [Model] 화면에 넘길 데이터 바구니입니다. 검사에 실패했을 때 질문 정보를 다시 담는 데 씁니다.
import org.springframework.ui.Model;
// [BindingResult] 입력 검사 결과 보관함입니다. 오류가 있으면 여기 기록되고, hasErrors() 로 확인합니다. (v2.16 추가)
//   · 반드시 검사 대상 객체(@Valid AnswerForm) 바로 뒤에 적어야 스프링이 결과를 연결해 줍니다.
import org.springframework.validation.BindingResult;
// [@PathVariable] 주소의 {id} 자리 값(질문 번호)을 꺼내는 꼬리표입니다.
import org.springframework.web.bind.annotation.PathVariable;
// [@PostMapping] POST 요청을 메서드와 연결합니다.
import org.springframework.web.bind.annotation.PostMapping;
// [@RequestMapping] 클래스 공통 주소 "/answer" 를 정합니다.
import org.springframework.web.bind.annotation.RequestMapping;

// 질문 객체 타입입니다.
import com.mysite.sbb.question.Question;
// 질문을 찾아 주는 업무 담당입니다.
import com.mysite.sbb.question.QuestionService;

// [@Valid] "이 객체의 검사 규칙을 실행하라"는 꼬리표입니다. (v2.16 추가)
import jakarta.validation.Valid;
// [Lombok] 생성자 자동 생성 꼬리표를 가져옵니다.
import lombok.RequiredArgsConstructor;

// 이 클래스의 모든 주소 앞에 /answer 를 붙입니다.
@RequestMapping("/answer")
// final 필드 두 개를 받는 생성자를 자동으로 만듭니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 웹 요청 창구임을 스프링에게 알립니다.
@Controller
// 답변 관련 요청을 담당하는 창구 클래스입니다.
public class AnswerController {

	// 질문을 찾는 업무 담당입니다.
	private final QuestionService questionService;
	// 답변을 저장하는 업무 담당입니다.
	private final AnswerService answerService;

	// ─ 주소 연결 ─
	// [POST /answer/create/{id}] 답변 등록 폼을 보내면 createAnswer() 가 실행됩니다.
	@PostMapping("/create/{id}")
	// ─ createAnswer() 매개변수 ─
	//   Model model         : 화면 바구니 (검사 실패 시 질문 정보를 다시 담습니다)
	//   @PathVariable id    : 질문 번호 (예: 3)
	//   @Valid AnswerForm  : 폼 값을 담은 객체. @Valid 로 검사 규칙을 실행합니다. (v2.16 변경: 이전에는 @RequestParam 으로 글만 받았습니다)
	public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm,
			// BindingResult : 위 AnswerForm 검사 결과 보관함입니다. 이 값으로 저장할지 말지 정합니다.
			BindingResult bindingResult) {
		// 질문 번호로 질문을 찾습니다. (검사 실패 시에도 상세 화면을 다시 그리려면 질문이 필요합니다)
		Question question = this.questionService.getQuestion(id);
		// ─ 검사 실패 분기 ─
		// hasErrors() 는 오류가 하나라도 있으면 true 입니다. 이때는 저장하지 않습니다.
		if (bindingResult.hasErrors()) {
			// 상세 화면을 다시 그릴 때 질문 정보가 필요하므로 바구니에 다시 담습니다.
			model.addAttribute("question", question);
			// 같은 상세 화면을 다시 그립니다. 입력한 내용과 오류 메시지가 함께 보입니다.
			return "question_detail";
		}
		// 검사를 통과하면 폼 객체에서 content 를 꺼내 저장을 맡깁니다. (v2.16 변경: 폼 객체에서 꺼냄)
		this.answerService.create(question, answerForm.getContent());
		// 저장 후 상세 화면으로 이동합니다. (redirect 는 새로고침 시 중복 저장을 막습니다)
		return String.format("redirect:/question/detail/%s", id);
	}
}
