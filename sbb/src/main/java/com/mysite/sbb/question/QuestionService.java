// ══════════════════════════════════════════════════════════════════════
// [QuestionService = 질문 업무 담당] (v3.02 갱신: 페이지 단위 목록)
// ══════════════════════════════════════════════════════════════════════
//
// 이번 단계에서 목록 조회가 "전체 한꺼번에"에서 "한 페이지씩"으로 바뀌었습니다.
//   · 질문이 1000개면 1000개를 모두 화면에 보내면 느립니다.
//   · 그래서 최신 질문 10개씩 나누어 보내고, 페이지 번호로 다음 묶음을 요청합니다.
//   · 정렬 규칙(최신순)과 페이지 크기(10개)도 이 업무 담당이 정합니다.
// ══════════════════════════════════════════════════════════════════════
// 질문 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.question;

// [LocalDateTime] 지금 이 순간의 날짜와 시각입니다. 질문 작성 시각을 저장할 때 씁니다.
import java.time.LocalDateTime;
// [ArrayList] 정렬 조건(Sort.Order)을 여러 개 담을 수 있는 목록 자료형입니다.
import java.util.ArrayList;
// [List] 목록 자료형입니다.
import java.util.List;
// [Optional] 값이 있을 수도 없을 수도 있는 상자입니다. findById 의 결과 타입입니다.
import java.util.Optional;

// [Page] 한 페이지 분량의 결과 상자입니다. getList 의 반환형입니다. (v3.02 추가)
import org.springframework.data.domain.Page;
// [PageRequest] 페이지 번호, 페이지 크기, 정렬을 묶어서 요청서(Pageable)를 만드는 도구입니다. (v3.02 추가)
import org.springframework.data.domain.PageRequest;
// [Pageable] 요청서의 자료형입니다. (v3.02 추가)
import org.springframework.data.domain.Pageable;
// [Sort] 정렬 규칙을 담는 자료형입니다. (v3.02 추가)
import org.springframework.data.domain.Sort;
// [@Service] 업무 처리 담당 클래스라는 표시 꼬리표입니다.
import org.springframework.stereotype.Service;

// 데이터가 없을 때 던지는 예외 클래스입니다.
import com.mysite.sbb.DataNotFoundException;
// [SiteUser] 질문 작성자 회원의 타입입니다. (v3.08 추가)
import com.mysite.sbb.user.SiteUser;

// [Lombok] 생성자 자동 생성 꼬리표를 가져옵니다.
import lombok.RequiredArgsConstructor;

// final 필드(questionRepository)를 받는 생성자를 자동으로 만듭니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 업무 부품으로 스프링에 등록합니다.
@Service
// 질문 업무를 처리하는 클래스입니다.
public class QuestionService {

	// 질문 저장소입니다. DB 와 대화하는 담당에게 부탁합니다.
	private final QuestionRepository questionRepository;

	// ─ getList(int page) : 요청한 페이지의 질문 목록 (v3.02 변경) ─
	// 이전에는 List<Question> 전체를 돌려줬지만, 이제는 Page 상자에 "한 페이지 분량"을 담아 돌려줍니다.
	//   · int page : 요청한 페이지 번호입니다. 0 이 첫 페이지입니다. (예: 2 이면 세 번째 페이지)
	//   · 반환형 Page<Question> : 질문 10개(또는 그보다 적은 수)와 페이지 정보를 함께 담은 상자입니다.
	public Page<Question> getList(int page) {
		// ─ 정렬 조건 목록 만들기 ─
		// 정렬 규칙을 여러 개 담을 수 있도록 목록을 준비합니다. <Sort.Order> 는 "정렬 한 개"를 뜻하는 제네릭 표기입니다.
		List<Sort.Order> sorts = new ArrayList<>();
		// ─ 정렬 규칙 추가 ─
		// Sort.Order.desc("createDate") : 작성 시각(createDate 필드) 기준 내림차순(큰 값 먼저) 정렬입니다.
		//   · 시각이 큰 값 = 나중에 쓴 글 → 최신 글이 목록의 맨 위에 옵니다.
		//   · desc = descending(내림차순), asc = ascending(오름차순)입니다.
		sorts.add(Sort.Order.desc("createDate"));
		// ─ 요청서 만들기 ─
		// PageRequest.of(페이지 번호, 페이지 크기, 정렬) 으로 "이 페이지를, 10개씩, 이 정렬로" 라는 요청서를 만듭니다.
		//   · 예) page=0 이면 최신 10개, page=1 이면 그 다음 10개입니다.
		//   · SQL 로는 대략 order by create_date desc limit 10 offset (page × 10) 입니다.
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		// ─ 저장소에 요청서 전달 ─
		// 요청서를 저장소에 넘기면 해당 페이지의 질문과 페이지 정보가 Page 상자에 담겨 돌아옵니다.
		// 이 메서드는 그 결과를 그대로 호출한 곳(QuestionController)에 전달합니다.
		return this.questionRepository.findAll(pageable);
	}

	// getQuestion(id) : 번호로 질문 한 개를 찾습니다. (v2.10 부터 있던 메서드, 설명 유지)
	public Question getQuestion(Integer id) {
		// findById : 기본키가 같은 질문을 찾습니다. 결과는 Optional 상자입니다.
		Optional<Question> question = this.questionRepository.findById(id);
		// isPresent() : 상자 안에 질문이 있으면 true 입니다.
		if (question.isPresent()) {
			// get() : 상자에서 질문을 꺼내 돌려줍니다.
			return question.get();
		} else {
			// 질문이 없으면 "데이터 없음" 예외를 던집니다.
			throw new DataNotFoundException("question not found");
		}
	}

	// create(제목, 내용) : 질문을 저장합니다. (v2.16 부터 있던 메서드, 설명 유지)
	// ─ create(제목, 내용, 작성자) (v3.08 변경) ─
	// 작성자(user) 매개변수가 추가되었습니다. 질문을 누가 썼는지 함께 저장합니다.
	public void create(String subject, String content, SiteUser user) {
		// 빈 질문 객체를 만듭니다.
		Question q = new Question();
		// 제목을 채웁니다.
		q.setSubject(subject);
		// 내용을 채웁니다.
		q.setContent(content);
		// 작성 시각을 지금으로 채웁니다.
		q.setCreateDate(LocalDateTime.now());
		// ─ 작성자 연결 (v3.08 추가) ─
		// 질문에 작성자 회원을 연결합니다. 저장되면 question 표의 author_id 열에 회원 번호가 들어갑니다.
		q.setAuthor(user);
		// save : INSERT 로 question 표에 한 줄을 추가합니다.
		this.questionRepository.save(q);
	}

	// ─ modify(질문, 새 제목, 새 내용) : 질문 수정 (v3.09 추가) ─
	public void modify(Question question, String subject, String content) {
		// 제목을 바꿉니다.
		question.setSubject(subject);
		// 내용을 바꿉니다.
		question.setContent(content);
		// 수정 시각을 지금으로 기록합니다.
		question.setModifyDate(LocalDateTime.now());
		// 기존 질문(id 있음)을 save 하므로 UPDATE 가 실행됩니다.
		this.questionRepository.save(question);
	}

	// ─ delete(질문) : 질문 삭제 (v3.09 추가) ─
	public void delete(Question question) {
		// 질문을 지웁니다. Question.answerList 의 cascade = REMOVE 설정 덕분에 달린 답변들도 함께 지워집니다.
		this.questionRepository.delete(question);
	}
}
