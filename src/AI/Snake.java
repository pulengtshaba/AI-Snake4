package AI;

import managers.Apple;
import managers.Game;
import utils.GameUtils;

import java.util.ArrayList;

public class Snake
{
    private final ArrayList<Node> nodes;
    private final int describer;
    private final int length;
    
    public Snake(final int describer, final int length) {
        this.nodes = new ArrayList<Node>();
        this.describer = describer;
        this.length = length;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int getDescriber() {
        return this.describer;
    }
    
    public void addPoint(final Node node) {
        if (this.nodes.size() == 0) {
            this.nodes.add(node);
        }
        else {
            final Node p = this.nodes.get(this.nodes.size() - 1);
            if (!p.equals(node)) {
                this.nodes.add(node);
            }
        }
    }
    
    public Node getHead() {
        return this.nodes.get(0);
    }
    
    public Node getTail() {
        return this.nodes.get(this.nodes.size() - 1);
    }
    
    public int getNextMove() {
        //final Point nearestZombieFollowingMe = GameManager.IAmFollowed(this.getHead());
        ArrayList<Node> path;

        if (!Apple.appleIsFresh()){
            Game.markApple();
            path = PathFinder.maxStall(this.getHead(), this.getTail());
        }
        else{
            path = PathFinder.calculatePath(this.getHead(), Game.APPLE);
            if (path.isEmpty()) {
                path = PathFinder.calculatePath(this.getHead(), this.getTail());
                if (path.isEmpty()) {
                    path = PathFinder.maxStall(this.getHead(), this.getTail());
                    if (path.isEmpty()) {
                        path = PathFinder.calculateSafetyPath(this.getHead(), this.getTail());
                        if (path.isEmpty()) {
                            return 5;
                        }
                    }

                }
            }
        }

        if (path.isEmpty()) return GameUtils.STRAIGHT;

        final Node p = path.get(path.size() - 1);
        final Node h = this.getHead();
        if (h.getX() == p.getX()) {
            if (p.getY() > h.getY()) {
                return GameUtils.DOWN;
            }
            return GameUtils.UP;
        }
        else {
            if (p.getX() > h.getX()) {
                return GameUtils.RIGHT;
            }
            return GameUtils.LEFT;
        }
    }
}
