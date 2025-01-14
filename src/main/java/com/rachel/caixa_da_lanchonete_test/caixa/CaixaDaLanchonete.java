package com.rachel.caixa_da_lanchonete_test.caixa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CaixaDaLanchonete {
    public String calcularTotalCompra(List<String> itens, String pagamento) {
        // Criar lista com arrays
        // Validar item
        // Validar lista item
        // Validar quantidade
        // Validar extra
        // Percorrer e somar lista valida

        
        List<Item> cardapio = this.getListCardapio();


        List<Double> valores = itens.stream().map(item -> {
            String[] codigoQntd = item.split(",");

            String codigoItem = codigoQntd[0];
            double quantidadeItem = this.tratarQuantidadeItem(codigoQntd[1]);
    

            Optional<Item> itemCorrespondenteOptional = cardapio.stream()
                .filter(itemCardapio -> itemCardapio.getCodigo().equals(codigoItem))
                .findFirst();

            if(itemCorrespondenteOptional.isEmpty()){
                throw new RuntimeException("Item inválido");
            }

            Item itemCorrespondente = itemCorrespondenteOptional.get();

            

            return itemCorrespondente.getValor() * quantidadeItem;

        }).collect(Collectors.toList());

        double total = valores.stream().reduce(0.00, (acc, valor) -> acc + valor);

        double totalFinal = verificarDescontosETaxasPagamento(pagamento, total);

        return String.format("R$ %.2f", totalFinal);
    }

    private double verificarDescontosETaxasPagamento(String pagamento, double total){
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

    protected void validarQuantidadeItensPedido(List<String> itens){
        if(itens == null || itens.size() == 0){
           throw new RuntimeException("Não há itens no carrinho de compra");
        }
    }

    protected double tratarQuantidadeItem(String quantidadeString){
        final RuntimeException exception = new RuntimeException("Quantidade inválida");

        try {
            double quantidadeDouble = Double.parseDouble(quantidadeString);

            if(quantidadeDouble <= 0){
                throw exception;
            }

            return quantidadeDouble;

        } catch (NullPointerException | NumberFormatException e){
            throw exception;
        }
    }

    protected void validarVinculoentreExtraPrincipal(List<String> itens){
    }

    public List<Item> getListCardapio(){
        return null;
    }
}
