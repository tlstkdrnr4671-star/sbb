// ══════════════════════════════════════════════════════════════════════
// [AnswerService = 답변 "저장" 업무 담당] (v2.12 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 답변을 저장할 때 해야 할 일을 순서대로 모아 둔 클래스입니다.
//   ① 빈 답변 객체를 만든다
//   ② 내용, 작성 시각, 소속 질문을 채운다
//   ③ 저장소(AnswerRepository)에 저장을 맡긴다 → DB 에 한 줄 추가(INSERT)
//
// 왜 Controller 가 직접 하지 않을까요? → 창구는 "요청을 받는 일"만, 업무 담당은 "업무 규칙"을 맡습니다.
// ══════════════════════════════════════════════════════════════════════
// 답변 관련 클래스가 모인 폴더 주소입니다. AnswerRepository, Answer 와 같은 폴더입니다.
package com.mysite.sbb.answer;

// 답변이 속할 질문 객체의 타입입니다. 질문 폴더(question)에 있어서 import 가 필요합니다.
import com.mysite.sbb.question.Question;
// [Lombok] 생성자 자동 생성 꼬리표를 가져옵니다.
import lombok.RequiredArgsConstructor;
// [@Service] 업무 처리 담당 클래스라는 표시 꼬리표입니다. 스프링이 부품으로 등록합니다.
import org.springframework.stereotype.Service;

// [LocalDateTime] 지금 이 순간의 날짜와 시각을 담는 자료형입니다. 답변 작성 시각을 저장할 때 씁니다.
import java.time.LocalDateTime;

// ─ 생성자 자동 생성 ─
// final 필드(answerRepository)를 받는 생성자를 Lombok 이 만들어 줍니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 이 클래스가 업무 처리 부품임을 스프링에게 알립니다.
@Service
// 답변 업무를 담당하는 클래스입니다. 화면이나 주소는 전혀 모릅니다.
public class AnswerService {

	// ─ 저장소 필드 ─
	// 답변 표(answer)와 대화하는 저장소입니다. 직접 new 하지 않고 스프링이 넣어 줍니다.
	private final AnswerRepository answerRepository;

	// ─ create() : 답변 저장 ─
	//   · 매개변수 Question question : 이 답변이 달릴 질문입니다.
	//   · 매개변수 String content    : 사용자가 쓴 답변 글입니다.
	//   · 반환형 void              : 돌려줄 값이 없습니다. (저장만 하고 끝냅니다)
	public void create(Question question, String content) {
		// ─ 빈 답변 객체 만들기 ─
		// new Answer() 는 아직 DB 에 없는 빈 객체입니다. 기본키 id 도 아직 비어 있습니다.
		// 이 객체가 한 줄의 DB 데이터가 될 예정입니다.
		Answer answer = new Answer();
		// 답변 내용을 채웁니다. (Answer 의 @Setter 가 만든 메서드입니다)
		answer.setContent(content);
		// ─ 작성 시각 ─
		// LocalDateTime.now() 는 지금 이 순간의 날짜와 시각입니다. (예: 2025-01-01T10:30:00)
		// 작성 시각을 함께 저장해 두면 나중에 "언제 쓴 답변인지" 보여 줄 수 있습니다.
		answer.setCreateDate(LocalDateTime.now());
		// ─ 소속 질문 연결 ─
		// "이 답변은 이 질문에 달린 것"이라는 연결을 만듭니다. (앞 단계의 @ManyToOne 관계)
		// 저장되면 answer 표에 question_id 열이 생기고, 여기에 질문 번호가 들어갑니다.
		answer.setQuestion(question);
		// ─ 저장 (INSERT) ─
		//   · save(answer) 는 "새 객체이므로 INSERT 하라"는 뜻입니다. 대략 아래 SQL 이 실행됩니다.
		//       insert into answer (content, create_date, question_id, id) values (?, ?, ?, default)
		//   · 저장되면서 DB 가 자동 증가 번호(id)를 붙이고, 그 값이 answer 객체에도 채워집니다.
		//   · 이 메서드가 끝나면 답변이 DB 에 실제로 남습니다.
		this.answerRepository.save(answer);
	}
}
