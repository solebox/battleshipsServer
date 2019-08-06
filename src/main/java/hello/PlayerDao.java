package hello;

import dto.Game;
import dto.Player;
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
public class PlayerDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PlayerDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public List<Player> getPlayers(){
        List<Player> list = new ArrayList<Player>();
        list = this.jdbcTemplate.query("SELECT username, password, email, score FROM Users", new RowMapper() {
            public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
                Player player = new Player(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                player.setScore(rs.getInt("score"));
                return player;
            }
        });
        return list;
    }

    public JdbcTemplate getTemplate(){
        return this.jdbcTemplate;
    }

    public boolean isPasswordCorrect(String username, String password){
        Integer count = jdbcTemplate.queryForObject("select count(*) from USERS.Users where username = ? and password = ?", new Object[]{username, password}, Integer.class);
        return count > 0;
    }
}