package com.github.victorh1705.npuzzle.demo;

import com.github.victorh1705.npuzzle.demo.enumeration.ETipoBusca;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Puzzle {
    private int linhas = 3;
    private int colunas = 3;
    private int nosExpandidos = 0;
    private int nosVisitados = 0;
    private int profundidade = 0;
    private int profundidadeAtual = 0;
    private int idPaiAtual = 0;
    private int indiceMenorDistManhattan = -1;
    private int valorMenorDistancia = 1000;
    private int qtdTrocasEmbaralhamento = 50;
    private int posicaoVazia;
    private int constanteDeSobra = 1000000;
    private int passosSolucao = 0;
    private int passoAtual = 0;
    private int passosRestantes = 0;
    private int fatorMultiplicacao = 1;
    private double custoSolucao = 0.0, tempoExecucao = 0.0, fatorRamificacao = 0.0;
    boolean resultadoObtido = false;
    boolean embaralhado = false;
    boolean rodando = false;
    private boolean voltouProPai = false;
    private List<Integer> valores;
    private List<Integer> valoresBackup;
    private List<Integer> auxVerificaLoop;
    private Date tempoInicial, tempoFinal;
    private List<Item> estadosVisitados;
    int caminhoSolucao = constanteDeSobra;
    private int posicaoAtualVisitados = 0;

    public Puzzle() {
        inicializa();
    }

    private void inicializa() {
        valores = new ArrayList<>(linhas * colunas);
        valoresBackup = new ArrayList<>(linhas * colunas);
        auxVerificaLoop = new ArrayList<>(linhas * colunas);
        estadosVisitados = new ArrayList<Item>(linhas * colunas * constanteDeSobra *
                fatorMultiplicacao);
    }

    public int getLinhas() {
        return linhas;
    }

    public void setLinhas(int linhas) {
        this.linhas = linhas;
    }

    public int getColunas() {
        return colunas;
    }

    public void setColunas(int colunas) {
        this.colunas = colunas;
    }

    public List<Integer> getValores() {
        return valores;
    }

    public void setValores(List<Integer> valores) {
        this.valores = valores;
    }

    void backtracking() {
        idPaiAtual = 0;
        profundidade = 0;
        profundidadeAtual = 0;
        voltouProPai = false;

        System.out.print("Executando backtracking, aguarde...\n");
//        tempoInicial = new Date();
        int movimentoRealizado;
        boolean fracasso = false, sucesso = false;
        rodando = true;
        limpaEstadosVisitados();
        adicionaNovoEstadoVisitado(-1);
        if (verificaSolucao())
            sucesso = true;
        while (!sucesso && !fracasso) {
            if ((posicaoAtualVisitados % 25000) == 0)
                System.out.print("Aguarde mais um pouco...\n");
            movimentoRealizado = escolherRegra();
            if (movimentoRealizado != -1) {
                if (!voltouProPai)
                    idPaiAtual = posicaoAtualVisitados;
                else
                    voltouProPai = false;
                adicionaNovoEstadoVisitado(idPaiAtual);
                if (verificaSolucao()) {
                    sucesso = true;
                }
            } else {
                if (verificaFracasso())
                    fracasso = true;
                else
                    voltaProPai();
            }
        }
//        tempoFinal = new Date();
        resultadoObtido = true;
        rodando = false;
        embaralhado = false;
        nosExpandidos = posicaoAtualVisitados;
        nosVisitados = posicaoAtualVisitados + 1;
        fatorRamificacao = 1.00;
        fatorMultiplicacao = 1;
//        tempoExecucao=(tempoFinal-tempoInicial)/1000.0;
        //system("cls");

    }

    void buscaLargura(boolean largura) {
        profundidade = 0;
        profundidadeAtual = 0;

        if (largura)
            System.out.print("Executando busca em largura, aguarde...\n");
        else
            System.out.print("Executando busca ordenada, aguarde...\n");
//        tempoInicial = new Date();
        boolean fracasso = false, sucesso = false;
        rodando = true;
        limpaEstadosVisitados();
        adicionaPaiListaAbertos(-1, false);
        while (!sucesso && !fracasso) {
            if ((posicaoAtualVisitados % 25000) == 0)
                System.out.print("Aguarde mais um pouco...\n");
            if (verificaSolucao())
                sucesso = true;
            else {
                if (!obterProximoEstado(ETipoBusca.LARGURA))
                    fracasso = true;
            }
        }
//        tempoFinal = new Date();
        resultadoObtido = true;
        rodando = false;
        embaralhado = false;
        nosExpandidos = posicaoAtualVisitados;
        nosVisitados = contaEstadosFechados() + 1;
//        tempoExecucao=(tempoFinal-tempoInicial)/1000.0;
        fatorRamificacao = (float) nosExpandidos / (float) nosVisitados;
        fatorMultiplicacao = 1;
        profundidade--;
        //system("cls");

    }

    void buscaProfundidade() {
        profundidade = 0;
        profundidadeAtual = 0;

        System.out.print("Executando busca em profundidade, aguarde...\n");
        tempoInicial = new Date();
        boolean fracasso = false, sucesso = false;
        rodando = true;
        limpaEstadosVisitados();
        adicionaPaiListaAbertos(-1, true);
        while (!sucesso && !fracasso) {
            if ((posicaoAtualVisitados % 25000) == 0)
                System.out.print("Aguarde mais um pouco...\n");
            if (verificaSolucao())
                sucesso = true;
            else {
                if (!obterProximoEstado(ETipoBusca.PROFUNDIDADE))
                    fracasso = true;
            }
        }
        tempoFinal = new Date();
        resultadoObtido = true;
        rodando = false;
        embaralhado = false;
        nosExpandidos = posicaoAtualVisitados;
        nosVisitados = contaEstadosFechados() + 1;
//        tempoExecucao=(tempoFinal-tempoInicial)/1000.0;
        fatorRamificacao = (float) nosExpandidos / (float) nosVisitados;
        fatorMultiplicacao = 1;
        profundidade--;

    }

    void buscaOrdenada() {
        buscaLargura(false);
    }

    void buscaGulosa() {
        profundidade = 0;
        profundidadeAtual = 0;
        indiceMenorDistManhattan = -1;
        valorMenorDistancia = 1000;

        System.out.print("Executando busca gulosa, aguarde...\n");
        tempoInicial = new Date();
        boolean fracasso = false, sucesso = false;
        rodando = true;
        limpaEstadosVisitados();
        adicionaPaiListaAbertos(-1, false);
        while (!sucesso && !fracasso) {
            if ((posicaoAtualVisitados % 25000) == 0)
                System.out.print("Aguarde mais um pouco...\n");
            if (verificaSolucao())
                sucesso = true;
            else {
                if (!obterProximoEstado(ETipoBusca.GULOSA))
                    fracasso = true;
            }
        }
        tempoFinal = new Date();
        resultadoObtido = true;
        rodando = false;
        embaralhado = false;
        nosExpandidos = posicaoAtualVisitados;
        nosVisitados = contaEstadosFechados() + 1;
//        tempoExecucao=(tempoFinal-tempoInicial)/1000.0;
        fatorRamificacao = (float) nosExpandidos / (float) nosVisitados;
        fatorMultiplicacao = 1;
        profundidade--;

    }

    void aEstrela() {
        profundidade = 0;
        profundidadeAtual = 0;
        indiceMenorDistManhattan = -1;
        valorMenorDistancia = 1000;

        System.out.print("Executando A*, aguarde...\n");
        tempoInicial = new Date();
        boolean fracasso = false, sucesso = false;
        rodando = true;
        limpaEstadosVisitados();
        adicionaPaiListaAbertos(-1, false);
        while (!sucesso && !fracasso) {
            if ((posicaoAtualVisitados % 25000) == 0)
                System.out.print("Aguarde mais um pouco...\n");
            if (verificaSolucao())
                sucesso = true;
            else {
                if (!obterProximoEstado(ETipoBusca.AESTRELA))
                    fracasso = true;
            }
        }
        tempoFinal = new Date();
        resultadoObtido = true;
        rodando = false;
        embaralhado = false;
        nosExpandidos = posicaoAtualVisitados;
        nosVisitados = contaEstadosFechados() + 1;
//        tempoExecucao = (tempoFinal - tempoInicial) / 1000.0;
        fatorRamificacao = (float) nosExpandidos / (float) nosVisitados;
        fatorMultiplicacao = 1;
        profundidade--;

    }

    boolean obterProximoEstado(ETipoBusca tipoBusca) {
        switch (tipoBusca) {
            case LARGURA:
                for (int i = 0; i < posicaoAtualVisitados; i++) {
                    Item _localVisitado = estadosVisitados.get(i);
                    if (_localVisitado.isAtivo()) {
                        for (int j = 0; j < (linhas * colunas); j++) {
                            valores.add(j, _localVisitado.getEstado().get(j));
                            if (valores.get(j) == -1)
                                posicaoVazia = j;
                        }
                        _localVisitado.setAtivo(false);
                        profundidadeAtual = _localVisitado.getProfundidade() + 1;
                        if (profundidade < profundidadeAtual)
                            profundidade = profundidadeAtual;
                        if ((posicaoAtualVisitados + 100) >
                                (linhas * colunas * fatorMultiplicacao * constanteDeSobra)) {
                            aumentaTamanhoVetorVisitados();
                        }
                        adicionaFilhosListaAbertos(_localVisitado.getId(), false);
                        return true;
                    }
                }
                break;
            case PROFUNDIDADE:
                for (int i = posicaoAtualVisitados - 1; i >= 0; i--) {
                    if (estadosVisitados.get(i).isAtivo()) {
                        for (int j = 0; j < (linhas * colunas); j++) {
                            valores.add(j, estadosVisitados.get(i).getEstado().get(j));
                            if (valores.get(j) == -1)
                                posicaoVazia = j;
                        }
                        estadosVisitados.get(i).setAtivo(false);
                        profundidadeAtual = estadosVisitados.get(i).getProfundidade() + 1;
                        if (profundidade < profundidadeAtual)
                            profundidade = profundidadeAtual;
                        if ((posicaoAtualVisitados + 100) >
                                (linhas * colunas * fatorMultiplicacao * constanteDeSobra)) {
                            aumentaTamanhoVetorVisitados();
                        }
                        adicionaFilhosListaAbertos(estadosVisitados.get(i).getId(), true);
                        return true;
                    }
                }
                break;
            case GULOSA:
                if (indiceMenorDistManhattan > -1) {
                    for (int j = 0; j < (linhas * colunas); j++) {
                        valores.add(j, estadosVisitados.get(indiceMenorDistManhattan).getEstado().get(j));
                        if (valores.get(j) == -1)
                            posicaoVazia = j;
                    }
                    estadosVisitados.get(indiceMenorDistManhattan).setAtivo(false);
                    profundidadeAtual = estadosVisitados.get(indiceMenorDistManhattan).getProfundidade() + 1;
                    if (profundidade < profundidadeAtual)
                        profundidade = profundidadeAtual;
                    if ((posicaoAtualVisitados + 100) > (linhas * colunas * fatorMultiplicacao * constanteDeSobra)) {
                        aumentaTamanhoVetorVisitados();
                    }
                    int idAtual = estadosVisitados.get(indiceMenorDistManhattan).getId();
                    indiceMenorDistManhattan = -1;
                    valorMenorDistancia = 1000;
                    adicionaFilhosListaAbertos(idAtual, false);
                    if (indiceMenorDistManhattan == -1) {
                        for (int i = 0; i < posicaoAtualVisitados; i++) {
                            if (estadosVisitados.get(i).isAtivo()) {
                                if (estadosVisitados.get(i).getDistManhattan() < valorMenorDistancia) {
                                    valorMenorDistancia = estadosVisitados.get(i).getDistManhattan();
                                    indiceMenorDistManhattan = i;
                                }
                            }
                        }
                    }
                    return true;
                }
                break;
            case AESTRELA:
                if (indiceMenorDistManhattan > -1) {
                    for (int j = 0; j < (linhas * colunas); j++) {
                        valores.add(j, estadosVisitados.get(indiceMenorDistManhattan).getEstado().get(j));
                        if (valores.get(j) == -1)
                            posicaoVazia = j;
                    }
                    estadosVisitados.get(indiceMenorDistManhattan).setAtivo(false);
                    profundidadeAtual = estadosVisitados.get(indiceMenorDistManhattan).getProfundidade() + 1;
                    if (profundidade < profundidadeAtual)
                        profundidade = profundidadeAtual;
                    if ((posicaoAtualVisitados + 100) > (linhas * colunas * fatorMultiplicacao * constanteDeSobra)) {
                        aumentaTamanhoVetorVisitados();
                    }
                    int idAtual = estadosVisitados.get(indiceMenorDistManhattan).getId();
                    indiceMenorDistManhattan = -1;
                    valorMenorDistancia = 1000;
                    adicionaFilhosListaAbertos(idAtual, false);
                    int indiceAtual = indiceMenorDistManhattan;
                    indiceMenorDistManhattan = -1;
                    valorMenorDistancia = 1000;
                    for (int i = 0; i < posicaoAtualVisitados; i++) {
                        if (estadosVisitados.get(i).isAtivo() &&
                                estadosVisitados.get(i).getProfundidade() <
                                        estadosVisitados.get(idAtual - 1).getProfundidade()) {
                            if (estadosVisitados.get(i).getDistManhattan() < valorMenorDistancia) {
                                valorMenorDistancia = estadosVisitados.get(i).getDistManhattan();
                                indiceMenorDistManhattan = i;
                            }
                        }
                    }
                    if (indiceMenorDistManhattan == -1)
                        indiceMenorDistManhattan = indiceAtual;
                    if (indiceMenorDistManhattan == -1) {
                        for (int i = 0; i < posicaoAtualVisitados; i++) {
                            if (estadosVisitados.get(i).isAtivo() &&
                                    estadosVisitados.get(i).getProfundidade() <=
                                            estadosVisitados.get(idAtual - 1).getProfundidade()) {
                                if (estadosVisitados.get(i).getDistManhattan() < valorMenorDistancia) {
                                    valorMenorDistancia = estadosVisitados.get(i).getDistManhattan();
                                    indiceMenorDistManhattan = i;
                                }
                            }
                        }
                    }
                    if (indiceMenorDistManhattan == -1) {
                        for (int i = 0; i < posicaoAtualVisitados; i++) {
                            if (estadosVisitados.get(i).isAtivo() && estadosVisitados.get(i).getProfundidade() <=
                                    estadosVisitados.get(idAtual - 1).getProfundidade() + 1) {
                                if (estadosVisitados.get(i).getDistManhattan() < valorMenorDistancia) {
                                    valorMenorDistancia = estadosVisitados.get(i).getDistManhattan();
                                    indiceMenorDistManhattan = i;
                                }
                            }
                        }
                    }
                    return true;
                }
                break;

        }
        return false;
    }

    void voltaProPai() {
        int idPai = -1;
        for (int i = posicaoAtualVisitados - 1; i >= 0 && idPai == -1; i--) {
            if (estadosVisitados.get(i).isAtivo()) {
                idPai = estadosVisitados.get(i).getIdPai();
                estadosVisitados.get(i).setAtivo(false);
            }
        }
        for (int i = posicaoAtualVisitados - 1; i >= 0; i--) {
            if (estadosVisitados.get(i).getId() == idPai) {
                for (int j = 0; j < linhas * colunas; j++) {
                    valores.add(j, estadosVisitados.get(i).getEstado().get(j));
                    if (valores.get(j) == -1)
                        posicaoVazia = j;
                }
            }
        }
        idPaiAtual = idPai;
        voltouProPai = true;
    }

    int escolherRegra() {
        int regras[] = {
                0, 2, 3, 1
        };
        for (int i = 0; i < 4; i++) {
            if (realizaMovimento(regras[i]))
                return regras[i];
        }
        return -1;
    }

    void adicionaPaiListaAbertos(int idPai, boolean buscaProfundidade) {
        Item novoEstado = new Item(posicaoAtualVisitados + 1, valores, idPai, 0,
                -1, false);
        estadosVisitados.add(posicaoAtualVisitados, novoEstado);
        posicaoAtualVisitados++;
        profundidadeAtual = 1;
        adicionaFilhosListaAbertos(posicaoAtualVisitados, buscaProfundidade);
    }

    void adicionaFilhosListaAbertos(int idPai, boolean buscaProfundidade) {
        int regras[] = {
                0, 2, 3, 1
        };
        if (buscaProfundidade) {
            regras[0] = 1;
            regras[1] = 3;
            regras[2] = 2;
            regras[3] = 0;
        }
        for (int i = 0; i < 4; i++)
            geraFilho(regras[i], idPai);
    }

    void adicionaNovoEstadoVisitado(int idPai) {
        int profundidade;
        if (idPai == -1)
            profundidade = 0;
        else
            profundidade = estadosVisitados.get(idPai - 1).getProfundidade() + 1;

        Item novoEstado = new Item(posicaoAtualVisitados + 1, null, idPai, 0,
                0, true);
        estadosVisitados.add(posicaoAtualVisitados, novoEstado);
        //System.out.printf("id: %d , idPai: %d , profundidade: %d , estado : ",novoEstado.getId(),
        // novoEstado.getId()Pai, novoEstado.getProfundidade());
        for (int i = 0; i < linhas * colunas; i++) {
            novoEstado.getEstado().add(i, valores.get(i));
            //System.out.printf("%d ",novoEstado.getEstado().get(i));
        }
        //System.out.printf("\n");
        posicaoAtualVisitados++;
        if (novoEstado.getProfundidade() > profundidade)
            profundidade = novoEstado.getProfundidade();
        if ((posicaoAtualVisitados + 100) > (linhas * colunas * fatorMultiplicacao * constanteDeSobra)) {
            aumentaTamanhoVetorVisitados();
        }

    }


    void embaralhar() {
        limpaEstadosVisitados();
        ordenar();
        for (int j = 0; j < qtdTrocasEmbaralhamento; j++) {
            if (!realizaMovimento((int) (Math.random() * 100) % 4))
                j--;
        }
        if (verificaSolucao())
            while (!realizaMovimento((int) (Math.random() * 100) % 4)) ;
    /*valores[0]=4;
    valores[1]=-1;
    valores[2]=1;
    valores[3]=3;
    valores[4]=2;
    valores[5]=5;
    posicaoVazia=1;*/
        for (int i = 0; i < linhas * colunas; i++) {
            valoresBackup.add(i, valores.get(i));
        }
        embaralhado = true;
        resultadoObtido = false;
    }

    void ordenar() {
        inicializa();
        resultadoObtido = false;
        embaralhado = false;
        valores.clear();
        valores = new ArrayList<>(linhas * colunas);
        int i;
        for (i = 0; i < (linhas * colunas) - 1; i++) {
            valores.add(i, i + 1);
        }
        valores.add(i, -1);
        posicaoVazia = i;
    }

    //------------------------------FUNCOES AUXILIARES---------------------------------------------------------------
    boolean realizaMovimento(int movimento) {
        switch (movimento) {
            case 0:
                if (posicaoVazia + colunas < (linhas * colunas)) {
                    if (realizaTroca(posicaoVazia, posicaoVazia + colunas)) {
                        posicaoVazia = posicaoVazia + colunas;
                        return true;
                    }
                }
                break;
            case 1:
                if (posicaoVazia - colunas > -1) {
                    if (realizaTroca(posicaoVazia, posicaoVazia - colunas)) {
                        posicaoVazia = posicaoVazia - colunas;
                        return true;
                    }
                }
                break;
            case 2:
                if ((posicaoVazia % colunas) + 1 < colunas) {
                    if (realizaTroca(posicaoVazia, posicaoVazia + 1)) {
                        posicaoVazia = posicaoVazia + 1;
                        return true;
                    }
                }
                break;
            case 3:
                if ((posicaoVazia % colunas) > 0) {
                    if (realizaTroca(posicaoVazia, posicaoVazia - 1)) {
                        posicaoVazia = posicaoVazia - 1;
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    void geraFilho(int movimento, int idPai) {
        switch (movimento) {
            case 0:
                if (posicaoVazia + colunas < (linhas * colunas))
                    adicionaFilhoSeNaoGerarLoop(posicaoVazia, posicaoVazia + colunas, idPai);
                break;
            case 1:
                if (posicaoVazia - colunas > -1)
                    adicionaFilhoSeNaoGerarLoop(posicaoVazia, posicaoVazia - colunas, idPai);
                break;
            case 2:
                if ((posicaoVazia % colunas) + 1 < colunas)
                    adicionaFilhoSeNaoGerarLoop(posicaoVazia, posicaoVazia + 1, idPai);
                break;
            case 3:
                if ((posicaoVazia % colunas) > 0)
                    adicionaFilhoSeNaoGerarLoop(posicaoVazia, posicaoVazia - 1, idPai);
                break;
        }
    }

    boolean realizaTroca(int posicaoVazia, int novaPosicaoVazia) {
        if (verificaNaoGeraLoop(posicaoVazia, novaPosicaoVazia)) {
            Collections.swap(valores,posicaoVazia,novaPosicaoVazia);
            return true;
        } else
            return false;
    }

    void adicionaFilhoSeNaoGerarLoop(int posicaoVazia, int novaPosicaoVazia, int idPai) {
        boolean naoGera;
        boolean adiciona = true;
        auxVerificaLoop.clear();
        for (int i = 0; i < linhas * colunas; i++) {
            auxVerificaLoop.add(i, valores.get(i));
        }
        auxVerificaLoop.add(posicaoVazia, auxVerificaLoop.get(novaPosicaoVazia));
        auxVerificaLoop.add(novaPosicaoVazia, -1);
        for (int i = 0; i < posicaoAtualVisitados && adiciona; i++) {
            if (!estadosVisitados.get(i).isAtivo()) {
                naoGera = false;
                for (int j = 0; j < (linhas * colunas) && !naoGera; j++) {
                    if (auxVerificaLoop.get(j) != estadosVisitados.get(i).getEstado().get(j))
                        naoGera = true;
                }
                if (!naoGera)
                    adiciona = false;
            }
        }
        if (adiciona) {
            Item novoEstado = new Item(posicaoAtualVisitados + 1, auxVerificaLoop.subList(0, auxVerificaLoop.size()),
                    idPai, profundidadeAtual, calculaDistanciaManhattan(), true);
            //System.out.printf("id: %d , idPai: %d, profundidade: %d , distancia: %d , : ",
            // novoEstado.getId(), novoEstado.getId()Pai, novoEstado.getProfundidade(),
            // novoEstado.getDistManhattan());
            //System.out.printf("\n");
            if (valorMenorDistancia > novoEstado.getDistManhattan()) {
                valorMenorDistancia = novoEstado.getDistManhattan();
                indiceMenorDistManhattan = posicaoAtualVisitados;
            }
            estadosVisitados.add(posicaoAtualVisitados, novoEstado);
            posicaoAtualVisitados++;
        }
    }

    int calculaDistanciaManhattan() {
        List<Integer> distanciaUnitaria = new ArrayList<>(linhas * colunas);
        int soma = 0;
        for (int i = 0; i < linhas * colunas; i++) {
            distanciaUnitaria.add(i, auxVerificaLoop.get(i) - (i + 1));
            if (distanciaUnitaria.get(i) < 0)
                distanciaUnitaria.add(i, distanciaUnitaria.get(i) * -1);
            distanciaUnitaria.add(i, (distanciaUnitaria.get(i) - (colunas * (distanciaUnitaria.get(i) / colunas)) +
                    (distanciaUnitaria.get(i)) / colunas));
            soma += distanciaUnitaria.get(i);
        }
        return soma;
    }

    boolean verificaNaoGeraLoop(int posicaoVazia, int novaPosicaoVazia) {
        auxVerificaLoop = valores.stream().collect(Collectors.toList());
        Collections.swap(auxVerificaLoop, posicaoVazia, novaPosicaoVazia);
        for (int i = 0; i < posicaoAtualVisitados; i++) {
            for (int j = 0; j < (linhas * colunas); j++) {
                if (auxVerificaLoop.get(j).equals(estadosVisitados.get(i).getEstado().get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean verificaSolucao() {
        for (int i = 0; i < (linhas * colunas) - 1; i++)
            if (valores.get(i) != i + 1) return false;
        int idSolucao = obterIdSolucao();
            if (idSolucao == -1) return false;
        custoSolucao = estadosVisitados.get(idSolucao - 1).getProfundidade();
        passosRestantes = (int) custoSolucao;
        return true;
    }

    boolean verificaFracasso() {
        for (int j = 0; j < (linhas * colunas); j++) {
            if (!valoresBackup.get(j).equals(valores.get(j))) return false;
        }
        return true;
    }

    int contaEstadosFechados() {
        int contador = 0;
        for (int i = 0; i < posicaoAtualVisitados; i++) {
            if (!estadosVisitados.get(i).isAtivo())
                contador++;
        }
        return contador;
    }

    int contaEstadosAbertos() {
        int contador = 0;
        for (int i = 0; i < posicaoAtualVisitados; i++) {
            if (estadosVisitados.get(i).isAtivo())
                contador++;
        }
        return contador;
    }

    void limpaEstadosVisitados() {
        posicaoAtualVisitados = 0;
        estadosVisitados.clear();
    }


    int obterIdSolucao() {
        for (int i = posicaoAtualVisitados - 1; i >= 0; i--) {
            for (int j = 0; j < (linhas * colunas) - 1; j++) {
                if (estadosVisitados.get(i).getEstado().get(j) == (j + 1)) {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    void aumentaTamanhoVetorVisitados() {
        fatorMultiplicacao++;
        List<Item> estadosVisitadosAumentarTamanho =
                new ArrayList<>(linhas * colunas * constanteDeSobra * fatorMultiplicacao);
        for (int i = 0; i < posicaoAtualVisitados; i++) {
            estadosVisitadosAumentarTamanho.add(i, estadosVisitados.get(i));
            for (int j = 0; j < linhas * colunas; j++)
                estadosVisitadosAumentarTamanho.get(i).getEstado().add(j, estadosVisitados.get(i).getEstado().get(j));
        }
        estadosVisitados.clear();
        estadosVisitados = new ArrayList<>(linhas * colunas * constanteDeSobra * fatorMultiplicacao);
        for (int i = 0; i < posicaoAtualVisitados; i++) {
            estadosVisitados.add(i, estadosVisitadosAumentarTamanho.get(i));
            for (int j = 0; j < linhas * colunas; j++)
                estadosVisitados.get(i).getEstado().add(j, estadosVisitadosAumentarTamanho.get(i).getEstado().get(j));
        }
        estadosVisitadosAumentarTamanho = null;
    }

}
