import java.util.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class SpaceTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {1}, {5}
        });
    }

    private int spaceSize;
    private Space space;

    @Before
    public void before(){
        FileSystem.fileStorage = new Space(spaceSize);
    }
    @After
    public void after() {
        FileSystem.fileStorage = null;
    }

    public SpaceTest(int spaceSize) {
        this.space = new Space(spaceSize);
        this.spaceSize = spaceSize;
    }

    @Test
    public void checkConstructor() {
        assertEquals(space.countFreeSpace(), spaceSize);
    }

    @Test
    public void validSizeAlloc() throws OutOfSpaceException {
        int fileSize = 1;
        Leaf leaf = new Leaf("name", fileSize);
        space.Alloc(fileSize, leaf);
    }

    @Test(expected = OutOfSpaceException.class)
    public void invalidSizeAlloc() throws OutOfSpaceException {
        int fileSize = spaceSize +1;
        Leaf leaf = new Leaf("name", fileSize);
        space.Alloc(fileSize, leaf);
    }

    @Test
    public void fileSizeIsDifferentThanAllocSize() throws OutOfSpaceException {
        int fileSize = spaceSize - 1;
        Leaf leaf = new Leaf("name", fileSize + 1);
        space.Alloc(fileSize, leaf);
    }

    @Test
    public void checkDealloc() throws OutOfSpaceException {
        int fileSize = 1;
        Leaf leaf = new Leaf("name", fileSize);
        space.Alloc(fileSize, leaf);
        space.Dealloc(leaf);
        assertEquals(space.countFreeSpace(), spaceSize);
    }

    @Test
    public void checkCountFreeSpace() throws OutOfSpaceException {
        assertEquals(space.countFreeSpace(), spaceSize);
        int fileSize = 1;
        Leaf leaf = new Leaf("name", fileSize);
        space.Alloc(fileSize, leaf);
        assertEquals(space.countFreeSpace(), spaceSize -fileSize);
    }

    @Test
    public void checkGetAlloc() throws OutOfSpaceException {
        // check that all blocks are null at the beginning
        Leaf leaf = null;
        for (Leaf block : space.getAlloc()) {
            if (block != null) {
                leaf = block;
                break;
            }
        }
        assertNull(leaf);

        // check after allocation
        int fileSize = 1;
        leaf = new Leaf("name", fileSize);
        space.Alloc(fileSize, leaf);
        Leaf[] expectedBlocks = new Leaf[spaceSize];
        expectedBlocks[0] = leaf;
        assertArrayEquals(expectedBlocks, space.getAlloc());
    }
}
