public class LeafStub {

    public int size;
    public int[] allocations;

    public LeafStub(int size) throws OutOfSpaceException {
        this.size = size;
        this.allocations = new int[size];
        for (int i = 0; i < size; i++) {
            this.allocations[i] = i;
        }
    }
}
