커뮤니티 웹 서비스 프로젝트(2024.3.27 ~ 

개발환경
* IntelliJ IDEA 
* Spring Boot 3.2.4
* JPA
* java 21
* Thymeleaf
* Gradle

CRUD(제목,내용,작성시간,수정시간,글쓴이)
- 게시물 작성 / 수정 / 삭제 / 불러오기
- 댓글 / 대댓글 작성 / 불러오기 / 삭제
- 게시물 이미지 업로드 / 불러오기 / 삭제 / 수정

Oauth2.0 
- 구글 로그인
- 카카오 로그인
- 네이버 로그인

Spring Security 
- CustomUserDetails 유저 정보 커스텀화
- 인증,권한 관리 
- passwordEncoder 패스워드 암호화 저장
- 로그인/로그아웃/회원가입

게시판 / 댓글 페이징 처리 시스템
- Page 객체로 Dto 불러오기
- Pageable 이용해서 페이지 객체 정보 관리
- 페이지 최대 글 3개로 Set
- 페이지 범위 4개로 Set

Restful API
- ResponseEntity 
- @ReqeustBody
- Ajax 
- JQuery
- JSON

예외처리 로직 
- throw Exception 처리
- @RestControllerAdvice,@ExceptionHandler 전역 예외 처리
- 예외처리 메세지를 받아와서 뷰에 출력

권한 확인
- 글 작성자 확인 기능
- 댓글 / 게시판 작성 권한 확인 기능

게시물 조회수 시스템
- Entity 조회수 Count
- Cookie를 이용해서 조회수 중복 카운트 방지 

게시물 댓글 개수 시스템
- 댓글/대댓글 작성 시 Entity 댓글 개수 Count
- 게시물에 댓글 개수 표시

유효성 검증
- @Valid

엔티티 관계
- Article -> Comment -> Reply

작성시간과 수정시간
- JPA Auditing 기능 Entity 상속

