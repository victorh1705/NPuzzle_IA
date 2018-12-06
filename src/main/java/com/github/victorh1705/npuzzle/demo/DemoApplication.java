package com.github.victorh1705.npuzzle.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);


        Puzzle puzzle = new Puzzle();

        puzzle.embaralhar();
        puzzle.buscaLargura(false);
//        puzzle.aEstrela();
        System.out.println("acabou");
        System.out.println("Custo da Solucao : " + puzzle.getCustoSolucao());
        System.out.println("Numero de Passos : " + puzzle.getPassosSolucao());
    }
}
