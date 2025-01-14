package com.rachel.caixa_da_lanchonete_test.caixa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Item {
    private String codigo;
    private TipoItem tipoItem;
    private String codigoPrincipalVinculado;
    private String descricao;
    private double valor;

    
    public void setCodigoPrincpalVinculado(String codigoPrincipalVinculado){
        if(this.tipoItem == TipoItem.EXTRA){
            this.codigoPrincipalVinculado = codigoPrincipalVinculado;
        }
    }
}
