/**
 *
 * @author Jedaboy/Mateus Oliveira/Guilherme Leme
 */
package daoRepository.impl;

import daoRepository.GeneroDao;
import dbRepository.DB;
import dbRepository.DbException;
import entities.Genero;
import entities.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class GeneroDaoJDBC implements GeneroDao {

    Connection connection = null;

    public GeneroDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    //Lista todos os generos disponiveis no DB.
    @Override
    public List<Genero> getGeneros() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            //Procura no DB todos os generos registrados.
            st = connection.prepareStatement("SELECT * FROM genero");

            rs = st.executeQuery();

            //Cria uma lista com todos os generos registrados.
            List<Genero> list = new ArrayList<>();

            //Adiciona todos os generos na lista.
            while (rs.next()) {
                Genero genero = instantiateGenero(rs);
                list.add(genero);
            }

            return list;
        } catch (SQLException e) {
            throw new DbException("Erro ao exibir generos disponiveis!");
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    //Lista os generos nao favoritados do usuario.
    @Override
    public List<Genero> getGenerosNaoFav(Usuario usuario) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            //Procura os generos favoritos do usuario.
            st = connection.prepareStatement("SELECT * FROM usuario_genero WHERE usuario_genero.id_usuario = ?");

            st.setInt(1, usuario.getId());

            rs = st.executeQuery();

            //Cria uma lista com todos os ids dos generos favoritos do usuario.
            List<Integer> ids = new ArrayList<>();

            while (rs.next()) {
                int a = rs.getInt("id_genero");
                ids.add(a);
            }

            //Cria uma lista com todos os generos disponiveis e remove os generos favoritos do usuario.
            List<Genero> list = getGeneros();

            for (Integer id : ids) {
                for (Genero genero : list) {
                    if (genero.getId() == id) {
                        list.remove(genero);
                    }
                }
            }

            return list;
        } catch (SQLException e) {
            throw new DbException("Erro ao consultar os generos nao favoritados.");
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Integer> getIdGenerosNaoFav(Usuario usuario) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            //Procura os generos favoritos do usuario.
            st = connection.prepareStatement("SELECT * FROM usuario_genero WHERE usuario_genero.id_usuario = ?");

            st.setInt(1, usuario.getId());

            rs = st.executeQuery();

            //Cria uma lista com todos os ids dos generos favoritos do usuario.
            List<Integer> ids = new ArrayList<>();

            while (rs.next()) {
                int a = rs.getInt("id_genero");
                ids.add(a);
            }

            //Cria uma lista com todos os generos disponiveis e remove os generos favoritos do usuario.
            List<Genero> list = getGeneros();

            for (Integer id : ids) {
                for (Genero genero : list) {
                    if (genero.getId() == id) {
                        list.remove(genero);
                    }
                }
            }

            return ids;
        } catch (SQLException e) {
            throw new DbException("Erro ao consultar os generos nao favoritados.");
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    //Retorna o genero que possui o id informado.
    public Genero getGeneroById(Integer idGenero) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            //Procura no DB um genero com o id informado.
            st = connection.prepareStatement("SELECT id, nome FROM genero WHERE genero.id = ?");

            st.setInt(1, idGenero);

            rs = st.executeQuery();

            //Se houver um genero com o id informado, retorna o genero.
            if (rs != null) {
                rs.next();
                Genero genero = new Genero(rs.getString("nome"), rs.getInt("id"));
                return genero;
            } else {
                throw new DbException("Nao existe genero com o id informado!");
            }

        } catch (SQLException e) {
            throw new DbException("Erro ao buscar genero!");
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    //Adiciona o genero informado na lista de favoritos do usuario informado.
    @Override
    public void addGeneroFav(Usuario usuario, Genero genero) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            //Verifica se o usuario ja nao possui o genero na lista de favoritos.
            st = connection.prepareStatement("SELECT * FROM usuario_genero WHERE usuario_genero.id_usuario = ? AND usuario_genero.id_genero = ?");

            st.setInt(1, usuario.getId());
            st.setInt(2, genero.getId());

            rs = st.executeQuery();
            //Se ele ja possuir o genero na lista de favoritos, o sistema joga uma exception avisando que ja esta na lista de favoritos.
            if (rs.next()) {
                throw new DbException("O genero ja esta na lista de favoritos.");
            } else {
                //Insere no DB a informacao de que o usuario adicionou o genero aos favoritos.
                st = connection.prepareStatement("INSERT INTO usuario_genero (id_usuario, id_genero) VALUES (?, ?)");

                st.setInt(1, usuario.getId());
                st.setInt(2, genero.getId());

                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DbException("Erro ao adicionar genero aos favoritos!");
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    //Remove o genero informado da lista de favoritos do usuario informado.
    @Override
    public void rmvGeneroFav(Usuario usuario, Genero genero) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            //Procura no DB se o genero esta na lista de favoritos.
            st = connection.prepareStatement("SELECT * FROM usuario_genero WHERE usuario_genero.id_usuario = ? AND usuario_genero.id_genero = ?");

            st.setInt(1, usuario.getId());
            st.setInt(2, genero.getId());

            rs = st.executeQuery();

            //Se o genero nao estiver na lista, joga uma exception.
            if (!rs.next()) {
                throw new DbException("O genero nao esta na lista de favoritos.");
            } else {
                st = connection.prepareStatement("DELETE FROM usuario_genero WHERE id_usuario = ? AND id_genero = ?", Statement.RETURN_GENERATED_KEYS);

                st.setInt(1, usuario.getId());
                st.setInt(2, genero.getId());

                int irmaodojoarel = st.executeUpdate();

                //Verifica se a operacao de remocao foi realizada com sucesso. Se algo der errado, joga uma exception.
                if (irmaodojoarel == 0) {
                    throw new DbException("Erro ao remover o genero dos favoritos.");
                }
            }
        } catch (SQLException e) {
            throw new DbException("Erro ao remover o genero dos favoritos.");
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    //Retorna a lista de generos favoritos do usuario informado.
    @Override
    public List<Genero> getGeneroFav(Usuario usuario) {

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            //Procura todos os generos favoritos do usuario informado.
            st = connection.prepareStatement("SELECT * FROM usuario_genero WHERE usuario_genero.id_usuario = ?");

            st.setInt(1, usuario.getId());

            rs = st.executeQuery();

            //Cria uma lista de ids dos generos favoritos.
            List<Integer> ids = new ArrayList<>();
            List<Genero> list = new ArrayList<>();

            while (rs.next()) {
                int a = rs.getInt("id_genero");
                ids.add(a);
            }

            //Cria uma lista de generos favoritos a partir dos ids informados.
            for (Integer b : ids) {
                Genero genero = getGeneroById(b);
                list.add(genero);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException("Erro ao retornar os generos favoritos.");
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Integer> getIdGeneroFav(Usuario usuario) {

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            //Procura todos os generos favoritos do usuario informado.
            st = connection.prepareStatement("SELECT * FROM usuario_genero WHERE usuario_genero.id_usuario = ?");

            st.setInt(1, usuario.getId());

            rs = st.executeQuery();

            //Cria uma lista de ids dos generos favoritos.
            List<Integer> ids = new ArrayList<>();
            List<Genero> list = new ArrayList<>();

            while (rs.next()) {
                int a = rs.getInt("id_genero");
                ids.add(a);
            }

            //Cria uma lista de generos favoritos a partir dos ids informados.
            for (Integer b : ids) {
                Genero genero = getGeneroById(b);
                list.add(genero);
            }
            return ids;
        } catch (SQLException e) {
            throw new DbException("Erro ao retornar os generos favoritos.");
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    //Instancia um genero a partir do ResultSet inforamado.
    private Genero instantiateGenero(ResultSet rs) throws SQLException {
        Genero genero = new Genero();
        genero.setId(rs.getInt("id"));
        genero.setNome(rs.getString("nome"));
        return genero;
    }
}
