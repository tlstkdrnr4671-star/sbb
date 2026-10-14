// ══════════════════════════════════════════════════════════════════════
// [QuestionForm = 질문 등록 폼의 "전달 상자"] (v2.16 추가)
// ══════════════════════════════════════════════════════════════════════
//
// 질문 등록 화면의 제목 칸과 내용 칸 값을 담습니다. 각 칸에 검사 규칙도 함께 적어 둡니다.
// AnswerForm 과 같은 방식(전달 상자 + 검사 규칙)입니다.
// ══════════════════════════════════════════════════════════════════════
// 질문 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.question;

// [@NotEmpty] 비어 있으면 안 되는 값에 붙이는 검사 꼬리표입니다.
import jakarta.validation.constraints.NotEmpty;
// [@Size] 글자 수 범위를 정하는 검사 꼬리표입니다.
import jakarta.validation.constraints.Size;

// [Lombok @Getter] getter 메서드를 자동으로 만듭니다.
import lombok.Getter;
// [Lombok @Setter] setter 메서드를 자동으로 만듭니다.
import lombok.Setter;

// 모든 필드의 getter 를 자동으로 만듭니다. (getSubject, getContent)
@Getter
// 모든 필드의 setter 를 자동으로 만듭니다. (setSubject, setContent)
@Setter
// 질문 등록 폼 전용 객체입니다.
public class QuestionForm {
	// ─ 제목 검사 1: 비어 있으면 안 됨 ─
	@NotEmpty(message = "제목은 필수항목입니다.")
	// ─ 제목 검사 2: 최대 200글자 ─
	// max = 200 : 200글자를 넘으면 오류입니다. DB 의 subject 열 길이 제한과 맞춘 값입니다.
	@Size(max = 200)
	// 질문 제목입니다.
	private String subject;

	// ─ 내용 검사: 비어 있으면 안 됨 ─
	@NotEmpty(message = "내용은 필수항목입니다.")
	// 질문 내용입니다.
	private String content;
}
