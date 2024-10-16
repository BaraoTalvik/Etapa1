/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.henriquetalvik.principal;

import com.henriquetalvik.model.DadosEpisodio;
import com.henriquetalvik.model.DadosSerie;
import com.henriquetalvik.model.DadosTemporada;
import com.henriquetalvik.model.Episodio;
import com.henriquetalvik.service.ConsumoApi;
import com.henriquetalvik.service.ConverteDados;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author henri
 */

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public void exibeMenu() {
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = leitura.nextLine();
        DadosSerie dados = buscarDadosSerie(nomeSerie);
        System.out.println(dados);

        List<DadosTemporada> temporadas = buscarDadosTemporadas(nomeSerie, dados.totalTemporadas());

        temporadas.forEach(System.out::println);

        List<Episodio> episodios = obterEpisodiosETitulos(temporadas);

        exibirResultados(episodios);
    }

    private DadosSerie buscarDadosSerie(String nomeSerie) {
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    private List<DadosTemporada> buscarDadosTemporadas(String nomeSerie, int totalTemporadas) {
        return IntStream.rangeClosed(1, totalTemporadas)
                .mapToObj(i -> consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY))
                .map(jsonTemporada -> conversor.obterDados(jsonTemporada, DadosTemporada.class))
                .collect(Collectors.toList());
    }

    private List<Episodio> obterEpisodiosETitulos(List<DadosTemporada> temporadas) {
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
        
        return temporadas.stream()
                .flatMap(t -> t.episodios().stream().map(e -> new Episodio(t.numero(), e)))
                .collect(Collectors.toList());
    }

    private void exibirResultados(List<Episodio> episodios) {
        episodios.forEach(System.out::println);//Não esquecer que precisa de :: para não dar pau

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics estatisticas = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + estatisticas.getAverage());
        System.out.println("Melhor episódio: " + estatisticas.getMax());
        System.out.println("Pior episódio: " + estatisticas.getMin());
        System.out.println("Quantidade: " + estatisticas.getCount());
    }
}

//Provavelmente não há necessidade de extrair as lambdas para métodos auxiliares
//considerando que ele já é conciso, claro e as lambdas não são complexas.
