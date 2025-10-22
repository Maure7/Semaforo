package BDclases;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Venda {

    private int id;
    private int veiculoId;
    private int clienteId;
    private BigDecimal preco;
    private LocalDate data;
    private String metodoPagamento;
    private int parcelas;

    private String nomeCliente;
    private String cedulaCliente;
    private Integer padronVeiculo;
    private Integer vendedorId;
    private String nomeVendedor;

    public Venda() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getVeiculoId() { return veiculoId; }
    public void setVeiculoId(int veiculoId) { this.veiculoId = veiculoId; }
    
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    
    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    
    public int getParcelas() { return parcelas; }
    public void setParcelas(int parcelas) { this.parcelas = parcelas; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    
    public String getCedulaCliente() { return cedulaCliente; }
    public void setCedulaCliente(String cedulaCliente) { this.cedulaCliente = cedulaCliente; }
    
    public Integer getPadronVeiculo() { return padronVeiculo; }
    public void setPadronVeiculo(Integer padronVeiculo) { this.padronVeiculo = padronVeiculo; }

    public Integer getVendedorId() { return vendedorId; }
    public void setVendedorId(Integer vendedorId) { this.vendedorId = vendedorId; }

    public String getNomeVendedor() { return nomeVendedor; }
    public void setNomeVendedor(String nomeVendedor) { this.nomeVendedor = nomeVendedor; } 

}