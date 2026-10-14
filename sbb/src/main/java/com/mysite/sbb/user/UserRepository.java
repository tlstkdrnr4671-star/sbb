// ══════════════════════════════════════════════════════════════════════
// [UserRepository = 회원 표 전용 저장소 창구] (v3.06 추가)
// ══════════════════════════════════════════════════════════════════════
//
// QuestionRepository, AnswerRepository 와 같은 구조입니다. 구현 코드 없이 규격(인터페이스)만 적습니다.
//   <SiteUser, Long> : 다루는 엔티티는 SiteUser, 기본키 자료형은 Long 입니다.
//   (SiteUser 의 id 가 Long 이므로 반드시 같은 자료형으로 맞춥니다)
// ══════════════════════════════════════════════════════════════════════
// 회원 관련 클래스가 모인 폴더 주소입니다.
package com.mysite.sbb.user;

// [JpaRepository] 기본 저장/조회 기능을 물려받는 규격입니다. save, findById, findAll 등이 이미 들어 있습니다.
import org.springframework.data.jpa.repository.JpaRepository;

// 본문이 비어 있어도 기본 저장(save)과 조회(findById) 기능을 바로 쓸 수 있습니다.
public interface UserRepository extends JpaRepository<SiteUser, Long> {
}
