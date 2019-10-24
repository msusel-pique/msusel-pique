package qatch.analysis;

public class Finding {

    // instance variables
    private String filePath;
    private int lineNumber;
    private int characterNumber;


    // constructors
    public Finding() { }
    public Finding(String filePath, int lineNumber, int characterNumber) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.characterNumber = characterNumber;
    }


    // getters and setters
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    public void setCharacterNumber(int characterNumber) {  this.characterNumber = characterNumber; }
    public String getLocation() { return filePath + "," + lineNumber + "," + characterNumber; }
}
