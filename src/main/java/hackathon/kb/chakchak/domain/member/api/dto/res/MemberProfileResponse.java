package hackathon.kb.chakchak.domain.member.api.dto.res;

import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.domain.enums.Gender;
import hackathon.kb.chakchak.domain.member.domain.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberProfileResponse {
    private Long id;
    private String name;
    private Short age;
    private Gender gender;
    private String address;
    private String phoneNumber; // 선택
    private MemberRole role;
    private String image; // 선택

    public static MemberProfileResponse from(Member m) {
        return MemberProfileResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .age(m.getAge())
                .gender(m.getGender())
                .address(m.getAddress())
                .phoneNumber(m.getPhoneNumber())
                .role(m.getRole())
                .image(m.getImage())
                .build();
    }
}

