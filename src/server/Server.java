/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import daoRepository.DaoFactory;
import daoRepository.GeneroDao;
import daoRepository.Mp3DataDao;
import daoRepository.MusicaDao;
import daoRepository.UsuarioDao;
import entities.Genero;
import entities.Mensagem;
import entities.MusicData;
import entities.Musica;
import entities.Status;
import entities.Usuario;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

/**
 *
 * @author jedaf
 */
public class Server {

    private ServerSocket sS;
    private boolean flag;
    private ObjectInputStream oIS = null;
    private ObjectOutputStream oOS = null;
    private MusicData musicData;
    LocalDateTime agora;
    DateTimeFormatter formatterData;
    String dataFormatada;
    DateTimeFormatter formatterHora;
    String horaFormatada;

    public void criarServerSocket(int porta) throws IOException {

        sS = new ServerSocket(porta);
    }

    public Socket esperaConexao() throws IOException {
        Socket skt = sS.accept();
        return skt;
    }

    public Usuario trataConexao(Socket skt, int cod) throws IOException, UnsupportedAudioFileException, ClassNotFoundException, SQLException {

        Usuario usuarioEncerrado = null;
        
        oOS = new ObjectOutputStream(skt.getOutputStream());
        oIS = new ObjectInputStream(skt.getInputStream());

  
        try {
            //protocolo do app
            while (usuarioEncerrado == null) {
                //tier 1
                
              
                String msg = oIS.readUTF();

                 agora = LocalDateTime.now();
                //formatar a data
                 formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu");
                 dataFormatada = formatterData.format(agora);
                //formatar a hora
                 formatterHora = DateTimeFormatter.ofPattern("HH:mm:ss");
                 horaFormatada = formatterHora.format(agora);
                System.out.println(horaFormatada);
                System.out.println(dataFormatada);
                
                if (!msg.equals(null)) {
                    System.out.println("Mensagem recebida: " + msg);
                }
          
                System.out.println("Efetuando contato");
                Mensagem m = (Mensagem) oIS.readObject();
                String operacao = m.getOperacao();

                //TROCAR PARA UM SWITCH
                switch (operacao) {
                    case "LOGIN":
                        try {
                        Usuario usuario = (Usuario) m.getParam("Login");
                        UsuarioDao usuarioDao = DaoFactory.createUsuarioDao();
                        usuario = usuarioDao.login(usuario);
                        oOS.writeObject(usuario);
                        oOS.flush();
                        System.out.println(usuario);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("Falha no login");
                    }
                    break;
                    
                    
                    case "REGISTRO":
                         try {
                        Usuario usuario = (Usuario) m.getParam("registro");
                        UsuarioDao usuarioDao = DaoFactory.createUsuarioDao();
                        if(usuarioDao.register(usuario)){
                        System.out.println(usuario);
                        m.setStatus(Status.OK);
                        oOS.writeObject(m);
                        oOS.flush(); //libera buffer
                        }else{
                         System.out.println("Falha no registro");
                        m.setStatus(Status.PARAMERROR);
                        oOS.writeObject(m);
                        oOS.flush(); //libera buffer
                        }
                    } catch (Exception ex) {
                       ex.printStackTrace();
                    }
                    break;
                    
                    
                    case "ENCERRAMENTO":
                        try {
                        usuarioEncerrado = (Usuario) m.getParam("Encerra_Sessao");
                        usuarioEncerrado.setId(cod);
                        System.out.println("Sessão de " + usuarioEncerrado + " foi encerrada");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("Falha no Encerramento");
                    }
                    break;
                    
                    
                    case "GENEROS":
                        try {
                        if (m.getStatus() == Status.GENERO) {
                            GeneroDao generoDao = DaoFactory.createGeneroDao();
                            List<Genero> generos = generoDao.getGeneros();
                            Mensagem reply = new Mensagem("GENEROS");
                            reply.setStatus(Status.GENERO);

                            if (generos == null) {
                                System.out.println("NULO!");
                            }

                            reply.setParam("Generos", generos);
                            oOS.writeObject(reply);
                            oOS.flush();
                            System.out.println("Lista de Generos enviada");
                        }
                        if (m.getStatus() == Status.GENERO_ADD) {

                            GeneroDao generoDao = DaoFactory.createGeneroDao();
                            generoDao.addGeneroFav((Usuario) m.getParam("Usuario"), (Genero) m.getParam("Genero"));

                            System.out.println("Generos adicionados");
                        }
                        if (m.getStatus() == Status.GENERO_RMV) {

                            GeneroDao generoDao = DaoFactory.createGeneroDao();
                            generoDao.rmvGeneroFav((Usuario) m.getParam("Usuario"), (Genero) m.getParam("Genero"));

                            System.out.println("Generos removidos");
                        }
                        if (m.getStatus() == Status.GENERO_FAV) {
                            GeneroDao generoDao = DaoFactory.createGeneroDao();
                            List<Genero> generos = generoDao.getGeneroFav((Usuario) m.getParam("Usuario"));
                            Mensagem reply = new Mensagem("GENEROS");
                            reply.setStatus(Status.GENERO_FAV);

                            if (generos == null) {
                                System.out.println("NULO!");
                            }

                            reply.setParam("Generos_Favoritos", generos);

                            oOS.writeObject(reply);
                            oOS.flush();
                            System.out.println("Lista de Generos enviada");
                        }
                        if (m.getStatus() == Status.GENERO_BY_ID) {
                            GeneroDao generoDao = DaoFactory.createGeneroDao();
                            Genero genero = generoDao.getGeneroById((Integer) m.getParam("IDGenero"));
                            Mensagem reply = new Mensagem("GENEROS");
                            reply.setStatus(Status.GENERO_BY_ID);

                            reply.setParam("Genero_By_ID", genero);

                            oOS.writeObject(reply);
                            oOS.flush();
                            System.out.println("Generos by id sendIH");
                        }
                        if (m.getStatus() == Status.GENERO_USUARIO) {
                            GeneroDao generoDao = DaoFactory.createGeneroDao();
                            List<Integer> ids = generoDao.getIdGeneroFav((Usuario) m.getParam("Genero_Usuario"));
                            Mensagem reply = new Mensagem("GENEROS");
                            reply.setStatus(Status.GENERO_USUARIO);

                            reply.setParam("IdGeneroFav", ids);

                            oOS.writeObject(reply);
                            oOS.flush();
                            System.out.println("Generos by id sendIH");
                        }
                    } catch (Exception ex) {
                         ex.printStackTrace();
                        System.out.println("Falha na operação generos");
                    }
                    break;
                    
                    
                    case "MUSICAS":
                        try {
                        if (m.getStatus() == Status.ALL_MUSICS) {
                            MusicaDao musicaDao = DaoFactory.createMusicaDao();
                            List<Musica> musicas;
                            musicas = musicaDao.listarMusicas();
                            Mensagem reply = new Mensagem("MUSICAS");
                            reply.setStatus(Status.ALL_MUSICS);

                            reply.setParam("All_Music", musicas);

                            oOS.writeObject(reply);
                            oOS.flush();
                            System.out.println("Todas as musicas foram enviadas");
                        }
                        if (m.getStatus() == Status.MUSICS_FAV_AV) {
                            MusicaDao musicaDao = DaoFactory.createMusicaDao();
                            List<Integer> musicas;
                            musicas = musicaDao.musicasFavoritasJaAvaliadas((Usuario) m.getParam("Usuario"));
                            Mensagem reply = new Mensagem("MUSICAS");
                            reply.setStatus(Status.MUSICS_FAV_AV);

                            reply.setParam("M_F_J_A", musicas);

                            oOS.writeObject(reply);
                            oOS.flush();
                            System.out.println("Musicas favoritas ja avaliadas foram enviadas");
                        }
                        if (m.getStatus() == Status.AVALIACAO) {
                            MusicaDao musicaDao = DaoFactory.createMusicaDao();
                            musicaDao.avaliarMusica((Usuario) m.getParam("Usuario"), (Musica) m.getParam("Musica"), (Integer) m.getParam("Nota"));

                            System.out.println("Nota adicionada");
                        }
                    } catch (Exception ex) {
                         ex.printStackTrace();
                        System.out.println("Falha na operação de avaliação");
                    }
                    break;
                    
                    
                    case "STREAM":
                        //tier 2
                        try {
                      
                        if (m.getStatus() == Status.STREAM_ID) {
                            
                            System.out.println("StreamID");
                            //pego audio db
                            Mp3DataDao data = DaoFactory.createMp3DataDao();
                            data.setId((int) m.getParam("Musica_id"));
                            musicData = data.recebeInputStream();
                             
                            
                            msg = oIS.readUTF();
                            System.out.println(msg);
                            //imagem_capa stream
                            ObjetoFluxoSaida oFS = new ObjetoFluxoSaida(skt.getOutputStream());
                            InputStream in = new BufferedInputStream(data.recebeImagemBlob());
                             if (skt.isBound()) {

                               byte buffer[] = new byte[ 1024 ];
                               int count;

                               while ((count = in.read(buffer)) != -1 ) {
                                   
                                  oFS.write(buffer, 0, count);
         
                                }                                  
                             }
                             oFS.flush();
                             agora = LocalDateTime.now();
                            //formata a data
                             formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu");
                            dataFormatada = formatterData.format(agora);
                            //formata a hora
                             formatterHora = DateTimeFormatter.ofPattern("HH:mm:ss");
                             horaFormatada = formatterHora.format(agora);
                            System.out.println(horaFormatada);
                            System.out.println(dataFormatada);
                             
                                       
                            flag = false;
                        }
                        if (m.getStatus() == Status.STREAM_END) {
                            System.out.println("STREAM_END");
                            flag = true;
                        }
                                  
                       
                    if(m.getStatus() == Status.STREAM_ID){
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    System.out.println("rodando thread: tercearia");
                                     //audio stream
                                    ObjetoFluxoSaida oFS =  new ObjetoFluxoSaida(skt.getOutputStream());
                                   InputStream in = new BufferedInputStream(musicData.getInputStream());

                                    if (skt.isBound()) {

                                        byte buffer[] = new byte[ 512 ];
                                        int count;
                                        int totalBytesWritten = 0;
                                        while ((count = in.read(buffer)) != -1 && !flag) {

                                            oFS.write(buffer, 0, count);
                                            totalBytesWritten += count;
                                            
                                           if(!flag){
                                            sleep(15);
                                           }
                                        }
                                        
                                        oFS.flush();
                                         System.out.println("Bytes totais escritos: " + totalBytesWritten);
                                         
                                         
                                         agora = LocalDateTime.now();
                                         //formata a data
                                         formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu");
                                         dataFormatada = formatterData.format(agora);
                                         //formata a hora
                                         formatterHora = DateTimeFormatter.ofPattern("HH:mm:ss");
                                         horaFormatada = formatterHora.format(agora);
                                         System.out.println(horaFormatada);
                                         System.out.println(dataFormatada);
                              
                                    }
                                                           
                                    System.out.println("audio enviado: thread tercearia encerrada ");

                                } catch (EOFException exception) {
                                    exception.printStackTrace();
                                     System.out.println("Flag : tercearia: 2  " + flag );
                                    System.out.println("Musica interrompida EOF: tercearia ");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                     System.out.println("Flag : tercearia: 3 " + flag );
                                    System.out.println("Musica interrompida IOF: tercearia");
                                } catch (Exception ex) {
                                    System.out.println("StreamCorruptedException: invalid type code: AC");
                                }
                                 
                            }
                        }.start();
                           
                        
                       }
                      } catch (Exception ex) {
                             ex.printStackTrace();
                        System.out.println("Erro na operação Stream: secundaria");
                    }
                        break;
                    default:
                    System.out.println("Erro nas operações: secundaria");
                }

                    if (usuarioEncerrado == null) {
                        System.out.println("Rodando... : secundaria");
                    }
                }
                return usuarioEncerrado;
            }catch (Exception ex) {
            System.out.println("Flag : tercearia: 5 " + flag );
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            usuarioEncerrado = new Usuario();
            usuarioEncerrado.setId(cod);
            return usuarioEncerrado;
        }finally {
            fechaSocket(skt);

        }

        }



    public void fechaSocket(Socket skt) throws IOException {
        skt.close();
    }

}
// esse programinha possui cerca de 2.851 linhas de codigo
//servidor + cliente possuem aproximadamente 8.680 linhas de codigo