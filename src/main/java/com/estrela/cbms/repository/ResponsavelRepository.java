package com.estrela.cbms.repository;

import com.estrela.cbms.model.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {
    Optional<Responsavel> findByCpf(String cpf);
    List<Responsavel> findByNomeCompletoContainingIgnoreCaseOrCpfContaining(String nome, String cpf);
}
