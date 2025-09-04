package hackathon.kb.chakchak.domain.member.repository;

import hackathon.kb.chakchak.domain.member.domain.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByKakaoId(Long kakaoId);

    boolean existsByKakaoId(Long kakaoId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE member
           SET inheritance_type = 'SELLER',
               role              = 'SELLER',
               biz_no                       = :bizNo,
               company_name                 = :companyName,
               rep_name                     = :repName,
               industry_business_type       = :industryBusinessType,
               biz_description              = :bizDescription,
               company_phone_number         = :companyPhoneNumber,
               zip_code                     = :zipCode,
               road_name_address            = :roadNameAddress,
               company_classification_code  = :companyClassificationCode,
                adm_cd                      = :admCd
         WHERE member_id = :memberId
        """, nativeQuery = true)

    int promoteBuyerToSeller(
            @Param("memberId") Long memberId,
            @Param("bizNo") String bizNo,
            @Param("companyName") String companyName,
            @Param("repName") String repName,
            @Param("industryBusinessType") String industryBusinessType,
            @Param("bizDescription") String bizDescription,
            @Param("companyPhoneNumber") String companyPhoneNumber,
            @Param("zipCode") String zipCode,
            @Param("roadNameAddress") String roadNameAddress,
            @Param("companyClassificationCode") String companyClassificationCode,
            @Param("admCd") String admCd
    );

    // @Modifying(clearAutomatically = true)
    // @Query("UPDATE Seller s SET s.admCd = :admCd WHERE s.id = :sellerId")
    // int updateSellerAdmCd(@Param("sellerId") Long sellerId, @Param("admCd") String admcd);
}
