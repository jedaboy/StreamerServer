/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 
package dbRepository;

import dbRepository.DB;
import entities.Status;
import entities.Mensagem;
import entities.Usuario;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import static java.lang.System.in;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import javazoom.jl.converter.*;
import javazoom.jl.decoder.*;
import javazoom.jl.player.*;

public class S {

    private JButton action;
    private JSlider slider;
    private int secondsToSkip = 0;
    private Thread t1;
    private Timer playTimer;
    private boolean ignoreStateChange = false;
    private int cont = 0;
    private int duracao;
    private Socket skt;
    private int aux;
    private AudioInputStream ain = null;
    private boolean boolFlag = false;
    private int flag = 0;
    private boolean playing;
    private JFrame frame;
    private JPanel p;

    public S() {

        playTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                atualizacaoDeEstado();

            }
        });

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(S.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(S.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(S.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(S.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        //cria a janela
        Scanner sc = new Scanner(System.in);
        frame = new JFrame();
        //cria a barra de progresso do audio e define o min como 0 e o max como 100, iniciando em 0
        duracao = 464;
        slider = new JSlider(0, duracao, 0);
        action = new JButton();
        //adiciona a barra de tarefas a janela
        //p = new JPanel(new GridLayout(3, 3));
        //frame.add(p);
        //frame.pack();
        frame.add(action);
        frame.pack();
        frame.add(slider);
        frame.pack();

        //pega  a musica
        //String fileName = "C:\\Users\\jedaf\\Desktop\\sou-um-milagre-eliana-ribeiro.mp3";
        //carrega o audio pela primeira vez
        // AudioInputStream audioInputStream = program.getAudioInputStream(fileName);
        // fio 1 responsavel por iniciar a musica
        t1 = new Thread() {
            @Override
            public void run() {

                try {
                    skt = DB.getTCP();

                    ObjectInputStream oIS = null;
                    ObjectOutputStream oOS = null;

                    //protocolo do app
                    oOS = new ObjectOutputStream(skt.getOutputStream());
                    oIS = new ObjectInputStream(skt.getInputStream());

                    System.out.println("Enviando Mensagem");

                    String msg = "Fala mundão!";
                    oOS.writeUTF(msg);
                    oOS.flush();

                    Mensagem m = new Mensagem("STREAM");
                    m.setStatus(Status.STREAM);
                    //m.setParam("registro", usuario);

                    oOS.writeObject(m);
                    oOS.flush(); //libera buffer

                    //após uma comunicação inicial de teste ser bem sucedida o envio do audio começa
                    // We read audio data from here
                    // é deste socket que leremos o  stream de audio
                    InputStream audioSrc = skt.getInputStream();
                    // add buffer for mark/reset support
                    InputStream bufferedIn = new BufferedInputStream(audioSrc);
                    ain = AudioSystem.getAudioInputStream(bufferedIn);
                    if (flag == 0) {
                        System.out.println("FLAG");
                        ain.markSupported();
                        ain.mark(Integer.MAX_VALUE);
                        flag = 1;
                    };

                    tocaStreamDeMusica(ain);

                    // program.tocaMusica(audioInputStream);
                } catch (IOException ex) {
                    Logger.getLogger(S.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(S.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(S.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        };
        // aqui criamos um ouvinte para o slider(barra de prog), sempre que ela for alterada pegaremos um valor
        // instanciaremos o audio e tocaremos a musica novamente daquele ponto
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Timer atualizacaoTardia = new Timer(1, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {

                        try {
                            //String fileName = "C:\\Users\\jedaf\\Desktop\\sou-um-milagre-eliana-ribeiro.mp3";
                            // AudioInputStream audioInputStream = program.getAudioInputStream(fileName);

                            if (!slider.getValueIsAdjusting()) {
                                t1.stop();

                                secondsToSkip = slider.getValue();

                                System.out.println("bifurcacao");

                                if (secondsToSkip > aux) {
                                    if (!boolFlag) {
                                        System.out.println("live stream, sempre em frente");

                                        ain.reset();
                                    } else {
                                        System.out.println("resetado, voltou pelo menos uma vez");
                                        ain.reset();
                                    }
                                } else {
                                    System.out.println("reseta");
                                    ain.reset();
                                    boolFlag = true;
                                }

                                //System.out.println("A");
                                // System.out.println("Cheguei aqui");
                                if (secondsToSkip > 0) {

                                    selecionaPontoDaMusica(secondsToSkip);
                                    cont = secondsToSkip;

                                    t1 = new Thread() {
                                        @Override
                                        public void run() {

                                            try {
                                                tocaStreamDeMusica(ain);
                                            } catch (IOException ex) {
                                                Logger.getLogger(S.class.getName()).log(Level.SEVERE, null, ex);
                                            } catch (UnsupportedAudioFileException ex) {
                                                Logger.getLogger(S.class.getName()).log(Level.SEVERE, null, ex);
                                            } catch (LineUnavailableException ex) {
                                                Logger.getLogger(S.class.getName()).log(Level.SEVERE, null, ex);
                                            }

                                        }

                                    };

                                    t1.start();

                                }
                            }

                        } catch (UnsupportedAudioFileException ex) {
                            Logger.getLogger(S.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (LineUnavailableException ex) {
                            Logger.getLogger(S.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(S.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                });

                atualizacaoTardia.setRepeats(false);

                if (ignoreStateChange) {
                    return;
                }
                atualizacaoTardia.restart();

            }
        });

        action.setText("Play");
        action.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                actionMouseClicked(evt);
            }
        });

        frame.setVisible(true);
    }

   /* public static void main(String[] args) throws Exception {

        //instancia a propria classe
        S program = new S();

        // inicia a musica    
        //inicia o cronometro
    }

    public void atualizacaoDeEstado() {
        ignoreStateChange = true;

        Timer cronometro = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                cont++;
                if (cont >= duracao) {
                    cont = 0;

                }
                System.out.println("TEMPO: " + cont);

            }
        });
        cronometro.start();
        cronometro.setRepeats(false);
        cronometro.restart();

        slider.setValue(cont);
        aux = slider.getValue();
        ignoreStateChange = false;
    }

    //pote de ouro!
    private void selecionaPontoDaMusica(int secondsToSkip)
            throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {
        ain.markSupported();
        ain.mark(Integer.MAX_VALUE);
        //BufferedInputStream b = new BufferedInputStream(inputStream);
        // AudioInputStream audioStream = audioInputStream;
        AudioFormat audioFormat = ain.getFormat();

        System.out.println(ain.getFormat());
        AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                audioFormat.getSampleRate(), 16,
                audioFormat.getChannels(),
                audioFormat.getChannels() * 2,
                audioFormat.getSampleRate(),
                false);

        AudioFormat format = convertFormat;
        //  System.out.println("Slider FrameRate: " + format.getFrameRate());
        // System.out.println("Slider FrameSize: " + format.getFrameSize());
        // find out how many bytes you have to skip, this depends on bytes per frame (a.k.a. frameSize)
        long bytesToSkip = 16000 * secondsToSkip;
        //  System.out.println("Formato convertido: " + convertFormat);
        // System.out.println("Slider bytesToSkip: " + bytesToSkip);
        // System.out.println("secondsToSkip: " + secondsToSkip);
        // now skip until the correct number of bytes have been skipped
        long justSkipped = 0;
        // if (flag == 0) {
        //     System.out.println("FLAG");
        //    ain.markSupported();
        //   ain.mark(Integer.MAX_VALUE);
        //   flag = 1;
        // }
        while (bytesToSkip > 0 && (justSkipped = ain.skip(bytesToSkip)) > 0) {
            bytesToSkip -= justSkipped;
        }
        System.out.println("checkpoint2");
    }

    private static final int BUFFER_SIZE = 128000;

    private AudioInputStream getAudioInputStream(String filename) throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(new File(filename));
    }

    private InputStream getInputStream(String filename) throws UnsupportedAudioFileException, IOException {
        InputStream is = new FileInputStream(filename);
        return is;
    }

    private void actionMouseClicked(java.awt.event.MouseEvent evt) {

        if (!playing) {

            // labelFileName.setText("Playing File: " + audioFilePath); //not important
            // System.out.print((int) clip.getMicrosecondLength() / 1_000_000);
            // clip lenght in seconds? this feature will set the slider lenght to the same length of the audio clip
            t1.start();
            action.setText("Stop");
            playing = true;
            playTimer.start();
        } else {

            t1.stop();
            action.setText("Play");
            playing = false;
            playTimer.stop();
        }

    }

    //G(OLD)
    public static void tocaStreamDeMusica(AudioInputStream ain)
            throws IOException, UnsupportedAudioFileException,
            LineUnavailableException {

        SourceDataLine sourceDataLine = null;   // e escreveremos aqui

        try {

            //informação sobre o formato da stream
            AudioFormat format = ain.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            // se o formato não for suportado de forma direta, tenta se efetuar a conversão
            // para PCM.
            if (!AudioSystem.isLineSupported(info)) {
                // Este é o formato PCM para o qual queremos converter nosso audio
                // Estes parametros são informações sobre o formato do audio 
                // não precisa entender para usos casuais.
                AudioFormat pcm
                        = new AudioFormat(format.getSampleRate(), 16,
                                format.getChannels(), true, false);
                // Get a wrapper stream around the input stream that does the
                // transcoding for us.

                //Pega um (wrapper: empacotamento(?) ao redor da stream de entrada que faz a transcrição
                //para nos(???)
                ain = AudioSystem.getAudioInputStream(pcm, ain);

                // atualiza o formato e a variavel "info para os novos dados que foram transcritos
                format = ain.getFormat();
                info = new DataLine.Info(SourceDataLine.class, format);
            }

            // Abre a linha através da qual tocaremos o stream de audio
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceDataLine.open(format);

            // aloca um buffer para ler e escrever a stream de entrada na linha
            // Make it large enough to hold 4k audio frames*.
            // perceba que a SourceDataLine possui o proprio buffer interno.
            int framesize = format.getFrameSize();
            byte[] buffer = new byte[4 * 1024 * framesize]; // o buffer
            int numbytes = 0;                               // a quantia de bytes

            // nos ainda não iniciamos a linha
            boolean started = false;

            for (;;) {  // saimos do loop quando chegamos no fim da stream
                // Primeiro, lemos alguns bytes da stream de entrada

                // if (flag == 0) {
                //    System.out.println("FLAG");
                //    ain.markSupported();
                //   ain.mark(Integer.MAX_VALUE);
                //   flag = 1;
                // }
                int bytesread = ain.read(buffer, numbytes, buffer.length - numbytes);
                // se não tiver mais bytes para se ler estamos encerrados
                if (bytesread == -1) {
                    break;
                }
                numbytes += bytesread;

                // Agora nos pegamos um pouco de bytes de audio  para escrever nesta linha,
                // tocamos a linha que ira reproduzir os dados que estiverem nela
                if (!started) {
                    sourceDataLine.start();
                    started = true;
                }

                // We must write bytes to the line in an integer multiple of
                // the framesize.  So figure out how many bytes we'll write.
                //precisamos escrever os bytes na linha em um inteiro multiplo
                //do tamnho do framesize, então .... quantos bytes nos iremos escrever
                int bytestowrite = (numbytes / framesize) * framesize;

                // Now write the bytes. The line will buffer them and play
                // them. This call will block until all bytes are written.
                //agora escrevemos os bytes a linha ira buffer(?) e então nos tocaremos ela, 
                // Esta chamada será bloqueada até que todos os bytes sejam gravados.
                sourceDataLine.write(buffer, 0, bytestowrite);

                // If we didn't have an integer multiple of the frame size, 
                // then copy the remaining bytes to the start of the buffer.
                // Se não tivermos um múltiplo inteiro do tamanho do frame,
                // então copiamos os bytes restantes para o início do buffer.
                int remaining = numbytes - bytestowrite;
                if (remaining > 0) {
                    System.arraycopy(buffer, bytestowrite, buffer, 0, remaining);
                }
                numbytes = remaining;
            }

            // Now block until all buffered sound finishes playing.
            // Agora bloqueamos até que todo o som armazenado no buffer termine de tocar.
            sourceDataLine.drain();
        } finally { // Always relinquish the resources we use
            //sempre feche os recusos que utilizar (guarde encerre ou algo do genero)
            if (sourceDataLine != null) {
                sourceDataLine.close();
            }
            if (ain != null) {
                ain.close();
            }
        }
    }

    public static void mp3ToWav(File mp3Data) throws UnsupportedAudioFileException, IOException {
        // open stream
        AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(mp3Data);
        AudioFormat sourceFormat = mp3Stream.getFormat();
        // create audio format object for the desired stream/audio format
        // this is *not* the same as the file format (wav)
        AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(), 16,
                sourceFormat.getChannels(),
                sourceFormat.getChannels() * 2,
                sourceFormat.getSampleRate(),
                false);
        // create stream that delivers the desired format
        AudioInputStream converted = AudioSystem.getAudioInputStream(convertFormat, mp3Stream);
        // write stream into a file with file format wav
        AudioSystem.write(converted, AudioFileFormat.Type.WAVE, new File("/tmp/out.wav"));
    }
}   */

