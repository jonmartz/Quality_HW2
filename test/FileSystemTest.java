import java.nio.file.DirectoryNotEmptyException;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class FileSystemTest {

    private int diskSpace = 10;
    private FileSystem fileSystem = new FileSystem(diskSpace);

    // paths to use
    private String[] validPath = {"root", "name"};
    private String[] invalidPath = {"notRoot", "name"};
    private String[] longDirPath = {"root", "dir1", "dir2"};
    private String[] longFilePath = {"root", "dir1", "dir2", "name"};

    // test method: constructor

    @Test
    public void checkConstructor() {
        assertEquals(diskSpace, fileSystem.disk().length);
    }

    // test method: dir()

    @Test(expected = BadFileNameException.class)
    public void invalidDirName() throws BadFileNameException {
        fileSystem.dir(invalidPath);
    }

    @Test(expected = BadFileNameException.class)
    public void makeDirButFileAlreadyExists() throws BadFileNameException, OutOfSpaceException {
        fileSystem.file(validPath, 1);
        fileSystem.dir(validPath);
    }

    @Test
    public void makeDirButDirAlreadyExists() throws BadFileNameException {
        fileSystem.dir(validPath);
        fileSystem.dir(validPath);
    }

    // test method: disk()

    @Test
    public void checkDisk() throws BadFileNameException, OutOfSpaceException {
        String[][] disk = fileSystem.disk();
        String[] file = null;
        for (String[] block : disk) {
            if (block != null) {
                file = block;
                break;
            }
        }
        assertNull(file);

        fileSystem.file(validPath, 1);
        disk = fileSystem.disk();
        for (String[] block : disk) {
            if (block != null) {
                file = block;
                break;
            }
        }
        assertArrayEquals(validPath, file);
    }

    // test method: file()

    @Test
    public void DoesNotAllocateSpaceForFileOfSizeZero() throws OutOfSpaceException, BadFileNameException {
        fileSystem.file(validPath, 0);
        String[][] disk = fileSystem.disk();
        String[] file = null;
        for (String[] block : disk) {
            if (block != null) {
                file = block;
                break;
            }
        }
        assertNull(file);
    }

    @Test()
    public void fileIsAlmostTooBig() throws OutOfSpaceException, BadFileNameException {
        fileSystem.file(validPath, diskSpace-1);
        fileSystem.file(validPath, diskSpace);
    }

    @Test(expected = OutOfSpaceException.class)
    public void fileIsTooBig() throws OutOfSpaceException, BadFileNameException {
        fileSystem.file(validPath, diskSpace+1);
    }

    @Test
    public void allDirsAreCreated() throws OutOfSpaceException, BadFileNameException {
        fileSystem.file(longFilePath, 1);
        String[] file = null;
        String[][] disk = fileSystem.disk();
        for (String[] block : disk) {
            if (block != null) {
                file = block;
                break;
            }
        }
        assertArrayEquals(longFilePath, file);
    }

    @Test(expected = BadFileNameException.class)
    public void invalidFileName() throws BadFileNameException, OutOfSpaceException {
        fileSystem.file(invalidPath, 1);
    }

    @Test(expected = BadFileNameException.class)
    public void makeFileButDirAlreadyExists() throws BadFileNameException, OutOfSpaceException {
        fileSystem.dir(validPath);
        fileSystem.file(validPath, 1);
    }

    @Test
    public void makeNotTooBigFileButFileAlreadyExists() throws BadFileNameException, OutOfSpaceException {
        fileSystem.file(validPath, 1);
        fileSystem.file(validPath, 2);
        int blocksWithFile = 0;
        String[][] disk = fileSystem.disk();
        for (String[] block : disk) {
            if (block != null)
                blocksWithFile++;
        }
        assertEquals(2, blocksWithFile);
    }

    @Test
    public void makeTooBigFileButFileAlreadyExists() throws BadFileNameException, OutOfSpaceException {
        fileSystem.file(validPath, 1);
        try {
            fileSystem.file(validPath, diskSpace+1);
        }
        catch (OutOfSpaceException e) {
            String[] file = null;
            String[][] disk = fileSystem.disk();
            for (String[] block : disk) {
                if (block != null) {
                    file = block;
                    break;
                }
            }
            assertArrayEquals(validPath, file);
        }
        fail("allocated a file that is bigger than disk");
    }

    // test method: lsdir()

    @Test
    public void checkIsDir() throws OutOfSpaceException, BadFileNameException {
        String[] names = fileSystem.lsdir(longDirPath);
        assertNull(names);

        // add two files and a sub dir
        String[] expectedNames = {"file1", "file2", "subDir"};
        for (String name : expectedNames) {
            String[] path = new String[longDirPath.length+1];
            for (int i = 0; i < longDirPath.length; i++)
                path[i] = longDirPath[i];
            path[path.length-1] = name;
            if (name.equals("subDir"))
                fileSystem.dir(path);
            else
                fileSystem.file(path, 1);
        }
        names = fileSystem.lsdir(longDirPath);
        assertArrayEquals(expectedNames, names);
    }

    // test method: rmfile()

    @Test
    public void removeFileThatDoesntExist() {
        fileSystem.rmfile(validPath);
    }

    @Test
    public void removeFileThatExists() throws OutOfSpaceException, BadFileNameException {
        fileSystem.file(validPath, 1);
        fileSystem.rmfile(validPath);
        String[][] disk = fileSystem.disk();
        String[] file = null;
        for (String[] block : disk) {
            if (block != null) {
                file = block;
                break;
            }
        }
        assertNull(file);
    }

    // test method: rmdir()

    @Test
    public void removeDirThatDoesntExist() throws DirectoryNotEmptyException {
        fileSystem.rmdir(longDirPath);
    }

    @Test
    public void removeEmptyDir() throws DirectoryNotEmptyException, BadFileNameException {
        fileSystem.dir(longDirPath);
        fileSystem.rmdir(longDirPath);
        String[][] disk = fileSystem.disk();
        String[] file = null;
        for (String[] block : disk) {
            if (block != null) {
                file = block;
                break;
            }
        }
        assertNull(file);
    }

    @Test(expected = DirectoryNotEmptyException.class)
    public void removeNotEmptyDir() throws DirectoryNotEmptyException, BadFileNameException {
        fileSystem.dir(longFilePath);
        fileSystem.rmdir(longDirPath);
    }

    // test method: FileExists()

    @Test
    public void fileExists() throws BadFileNameException, OutOfSpaceException {
        fileSystem.file(validPath, 1);
        Leaf file = fileSystem.FileExists(validPath);
        String expectedName = validPath[validPath.length-1];
        assertEquals(expectedName, file.name);
    }

    // test method: DirExists()

    @Test
    public void dirExists() throws BadFileNameException, OutOfSpaceException {
        fileSystem.dir(longDirPath);
        Tree dir = fileSystem.DirExists(longDirPath);
        String expectedName = longDirPath[longDirPath.length-1];
        assertEquals(expectedName, dir.name);
    }
}
