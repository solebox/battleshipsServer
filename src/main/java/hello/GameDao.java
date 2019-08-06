package hello;

import dto.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GameDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public GameDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public List<Game> getSoldier(){
        List<Game> list = new ArrayList<Game>();
        list = this.jdbcTemplate.query("select name, age from soldier", new RowMapper() {
            public Game mapRow(ResultSet rs, int rowNum) throws SQLException {
                Game game = new Game();
                game.setState(Game.GameState.ACTIVE);
                return game;
            }
        });
        return list;
    }

    public JdbcTemplate getTemplate(){
        return this.jdbcTemplate;
    }
}