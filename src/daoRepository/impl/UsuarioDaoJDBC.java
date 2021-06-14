/**
 *
 * @author Jedaboy/Mateus Oliveira/Guilherme Leme
 */
package daoRepository.impl;

import daoRepository.UsuarioDao;
import dbRepository.DB;
import dbRepository.DbException;
import entities.Usuario;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioDaoJDBC implements UsuarioDao {
    
  
    private Connection connection;

    public UsuarioDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    //Registra o usuario.
    @Override
    public boolean register (Usuario usuario) {
        boolean registrou;
        PreparedStatement st = null;
        try{
            //Se nao houver nenhum usuario com o mesmo nome ou email informado, inicia o registro no banco de dados.
            if (checkUsuario(usuario)) {

                //informa exito no registro
                registrou = true;
                
                //Se conecta ao banco de dados e registra o usuario.
                st = connection.prepareStatement(
                        "INSERT INTO usuario "
                                + "(nome, senha, email) "
                                + "VALUES "
                                + "(?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);

                st.setString(1, usuario.getNome());
                st.setString(2, usuario.getSenha());
                st.setString(3, usuario.getEmail());
                
                
                //Variavel para checar se houve a criacao.
                int linhasAlteradas = st.executeUpdate();

                //Checa se houve a criacao. Se o usuario nao for cadastrado, retornara uma exception alertando que houve um erro.
                if (linhasAlteradas == 0) {
                     return false;
                //Retorna uma mensagem anunciando que o registro foi concluido.
                } else {
                    System.out.println("Usuario registrado com sucesso!");
                }
            }
            else {
                return false;
                
            }
        }
        catch (SQLException e){
             return false;
        }
        finally {
            DB.closeStatement(st);
        }
        return registrou;
    }

    //Procura um usuario com mesmo nome ou email no DB.
    @Override
    public boolean checkUsuario (Usuario usuario){
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            //Checa se existe algum usuario criado com o email ou nome informado.
            st = connection.prepareStatement("SELECT * FROM usuario WHERE usuario.nome = ? OR usuario.email = ? ");

            st.setString(1, usuario.getNome());
            st.setString(2, usuario.getEmail());

            rs = st.executeQuery();

            //Caso exista um usuario com email ou nome ja cadastrado, retorna false, assim nao permitindo a criacao do usuario com estes nomes.
            if (rs.next()) {
                return false;
            }
            else {
                return true;
            }
        }
        catch (SQLException e){
            throw new DbException("Erro ao registrar usuário!");
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    //Realiza o login.
    @Override
    public Usuario login (Usuario usuario)throws Exception{
        Usuario usuarioLogin = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
            //Procura no DB se existe algum usuario com nome e senha informados.
            st = connection.prepareStatement("SELECT * FROM usuario WHERE usuario.nome = ? AND usuario.senha = ? ");

            st.setString(1, usuario.getNome());
            st.setString(2, usuario.getSenha());

            rs = st.executeQuery();

            //Se houver um usuario cadastrado com as informacoes, realiza o login e retorna um objeto com todos os dados do usuario (id, nome, email e senha).
            if (rs.next()) {
                //Metodo para instanciar um objeto com os dados do DB.
                usuarioLogin = instantiateUsuario(rs);            
                return usuarioLogin;
            }
                 
                 return null;    
       
    }

    //Metodo para instanciar um objeto com os dados do DB.
    private Usuario instantiateUsuario (ResultSet rs) throws SQLException{
        Usuario usuario = new Usuario();
        usuario.setLogou(true);
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setEmail(rs.getString("email"));
        return usuario;
    }
}
