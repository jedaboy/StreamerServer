/**
 *
 * @author Jedaboy/Mateus Oliveira/Guilherme Leme
 */
package entities;

import java.io.Serializable;
import java.util.Objects;

public class Genero implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nome;
    private Integer id;

    public Genero() {
    }

    public Genero(String nome, Integer id) {
        this.nome = nome;
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Genero genero = (Genero) o;
        return Objects.equals(id, genero.id);
    }

    @Override
    public String toString() {
        return nome;
    }
}
