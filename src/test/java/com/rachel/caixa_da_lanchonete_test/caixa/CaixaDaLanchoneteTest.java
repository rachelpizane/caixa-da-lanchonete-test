package com.rachel.caixa_da_lanchonete_test.caixa;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


public class CaixaDaLanchoneteTest {
    @Spy
    private CaixaDaLanchonete caixaDaLanchonete;

    private List<Item> cardapio = new ArrayList<>();;
    private List<String> itens = new ArrayList<>();;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        itens.add("cafe,2");
        itens.add("salgado,1");

        cardapio.add(Item.builder().codigo("cafe").valor(3.00).build());
        cardapio.add(Item.builder().codigo("salgado").valor(7.25).build());

        Mockito.when(caixaDaLanchonete.getListCardapio()).thenReturn(cardapio);
    }

    @ParameterizedTest
    @ValueSource(strings = { "debito", "dinheiro" })
    void dadoPagamentoInformado_quandoCalcularValorCompra_retornaValorTotalCompra(String pagamento) {
        String resultadoAtual = caixaDaLanchonete.calcularTotalCompra(itens, pagamento);
        String resultadoEsperado = "R$ 13,25";

        assertEquals(resultadoEsperado, resultadoAtual);
    }

    @Test
    void dadoPagamentoTipoPix_quandoCalcularValorCompra_retornaValorTotalCompraComDescontode5() {
        String resultadoAtual = caixaDaLanchonete.calcularTotalCompra(itens, "pix");
        String resultadoEsperado = "R$ 12,59";

        assertEquals(resultadoEsperado, resultadoAtual);
    }

    @Test
    void dadoPagamentoTipoCredito_quandoCalcularValorCompra_retornaValorTotalCompraComAcrescimo3() {
        String resultadoAtual = caixaDaLanchonete.calcularTotalCompra(itens, "credito");
        String resultadoEsperado = "R$ 13,65";

        assertEquals(resultadoEsperado, resultadoAtual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "cheque", " " })
    void dadoPagamentoInexistente_quandoCalcularValorCompra_lancaExcecao(String pagamento) {
        RuntimeException excecao = assertThrows(RuntimeException.class,
                () -> caixaDaLanchonete.calcularTotalCompra(itens, pagamento));
        String mensagemEsperada = "Forma de pagamento inválida";

        assertEquals(mensagemEsperada, excecao.getMessage());
    }

    @Test
    void dadoCodigoItemInexistente_quandoCalcularValorCompra_lancaExcecao() {
        itens.add("refrigerante,2");

        RuntimeException excecao = assertThrows(RuntimeException.class,
                () -> caixaDaLanchonete.calcularTotalCompra(itens, "debito"));
        String mensagemEsperada = "Item inválido";

        assertEquals(mensagemEsperada, excecao.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-2", "teste", ""})
    void dadoQuantidadeInvalida_quandoTratarQuantidadeItem_lancaExcecao(String quantidadeString) {
        RuntimeException excecao = assertThrows(RuntimeException.class,
        () -> caixaDaLanchonete.tratarQuantidadeItem(quantidadeString));

        String mensagemEsperada = "Quantidade inválida";
        
        assertEquals(mensagemEsperada, excecao.getMessage());
    }

    @Test
    void dadoQuantidadeNula_quandoTratarQuantidadeItem_lancaExcecao() {
        RuntimeException excecao = assertThrows(RuntimeException.class,
        () -> caixaDaLanchonete.tratarQuantidadeItem(null));

        String mensagemEsperada = "Quantidade inválida";
        
        assertEquals(mensagemEsperada, excecao.getMessage());
    }

    @Test
    void dadoItensVazio_quandoValidarQuantidadeItensPedido_lancaExcecao() {
        itens.remove(1);
        itens.remove(0);

        RuntimeException excecao = assertThrows(RuntimeException.class,
        () -> caixaDaLanchonete.validarQuantidadeItensPedido(itens));

        String mensagemEsperada = "Não há itens no carrinho de compra";
        
        assertEquals(mensagemEsperada, excecao.getMessage());
    }

    @Test
    void dadoItensNulo_quandoValidarQuantidadeItensPedido_lancaExcecao() {
        RuntimeException excecao = assertThrows(RuntimeException.class,
        () -> caixaDaLanchonete.validarQuantidadeItensPedido(null));

        String mensagemEsperada = "Não há itens no carrinho de compra";
        
        assertEquals(mensagemEsperada, excecao.getMessage());
    }

    @Test
    void dadoItemExtraSemItemPrincipal_quandoCalcularValorCompra_lancaExcecao(){
        itens.remove(0);
        itens.add("chantily,1");

        cardapio.add(Item.builder().codigo("chantily").valor(1.50).tipoItem(TipoItem.EXTRA).codigoPrincipalVinculado("cafe").build());

        RuntimeException excecao = assertThrows(RuntimeException.class,
        () -> caixaDaLanchonete.validarVinculoentreExtraPrincipal(itens));

        String mensagemEsperada = "Item extra não pode ser pedido sem o principal";

        assertEquals(mensagemEsperada, excecao.getMessage());
    }
}
