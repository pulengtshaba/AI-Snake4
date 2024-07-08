package AI;

import managers.Game;

import java.util.ArrayList;

public class Node implements Comparable<Node>
{
    private final int x;
    private final int y;
    private int H;
    private int G;
    private Node Parent;
    private int safety;
    private int distance;
    private int distanceToBorder;
    
    public Node(final int x, final int y) {
        this.H = 0;
        this.G = 0;
        this.Parent = null;
        this.safety = 0;
        this.distance = 0;
        this.distanceToBorder = 0;
        this.x = x;
        this.y = y;
    }
    
    public void setDistanceToBorder(final int distanceToBorder) {
        this.distanceToBorder = distanceToBorder;
    }
    
    public int getDistanceToBorder() {
        return this.distanceToBorder;
    }
    
    public void setParent(final Node parent) {
        this.Parent = parent;
    }
    
    public void setDistance(final int distance) {
        this.distance = distance;
    }
    
    public int getDistance() {
        return this.distance;
    }
    
    public Node getParent() {
        return this.Parent;
    }
    
    public int getG() {
        return this.G;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setH(final int h) {
        this.H = h;
    }
    
    public void setG(final int g) {
        this.G = g;
    }
    
    public void setSafety(final int safety) {
        this.safety = safety;
    }
    
    public int getF() {
        return this.H + this.G;
    }
    
    public int getH() {
        return this.H;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Node) {
            final Node b = (Node)obj;
            return this.x == b.getX() && this.y == b.getY();
        }
        return false;
    }
    
    @Override
    public int compareTo(final Node o) {
        return (this.getF() == o.getF()) ? (this.H - o.H) : (this.getF() - o.getF());
    }
    
    public ArrayList<Node> getNeighbours() {
        final ArrayList<Node> neighbours = new ArrayList<Node>();
        if (Game.areaSafe(this.x, this.y - 1)) {
            neighbours.add(new Node(this.x, this.y - 1));
        }
        if (Game.areaSafe(this.x, this.y + 1)) {
            neighbours.add(new Node(this.x, this.y + 1));
        }
        if (Game.areaSafe(this.x - 1, this.y)) {
            neighbours.add(new Node(this.x - 1, this.y));
        }
        if (Game.areaSafe(this.x + 1, this.y)) {
            neighbours.add(new Node(this.x + 1, this.y));
        }
        return neighbours;
    }
}
