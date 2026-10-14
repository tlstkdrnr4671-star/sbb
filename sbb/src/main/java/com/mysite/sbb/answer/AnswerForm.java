// ══════════════════════════════════════════════════════════════════════
// [AnswerForm = 답변 입력 폼의 "전달 상자"] (v2.16 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 폼(form)에서 보낸 값을 한곳에 담는 객체입니다. 이름은 Form 으로 끝나는 것이 관례입니다.
//   · 화면: <textarea name="content"> 에 쓴 글
//   · 전달: 스프링이 이 객체의 content 변수에 자동으로 넣어 줍니다. (데이터 바인딩)
//   · 검사: content 에 붙은 @NotEmpty 규칙으로, 비어 있으면 오류를 기록합니다.
//
// 왜 Answer(엔티티)와 따로 만들까요?
//   · Answer 는 DB 표와 연결된 "저장용" 객체이고, AnswerForm 은 "화면 입력용" 객체입니다.
//   · 화면 입력 규칙을 바꿔도 DB 구조는 그대로 둘 수 있습니다. (두 역할을 분리)
// ══════════════════════════════════════════════════════════════════════
// 답변 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.answer;

// [@NotEmpty] "비어 있으면 안 됨" 검사 규칙을 가져옵니다. 값이 null 이거나 빈 글자("")면 오류입니다.
import jakarta.validation.constraints.NotEmpty;

// [Lombok @Getter] getContent() 메서드를 자동으로 만들기 위해 가져옵니다.
import lombok.Getter;
// [Lombok @Setter] setContent() 메서드를 자동으로 만들기 위해 가져옵니다. 스프링이 입력값을 넣을 때 이 메서드를 씁니다.
import lombok.Setter;

// ─ 자동 생성: getter ─
// getContent() 가 만들어져서, 컨트롤러가 입력값을 꺼내 쓸 수 있습니다.
@Getter
// ─ 자동 생성: setter ─
// setContent(값) 이 만들어져서, 스프링이 폼의 content 값을 이 객체에 넣을 수 있습니다.
@Setter
// 답변 입력 폼 전용 객체입니다. @Entity 가 없으므로 DB 표와는 연결되지 않습니다.
public class AnswerForm {
	// ─ 검사 규칙 ─
	// 내용이 비어 있으면 검사에 실패하고, 화면에 "내용은 필수항목입니다." 라는 문장이 나타납니다.
	//   · message 속성 : 실패했을 때 보여 줄 문장입니다.
	@NotEmpty(message = "내용은 필수항목입니다.")
	// 답변 내용입니다. 폼의 입력 칸(name="content")과 이름이 같아서 자동으로 연결됩니다.
	private String content;
}
