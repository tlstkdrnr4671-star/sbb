// ============================================================
// [강의 노트] Repository(리포지토리, 저장소) = 엔티티(표)에 저장, 조회, 수정, 삭제를 해 주는 "창구"입니다.
// 우리가 쓰는 것은 "규격서(인터페이스)"뿐이고, 실제 구현 코드는 스프링 데이터 JPA 가 자동으로 만들어 줍니다.
// 그래서 이 파일 안에는 SQL 도, 저장 코드도 전혀 없습니다. 그래도 기능이 동작합니다.
// ============================================================
// 같은 패키지에 있어야 @SpringBootApplication 의 @ComponentScan / JPA 자동 탐색이 이 저장소를 찾습니다.
package com.mysite.sbb;

// [JpaRepository] 스프링이 미리 만들어 둔 "기본 저장소 규격"입니다.
// save(저장), findById(번호로 찾기), findAll(전체 조회), delete(삭제) 같은 기능이 이미 들어 있습니다.
import org.springframework.data.jpa.repository.JpaRepository;

// [interface] 인터페이스는 "기능 목록만 적은 규격서"입니다. 실제 동작 코드는 적지 않습니다.
// [extends JpaRepository<Answer, Integer>] 기능을 물려받되, 두 가지를 알려 줍니다.
//   <Answer>  : 이 저장소가 다룰 엔티티(표)는 Answer 입니다.
//   <Integer> : Answer 의 기본키(id) 자료형은 Integer 입니다.
// 그래서 answerRepository.findById(3) 처럼 번호로 답변을 찾을 수 있습니다.
// 본문 { } 이 비어 있어도, 물려받은 기능을 그대로 쓸 수 있습니다.
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

}
