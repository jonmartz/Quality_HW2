import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.*;
import static org.junit.Assert.*;

public class LeafTest {

    private static Random randomNum;
    private final int highest = 5;
    private int size;

    @BeforeClass
    public static void beforeClass() {
        randomNum = new Random();
    }
    @Before
    public void before(){
        this.size = randomNum.nextInt(highest) + 1;
        FileSystem.fileStorage = new Space(size + 1);
    }
    @After
    public void after() {
        FileSystem.fileStorage = null;
    }

    @Test
    //Test for checking simple creation
    public void checkCreation() throws OutOfSpaceException{
        Leaf leaf = new Leaf("nameOfLeaf", size);
    }

    @Test(expected = OutOfSpaceException.class)
    //Test for checking invalid creation
    public void checkInvalidCreation() throws OutOfSpaceException {
        Leaf leaf = new Leaf("nameOfLeaf",size*2);
    }

    @Test
    //Test for checking if names match between received name and existing name of the object Leaf.
    public void checkName() {
        Leaf leaf = null;
        String name = "nameOfLeaf";
        try {
            leaf = new Leaf(name,size);
        }
        catch (OutOfSpaceException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(leaf.name,name);
    }

    @Test
    //Test for checking if size for the leaf been allocated as needed.
    public void checkSize() {
        Leaf leaf = null;
        String name = "nameOfLeaf";
        try {
            leaf = new Leaf(name,size);
        }
        catch (OutOfSpaceException e) {
            e.printStackTrace();
        }
        assertEquals(leaf.allocations.length,size);
        for(int i = 0 ; i < size ; i++){
            assertEquals(FileSystem.fileStorage.getAlloc()[leaf.allocations[i]],leaf);
        }
    }

    @Test
    //Test for checking the leaf path.
    public void checkLeafPath()
    {
        Leaf leaf = null;
        int leafNodeDepth = this.randomNum.nextInt(highest);
        String name = "nameOfLeaf";
        try {
            leaf = new Leaf(name,size);
        }
        catch (OutOfSpaceException e) {
            e.printStackTrace();
        }
        Node nodeBefore = leaf;
        leaf.depth = leafNodeDepth+1;
        List<Tree> forest = new ArrayList<>();
        Tree tree = null;
        String treeName = "nameOfTree";
        for(int i = 0 ; i < leafNodeDepth ; i++){
            tree = new Tree(treeName + ": " + i);
            nodeBefore.parent = tree;
            tree.depth = nodeBefore.depth-1;
            nodeBefore = tree;
            if(nodeBefore instanceof Tree) {
                forest.add(0, (Tree)nodeBefore);
            }
        }
        String[] leafPath = leaf.getPath();//From Node class function
        assertEquals(leafPath.length,leafNodeDepth+1);
        assertEquals(leafPath[leafPath.length-1],name);
        for(int j = 0 ; j < forest.size() ; j++){
            assertEquals(forest.get(j).name,leafPath[j]);
        }
    }
}

