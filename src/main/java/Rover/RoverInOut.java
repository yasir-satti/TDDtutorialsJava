package Rover;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RoverInOut {
    private String facing;
    private int endY;
    private int endX;

    public RoverInOut(String facing, int x, int y) {
        this.facing = facing;
        this.endX = x;
        this.endY = y;
    }

    public String getFacing() {

        return facing;
    }

    public int[] getPosition() {
        return new int[]{endX, endY};
    }

    public Runnable mapInstructionToCommand(Character instruction) {
        Map<Character, Runnable> commands = new HashMap<Character, Runnable>();
        commands.put('F', this::forward);
        commands.put('B', this::back);
        commands.put('R', this::right);
        commands.put('L', this::left);
        return commands.get(instruction);
    }

    public void right() {
        turn("N", "E", "S", "W");
    }

    public void left() {
        turn("W", "S", "E", "N");
    }

    private void forward() {
        if (facing == "N")
           endY += 1;
        else if (facing == "E")
            endX += 1;
        else if (facing == "S")
            endY -= 1;
        else if (facing == "W")
            endX -= 1;
    }

    private void back() {
        if (facing == "N")
            endY -= 1;
        else if (facing == "E")
            endX -= 1;
        else if (facing == "S")
            endY += 1;
        else if (facing == "W")
            endX += 1;
    }

    private void turn(String n, String e, String s, String w) {
        String[] campass = new String[]{n, e, s, w};
        int index = Arrays.asList(campass).indexOf(facing);
        facing = campass[(index + 1) % 4];
    }

    public void go(String instruction) {
        instruction.chars().
                forEach(
                        c -> mapInstructionToCommand((char)c).run()
                );
    }
}
