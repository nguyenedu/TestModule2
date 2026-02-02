package task;

import model.Course;
import model.Student;
import service.StudentManagement;

import java.util.List;
import java.util.Random;

public class ScoreInputTask implements Runnable {
    private List<String> studentIds;
    private List<String> courseIds;
    private StudentManagement management;

    public ScoreInputTask(List<String> studentIds, List<String> courseIds, StudentManagement management) {
        this.studentIds = studentIds;
        this.courseIds = courseIds;
        this.management = management;
    }

    @Override
    public void run() {
        Random random = new Random();
        for (String studentId : studentIds) {
            try {
                Student student = management.getStudentRepository().findById(studentId).orElse(null);
                if (student != null) {
                    for (Course course : student.getEnrolledCourses()) {
                        double score = random.nextDouble() * 10;
                        management.safeInputScore(studentId, course.getCourseId(), score);
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi nhập điểm cho sinh viên " + studentId + ": " + e.getMessage());
            }
        }
    }
}