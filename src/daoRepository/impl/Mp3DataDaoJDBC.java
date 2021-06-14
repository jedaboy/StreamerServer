/**
 *
 * @author Jedaboy/Mateus Oliveira/Guilherme Leme
 */
package daoRepository.impl;

import java.sql.Connection;
import daoRepository.Mp3DataDao;
import entities.MusicData;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

public class Mp3DataDaoJDBC implements Mp3DataDao {

    private Connection connection;
    private boolean nextBPressed = false;
    private int userId;

    public Mp3DataDaoJDBC(Connection connection) throws IOException, SQLException {

        this.connection = connection;
        userId = 1;

    }

    public int getId() {

        return userId;
    }

    public void setId(int userId) {

        this.userId = userId;
    }

    @Override
    public void InsereBlob() throws SQLException, UnsupportedAudioFileException, IOException {
        PreparedStatement st = null;
        st = connection.prepareStatement("INSERT INTO blobb(data_music) " + "values(?)");

        File file = new File("C:\\Users\\jedaf\\Desktop\\intheend.mp3");
        FileInputStream fis = new FileInputStream(file);
        st.setBinaryStream(1, (InputStream) fis, (int) (file.length()));

        st.execute();

    }

    @Override
    public void atualizaBlob() throws SQLException, UnsupportedAudioFileException, IOException {
        PreparedStatement st = null;
        st = connection.prepareStatement("UPDATE blobb "
                + "SET data_music = ? "
                + "WHERE id = ?");

        File file = new File("C:\\Users\\jedaf\\Desktop\\intheend.mp3");
        FileInputStream fis = new FileInputStream(file);
        st.setBinaryStream(1, (InputStream) fis, (int) (file.length()));
        st.setInt(2, userId);
        st.execute();

    }

    @Override
    public void InsereFramerate() throws SQLException, UnsupportedAudioFileException, IOException {
        PreparedStatement st = null;
        st = connection.prepareStatement("UPDATE blobb "
                + "SET frame_rate = ? "
                + "WHERE id = ?");

        File file = new File("C:\\Users\\jedaf\\Desktop\\intheend.wav");
        AudioInputStream ais = AudioSystem.getAudioInputStream(file);

        long frameCount = ais.getFrameLength();
        System.out.println("Frame rate: " + frameCount);

        st.setLong(1, frameCount);
        st.setInt(2, userId);
        st.execute();

    }

    @Override
    public void InsereImageBlob() throws SQLException, UnsupportedAudioFileException, IOException {

        PreparedStatement st = null;

        st = connection.prepareStatement("UPDATE blobb "
                + "SET imagem_capa = ? "
                + "WHERE id = ?");

        ImageIcon imageIcon = new ImageIcon("C:\\Users\\jedaf\\Desktop\\18.png");

        Image image = imageIcon.getImage();

        BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", baos);
        } finally {
            try {
                baos.close();
            } catch (Exception e) {
            }
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        st.setBinaryStream(1, (ByteArrayInputStream) bais);
        st.setInt(2, userId);

        st.execute();
    }

    @Override
    public MusicData recebeInputStream() throws SQLException, UnsupportedAudioFileException, IOException {

        ResultSet rs = null;
        PreparedStatement st = null;
        String query = "SELECT * FROM blobb WHERE ID = ?";

        st = connection.prepareStatement(query);
        st.setInt(1, userId);
        rs = st.executeQuery();

        rs.next();
        InputStream bS = rs.getBinaryStream("data_music");
        long fr = rs.getLong("frame_rate");
        InputStream is = new BufferedInputStream(rs.getBinaryStream("imagem_capa"));
        Image image = ImageIO.read(is);
        ImageIcon imageIc = new ImageIcon(image);
        MusicData md = new MusicData(rs.getBinaryStream("data_music"), rs.getLong("frame_rate"), imageIc);

        return md;

    }

    @Override
    public InputStream recebeInputStream2() throws SQLException, UnsupportedAudioFileException, IOException {

        ResultSet rs = null;
        PreparedStatement st = null;
        String query = "SELECT * FROM blobb WHERE ID = ?";

        st = connection.prepareStatement(query);
        st.setInt(1, userId);
        rs = st.executeQuery();

        rs.next();
        InputStream bS = rs.getBinaryStream("data_music");
        long fr = rs.getLong("frame_rate");
        InputStream is = new BufferedInputStream(rs.getBinaryStream("imagem_capa"));
        Image image = ImageIO.read(is);
        ImageIcon imageIc = new ImageIcon(image);
        MusicData md = new MusicData(rs.getBinaryStream("data_music"), rs.getLong("frame_rate"), imageIc);

        return bS;

    }

    @Override
    public long recebeFrameRate() throws SQLException, UnsupportedAudioFileException, IOException {

        ResultSet rs = null;
        PreparedStatement st = null;
        String query = "SELECT frame_rate FROM blobb WHERE ID = ?";

        st = connection.prepareStatement(query);
        st.setInt(1, userId);
        rs = st.executeQuery();
        rs.next();

        long bS = rs.getLong("frame_rate");

        return bS;
    }

    @Override
    public InputStream recebeImagemBlob() throws SQLException, UnsupportedAudioFileException, IOException {

        ResultSet rs = null;
        PreparedStatement st = null;
        String query = "SELECT imagem_capa FROM blobb WHERE ID = ?";

        st = connection.prepareStatement(query);
        st.setInt(1, userId);
        rs = st.executeQuery();
        rs.next();
        InputStream is = new BufferedInputStream(rs.getBinaryStream("imagem_capa"));  
        return is;

    }

}
