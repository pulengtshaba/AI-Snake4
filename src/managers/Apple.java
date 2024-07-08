package managers;


import AI.Node;

public class Apple {

    private static double appleLife = 5;
    private static Node appleNode = null;

    public static void setApplePoint(Node node){
        if (appleNode == null || (appleNode.getX() != node.getX() || appleNode.getY() != node.getY())){
            appleLife = 5;
            appleNode = node;
        }
        else{
            appleLife -= 0.1;
        }
    }

    public static boolean appleIsFresh(){
        return appleLife >= 0;
    }

}
