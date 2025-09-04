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

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Lob
    private String tags;
}