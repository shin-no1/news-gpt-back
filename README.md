# 📰 NewsGPT Backend

AI 기반 뉴스 요약 및 분석 서비스 **NewsGPT**의 백엔드 서버입니다.  
사용자가 입력한 뉴스 URL을 바탕으로 크롤링, AI 요약, 북마크 및 히스토리 관리를 지원하는 기능을 직접 설계하고 구현했습니다.

API 명세 자동화, JWT 인증, 캐시 및 이력 저장 전략 등 실무 중심의 설계를 적용했습니다.

> 🔗 [API 문서 (Spring REST Docs)](https://api.know-that.dev/docs/index.html)  
> 🔗 [Frontend GitHub 보기](https://github.com/shin-no1/news-gpt-front)  
> 🔗 [구현 보기](https://newsgpt.know-that.dev/news)  

---

## 주요 역할 및 구현 기능

### 뉴스 요약 처리
- 사용자가 제출한 뉴스 URL을 `Jsoup`으로 크롤링하여 본문을 추출
- OpenAI GPT API를 통해 기사 요약 수행
- 프롬프트 버전에 따라 요약 결과를 구분하여 저장 (캐시 + 히스토리 관리)

### 북마크 기능
- 사용자가 요약된 뉴스를 그룹 단위로 북마크 가능
- 북마크 그룹 생성, 수정, 정렬 순서 관리 기능 제공 (예정)

### 요약 히스토리 관리
- 동일한 기사라도 프롬프트 버전이 다르면 요약 결과도 별도로 저장
- 최신 버전 결과는 `news_summary`, 과거 이력은 `news_summary_history` 테이블로 분리 저장

### 사용자 인증/인가
- JWT 기반 로그인 인증 처리
- 인증된 사용자만 북마크 및 히스토리 접근 가능 (히스토리 예정)

---

## 기술 스택 및 설계 요소

| 구분       | 기술 및 도구                                                 |
|------------|--------------------------------------------------------------|
| Language   | Java 17                                                      |
| Framework  | Spring Boot 3.x, Spring Security, Spring Web, Spring Data JPA |
| DB         | MariaDB (JPA), Redis (요약 캐싱)) |
| API 문서    | Spring REST Docs + Asciidoctor                              |
| 크롤링     | Jsoup                                                        |
| AI 연동    | OpenAI GPT-3-turbo API                                             |
| 인증       | JWT 토큰 기반 인증                                           |
| 빌드 도구   | Maven                                                       |
| 형상 관리   | GitHub                                                      |

---

## 아키텍처 설계
```
사용자 → [Frontend] → 뉴스 URL 전달
↓
[Backend (Spring Boot)]
├─ Jsoup HTML 파싱
├─ OpenAI GPT 요약 요청
├─ DB 저장 (캐싱 + 히스토리)
├─ Redis 캐싱 처리 (요청 횟수 제한)
└─ 사용자별 북마크 기능 제공
```
- 캐싱 구조: 프롬프트 버전이 동일한 경우, DB에 저장된 요약 결과를 재사용
- 버전 관리 전략: 프롬프트 버전 기준으로 이력 저장 및 최신 버전 구분
- API 보안: 모든 민감 API는 JWT 인증 필수

---

## 테스트 및 API 문서화

- `@WebMvcTest` + `MockMvc` 기반의 통합 테스트 작성
- REST Docs로 API 문서 자동 생성 → HTML 문서로 정적 배포
- 예외 상황 및 인증 처리까지 명세 포함

🔗 [API 문서 보기](https://api.know-that.dev/docs/index.html)

---

## 데이터베이스 설계

| 테이블명               | 설명                                       |
|------------------------|--------------------------------------------|
| `site`                 | 뉴스 출처 도메인 정보                      |
| `news_summary`         | 가장 최신 버전의 뉴스 요약 결과 캐시       |
| `news_summary_history` | 프롬프트 버전별 요약 히스토리              |
| `bookmark_group`       | 북마크 그룹 정보                           |
| `bookmark`             | 북마크된 뉴스 정보                         |
| `user`                 | 사용자 정보 (JWT 기반 인증 사용자)         |


