package com.rachel.caixa_da_lanchonete_test.caixa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Item {
    private String codigo;
    private String descricao;
    private double valor;
}
