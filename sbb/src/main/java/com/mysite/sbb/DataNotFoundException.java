// ══════════════════════════════════════════════════════════════════════
// [DataNotFoundException = "찾는 데이터가 없다"를 알리는 예외(에러 신호)] (v2.10 추가)
// ══════════════════════════════════════════════════════════════════════
//
// ── 예외(Exception)란? ────────────────────────────────────────────────
//   프로그램이 정상적으로 계속할 수 없을 때 "비상 신호"를 보내는 방법입니다.
//   throw 로 신호를 던지면, 그 신호는 호출한 곳으로 거슬러 올라가며 전달됩니다.
//
// ── 언제 던지나요? ─────────────────────────────────────────────────────
//   QuestionService.getQuestion(id) 에서 "id 번호의 질문이 없음"을 발견했을 때 던집니다.
//   예) 주소창에 /question/detail/999 처럼 없는 번호를 입력한 경우
//
// ── 던져진 뒤 어떻게 되나요? (이 파일의 @ResponseStatus 가 하는 일) ─────────
//   ① Service 에서 throw → ② 컨트롤러 메서드 밖으로 신호가 튀어나옴
//   ③ 스프링이 이 예외 클래스에 붙은 @ResponseStatus 를 보고 404(Not Found) 응답을 만듦
//   → 화면에 "없는 페이지"로 보이고, 컨트롤러에서 try/catch 를 쓰지 않아도 처리됩니다.
//
// ── DB 쪽에서 무슨 일이 일어났나요? ──────────────────────────────────────
//   findById(999) 는 SQL 을 정상 실행하지만 결과가 0줄입니다.
//   이때 DB 는 "오류"가 아니라 "빈 결과"를 돌려줍니다. 그래서 Optional 이 비어 있고,
//   Service 가 빈 것을 발견하고 이 예외를 직접 던지는 것입니다. (DB 가 고장난 것이 아님)
// ══════════════════════════════════════════════════════════════════════
// ─ 패키지 위치 ─
// 질문/답변 등 여러 기능이 함께 쓰는 공통 예외이므로, 기능 폴더(question 등)가 아니라 최상위 com.mysite.sbb 에 둡니다.
package com.mysite.sbb;

// [HttpStatus] HTTP 응답 상태 코드를 이름으로 적어 둔 목록입니다. NOT_FOUND 는 번호 404 를 뜻합니다.
//   참고) HTTP 상태 코드: 200 = 성공, 404 = 찾을 수 없음, 500 = 서버 내부 오류
import org.springframework.http.HttpStatus;
// [@ResponseStatus] 이 예외가 밖으로 나갈 때 응답 상태 코드를 정해 주는 꼬리표를 가져옵니다.
import org.springframework.web.bind.annotation.ResponseStatus;

// ─ @ResponseStatus 상세 ─
//   value  = HttpStatus.NOT_FOUND : 응답 상태를 404 로 정합니다.
//   reason = "entity not found"   : 응답에 함께 실리는 짧은 설명 문장입니다. (개발 중 오류 화면에서 보입니다)
//   이 꼬리표 덕분에 Service 와 Controller 어디에서도 "404 로 바꿔라"라고 따로 쓸 필요가 없습니다.
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "entity not found")
// ─ 클래스 선언 ─
//   · RuntimeException 을 물려받았습니다. (예외 클래스들의 부모 중 하나입니다)
//   · RuntimeException 은 "실행 중에 생기는 예외"입니다. 메서드 뒤에 throws 를 적지 않아도 컴파일 오류가 나지 않습니다.
//   · "extends" 는 "이 클래스는 저 클래스의 기능을 물려받는다"는 뜻입니다.
public class DataNotFoundException extends RuntimeException {
	// ─ serialVersionUID ─
	//   · 예외 객체를 파일이나 네트워크로 옮길 때(직렬화) 쓰는 "버전 번호"입니다.
	//   · static final : 클래스 전체에서 하나뿐이고 바꿀 수 없는 상수입니다.
	//   · 지금은 값의 의미를 몰라도 됩니다. 이 줄이 없으면 컴파일러가 경고를 띄우므로 관례로 적어 둡니다.
	private static final long serialVersionUID = 1L;

	// ─ 생성자 ─
	// 예외를 만들 때 "어떤 메시지인지" 문자열을 받습니다.
	//   사용 예) throw new DataNotFoundException("question not found");
	public DataNotFoundException(String message) {
		// super(message) : 부모 클래스(RuntimeException)에게 메시지를 전달해서 저장하게 합니다.
		//   · 나중에 에러 로그나 개발자 화면에서 "question not found" 같은 메시지를 읽을 수 있습니다.
		super(message);
	}
}
