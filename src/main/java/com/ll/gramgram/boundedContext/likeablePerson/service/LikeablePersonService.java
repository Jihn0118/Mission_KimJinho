package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if ( member.hasConnectedInstaMember() == false ) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        // 필수미션 케이스5
        if (member.getInstaMember().getFromLikeablePeople().size() >= 10){
            return RsData.of("F-2", "10명까지만 호감 상대를 등록할 수 있습니다.");
        }

        InstaMember fromInstaMember = member.getInstaMember();
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();


        // LikeablePerson 테이블에서 나의 인스타 아이디와
        // 내가 호감을 보내려고 하는 상대방의 인스타 아이디에 대한 LikeablePerson 리스트를 받아옴
        List<LikeablePerson> likeablePersonList = likeablePersonRepository.findByFromInstaMemberIdAndToInstaMemberId(fromInstaMember.getId(), toInstaMember.getId());

        // 필수미션 케이스4: 중복으로 호감표시를 할 때
        if (!likeablePersonList.isEmpty()){
            // 필수 미션 케이스6
            LikeablePerson likeablePerson = likeablePersonList.get(0);

            // 기존의 호감 사유 이름
            String existingAttractiveName = likeablePerson.getAttractiveTypeDisplayName();

            // 기존의 사유와 다른 사유로 호감을 표시할 때
            if (likeablePerson.getAttractiveTypeCode() != attractiveTypeCode){
                // 호감 사유를 업데이트함
                this.updateAttractiveTypeCode(likeablePersonList.get(0), attractiveTypeCode);

                return RsData.of("S-2", String.format("%s에 대한 호감 사유를 %s에서 %s(으)로 변경합니다.",
                        likeablePerson.getToInstaMemberUsername(), existingAttractiveName,
                        likeablePersonList.get(0).getAttractiveTypeDisplayName()));
            }
            return RsData.of("F-2", String.format("%s님에게 중복으로 호감을 표시할 수 없습니다.", username));
        }

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장

        // 너가 좋아하는 호감표시 생겼어.
        fromInstaMember.addFromLikeablePerson(likeablePerson);

        // 너를 좋아하는 호감표시 생겼어.
        toInstaMember.addToLikeablePerson(likeablePerson);

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    public Optional<LikeablePerson> findById(Long id) {
        return likeablePersonRepository.findById(id);
    }

    // 삭제하면 list에 바로 반영되도록 @Transactional 어노테이션을 추가
    @Transactional
    public RsData<LikeablePerson> delete(Member member, Optional<LikeablePerson> optionalLikeablePerson){
        if(!optionalLikeablePerson.get().getFromInstaMember().equals(member.getInstaMember())){
            return RsData.of("F-1","호감을 삭제할 권한이 없습니다.");
        }

        this.likeablePersonRepository.delete(optionalLikeablePerson.get());

        return RsData.of("S-1", String.format("%s님께 보낸 호감이 삭제되었습니다.", optionalLikeablePerson.get().getToInstaMemberUsername()));
    }

    // 기존 호감 표시에서 사유만 수정되고, 수정 날짜도 지금으로 수정된다.
    @Transactional
    public void updateAttractiveTypeCode(LikeablePerson likeablePerson, Integer attractivTypeCode){
        likeablePerson.setAttractiveTypeCode(attractivTypeCode);
        likeablePerson.setModifyDate(LocalDateTime.now());
        likeablePersonRepository.save(likeablePerson);
    }
}
