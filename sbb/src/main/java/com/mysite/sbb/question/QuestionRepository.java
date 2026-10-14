// ══════════════════════════════════════════════════════════════════════
// [QuestionRepository = 질문 표 전용 저장소 창구] (v3.02 갱신: 페이지 단위 조회 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 이 인터페이스에는 구현 코드가 없습니다. 메서드 "이름"만 적으면 스프링 데이터 JPA 가
// 이름을 읽고 SQL 을 자동으로 만들어 실행해 줍니다. (구현은 스프링이 런타임에 만듭니다)
//
// 메서드 종류
//   · 기본 제공 메서드 : findById, findAll, save, delete ... (JpaRepository 에서 물려받음)
//   · 쿼리 메서드      : findBySubject 처럼 이름으로 검색 조건을 적은 메서드
//   · 페이지 메서드    : findAll(Pageable) → 한 페이지 분량만 조회 (v3.02 추가)
// ══════════════════════════════════════════════════════════════════════
// 질문 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.question;

// [List] 여러 개의 결과를 담는 목록 자료형입니다. findBySubjectLike 의 반환형으로 씁니다.
import java.util.List;

// [Page] "한 페이지 분량의 결과 + 전체 페이지 정보"를 담는 상자입니다. (v3.02 추가)
//   Page 는 질문 목록뿐 아니라 "전체 개수, 현재 페이지 번호, 다음 페이지가 있는지" 같은 정보도 함께 가집니다.
import org.springframework.data.domain.Page;
// [Pageable] "몇 페이지를, 한 페이지에 몇 개씩, 어떤 순서로" 가져올지 정하는 요청서입니다. (v3.02 추가)
import org.springframework.data.domain.Pageable;
// [JpaRepository] 기본 저장소 규격입니다. (save, findById, findAll 등을 물려받습니다)
import org.springframework.data.jpa.repository.JpaRepository;

// <Question, Integer> : 다룰 엔티티는 Question, 기본키 자료형은 Integer 입니다.
public interface QuestionRepository extends JpaRepository<Question, Integer> {
	// ─ 쿼리 메서드 1 ─
	// 메서드 이름이 검색 명령서입니다. find + By + 필드이름 → "그 필드 값이 같은 것을 찾아라"
	//   · 실행 SQL 대략: select ... from question q1_0 where q1_0.subject=?
	//   · 같은 제목이 여러 개면 결과가 여러 줄이라서 에러가 날 수 있습니다. (Question 한 개로 받기 때문)
	Question findBySubject(String subject);

	// ─ 쿼리 메서드 2 ─
	// And 로 조건을 이어 붙입니다. → where subject=? and content=?
	// 매개변수 순서는 메서드 이름의 조건 순서(Subject, Content)와 맞춥니다.
	Question findBySubjectAndContent(String subject, String content);

	// ─ 쿼리 메서드 3 ─
	// Like 는 "비슷한 글자 찾기"입니다. → where subject like ?
	// 값에 % 를 붙이면 "포함" 검색입니다. 예) "%스프링%" 은 "스프링"이 들어간 제목을 모두 찾습니다.
	// 여러 줄이 나올 수 있어서 반환형은 List 입니다.
	List<Question> findBySubjectLike(String subject);

	// ─ 페이지 조회 (v3.02 추가) ─
	// 전체 질문을 "요청서(pageable)가 말한 한 페이지 분량만" 가져옵니다.
	//   · 요청서 안에는 페이지 번호, 한 페이지 개수(10), 정렬 방식이 들어 있습니다.
	//   · 실행 SQL 대략: select ... from question order by create_date desc limit 10 offset (페이지 × 10)
	//   · limit 과 offset 이 "몇 개를, 어디서부터" 가져올지를 정합니다. (DB 가 페이지를 잘라 줍니다)
	//   · 이 메서드는 기본 제공 findAll(Pageable) 과 같은 기능을 Page 타입으로 드러내 준 것입니다.
	Page<Question> findAll(Pageable pageable);
}
