package com.ssafy.mbting.db.entity;

import com.ssafy.mbting.api.request.MemberRegisterRequest;
import com.ssafy.mbting.db.enums.Gender;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 유저 모델 정의.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Member extends BaseEntity{

    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private String sido;
    private String mbti;
    private String profileUrl;
    @Builder.Default
    private Boolean deleted = false;

    @Builder.Default
    @OneToMany(
            mappedBy = "member"
    )
    private List<InterestMember> interestMember = new ArrayList<>();
    @Builder.Default
    @OneToMany(
            mappedBy = "toId"
    )
    private List<Message> messages = new ArrayList<>();
    @Builder.Default
    @OneToMany(
            mappedBy = "toId"
    )
    private List<Friend> friends = new ArrayList<>();

    public static Member of(MemberRegisterRequest memberRegisterRequest) {
        return Member.builder()
                .email(memberRegisterRequest.getEmail())
                .nickname(memberRegisterRequest.getNickname())
                .gender(memberRegisterRequest.getGender())
                .birth(memberRegisterRequest.getBirth())
                .sido(memberRegisterRequest.getSido())
                .mbti(memberRegisterRequest.getMbti())
                .profileUrl(memberRegisterRequest.getProfileUrl())
                .build();
    }
}
