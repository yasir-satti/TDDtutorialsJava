package Rover;

public class Rover {
    private String facing;

    public Rover(String facing) {
        this.facing = facing;
    }

    public String getFacing() {
        return facing;
    }

    public void go(String instruction) {
        if (facing == "N")
            facing = "E";
        else if (facing == "E")
            facing = "S";
        else if (facing == "S")
            facing = "W";
        else
            facing = "N";
    }
}
