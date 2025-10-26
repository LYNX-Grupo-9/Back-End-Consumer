package br.com.exemplo.crudadvogadoconsumer.repository;

import br.com.exemplo.crudadvogadoconsumer.model.Advogado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdvogadoRepository extends JpaRepository<Advogado, UUID> {
}
