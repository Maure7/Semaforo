package BDclases;

import java.math.BigDecimal;

public class Vehiculo {
    private int id;
    private String marca;
    private String modelo;
    private int km;
    private BigDecimal preco;
    private String cor;
    private String placa;
    private int ano;
    private String cidade;
    private boolean disponivel;
    private int padron;
    private byte[] imagePath;
    private String tipoVehiculo;
    private String combustible; 
    private String numeroMotor;
    private String numeroChasis;
    private AntigoDono antigoDono;
    private Integer idAntigoDono;
    private String nomeAntigoDono;
    private String cidadeAntigoDono;
    private String telefoneAntigoDono;
    private String cedulaAntigoDono; 
    
    public Vehiculo() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public int getKm() { return km; }
    public void setKm(int km) { this.km = km; }
    
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; } 
    
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    
    
    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }
    
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
    
    public int getPadron() { return padron; }
    public void setPadron(int padron) { this.padron = padron; }
    
    public byte[] getImagePath() { return imagePath; }
    public void setImagePath(byte[] imagePath) { this.imagePath = imagePath; }
    
    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }

    public String getCombustible() { return combustible; }
    public void setCombustible(String combustible) { this.combustible = combustible; }

    public String getNumeroMotor() { return numeroMotor; }
    public void setNumeroMotor(String numeroMotor) { this.numeroMotor = numeroMotor; }

    public String getNumeroChasis() { return numeroChasis; }
    public void setNumeroChasis(String numeroChasis) { this.numeroChasis = numeroChasis; }
    
     public AntigoDono getAntigoDono() { return antigoDono; }
    public void setAntigoDono(AntigoDono antigoDono) {
        this.antigoDono = antigoDono;
        if (antigoDono != null) {
            this.idAntigoDono = antigoDono.getId();
            this.nomeAntigoDono = antigoDono.getNome();
            this.cidadeAntigoDono = antigoDono.getCidade();
            this.telefoneAntigoDono = antigoDono.getTelefone();
            this.cedulaAntigoDono = antigoDono.getCedula(); 
        } else {
            this.idAntigoDono = null;
            this.nomeAntigoDono = null;
            this.cidadeAntigoDono = null;
            this.telefoneAntigoDono = null;
            this.cedulaAntigoDono = null; 
        }
    }
    
    public Integer getIdAntigoDono() { return idAntigoDono; }
    public void setIdAntigoDono(Integer idAntigoDono) {
        this.idAntigoDono = idAntigoDono;
        if (this.antigoDono != null && (idAntigoDono == null || !idAntigoDono.equals(this.antigoDono.getId()))) {
            this.antigoDono = null;
            this.antigoDono = null;
            this.nomeAntigoDono = null;
            this.cidadeAntigoDono = null;
            this.telefoneAntigoDono = null;
            this.cedulaAntigoDono = null;
        }
    }
    
    public String getNomeAntigoDono() { return nomeAntigoDono; }
    public void setNomeAntigoDono(String nomeAntigoDono) { this.nomeAntigoDono = nomeAntigoDono; }

    public String getCidadeAntigoDono() { return cidadeAntigoDono; }
    public void setCidadeAntigoDono(String cidadeAntigoDono) { this.cidadeAntigoDono = cidadeAntigoDono; }

    public String getTelefoneAntigoDono() { return telefoneAntigoDono; }
    public void setTelefoneAntigoDono(String telefoneAntigoDono) { this.telefoneAntigoDono = telefoneAntigoDono; }

    public String getCedulaAntigoDono() { return cedulaAntigoDono; }
    public void setCedulaAntigoDono(String cedulaAntigoDono) { this.cedulaAntigoDono = cedulaAntigoDono; }
    
    @Override
    public String toString() {
        return "Vehiculo{" +
                "id=" + id +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", placa='" + placa + '\'' +
                 ", tipoVehiculo='" + tipoVehiculo + '\'' +
                ", combustible='" + combustible + '\'' +
                ", numeroMotor='" + numeroMotor + '\'' +
                ", numeroChasis='" + numeroChasis + '\'' + 
                ", ano=" + ano +
                ", idAntigoDono=" + idAntigoDono +
                ", nomeAntigoDono='" + nomeAntigoDono + '\'' + 
                ", cidadeAntigoDono='" + cidadeAntigoDono + '\'' +
                ", telefoneAntigoDono='" + telefoneAntigoDono + '\'' +
                ", cedulaAntigoDono='" + cedulaAntigoDono + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
