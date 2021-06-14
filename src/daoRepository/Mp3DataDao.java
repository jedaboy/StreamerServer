/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daoRepository;

import entities.MusicData;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

/**
 *
 * @author jedaf
 */
public interface Mp3DataDao {

    public void InsereBlob() throws SQLException, UnsupportedAudioFileException, IOException;

    public void InsereFramerate() throws SQLException, UnsupportedAudioFileException, IOException;

    public MusicData recebeInputStream() throws SQLException, UnsupportedAudioFileException, IOException;

    public InputStream recebeInputStream2() throws SQLException, UnsupportedAudioFileException, IOException;

    public long recebeFrameRate() throws SQLException, UnsupportedAudioFileException, IOException;

    public void InsereImageBlob() throws SQLException, UnsupportedAudioFileException, IOException;

    public InputStream recebeImagemBlob() throws SQLException, UnsupportedAudioFileException, IOException;

    public void atualizaBlob() throws SQLException, UnsupportedAudioFileException, IOException;

    public void setId(int userId);

    public int getId();
}
