package pique.model;

import com.google.gson.annotations.Expose;
import pique.evaluation.DefaultFindingEvaluator;
import pique.evaluation.DefaultNormalizer;
import pique.evaluation.IEvaluator;
import pique.evaluation.INormalizer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * A Finding is actual instances of diagnostic IDs found in a project after a static analysis tool run.
 * <p>
 * A Finding can be a metric value (e.g. the tool found that the "total methods available in classes" is 401)
 * or a finding can be a rule discovery (e.g. rule "SCS001: Bad Hashing Function" was found in file X.java
 * at line 5, character 2).
 */
public class Finding extends ModelNode {

    //region Instance variables

    private int characterNumber;
    private int lineNumber;
    private int severity;  // TODO: consider refactoring into enum
    private String filePath;

    //endregion

    // Constructors

    // (TODO: change to builder pattern to better accommodate metrics and rule findings)

    public Finding(String filePath, int lineNumber, int characterNumber, int severity) {
        super("", "", new DefaultFindingEvaluator(), new DefaultNormalizer());
        this.name = hashName(filePath, String.valueOf(lineNumber), String.valueOf(characterNumber),
                String.valueOf(severity));
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.characterNumber = characterNumber;
        this.severity = severity;
    }

    // Used for cloning
    public Finding(double value, String name, String description, IEvaluator evaluator, INormalizer normalizer,
                   Map<String, ModelNode> children, Map<String, Double> weights, String filePath, int lineNumber,
                   int characterNumber, int severity) {
        super(value, name, description, evaluator, normalizer, children, weights);
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.characterNumber = characterNumber;
        this.severity = severity;
    }


    //region Getters and setters

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

    public String getLocation() {
        return filePath + "," + lineNumber + "," + characterNumber;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    //endregion


    //region Methods

    @Override
    public Finding clone() {
        return new Finding(getValue(), getName(), getDescription(), getEvaluator(), getNormalizer(), getChildren(),
                getWeights(),  getFilePath(), getLineNumber(), getCharacterNumber(), getSeverity());
    }

    // Generate a hashed name using the properties of the Finding under construction.
    private String hashName(String... params) {

        StringBuilder hashBuilder = new StringBuilder();

        for (String param : params) {
            hashBuilder.append(param);
        }

        String valueToHash = hashBuilder.toString();
        return String.valueOf(valueToHash.hashCode());
    }

    //endregion
}
