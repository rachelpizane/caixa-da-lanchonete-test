package com.rachel.caixa_da_lanchonete_test.caixa;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CaixaDaLanchoneteTest {
    @Spy
    private CaixaDaLanchonete caixaDaLanchonete;

    private List<ItemCardapio> cardapio = new ArrayList<>();;
    private List<String> itens = new ArrayList<>();;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        itens.add("suco,2");
        itens.add("salgado,1");
        
        cardapio.add(ItemCardapio.builder().codigo("suco").valor(6.20).build());
        cardapio.add(ItemCardapio.builder().codigo("cafe").valor(3.00).build());
        cardapio.add(ItemCardapio.builder().codigo("salgado").valor(7.25).build());
        cardapio.add(ItemCardapio.builder().codigo("combo1").valor(9.50).build());
        cardapio.add(ItemCardapio.builder().codigo("combo2").valor(7.50).build());
        cardapio.add(ItemCardapio.builder().codigo("sanduiche").valor(6.50).build());
        cardapio.add(ItemCardapio.builder().codigo("chantily").valor(1.50).tipoItem(TipoItem.EXTRA).codigoPrincipalVinculado("cafe").build());
        cardapio.add(ItemCardapio.builder().codigo("queijo").valor(2.00).tipoItem(TipoItem.EXTRA).codigoPrincipalVinculado("sanduiche").build());


        Mockito.when(caixaDaLanchonete.getListCardapio()).thenReturn(cardapio);
    }

    @ParameterizedTest
    @ValueSource(strings = { "debito", "dinheiro" })
    void dadoPagamentoInformado_quandoCalcularValorCompra_retornaValorTotalCompra(String pagamento) {
        String resultadoAtual = caixaDaLanchonete.calcularTotalCompra(itens, pagamento);
        String resultadoEsperado = "R$ 19,65";

        assertEquals(resultadoEsperado, resultadoAtual);
    }

    @Test
    void dadoPagamentoTipoPix_quandoCalcularValorCompra_retornaValorTotalCompraComDescontode5() {
        String resultadoAtual = caixaDaLanchonete.calcularTotalCompra(itens, "pix");
        String resultadoEsperado = "R$ 18,67";

        assertEquals(resultadoEsperado, resultadoAtual);
    }

    @ParameterizedTest
    @CsvSource({
        "'combo1,2','suco,3','35,72'",
        "'cafe,1','chantily,2','5,70'",
        "'sanduiche,2','queijo,3','18,05'",
        "'salgado,2','sanduiche,5','44,65'",
    })
    void dadoItensInformadosEPagamentoTipoPix_quandoCalcularValorCompra_retornaValorTotalCompraComDescontode5(String item1, String item2, String valorTotal) {
        itens.clear();

        itens.add(item1);
        itens.add(item2);

        String resultadoAtual = caixaDaLanchonete.calcularTotalCompra(itens, "pix");
        String resultadoEsperado = "R$ " + valorTotal;

        assertEquals(resultadoEsperado, resultadoAtual);
    }

    @Test
    void dadoPagamentoTipoCredito_quandoCalcularValorCompra_retornaValorTotalCompraComAcrescimo3() {
        String resultadoAtual = caixaDaLanchonete.calcularTotalCompra(itens, "credito");
        String resultadoEsperado = "R$ 20,24";

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
    @ValueSource(strings = { "0", "-2", "teste", "" })
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

    @ParameterizedTest
    @ValueSource(strings = {"chantily", "queijo" })
    void dadoItemExtraSemItemPrincipal_quandoValidarVinculoentreExtraPrincipal_lancaExcecao(String codigoItemExtra) {
        itens.add(codigoItemExtra + ",1");

        List<ItemCarrinho> itensTratados = caixaDaLanchonete.tratarListaItens(itens);

        ItemCardapio itemCardapio = cardapio.stream()
            .filter(item -> item.getCodigo().equals(codigoItemExtra))
            .findAny().get();

        RuntimeException excecao = assertThrows(RuntimeException.class,
                () -> caixaDaLanchonete.validarVinculoentreExtraPrincipal(itensTratados, itemCardapio));
        String mensagemEsperada = "Item extra não pode ser pedido sem o principal";

        assertEquals(mensagemEsperada, excecao.getMessage());
    }

    @Test
    void dadoItemExtraComItemComboSemItemPrincipal_quandoValidarVinculoentreExtraPrincipal_lancaExcecao() {
        String codigoItemExtra = "queijo";

        itens.add("combo2,1");
        itens.add(codigoItemExtra + ",1");


        List<ItemCarrinho> itensTratados = caixaDaLanchonete.tratarListaItens(itens);

        ItemCardapio itemCardapio = cardapio.stream()
            .filter(item -> item.getCodigo().equals(codigoItemExtra))
            .findAny().get();

        RuntimeException excecao = assertThrows(RuntimeException.class,
                () -> caixaDaLanchonete.validarVinculoentreExtraPrincipal(itensTratados, itemCardapio));
        String mensagemEsperada = "Item extra não pode ser pedido sem o principal";

        assertEquals(mensagemEsperada, excecao.getMessage());
    }

    @Test 
    void dadoItemPrincipalComMaisUmItemExtra_quandoCalcularValorCompra_retornaValorTotalCompra(){
        itens.clear();
        itens.add("cafe,1");
        itens.add("chantily,2");

        String resultadoAtual = caixaDaLanchonete.calcularTotalCompra(itens, "debito");
        String resultadoEsperado = "R$ 6,00";

        assertEquals(resultadoEsperado, resultadoAtual);
    }
}
