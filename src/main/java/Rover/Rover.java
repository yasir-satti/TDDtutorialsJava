package Rover;

import java.util.Arrays;

public class Rover {
    private String facing;

    public Rover(String facing) {
        this.facing = facing;
    }

    public String getFacing() {
        return facing;
    }

    public void go(String instruction) {
        if(instruction == "R") {
            right();
        } else{
            left();
        }
    }

    private void right() {
        turn("N", "E", "S", "W");
    }

    private void left() {
        turn("W", "S", "E", "N");
    }

    private void turn(String n, String e, String s, String w) {
        String[] campass = new String[]{n, e, s, w};
        int index = Arrays.asList(campass).indexOf(facing);
        facing = campass[(index + 1) % 4];
    }
}
