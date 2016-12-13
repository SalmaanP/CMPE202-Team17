public abstract class Screen implements IScreenHandler {
    SortingWorld sortingWorld = null;

    public Screen(SortingWorld world) {
        this.sortingWorld = world;
    }

}