package com.ll.gramgram.base.initData;

import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "test"})
public class NotProd {
    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            InstaMemberService instaMemberService,
            LikeablePersonService likeablePersonService
    ) {
        return args -> {
            Member memberAdmin = memberService.join("admin", "1234").getData();
            Member memberUser1 = memberService.join("user1", "1234").getData();
            Member memberUser2 = memberService.join("user2", "1234").getData();
            Member memberUser3 = memberService.join("user3", "1234").getData();
            Member memberUser4 = memberService.join("user4", "1234").getData();

            Member memberUser5ByKakao = memberService.whenSocialLogin("KAKAO", "KAKAO__2733159143").getData();
            Member memberUser6ByGoogle = memberService.whenSocialLogin("GOOGLE", "GOOGLE__116604773536495149101").getData();


            instaMemberService.connect(memberUser2, "insta_user2", "M");
            instaMemberService.connect(memberUser3, "insta_user3", "W");
            instaMemberService.connect(memberUser4, "insta_user4", "M");

            likeablePersonService.like(memberUser3, "insta_user4", 1);
            likeablePersonService.like(memberUser3, "insta_user100", 2);

            // 내 테스트 데이터 추가
            instaMemberService.connect(memberUser6ByGoogle, "김진호테스트", "M");
            likeablePersonService.like(memberUser6ByGoogle, "insta_user100", 1);
            likeablePersonService.like(memberUser6ByGoogle, "insta_user101", 2);
            likeablePersonService.like(memberUser6ByGoogle, "insta_user102", 3);
            likeablePersonService.like(memberUser6ByGoogle, "insta_user103", 1);
            likeablePersonService.like(memberUser6ByGoogle, "insta_user104", 2);
            likeablePersonService.like(memberUser6ByGoogle, "insta_user105", 3);
            likeablePersonService.like(memberUser6ByGoogle, "insta_user106", 1);
            likeablePersonService.like(memberUser6ByGoogle, "insta_user107", 2);
        };
    }
}
