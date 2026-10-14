// ============================================================
// [강의 노트] 컨트롤러(Controller) = 웹 요청을 처음 받아 처리하는 "접수 창구"입니다.
//
// 웹 사이트가 동작하는 기본 흐름 (이 흐름은 프로젝트 끝까지 계속 반복됩니다)
//   1) 사용자가 브라우저 주소창에 http://localhost:8080/hello 를 입력 (= 요청 Request)
//   2) 스프링이 주소(/hello)를 보고 "이 주소는 어느 창구가 담당하지?"를 찾음
//   3) 담당 창구(이 클래스)의 메서드가 실행됨
//   4) 메서드가 돌려준 값이 브라우저 화면에 나감 (= 응답 Response)
//
// 자바 입장에서는 "메서드 호출", 웹 입장에서는 "요청과 응답"입니다.
// ============================================================
// 이 클래스도 com.mysite.sbb 패키지에 있습니다. 그래서 @SpringBootApplication 의
// @ComponentScan 이 이 클래스를 자동으로 찾아서 부품(Bean)으로 등록합니다.
package com.mysite.sbb;

// [import] @Controller 꼬리표를 쓰려고 가져옵니다. 스프링 MVC 의 창구 역할 표시입니다.
import org.springframework.stereotype.Controller;
// [import] @GetMapping 은 "이 주소로 GET 방식 요청이 오면 이 메서드를 실행"하라는 꼬리표입니다.
// GET 은 "주소창에 입력하거나 링크를 눌러서 정보를 달라고 요청하는 방식"입니다.
import org.springframework.web.bind.annotation.GetMapping;
// [import] @ResponseBody 는 "반환값을 화면 파일(HTML)로 찾지 말고 그대로 응답 본문에 넣어라"는 꼬리표입니다.
import org.springframework.web.bind.annotation.ResponseBody;

// [@Controller] "이 클래스는 웹 요청을 처리하는 창구입니다"라는 표시입니다.
// 스프링이 시작될 때 이 표시를 보고 이 클래스를 객체로 만들어 보관해 둡니다. (= Bean 등록)
@Controller
// 창구 클래스의 선언입니다. 클래스 이름(HelloController)은 "무슨 일을 하는 창구인지" 알 수 있게 짓습니다.
public class HelloController {
	// [@GetMapping("/hello")] 주소 연결(라우팅) 꼬리표입니다.
	//   "/hello" 주소로 GET 요청이 들어오면 아래의 hello() 메서드를 실행하라는 뜻입니다.
	//   http://localhost:8080/hello 에서 localhost:8080 이 뒤의 /hello 가 주소의 경로(path)입니다.
	@GetMapping("/hello")
	// [@ResponseBody] 메서드가 돌려주는 값(문자열)을 "화면 템플릿 이름"이 아니라 "응답 내용 그 자체"로 보냅니다.
	// 지금은 HTML 화면이 없으므로 글자 "Hello Spring Boot Board" 를 그대로 보여 주는 용도입니다.
	@ResponseBody
	// [메서드] hello() 는 "/hello 요청을 처리하는 실제 작업 함수"입니다.
	  // public   : 스프링이 외부에서 호출할 수 있어야 하므로 공개합니다.
	  // String   : 돌려주는 값의 자료형이 문자열(글자)이라는 뜻입니다.
	  // hello()  : 메서드 이름입니다. 이름은 자유지만 기능을 나타내면 좋습니다.
	public String hello() {
		// [return] 메서드의 결과를 돌려줍니다. 이 글자가 브라우저 화면에 그대로 나타납니다.
		// 문자열은 큰따옴표(" ")로 감쌉니다.
		return "Hello Spring Boot Board";
	}
}
