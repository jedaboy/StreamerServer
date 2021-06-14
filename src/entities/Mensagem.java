/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import entities.Status;

/**
 *
 * @author jedaf
 */
public class Mensagem implements Serializable {

    private static final long serialVersionUID = 1L;
    private String operacao;
    private Status status;

    Map<String, Object> params;

    public Mensagem(String operacao) {
        params = new HashMap<>();
        this.operacao = operacao;
//params.put("Login", )
    }

    public void setStatus(Status s) {

        this.status = s;
    }

    public Status getStatus() {

        return status;
    }

    public void setParam(String chave, Object valor) {
        params.put(chave, valor);
    }

    public Object getParam(String chave) {

        return params.get(chave);
    }

    public String getOperacao() {

        return operacao;
    }
}
