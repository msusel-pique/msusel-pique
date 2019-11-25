package qatch.analysis;

import com.google.gson.annotations.Expose;

public class Finding {

    // Instance variables

    @Expose
    private int characterNumber;
    @Expose
    private String filePath;
    @Expose
    private int lineNumber;
    @Expose
    private int severity;  // TODO: think about enum or better extensible approaches for finding custom information


    // Constructors

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


    // Getters and setters

    public void setCharacterNumber(int characterNumber) {  this.characterNumber = characterNumber; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getFilePath() {
        return filePath;
    }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    public String getLocation() { return filePath + "," + lineNumber + "," + characterNumber; }
    public int getSeverity() { return severity; }


    // Methods

    public Finding clone() {
        return new Finding(getFilePath(), this.lineNumber, this.characterNumber, getSeverity());
    }
}
