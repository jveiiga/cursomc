package com.example.cursomc.domain;

import com.example.cursomc.domain.enums.EstadoPagamento;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.persistence.Entity;

import java.util.Date;


@Entity
@JsonTypeName("pagamentoComBoleto")
public class PagamentoComBoleto extends Pagamento {

    private static final long serialVersionUID = 1L;

    @JsonFormat(pattern="dd/MM/yyyy" )
    private Date dataVencimento;

    @JsonFormat(pattern="dd/MM/yyyy" )
    private Date dataPagemento;

    public PagamentoComBoleto() {
    }

    public PagamentoComBoleto(Integer id, EstadoPagamento estado, Pedido pedido, Date dataVencimento, Date dataPagemento) {
        super(id, estado, pedido);
        this.dataVencimento = dataVencimento;
        this.dataPagemento = dataPagemento;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Date getDataPagemento() {
        return dataPagemento;
    }

    public void setDataPagemento(Date dataPagemento) {
        this.dataPagemento = dataPagemento;
    }
}
