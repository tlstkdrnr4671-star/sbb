// ============================================================
// [강의 노트] QuestionRepository = 질문(question 표)을 다루는 "저장소 창구"입니다.
// AnswerRepository 와 같은 구조이고, 아래에는 "메서드 이름으로 검색하는 법"이 추가로 있습니다.
// ============================================================
// Question 엔티티와 같은 패키지에 있습니다.
package com.mysite.sbb.question;

// [List] 여러 개의 결과를 담을 목록 자료형입니다. 아래 findBySubjectLike 의 반환형으로 쓰입니다.
import java.util.List;

// [JpaRepository] 기본 저장소 규격입니다. (AnswerRepository 와 같은 설명)
import org.springframework.data.jpa.repository.JpaRepository;

// <Question> : 이 저장소가 다룰 엔티티는 Question 입니다. <Integer> : 기본키 자료형입니다.
// 아래 세 메서드는 "구현 없이 이름만" 적었습니다. 이것이 스프링 데이터 JPA 의 특별한 점입니다.
public interface QuestionRepository extends JpaRepository<Question, Integer> {
	// [쿼리 메서드] 메서드 "이름" 자체가 검색 명령서입니다. 스프링이 이름을 읽고 SQL 을 자동으로 만듭니다.
	//   findBySubject(String subject)  →  SELECT ... FROM question WHERE subject = ?
	//   규칙: find + By + 필드이름  →  "그 필드 값이 같은 것을 찾아라"
	//   반환형 Question : 결과가 한 개라고 기대합니다. (같은 제목이 여러 개면 에러가 날 수 있습니다)
	Question findBySubject(String subject);

	// And 로 조건을 이어 붙입니다.  →  WHERE subject = ? AND content = ?
	// 매개변수의 순서는 메서드 이름의 조건 순서(Subject, Content)와 맞춥니다.
	Question findBySubjectAndContent(String subject, String content);

	// Like 는 "비슷한 글자 찾기"입니다.  →  WHERE subject LIKE ?
	// 여러 개가 나올 수 있으므로 반환형을 List<Question> 으로 씁니다.
	// 값에 % 를 붙이면 "포함" 검색이 됩니다. 예) "%스프링%" 은 "스프링"이 들어간 제목을 모두 찾습니다.
	List<Question> findBySubjectLike(String subject);
}
