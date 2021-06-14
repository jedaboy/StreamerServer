package daoRepository.impl;

/**
 *
 * @author Jedaboy/Mateus Oliveira/Guilherme Leme
 */
import daoRepository.DaoFactory;
import daoRepository.GeneroDao;
import daoRepository.MusicaDao;
import dbRepository.DB;
import dbRepository.DbException;
import entities.Genero;
import entities.Musica;
import entities.Usuario;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class MusicaDaoJDBC implements MusicaDao {

    private Connection connection;

    public MusicaDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    //Retorna todas as musicas do sistema.
    @Override
    public List<Musica> listarMusicas() {
        try {
            PreparedStatement st = null;
            ResultSet rs = null;

            //Busca no DB todas as musicas armazenadas.
            st = connection.prepareStatement("SELECT * FROM musica");
            rs = st.executeQuery();

            //Cria uma lista de musicas e adiciona todas as musicas do DB.
            List<Musica> musicas = new ArrayList<>();
            while (rs.next()) {
                Musica musica = new Musica();
                musica.setId(rs.getInt("id"));
                musica.setDuracao(rs.getInt("duracao"));
                musica.setGenId(rs.getInt("genero"));
                musica.setNome(rs.getString("nome"));
                musica.setArtista(rs.getString("artista"));
                musica.setNota(rs.getInt("nota"));
                musica.setAvaliacoes(rs.getInt("avaliacoes"));
                //InputStream is = new BufferedInputStream(rs.getBinaryStream("imagem_capa"));
                //Image image = ImageIO.read(is);
                //if(image!= null){
                //ImageIcon imageIc = new ImageIcon(image);
                //musica.setImageIcon(imageIc);
                //}
                musicas.add(musica);
            }
            return musicas;

            //  } finally {
            // DB.closeResultSet(rs);
            // DB.closeStatement(st);
        } catch (SQLException ex) {
            Logger.getLogger(MusicaDaoJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //Retorna todas as musicas que possuem pelo menos um genero favoritado pelo usuario.
    @Override
    public List<Musica> recomendacoes(Usuario usuario) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement("SELECT id_genero FROM usuario_genero WHERE usuario_genero.id_usuario = ?");

            st.setInt(1, usuario.getId());

            rs = st.executeQuery();

            //Caso o usuario nao possua generos favoritos, o sistema cria uma exception avisando o usuario.
            if (!rs.next()) {
                throw new DbException("O usuario nao possui generos favoritos!");
            }

            //modificado pois não exibia as musicas do primeiro genero favorito, pega os generos fav
            boolean a = true;
            List<Integer> idGeneros = new ArrayList<>();
            while (a) {
                idGeneros.add(rs.getInt("id_genero"));
                //  JOptionPane.showMessageDialog(null,idGeneros.toString());
                if (!rs.next()) {
                    a = false;
                }
            }

            List<Musica> musicas = new ArrayList<>();
            GeneroDao generoDao = DaoFactory.createGeneroDao();

            for (Integer idGenero : idGeneros) {                         //idGenero
                musicas.addAll(recomendacoesGenero(generoDao.getGeneroById(idGenero)));
            }

            return musicas;
        } catch (SQLException e) {
            throw new DbException("Erro ao carregar as recomendacoes");
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    //Retorna todas as musicas que possuem pelo menos um genero favoritado pelo usuario e que nao foram avaliadas pelo usuario.
    @Override
    public List<Musica> musicasFavoritasNaoAvaliadas(Usuario usuario) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement("SELECT * FROM usuario_musica WHERE usuario_musica.id_usuario = ?");

            st.setInt(1, usuario.getId());

            rs = st.executeQuery();

            List<Integer> ids = new ArrayList<>();

            //Adiciona todos os ids das musicas ja avaliadas pelo usuario em uma lista.
            while (rs.next()) {
                int aux = rs.getInt("id_musica");
                ids.add(aux);
            }
            List<Musica> list = recomendacoes(usuario);

            //Remove todas as musicas ja avaliadas pelo usuario da lista de recomendacoes.
            for (Iterator<Integer> i = ids.iterator(); i.hasNext();) {
                Integer id = i.next();
                for (Iterator<Musica> j = list.iterator(); j.hasNext();) {
                    Musica musica = j.next();
                    if (musica.getId() == id) {
                        j.remove();
                    }
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DbException("Erro ao carregar musicas nao avaliadas!");
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Integer> musicasFavoritasJaAvaliadas(Usuario usuario) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement("SELECT * FROM usuario_musica WHERE usuario_musica.id_usuario = ?");

            st.setInt(1, usuario.getId());

            rs = st.executeQuery();

            List<Integer> ids = new ArrayList<>();

            //Adiciona todos os ids das musicas ja avaliadas pelo usuario em uma lista.
            while (rs.next()) {
                int aux = rs.getInt("id_musica");
                ids.add(aux);
            }

            //Remove todas as musicas ja avaliadas pelo usuario da lista de recomendacoes.
            return ids;
        } catch (SQLException e) {
            throw new DbException("Erro ao carregar musicas nao avaliadas!");
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    /* public List<Musica> musicasNaoAvaliadas(Usuario usuario) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement("SELECT * FROM usuario_musica WHERE usuario_musica.id_usuario = ?");

            st.setInt(1, usuario.getId());

            rs = st.executeQuery();

            List<Integer> ids = new ArrayList<>();

            //Adiciona todos os ids das musicas ja avaliadas pelo usuario em uma lista.
            while (rs.next()) {
                int aux = rs.getInt("id_musica");
                ids.add(aux);
            }

            /*funcao listarMusicas pega  todas as musicas da base de dados 
            e as coloca em uma lista e aqui as armazemanos em um array dinamico
            List<Musica> list = listarMusicas();

            //Remove todas as musicas ja avaliadas pelo usuario da lista de musicas.
            //percorre cada elemento do vetor ids 
           /* for (Musica musica : list) {
                for (Integer id : ids) {

                    if (musica.getId() == id) {
                        list.remove(musica);
                    }
                }
            }

            for (Iterator<Integer> i = ids.iterator(); i.hasNext();) {
                Integer id = i.next();
                for (Iterator<Musica> j = list.iterator(); j.hasNext();) {
                    Musica musica = j.next();
                    if (musica.getId() == id) {
                        j.remove();
                    }
                }
            }

            return list;
        } catch (SQLException e) {
            throw new DbException("Erro ao carregar musicas nao avaliadas!");
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    } */
    //Avalia a musica informada e adiciona no DB a informacao de qual usuario avaliou.
    @Override
    public void avaliarMusica(Usuario usuario, Musica musica,
            Integer nota
    ) {

        //Verifica se a nota inserida pelo usuario é valida.
        if (nota > 5 || nota < 1) {
            throw new DbException("Apenas valores entre 1 e 5");
        }

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            //Procura se o usuario ja avaliou a musica.
            st = connection.prepareStatement("SELECT * FROM usuario_musica WHERE usuario_musica.id_usuario = ? AND usuario_musica.id_musica = ?");

            st.setInt(1, usuario.getId());
            st.setInt(2, musica.getId());

            rs = st.executeQuery();

            //Se o usuario ja avaliou, joga uma exception avisando que ja foi avaliado.
            if (rs.next()) {
                throw new DbException("Usuario ja avaliou a musica");
            } else {
                //Faz o update da nota e do numero de avaliacoes.
                st = connection.prepareStatement("UPDATE musica SET nota = nota + ? , avaliacoes = avaliacoes + 1 WHERE musica.id = ?");

                st.setInt(1, nota);
                st.setInt(2, musica.getId());

                st.executeUpdate();

                //Adiciona no DB a informacao de que o usuario ja avaliou a musica.
                st = connection.prepareStatement("INSERT INTO usuario_musica(id_usuario, id_musica) VALUES (?, ?)");

                st.setInt(1, usuario.getId());
                st.setInt(2, musica.getId());

                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DbException("Erro na avaliacao da musica!");
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    //Retorna a musica com o id informado.
    @Override
    public Musica getMusicaById(Integer id
    ) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            //Procura no DB a musica pelo id informado.
            st = connection.prepareStatement("SELECT * FROM musica WHERE musica.id = ?");

            st.setInt(1, id);

            rs = st.executeQuery();

            //Se houver uma musica com o id informado, o metodo retorna a musica.
            if (rs != null) {
                rs.next();
                Musica musica = instantiateMusica(rs);
                return musica;
            } //Se nao houver uma musica com o id informado, joga uma exception.
            else {
                throw new DbException("Musica nao encontrada");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    //Retorna uma lista de musicas do genero informado.
    private List<Musica> recomendacoesGenero(Genero genero) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            //Busca no DB todas as musicas de um determinado genero.
            st = connection.prepareStatement("SELECT id_musica FROM genero_musica WHERE genero_musica.id_genero = ?");

            st.setInt(1, genero.getId());

            rs = st.executeQuery();

            List<Musica> musicasGen = new ArrayList<>();

            //Verifica se existem musicas do genero informado. Se nao houver, joga uma exception.
            if (!rs.next()) {
                throw new DbException("Nao existem musicas do genero informado!");
            } else {
                //Enquanto existir musicas do genero, adiciona as musicas na lista.
                while (rs.next()) {
                    //Instancia uma musica com os dados do ResultSet.
                    Musica musica = getMusicaById(rs.getInt("id_musica"));
                    musicasGen.add(musica);
                }
            }
            return musicasGen;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    //Retorna uma musica com os dados do ResultSet informado.
    private Musica instantiateMusica(ResultSet rs) throws SQLException {
        //Instancia uma musica com os dados do RS.
        Musica musica = new Musica();
        musica.setId(rs.getInt("id"));
        musica.setGenId(rs.getInt("genero"));
        musica.setNome(rs.getString("nome"));
        musica.setArtista(rs.getString("artista"));
        musica.setNota(rs.getInt("nota"));
        musica.setAvaliacoes(rs.getInt("avaliacoes"));

        PreparedStatement st = null;
        ResultSet rs1 = null;
        //Procura os generos da musica informada.
        try {
            st = connection.prepareStatement("SELECT id_genero FROM genero_musica WHERE id_musica = ?");

            st.setInt(1, musica.getId());

            rs1 = st.executeQuery();

            //Cria a lista de generos e adiciona todos os generos da musica informada.        
            //Seta a lista criada como a lista de generos da musica.
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs1);
        }

        return musica;
    }
}
