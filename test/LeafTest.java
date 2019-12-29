import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class LeafTest {
    private Random randomNum;
    private final int highest = 101;
    //private String name = "test";
    //private int size = 7;
    //private int diskSpace = 10;
    //private FileSystem fileSystem = new FileSystem(diskSpace);

    @Before
    public void beforeTests() {
        randomNum = new Random();
    }

    @Test
    //Test for checking simple creation
    public void checkCreation() throws OutOfSpaceException{
        int size = this.randomNum.nextInt(highest);
        FileSystem.fileStorage = new Space(size+1);
        Leaf leaf = new Leaf("nameOfLeaf",size);
        FileSystem.fileStorage = null;
    }

    @Test(expected = OutOfSpaceException.class)
    //Test for checking invalid creation
    public void checkInvalidCreation() throws OutOfSpaceException {
        int size = this.randomNum.nextInt(highest);
        FileSystem.fileStorage = new Space(size+1);
        Leaf leaf = new Leaf("nameOfLeaf",size*10);
        FileSystem.fileStorage = null;
    }

    @Test
    //Test for checking if names match between received name and existing name of the object Leaf.
    public void checkName() {
        Leaf leaf = null;
        int size = randomNum.nextInt(highest);
        FileSystem.fileStorage = new Space(size+1);
        String name = "nameOfLeaf";
        try {
            leaf = new Leaf(name,size);
        }
        catch (OutOfSpaceException e) {
            e.printStackTrace();
            assertTrue(false);
        }
        assertEquals(leaf.name,name);
        FileSystem.fileStorage = null;
    }

    @Test
    //Test for checking if size for the leaf been allocated as needed.
    public void checkSize() {
        Leaf leaf = null;
        int size = this.randomNum.nextInt(highest);
        FileSystem.fileStorage = new Space(size+1);
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
        FileSystem.fileStorage = null;
    }

    @Test
    //Test for checking the leaf path.
    public void checkLeafPath()
    {
        Leaf leaf = null;
        int size = this.randomNum.nextInt(highest);
        int leafNodeDepth = this.randomNum.nextInt(highest);
        FileSystem.fileStorage = new Space(size+1);
        Node nodeBefore = leaf;
        String name = "nameOfLeaf";
        try {
            leaf = new Leaf(name,size);
        }
        catch (OutOfSpaceException e) {
            e.printStackTrace();
        }
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
        FileSystem.fileStorage = null;
    }

    @After
    public void clear()
    {
        FileSystem.fileStorage = null;
    }
}

