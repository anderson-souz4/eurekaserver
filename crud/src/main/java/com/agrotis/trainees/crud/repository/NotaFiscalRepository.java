package com.agrotis.trainees.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrotis.trainees.crud.entity.CabecalhoNota;
import com.agrotis.trainees.crud.entity.enums.TipoNota;

import java.util.Optional;

@Repository
public interface NotaFiscalRepository extends JpaRepository<CabecalhoNota, Integer> {


}