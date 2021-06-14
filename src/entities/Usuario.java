package entities;

/**
 *
 * @author Jedaboy/Mateus Oliveira/Guilherme Leme
 */

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nome;
    private String senha;
    private String email;
    private Integer id = null;
    //jeda
    private boolean logou;

    public Set<Genero> generosFavoritos = new HashSet<>();

    public Usuario() {
    }

    public Usuario(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
        // this.logou = logou;
    }

    public Usuario(String nome, String senha, String email) {
        this.nome = nome;
        this.senha = senha;
        this.email = email;

    }

    public Usuario(String nome, String senha, String email, Integer id) {
        this.nome = nome;
        this.senha = senha;
        this.email = email;
        this.id = id;

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getLogou() {
        return logou;
    }

    public void setLogou(boolean logou) {
        this.logou = logou;
    }

    public Set<Genero> getGenerosFavoritos() {
        return generosFavoritos;
    }

    public void setGenerosFavoritos(Set<Genero> generoFavoritos) {
        this.generosFavoritos = generoFavoritos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{"
                + "nome='" + nome + '\''
                + ", senha='" + senha + '\''
                + ", email='" + email + '\''
                + ", id=" + id
                + '}';
    }
}
