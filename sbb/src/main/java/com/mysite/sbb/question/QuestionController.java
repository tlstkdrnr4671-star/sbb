// ══════════════════════════════════════════════════════════════════════
// [QuestionController = "질문" 관련 요청을 받는 창구] (v2.07 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 이 클래스는 "DB → 자바 → 화면"으로 데이터가 흘러가는 첫 번째 완성 예제입니다.
//
// ── 질문 목록 화면을 열 때의 전체 흐름 ──────────────────────────────────
//   [1] 브라우저가 http://localhost:8070/question/list 로 GET 요청을 보냅니다.
//   [2] 스프링(DispatcherServlet)이 주소를 보고 @GetMapping("/question/list") 메서드, 즉 list() 를 찾아 실행합니다.
//   [3] list() 가 questionRepository.findAll() 을 호출합니다.
//   [4] JPA 가 SQL 을 만들어 H2 DB 에 보냅니다. (아래 "findAll 이 보내는 SQL" 참고)
//   [5] DB 의 줄들이 Question 객체 여러 개로 바뀌어 돌아옵니다. (List<Question>)
//   [6] list() 가 model.addAttribute("questionList", ...) 로 화면 바구니에 담습니다.
//   [7] "question_list" 라는 화면 이름을 돌려줍니다.
//   [8] Thymeleaf 가 templates/question_list.html 을 찾아, th:each 로 질문 개수만큼 <tr> 을 만듭니다.
//   [9] 완성된 HTML 이 브라우저에 도착해서 질문 목록 표가 보입니다.
//
// ── 등장하는 역할 정리 ──────────────────────────────────────────────────
//   · 컨트롤러(Controller) : 요청을 받아 "무슨 일을 할지" 정하는 창구입니다. (이 클래스)
//   · 리포지토리(Repository): DB 와 대화하는 담당입니다. (QuestionRepository)
//   · Model                : 컨트롤러가 화면으로 넘길 데이터를 담는 바구니입니다.
//   · 템플릿(Template)      : 데이터를 끼워 넣을 HTML 뼈대입니다. (question_list.html)
//
// ── 이 단계의 한계 (다음 단계에서 개선) ─────────────────────────────────
//   · 모든 질문을 한꺼번에 가져옵니다. 질문이 많아지면 느려지므로, 뒤 단계에서 "페이지 나누기"를 씁니다.
//   · 컨트롤러가 리포지토리를 직접 부릅니다. 뒤 단계에서 서비스(Service) 계층을 가운데 둡니다.
// ══════════════════════════════════════════════════════════════════════
// ─ 패키지(package) ─
// 질문 관련 클래스를 모아 두는 폴더 주소입니다. 같은 폴더의 Question, QuestionRepository 는 import 없이 바로 쓸 수 있습니다.
package com.mysite.sbb.question;

// [java.util.List] 여러 개의 질문을 순서대로 담는 목록 자료형입니다. findAll() 의 결과를 받을 때 씁니다.
import java.util.List;

// [@Controller 꼬리표를 가져오기] 웹 요청을 처리하는 창구 클래스라는 표시를 붙일 때 씁니다. (HelloController 와 같은 꼬리표입니다)
import org.springframework.stereotype.Controller;
// [Model] 화면으로 넘길 데이터를 담는 바구니 타입입니다. 메서드 매개변수로 받으면 스프링이 빈 바구니를 넣어 줍니다.
import org.springframework.ui.Model;
// [@GetMapping] GET 방식 요청의 주소와 메서드를 연결하는 꼬리표입니다.
import org.springframework.web.bind.annotation.GetMapping;

// [Lombok] @RequiredArgsConstructor 를 쓰기 위해 가져옵니다. (생성자 코드를 자동으로 만들어 줍니다)
import lombok.RequiredArgsConstructor;

// ─ 생성자 자동 생성 ─
// final 필드(questionRepository)를 받는 생성자를 Lombok 이 만들어 줍니다.
// 스프링이 이 클래스의 객체를 만들 때 이 생성자를 부르면서 QuestionRepository 객체를 넣어 줍니다. (생성자 주입, DI)
@RequiredArgsConstructor
// ─ 웹 창구 표시 ─
// 이 클래스가 웹 요청을 처리하는 창구임을 스프링에게 알립니다.
// 스프링은 시작할 때 이 클래스의 객체를 한 개 만들어 보관하고(Bean 등록), 요청이 올 때마다 그 객체의 메서드를 실행합니다.
@Controller
// 질문 관련 요청을 담당하는 창구 클래스입니다. public 이어야 스프링이 접근해서 쓸 수 있습니다.
public class QuestionController {

	// ─ 저장소(Repository) 필드 ─
	// 질문 표(question)와 대화하는 저장소입니다.
	//   · final : 한 번 넣어 준 뒤에는 바꾸지 않습니다. 실수로 다른 값을 넣는 것을 막습니다.
	//   · QuestionRepository 는 인터페이스(규격서)입니다. 직접 new 할 수 없고, 스프링이 구현체를 만들어 넣어 줍니다.
	private final QuestionRepository questionRepository;

	// ─ 주소 연결 ─
	// [GET /question/list] 주소로 GET 요청이 오면 아래 list() 메서드가 실행됩니다.
	//   · "/question/list" 를 전체 주소로 적었습니다. (클래스 공통 주소 @RequestMapping 은 다음 단계 이후에 씁니다)
	//   · 주소의 / 로 나뉜 부분은 폴더처럼 생각하면 편합니다. question 폴더 안의 list 페이지라는 뜻입니다.
	@GetMapping("/question/list")
	// ─ list() 메서드 ─
	// 질문 목록 화면을 준비하는 메서드입니다.
	//   · 매개변수 Model model : 화면으로 넘길 데이터 바구니입니다. 스프링이 자동으로 넣어 줍니다. 우리는 값을 담기만 합니다.
	//   · 반환형 String       : 돌려주는 값은 글자가 아니라 "화면 이름"입니다. (@ResponseBody 가 없기 때문)
	public String list(Model model) {
		// ─ findAll() : 전체 조회 ─
		//   · JpaRepository 가 기본으로 제공하는 "전체 가져오기" 메서드입니다. 우리가 따로 만든 적이 없습니다.
		//   · 실행되는 SQL 은 대략 아래와 같습니다. (question 표의 모든 줄을 가져옵니다)
		//       select q1_0.id, q1_0.content, q1_0.create_date, q1_0.subject from question q1_0
		//   · 결과는 Question 객체 여러 개가 든 List 입니다. 질문이 하나도 없으면 빈 목록이며, 이것은 오류가 아닙니다.
		//   · this. 는 "이 객체(QuestionController)가 가진 questionRepository 필드"를 가리킵니다.
		List<Question> questionList = this.questionRepository.findAll();
		// ─ 바구니에 담기 ─
		//   · "questionList" 는 이름표입니다. 화면 HTML 에서는 이 이름으로 목록을 꺼냅니다.
		//     예) <tr th:each="question : ${questionList}">
		//   · 이름표(문자열)와 실제 데이터(questionList)를 짝지어 바구니에 넣는 명령입니다.
		model.addAttribute("questionList", questionList);
		// ─ 화면 이름 돌려주기 ─
		// "question_list" 는 templates/question_list.html 파일을 뜻합니다.
		//   · 스프링이 앞의 폴더(templates/)와 뒤의 .html 을 자동으로 붙여 줍니다.
		//   · 이 값을 받으면 Thymeleaf 가 그 파일을 읽어서 데이터를 채운 뒤 브라우저로 보냅니다.
		return "question_list";
	}
}
