package qatch.analysis;

public abstract class Tool implements  ITool {
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
