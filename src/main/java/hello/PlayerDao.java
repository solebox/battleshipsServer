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

    public String register(String username, String password, String email){
        String result = "failed";
        try{

            Integer count = jdbcTemplate.queryForObject("select count(*) from USERS.Users where username = ?", new Object[]{username}, Integer.class);

            //if user doesn't exist
            if (count > 0) {
                result =  "user_exists";
            }else{
                //insert to table

                int rows_effected =  jdbcTemplate.update("INSERT INTO USERS.Users VALUES(?,?,?, 0)", username, password, email);
                result =  "completed";

            }


            //STEP 6: Clean-up environment
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();

        }
        return result;
    }
}