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

// [HttpStatus] 응답 상태 코드(200, 400, 404 ...) 이름표 모음입니다. BAD_REQUEST 는 400 입니다. (v3.09 추가)
import org.springframework.http.HttpStatus;
// [@PreAuthorize] 메서드 실행 전에 권한을 검사하는 꼬리표입니다. 조건이 거짓이면 메서드가 실행되지 않습니다. (v3.08 추가)
import org.springframework.security.access.prepost.PreAuthorize;
// [@Controller] 웹 요청 창구 클래스 꼬리표입니다.
import org.springframework.stereotype.Controller;
// [Model] 화면에 넘길 데이터 바구니입니다.
import org.springframework.ui.Model;
// [BindingResult] 입력 검사 결과 보관함입니다.
import org.springframework.validation.BindingResult;
// [@GetMapping] GET 요청을 메서드와 연결합니다. 수정 화면, 삭제 요청에 씁니다. (v3.09 추가)
import org.springframework.web.bind.annotation.GetMapping;
// [@PathVariable] 주소의 {id} 값을 꺼내는 꼬리표입니다.
import org.springframework.web.bind.annotation.PathVariable;
// [@PostMapping] POST 요청을 메서드와 연결합니다.
import org.springframework.web.bind.annotation.PostMapping;
// [@RequestMapping] 클래스 공통 주소 "/answer" 를 정합니다.
import org.springframework.web.bind.annotation.RequestMapping;
// [ResponseStatusException] 지정한 상태 코드로 요청을 거절하는 예외입니다. (v3.09 추가)
import org.springframework.web.server.ResponseStatusException;

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
		// ─ 저장한 답변 받기 (v3.11 변경) ─
		// create 가 저장한 답변을 돌려주므로 answer 변수에 받아 둡니다. 바로 아래 이동 주소에 답변 번호를 씁니다.
		Answer answer = this.answerService.create(question, answerForm.getContent(), siteUser);
		// 저장 후 상세 화면으로 이동합니다.
		// ─ 앵커(#)로 이동 (v3.11 변경) ─
		// 주소 끝의 #answer_7 같은 부분을 "앵커"라고 합니다. 브라우저는 페이지를 연 뒤 id="answer_7" 인 요소 위치로 스크롤합니다.
		//   · 첫 번째 %s : 질문 번호, 두 번째 %s : 답변 번호
		//   · 결과 예) redirect:/question/detail/3#answer_7
		// 답변이 많아도 방금 쓴 답변으로 바로 화면이 내려갑니다.
		return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
	}

	// ══ 답변 수정 화면 열기 (v3.09 추가) ══
	// [GET /answer/modify/{id}] 수정 버튼을 누르면 실행됩니다. 로그인한 사람만 열 수 있습니다.
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	// 매개변수: 빈 폼 객체, 답변 번호, 로그인 사용자 정보
	public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
		// 고칠 답변을 번호로 꺼냅니다.
		Answer answer = this.answerService.getAnswer(id);
		// ─ 본인 확인 ─
		// 답변 작성자 아이디와 로그인한 아이디가 다르면(!) 남의 글이므로 거절합니다.
		//   · answer.getAuthor().getUsername() : 작성자 아이디
		//   · principal.getName()             : 지금 로그인한 아이디
		//   · equals : 문자열 내용이 같은지 비교합니다. (== 는 객체가 같은지 비교하므로 문자열엔 쓰지 않습니다)
		if (!answer.getAuthor().getUsername().equals(principal.getName())) {
			// 400 오류와 함께 "수정권한이 없습니다." 메시지로 요청을 거절합니다.
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		// 기존 내용을 폼에 미리 채워 둡니다. 그래야 수정 화면 입력 칸에 원래 글이 보입니다.
		answerForm.setContent(answer.getContent());
		// 답변 수정 화면(answer_form.html)을 그립니다.
		return "answer_form";
	}

	// ══ 답변 수정 저장 (v3.09 추가) ══
	// [POST /answer/modify/{id}] 수정 화면에서 "저장하기"를 누르면 실행됩니다.
	// GET 과 이름(answerModify)이 같지만 매개변수가 달라서 자바에서 함께 쓸 수 있습니다. (메서드 오버로딩)
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	// @Valid 로 입력값(내용 비었는지)을 검사하고, 결과는 bindingResult 에 담깁니다.
	public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
			@PathVariable("id") Integer id, Principal principal) {
		// 내용이 비어 있으면 저장하지 않고 수정 화면을 다시 보여 줍니다.
		if (bindingResult.hasErrors()) {
			return "answer_form";
		}
		// 고칠 답변을 꺼냅니다.
		Answer answer = this.answerService.getAnswer(id);
		// 저장할 때도 본인인지 다시 확인합니다. (주소를 직접 입력해 보내는 경우까지 막기 위해)
		if (!answer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		// 서비스에 새 내용으로 수정을 맡깁니다. (UPDATE)
		this.answerService.modify(answer, answerForm.getContent());
		// 수정이 끝나면 그 답변이 달린 질문의 상세 화면으로 이동합니다.
		// answer.getQuestion().getId() : 답변 → 소속 질문 → 질문 번호 순서로 꺼냅니다.
		// 수정한 답변 위치(#answer_번호)로 이동합니다. (v3.11 변경)
		return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
	}

	// ══ 답변 삭제 (v3.09 추가) ══
	// [GET /answer/delete/{id}] 삭제 버튼을 누르고 확인 창에서 "확인"을 고르면 실행됩니다.
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	// 매개변수: 로그인 사용자 정보, 지울 답변 번호
	public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
		// 지울 답변을 꺼냅니다.
		Answer answer = this.answerService.getAnswer(id);
		// 본인 글이 아니면 지울 수 없습니다.
		if (!answer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}
		// 서비스에 삭제를 맡깁니다. (DELETE)
		this.answerService.delete(answer);
		// 삭제 후 질문 상세 화면으로 돌아갑니다. (답변 객체는 메모리에 남아 있어서 질문 번호를 꺼낼 수 있습니다)
		return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
	}

	// ══ 답변 추천 (v3.10 추가) ══
	// [GET /answer/vote/{id}] 추천 버튼을 누르고 확인하면 실행됩니다. 로그인한 사람만 추천할 수 있습니다.
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	// 매개변수: 로그인 사용자 정보, 추천할 답변 번호
	public String answerVote(Principal principal, @PathVariable("id") Integer id) {
		// 추천할 답변을 꺼냅니다.
		Answer answer = this.answerService.getAnswer(id);
		// 추천하는 사람(지금 로그인한 회원)을 꺼냅니다.
		SiteUser siteUser = this.userService.getUser(principal.getName());
		// 서비스에 추천 기록을 맡깁니다.
		this.answerService.vote(answer, siteUser);
		// 추천 후 질문 상세 화면으로 돌아갑니다. 추천 수가 1 늘어난 것이 보입니다.
		// 추천한 답변 위치(#answer_번호)로 이동합니다. (v3.11 변경)
		return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
	}
}
