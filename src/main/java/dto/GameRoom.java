package dto;

public class GameRoom {

    private String name;
    private Game game;
    private GameRoomState currentState;
    public enum GameRoomState{Empty, Not_empty, Full}

    //name getter & setter
    public String getName() {
        return name;
    }

    public void resetRoom() {
        currentState = GameRoomState.Empty;
        game.resetGame();
        this.game = null;
        this.game = new Game();
    }

    //game getter & setter
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }

    //currentState getter & setter
    public GameRoomState getCurrentState() {
        return currentState;
    }
    public void setCurrentState()
    {
        if (game.getPlayers().isEmpty()) currentState = GameRoomState.Empty;
        else if (game.getPlayers().size() == 2) currentState = GameRoomState.Full;
        else currentState = GameRoomState.Not_empty;
    }

    public GameRoom(String name)
    {
        this.name = name;
        this.game = new Game();
        this.currentState = GameRoomState.Empty;
    }
}
