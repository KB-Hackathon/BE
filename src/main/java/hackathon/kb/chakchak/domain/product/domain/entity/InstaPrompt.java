package hackathon.kb.chakchak.domain.product.domain.entity;

import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "insta_prompt")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstaPrompt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "insta_prompt_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    /** 키워드 Top-N: [{"token":"쑥","score":0.41}, ...] */
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String topKeywords;

    /** 이모지 비율(0.0~1.0) */
    @Column(nullable = false)
    private Double emojiRatio;

    /** 해시태그 수(평균/대표값을 정수로 보관) */
    @Column(nullable = false)
    private Integer hashtagCount;

    /** 문장 길이 분포 히스토그램: {"bins":[0,20,40,...],"counts":[...]} */
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String sentenceLenHistJson;

    /** CTA 유형 분포: [{"type":"visit","ratio":0.32}, {"type":"follow","ratio":0.18}, ...] */
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String ctaTypesJson;
}