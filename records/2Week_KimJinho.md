## Title: [2Week] 김진호

### 미션 요구사항 분석 & 체크리스트

---

**1. 필수미션 - 호감표시 할 때 예외처리 케이스 3가지 처리**

목표
- 케이스 4: 한 명의 인스타 회원이 다른 인스타 회원에게 중복으로 호감 표시를 할 수 없습니다.
- 케이스 5: 한명의 인스타 회원이 11명 이상의 호감 상대를 등록 할 수 없습니다.
- 케이스 6: 케이스 4 가 발생했을 때 기존의 사유와 다른 사유로 호감을 표시하는 경우에는 성공으로 처리한다.

---
**2. 선택미션 - 네이버 로그인**

목표
- 카카오 로그인이 가능한것 처럼, 네이버 로그인으로도 가입 및 로그인 처리가 되도록 해주세요.
    - 스프링 OAuth2 클라이언트로 구현해주세요.

- 네이버 로그인으로 가입한 회원의 providerTypeCode : NAVER

---
**체크리스트**

- [x] 필수 미션 - 케이스4
- [x] 필수 미션 - 케이스5
- [x] 필수 미션 - 케이스6
- [x] 선택 미션 - 네이버 로그인

### 2주차 미션 요약

---

**[접근 방법]**
1. 필수 미션 - 케이스5
   1. LikeablePerson 테이블에서 현재 로그인한 사용자의 인스타로 호감 표시한 모든 데이터를 가지고 온다.
   2. 그 데이터의 개수가 10개일 때, 호감 표시를 못하도록 예외처리를 했다.
2. 필수 미션 - 케이스4
   1. 본인의 인스타 ID와 상대방의 인스타 ID를 가진 LikeablePerson 리스트를 받아온다.
   2. 비어있지 않다면 이미 호감 표시를 한 것이기 때문에 예외처리를 한다.
3. 필수 미션 - 케이스6
   1. 기존의 사유와 다른 사유로 호감을 표시할 때
   2. 해당 LikeablePerson 데이터를 가져와서 attractiveTypeCode와 ModifyDate를 Update한다.
4. 선택 미션 - 네이버 로그인
   1. Naver Developers에서 애플리케이션 생성하기
   2. application.yml에 네이버 OAuth2 추가
   3. 네이버 로그인 성공! 
   4. 하지만 헤더에 rq.member.username이 NAVER__{"id": 2731659195, "gender": "M", "name": "홍길동"} 이런 형식으로 나온다.
   ```html
   <span th:if="${@rq.login}" th:text="|${@rq.member.username}님 환영합니다.|"></span>
   ```
   5. username을 정하는 CustomOAuth2UserService.java에 loadUser 메서드에서 데이터를 가공해야한다.
   6. Naver의 attributes는 아래처럼 나오기 때문에 필요한 정보인 회원번호는 response안에 담겨 있다.
      ```thymeleafexpressions
      {
         resultcode=00,
         message=success,
         response={
            id=[id],
            nickname=[닉네임],
            gender=M,
            name=홍길동
         }
      }
       ```
   7. OAuth2User의 attributes들을 .get("response")로 가져와서 response라는 객체로 묶인 id를 get("response")로 꺼내준다.
- [참고 블로그](https://lotuus.tistory.com/80)

**[리팩토링]**
