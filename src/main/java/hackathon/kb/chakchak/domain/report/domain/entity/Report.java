package hackathon.kb.chakchak.domain.report.domain.entity;

import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "report")
@NoArgsConstructor @AllArgsConstructor @Builder
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "seller_id",
		nullable = false,
		unique = true,
		foreignKey = @ForeignKey(name = "fk_report_seller")
	)
	private Seller seller;

	@Column(name = "total_sales")
	private Integer totalSales;

	@Column(name = "success_cnt")
	private Integer successCnt;

	@Column(name = "failed_cnt")
	private Integer failedCnt;

	@Column(name = "over_10")
	private Integer over10;

	@Column(name = "over_20")
	private Integer over20;

	@Column(name = "over_30")
	private Integer over30;

	@Column(name = "over_40")
	private Integer over40;

	@Column(name = "over_50")
	private Integer over50;

	@Column(name = "over_60")
	private Integer over60;

	@Column(name = "male_cnt")
	private Integer maleCnt;

	@Column(name = "female_cnt")
	private Integer femaleCnt;
}
