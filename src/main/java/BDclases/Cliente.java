package BDclases;

public class Cliente extends Pessoa {
    private int id;
    private String ci;
    private String estadoCivil;
    private String domicilio;

    public Cliente() {}

    public Cliente(int id, String nome, String ci) {
        this.id = id;
        this.nome = nome;            
        this.ci = ci;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCi() { return ci; }
    public void setCi(String ci) { this.ci = ci; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }
}
