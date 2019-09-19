package hello;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dto.Player;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import javax.sql.RowSet;


@RestController
public class LoginController {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/USERS?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    static final String USER = "oren";
    static final String PASS = "1234";
    Connection conn = null;
    Statement stmt = null;
    static String username, password, email;
    @Autowired GameDao battleShips;
    @Autowired PlayerDao players;


    @RequestMapping("/signUp")
    public HashMap<String, Object> signUp(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        HashMap<String, Object> result = new HashMap<>();
        String message = players.register(username, password, email);
        result.put("message",message);
        switch(message) {
            case "completed":
                result.put("success", true);
                break;
            default:
               result.put("success", false);
        }
        return result;
    }

    @RequestMapping("/signIn")
    public HashMap<String, Object> signIn(@RequestBody HashMap<String, String> requestBody) {
        String _username = requestBody.get("username");
        String _password = requestBody.get("password");
        HashMap<String, Object> result = new HashMap<>();
        String message = "failed";
        try{
            boolean is_logged_in = this.players.isPasswordCorrect(_username, _password);

            if (!is_logged_in) message =  "wrong_password";
            else {
                 message = "signed_in";
            }

            //STEP 6: Clean-up environment

        }catch(Exception e){
            //Handle errors for Class.forName
            message = ""+ e;
            e.printStackTrace();
        }
        result.put("message", message);
        switch(message) {
            case "signed_in":
                result.put("success", true);
                result.put("score", 23);
                break;
            default:
                result.put("success", false);
        }
        return result;
    }

    @RequestMapping("/getLobbyData")
    public HashMap<String, Object> getLobbyData(@RequestBody HashMap<String, String> requestBody) {
        String _username = requestBody.get("username");
        HashMap<String, Object> result = new HashMap<>();
        try{

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();

            String sql = "SELECT score FROM Users WHERE username='" + _username +"'";
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            String score = String.valueOf(rs.getInt("score"));

            sql = "SELECT username, score FROM Users ORDER BY score DESC";
            rs = stmt.executeQuery(sql);

            ArrayList<HashMap<String,String>> top_users = new ArrayList<>();
            while(rs.next()){
                String username = rs.getString("username");
                String user_score = String.valueOf(rs.getInt("score"));
                HashMap<String, String> pair = new HashMap<>();
                pair.put("username", username);
                pair.put("score", user_score);
                top_users.add(pair);
            }
            result.put("top_users", top_users);

            HashMap<String, String> rooms = new HashMap<>();
            for (int i = 0; i < 3; i++) {
                String room_state = MyServerApplication.getInstance().getMyAllRooms()[i].getCurrentState().toString();
                String room_name = MyServerApplication.getInstance().getMyAllRooms()[i].getName();
                rooms.put(room_name, room_state);
            }
            result.put("rooms", rooms);
            result.put("my_score", score);

            return result;

            //STEP 6: Clean-up environment
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try

        return null;
    }
    
}
