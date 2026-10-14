// ============================================================
// [강의 노트] sbb 프로젝트의 "출발점" 파일입니다.
// 이 파일의 main 메서드가 실행되면 웹 사이트 전체가 살아납니다.
// 자바는 알지만 스프링은 처음이라면, 이 파일을 "가게의 문을 여는 열쇠"라고 생각하세요.
// ============================================================
// [package] 이 파일이 놓인 "폴더 주소"입니다.
// com.mysite.sbb 는 src/main/java/com/mysite/sbb 폴더를 뜻합니다.
// 같은 package 안의 클래스끼리는 import 없이 서로 부를 수 있습니다.
package com.mysite.sbb;

// [import] 다른 곳에 있는 클래스를 "가져다 쓰겠다"는 선언입니다.
// SpringApplication 은 스프링 부트 프로그램을 실제로 시작시키는 도구 클래스입니다.
import org.springframework.boot.SpringApplication;
// SpringBootApplication 은 "이 클래스가 스프링 부트의 출발점이다"라는 표시(꼬리표)입니다.
import org.springframework.boot.autoconfigure.SpringBootApplication;

// [어노테이션 @] 클래스나 메서드 위에 붙이는 "꼬리표(표지판)"입니다.
// 꼬리표를 보고 스프링이 이 클래스를 특별하게 다룹니다.
// @SpringBootApplication 은 아래 3가지 꼬리표를 한 번에 붙여 주는 "종합 세트"입니다.
//   1) @Configuration          : 이 클래스가 설정(설계) 역할을 한다는 표시
//   2) @EnableAutoConfiguration : 프로젝트에 들어 있는 라이브러리를 보고 필요한 설정을 자동으로 해 줌
//                                 (예: 웹 라이브러리가 있으면 내장 웹 서버를 자동으로 준비)
//   3) @ComponentScan          : 이 package(com.mysite.sbb)와 하위 폴더를 뒤져서
//                                 @Controller, @Service 같은 "부품(Bean)"을 자동으로 찾아 등록
// 여기서 Bean(빈)은 "스프링이 대신 만들고 관리해 주는 객체"라는 뜻입니다.
@SpringBootApplication
// [class] 클래스는 "설계도"입니다. 이 파일 이름과 같은 공개(public) 클래스를 선언합니다.
// 자바 규칙: public 클래스의 이름은 파일 이름(SbbApplication.java)과 반드시 같아야 합니다.
public class SbbApplication {

	// [main 메서드] 자바 프로그램이 실행될 때 가장 먼저 호출되는 "정해진 시작 함수"입니다.
	//   public         : 어디서든 호출할 수 있음
	//   static         : 객체를 만들지 않고 클래스 이름만으로 호출할 수 있음
	//   void           : 호출한 곳에 돌려줄 값이 없음
	//   String[] args  : 실행할 때 명령줄에서 넘겨받은 값들 (지금은 사용하지 않음)
	public static void main(String[] args) {
		// [핵심 한 줄] 스프링 부트를 실제로 "시작"시킵니다. 내부에서 순서대로 일어나는 일:
		//   1) 스프링 컨테이너(부품 보관소)를 만든다
		//   2) @ComponentScan 으로 찾은 부품(Bean)들을 보관소에 등록한다
		//   3) 내장 웹 서버(톰캣)를 켜서 브라우저의 요청을 기다린다
		// SbbApplication.class 는 "기준이 되는 클래스가 무엇인지" 알려 주는 표기입니다.
		SpringApplication.run(SbbApplication.class, args);
	}

}
