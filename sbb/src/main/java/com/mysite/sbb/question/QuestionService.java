// ══════════════════════════════════════════════════════════════════════
// [QuestionService = 질문 "업무" 담당 클래스] (v2.09 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 세 계층의 역할 분담 (이 프로젝트 전체에 반복해서 나오는 구조입니다)
//   · Controller (창구)    : 웹 요청을 받고, 화면 이름이나 이동 주소를 정함 → QuestionController
//   · Service    (업무)    : 실제 업무 규칙을 처리함 → 지금은 "전체 목록 가져오기" 한 가지
//   · Repository (저장소)  : DB 와 직접 대화함 (SQL 을 자동으로 만들어 실행) → QuestionRepository
//
// 지금은 Service 가 하는 일이 적어서 "그냥 전달만 하는 것처럼" 보일 수 있습니다.
// 하지만 이 자리가 있어야 나중에 "정렬", "검색", "작성자 확인" 같은 업무 규칙을 한 곳에 모을 수 있습니다.
// ══════════════════════════════════════════════════════════════════════
// 질문 관련 클래스가 모인 폴더 주소입니다. Question, QuestionRepository 와 같은 폴더에 있습니다.
package com.mysite.sbb.question;

// [List] 질문 목록을 돌려줄 때 쓰는 자료형입니다.
import java.util.List;

// [@Service] "업무 처리 담당" 클래스라는 표시 꼬리표입니다. @Controller 와 같이 스프링이 부품(Bean)으로 등록합니다.
// 기능상 차이는 없고, 역할 이름을 드러내기 위해 붙입니다.
import org.springframework.stereotype.Service;

// [Lombok] @RequiredArgsConstructor 를 쓰기 위해 가져옵니다.
import lombok.RequiredArgsConstructor;

// ─ 생성자 자동 생성 ─
// final 필드(questionRepository)를 받는 생성자를 Lombok 이 만들어 줍니다.
// 스프링이 이 객체를 만들 때 QuestionRepository 를 넣어 주므로, 우리는 new 로 만들 필요가 없습니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 이 클래스가 업무 부품임을 스프링에게 알립니다. 스프링이 시작할 때 객체를 만들어 보관합니다.
@Service
// 질문 업무를 처리하는 클래스입니다. 화면이나 웹 요청을 전혀 모르고, 업무와 저장소만 다룹니다.
public class QuestionService {

	// ─ 저장소 필드 ─
	// DB 와 대화하는 QuestionRepository 입니다. Service 는 DB 를 직접 다루지 않고 이 저장소에게 부탁합니다.
	//   · 인터페이스이므로 new 할 수 없습니다. 스프링이 JPA 구현체를 만들어 넣어 줍니다.
	private final QuestionRepository questionRepository;

	// ─ getList() : 질문 목록 가져오기 ─
	// 질문 전체 목록을 돌려주는 업무 메서드입니다.
	//   · 매개변수가 없습니다. 지금은 "모든 질문"만 필요하기 때문입니다.
	//   · 반환형 List<Question> : 질문 객체들이 든 목록입니다. 질문이 없으면 빈 목록입니다.
	//   · 이름 getList 는 "목록을 가져온다"는 뜻입니다. (자바에서 get 으로 시작하는 이름은 보통 "꺼내 준다"는 뜻입니다)
	public List<Question> getList() {
		// ─ 저장소에 부탁 ─
		// findAll() 은 JpaRepository 가 가진 "전체 조회" 메서드입니다.
		//   · 실행되는 SQL 은 대략 아래와 같습니다.
		//       select q1_0.id, q1_0.content, q1_0.create_date, q1_0.subject from question q1_0
		//   · 결과(DB 의 여러 줄)가 Question 객체 목록으로 바뀌어 여기로 돌아옵니다.
		//   · 이 메서드는 결과를 그대로 전달만 합니다. 나중에 정렬이나 검색 규칙을 넣을 자리입니다.
		return this.questionRepository.findAll();
	}
}
