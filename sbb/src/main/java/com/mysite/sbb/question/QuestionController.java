// ══════════════════════════════════════════════════════════════════════
// [QuestionController = 질문 관련 요청 창구] (v2.16 갱신: 질문 등록 GET/POST 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 질문 등록은 같은 주소(/question/create)에 두 가지 요청이 옵니다.
//   GET  /question/create : 빈 입력 화면을 보여 줌  → questionCreate(QuestionForm) 첫 번째
//   POST /question/create : 입력값을 검사하고 저장  → questionCreate(@Valid ...) 두 번째
// 같은 주소라도 방식(GET/POST)이 다르면 서로 다른 요청입니다. 그래서 같은 이름의 메서드가 두 개여도 충돌하지 않습니다.
// ══════════════════════════════════════════════════════════════════════
// 질문 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.question;

// [List] 질문 목록을 담는 자료형입니다.
import java.util.List;

// [@Controller] 웹 요청 창구 클래스 꼬리표입니다.
import org.springframework.stereotype.Controller;
// [Model] 화면에 넘길 데이터 바구니입니다.
import org.springframework.ui.Model;
// [BindingResult] 질문 등록 폼의 검사 결과 보관함입니다. (v2.16 추가)
import org.springframework.validation.BindingResult;
// [@GetMapping] GET 요청을 메서드와 연결합니다.
import org.springframework.web.bind.annotation.GetMapping;
// [@PathVariable] 주소의 {id} 값을 꺼냅니다.
import org.springframework.web.bind.annotation.PathVariable;
// [@PostMapping] POST 요청을 메서드와 연결합니다. (v2.16 추가)
import org.springframework.web.bind.annotation.PostMapping;
// [@RequestMapping] 클래스 공통 주소 "/question" 을 정합니다.
import org.springframework.web.bind.annotation.RequestMapping;

// 상세 화면의 답변 입력 폼 객체 타입입니다. (v2.16 추가)
import com.mysite.sbb.answer.AnswerForm;

// [@Valid] 질문 등록 폼의 검사 규칙을 실행하라는 꼬리표입니다. (v2.16 추가)
import jakarta.validation.Valid;
// [Lombok] 생성자 자동 생성 꼬리표를 가져옵니다.
import lombok.RequiredArgsConstructor;

// 이 클래스의 모든 주소 앞에 /question 을 붙입니다.
@RequestMapping("/question")
// final 필드(questionService)를 받는 생성자를 자동으로 만듭니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 웹 요청 창구임을 스프링에게 알립니다.
@Controller
// 질문 관련 요청을 담당하는 창구 클래스입니다.
public class QuestionController {

	// 질문 업무를 맡기는 서비스 객체입니다.
	private final QuestionService questionService;

	// [GET /question/list] 질문 목록 화면을 보여 줍니다.
	@GetMapping("/list")
	// list() : 목록을 가져와 바구니에 담고 화면 이름을 돌려줍니다.
	public String list(Model model) {
		// 서비스에게 목록을 달라고 요청합니다.
		List<Question> questionList = this.questionService.getList();
		// 목록을 "questionList" 이름표로 담습니다.
		model.addAttribute("questionList", questionList);
		// question_list.html 화면을 그립니다.
		return "question_list";
	}

	// [GET /question/detail/{id}] 질문 상세 화면을 보여 줍니다.
	@GetMapping(value = "/detail/{id}")
	// detail() : 상세 화면 준비.
	//   AnswerForm answerForm : 답변 입력 폼(th:object="${answerForm}")이 쓸 빈 객체입니다. (v2.16 추가)
	//     스프링이 이 빈 객체를 model 에 자동으로 담아 주므로, 폼의 th:object 가 연결될 수 있습니다.
	public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
		// 질문 번호로 질문을 찾습니다. 없으면 DataNotFoundException 이 던져집니다.
		Question question = this.questionService.getQuestion(id);
		// 질문을 "question" 이름표로 담습니다.
		model.addAttribute("question", question);
		// 상세 화면을 그립니다.
		return "question_detail";
	}

	// ─ 질문 등록 화면 (GET, v2.16 추가) ─
	// [GET /question/create] 빈 질문 등록 폼 화면을 보여 줍니다. 아직 저장하지는 않습니다.
	@GetMapping("/create")
	// questionForm : 스프링이 빈 QuestionForm 객체를 만들어 넣습니다. 폼의 th:object 와 짝이 됩니다.
	public String questionCreate(QuestionForm questionForm) {
		// 질문 등록 화면(question_form.html)을 그립니다.
		return "question_form";
	}

	// ─ 질문 저장 (POST, v2.16 추가) ─
	// [POST /question/create] 저장하기 버튼을 누르면 이 메서드가 실행됩니다.
	@PostMapping("/create")
	// @Valid : 입력값을 QuestionForm 규칙(제목, 내용 비었는지, 제목 200자 이하인지)으로 검사합니다.
	// BindingResult : 검사 결과 보관함입니다. 반드시 @Valid 객체 바로 뒤에 둡니다.
	public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult) {
		// 오류가 있으면 저장하지 않고 입력 화면을 다시 보여 줍니다. (입력값과 오류 메시지가 함께 나옵니다)
		if (bindingResult.hasErrors()) {
			// 같은 입력 화면을 다시 그립니다.
			return "question_form";
		}
		// 검사를 통과했으므로 제목과 내용을 서비스에 맡겨 저장합니다.
		this.questionService.create(questionForm.getSubject(), questionForm.getContent());
		// 저장 후 목록 화면으로 이동합니다. (redirect 는 새로고침 중복 저장을 막습니다)
		return "redirect:/question/list";
	}
}
