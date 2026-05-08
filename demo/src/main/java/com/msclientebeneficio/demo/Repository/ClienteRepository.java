package com.msclientebeneficio.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.msclientebeneficio.demo.Model.Cliente;

@Repository
public interface ClienteRepository  extends JpaRepository<Cliente, String>{

    Cliente findByRun(String run);

    void deleteByRun(String run, String dv);

    boolean existsByRun(String run);

}
