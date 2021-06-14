/**
 *
 * @author Jedaboy/Mateus Oliveira/Guilherme Leme
 */
package daoRepository;

import daoRepository.impl.GeneroDaoJDBC;
import daoRepository.impl.Mp3DataDaoJDBC;
import daoRepository.impl.MusicaDaoJDBC;
import daoRepository.impl.UsuarioDaoJDBC;
import dbRepository.DB;
import java.io.IOException;
import java.sql.SQLException;

public class DaoFactory {

    public static UsuarioDao createUsuarioDao() {
        return new UsuarioDaoJDBC(DB.getConnection());
    }

    public static GeneroDao createGeneroDao() {
        return new GeneroDaoJDBC(DB.getConnection());
    }

    public static MusicaDao createMusicaDao() {
        return new MusicaDaoJDBC(DB.getConnection());
    }

    public static Mp3DataDao createMp3DataDao() throws IOException, SQLException {
        return new Mp3DataDaoJDBC(DB.getConnection());
    }
}
