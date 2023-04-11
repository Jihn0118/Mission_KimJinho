## Title: [2Week] 김진호

### 미션 요구사항 분석 & 체크리스트

---

**1. 필수미션 - 호감표시 할 때 예외처리 케이스 3가지 처리**

목표
- 케이스 4: 한 명의 인스타 회원이 다른 인스타 회원에게 중복으로 호감 표시를 할 수 없습니다.
- 케이스 5: 한명의 인스타 회원이 11명 이상의 호감 상대를 등록 할 수 없습니다.
- 케이스 6: 케이스 4 가 발생했을 때 기존의 사유와 다른 사유로 호감을 표시하는 경우에는 성공으로 처리한다.

케이스 4 SQL
```SQL
# 어떠한 회원이 특정회원에 대해서 이미 호감표시를 했는지 검사하는 SQL, 질의결과가 하나라도 있다면 이미 호감을 표시한 경우이다.
# 여기서 1은 로그인한 회원의 인스트 계정 번호이고
# 여기서 2는 상대방의 인스타계정 번호이다.
SELECT *
FROM likeable_person
WHERE from_insta_member_id = 1
AND to_insta_member_id = 2;
```

케이스 5 SQL
```SQL
# 어떠한 회원이 호감표시를 총 몇번 했는지 검사하는 SQL
# 여기서 1은 로그인한 회원의 인스타계정 번호이다.
SELECT COUNT(*)
FROM likeable_person
WHERE from_insta_member_id = 1;
```
케이스 5 자바
```java
// 내가 좋아하는 사람 리스트
List<LikeablePerson> fromLikeablePeople = rq.getMember().getInstaMember().getFromLikeablePeople();
// 내가 좋아하는 사람의 수
fromLikeablePeople.size();

// 나를 좋아하는 사람 리스트
List<LikeablePerson> toLikeablePeople = rq.getMember().getInstaMember().getToLikeablePeople();
// 나를 좋아하는 사람의 수
toLikeablePeople.size();
```

케이스 5 타임리프
```thymeleafexpressions
<!-- 내가 좋아하는 사람의 수 -->
<span th:text="|내가 좋아하는 사람의 수 : ${#lists.size(@rq.member.instaMember.fromLikeablePeople)}|"></span>
<!-- 나를 좋아하는 사람의 수 -->
<span th:text="|나를 좋아하는 사람의 수 : ${#lists.size(@rq.member.instaMember.toLikeablePeople)}|"></span>

<!-- 내가 좋아하는 사람 목록 -->
<li th:each="likeablePerson: ${@rq.member.instaMember.fromLikeablePeople}">
	<span class="toInstaMember_username" th:text="${likeablePerson.toInstaMember.username}"></span>
	...
</li>
```
케이스 6 SQL
```SQL
# 사용자는 호감표시를 했지만 케이스 6에 해당되기 때문에 실제로는 수정이 일어난다.
# 여기서 5는 기존 호감표시 번호이다.
UPDATE likeable_person
SET modify_date = NOW(),
attractive_type_code = 2
WHERE id = 5;
```
---
**2. 선택미션 - 네이버 로그인**


목표
- 카카오 로그인이 가능한것 처럼, 네이버 로그인으로도 가입 및 로그인 처리가 되도록 해주세요.
    - 스프링 OAuth2 클라이언트로 구현해주세요.

- 네이버 로그인으로 가입한 회원의 providerTypeCode : NAVER

SQL
```SQL
# 최초로 네이버 로그인을 통해서 가입이 될 때 실행되어야 할 SQL
# 네이버 앱에서의 해당 회원번호를 2731659195 라고 가정하면
INSERT INTO `member`
SET create_date = NOW(),
modify_date = NOW(),
provider_type_code = 'NAVER',
username = 'NAVER__2731659195',
password = '',g
insta_member_id = NULL;
```
---
**체크리스트**

- [x] 필수 미션 - 케이스4
- [x] 필수 미션 - 케이스5
- [x] 필수 미션 - 케이스6
- [ ] 선택 미션 - 네이버 로그인

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

**[리팩토링]**

