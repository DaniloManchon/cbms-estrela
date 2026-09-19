package com.estrela.cbms.repository;

import com.estrela.cbms.model.Beneficiario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiarioRepository extends MongoRepository<Beneficiario, String> {
    Optional<Beneficiario> findByCpf(String cpf);
    Optional<Beneficiario> findByCodigoBarras(String codigoBarras);
    List<Beneficiario> findByNomeCompletoContainingIgnoreCaseOrCpfContainingOrCodigoBarrasContaining(String nome, String cpf, String codigoBarras);
}
