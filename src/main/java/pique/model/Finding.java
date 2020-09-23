package pique.model;

import com.google.gson.annotations.Expose;

/**
 * A Finding is actual instances of diagnostic IDs found in a project after a static analysis tool run.
 *
 * A Finding can be a metric value (e.g. the tool found that the "total methods available in classes" is 401)
 * or a finding can be a rule discovery (e.g. rule "SCS001: Bad Hashing Function" was found in file X.java
 * at line 5, character 2).
 */
public class Finding {

    // Instance variables

    @Expose
    private int characterNumber;
    @Expose
    private int lineNumber;
    @Expose
    private int severity;  // TODO: consider refactoring into enum
    @Expose
    private double value;  // needed for metrics based findings (e.g. inherited methods = 45)
    @Expose
    private String filePath;


    // Constructors

    // (TODO: change to builder pattern to better accommodate metrics and rule findings)
    public Finding() { }

    public Finding(double value) { this.value = value; }

    public Finding(String filePath, int lineNumber, int characterNumber) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.characterNumber = characterNumber;
        this.severity = 1;
    }

    public Finding(String filePath, int lineNumber, int characterNumber, int severity) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.characterNumber = characterNumber;
        this.severity = severity;
    }


    // Getters and setters

    public int getCharacterNumber() {
        return characterNumber;
    }
    public void setCharacterNumber(int characterNumber) {
        this.characterNumber = characterNumber;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLineNumber() {
        return lineNumber;
    }
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLocation() { return filePath + "," + lineNumber + "," + characterNumber; }

    public int getSeverity() { return severity; }
    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public double getValue() {
        return value;
    }


    // Methods

    @Override
    public Finding clone() {
        return new Finding(getFilePath(), this.lineNumber, this.characterNumber, getSeverity());
    }
}
