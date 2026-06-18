package com.inventario.msinventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.msinventario.model.Movimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento,Long>{

}
