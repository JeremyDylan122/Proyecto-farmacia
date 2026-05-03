package com.farmacia.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.farmacia.model.Proveedor;


@Repository
public interface RepositorioProveedor extends JpaRepository<Proveedor, String> { 
    // PUBLIC-INTERFAZ SE PUEDE USAR DE OTRAS CLASES
    // INTERFACE ES UN CONTRATO, SPRING CREA LA LOGICA POR TI
    //JPA REPOSITORY: PROPOPRCIONA METODOS CRUD BASICOS, NO HAY QUE IMPLEMENTAR NADA, SOLO DECLARAR INTERFACE (PROVEEDOR ES ENTIDAD O TABLA) Y ( sTRING LA CLAVE PRIMARIA)

    List<Proveedor> findByNombreContainingIgnoreCase(String nombre); //BUSCA POR NOMBRE, IGNORA MAYUSCULAS O MINUSCULAS

    List<Proveedor> findByDireccionContaining(String direccion); //BUSCA POR DIRECCION

    Optional<Proveedor> findByEmail(String email); //BUSCA POIR EMAIL

    boolean existsByEmail(String email); //VALIDA REPETIDO RETORNA TRUE O FALSE


//VS CODE CON SPRINGBOOT /JAVA DETECTO UN METODO DERIBADO DE SPRING DATA JPA, OFRECE AYUDA VISUAL
//metodos de JPA EL EDITOR RECONOCE ESO Y OFRECE LA IMPLEMENTACION GENERADA
//INTERNAMENTE GENERA ALGO COMO: SQL: SELECT * FROM PROVEEDORES WHERE NOMBRE LIKE LOWE('%TEXTO%')
}
