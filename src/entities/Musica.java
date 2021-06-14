/**
 *
 * @author Jedaboy/Mateus Oliveira/Guilherme Leme
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Musica implements Serializable {

    private Integer id;
    private Integer genId;
    private String nome;
    private String artista;
    private Integer nota;
    private Integer duracao;
    private Integer avaliacoes;
    private float notaGeral;
    private ImageIcon imageIc;

    private List<Genero> generos = new ArrayList<>();

    public Musica() {
    }

    public Musica(Integer id, Integer genId, String nome, String artista, Integer nota, Integer avaliacoes) {
        this.id = id;
        this.genId = genId;
        this.nome = nome;
        this.artista = artista;
        this.nota = nota;
        this.avaliacoes = avaliacoes;
    }

    public Musica(Integer id, String nome, String artista, Integer nota, Integer avaliacoes, List<Genero> generos, Integer duracao) {
        this.id = id;
        this.nome = nome;
        this.artista = artista;
        this.nota = nota;
        this.avaliacoes = avaliacoes;
        this.generos = generos;
        this.duracao = duracao;
    }

    public ImageIcon getImageIcon() {
        return imageIc;
    }

    public void setImageIcon(ImageIcon imageIc) {
        this.imageIc = imageIc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public Integer getGenId() {
        return genId;
    }

    public void setGenId(Integer genId) {
        this.genId = genId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public float getNotaGeral() {
        return calcNota(avaliacoes, nota);
    }

    public void setNotaGeral(float notaGeral) {
        this.notaGeral = notaGeral;
    }

    public Integer getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(Integer numAvaliacoes) {
        this.avaliacoes = numAvaliacoes;
    }

    public List<Genero> getGeneros() {
        return generos;
    }

    public void setGeneros(List<Genero> generos) {
        this.generos = generos;
    }

    public float calcNota(Integer numAvaliacoes, Integer nota) {
        return nota / numAvaliacoes;
    }

    @Override
    public String toString() {
        return " Nome: " + nome + "  "
                + " Artista: " + artista + "  "
                + " Nota: " + calcNota(avaliacoes, nota);
    }
}
