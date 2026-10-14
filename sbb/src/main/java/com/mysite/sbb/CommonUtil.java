// ══════════════════════════════════════════════════════════════════════
// [CommonUtil = 여러 화면에서 함께 쓰는 도구 모음] (v3.12 새 파일)
// ══════════════════════════════════════════════════════════════════════
//
// 지금은 markdown() 하나만 있습니다. 질문/답변 내용(마크다운 글)을 HTML 로 바꿔 줍니다.
//
// ── 화면에서 쓰는 법 ───────────────────────────────────────────────
//   question_detail.html :  th:utext="${@commonUtil.markdown(question.content)}"
//   · @commonUtil : 스프링이 보관 중인 부품(Bean) 이름입니다. 클래스 이름의 첫 글자를 소문자로 바꾼 이름이 자동으로 붙습니다.
//   · Thymeleaf 의 ${@부품이름.메서드(...)} 문법으로 자바 메서드를 화면에서 바로 부를 수 있습니다.
// ══════════════════════════════════════════════════════════════════════
// 프로젝트 최상위 패키지입니다. 특정 기능(질문, 답변, 회원)에 속하지 않는 공용 도구라서 여기에 둡니다.
package com.mysite.sbb;

// [Node] 마크다운 글을 분석해서 만든 "문서 나무 구조"의 한 마디입니다. (제목, 문단, 목록 등이 마디가 됩니다)
import org.commonmark.node.Node;
// [Parser] 마크다운 글자를 읽어서 Node 나무 구조로 바꾸는 "분석기"입니다.
import org.commonmark.parser.Parser;
// [HtmlRenderer] Node 나무 구조를 HTML 글자로 바꾸는 "출력기"입니다.
import org.commonmark.renderer.html.HtmlRenderer;
// [@Component] 이 클래스를 스프링 부품(Bean)으로 등록하는 꼬리표입니다.
import org.springframework.stereotype.Component;

// ─ 부품 등록 ─
// @Component 를 붙이면 스프링이 시작할 때 CommonUtil 객체를 하나 만들어 "commonUtil" 이라는 이름으로 보관합니다.
// @Controller, @Service 도 사실 @Component 의 한 종류입니다. 역할이 특별하지 않은 일반 부품에는 @Component 를 씁니다.
@Component
// 공용 도구 클래스입니다.
public class CommonUtil {
	// ─ markdown(글) : 마크다운 글 → HTML 글 ─
	//   · 매개변수 markdown : 사용자가 쓴 원래 글
	//   · 반환값            : HTML 태그가 들어간 글 (예: "**안녕**" → "<p><strong>안녕</strong></p>")
	public String markdown(String markdown) {
		// 분석기를 만듭니다. builder().build() 는 "기본 설정으로 조립해서 완성"하는 빌더 패턴입니다.
		Parser parser = Parser.builder().build();
		// 글을 분석해서 문서 나무 구조(document)를 만듭니다.
		Node document = parser.parse(markdown);
		// HTML 출력기를 기본 설정으로 만듭니다.
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		// 나무 구조를 HTML 글자로 바꿔 돌려줍니다.
		return renderer.render(document);
	}
}
