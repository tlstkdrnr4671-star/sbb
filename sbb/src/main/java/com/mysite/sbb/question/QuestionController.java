// ══════════════════════════════════════════════════════════════════════
// [QuestionController = "질문" 관련 요청을 받는 창구] (v2.09 갱신)
// ══════════════════════════════════════════════════════════════════════
//
// 이번 단계부터 창구는 DB 를 직접 만지지 않습니다. "업무 담당(Service)"에게 일을 부탁합니다.
//
// ── 질문 목록 화면을 열 때의 전체 흐름 (세 계층) ─────────────────────────
//   [1] 브라우저: GET http://localhost:8070/question/list
//   [2] 스프링이 이 메서드(list)를 찾아 실행 ............ ← 창구 (Controller, 이 클래스)
//   [3] list() 가 questionService.getList() 호출 ....... ← 업무 (Service, QuestionService)
//   [4] getList() 가 questionRepository.findAll() 호출 ... ← 저장소 (Repository, QuestionRepository)
//   [5] JPA 가 SQL 을 실행하고 Question 목록을 돌려줌 (DB 행 → 자바 객체)
//   [6] 목록이 거꾸로 올라와 model 에 담김 → "question_list" → Thymeleaf 가 HTML 완성
//
// ── 왜 세 계층으로 나눌까? ───────────────────────────────────────────────
//   · 창구가 DB 까지 직접 만지면, 나중에 "정렬 규칙을 바꿔라"는 요구가 와도 창구 여러 곳을 고쳐야 합니다.
//   · 업무 규칙을 Service 한 곳에 모아 두면, 여러 창구가 같은 규칙을 재사용할 수 있습니다.
//   · 각 계층이 자기 일만 하므로 코드를 찾기도 쉽고, 테스트도 따로 하기 쉽습니다.
// ══════════════════════════════════════════════════════════════════════
// 질문 관련 클래스가 모인 폴더 주소입니다. 같은 폴더의 QuestionService 는 import 없이 바로 쓸 수 있습니다.
package com.mysite.sbb.question;

// [List] 질문 여러 개를 담는 목록 자료형입니다. getList() 의 결과를 받을 때 씁니다.
import java.util.List;

// [@Controller] 웹 요청을 처리하는 창구 클래스라는 표시 꼬리표입니다.
import org.springframework.stereotype.Controller;
// [Model] 화면으로 넘길 데이터 바구니입니다. 스프링이 빈 바구니를 넣어 줍니다.
import org.springframework.ui.Model;
// [@GetMapping] GET 요청의 주소와 메서드를 연결하는 꼬리표입니다.
import org.springframework.web.bind.annotation.GetMapping;
// [@PathVariable] 주소의 "바뀌는 부분"을 메서드의 매개변수로 꺼내 주는 꼬리표를 가져옵니다. (v2.10 추가)
import org.springframework.web.bind.annotation.PathVariable;

// [Lombok] @RequiredArgsConstructor 를 쓰기 위해 가져옵니다.
import lombok.RequiredArgsConstructor;

// ─ 생성자 자동 생성 ─
// final 필드(questionService)를 받는 생성자를 Lombok 이 만들어 줍니다. 스프링이 QuestionService 객체를 넣어 줍니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 이 클래스가 웹 요청 창구임을 스프링에게 알립니다. 스프링은 시작할 때 객체를 만들어 보관합니다.
@Controller
// 질문 관련 요청을 담당하는 창구 클래스입니다.
public class QuestionController {

	// ─ 업무 담당 필드 ─
	// 질문 업무를 맡기는 QuestionService 객체입니다.
	  //   · 이전 단계에서는 QuestionRepository 를 직접 썼지만, 이제는 Service 를 거칩니다.
	  //   · final 이라서 한 번 넣어 준 뒤에는 바꾸지 않습니다.
	private final QuestionService questionService;

	// ─ 주소 연결 ─
	// [GET /question/list] 이 주소로 GET 요청이 오면 아래 list() 가 실행됩니다.
	@GetMapping("/question/list")
	// ─ list() 메서드 ─
	// 질문 목록 화면을 준비합니다. 데이터를 직접 가져오지 않고 업무 담당에게 부탁만 합니다.
	//   · Model model : 화면으로 넘길 바구니 (스프링이 넣어 줌)
	//   · 반환형 String : 화면 이름 "question_list" 를 돌려주기 위한 타입입니다.
	public String list(Model model) {
		// ─ 업무 담당에게 부탁 ─
		//   · questionService.getList() 를 호출하면 Service 가 필요한 DB 조회를 맡아서 결과를 돌려줍니다.
		//   · 여기서는 "어떻게 DB 에서 가져오는지"를 전혀 몰라도 됩니다. 이것이 계층을 나누는 이유입니다.
		//   · 결과 타입은 List<Question> 입니다. (질문 객체들의 목록)
		List<Question> questionList = this.questionService.getList();
		// 바구니에 "questionList" 라는 이름표로 목록을 담습니다. 화면에서는 같은 이름으로 꺼냅니다.
		model.addAttribute("questionList", questionList);
		// "question_list" → templates/question_list.html 을 그리라는 신호입니다. (앞의 폴더와 뒤의 .html 은 자동으로 붙습니다)
		return "question_list";
	}

	// ─ 주소 패턴 (v2.10 추가) ─
	// [GET /question/detail/{id}] 중괄호 {id} 는 "아무 숫자나 들어갈 수 있는 자리"입니다.
	//   · /question/detail/1 , /question/detail/2 , /question/detail/57 모두 이 메서드가 처리합니다.
	//   · value = "..." 은 주소를 적는 이름 있는 속성 방식입니다. 앞 단계의 @GetMapping("/question/list") 와 같은 뜻입니다.
	@GetMapping(value = "/question/detail/{id}")
	// ─ detail() 메서드 ─
	// 질문 상세 화면을 준비합니다. 목록 화면과 달리 "한 개의 질문"만 가져옵니다.
	//   · @PathVariable("id") Integer id : 주소의 {id} 자리에 적힌 값(예: 3)을 꺼내 정수형 id 변수에 넣습니다.
	//     주소는 원래 글자(문자열)라서 "3" 이 들어옵니다. 스프링이 Integer 자료형으로 자동 변환해 줍니다.
	//   · Model model : 화면으로 넘길 바구니입니다. (목록 화면과 같습니다)
	public String detail(Model model, @PathVariable("id") Integer id) {
		// ─ 업무 담당에게 한 개 요청 ─
		//   · getQuestion(id) 는 번호로 질문 한 개를 찾아 돌려줍니다.
		//   · 질문이 없으면 Service 가 DataNotFoundException 을 던지고, 이 줄 아래는 실행되지 않습니다.
		//     (그래서 없는 번호로 들어오면 404 화면이 보입니다)
		Question question = this.questionService.getQuestion(id);
		// 찾은 질문을 "question" 이라는 이름표로 바구니에 담습니다.
		// question_detail.html 에서는 ${question.subject} 처럼 같은 이름으로 꺼냅니다.
		model.addAttribute("question", question);
		// "question_detail" → templates/question_detail.html 을 그리라는 신호입니다.
		return "question_detail";
	}
}
