package assessments;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

final class StudentResult1 {
    enum AssessmentType1 {
        PROGRAMMING_TEST(AssessmentFeed.PROGRAMMING_TEST,30,25, Assessment1.ProgrammingTest1::new),
        AI_PROJECT(AssessmentFeed.AI_PROJECT,70,45, Assessment1.AiProject1::new),
        DESIGN_VIVA(AssessmentFeed.DESIGN_VIVA,0,30, Assessment1.DesignViva1::new);

        private final String title;
        private final int ugWeight;
        private final int mscWeight;
        private final BiFunction<String, String, Assessment1> create;

        AssessmentType1(String title, int ugWeight, int mscWeight,
                        BiFunction<String, String, Assessment1> create) {
            this.title=title;
            this.ugWeight=ugWeight;
            this.mscWeight=mscWeight;
            this.create=create;
        }

        String getTitle() {
            return title;
        }

        private int weightFor(String studentType) {
            return "MSc".equals(studentType) ? mscWeight : ugWeight;
        }
    }

    private final String cid;
    private final String name;
    private final String studentType;
    private final Map<AssessmentType1, Assessment1> assessments=new EnumMap<>(AssessmentType1.class);

    StudentResult1(String cid) {
        this.cid=cid;
        name=AssessmentFeed.getStudentName(cid);
        studentType=AssessmentFeed.getStudentType(cid);

        for (AssessmentType1 type : AssessmentType1.values()) {
            if (type.weightFor(studentType)>0) {
                assessments.put(type,type.create.apply(cid, type.title));
            }
        }
    }

    String getCid() {
        return cid;
    }

    String getName() {
        return name;
    }

    String getStudentType() {
        return studentType;
    }

    Assessment1 getAssessment(AssessmentType1 type) {
        return assessments.get(type);
    }

    double getModulePercentage() {
        double weightedTotal=0.0;
        int totalWeight=0;
        for (Map.Entry<AssessmentType1, Assessment1> entry : assessments.entrySet()) {
            int weight=entry.getKey().weightFor(studentType);
            weightedTotal+=weight*entry.getValue().getFinalPercentage();
            totalWeight+=weight;
        }
        return weightedTotal/totalWeight;
    }

    boolean hasAiDiscrepancy() {
        double aiTotal=0.0;
        double nonAiTotal=0.0;
        int aiCount=0;
        int nonAiCount=0;
        for (Assessment1 assessment : assessments.values()) {
            if (assessment.isAiPermitted()) {
                aiTotal+=assessment.getFinalPercentage();
                aiCount++;
            } else {
                nonAiTotal+=assessment.getFinalPercentage();
                nonAiCount++;
            }
        }
        return aiCount>0 && nonAiCount>0 && aiTotal/aiCount-nonAiTotal/nonAiCount>=20.0;
    }
}
