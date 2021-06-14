/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;

/**
 *
 * @author jedaf
 */
public enum Status implements Serializable {

    OK, ERROR, PARAMERROR, AUTENTICACAO, REGISTRO, SESSAO_ENCERRADA, GENERO, GENERO_ADD, GENERO_RMV, GENERO_FAV, GENERO_BY_ID, GENERO_USUARIO, ALL_MUSICS, MUSICS_FAV_AV,
    AVALIACAO, STREAM, STREAM_ID, STREAM_END;

    private static final long serialVersionUID = 1L;
}
