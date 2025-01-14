package com.rachel.caixa_da_lanchonete_test.caixa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ItemCarrinho {
    private String codigo;
    private int quantidade;
}
