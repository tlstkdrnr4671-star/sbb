// ══════════════════════════════════════════════════════════════════════
// [AnswerController = "답변" 관련 요청을 받는 창구] (v2.12 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 이 클래스는 "질문 상세 화면의 답변 입력 칸"에서 보낸 글을 받아 저장하는 창구입니다.
//
// ── 답변 등록의 전체 흐름 ────────────────────────────────────────────────
//   [1] 질문 상세 화면(question_detail.html)의 입력 칸에 답변을 쓰고 "답변등록" 버튼을 누름
//   [2] 브라우저가 POST http://localhost:8070/answer/create/3 로 form 내용을 보냄
//        · 보내는 내용: content=답변글 (입력 칸의 name 이 content 이기 때문)
//   [3] 스프링이 주소를 보고 createAnswer() 를 찾아 실행 (POST 방식이므로 @PostMapping)
//   [4] @PathVariable 로 질문 번호 3 을, @RequestParam 으로 답변 글을 꺼냄
//   [5] 질문 번호로 Question 객체를 찾음 (QuestionService)
//   [6] 답변 저장을 AnswerService 에 맡김 → INSERT
//   [7] redirect: 응답 → 브라우저가 /question/detail/3 을 다시 열어 새 답변이 보임
//
// ── GET 과 POST 의 차이 (처음 등장) ──────────────────────────────────────
//   · GET  : 주소창에 입력하거나 링크를 눌러 "보여 달라"고 요청합니다. (목록, 상세 화면)
//   · POST : form 의 내용을 묶어서 "저장해 달라"고 보냅니다. (답변 등록, 나중에 질문 등록)
//   · 그래서 답변 저장은 @GetMapping 이 아니라 @PostMapping 을 씁니다.
//
// ── 주소 조립 ──────────────────────────────────────────────────────────
//   클래스 주소 "/answer" + 메서드 주소 "/create/{id}" = /answer/create/{id}
// ══════════════════════════════════════════════════════════════════════
// 답변 관련 클래스가 모인 폴더 주소입니다. (answer 폴더) 같은 폴더의 AnswerService, Answer 등을 import 없이 씁니다.
package com.mysite.sbb.answer;

// 질문 객체의 타입입니다. 다른 폴더(question)에 있어서 import 로 가져옵니다.
import com.mysite.sbb.question.Question;
// 질문을 찾아 주는 업무 담당입니다. 답변은 어느 질문에 달릴지 먼저 알아야 하므로 필요합니다.
import com.mysite.sbb.question.QuestionService;
// [Lombok] 생성자 자동 생성 꼬리표를 가져옵니다.
import lombok.RequiredArgsConstructor;
// [@Controller] 웹 요청 창구 클래스라는 표시 꼬리표입니다.
import org.springframework.stereotype.Controller;
// [Model] 화면으로 넘길 데이터 바구니입니다. (이 단계에서는 실제로 쓰지 않고 매개변수로만 받습니다)
import org.springframework.ui.Model;
// [@PathVariable] 주소의 {id} 자리 값(질문 번호)을 메서드 매개변수로 꺼내는 꼬리표입니다.
import org.springframework.web.bind.annotation.PathVariable;
// [@PostMapping] POST 방식 요청의 주소와 메서드를 연결하는 꼬리표입니다. (form 전송은 보통 POST 입니다)
import org.springframework.web.bind.annotation.PostMapping;
// [@RequestMapping] 클래스 공통 주소 "/answer" 를 정하는 꼬리표입니다.
import org.springframework.web.bind.annotation.RequestMapping;
// [@RequestParam] form 입력 칸의 값(name 속성)을 메서드 매개변수로 꺼내는 꼬리표입니다.
import org.springframework.web.bind.annotation.RequestParam;

// ─ 클래스 공통 주소 ─
// 이 클래스의 모든 주소 앞에 "/answer" 를 붙입니다. 아래 @PostMapping("/create/{id}") 는 /answer/create/{id} 가 됩니다.
@RequestMapping("/answer")
// ─ 생성자 자동 생성 ─
// final 필드 두 개(questionService, answerService)를 받는 생성자를 Lombok 이 만듭니다. 스프링이 두 업무 담당을 넣어 줍니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 이 클래스가 웹 요청 창구임을 스프링에게 알립니다. 스프링이 시작할 때 객체를 만들어 보관합니다.
@Controller
// 답변 관련 요청을 담당하는 창구 클래스입니다.
public class AnswerController {

	// 질문을 찾는 업무 담당입니다. (질문 번호로 질문 객체를 얻을 때 씁니다)
	private final QuestionService questionService;
	// 답변을 저장하는 업무 담당입니다. 창구는 직접 DB 를 만지지 않고 이 담당에게 맡깁니다.
	private final AnswerService answerService;

	// ─ 주소 연결 ─
	// [POST /answer/create/{id}] 답변 등록 form 을 보내면 아래 createAnswer() 가 실행됩니다.
	//   · {id} 는 "어느 질문에 답변을 달지"를 나타내는 질문 번호입니다. (예: /answer/create/3 → 3번 질문)
	@PostMapping("/create/{id}")
	// ─ createAnswer() 메서드 ─
	// 답변을 받아 저장하고, 질문 상세 화면으로 돌려보내는 메서드입니다.
	//   · @PathVariable("id") Integer id : 주소의 질문 번호를 정수로 꺼냅니다. (예: 3)
	public String createAnswer(Model model, @PathVariable("id") Integer id,
			// ─ 답변 글 꺼내기 ─
			//   · form 의 <textarea name="content"> 에 쓴 글이 여기로 옵니다.
			//   · value = "content" 는 form 입력 칸의 name 과 같아야 연결됩니다. 이름이 다르면 값이 null 이 됩니다.
			//   · 매개변수 String content 에 답변 글 전체가 문자열로 들어갑니다.
			@RequestParam(value = "content") String content) {
		// ─ 질문 찾기 ─
		// 번호로 질문 객체를 찾습니다. 질문이 없으면 DataNotFoundException 이 던져지고 저장은 일어나지 않습니다.
		Question question = this.questionService.getQuestion(id);
		// ─ 저장 맡기기 ─
		// 찾은 질문과 답변 글을 AnswerService.create 에 넘깁니다. 실제 INSERT 는 서비스에서 일어납니다.
		this.answerService.create(question, content);
		// ─ 저장 후 이동 ─
		//   · String.format : 글자 틀 "redirect:/question/detail/%s" 의 %s 자리에 id 값을 끼워 넣습니다.
		//   · 결과 예) id 가 3 이면 "redirect:/question/detail/3"
		//   · "redirect:" 는 화면을 그리지 않고 브라우저에게 "그 주소를 새로 열라"고 알려 줍니다.
		//     그래서 저장 직후 새로고침을 눌러도 같은 답변이 두 번 저장되지 않습니다. (새로고침이 저장 요청을 다시 보내지 않게 됨)
		return String.format("redirect:/question/detail/%s", id);
	}
}
