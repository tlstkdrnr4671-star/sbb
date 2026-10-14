// ══════════════════════════════════════════════════════════════════════
// [AnswerService = 답변 저장 업무 담당] (v3.08 갱신: 작성자 정보 저장)
// ══════════════════════════════════════════════════════════════════════
//
// 이전에는 create(질문, 내용) 두 값만 받았지만, 이제는 누가 썼는지(작성자)도 함께 받아 저장합니다.
// 작성자 정보가 없으면 "누가 쓴 답변인지" 화면에 보여 줄 수 없기 때문입니다.
// ══════════════════════════════════════════════════════════════════════
// 답변 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.answer;

// [LocalDateTime] 지금 시각을 담는 자료형입니다. 작성 시각을 저장할 때 씁니다.
import java.time.LocalDateTime;

// [@Service] 업무 처리 담당 부품이라는 표시 꼬리표입니다.
import org.springframework.stereotype.Service;

// 답변이 달릴 질문의 타입입니다.
import com.mysite.sbb.question.Question;
// 답변을 쓴 회원의 타입입니다. (v3.08 추가)
import com.mysite.sbb.user.SiteUser;

// [Lombok] 생성자 자동 생성 꼬리표를 가져옵니다.
import lombok.RequiredArgsConstructor;

// final 필드(answerRepository)를 받는 생성자를 자동으로 만듭니다. (생성자 주입, DI)
@RequiredArgsConstructor
// 업무 부품으로 스프링에 등록합니다.
@Service
// 답변 업무를 담당하는 클래스입니다.
public class AnswerService {

	// 답변 저장소입니다.
	private final AnswerRepository answerRepository;

	// ─ create(질문, 내용, 작성자) (v3.08 변경) ─
	// 매개변수 author 가 추가되었습니다. 이 답변을 쓴 회원입니다.
	// 반환형은 아직 void 입니다. (v3.11 에서 저장한 답변을 돌려주도록 바뀝니다)
	public void create(Question question, String content, SiteUser author) {
		// 빈 답변 객체를 만듭니다. 아직 DB 에는 없습니다.
		Answer answer = new Answer();
		// 내용을 채웁니다.
		answer.setContent(content);
		// 작성 시각을 지금으로 채웁니다.
		answer.setCreateDate(LocalDateTime.now());
		// 소속 질문을 연결합니다. (question_id 열)
		answer.setQuestion(question);
		// ─ 작성자 연결 (v3.08 추가) ─
		// 작성자 회원을 답변에 연결합니다. 저장되면 author_id 열에 회원 번호가 들어갑니다.
		answer.setAuthor(author);
		// save : 새 답변을 INSERT 합니다. 이 한 줄이 끝나면 답변이 DB 에 남습니다.
		this.answerRepository.save(answer);
	}
}
