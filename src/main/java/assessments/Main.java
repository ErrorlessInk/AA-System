package assessments;

import assessments.StudentResult1.AssessmentType1;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public final class Main {
    public static void main(String[] args) {
        List<StudentResult1> students=new ArrayList<>();
        for (String cid : AssessmentFeed.getCIDs()) {
            students.add(new StudentResult1(cid));
        }
        printReport(students);

        if (!GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeLater(() -> showGrid(students));
        }
    }

    private static void printReport(List<StudentResult1> students) {
        System.out.println("Assessment Results Report");
        for (StudentResult1 student : students) {
            System.out.printf("%n%s | CID: %s | Type: %s%n",
                    student.getName(),student.getCid(), student.getStudentType());
            for (AssessmentType1 type : AssessmentType1.values()) {
                Assessment1 assessment=student.getAssessment(type);
                if (assessment==null) {
                    System.out.println("  "+type.getTitle()+": N/A");
                    continue;
                }
                System.out.printf("  %s: %s%n",assessment.getTitle(),
                        percentage(assessment.getFinalPercentage()));

                for (Map.Entry<String, Double> raw : assessment.getRawValues().entrySet()) {
                    double mark=raw.getValue();
                    String value=AssessmentFeed.EXPLAINED_SUCCESSFULLY.equals(raw.getKey())
                            ? (mark==1.0 ? "Yes (1.0)" : "No (0.0)") : percentage(mark);
                    System.out.printf("    %s: %s%n",raw.getKey(), value);
                }
            }
            System.out.println("  Overall module mark: "+percentage(student.getModulePercentage()));
            System.out.println("  AI discrepancy: "+(student.hasAiDiscrepancy() ? "Yes" : "No"));
        }
    }

    private static void showGrid(List<StudentResult1> students) {
        AssessmentType1[] types=AssessmentType1.values();
        String[] headers=new String[types.length+4];
        headers[0]="Student Name";
        headers[1]="CID";
        headers[2]="Type";
        for (int i=0; i<types.length; i++) {
            headers[i+3]=types[i].getTitle();
        }
        headers[headers.length-1]="Overall Module Mark";

        DefaultTableModel model=new DefaultTableModel(headers,0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (StudentResult1 student : students) {
            String[] row=new String[headers.length];
            row[0]=student.getName();
            row[1]=student.getCid();
            row[2]=student.getStudentType();
            for (int i=0; i<types.length; i++) {
                Assessment1 assessment=student.getAssessment(types[i]);
                row[i+3]=assessment==null ? "N/A" : percentage(assessment.getFinalPercentage());
            }
            row[row.length-1]=percentage(student.getModulePercentage());
            model.addRow(row);
        }

        JTable table=new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component cell=super.prepareRenderer(renderer,row, column);
                boolean warning=students.get(convertRowIndexToModel(row)).hasAiDiscrepancy();

                cell.setBackground(warning ? Color.RED : isCellSelected(row,column)
                        ? getSelectionBackground() : getBackground());
                cell.setForeground(warning ? Color.BLACK : isCellSelected(row,column)
                        ? getSelectionForeground() : getForeground());
                return cell;
            }
        };
        int gridWidth=0;
        for (int i=0; i<headers.length; i++) {
            int width=Math.max(60,table.getFontMetrics(table.getFont()).stringWidth(headers[i])+32);
            if (i==0) {
                width=Math.max(width,140);
            }
            table.getColumnModel().getColumn(i).setPreferredWidth(width);
            gridWidth+=width;
        }
        table.setRowHeight(24);
        table.setPreferredScrollableViewportSize(new Dimension(gridWidth,160));

        JFrame frame=new JFrame("Assessment Results");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(table));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String percentage(double mark) {
        return String.format(Locale.ROOT,"%.2f%%", mark);
    }
}
