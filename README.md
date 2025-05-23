# 👗 FITLOOP - Backend
<p align="center">
  <img src="https://github.com/user-attachments/assets/b6bb06a6-a87c-4507-8a58-7600ed47b422" alt="FITLOOP Logo">
</p>

**FITLOOP**은 사용자가 옷을 사고팔 수 있는 패션 거래 플랫폼입니다.  
사용자는 마켓에서 옷을 사고팔 수 있으며, 룩북을 통해 스타일을 공유하고, 챌린지에 참여해 트렌디한 패션 문화를 즐길 수 있습니다.

---
<br>

## 🛠️ 프로젝트 개요

- **프로젝트명**: FITLOOP
- **목적**: 사용자들이 패션 아이템을 사고팔고, 룩북과 챌린지를 통해 스타일을 공유하는 패션 커뮤니티 플랫폼 제공

<br>

### ✨ 주요 기능

- **🛒 마켓플레이스**: 사용자가 직접 패션 아이템을 등록 및 판매
- **📸 룩북**: 사진 업로드를 통한 스타일 공유
- **🎥 챌린지**: 태그 + 영상 기반의 패션 챌린지 참여 기능
- **⭐ 즐겨찾기(북마크)**: 관심 있는 상품 및 룩북을 저장하여 쉽게 다시 보기
- **🔍 필터링**: 스타일, 브랜드, 가격대 등의 조건으로 검색 가능

---
<br>

## 💻 개발 환경
<table> 
    <thead> 
        <tr>
            <th>카테고리</th> 
            <th>라이브러리</th> 
            <th>설명</th> 
        </tr> 
    </thead> 
    <tbody> 
        <tr> 
            <td rowspan="2">
                <strong>프레임워크</strong>
            </td> 
            <td>Spring Boot (v3.2.1)</td>  
            <td>Spring 기반 웹 애플리케이션 프레임워크</td> 
        </tr> 
        <tr>    
            <td>Spring Security</td> 
            <td>인증 및 권한 관리 라이브러리</td> 
        </tr> 
        <tr> 
            <td>    
                <strong>데이터 처리</strong>
            </td>
            <td>Spring Data JPA</td> 
            <td>ORM(Object Relational Mapping) 데이터 처리 라이브러리</td>
        </tr> 
        <tr> 
            <td rowspan="2">   
            <strong>데이터베이스</strong></td>    
            <td>MySQL</td> 
            <td>관계형 데이터베이스</td> 
        </tr> 
        <tr> 
            <td>MySQL Connector</td>
            <td>MySQL 데이터베이스 연동 드라이버</td>
        </tr> 
        <tr> 
            <td rowspan="2">
                <strong>보안</strong>
            </td> 
            <td>JWT (io.jsonwebtoken)</td>
            <td>JSON Web Token 기반 인증 및 인가</td> 
        </tr> 
        <tr> 
            <td>Spring OAuth2 Client</td> 
            <td>OAuth2 기반 소셜 로그인 및 인증 처리</td> 
        </tr> 
        <tr> 
            <td>
                <strong>유효성 검사</strong>
            </td> 
            <td>Spring Boot Validation</td> 
            <td>데이터 검증 라이브러리</td> 
        </tr> 
        <tr> 
            <td rowspan="2">
                <strong>유틸리티</strong>
            </td> 
            <td>Project Lombok</td>
            <td>코드 간결화를 위한 애노테이션 라이브러리</td>
        </tr> 
            <tr> 
                <td>Spring Cloud AWS</td>
                <td>AWS 서비스 연동을 위한 라이브러리</td>
            </tr> 
        <tr> 
            <td rowspan="2">
                <strong>테스트</strong>
            </td> 
            <td>Spring Boot Test</td> 
            <td>Spring 기반 테스트 프레임워크</td> 
        </tr> 
        <tr> 
            <td>Spring Security Test</td>
            <td>보안 기능 테스트 지원 라이브러리</td> 
        </tr> 
    </tbody> 
</table>


---

<br>

## 🚀 설계 방향

FITLOOP 백엔드는 Spring Boot & Spring Security 기반으로 구축되었으며, 보안과 유지보수성을 고려한 설계를 적용하였습니다.

### 🔐 인증 및 권한 관리
- **Spring Security & JWT 기반 인증 시스템 적용**
    - 로그인 시 Access Token과 Refresh Token을 발급
    - Access Token을 헤더에 저장하여 요청 시 포함
    - 리프레시 토큰은 HttpOnly 쿠키 에 저장하여 보안 강화 
    - DB에 리프레시 토큰을 저장하여 유효성 검증을 추가로 수행 
    - 요청마다 JWT 필터를 통해 토큰 유효성 및 권한을 검증

- **역할(Role) 기반 접근 제어**
    - Spring Security의 SecurityConfig를 활용하여 API 접근 권한 관리
    - 사용자 역할(MEMBER, ADMIN)에 따라 접근 가능한 엔드포인트를 구분
    - 로그인 응답 시 사용자 권한 및 개인정보 입력 여부 를 반환하여 프론트엔드의 UI 렌더링을 지원

- **안전한 로그아웃 처리**
    - Spring Security 로그아웃 필터 구현
    - 로그아웃 시 DB에서 리프레시 토큰 삭제
    - 쿠키 만료 처리를 통해 클라이언트의 보안 유지

### 🏛️ 엔티티 설계 및 매핑
- **JPA를 활용한 엔티티 설계 및 단방향 매핑 적용**
    - 유지보수성을 고려하여 단방향 매핑을 기본 원칙으로 설계
    - 필요할 경우 지연 로딩을 적용하여 성능 최적화

### 🚨 예외 처리

- **커스텀 에러 코드 사용**
    - 에러 유형을 명확히 정의하고, 일관된 JSON 형식으로 응답하여 프론트엔드의 오류 처리 용이성 향상


---

<br>

## 코드 컨벤션

### ✅ Backend Convention
- 변수, 메소드, 인스턴스를 작성할 때는 기본적으로 “Camel Case(카멜 케이스)”를 사용합니다.
- 메소드명을 작성할 때는 동사 + 명사 형태로 구성합니다.
- Class, Constructor를 작성할 때는 “Pascal Case(=upper 카멜 케이스)”를 사용합니다.
- 약어는 가능하면 사용하지 않습니다. 
- Repository/Controller/Service 경우, (엔티티명) + Repository/Controller/Service 형태로 작명합니다.
- DTO가 요청/응답 중 어떤 상황에서 어떤 역할에서 사용되는지를 반드시 나타내야 합니다.
  기본적으로 매핑에 사용되는 DTO인 경우 (엔티티명) + Request/Response 형태로 작명합니다.
- 클래스를 import 할 때는 반드시 와일드카드(*) 없이 모든 클래스명을 다 써야 합니다.
- 클래스, 메소드, 인스턴스 변수의 제한자는 Java Lauguage Specification에서 명시한 아래의 순서를 준수합니다.
- 조건/반복문의 실행문이 한 줄로 끝나도 중괄호를 사용합니다.
- 빈 줄은 명령문 그룹의 영역을 표시하기 위하여 사용합니다.
- 식별자와 여는 소괄호 ()사이에는 공백을 삽입하지 않습니다.
  생성자와 메소드의 선언, 호출, 어노테이션 선언 뒤에 쓰이는 소괄호가 이에 해당합니다.


<br>
<br>
<br>

### ⚙️ ERD 설계
- [ERD 설계 보기](https://www.erdcloud.com/d/Ey2588ifii9X4k5A9)

## 📚 API 명세 (Swagger 기반)

FITLOOP 프로젝트는 Swagger(OpenAPI 3.0)를 활용하여 REST API 명세를 자동화하였으며, SwaggerHub를 통해 팀원들과 API 문서를 공유하고 관리하고 있습니다.

### 🔗 API 문서 보기

- [SwaggerHub 문서 보기](https://app.swaggerhub.com/apis/none-f10-fb1/fitloop-api/1.0.0)
- 또는 `fitloop-api.yaml` 파일을 Postman 또는 Swagger Editor에서 불러와 사용 가능

---

### 🧪 테스트 방법

#### Swagger UI (로컬 테스트)
- 주소: `http://localhost:8080/swagger-ui/index.html`
- `Try it out` 버튼 클릭 후 실제 API 호출 가능 (JWT access 헤더 필요 시 수동 입력)

#### Postman 테스트
1. `fitloop-api.yaml` 파일을 다운로드
2. Postman → `Import` → `File` → YAML 파일 선택
3. 각 API 요청 실행 (`access` 헤더 등 필요 시 수동 입력)

---

### 📌 주요 API 목록

| Method | Endpoint                          | 설명                   |
|--------|------------------------------------|------------------------|
| POST   | `/api/v1/register`                | 회원가입               |
| POST   | `/api/v1/users/profile`           | 사용자 프로필 등록     |
| GET    | `/api/v1/user`                    | 유저 정보 조회         |
| POST   | `/api/v1/products/register`       | 상품 등록              |
| GET    | `/api/v1/products/{id}`           | 상품 상세 조회         |
| GET    | `/api/v1/products/recent`         | 최근 상품 목록 조회    |
| POST   | `/api/v1/cart/add`                | 장바구니 담기          |
| GET    | `/api/v1/cart`                    | 장바구니 조회          |
| DELETE | `/api/v1/cart/remove`             | 장바구니 항목 삭제     |
| DELETE | `/api/v1/cart/clear`              | 장바구니 전체 비우기   |
| POST   | `/api/v1/reissue`                 | JWT 토큰 재발급        |
| GET    | `/api/v1/auth/{provider}`         | 소셜 로그인 (Google 등)|
| GET    | `/health-check`                   | 서버 상태 확인         |

---

### 🧾 요청 예시

#### 회원가입
```json
{
  "username": "testuser",
  "password": "P@ssw0rd!",
  "name": "홍길동",
  "birthday": "1995-05-15",
  "email": "test@example.com"
} 
```

<br>
<br>
<br>

## 🗓️ FITLOOP 프로젝트 개발 히스토리

본 프로젝트는 애자일 방법론 중 하나인 스크럼(Scrum)을 적용하여 짧은 주기로 기능을 개발하고,<br> 지속적인 논의와 피드백을 통해 점진적으로 완성도를 높이는 방식으로 진행하였습니다.


<details>
  <summary>📅 2024년 12월 개발 히스토리 보기</summary>

| 날짜 | 작업 내용 |
|------|-----------|
| 12.20 | 프로젝트 구조 설계 시작, 기능 구상 |
| 12.21 | 프로젝트 기능 기획 |
| 12.23 | 프로젝트 상세 기능 기획 |
| 12.24 | 프로젝트명 확정(FitLoop), 컨벤션 논의 |
| 12.26 | ERD 논의 시작 |
| 12.27 | 로고 제작, 테이블 구성 논의 |
| 12.28 | 이미지 정책, 구독 서비스, Enum 논의 |
| 12.29 | 상태 이력 테이블 필요성 검토 |
| 12.30 | ERD 구현 및 관계 설정, 배송지 테이블 추가 |
| 12.31 | 구독형 서비스 논의 |

</details>

<details>
  <summary>📅 2024년 1월 개발 히스토리 보기</summary>

| 날짜 | 작업 내용 |
|------|-----------|
| 01.02 | 개발 일정 수립, JWT/SSR/Middleware 정리 |
| 01.03 | Gradle 설정, 예외 처리 설계 |
| 01.04 | 로고 및 GitHub 라벨 확정 |
| 01.05 | 이슈/PR 템플릿 작성, AWS 및 쿠버네티스 학습 |
| 01.06 | Ant Design 학습, 피그마 초안 |
| 01.07 | 인증/인가 구조, HttpOnly 쿠키 정리 |
| 01.08 | JWT와 SSR/CSR 개념 비교 |
| 01.09 | ESLint 대응, 이미지 최적화 논의 |
| 01.10 | 리프레시 토큰 구조, 삭제 처리 논의 |
| 01.11 | JWT 로그인 로직 구성, 토큰 저장 전략 수립, 캐시(redis) 대해 논의 |
| 01.12 | 회원 상태(status), @Transactional 처리 논의 |
| 01.13 | 테이블 통합 vs 분리 → 분리 설계 선택 |
| 01.14 | JWT 기반 코드 구현 시작 |
| 01.15 | Axios 도입, JSON 전송 방식 결정 |
| 01.16 | API 명세서 및 버튼 UI 기획 |
| 01.17 | 계정 정보 페이지 개발 |
| 01.19 | 시스템 기능 구현 내용 정리 |
| 01.20 | 공통 에러 코드 및 전역 예외 처리 구성 |
| 01.21 | 사용자 정의 예외 클래스 정리 |
| 01.22 | Spring Security 필터 설정 논의 |
| 01.23 | CORS 설정 및 토큰 로직 정리 |
| 01.24 | RefreshToken 로직 개발 시작 |
| 01.25 | 프론트 상태 관리, 진행률 UI 구성 |
| 01.26 | 회원가입 유효성 검사 추가 |
| 01.27 | 사용자 정의 에러 반환 방식 정리 |

</details>

<details>
  <summary>📅 2024년 2월 개발 히스토리 보기</summary>

| 날짜 | 작업 내용 |
|------|-----------|
| 02.14 | Enum/Boolean 필드 설계, 생일 예외 처리 개선 |
| 02.21 | 로그인 후 개인정보 입력 여부 분기 설계 |
| 02.23 | JWT 필터 permitAll 이슈 해결 방식 논의 |
| 02.24 | 마이페이지 설계, 로그아웃 필터 개발 |
| 02.25 | 로그아웃 필터 완성, 마이페이지 UI 구성 |
| 02.26 | 상품 도메인 기획 및 등록/목록 페이지 분배 |
| 02.28 | 공통 색상 시스템 구축, 광고 이미지 연동 설계 |

</details>

<details>
  <summary>📅 2024년 3월 개발 히스토리 보기</summary>

| 날짜 | 작업 내용 |
|------|-----------|
| 03.03 | Tailwind 및 스크롤바 스타일 개선 |
| 03.04 | 카테고리 UI 및 페이지 이동 로직 구현, AWS 진행 |
| 03.08 | 상품 등록 시 카테고리 선택 UI 논의 |
| 03.11 | UserDetails ID 이슈 해결, 개인정보 입력 UI 보완 |
| 03.12 | S3 광고 이미지 연동 컨트롤러 설계, 마이페이지 구성 |
| 03.13 | URL 명칭 통일, 회원가입 UX 개선, 사용자 통계 테이블 설계 |

</details>
