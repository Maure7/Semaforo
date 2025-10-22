package BDclases;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Compra {
      
    private int id;
    private LocalDate data;
    private BigDecimal preco;
    private boolean divida;
    private int veiculoId;
    private int padron;
    private String modelo;
    private String nomeAntigoDono;
    
    public Compra() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    
    public boolean isDivida() { return divida; }
    public void setDivida(boolean divida) { this.divida = divida; }
    
    public int getVeiculoId() { return veiculoId; }
    public void setVeiculoId(int veiculoId) { this.veiculoId = veiculoId; }
    
    public int getPadron() { return padron; }
    public void setPadron(int padron) { this.padron = padron; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
      
    public String getNomeAntigoDono() { return nomeAntigoDono; }
    public void setNomeAntigoDono(String nomeAntigoDono) { this.nomeAntigoDono = nomeAntigoDono; }
}
