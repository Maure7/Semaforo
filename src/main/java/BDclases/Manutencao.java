package BDclases;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Manutencao {

    public Manutencao() {
    }
    
    private int id;
    private LocalDate data;
    private String descricao;
    private BigDecimal custo;
    private int veiculoId;
    private int kmManutencao;
  
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public void setData(LocalDate data) { this.data = data; }
    public LocalDate getData() { return data; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public BigDecimal getCusto() { return custo; }
    public void setCusto(BigDecimal custo) { this.custo = custo; }
    
    public int getVeiculoId() { return veiculoId; }
    public void setVeiculoId(int veiculoId) { this.veiculoId = veiculoId; }
    
    public int getKmManutencao() { return kmManutencao; }
    public void setKmManutencao(int kmManutencao) { this.kmManutencao = kmManutencao; }
        
    @Override
    public String toString() {
        return "Manutencao{" + "id=" + id + ", data=" + data + ", descricao='" + descricao + '\'' + ", custo=" + custo + '}';
    }
}
