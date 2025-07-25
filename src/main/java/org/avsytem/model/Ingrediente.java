package org.avsytem.model;

import java.util.Objects;

public class Ingrediente {
    private String nome;
    private double quantidade;
    private String unidade; // Ex: "x�caras", "gramas", "colheres de sopa"

    public Ingrediente(String nome, double quantidade, String unidade) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidade = unidade;
    }

    public Ingrediente() {}


    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    @Override
    public String toString() {
        return quantidade + " " + unidade + " de " + nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingrediente that = (Ingrediente) o;
        return Double.compare(that.quantidade, quantidade) == 0 &&
                Objects.equals(nome, that.nome) &&
                Objects.equals(unidade, that.unidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, quantidade, unidade);
    }
}
