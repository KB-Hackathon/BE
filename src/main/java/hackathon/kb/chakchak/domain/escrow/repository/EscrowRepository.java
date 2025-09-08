package hackathon.kb.chakchak.domain.escrow.repository;

import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.stereotype.Repository;

import hackathon.kb.chakchak.domain.escrow.domain.entity.Escrow;

@Repository
public interface EscrowRepository extends JpaAttributeConverter<Escrow, Long> {
}
