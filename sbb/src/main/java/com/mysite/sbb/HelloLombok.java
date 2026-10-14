// ============================================================
// [강의 노트] 롬복(Lombok) 실습 클래스입니다. 웹과는 무관하고 "자바 + 롬복 사용법"만 보여 줍니다.
// main 메서드가 있으므로 이 파일을 직접 실행하면 콘솔(검은 창)에 결과가 출력됩니다.
// ============================================================
package com.mysite.sbb;

// [import] 롬복의 @Getter 꼬리표를 가져옵니다.
import lombok.Getter;
// [import] 롬복의 @RequiredArgsConstructor 꼬리표를 가져옵니다.
import lombok.RequiredArgsConstructor;

// [@RequiredArgsConstructor] "final 로 선언된 필드만 골라서" 생성자를 자동으로 만들어 줍니다.
// 아래 두 필드가 final 이므로, new HelloLombok("헬로", 5) 같은 생성자가 자동으로 생깁니다.
@RequiredArgsConstructor
// [@Getter] 모든 필드의 값을 꺼내는 메서드(getHello(), getLombok())를 자동으로 만들어 줍니다.
// 원래라면 public String getHello() { return hello; } 를 직접 써야 하는데, 그 수고를 줄여 줍니다.
@Getter
// 롬복이 만든 코드는 이 파일에 보이지 않지만, 컴파일된 결과에는 들어 있습니다.
public class HelloLombok {
	// [필드] 클래스 안에 저장해 두는 변수입니다.
	//   private : 이 클래스 안에서만 직접 접근할 수 있는 비공개 변수
	//   final   : 한 번 값을 넣으면 바꿀 수 없는 변수
	//   String  : 글자(문자열) 자료형
	private final String hello;
	// int : 정수 자료형입니다. 생성할 때 한 번 정해지면 바뀌지 않습니다.
	private final int lombok;

	// 이 main 은 롬복 동작을 눈으로 확인하는 실습용 시작점입니다. (SbbApplication 의 main 과 같은 역할)
	public static void main(String[] args) {
		// [new] 설계도(클래스)로 실제 물건(객체)을 만드는 명령입니다.
		// "헬로" 와 5 는 생성자에 넘겨 주는 값이며, 위의 두 필드에 차례대로 들어갑니다.
		HelloLombok helloLombok = new HelloLombok("헬로", 5);
		// System.out.println : 콘솔에 한 줄을 출력합니다.
		// helloLombok.getHello() : 롬복이 만들어 준 getter 로 hello 값("헬로")을 꺼냅니다.
		System.out.println(helloLombok.getHello());
		// 같은 방식으로 lombok 필드 값(5)을 꺼내서 출력합니다.
		System.out.println(helloLombok.getLombok());
	}
}
