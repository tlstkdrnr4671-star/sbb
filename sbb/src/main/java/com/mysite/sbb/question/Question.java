// ============================================================
// [강의 노트] Question = "질문" 한 개를 DB 의 question 테이블 한 줄로 표현한 엔티티입니다.
// 질문 한 개는 답변을 여러 개 가질 수 있습니다. (질문 : 답변 = 1 : N)
// Answer.java 와 짝을 이루므로 두 파일을 함께 보면 이해가 빠릅니다.
// ============================================================
// Answer 는 다른 패키지(answer)에 있으므로, 아래에서 import 로 가져옵니다.
package com.mysite.sbb.question;

// [LocalDateTime] 날짜와 시간 자료형입니다. 질문 작성 시각을 담습니다.
import java.time.LocalDateTime;
// [List] 여러 개의 객체를 순서대로 담는 자바 목록(컬렉션)입니다. 여기서는 답변 여러 개를 담습니다.
// List<Answer> 의 <Answer> 는 "목록 안에 담을 객체 종류"를 뜻하는 제네릭 표기입니다.
import java.util.List;
// [Set] 중복 없는 모음 자료형입니다. 추천인 목록에 씁니다. (v3.10 추가)
import java.util.Set;

// [다른 패키지의 클래스 가져오기] 질문은 답변(Answer) 목록을 가지므로 answer 패키지의 Answer 를 가져옵니다.
import com.mysite.sbb.answer.Answer;
// [SiteUser] 질문을 쓴 회원의 타입입니다. user 폴더에서 가져옵니다. (v3.08 추가)
import com.mysite.sbb.user.SiteUser;

// CascadeType : "부모(질문)에 작업을 하면 자식(답변)에도 같은 작업을 적용할지" 정하는 선택지 목록입니다.
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
// [@ManyToMany] 다대다 관계 꼬리표입니다. (v3.10 추가)
import jakarta.persistence.ManyToMany;
// [ManyToOne] "질문 여러 개 : 회원 한 명" 관계를 나타내는 꼬리표입니다. (v3.08 추가)
import jakarta.persistence.ManyToOne;
// OneToMany : "질문 한 개(One) : 답변 여러 개(Many)" 관계를 나타내는 꼬리표입니다.
import jakarta.persistence.OneToMany;
// [Lombok] @Getter 를 쓰기 위해 가져옵니다. (Answer.java 와 같은 이유)
import lombok.Getter;
// [Lombok] @Setter 를 쓰기 위해 가져옵니다.
import lombok.Setter;

// 모든 필드의 값을 꺼내는 getter 메서드를 자동으로 만듭니다.
@Getter
// 모든 필드에 값을 넣는 setter 메서드를 자동으로 만듭니다.
@Setter
// [@Entity] Question 도 DB 표(question 테이블)와 연결되는 엔티티라는 표시입니다.
@Entity
public class Question {
	// 기본키(고유 번호)입니다. Answer 와 같은 방식으로 자동 증가합니다.
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// [@Column(length = 200)] 제목 열의 최대 길이를 200 글자로 정합니다. 제목은 짧아야 하므로 VARCHAR(200) 이 됩니다.
	@Column(length = 200)
	// 질문 제목입니다.
	private String subject;

	// 질문 내용은 길 수 있으므로 TEXT 로 지정합니다. (Answer.content 와 같은 이유)
	@Column(columnDefinition = "TEXT")
	// 질문 내용입니다.
	private String content;

	// 질문 작성 시각입니다.
	private LocalDateTime createDate;

	// [@OneToMany] 질문 한 개가 답변 여러 개를 가진다는 관계 꼬리표입니다.
	// mappedBy = "question" : "답변 쪽의 question 필드가 관계를 관리하는 주인이다"라는 뜻입니다.
	//   - 외래키(question_id)는 답변 표에 있으므로, 실제 연결 정보는 Answer.question 이 가집니다.
	//   - 이쪽(Question)은 거울처럼 읽기 위한 쪽입니다. 그래서 "mappedBy(거울에 비친 대상)"라고 부릅니다.
	// cascade = CascadeType.REMOVE : 질문을 삭제하면, 그 질문에 달린 답변도 함께 삭제합니다.
	//   (주인 없는 답변이 남아 있는 일을 막기 위함입니다.)
	@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
	// 이 질문에 달린 답변들의 목록입니다. DB 에 목록이 따로 저장되지 않고,
	// 답변 표의 question_id 값을 보고 JPA 가 필요한 답변들을 찾아옵니다.
	private List<Answer> answerList;

	// ─ 질문의 작성자 관계 (v3.08 추가) ─
	// 한 회원이 여러 질문을 쓸 수 있으므로 "질문 여러 개 : 회원 한 명" 관계입니다.
	// 질문 표에 author_id 열이 생기고, 여기에 회원 번호가 저장됩니다.
	@ManyToOne
	// [author] 이 질문을 쓴 회원 객체입니다. 화면에서 question.author.username 으로 작성자 이름을 꺼냅니다.
	private SiteUser author;

	// ─ 수정 일시 (v3.09 추가) ─
	// 질문을 마지막으로 고친 시각입니다. 한 번도 고치지 않았으면 null 입니다.
	// 화면에서는 이 값이 있을 때만 "modified at" 표시를 보여 줍니다.
	private LocalDateTime modifyDate;
	
	// ─ 추천인 목록 (v3.10 추가) ─
	// 질문 여러 개 : 회원 여러 명 관계입니다. JPA 가 중간 표 question_voter 를 만들어 관리합니다.
	@ManyToMany
    // [voter] 이 질문을 추천한 회원들입니다. 추천 수 = voter 의 개수입니다.
    Set<SiteUser> voter;
}
