package daoRepository;

import entities.Usuario;

public interface UsuarioDao {

    boolean register(Usuario usuario);

    boolean checkUsuario(Usuario usuario);

    Usuario login(Usuario usuario) throws Exception;

}
