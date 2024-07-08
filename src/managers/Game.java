package managers;

import AI.PathFinder;
import AI.Node;
import AI.Snake;

import java.util.ArrayList;
import java.util.Comparator;

public class Game
{
    public static Node nearestBoarder;
    public static int safety;
    public static Node APPLE;
    public static int[][] GAME_FIELD;
    private static int WIDTH;
    private static int HEIGHT;
    public static ArrayList<Snake> OBSTACLES;
    public static ArrayList<Snake> RIVALS;
    public static Snake ME;
    
    public static void initGame(final int WIDTH, final int HEIGHT) {
        Game.GAME_FIELD = new int[HEIGHT][WIDTH];
        Game.WIDTH = WIDTH;
        Game.HEIGHT = HEIGHT;
    }
    
    public static void clear() {
        Game.GAME_FIELD = new int[Game.HEIGHT][Game.WIDTH];
        Game.OBSTACLES.clear();
        Game.RIVALS.clear();
    }

    public static void markApple(){
        GAME_FIELD[APPLE.getY()][APPLE.getX()] = 1;
    }

    public static void setAPPLE(final Node apple) {
        Game.APPLE = apple;
        Apple.setApplePoint(apple);
    }
    
    public static void drawSnake(final String snakeDesc, final int desc, final boolean me) {
        final String[] snakeDescList = snakeDesc.split(" ");
        Snake snake;
        if (desc == 1) {
            snake = new Snake(1, -1);
        }
        else {
            snake = new Snake(0, Integer.parseInt(snakeDescList[1]));
        }
        if (desc == 0 && snakeDescList[0].equals("dead")) {
            return;
        }
        int i = 0;
        if (desc == 0) {
            i = 3;
        }
        while (i < snakeDescList.length - 1) {
            final String[] firstKnick = snakeDescList[i].split(",");
            final String[] secondKnick = snakeDescList[i + 1].split(",");
            final Node A = new Node(Integer.parseInt(firstKnick[0]), Integer.parseInt(firstKnick[1]));
            final Node B = new Node(Integer.parseInt(secondKnick[0]), Integer.parseInt(secondKnick[1]));
            drawLine(snake, A, B, desc);
            ++i;
        }
        if (me) {
            Game.ME = snake;
        }
        else if (desc == 1) {
            Game.OBSTACLES.add(snake);
        }
        else if (desc == 0) {
            Game.RIVALS.add(snake);
        }
    }
    
    private static void drawLine(final Snake snake, final Node A, final Node B, final int decider) {
        final int ob = (decider == 1) ? 5 : 2;
        int currX = A.getX();
        int currY = A.getY();
        int nextX = B.getX();
        int nextY = B.getY();
        if (currX == nextX) {
            if (currY > nextY) {
                while (nextY <= currY) {
                    snake.addPoint(new Node(currX, currY));
                    Game.GAME_FIELD[nextY][currX] = ob;
                    ++nextY;
                }
            }
            else {
                while (currY <= nextY) {
                    snake.addPoint(new Node(currX, currY));
                    Game.GAME_FIELD[currY][currX] = ob;
                    ++currY;
                }
            }
        }
        else if (currX > nextX) {
            while (nextX <= currX) {
                snake.addPoint(new Node(currX, currY));
                Game.GAME_FIELD[currY][nextX] = ob;
                ++nextX;
            }
        }
        else {
            while (currX <= nextX) {
                snake.addPoint(new Node(currX, currY));
                Game.GAME_FIELD[currY][currX] = ob;
                ++currX;
            }
        }
    }
    

    public static boolean areaSafe(final int x, final int y) {
        return x >= 0 && x < Game.WIDTH && y >= 0 && y < Game.HEIGHT && Game.GAME_FIELD[y][x] == 0;
    }
    
    public static boolean inBoundary(final Node p) {
        return p.getX() == 0 || p.getX() == Game.WIDTH - 1 || p.getY() == 0 || p.getY() == Game.HEIGHT - 1;
    }
    
    public static int boundaryCost(final Node p, final Node MyHead) {
        if (p.getX() == 0 || p.getX() == Game.WIDTH - 1 || p.getY() == 0 || p.getY() == Game.HEIGHT - 1) {
            final int myDistance = PathFinder.getManhattanDistance(MyHead, p);
            final int d = Game.safety + 1 - myDistance;
            if (d > 0) {
                return d * 200;
            }
        }
        return 0;
    }
    

    
    public static int getRivalCost(final Node childNode, final Node myHead) {
        int cost = 0;
        final int myDistanceToNode = PathFinder.getManhattanDistance(myHead, childNode);

        for (final Snake rival : Game.RIVALS) {
            final int rivalDistanceToNode = PathFinder.getManhattanDistance(rival.getHead(), childNode);
            if (myDistanceToNode == rivalDistanceToNode && myDistanceToNode == 1) {
                /*if (childNode.equals(GameManager.APPLE)) {
                    return Integer.MAX_VALUE;
                }
                return 10000000;*/
                return Integer.MAX_VALUE;
            }
        }

        /*for (final SnakeObj zombieq : GameManager.OBSTACLES) {


            if (myDistanceToNode <= GameManager.safety) {
                final int rivalDistanceToNode2 = Astar.quickDistance(zombie.getHead(), childNode);
                if (rivalDistanceToNode2 > GameManager.safety) {
                    continue;
                }
                if (rivalDistanceToNode2 == myDistanceToNode && myDistanceToNode == 1) {
                    if (childNode.equals(GameManager.APPLE)) {
                        return Integer.MAX_VALUE;
                    }
                    return 1000000;
                }
                else {
                    if (zombie.getDescriber() != 1) {
                        continue;
                    }
                    final int d = GameManager.safety + 1 - rivalDistanceToNode2;
                    if (d <= 0) {
                        continue;
                    }
                    cost += d * 5000;
                }
            }
        }*/
        return cost;
    }

    public static int getFreeSpaceCost(final Node childNode, final boolean me) {
        int defaultFreeSpace = 4;
        if (inBoundary(childNode)) {
            defaultFreeSpace = 2;
        }
        if (me) {
            --defaultFreeSpace;
        }
        final int x = childNode.getX();
        final int y = childNode.getY();
        if (areaSafe(x, y - 1)) {
            --defaultFreeSpace;
        }
        if (areaSafe(x, y + 1)) {
            --defaultFreeSpace;
        }
        if (areaSafe(x - 1, y)) {
            --defaultFreeSpace;
        }
        if (areaSafe(x + 1, y)) {
            --defaultFreeSpace;
        }
        return defaultFreeSpace * 100;
    }

    public static void unmarkPosition(final Node A) {
        if (Game.GAME_FIELD[A.getY()][A.getX()] == 7) {
            Game.GAME_FIELD[A.getY()][A.getX()] = 0;
        }
    }

    public static void markPosition(final Node A) {
        if (Game.GAME_FIELD[A.getY()][A.getX()] == 0) {
            Game.GAME_FIELD[A.getY()][A.getX()] = 7;
        }
    }





    static {
        Game.nearestBoarder = null;
        Game.safety = 15;
        Game.OBSTACLES = new ArrayList<Snake>();
        Game.RIVALS = new ArrayList<Snake>();
    }
}
