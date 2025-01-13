package com.rachel.caixa_da_lanchonete_test.caixa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaixaDaLanchonete {
    private static final Logger logger = LoggerFactory.getLogger(CaixaDaLanchonete.class);

    public String calcularTotalCompra(List<String> itens, String pagamento) {
        // Acessar cada item da lista
        // Transformar em um array a apartir da virgula.
        // Achar o valor corresponde o seu codigo
        // Multiplicar pela quantidade
        // Colocar em uma variavel de soma.

        List<Item> cardapio = this.getListCardapio();

        List<Double> valores = itens.stream().map(item -> {
            String[] codigoQntd = item.split(",");

            String codigoItem = codigoQntd[0];
            double quantidadeItem = Double.parseDouble(codigoQntd[1]);

            Optional<Item> itemCorrespondente = cardapio.stream()
                .filter(itemCardapio -> itemCardapio.getCodigo().equals(codigoItem))
                .findFirst();

            if(itemCorrespondente.isEmpty()){
                throw new RuntimeException("Erro");
            }

            return itemCorrespondente.get().getValor() * quantidadeItem;

        }).collect(Collectors.toList());

        double total = valores.stream().reduce(0.00, (acc, valor) -> acc + valor);

        if(pagamento.equals("pix")) {
            total = total * 0.95;
        }

        return String.format("R$ %.2f", total);
    }

    public List<Item> getListCardapio(){
        return null;
    }
}
