package qatch.analysis;

public class Finding {

    // instance variables
    private int characterNumber;
    private String filePath;
    private int lineNumber;
    private int severity;  // TODO: think about enum or better extensible approaches for finding custom information


    // constructors
    public Finding() { }

    public Finding(String filePath, int lineNumber, int characterNumber) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.characterNumber = characterNumber;
    }

    public Finding(String filePath, int lineNumber, int characterNumber, int severity) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.characterNumber = characterNumber;
        this.severity = severity;
    }


    // getters and setters
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }

    public void setCharacterNumber(int characterNumber) {  this.characterNumber = characterNumber; }

    public String getLocation() { return filePath + "," + lineNumber + "," + characterNumber; }

    public int getSeverity() { return severity; }
}
