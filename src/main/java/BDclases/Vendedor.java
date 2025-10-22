package BDclases;

public class Vendedor {
    private String nome;
    private String ci;
    private int idVendedor;
    private String domicilio;
    private String estadoCivil;

    public Vendedor() {
    }

    public Vendedor(int idVendedor, String nome, String ci, String domicilio, String estadoCivil) {
        this.nome = nome;
        this.ci = ci;
        this.idVendedor = idVendedor;
        this.domicilio = domicilio;
        this.estadoCivil = estadoCivil;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCi() { return ci; }
    public void setCi(String ci){ this.ci = ci; }

    public int getIdVendedor() { return idVendedor; }
    public void setIdVendedor(int idVendedor) { this.idVendedor = idVendedor; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }

    @Override
    public String toString() {
        return nome;
    }
}