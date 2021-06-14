/**
 *
 * @author Jedaboy/Mateus Oliveira/Guilherme Leme
 */
package application;

/* Importando por garantia */
import entities.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.util.Collections;
import javax.swing.SwingUtilities;
import java.awt.Container;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import server.Server;

/**
 *
 * @author jedaboy
 */
public class ClassePrincipal {

    private Socket skt;
    static Server server;
    static int cont = 0;
    static int contTotal = 0;
    static Usuario usuarioEncerrado = null;

    void setSocket(Socket skt) {
        this.skt = skt;
    }

    Socket getSocket() {
        return skt;
    }

    public static void main(String[] args) throws IOException {
        try {

            // List<Thread> clientesOnline = new ArrayList<>();
            int porta;
            Scanner sc = new Scanner(System.in);
            server = new Server();

            System.out.println("Informe a porta (ngrok): thread principal  ");
            porta = sc.nextInt();
            Thread changeListener;
            server.criarServerSocket(porta);
            while (true) {
                ClassePrincipal cp = new ClassePrincipal();

                System.out.println("Aguardando conexão: thread principal ");
                cp.setSocket(server.esperaConexao());
                System.out.println("Conexão estabelecida: thread principal ");

                new Thread() {
                    @Override
                    public void run() {
                        try {

                            usuarioEncerrado = server.trataConexao(cp.getSocket(), contTotal);
                            System.out.println("Cliente do ID: " + usuarioEncerrado.getId() + " teve sua sessão encerrada : thread cliente ");
                        } catch (IOException ex) {
                            Logger.getLogger(ClassePrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (UnsupportedAudioFileException ex) {
                            Logger.getLogger(ClassePrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {

                            Logger.getLogger(ClassePrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            Logger.getLogger(ClassePrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }.start();

                // clientesOnline.get(contTotal).start();
                System.out.println("numero de clientes Total: " + contTotal + " : thread principal");
                System.out.println("numero de clientes online: " + cont + " : thread principal");
                changeListener = new Thread() {
                    @Override
                    public void run() {
                        do {

                            try {

                                sleep(50);
                                if (usuarioEncerrado != null) {
                                    try {
                                        cont--;
                                        usuarioEncerrado = null;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ClassePrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        } while (true);
                    }
                };

                contTotal++;
                cont++;
                changeListener.start();

            }

        } catch (IOException e) {
            System.out.println("null");
            e.printStackTrace();
        }
    }

}
