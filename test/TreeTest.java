import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.*;
import static org.junit.Assert.*;

public class TreeTest {

    private static Random randomNum;
    private final int highest = 5;

    @BeforeClass
    public static void beforeClass() {
        randomNum = new Random();
    }
    @After
    public void after() {
        FileSystem.fileStorage = null;
    }

    @Test
    //Test for checking if names match between received name and existing name of the object Tree.
    public void checkName() {
        String treeName = "nameOfTree";
        Tree tree = new Tree(treeName);
        assertEquals(tree.name, treeName);
    }

    @Test
    //Test for getChildByName when Child exists:
    public void checkExistGetChildByName()
    {
        Node child1=null;
        Node child2=null;
        String treeName = "nameOfTree";
        int childrenCount = randomNum.nextInt(highest);;
        Tree tree = new Tree(treeName);
        List<Tree> childrenToCheck = new ArrayList<>();
        for(int i=0 ; i < childrenCount ; i++){
            childrenToCheck.add(new Tree(treeName+i));
        }
        for(int j=0 ; j < childrenToCheck.size() ; j++){
            child1 = childrenToCheck.get(j);
            tree.children.put(child1.name,child1);
            child2 = tree.GetChildByName(child1.name);
            assertEquals(child1,child2);
        }
    }

    @Test
    //Test for getChildByName when Child don't exists:
    public void checkNotExistGetChildByName()
    {
        String treeName = "nameOfTree";
        List<Tree> childrenToCheck = new ArrayList<>();
        int childrenCount = randomNum.nextInt(highest);;
        Tree tree = new Tree(treeName);
        List<String> childrenNameToCheck = new ArrayList<>();
        for(int i = 0 ; i < childrenCount ; i++){
            childrenNameToCheck.add(treeName + i);
        }
        for(int j=0 ; j < childrenNameToCheck.size() ; j++){
            String childName = childrenNameToCheck.get(j);
            assertNull(tree.children.get(childName));
            Tree childTree1 = tree.GetChildByName(childName);
            assertEquals(childTree1.depth,tree.depth+1);
            assertEquals(tree.children.get(childName),childTree1);
            childrenToCheck.add(childTree1);
            Tree childTree2 = childrenToCheck.get(j);
            Node childNode = tree.GetChildByName(childTree2.name);
            assertEquals(childTree2,childNode);
        }
    }

    @Test
    //Test for checking the tree path.
    public void checkTreePath() {
        int treeNodeDepth = randomNum.nextInt(highest);
        String treeNameTest = "nameOfTreeTest";
        String treeName = "nameOfTree";
        Tree treeTest = new Tree(treeNameTest);
        Tree tree;
        Node nodeBefore = treeTest;
        treeTest.depth = treeNodeDepth + 1;
        List<Tree> forest = new ArrayList<>();
        for (int i = 0; i < treeNodeDepth; i++) {
            tree = new Tree(treeName + " "+i);
            nodeBefore.parent = tree;
            tree.depth = nodeBefore.depth - 1;
            nodeBefore = tree;
            if (nodeBefore instanceof Tree) {
                forest.add(0, (Tree)nodeBefore);
            }
        }
        String[] treePath = treeTest.getPath();
        assertEquals(treePath.length, treeNodeDepth + 1);
        assertEquals(treePath[treePath.length - 1], treeNameTest);
        for (int j = 0; j < forest.size(); j++) {
            assertEquals(forest.get(j).name, treePath[j]);
        }
    }
}
