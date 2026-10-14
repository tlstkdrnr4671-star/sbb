// ============================================================
// [강의 노트] MainController = 메인 화면(특정 주소)을 담당하는 또 다른 "접수 창구"입니다.
// 한 프로젝트에는 @Controller 클래스를 여러 개 둘 수 있습니다. 주소(@GetMapping 값)만 겹치지 않으면 됩니다.
// HelloController 와 구조가 거의 같고, 차이는 주소("/sbb")와 돌려주는 문장뿐입니다.
// ============================================================
// 같은 com.mysite.sbb 패키지에 있으므로 @ComponentScan 이 자동으로 찾아서 등록합니다.
package com.mysite.sbb;

// [import] @Controller 꼬리표(웹 요청 창구 표시)를 가져옵니다.
import org.springframework.stereotype.Controller;
// [import] @GetMapping : GET 방식 요청의 주소를 메서드와 연결하는 꼬리표입니다.
import org.springframework.web.bind.annotation.GetMapping;
// [import] @ResponseBody : 반환한 문자열을 화면 파일이 아니라 응답 내용 그대로 보내는 꼬리표입니다.
import org.springframework.web.bind.annotation.ResponseBody;

// [@Controller] 웹 요청을 처리하는 창구 클래스라는 표시입니다. 스프링이 시작할 때 자동으로 등록합니다.
@Controller
// 클래스 이름 MainController 는 "메인 화면 담당 창구"라는 뜻입니다. 이름은 역할이 드러나게 짓습니다.
public class MainController {

	// [@GetMapping("/sbb")] 브라우저가 http://localhost:8080/sbb 로 GET 요청을 보내면 index() 메서드를 실행합니다.
	@GetMapping("/sbb")
	// [@ResponseBody] 돌려줄 문자열을 화면 파일을 찾지 않고 그대로 보여 줍니다.
	@ResponseBody
	// index() : 메서드 이름입니다. 웹에서 "기본(대표) 페이지"를 뜻하는 관례적인 이름입니다.
	public String index() {
		// 큰따옴표 안의 글자가 그대로 화면에 나옵니다. 한글도 그대로 쓸 수 있습니다.
		// 한글이 깨지지 않는 이유는 파일과 응답을 모두 UTF-8(글자 인코딩 방식)로 맞춰 두기 때문입니다.
		return "안녕하세요 sbb에 오신 것을 환영합니다.";
	}
}
