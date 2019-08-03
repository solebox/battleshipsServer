package dto;

public class Cell {

    public enum StateEnum {EMPTY, SHIP_PART, BOMBED, BOMBED_SHIP_PART}
    private StateEnum state;

    public Cell(StateEnum state) {
        this.setState(state);
    }

    public StateEnum getState() {
        return state;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }



}
