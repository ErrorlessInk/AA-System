package assessments;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static assessments.AssessmentFeed.DESIGN_MARK;
import static assessments.AssessmentFeed.EXPLAINED_SUCCESSFULLY;
import static assessments.AssessmentFeed.EXPLANATION_MARK;
import static assessments.AssessmentFeed.FUNCTIONALITY_MARK;
import static assessments.AssessmentFeed.PROGRAM_MARK;
import static assessments.AssessmentFeed.VERIFICATION_MARK;

abstract class Assessment1 {
    private final String title;
    private final double maximumMark=100.0;
    private final boolean aiPermitted;
    private final Map<String, Double> rawValues;

    Assessment1(String cid, String title, boolean aiPermitted, String... keys) {
        this.title=title;
        this.aiPermitted=aiPermitted;

        Map<String, Double> values=new LinkedHashMap<>();
        for (String key : keys) {
            values.put(key, AssessmentFeed.getResult(cid,title, key));
        }
        rawValues=Collections.unmodifiableMap(values);
    }

    String getTitle() {
        return title;
    }

    double getMaximumMark() {
        return maximumMark;
    }

    boolean isAiPermitted() {
        return aiPermitted;
    }

    Map<String, Double> getRawValues() {
        return rawValues;
    }

    protected double rawValue(String key) {
        return rawValues.get(key);
    }

    abstract double getFinalPercentage();

    static final class ProgrammingTest1 extends Assessment1 {
        ProgrammingTest1(String cid, String title) {
            super(cid,title, false,PROGRAM_MARK, EXPLAINED_SUCCESSFULLY);
        }

        @Override
        double getFinalPercentage() {
            double program=rawValue(PROGRAM_MARK);
            return rawValue(EXPLAINED_SUCCESSFULLY)==1.0 ? program : Math.min(program,40.0);
        }
    }

    static final class AiProject1 extends Assessment1 {
        AiProject1(String cid, String title) {
            super(cid,title, true,FUNCTIONALITY_MARK, VERIFICATION_MARK);
        }

        @Override
        double getFinalPercentage() {
            return (40*rawValue(FUNCTIONALITY_MARK)+60*rawValue(VERIFICATION_MARK))/100.0;
        }
    }

    static final class DesignViva1 extends Assessment1 {
        DesignViva1(String cid, String title) {
            super(cid,title, false,DESIGN_MARK, EXPLANATION_MARK);
        }

        @Override
        double getFinalPercentage() {
            return (rawValue(DESIGN_MARK)+rawValue(EXPLANATION_MARK))/2.0;
        }
    }
}
