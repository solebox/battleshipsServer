package hello;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dto.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import javax.sql.RowSet;


@RestController
public class LoginController {

    @Autowired PlayerDao players;


    @RequestMapping("/signUp")
    public HashMap<String, Object> signUp(@RequestBody HashMap<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        String email = requestBody.get("email");
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

            String score = String.valueOf(this.players.getScore(_username));

            ArrayList<HashMap<String,String>> top_users = this.players.getTopUsers();

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

        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        } catch (PlayerDao.PlayerDataException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
