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
        String[] campass = new String[]{"N", "E", "S", "W"};
        int index = Arrays.asList(campass).indexOf(facing);
        facing = campass[(index + 1) % 4];
    }
}
