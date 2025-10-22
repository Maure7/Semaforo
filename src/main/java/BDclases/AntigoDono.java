package BDclases;

public class AntigoDono extends Pessoa {
    private int id;
    private String telefone;
    private String cedula;

    public AntigoDono() {
    }
    public AntigoDono(int id, String nome, String cidade, String telefone, String cedula) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
        this.telefone = telefone;
        this.cedula = cedula;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    
     @Override
    public String toString() {
        return "AntigoDono{" +
                "id=" + id +
                ", nome='" + getNome() + '\'' +
                ", cidade='" + getCidade() + '\'' + 
                ", telefone='" + telefone + '\'' +
                ", cedula='" + cedula + '\'' +
                '}';
    }
}
