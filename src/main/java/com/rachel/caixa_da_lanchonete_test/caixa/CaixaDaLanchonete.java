package com.rachel.caixa_da_lanchonete_test.caixa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CaixaDaLanchonete {
    public String calcularTotalCompra(List<String> itens, String pagamento) {

        this.validarQuantidadeItensPedido(itens);

        List<ItemCardapio> cardapio = this.getListCardapio();
        List<ItemCarrinho> itensTratado = this.tratarListaItens(itens);

        List<Double> totalPorItem = itensTratado.stream().map(itemCompra -> {
            ItemCardapio itemCardapio; 

            Optional<ItemCardapio> itemCardapioOptional = cardapio.stream()
                .filter(itemCardapioPercorrido -> itemCardapioPercorrido.getCodigo().equals(itemCompra.getCodigo()))
                .findFirst();

            if(itemCardapioOptional.isEmpty()){
                throw new RuntimeException("Item inválido");
            }

            itemCardapio = itemCardapioOptional.get();

            this.validarVinculoentreExtraPrincipal(itensTratado, itemCardapio);

            return itemCardapio.getValor() * itemCompra.getQuantidade();
        }).collect(Collectors.toList());

        double totalCompra = totalPorItem.stream().reduce(0.00, (acc, valor) -> acc + valor);
        double totalCompraFinal = tratarDescontosETaxasPagamento(pagamento, totalCompra);

        return String.format("R$ %.2f", totalCompraFinal);
    }

    public List<ItemCardapio> getListCardapio(){
        return null;
    }

    private double tratarDescontosETaxasPagamento(String pagamento, double total){
        double totalFinal;

        switch (pagamento) {
            case "pix":
                double descontoPorc = 0.05;
                totalFinal = total * (1 - descontoPorc);
                break;

            case "credito":
                double acrescimoPorc = 0.03;
                totalFinal = total * (1 + acrescimoPorc);
                break;

            case "debito":
            case "dinheiro":
                totalFinal = total;
                break;

            default:
                throw new RuntimeException("Forma de pagamento inválida");
        }

        return totalFinal;
    }

    protected int tratarQuantidadeItem(String quantidadeString){
        final RuntimeException exception = new RuntimeException("Quantidade inválida");

        try {
            int  quantidadeInt = Integer.parseInt(quantidadeString);

            if(quantidadeInt <= 0){
                throw exception;
            }

            return quantidadeInt;

        } catch (NullPointerException | NumberFormatException e){
            throw exception;
        }
    }

    protected List<ItemCarrinho> tratarListaItens(List<String> itens) {
        List<ItemCarrinho> itensTratado = itens.stream().map(item -> {
            String[] codigoQntd = item.split(",");

            String codigoItem = codigoQntd[0];
            int quantidadeItem = this.tratarQuantidadeItem(codigoQntd[1]);

            return new ItemCarrinho(codigoItem, quantidadeItem);
        }).collect(Collectors.toList());

        return itensTratado;
    }

    protected void validarQuantidadeItensPedido(List<String> itens){
        if(itens == null || itens.size() == 0){
           throw new RuntimeException("Não há itens no carrinho de compra");
        }
    }

    protected void validarVinculoentreExtraPrincipal(List<ItemCarrinho> itensTratados, ItemCardapio itemCardapio){
        if(itemCardapio.getTipoItem() == TipoItem.EXTRA){
            boolean existeItemPrincipal = itensTratados.stream().anyMatch(item -> {
                return itemCardapio.getCodigoPrincipalVinculado().equals(item.getCodigo());
            });

            if(!existeItemPrincipal){
                throw new RuntimeException("Item extra não pode ser pedido sem o principal");
            }
        }
    }
}
