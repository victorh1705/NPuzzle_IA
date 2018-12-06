package com.github.victorh1705.npuzzle.demo;

import com.github.victorh1705.npuzzle.demo.enumeration.ETipoBusca;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Puzzle {
    private int linhas = 3;
    private int colunas = 2;
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
        valores = new LinkedList<>();
        valoresBackup = new LinkedList<>();
        auxVerificaLoop = new LinkedList<>();
        estadosVisitados = new LinkedList<>();
//                (linhas * colunas * constanteDeSobra *
//                fatorMultiplicacao);
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

    public int getPassosSolucao() {
        return passosSolucao;
    }

    public void setPassosSolucao(int passosSolucao) {
        this.passosSolucao = passosSolucao;
    }

    public int getPassosRestantes() {
        return passosRestantes;
    }

    public void setPassosRestantes(int passosRestantes) {
        this.passosRestantes = passosRestantes;
    }

    public double getCustoSolucao() {
        return custoSolucao;
    }

    public void setCustoSolucao(double custoSolucao) {
        this.custoSolucao = custoSolucao;
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
        tempoInicial = new Date();
        boolean fracasso = false, sucesso = false;
        rodando = true;
        limpaEstadosVisitados();
        adicionaPaiListaAbertos(-1, false);
        while (!sucesso && !fracasso) {
            if ((posicaoAtualVisitados % 25000) == 0)
                System.out.print("Aguarde mais um pouco...\n");
            if (verificaSolucao()) {
                sucesso = true;
                System.out.println("sucesso");
            }else {
                if (!obterProximoEstado(ETipoBusca.LARGURA))
                    fracasso = true;
            }
        }
        tempoFinal = new Date();
        resultadoObtido = true;
        rodando = false;
        embaralhado = false;
        nosExpandidos = posicaoAtualVisitados;
        nosVisitados = contaEstadosFechados() + 1;
        tempoExecucao = tempoFinal.getTime() - tempoInicial.getTime();
        fatorRamificacao = (float) (nosExpandidos / nosVisitados);
        fatorMultiplicacao = 1;
        profundidade--;

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
        tempoExecucao = tempoFinal.getTime() - tempoInicial.getTime();
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

    private boolean obterProximoEstado(ETipoBusca tipoBusca) {
        switch (tipoBusca) {
            case LARGURA:
                Iterator<Item> _visitados = estadosVisitados.iterator();
                for (int i = 0; i < posicaoAtualVisitados && _visitados.hasNext(); i++) {
                    Item next = _visitados.next();
                    if (next.isAtivo()) {

                        valores.clear();
                        valores = next.getEstado().stream().collect(Collectors.toList());
                        posicaoVazia = valores.indexOf(-1);
                        next.setAtivo(false);

                        profundidadeAtual = next.getProfundidade() + 1;
                        if (profundidade < profundidadeAtual)
                            profundidade = profundidadeAtual;
                        if ((posicaoAtualVisitados + 100) >
                                (linhas * colunas * fatorMultiplicacao * constanteDeSobra)) {
                            aumentaTamanhoVetorVisitados();
                        }
                        adicionaFilhosListaAbertos(next.getId(), false);
                        return true;
                    }
                }
                break;
            case PROFUNDIDADE:
                for (int i = posicaoAtualVisitados - 1; i >= 0; i--) {
                    Item estadoAtual = estadosVisitados.get(i);
                    if (estadoAtual.isAtivo()) {
                        valores = estadoAtual.getEstado()
                                .stream().collect(Collectors.toList());
                        posicaoVazia = valores.indexOf(-1);
                        estadoAtual.setAtivo(false);
                        profundidadeAtual = estadoAtual.getProfundidade() + 1;

                        if (profundidade < profundidadeAtual) profundidade = profundidadeAtual;
                        if ((posicaoAtualVisitados + 100) >
                                (linhas * colunas * fatorMultiplicacao * constanteDeSobra)) {
                            aumentaTamanhoVetorVisitados();
                        }
                        adicionaFilhosListaAbertos(estadoAtual.getId(), true);
                        return true;
                    }
                }
                break;
            case GULOSA:
                if (indiceMenorDistManhattan > -1) {
                    Item estadoAtual = estadosVisitados.get(indiceMenorDistManhattan);
                    valores = estadoAtual.getEstado().stream().collect(Collectors.toList());
                    posicaoVazia = valores.indexOf(-1);
                    estadoAtual.setAtivo(false);
                    profundidadeAtual = estadoAtual.getProfundidade() + 1;

                    if (profundidade < profundidadeAtual) profundidade = profundidadeAtual;
                    if ((posicaoAtualVisitados + 100) > (linhas * colunas * fatorMultiplicacao * constanteDeSobra)) {
                        aumentaTamanhoVetorVisitados();
                    }
                    int idAtual = estadoAtual.getId();

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
                    Item estadoAtual = estadosVisitados.get(indiceMenorDistManhattan);
                    valores = estadoAtual.getEstado().stream().collect(Collectors.toList());
                    posicaoVazia = valores.indexOf(-1);

                    estadoAtual.setAtivo(false);
                    profundidadeAtual = estadoAtual.getProfundidade() + 1;
                    if (profundidade < profundidadeAtual) profundidade = profundidadeAtual;
                    if ((posicaoAtualVisitados + 100) > (linhas * colunas * fatorMultiplicacao * constanteDeSobra)) {
                        aumentaTamanhoVetorVisitados();
                    }
                    int idAtual = estadoAtual.getId();

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

    private void voltaProPai() {
        int idPai = -1;
        for (int i = posicaoAtualVisitados - 1; i >= 0 && idPai == -1; i--) {
            if (estadosVisitados.get(i).isAtivo()) {
                idPai = estadosVisitados.get(i).getIdPai();
                estadosVisitados.get(i).setAtivo(false);
            }
        }
        for (int i = posicaoAtualVisitados - 1; i >= 0; i--) {
            if (estadosVisitados.get(i).getId() == idPai) {
                valores.clear();
                valores = estadosVisitados.get(i).getEstado().stream().collect(Collectors.toList());
                posicaoVazia = valores.indexOf(-1);
                break;
            }
        }
        idPaiAtual = idPai;
        voltouProPai = true;
    }

    private int escolherRegra() {
        int regras[] = {0, 2, 3, 1};
        for (int i = 0; i < 4; i++) {
            if (realizaMovimento(regras[i]))
                return regras[i];
        }
        return -1;
    }

    private void adicionaPaiListaAbertos(int idPai, boolean buscaProfundidade) {
        Item novoEstado = new Item(posicaoAtualVisitados + 1, valores, idPai, 0,
                -1, false);
        estadosVisitados.add(posicaoAtualVisitados, novoEstado);
        posicaoAtualVisitados++;
        profundidadeAtual = 1;
        adicionaFilhosListaAbertos(posicaoAtualVisitados, buscaProfundidade);
    }

    private void adicionaFilhosListaAbertos(int idPai, boolean buscaProfundidade) {
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

    private void adicionaNovoEstadoVisitado(int idPai) {
        int profundidade = (idPai == -1) ? 0 : (estadosVisitados.get(idPai - 1).getProfundidade() + 1);

        Item novoEstado = new Item(posicaoAtualVisitados + 1, null, idPai, 0,
                0, true);
        novoEstado.setEstado( valores.stream().collect(Collectors.toList()));
        if (novoEstado.getProfundidade() > profundidade) profundidade = novoEstado.getProfundidade();

        estadosVisitados.set(posicaoAtualVisitados, novoEstado);
        //System.out.printf("id: %d , idPai: %d , profundidade: %d , estado : ",novoEstado.getId(),
        // novoEstado.getId()Pai, novoEstado.getProfundidade());
        posicaoAtualVisitados++;
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
        valoresBackup = valores.stream().collect(Collectors.toList());
        embaralhado = true;
        resultadoObtido = false;
    }

    void ordenar() {
        inicializa();
        resultadoObtido = false;
        embaralhado = false;
        valores.clear();
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

    private void geraFilho(int movimento, int idPai) {
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

    private boolean realizaTroca(int posicaoVazia, int novaPosicaoVazia) {
        if (verificaNaoGeraLoop(posicaoVazia, novaPosicaoVazia)) {
            Collections.swap(valores, posicaoVazia, novaPosicaoVazia);
            return true;
        } else
            return false;
    }

    private void adicionaFilhoSeNaoGerarLoop(int posicaoVazia, int novaPosicaoVazia, int idPai) {
        auxVerificaLoop = valores.stream().collect(Collectors.toList());
        Collections.swap(auxVerificaLoop, novaPosicaoVazia, posicaoVazia);
        for (int i = 0; i < posicaoAtualVisitados; i++) {
            String estadoString = listToString.apply(estadosVisitados.get(i).getEstado());
            if (listToString.apply(auxVerificaLoop).equals(estadoString)) return;
//            if (!estadosVisitados.get(i).isAtivo()) {
//                if (!listToString.apply(auxVerificaLoop).equals(estadoString)) return;
//            }
        }
        Item novoEstado = new Item(posicaoAtualVisitados + 1, auxVerificaLoop.stream().collect(Collectors.toList()),
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

    private int calculaDistanciaManhattan() {
        List<Integer> distanciaUnitaria = new LinkedList<>();
        int soma = 0;
        for (int i = 0; i < linhas * colunas; i++) {
            int inserir = auxVerificaLoop.get(i) - (i + 1) / colunas;
            if (inserir < 0) inserir *= -1;
            distanciaUnitaria.add(i, inserir);
            soma += distanciaUnitaria.get(i);
        }
        return soma;
    }

    private boolean verificaNaoGeraLoop(int posicaoVazia, int novaPosicaoVazia) {
        auxVerificaLoop = valores.stream().collect(Collectors.toList());
        Collections.swap(auxVerificaLoop, posicaoVazia, novaPosicaoVazia);
        for (int i = 0; i < posicaoAtualVisitados; i++) {
            String estadoString = listToString.apply(estadosVisitados.get(i).getEstado());
            if (listToString.apply(auxVerificaLoop).equals(estadoString)) return false;
        }
        return true;
    }

    private boolean verificaSolucao() {
        for (int i = 0; i < (linhas * colunas) - 1; i++)
            if (valores.get(i) != i + 1) return false;

        int idSolucao;
        if ((idSolucao = obterIdSolucao()) == -1) return false;
        custoSolucao = estadosVisitados.get(idSolucao - 1).getProfundidade();
        passosRestantes = (int) custoSolucao;
        return true;
    }

    private boolean verificaFracasso() {
        return !listToString.apply(valoresBackup).equals(listToString.apply(valores));
    }

    private int contaEstadosFechados() {
        int contador = 0;
        for (int i = 0; i < posicaoAtualVisitados; i++) {
            if (!estadosVisitados.get(i).isAtivo())
                contador++;
        }
        return contador;
    }

    private int contaEstadosAbertos() {
        int contador = 0;
        for (int i = 0; i < posicaoAtualVisitados; i++) {
            if (estadosVisitados.get(i).isAtivo())
                contador++;
        }
        return contador;
    }

    private void limpaEstadosVisitados() {
        posicaoAtualVisitados = 0;
        estadosVisitados.clear();
    }


    int obterIdSolucao() {
        List<Integer> list = IntStream.range(0, (linhas * colunas) - 1)
                .mapToObj(i -> i + 1)
                .collect(Collectors.toList());
        String solucao = listToString.apply(list);
        for (int i = posicaoAtualVisitados - 1; i >= 0; i--) {
            if (listToString.apply(estadosVisitados.get(i).getEstado()).equals(solucao))
                return i + 1;
        }
        return -1;
    }

    private void aumentaTamanhoVetorVisitados() {
        fatorMultiplicacao++;
        List<Item> estadosVisitadosAumentarTamanho = new LinkedList<>();
        for (int i = 0; i < posicaoAtualVisitados; i++) {
            estadosVisitadosAumentarTamanho.set(i, estadosVisitados.get(i));
            for (int j = 0; j < linhas * colunas; j++)
                estadosVisitadosAumentarTamanho.get(i).getEstado().set(j, estadosVisitados.get(i).getEstado().get(j));
        }
        estadosVisitados.clear();
        estadosVisitados = new LinkedList<>();
        for (int i = 0; i < posicaoAtualVisitados; i++) {
            estadosVisitados.set(i, estadosVisitadosAumentarTamanho.get(i));
            for (int j = 0; j < linhas * colunas; j++)
                estadosVisitados.get(i).getEstado().set(j, estadosVisitadosAumentarTamanho.get(i).getEstado().get(j));
        }
        estadosVisitadosAumentarTamanho = null;
    }

    private String stringVetor(LinkedList<Integer> vetor) {
        return StringUtils.join(vetor, ",");
    }

    private Function<List<Integer>, String> listToString = list -> StringUtils.join(list, ",");
}
