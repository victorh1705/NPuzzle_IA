package com.github.victorh1705.npuzzle.demo;

import java.util.List;

public class Item {

    private int id;
    private List<Integer> estado;
    private int idPai;
    private int profundidade;
    private int distManhattan;
    private boolean ativo;

    public Item(int id, List<Integer> estado, int idPai, int profundidade, int distManhattan, boolean ativo) {
        this.id = id;
        this.estado = estado;
        this.idPai = idPai;
        this.profundidade = profundidade;
        this.distManhattan = distManhattan;
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getEstado() {
        return estado;
    }

    public void setEstado(List<Integer> estado) {
        this.estado = estado;
    }

    public int getIdPai() {
        return idPai;
    }

    public void setIdPai(int idPai) {
        this.idPai = idPai;
    }

    public int getProfundidade() {
        return profundidade;
    }

    public void setProfundidade(int profundidade) {
        this.profundidade = profundidade;
    }

    public int getDistManhattan() {
        return distManhattan;
    }

    public void setDistManhattan(int distManhattan) {
        this.distManhattan = distManhattan;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
