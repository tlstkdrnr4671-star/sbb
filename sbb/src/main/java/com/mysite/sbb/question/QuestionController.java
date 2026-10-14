// ══════════════════════════════════════════════════════════════════════
// [QuestionController = "질문" 관련 요청을 받는 창구] (v2.11 갱신)
// ══════════════════════════════════════════════════════════════════════
//
// 이번 단계에서 바뀐 점: 주소의 앞부분 "/question" 을 클래스 맨 위에 한 번만 적습니다.
//   이전: @GetMapping("/question/list") , @GetMapping(value = "/question/detail/{id}")
//   지금: @RequestMapping("/question") 를 클래스에 붙이고, 메서드에는 뒷부분만 적습니다.
//
// ── 최종 주소를 만드는 규칙 ───────────────────────────────────────────────
//   최종 주소 = 클래스의 @RequestMapping 값 + 메서드의 @GetMapping 값
//     "/question" + "/list"          → /question/list
//     "/question" + "/detail/{id}"   → /question/detail/{id}
//
// ── 이렇게 나누면 좋은 점 ────────────────────────────────────────────────
//   · "/question" 을 바꿔야 할 때 한 줄만 고치면 모든 질문 주소가 함께 바뀝니다.
//   · 같은 기능의 주소가 한 클래스에 모여 있어서 읽기 쉽습니다.
//   · 답변 기능(AnswerController)은 같은 방식으로 "/answer" 를 따로 맡게 됩니다. (다음 단계)
//
// ── 등장하는 흐름 (질문 목록 열기) ─────────────────────────────────────────
//   [브라우저] GET /question/list
//     → 스프링: 클래스 주소 /question 이 맞는지 확인 → 메서드 주소 /list 가 맞는지 확인
//     → list() 실행 → questionService.getList() → 목록 → "question_list" 화면
// ══════════════════════════════════════════════════════════════════════
package com.mysite.sbb.question;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// [@RequestMapping] 클래스 전체에 공통으로 붙는 "주소 앞부분"을 정하는 꼬리표를 가져옵니다. (v2.11 추가)
//   @GetMapping 은 메서드 하나의 주소를 정하고, @RequestMapping 은 클래스 전체의 주소를 정합니다.
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

// ─ 클래스 공통 주소 (v2.11 추가) ─
// 이 클래스 안의 모든 주소 앞에 "/question" 을 자동으로 붙입니다.
//   · 예) 아래 @GetMapping("/list") 는 실제로 /question/list 가 됩니다.
//   · 예) 아래 @GetMapping(value = "/detail/{id}") 는 실제로 /question/detail/{id} 가 됩니다.
//   · 주소를 한곳에서 관리하므로, 나중에 "/question" 을 "/questions"로 바꾸기도 쉽습니다.
@RequestMapping("/question")
// final 필드(questionService)를 받는 생성자를 Lombok 이 만들어 줍니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 웹 요청 창구임을 스프링에게 알립니다.
@Controller
// 질문 관련 요청을 담당하는 창구 클래스입니다.
public class QuestionController {

	// 질문 업무를 맡기는 서비스 객체입니다. 스프링이 생성자로 넣어 줍니다.
	private final QuestionService questionService;

	// ─ 주소 조립 (v2.11 변경) ─
	// 메서드에는 뒷부분 "/list"만 적었습니다. 앞부분 "/question"은 위 @RequestMapping 이 붙여 줍니다.
	// 최종 주소: GET /question/list  (질문 목록 화면)
	@GetMapping("/list")
	// list() : 질문 목록 화면을 준비합니다. 메서드 자체의 내용은 바뀌지 않았습니다.
	public String list(Model model) {
		// 서비스에게 질문 목록을 달라고 요청합니다. (v2.09 부터 Service 를 거칩니다)
		List<Question> questionList = this.questionService.getList();
		// 목록을 "questionList" 이름표로 바구니에 담습니다. 화면의 th:each 가 이 이름으로 꺼냅니다.
		model.addAttribute("questionList", questionList);
		// question_list.html 화면을 그리라는 신호입니다.
		return "question_list";
	}

	// ─ 주소 조립 (v2.11 변경) ─
	// "/detail/{id}"만 적었습니다. 최종 주소는 /question/detail/{id} 입니다.
	//   · {id} : 주소 중간의 "바뀌는 자리"입니다. 질문 번호가 들어갑니다. (예: /question/detail/3)
	@GetMapping(value = "/detail/{id}")
	// detail() : 질문 상세 화면을 준비합니다. (v2.10 에서 추가, 메서드 내용은 그대로입니다)
	//   · @PathVariable("id") Integer id : 주소의 {id} 값을 정수 id 로 꺼냅니다.
	public String detail(Model model, @PathVariable("id") Integer id) {
		// 번호로 질문 한 개를 찾습니다. 없으면 DataNotFoundException 이 던져지고 404 응답이 됩니다.
		Question question = this.questionService.getQuestion(id);
		// 찾은 질문을 "question" 이름표로 바구니에 담습니다.
		model.addAttribute("question", question);
		// question_detail.html 화면을 그리라는 신호입니다.
		return "question_detail";
	}
}
