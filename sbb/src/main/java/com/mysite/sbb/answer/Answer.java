// ============================================================
// [강의 노트] Answer = "답변" 한 개를 DB 표(answer 테이블)의 한 줄로 표현한 클래스입니다.
// 이것이 JPA 의 핵심인 "엔티티(Entity)"입니다.
//
// [엔티티란?] DB 표의 한 줄 = 자바 객체 한 개 로 짝지어 둔 클래스입니다.
//   - 클래스 이름 Answer  → 표 이름 answer (스프링이 자동으로 변환)
//   - 필드 content        → 열(컬럼) content
//   - 객체 한 개          → 표의 한 줄(행, row)
// 그래서 객체를 저장하면 INSERT 가 되고, 꺼내면 SELECT 가 됩니다.
// 우리는 SQL 문 대신 "자바 객체"를 다루고, JPA 가 SQL 로 바꿔서 DB 에 전달합니다.
//
// [관계] 답변은 질문에 달립니다. 그래서 답변 한 개는 질문 한 개를 가리킵니다. (아래 @ManyToOne 참고)
// ============================================================
// 이 엔티티는 com.mysite.sbb.answer 패키지(answer 폴더)에 있습니다. 스프링은 com.mysite.sbb 아래의 모든 하위 패키지를 탐색하므로 JPA 대상으로 찾아 등록합니다.
package com.mysite.sbb.answer;

// [LocalDateTime] 날짜와 시간을 함께 담는 자바 자료형입니다. 예) 2024-01-01 12:30:00 (작성 시각 저장용)
import java.time.LocalDateTime;
// [Set] 같은 값을 두 번 담지 않는 모음 자료형입니다. 한 사람이 두 번 추천해도 한 번만 기록되게 하려고 씁니다. (v3.10 추가)
import java.util.Set;

// [다른 패키지의 클래스 가져오기] 답변은 질문(Question)에 달리므로, question 패키지의 Question 을 가져와야 합니다.
import com.mysite.sbb.question.Question;
// [SiteUser] 답변을 쓴 회원의 타입입니다. 작성자를 연결하기 위해 user 폴더에서 가져옵니다. (v3.08 추가)
import com.mysite.sbb.user.SiteUser;

// [jakarta.persistence] JPA 꼬리표들이 모인 패키지입니다.
// 예전 교재에는 javax.persistence 로 나오지만, 스프링 부트 3 부터는 jakarta.persistence 를 씁니다.
// Column : 필드를 DB 표의 열(컬럼)에 맞춰 세부 규칙을 정하는 꼬리표입니다.
import jakarta.persistence.Column;
// Entity : "이 클래스는 DB 표와 연결된 엔티티다"라고 알려 주는 꼬리표입니다.
import jakarta.persistence.Entity;
// GeneratedValue : 기본키(id) 값을 자동으로 번호 매기는 방식을 정하는 꼬리표입니다.
import jakarta.persistence.GeneratedValue;
// GenerationType : 자동 번호 매기기 방식의 선택지 목록(열거형)입니다.
import jakarta.persistence.GenerationType;
// Id : 이 필드가 "기본키(Primary Key)"라는 표시입니다. 표의 각 줄을 구별하는 고유 번호입니다.
import jakarta.persistence.Id;
// [@ManyToMany] "여러 개 : 여러 개" 관계 꼬리표입니다. (v3.10 추가)
import jakarta.persistence.ManyToMany;
// ManyToOne : "답변(Many) 여러 개 : 질문(One) 한 개" 관계를 나타내는 꼬리표입니다.
import jakarta.persistence.ManyToOne;
// [Lombok] 앞 단계에서 배운 롬복의 꼬리표입니다. 아래 @Getter 를 쓰기 위해 가져옵니다.
import lombok.Getter;
// 같은 이유로 @Setter 를 가져옵니다.
import lombok.Setter;

// [@Getter] 모든 필드의 값을 꺼내는 메서드(getContent() 등)를 자동으로 만듭니다.
// JPA 는 객체를 DB 에 저장할 때 이 getter 로 값을 읽어 갑니다.
@Getter
// [@Setter] 모든 필드에 값을 넣는 메서드(setContent() 등)를 자동으로 만듭니다.
// 객체를 만든 뒤 값을 채울 때 씁니다. (실무에서는 꼭 필요한 곳에만 신중하게 씁니다.)
@Setter
// [@Entity] 가장 중요한 꼬리표입니다. "이 클래스는 DB 표와 연결된 엔티티다"라는 뜻입니다.
// 스프링이 시작할 때 이 표시를 보고 answer 테이블을 만들거나 확인합니다. (앞 단계의 ddl-auto 설정과 연결)
@Entity
// 클래스 한 개 = DB 표 한 개라는 큰 그림을 기억하세요.
public class Answer {
	// [@Id] 기본키 표시입니다. 기본키는 "표의 각 줄을 구별하는 고유 번호"입니다.
	// 같은 번호가 두 줄에 있으면 안 되므로, 보통 1, 2, 3 ... 으로 자동 증가시킵니다.
	@Id
	// [@GeneratedValue] 번호를 "누가" 매길지 정합니다.
	// IDENTITY : DB 의 자동 증가(auto increment) 기능에 맡깁니다. H2 가 이 방식을 지원합니다.
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// 기본키를 담는 필드입니다. Integer 는 정수를 감싼 "객체형" 자료형입니다.
	// int 대신 Integer 를 쓰는 이유: 아직 저장하기 전에는 번호가 없는 상태(null)를 표현할 수 있기 때문입니다.
	private Integer id;

	// [@Column] 필드와 DB 열(컬럼)의 세부 규칙을 정하는 꼬리표입니다.
	// columnDefinition = "TEXT" : DB 열의 자료형을 TEXT(긴 글을 담는 타입)로 직접 지정합니다.
	// 일반 글자(VARCHAR)는 길이가 제한되므로, 답변처럼 긴 글에는 TEXT 를 씁니다.
	@Column(columnDefinition = "TEXT")
	// 답변 내용입니다. 긴 글이 들어갈 수 있도록 위에서 TEXT 로 지정했습니다.
	private String content;

	// 답변을 작성한 시각입니다. 별도 꼬리표가 없으므로 JPA 가 기본 규칙대로 create_date 열을 만듭니다.
	private LocalDateTime createDate;

	// [@ManyToOne] 관계 꼬리표입니다. "답변 여러 개(Many)가 질문 한 개(One)에 속한다"는 뜻입니다.
	// 답변 표에는 question_id 라는 열이 자동으로 생기고, 여기에 질문의 번호가 저장됩니다.
	// 이 열을 "외래키(FK, Foreign Key)"라고 부릅니다. 다른 표(question)의 번호를 가리키는 열입니다.
	@ManyToOne
	// 이 답변이 달린 질문 객체입니다.
	// 자바에서는 "객체"로 연결되고, DB 에서는 question_id 번호로 연결됩니다. 이 변환을 JPA 가 해 줍니다.
	private Question question;

	// ─ 작성자 관계 (v3.08 추가) ─
	// 한 명의 회원이 여러 답변을 쓸 수 있으므로 "답변 여러 개 : 회원 한 명" 관계입니다. (질문의 question 과 같은 방식)
	// 답변 표에 author_id 열이 생기고, 여기에 회원 번호가 저장됩니다.
	@ManyToOne
	// [author] 이 답변을 쓴 회원 객체입니다. 화면에서 answer.author.username 으로 작성자 이름을 꺼냅니다.
	// 자바에서는 객체로, DB 에서는 author_id 번호로 연결됩니다.
	private SiteUser author;

	// ─ 수정 일시 (v3.09 추가) ─
	// 답변을 마지막으로 고친 시각입니다. 처음 저장할 때는 비어 있고(null), 수정할 때만 값이 들어갑니다.
	// DB 의 answer 표에 modify_date 열이 새로 생깁니다. (ddl-auto=update 가 자동으로 열을 추가)
	private LocalDateTime modifyDate;
	
	// ─ 추천인 목록 (v3.10 추가) ─
	// 한 답변을 여러 회원이 추천할 수 있고, 한 회원도 여러 답변을 추천할 수 있습니다. → 다대다(ManyToMany) 관계
	// 다대다 관계는 표 두 개만으로 표현할 수 없어서 JPA 가 중간 표(answer_voter)를 자동으로 만듭니다.
	//   · answer_voter 표 : answer_id 와 voter_id 두 열로 "누가 어떤 답변을 추천했는지" 한 줄씩 기록
	@ManyToMany
    // [voter] 이 답변을 추천한 회원들의 모음입니다. Set 이므로 같은 회원이 중복으로 들어가지 않습니다.
    // 화면에서는 #lists.size(answer.voter) 로 추천 수를 셉니다.
    Set<SiteUser> voter;
}
