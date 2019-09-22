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
import java.util.HashMap;
import java.util.List;

@Repository
public class PlayerDao {
    private JdbcTemplate jdbcTemplate;
    private int DEFAULT_TOP_USER_COUNT = 3;

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

    public Integer getScore(String username) throws PlayerDataException {
        Integer score;
            try {
                score = jdbcTemplate.queryForObject("SELECT score FROM USERS.Users WHERE username = ?", new Object[]{username}, Integer.class);
            } catch(Exception e) {
                   throw new PlayerDataException("cant get player score: " + e.getMessage());
            }

        return score;
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
    public ArrayList<HashMap<String, String>> getTopUsers(){
        return getTopUsers(DEFAULT_TOP_USER_COUNT);
    }
    public ArrayList<HashMap<String,String>> getTopUsers(int count) {

        List<HashMap<String, String>> top_users = new ArrayList<>();
        top_users = this.jdbcTemplate.query("SELECT username, score FROM Users ORDER BY score DESC limit ?", new Object[]{count}, new RowMapper() {
            public HashMap<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                HashMap<String, String> player = new HashMap<>();
                player.put("username", rs.getString("username"));
                player.put("score", rs.getString("score"));
                return player;
            }
        });
        return (ArrayList<HashMap<String,String>>) top_users;
    }

    public class PlayerDataException extends Throwable {
        private String message;
        public PlayerDataException(String message) {
            this.message = message;
        }
        @Override
        public String getMessage() {
            return message;
        }
    }
}