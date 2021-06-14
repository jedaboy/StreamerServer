package daoRepository;

import entities.Musica;
import entities.Usuario;

import java.util.List;

public interface MusicaDao {

    List<Musica> listarMusicas();

    List<Musica> recomendacoes(Usuario usuario);

    List<Musica> musicasFavoritasNaoAvaliadas(Usuario usuario);

    //List<Musica> musicasNaoAvaliadas(Usuario usuario);
    public List<Integer> musicasFavoritasJaAvaliadas(Usuario usuario);

    Musica getMusicaById(Integer id);

    void avaliarMusica(Usuario usuario, Musica musica, Integer nota);

}
