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

// [Optional] 아이디로 찾은 회원이 없을 수도 있으므로 상자에 담아 돌려받습니다. (v3.07 추가)
import java.util.Optional;
// [JpaRepository] 기본 저장/조회 기능을 물려받는 규격입니다. save, findById, findAll 등이 이미 들어 있습니다.
import org.springframework.data.jpa.repository.JpaRepository;

// 본문이 비어 있어도 기본 저장(save)과 조회(findById) 기능을 바로 쓸 수 있습니다.
public interface UserRepository extends JpaRepository<SiteUser, Long> {
	// ─ 아이디로 회원 찾기 (v3.07 추가) ─
	// find + By + username : "username 이 같은 회원을 찾아라" 라는 쿼리 메서드입니다. → where username=?
	//   · 메서드 본문이 없어도 스프링 데이터 JPA 가 이름을 읽고 SQL 을 만듭니다.
	//   · 결과가 없을 수 있으므로 Optional<SiteUser> 로 받습니다. (없으면 빈 상자)
	//   · 필드 이름은 SiteUser 의 username 과 같은 철자로 적어야 합니다.
	Optional<SiteUser> findByusername(String username);
}
