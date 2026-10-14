// ══════════════════════════════════════════════════════════════════════
// [QuestionController = 질문 관련 요청 창구] (v3.08 갱신: 로그인 검사, 작성자 기록)
// ══════════════════════════════════════════════════════════════════════
//
// 질문 등록(POST)은 이제 로그인한 회원만 할 수 있고, 저장할 때 작성자 정보도 함께 기록합니다.
// 작성자 확인에 쓰는 Principal 과 UserService 가 새로 추가되었습니다.
// ══════════════════════════════════════════════════════════════════════
// 질문 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.question;

// [Principal] 지금 로그인한 사용자 정보입니다. (v3.08 추가)
import java.security.Principal;

// [Page] 한 페이지 분량의 질문 상자입니다.
import org.springframework.data.domain.Page;
// [HttpStatus] 응답 상태 코드 이름표 모음입니다. BAD_REQUEST = 400 (v3.09 추가)
import org.springframework.http.HttpStatus;
// [@PreAuthorize] 메서드 실행 전 권한을 검사하는 꼬리표입니다. (v3.08 추가)
import org.springframework.security.access.prepost.PreAuthorize;
// [@Controller] 웹 요청 창구 꼬리표입니다.
import org.springframework.stereotype.Controller;
// [Model] 화면에 넘길 데이터 바구니입니다.
import org.springframework.ui.Model;
// [BindingResult] 검사 결과 보관함입니다.
import org.springframework.validation.BindingResult;
// [@GetMapping] GET 요청을 메서드와 연결합니다.
import org.springframework.web.bind.annotation.GetMapping;
// [@PathVariable] 주소의 {id} 값을 꺼냅니다.
import org.springframework.web.bind.annotation.PathVariable;
// [@PostMapping] POST 요청을 메서드와 연결합니다.
import org.springframework.web.bind.annotation.PostMapping;
// [@RequestMapping] 클래스 공통 주소 "/question" 을 정합니다.
import org.springframework.web.bind.annotation.RequestMapping;
// [@RequestParam] ?page= 같은 주소 값을 받는 꼬리표입니다.
import org.springframework.web.bind.annotation.RequestParam;
// [ResponseStatusException] 지정한 상태 코드로 요청을 거절하는 예외입니다. (v3.09 추가)
import org.springframework.web.server.ResponseStatusException;

// 상세 화면의 답변 입력 폼 객체 타입입니다.
import com.mysite.sbb.answer.AnswerForm;
// 작성자 회원 객체 타입입니다. (v3.08 추가)
import com.mysite.sbb.user.SiteUser;
// 지금 로그인한 회원을 찾는 업무 담당입니다. (v3.08 추가)
import com.mysite.sbb.user.UserService;

// [@Valid] 입력 검사 꼬리표입니다.
import jakarta.validation.Valid;
// [Lombok] 생성자 자동 생성 꼬리표를 가져옵니다.
import lombok.RequiredArgsConstructor;

// 이 클래스의 모든 주소 앞에 /question 을 붙입니다.
@RequestMapping("/question")
// final 필드들을 받는 생성자를 자동으로 만듭니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 웹 요청 창구임을 스프링에게 알립니다.
@Controller
// 질문 관련 요청을 담당하는 창구 클래스입니다.
public class QuestionController {

	// 질문 업무 담당입니다.
	private final QuestionService questionService;
	// 회원 업무 담당입니다. 로그인한 회원을 찾을 때 씁니다. (v3.08 추가)
	private final UserService userService;

	// [GET /question/list] 질문 목록 화면입니다. (v3.02 부터 ?page= 로 페이지를 나눕니다)
	@GetMapping("/list")
	// 목록 화면 요청을 처리합니다. (설명은 v3.02 와 같습니다)
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		// 서비스에게 그 페이지의 질문들을 요청합니다.
		Page<Question> paging = this.questionService.getList(page);
		// 페이지 상자를 "paging" 이름표로 담습니다.
		model.addAttribute("paging", paging);
		// 목록 화면(question_list.html)을 그립니다.
		return "question_list";
	}

	// [GET /question/detail/{id}] 질문 상세 화면입니다.
	@GetMapping(value = "/detail/{id}")
	// 상세 화면 준비. (설명은 v2.10, v2.16 과 같습니다)
	public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
		// 질문 번호로 질문 한 개를 찾습니다.
		Question question = this.questionService.getQuestion(id);
		// 질문을 "question" 이름표로 담습니다.
		model.addAttribute("question", question);
		// 상세 화면을 그립니다.
		return "question_detail";
	}

	// ─ 로그인 필수 (v3.08 추가) ─
	// 로그인하지 않으면 질문 등록 화면 자체를 열 수 없습니다. (GET 에도 같은 검사를 붙였습니다)
	@PreAuthorize("isAuthenticated()")
	// [GET /question/create] 질문 등록 화면을 보여 줍니다.
	@GetMapping("/create")
	// 빈 QuestionForm 객체를 받아 폼과 연결합니다.
	public String questionCreate(QuestionForm questionForm) {
		// 질문 등록 화면을 그립니다.
		return "question_form";
	}

	// ─ 로그인 필수 (v3.08 추가) ─
	// 질문을 저장하는 POST 에도 같은 검사를 붙였습니다. 로그인하지 않은 상태에서는 저장이 실행되지 않습니다.
	@PreAuthorize("isAuthenticated()")
	// [POST /question/create] 저장하기 버튼을 누르면 실행됩니다.
	@PostMapping("/create")
	// 매개변수 Principal principal 이 추가되었습니다. 지금 로그인한 사용자 정보입니다. (v3.08 추가)
	public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
		// 입력 오류가 있으면 저장하지 않고 입력 화면을 다시 보여 줍니다.
		if (bindingResult.hasErrors()) {
			// 같은 입력 화면을 다시 그립니다.
			return "question_form";
		}
		// ─ 작성자 찾기 (v3.08 추가) ─
		// 로그인한 아이디로 회원 객체를 찾습니다. 이 객체가 질문의 작성자가 됩니다.
		SiteUser siteUser = this.userService.getUser(principal.getName());
		// 제목, 내용, 작성자를 서비스에 맡겨 저장합니다. (v3.08 변경: 작성자 추가)
		this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
		// 저장 후 목록 화면으로 이동합니다.
		return "redirect:/question/list";
	}

	// ══ 질문 수정 화면 열기 (v3.09 추가) ══
	// [GET /question/modify/{id}] 수정 버튼을 누르면 실행됩니다.
	// 질문 등록 화면(question_form.html)을 그대로 재사용하고, 기존 제목과 내용을 미리 채워 보여 줍니다.
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	// 매개변수: 빈 폼 객체, 질문 번호, 로그인 사용자 정보
	public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
		// 고칠 질문을 번호로 꺼냅니다.
		Question question = this.questionService.getQuestion(id);
		// ─ 본인 확인 ─
		// 질문 작성자 아이디와 로그인한 아이디가 다르면 남의 글이므로 거절합니다. (equals 로 문자열 비교)
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			// 400 오류로 거절합니다.
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		// 기존 제목을 폼에 채웁니다.
		questionForm.setSubject(question.getSubject());
		// 기존 내용을 폼에 채웁니다.
		questionForm.setContent(question.getContent());
		// 질문 입력 화면을 그립니다. (등록 화면과 같은 파일을 수정용으로도 씁니다)
		return "question_form";
	}

	// ══ 질문 수정 저장 (v3.09 추가) ══
	// [POST /question/modify/{id}] 수정 화면에서 "저장하기"를 누르면 실행됩니다.
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	// 입력값 검사(@Valid) 결과, 로그인 정보, 질문 번호를 받습니다.
	public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal,
			@PathVariable("id") Integer id) {
		// 제목이나 내용이 잘못되었으면 저장하지 않고 화면을 다시 보여 줍니다.
		if (bindingResult.hasErrors()) {
			return "question_form";
		}
		// 고칠 질문을 꺼냅니다.
		Question question = this.questionService.getQuestion(id);
		// 저장할 때도 본인 확인을 다시 합니다.
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		// 새 제목, 새 내용으로 수정을 맡깁니다. (UPDATE)
		this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
		// 수정이 끝나면 그 질문의 상세 화면으로 이동합니다.
		return String.format("redirect:/question/detail/%s", id);
	}

	// ══ 질문 삭제 (v3.09 추가) ══
	// [GET /question/delete/{id}] 삭제 확인 창에서 "확인"을 누르면 실행됩니다.
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	// 매개변수: 로그인 사용자 정보, 지울 질문 번호
	public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
		// 지울 질문을 꺼냅니다.
		Question question = this.questionService.getQuestion(id);
		// 본인 글이 아니면 지울 수 없습니다.
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}
		// 삭제를 맡깁니다. 달린 답변도 함께 지워집니다. (cascade)
		this.questionService.delete(question);
		// 삭제 후 첫 화면("/")으로 이동합니다. MainController 가 다시 질문 목록으로 보내 줍니다.
		return "redirect:/";
	}

	// ══ 질문 추천 (v3.10 추가) ══
	// [GET /question/vote/{id}] 추천 버튼을 누르고 확인하면 실행됩니다.
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	// 매개변수: 로그인 사용자 정보, 추천할 질문 번호
	public String questionVote(Principal principal, @PathVariable("id") Integer id) {
		// 추천할 질문을 꺼냅니다.
		Question question = this.questionService.getQuestion(id);
		// 추천하는 회원을 꺼냅니다.
		SiteUser siteUser = this.userService.getUser(principal.getName());
		// 추천을 기록합니다.
		this.questionService.vote(question, siteUser);
		// 추천 후 같은 질문 상세 화면으로 돌아갑니다.
		return String.format("redirect:/question/detail/%s", id);
	}
}
