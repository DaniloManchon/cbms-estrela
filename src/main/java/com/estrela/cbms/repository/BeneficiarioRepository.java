package com.estrela.cbms.repository;

import com.estrela.cbms.model.Beneficiario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {
    Optional<Beneficiario> findByCpf(String cpf);
    Optional<Beneficiario> findByCodigoBarras(String codigoBarras);
    List<Beneficiario> findByNomeCompletoContainingIgnoreCaseOrCpfContainingOrCodigoBarrasContaining(String nome, String cpf, String codigoBarras);
}
