import AI.Node;
import managers.Game;
import utils.GameUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MyAgent extends za.ac.wits.snake.MyAgent {

    public static void main(String[] args) throws IOException {
        MyAgent agent = new MyAgent();
        MyAgent.start(agent, args);
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String initString = br.readLine();
            String[] temp = initString.split(" ");
            int nSnakes = Integer.parseInt(temp[0]);
            initGame(temp);

            while (true) {
                String line = br.readLine();
                if (line.contains("Game Over")) {
                    break;
                }

                //do stuff with apples
                setAppleCoord(line);

                // read in obstacles and do something with them!
                int nObstacles = 3;
                for (int obstacle = 0; obstacle < nObstacles; obstacle++) {
                    String obs = br.readLine();
                    /// do something with obs
                    addObstacle(obs);
                }

                int mySnakeNum = Integer.parseInt(br.readLine());
                for (int i = 0; i < nSnakes; i++) {
                    String snakeLine = br.readLine();
                    if (i == mySnakeNum) {
                        //hey! That's me :)
                        addMe(snakeLine);
                    }else{
                        addRival(snakeLine);
                    }
                    //do stuff with other snakes
                }
                //finished reading, calculate move:
                System.out.println(getMyMove());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initGame(String[] temp){
        Game.initGame(Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
    }

    public void setAppleCoord(String line){
        final String[] appleCoord = line.split(" ");
        Game.setAPPLE(new Node(Integer.parseInt(appleCoord[0]), Integer.parseInt(appleCoord[1])));
    }

    public void addObstacle(String obs){
        Game.drawSnake(obs, GameUtils.OBSTACLE, false);

    }

    public void addRival(String snakeLine){
        Game.drawSnake(snakeLine, GameUtils.SNAKE, false);
    }

    public void addMe(String snakeLine){
        Game.drawSnake(snakeLine, GameUtils.SNAKE, true);
    }

    public int getMyMove(){
        final int move = Game.ME.getNextMove();
        Game.clear();
        return move;
    }

}