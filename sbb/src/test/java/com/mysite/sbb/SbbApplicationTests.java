// ============================================================
// [강의 노트] 테스트 클래스 = "프로그램이 제대로 조립되었는지 검사하는 시험지"입니다.
// src/test 폴더에 있으며, 실제 서비스 실행과는 별도로 자동 검사를 돌릴 때 쓰입니다.
// ============================================================
package com.mysite.sbb;

// [static import] 클래스 이름 없이 Assertions 안의 검사 메서드를 바로 쓰게 해 줍니다.
// assertEquals(기대값, 실제값) : 두 값이 같은지 검사합니다. 다르면 테스트가 실패합니다.
import static org.junit.jupiter.api.Assertions.assertEquals;
// assertTrue(조건) : 조건이 참(true)인지 검사합니다.
import static org.junit.jupiter.api.Assertions.assertTrue;

// [List] 답변 목록을 담을 자료형입니다.
import java.util.List;
// [Optional] "값이 있을 수도, 없을 수도 있다"는 상황을 감싸는 상자입니다.
// 번호로 찾았는데 없을 수도 있으므로, 결과를 상자에 담아 돌려받는 것이 안전합니다.
import java.util.Optional;

// [JUnit 5] 자바에서 가장 많이 쓰는 "단위 테스트 도구"입니다. @Test 꼬리표를 쓰려고 가져옵니다.
import org.junit.jupiter.api.Test;
// [Autowired] "스프링이 만들어 둔 부품(Bean)을 여기에 자동으로 넣어 달라"는 꼬리표입니다.
import org.springframework.beans.factory.annotation.Autowired;
// [SpringBootTest] 테스트할 때 스프링 부트를 실제로 한 번 띄워 보는 도구입니다.
import org.springframework.boot.test.context.SpringBootTest;
// [Transactional] 메서드 안의 DB 작업을 "한 묶음(트랜잭션)"으로 처리하는 꼬리표입니다.
import org.springframework.transaction.annotation.Transactional;

// [다른 패키지의 클래스 가져오기] 테스트는 answer, question 패키지의 클래스를 사용하므로 import 로 가져옵니다.
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;

// 이 꼬리표를 붙이면 테스트 실행 시 스프링 부트 전체(부품, 설정 등)를 실제로 시작합니다.
// 즉 "우리 앱이 실제로 뜨는가?"를 확인하게 됩니다.
@SpringBootTest
// 테스트 클래스 선언입니다. 테스트 클래스는 보통 public 을 붙이지 않아도 됩니다.
class SbbApplicationTests {

	// [@Autowired] 의존성 주입(DI)입니다. 우리가 new 로 직접 만들지 않아도, 스프링이 보관하고 있던
	// QuestionRepository 객체를 아래 필드에 자동으로 넣어 줍니다. "필요한 부품을 밖에서 넣어 준다"는 뜻입니다.
	@Autowired
	// 질문 저장소 창구입니다. 이 테스트에서는 여기에서 질문 2번을 찾아옵니다.
	private QuestionRepository questionRepository;

	// [@Transactional] 이 테스트 메서드를 하나의 트랜잭션 안에서 실행합니다.
	// 테스트가 끝나면 DB 의 변경 내용이 자동으로 되돌려져(롤백) 테스트 데이터가 DB 에 남지 않습니다.
	// 또한 "답변 목록을 나중에 꺼내는(지연 로딩)" 작업도 트랜잭션 안에서만 안전하게 가능합니다.
	@Transactional
	// [@Test] 이 메서드 하나가 "테스트 한 개"라는 표시입니다. 실행 도구가 자동으로 찾아서 실행합니다.
	@Test
	// [testJpa] JPA 가 DB 와 실제로 잘 대화하는지 검사하는 테스트입니다.
	// 이 테스트는 DB 에 "질문 2번과 그 답변" 데이터가 이미 들어 있다고 가정합니다.
	// (강의 중 H2 콘솔에서 직접 넣어 둔 데이터입니다. 데이터가 없으면 테스트는 실패합니다.)
	void testJpa() {
		// [findById(2)] 기본키가 2인 질문을 DB 에서 찾아옵니다.  → SELECT ... FROM question WHERE id = 2
		// 결과는 바로 Question 이 아니라 Optional 상자에 담겨 옵니다. (없을 수도 있으니까요)
		// this. 는 "이 클래스의 questionRepository 필드"를 가리킵니다.
		Optional<Question> oq = this.questionRepository.findById(2);
		// isPresent() : 상자 안에 실제 질문이 들어 있는지 확인합니다. 없으면 여기서 테스트가 실패합니다.
		assertTrue(oq.isPresent());
		// get() : 상자를 열어 실제 Question 객체를 꺼냅니다. (확인된 뒤에만 안전하게 꺼낼 수 있습니다)
		Question q = oq.get();

		// [getAnswerList] 질문 객체에서 답변 목록을 꺼냅니다. 앞 단계의 @OneToMany 관계 덕분에 가능합니다.
		// JPA 가 answer 표에서 question_id 가 2인 답변들을 찾아 와서 이 목록을 채웁니다.
		List<Answer> answerList = q.getAnswerList();

		// 답변이 정확히 1개인지 검사합니다. (기대값 1, 실제 목록 크기)
		assertEquals(1, answerList.size());
		// 첫 번째 답변(인덱스 0)의 내용이 기대한 문장과 같은지 검사합니다. 자바 목록은 0번부터 셉니다.
		assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());
	}
}
