/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author jedaf
 */
import java.io.*;


public class ObjetoFluxoEntrada extends ObjectInputStream {

       
     public ObjetoFluxoEntrada(InputStream in) throws IOException {
    super(in);
  }

  @Override
  protected void readStreamHeader() throws IOException {

  }
   
    
}

