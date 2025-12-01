package com.exemplo.scraping.model;

public class Vaga {
    private String titulo;
    private String empresa;
    private Float salario;
    private String fonte;
    
    // Construtores
    public Vaga() {}
    
    public Vaga(String titulo, String empresa, Float salario, String fonte) {
        this.titulo = titulo;
        this.empresa = empresa;
        this.salario = salario;
        this.fonte = fonte;
    }
    
    // Getters e Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    
    public Float getSalario() { return salario; }
    public void setSalario(Float salario) { this.salario = salario; }
    
    public String getFonte() { return fonte; }
    public void setFonte(String fonte) { this.fonte = fonte; }
    
    public String getSalarioFormatado() {
        if (salario == null) {
            return "Não informado";
        } else {
            return String.format("R$ %.2f", salario);
        }
    }
}