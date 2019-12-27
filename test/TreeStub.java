public class TreeStub extends Tree {

    public TreeStub() {
        super("treeStub");
    }

    public TreeStub(String name) {
        super(name);
    }

    @Override
    public Tree GetChildByName(String name) {
        return new Tree("treeStub child "+name);
    }
}
