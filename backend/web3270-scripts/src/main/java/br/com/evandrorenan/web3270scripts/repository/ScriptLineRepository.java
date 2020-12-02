package br.com.evandrorenan.web3270scripts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.evandrorenan.web3270scripts.dao.ScriptLine;

public interface ScriptLineRepository extends JpaRepository<ScriptLine, Long> {
}