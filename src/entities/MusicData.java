/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import daoRepository.Mp3DataDao;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 *
 * @author jedaf
 */
public class MusicData implements Serializable {
    //variaveis de controle da reprodução de audio

    private long frameCount;
    private double duration;
    private AudioFormat format;
    private Clip clip;
    //irrelevante
    boolean botaoNext = false;
    //
    private boolean playing = false;
    private Timer playTimer;
    private boolean ignoreStateChange = false;
    Mp3DataDao mp3DataDao;

    InputStream is;

    //imagem que acompanha a musica 
    ImageIcon image;

    public MusicData(ImageIcon image, Mp3DataDao mp3DataDao, Timer playTimerr, long frameCount, double duration, AudioFormat format, Clip clip, boolean playingg) throws SQLException, LineUnavailableException, IOException, UnsupportedAudioFileException {
        playTimer = playTimerr;
        playing = playingg;
        this.clip = clip;
        this.format = format;
        this.duration = duration;
        this.image = image;
        this.frameCount = frameCount;
        this.mp3DataDao = mp3DataDao;
    }

    public MusicData(InputStream is, long frameCount, ImageIcon image) {
        this.is = is;
        this.frameCount = frameCount;
        this.image = image;
    }

    public MusicData() {
    }

    public InputStream getInputStream() {
        return is;
    }

    public void setFrameCount(long frameCount) {
        this.frameCount = frameCount;
    }

    public long getFrameCount() {
        return frameCount;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }

    public ImageIcon getImage() {
        return image;
    }

    public void setFormat(AudioFormat format) {
        this.format = format;
    }

    public AudioFormat getFormat() {
        return format;
    }

    public void setClip(Clip clip) {
        this.clip = clip;
    }

    public Clip getClip() {
        return clip;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean getPlaying() {
        return playing;
    }

    public void setPlayTimer(Timer playTimer) {
        this.playTimer = playTimer;
    }

    public Timer getPlayTimer() {
        return playTimer;
    }

    public void setMp3DataDao(Mp3DataDao mp3DataDao) {
        this.mp3DataDao = mp3DataDao;
    }

    public Mp3DataDao getMp3DataDao() {
        return mp3DataDao;
    }

    public void clipStop() {

        clip.stop();
    }

}
