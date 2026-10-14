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
// [Optional] 번호로 찾은 답변이 없을 수도 있으므로 상자에 담아 다룹니다. (v3.09 추가)
import java.util.Optional;

// [@Service] 업무 처리 담당 부품이라는 표시 꼬리표입니다.
import org.springframework.stereotype.Service;

// [DataNotFoundException] 답변을 찾지 못했을 때 던질 "데이터 없음" 예외입니다. (v3.09 추가)
import com.mysite.sbb.DataNotFoundException;
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

	// ─ getAnswer(번호) : 답변 한 개 찾기 (v3.09 추가) ─
	// 수정, 삭제를 하려면 먼저 "어떤 답변인지" 꺼내야 합니다. 질문의 getQuestion 과 같은 방식입니다.
	public Answer getAnswer(Integer id) {
		// findById : 기본키(id)가 같은 답변을 찾습니다. 결과는 Optional 상자입니다.
		Optional<Answer> answer = this.answerRepository.findById(id);
		// 상자에 답변이 들어 있으면 true 입니다.
		if (answer.isPresent()) {
			// 상자에서 답변을 꺼내 돌려줍니다.
			return answer.get();
		} else {
			// 답변이 없으면 예외를 던집니다. (화면에는 404 오류로 보입니다)
			throw new DataNotFoundException("answer not found");
		}
	}

	// ─ modify(답변, 새 내용) : 답변 수정 (v3.09 추가) ─
	// 이미 DB 에 있는 답변 객체의 내용과 수정 시각을 바꾼 뒤 다시 저장합니다.
	public void modify(Answer answer, String content) {
		// 내용을 새 글로 바꿉니다.
		answer.setContent(content);
		// 수정 시각을 지금으로 기록합니다.
		answer.setModifyDate(LocalDateTime.now());
		// ─ save 의 두 얼굴 ─
		// id 가 없는 새 객체를 save 하면 INSERT, id 가 있는 기존 객체를 save 하면 UPDATE 가 실행됩니다.
		// 여기서는 기존 답변이므로 UPDATE 로 내용이 바뀝니다.
		this.answerRepository.save(answer);
	}

	// ─ delete(답변) : 답변 삭제 (v3.09 추가) ─
	public void delete(Answer answer) {
		// delete : DELETE 문이 실행되어 answer 표에서 이 답변 한 줄이 지워집니다.
		this.answerRepository.delete(answer);
	}
}
