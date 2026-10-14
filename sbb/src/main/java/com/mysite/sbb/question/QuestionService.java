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
// [Specification] 자바 코드로 검색 조건을 조립하는 JPA 도구입니다. (v3.13 추가, 아래 search 메서드에서 사용)
import org.springframework.data.jpa.domain.Specification;
// [@Service] 업무 처리 담당 클래스라는 표시 꼬리표입니다.
import org.springframework.stereotype.Service;

// 데이터가 없을 때 던지는 예외 클래스입니다.
import com.mysite.sbb.DataNotFoundException;
// 답변 내용까지 검색하기 위해 Answer 타입을 가져옵니다. (v3.13 추가)
import com.mysite.sbb.answer.Answer;
// [SiteUser] 질문 작성자 회원의 타입입니다. (v3.08 추가)
import com.mysite.sbb.user.SiteUser;

// ── 아래 6개는 "조건을 코드로 만드는" JPA Criteria 도구들입니다. (v3.13 추가) ──
// [CriteriaBuilder] like, or 같은 조건식을 만들어 주는 공장입니다.
import jakarta.persistence.criteria.CriteriaBuilder;
// [CriteriaQuery] 만들고 있는 쿼리 전체입니다. distinct(중복 제거) 설정에 씁니다.
import jakarta.persistence.criteria.CriteriaQuery;
// [Join] 두 표를 연결(조인)한 결과입니다.
import jakarta.persistence.criteria.Join;
// [JoinType] 조인 방식입니다. LEFT 는 연결 대상이 없어도 기준 쪽 줄을 남기는 방식입니다.
import jakarta.persistence.criteria.JoinType;
// [Predicate] 완성된 조건식(참/거짓을 판단하는 식)입니다.
import jakarta.persistence.criteria.Predicate;
// [Root] 쿼리의 기준 엔티티(여기서는 Question)입니다. SQL 의 from question q 에 해당합니다.
import jakarta.persistence.criteria.Root;
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

	// ══ search(검색어) : 자바 코드로 만든 검색 조건 (v3.13 추가) ══
	// 같은 검색을 두 가지 방법으로 만들어 볼 수 있게 남겨 둔 메서드입니다.
	//   ① 이 search() : Specification(자바 코드)으로 조건 조립
	//   ② QuestionRepository 의 @Query : JPQL 문장으로 직접 작성  ← 지금 getList 가 실제로 쓰는 방법
	// [@SuppressWarnings("unused")] "이 메서드는 지금 안 쓰인다"는 컴파일러 경고를 끄는 꼬리표입니다.
	// ①을 쓰고 싶으면 getList 에서 this.questionRepository.findAll(search(kw), pageable) 로 바꾸면 됩니다.
	@SuppressWarnings("unused")
	// 검색어를 받아 "질문 검색 조건"(Specification)을 만들어 돌려줍니다.
	private Specification<Question> search(String kw) {
		// new Specification<>() { ... } : 이름 없는 클래스(익명 클래스)로 조건 객체를 바로 만듭니다.
		return new Specification<>() {
			// 직렬화 버전 번호입니다. Specification 이 Serializable 이라서 붙이는 관례적인 값입니다. (동작에는 영향 없음)
			private static final long serialVersionUID = 1L;

			// 아래 toPredicate 는 Specification 규격에 정해진 메서드를 "다시 정의(재정의)"한 것입니다.
			@Override
			// toPredicate(기준, 쿼리, 조건공장) : 실제 검색 조건을 만드는 곳입니다.
			//   · q     : 기준 엔티티 Question (SQL 의 q)
			//   · query : 쿼리 전체
			//   · cb    : 조건 만드는 도구
			public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// 한 질문에 답변이 여러 개면 조인 결과에 같은 질문이 여러 줄 생깁니다. distinct 로 한 번만 나오게 합니다.
				query.distinct(true); // 중복을 제거
				// u1 : 질문 → 질문 작성자(SiteUser) 연결. 작성자가 없는 질문도 남기려고 LEFT 조인합니다.
				Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
				// a : 질문 → 답변 목록 연결. 답변이 없는 질문도 남깁니다.
				Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
				// u2 : 답변 → 답변 작성자 연결.
				Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
				// cb.or(조건1, 조건2, ...) : 다섯 조건 중 하나라도 맞으면 검색 결과에 넣습니다.
				// cb.like(값, "%검색어%") : 검색어가 "어디든 들어 있으면" 참입니다. % 는 "아무 글자 0개 이상"이라는 뜻입니다.
				//   1) 제목  2) 내용  3) 질문 작성자 아이디  4) 답변 내용  5) 답변 작성자 아이디
				return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
						cb.like(q.get("content"), "%" + kw + "%"), // 내용
						cb.like(u1.get("username"), "%" + kw + "%"), // 질문 작성자
						cb.like(a.get("content"), "%" + kw + "%"), // 답변 내용
						cb.like(u2.get("username"), "%" + kw + "%")); // 답변 작성자
			}
		};
	}

	// ─ getList(int page) : 요청한 페이지의 질문 목록 (v3.02 변경) ─
	// 이전에는 List<Question> 전체를 돌려줬지만, 이제는 Page 상자에 "한 페이지 분량"을 담아 돌려줍니다.
	//   · int page : 요청한 페이지 번호입니다. 0 이 첫 페이지입니다. (예: 2 이면 세 번째 페이지)
	//   · 반환형 Page<Question> : 질문 10개(또는 그보다 적은 수)와 페이지 정보를 함께 담은 상자입니다.
	// ─ 검색어 매개변수 추가 (v3.13 변경) ─
	// kw(검색어)가 추가되었습니다. 이제 "검색어가 들어간 질문들" 중에서 한 페이지를 돌려줍니다.
	public Page<Question> getList(int page, String kw) {
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
		// ─ 검색 쿼리 호출 (v3.13 변경) ─
		// 저장소의 findAllByKeyword(검색어, 요청서) 를 부릅니다. 직접 작성한 @Query 문장(QuestionRepository 참고)이 실행됩니다.
		return this.questionRepository.findAllByKeyword(kw, pageable);
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

	// ─ vote(질문, 회원) : 질문 추천 (v3.10 추가) ─
	public void vote(Question question, SiteUser siteUser) {
		// 추천인 모음에 회원을 추가합니다. Set 이라서 같은 회원은 한 번만 들어갑니다.
		question.getVoter().add(siteUser);
		// 저장하면 중간 표(question_voter)에 기록됩니다.
		this.questionRepository.save(question);
	}
}
