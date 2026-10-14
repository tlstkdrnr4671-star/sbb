// ══════════════════════════════════════════════════════════════════════
// [QuestionController = 질문 관련 요청 창구] (v3.02 갱신: 페이지 번호를 받아 목록 표시)
// ══════════════════════════════════════════════════════════════════════
//
// 목록 화면 주소가 이제 이렇게 바뀝니다.
//   /question/list          → 첫 페이지 (page 값이 없으면 0)
//   /question/list?page=2   → 세 번째 페이지
// 주소 뒤의 ?page=2 를 @RequestParam 이 꺼내서 메서드 매개변수 page 에 넣어 줍니다.
// ══════════════════════════════════════════════════════════════════════
// 질문 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.question;

// [Page] 페이지 상자 타입입니다. 목록 화면에 "paging" 이름으로 넘깁니다. (v3.02 추가)
import org.springframework.data.domain.Page;
// [@Controller] 웹 요청 창구 클래스 꼬리표입니다.
import org.springframework.stereotype.Controller;
// [Model] 화면에 넘길 데이터 바구니입니다.
import org.springframework.ui.Model;
// [BindingResult] 검사 결과 보관함입니다. (질문 등록에서 씀)
import org.springframework.validation.BindingResult;
// [@GetMapping] GET 요청을 메서드와 연결합니다.
import org.springframework.web.bind.annotation.GetMapping;
// [@PathVariable] 주소의 {id} 값을 꺼내는 꼬리표입니다.
import org.springframework.web.bind.annotation.PathVariable;
// [@PostMapping] POST 요청을 메서드와 연결합니다.
import org.springframework.web.bind.annotation.PostMapping;
// [@RequestMapping] 클래스 공통 주소 "/question" 을 정합니다.
import org.springframework.web.bind.annotation.RequestMapping;
// [@RequestParam] 주소 뒤의 ?이름=값 을 메서드 매개변수로 꺼내는 꼬리표입니다. (v3.02 추가)
import org.springframework.web.bind.annotation.RequestParam;

// 상세 화면의 답변 입력 폼 객체 타입입니다.
import com.mysite.sbb.answer.AnswerForm;

// [@Valid] 질문 등록 폼의 검사 규칙을 실행하라는 꼬리표입니다.
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

	// 질문 업무 담당입니다.
	private final QuestionService questionService;

	// [GET /question/list] 목록 화면 요청입니다. 주소 뒤에 ?page=번호 가 붙을 수 있습니다.
	@GetMapping("/list")
	// ─ list() 매개변수 (v3.02 변경) ─
	//   @RequestParam(value = "page", defaultValue = "0") int page
	//     · 주소의 ?page=값 을 꺼내 int 변수 page 에 넣습니다. 예) /question/list?page=2 → page = 2
	//     · 주소에 page 가 없으면 기본값 "0"(첫 페이지)을 씁니다. (defaultValue 덕분에 빈 값이 되지 않습니다)
	//     · 문자열 "2" 는 스프링이 int 2 로 바꿔 줍니다.
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		// 서비스에게 "그 페이지의 질문들"을 요청합니다. 결과는 페이지 상자(Page)입니다.
		Page<Question> paging = this.questionService.getList(page);
		// ─ 바구니에 담기 (v3.02 변경) ─
		// 이전에는 questionList 였지만 이제는 "paging" 이름표로 페이지 상자를 담습니다.
		// 화면은 paging 안의 질문 목록과 페이지 번호 정보를 함께 씁니다. (paging.number, paging.totalPages 등)
		model.addAttribute("paging", paging);
		// question_list.html 화면을 그립니다.
		return "question_list";
	}

	// [GET /question/detail/{id}] 질문 상세 화면 요청입니다. (v2.10 부터 있던 주소)
	@GetMapping(value = "/detail/{id}")
	// detail() : 상세 화면 준비. 설명은 v2.10, v2.16 에서 한 것과 같습니다.
	public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
		// 질문 번호로 질문 한 개를 찾습니다.
		Question question = this.questionService.getQuestion(id);
		// 질문을 "question" 이름표로 담습니다.
		model.addAttribute("question", question);
		// 상세 화면을 그립니다.
		return "question_detail";
	}

	// [GET /question/create] 질문 등록 화면을 보여 줍니다.
	@GetMapping("/create")
	// 빈 QuestionForm 객체를 받아 폼과 연결합니다.
	public String questionCreate(QuestionForm questionForm) {
		// 질문 등록 화면을 그립니다.
		return "question_form";
	}

	// [POST /question/create] 저장하기 버튼을 누르면 실행됩니다.
	@PostMapping("/create")
	// @Valid 로 검사하고 BindingResult 로 결과를 받습니다. (v2.16 설명과 같습니다)
	public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult) {
		// 오류가 있으면 저장하지 않고 입력 화면을 다시 보여 줍니다.
		if (bindingResult.hasErrors()) {
			// 같은 입력 화면을 다시 그립니다.
			return "question_form";
		}
		// 검사를 통과하면 저장을 서비스에 맡깁니다.
		this.questionService.create(questionForm.getSubject(), questionForm.getContent());
		// 저장 후 목록 화면으로 이동합니다. (첫 페이지로 이동합니다)
		return "redirect:/question/list";
	}
}
