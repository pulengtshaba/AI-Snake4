package AI;

import managers.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class PathFinder
{
    private static ArrayList<Node> quickPath;
    
    public static ArrayList<Node> calculateSafetyPath(final Node A, final Node B) {
        Game.unmarkPosition(B);
        final boolean[][] openListArr = new boolean[Game.GAME_FIELD.length][Game.GAME_FIELD.length];
        final boolean[][] closedListArr = new boolean[Game.GAME_FIELD.length][Game.GAME_FIELD.length];

        final PriorityQueue<Node> openList = new PriorityQueue<Node>((o1, o2) -> {
            if (o2.getF() == o1.getF()) {
                return o2.getH() - o1.getH();
            }
            return o2.getF() - o1.getF();
        });
        openList.add(A);
        Node secondOption = null;
        int highestDistance = 0;
        while (!openList.isEmpty()) {
            final Node parentNode = openList.remove();
            if (parentNode.getDistance() > highestDistance) {
                secondOption = parentNode;
                highestDistance = parentNode.getDistance();
            }
            openListArr[parentNode.getY()][parentNode.getX()] = false;
            closedListArr[parentNode.getY()][parentNode.getX()] = true;
            for (final Node childNode : parentNode.getNeighbours()) {
                if (closedListArr[childNode.getY()][childNode.getX()]) {
                    continue;
                }
                if (!openListArr[childNode.getY()][childNode.getX()]) {
                    childNode.setDistance(parentNode.getDistance() + 1);
                    childNode.setParent(parentNode);
                    childNode.setH(getManhattanDistance(childNode, parentNode));
                    childNode.setG(parentNode.getG() + 10);
                    openList.add(childNode);
                    openListArr[childNode.getY()][childNode.getX()] = true;
                    if (childNode.equals(B)) {
                        return generatePath(childNode, A, true);
                    }
                }
                else {
                    if (childNode.getG() >= parentNode.getG() + 10) {
                        continue;
                    }
                    childNode.setG(parentNode.getG() + 10);
                    childNode.setParent(parentNode);
                }
            }
        }
        if (secondOption != null) {
            return generatePath(secondOption, A, false);
        }
        return new ArrayList<>();
    }
    
    public static ArrayList<Node> calculatePath(final Node A, final Node B) {
        Game.unmarkPosition(B);
        final boolean[][] openListArr = new boolean[Game.GAME_FIELD.length][Game.GAME_FIELD.length];
        final boolean[][] closedListArr = new boolean[Game.GAME_FIELD.length][Game.GAME_FIELD.length];
        final PriorityQueue<Node> openList = new PriorityQueue<Node>();
        A.setH(getManhattanDistance(A, B));
        openList.add(A);
        Node secondOption = null;
        int highestDistance = 0;
        while (!openList.isEmpty()) {
            final Node parentNode = openList.remove();
            if (parentNode.getDistance() > highestDistance) {
                secondOption = parentNode;
                highestDistance = parentNode.getDistance();
            }
            openListArr[parentNode.getY()][parentNode.getX()] = false;
            closedListArr[parentNode.getY()][parentNode.getX()] = true;
            for (final Node childNode : parentNode.getNeighbours()) {
                if (closedListArr[childNode.getY()][childNode.getX()]) {
                    continue;
                }
                if (!openListArr[childNode.getY()][childNode.getX()]) {
                    childNode.setParent(parentNode);
                    childNode.setDistance(parentNode.getDistance() + 1);
                    childNode.setH(determineSafe(childNode, B, A));
                    if (childNode.getH() == Integer.MAX_VALUE) {
                        closedListArr[childNode.getY()][childNode.getX()] = true;
                    }
                    else {
                        childNode.setG(parentNode.getG() + 10);
                        openList.add(childNode);
                        openListArr[childNode.getY()][childNode.getX()] = true;
                        if (childNode.equals(B)) {
                            return generatePath(childNode, A, true);
                        }
                    }
                }
                else {
                    if (childNode.getG() >= parentNode.getG() + 10) {
                        continue;
                    }
                    childNode.setG(parentNode.getG() + 10);
                    childNode.setParent(parentNode);
                }
            }
        }
        if (secondOption != null) {
            return generatePath(secondOption, A, false);
        }
        return new ArrayList<Node>();
    }
    
    private static int determineSafe(final Node childNode, final Node B, final Node myHead) {
        final int distance = getManhattanDistance(childNode, B);
        final int boundaryCost = Game.boundaryCost(childNode, myHead);
        final int rivalCost = Game.getRivalCost(childNode, myHead);
        final int freeSpaceCost = Game.getFreeSpaceCost(childNode, childNode.equals(myHead));
        if (rivalCost == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return distance + boundaryCost + rivalCost + freeSpaceCost;
    }
    
    private static ArrayList<Node> generatePath(Node childNode, final Node A, final boolean forwardCheck) {
        ArrayList<Node> path = new ArrayList<Node>();
        final Node B = childNode;
        while (childNode != null) {
            Game.markPosition(childNode);
            path.add(childNode);
            childNode = childNode.getParent();
            if (childNode.equals(A)) {
                break;
            }
        }
        final int temp = Game.safety;
        Game.safety = Game.ME.getLength() + 1;
        if (forwardCheck) {
            final ArrayList<Node> forwardCheckPath = forwardCheck(B, Game.ME.getTail());
            if (forwardCheckPath.isEmpty()) {
                quickDistance(Game.ME.getHead(), Game.ME.getTail());
                Collections.reverse(PathFinder.quickPath);
                path = PathFinder.quickPath;
            }
        }
        Game.safety = temp;
        return path;
    }
    
    public static int getManhattanDistance(final Node p1, final Node p2) {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }
    
    public static ArrayList<Node> forwardCheck(final Node A, final Node B) {
        Game.unmarkPosition(B);
        int distance = 0;
        final ArrayList<Node> path = new ArrayList<Node>();
        final ArrayList<Node> alreadyChecked = new ArrayList<Node>();
        Node main = A;
        while (distance <= Game.safety) {
            if (main.equals(B)) {
                return path;
            }
            if (!main.equals(A)) {
                path.add(main);
            }
            final PriorityQueue<Node> neighbours = new PriorityQueue<Node>(Comparator.comparingInt(o -> getManhattanDistance(o, B)));

            neighbours.addAll(main.getNeighbours());
            if (neighbours.isEmpty()) {
                return new ArrayList<Node>();
            }
            alreadyChecked.add(main);
            ++distance;
            for (int i = 0; i < neighbours.size(); ++i) {
                final Node p = neighbours.remove();
                if (!alreadyChecked.contains(p)) {
                    main = p;
                    break;
                }
            }
            alreadyChecked.addAll(neighbours);
        }
        return path;
    }
    
    public static int quickDistance(final Node A, final Node B) {
        PathFinder.quickPath.clear();
        Game.unmarkPosition(B);
        int distance = 0;
        final ArrayList<Node> alreadyChecked = new ArrayList<Node>();
        Node main = A;
        while (distance <= Game.safety) {
            if (main.equals(B)) {
                return distance;
            }
            if (!main.equals(A)) {
                PathFinder.quickPath.add(main);
            }
            final PriorityQueue<Node> neighbours = new PriorityQueue<Node>(Comparator.comparingInt(o -> getManhattanDistance(o, B)));
            neighbours.addAll(main.getNeighbours());
            if (neighbours.isEmpty()) {
                return distance;
            }
            alreadyChecked.add(main);
            ++distance;
            for (int i = 0; i < neighbours.size(); ++i) {
                final Node p = neighbours.remove();
                if (!alreadyChecked.contains(p)) {
                    main = p;
                    break;
                }
            }
            alreadyChecked.addAll(neighbours);
        }
        return getManhattanDistance(A, B);
    }

    
    public static ArrayList<Node> maxStall(final Node A, final Node B) {
        Game.unmarkPosition(B);
        final boolean[][] openListArr = new boolean[Game.GAME_FIELD.length][Game.GAME_FIELD.length];
        final boolean[][] closedListArr = new boolean[Game.GAME_FIELD.length][Game.GAME_FIELD.length];
        final PriorityQueue<Node> openList = new PriorityQueue<Node>((o1, o2) -> {
            if (o1.getF() == o2.getF()) {
                return o2.getH() - o1.getH();
            }
            else {
                return o2.getF() - o1.getF();
            }
        });
        A.setH(getManhattanDistance(A, B));
        openList.add(A);
        Node secondOption = null;
        int highestDistance = 0;
        while (!openList.isEmpty()) {
            final Node parentNode = openList.remove();
            if (parentNode.getDistance() > highestDistance) {
                secondOption = parentNode;
                highestDistance = parentNode.getDistance();
            }
            openListArr[parentNode.getY()][parentNode.getX()] = false;
            closedListArr[parentNode.getY()][parentNode.getX()] = true;
            for (final Node childNode : parentNode.getNeighbours()) {
                if (closedListArr[childNode.getY()][childNode.getX()]) {
                    continue;
                }
                if (!openListArr[childNode.getY()][childNode.getX()]) {
                    childNode.setParent(parentNode);
                    childNode.setDistance(parentNode.getDistance() + 1);
                    childNode.setH(determineStallSafety(childNode));
                    if (childNode.getH() == Integer.MAX_VALUE) {
                        closedListArr[childNode.getY()][childNode.getX()] = true;
                    }
                    else {
                        childNode.setG(parentNode.getG() + 10);
                        openList.add(childNode);
                        openListArr[childNode.getY()][childNode.getX()] = true;
                        if (childNode.equals(B)) {
                            return generatePath(childNode, A, true);
                        }
                    }
                }
                else {
                    if (childNode.getG() >= parentNode.getG() + 10) {
                        continue;
                    }
                    childNode.setG(parentNode.getG() + 10);
                    childNode.setParent(parentNode);
                    childNode.setDistance(parentNode.getDistance() + 1);
                    childNode.setH(determineStallSafety(childNode));
                    openList.remove(childNode);
                    openList.add(childNode);
                }
            }
        }
        if (secondOption != null) {
            return generatePath(secondOption, A, false);
        }
        return new ArrayList<Node>();
    }
    
    private static int determineStallSafety(final Node childNode) {
        for (final Snake r : Game.OBSTACLES) {
            if (getManhattanDistance(r.getHead(), childNode) <= 2) {
                return Integer.MAX_VALUE;
            }
        }
        return 100;
    }

    static {
        PathFinder.quickPath = new ArrayList<>();
    }
}
