package qatch.analysis;

public abstract class Tool implements  ITool {
    private String name;


    public Tool(String in_name) {
        this.name = in_name;
    }


    public String getName() {
        return name;
    }
}
