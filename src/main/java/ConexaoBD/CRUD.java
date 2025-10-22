package ConexaoBD;

import java.util.List;

public interface CRUD<T> {
    boolean inserir(T objeto);
    boolean atualizar(T objeto);
    boolean deletar(int id);
    List<T> listar();
    T buscarPorId(int id);
}
