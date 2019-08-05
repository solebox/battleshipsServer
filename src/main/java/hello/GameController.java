package hello;

import dto.Board;
import dto.GameRoom;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {
    @RequestMapping("/game")
    public Board index(Board board) {
        return board;
    }
    @GetMapping(value = "/gameroom/{roomId}")
    public @ResponseBody GameRoom getGameRoom(@PathVariable Integer roomId) {

        if (MyServerApplication.getInstance().getMyAllRooms()!=null &&roomId <MyServerApplication.getInstance().getMyAllRooms().length )
            return MyServerApplication.getInstance().getMyAllRooms()[roomId];
        else
            return null;

    }
}
