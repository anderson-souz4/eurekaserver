package com.agrotis.trainees.crud.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;

import com.agrotis.trainees.crud.entity.enums.TipoNota;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cabecalho_nota")
public class CabecalhoNota{

    public CabecalhoNota() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull(message = "Tipo da nota fiscal não pode ser nulo.")
    @Column(name = "nota_fiscal_tipo")
    @Enumerated(EnumType.STRING)
    private TipoNota notaFiscalTipo;

    @ManyToOne
    @JoinColumn(name = "parceiro_de_negocio_id")
    @NotNull(message = "Informe um parceiro de negócio.")
    private ParceiroNegocio parceiroNegocio;

    @NotNull(message = "Informe o numero da nota.")
    @Column(name = "numero", unique = true)
    private Integer numero;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @PastOrPresent
    @NotNull(message = "Preencha o campo data.")
    private LocalDate data;

    @JsonIgnore
    @OneToMany(mappedBy = "cabecalhoNota",orphanRemoval = true)
    private List<ItemNota> itens = new ArrayList<>();

    @DecimalMin(value = "00.00", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    public TipoNota getNotaFiscalTipo() {
        return notaFiscalTipo;
    }

    public void setNotaFiscalTipo(TipoNota notaFiscalTipo) {
        this.notaFiscalTipo = notaFiscalTipo;
    }

    public ParceiroNegocio getParceiroNegocio() {
        return parceiroNegocio;
    }

    public void setParceiroNegocio(ParceiroNegocio parceiroNegocio) {
        this.parceiroNegocio = parceiroNegocio;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public List<ItemNota> getItens() {
        return itens;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CabecalhoNota other = (CabecalhoNota) obj;
        return Objects.equals(id, other.id);
    }

    public void setItens(List<ItemNota> asList) {
        this.itens.add((ItemNota) asList);
    }

}
