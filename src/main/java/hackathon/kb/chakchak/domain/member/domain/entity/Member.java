package hackathon.kb.chakchak.domain.member.domain.entity;

import hackathon.kb.chakchak.domain.member.domain.enums.MemberRole;
import hackathon.kb.chakchak.domain.member.domain.enums.SocialType;
import hackathon.kb.chakchak.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "member")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "inheritance_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@NoArgsConstructor @AllArgsConstructor @SuperBuilder(toBuilder = true)
public abstract class Member extends BaseEntity {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50, nullable = false)
	private String name;

	@Column(nullable = false)
	private Short age;

	@Column(length = 255, nullable = false)
	private String address;

	@Column(length = 11)
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialType social;

	@Column(length = 200, nullable = false)
	private String fcmToken;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MemberRole role;

	@Column(length = 255)
	private String storeImage;
}
