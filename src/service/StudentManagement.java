package service;

import exception.CourseNotFoundException;
import exception.DuplicateStudentException;
import exception.InvalidScoreException;
import exception.StudentNotFoundException;
import model.Course;
import model.FullTimeStudent;
import model.PartTimeStudent;
import model.Student;
import repository.Repository;

import java.util.*;
import java.util.stream.Collectors;

public class StudentManagement {
    private Repository<Student> studentRepository;
    private Repository<Course> courseRepository;
    private Map<String, Map<String, Double>> scoreBoard;

    public StudentManagement() {
        this.studentRepository = new Repository<>();
        this.courseRepository = new Repository<>();
        this.scoreBoard = new HashMap<>();
    }

    public void initSampleData() {
        Course[] courses = {
                new Course("C001", "Lập trình Java", 3),
                new Course("C002", "Cơ sở dữ liệu", 4),
                new Course("C003", "Mạng máy tính", 3),
                new Course("C004", "Thuật toán", 4),
                new Course("C005", "Công nghệ Web", 3)
        };

        for (Course course : courses) {
            courseRepository.add(course.getCourseId(), course);
        }

        Student[] students = {
                new FullTimeStudent("FT001", "Nguyễn Văn An", "an@email.com"),
                new FullTimeStudent("FT002", "Trần Thị Bình", "binh@email.com"),
                new FullTimeStudent("FT003", "Lê Văn Cường", "cuong@email.com"),
                new FullTimeStudent("FT004", "Phạm Thị Dung", "dung@email.com"),
                new FullTimeStudent("FT005", "Hoàng Văn Em", "em@email.com"),
                new PartTimeStudent("PT001", "Đỗ Thị Phương", "phuong@email.com"),
                new PartTimeStudent("PT002", "Vũ Văn Giang", "giang@email.com"),
                new PartTimeStudent("PT003", "Bùi Thị Hà", "ha@email.com"),
                new PartTimeStudent("PT004", "Ngô Văn Inh", "inh@email.com"),
                new PartTimeStudent("PT005", "Mai Thị Kiều", "kieu@email.com")
        };

        Random random = new Random();
        for (Student student : students) {
            studentRepository.add(student.getId(), student);

            List<Course> allCourses = new ArrayList<>(Arrays.asList(courses));
            Collections.shuffle(allCourses);
            int numCourses = 2 + random.nextInt(2);

            for (int i = 0; i < numCourses; i++) {
                student.enrollCourse(allCourses.get(i));
            }
        }

        System.out.println("Đã khởi tạo dữ liệu mẫu thành công");
    }

    public void addStudent(Student student) throws DuplicateStudentException {
        if (studentRepository.exists(student.getId())) {
            throw new DuplicateStudentException("Sinh viên với ID " + student.getId() + " đã tồn tại");
        }
        studentRepository.add(student.getId(), student);
        System.out.println("Đã thêm sinh viên thành công");
    }

    public void addCourse(Course course) throws DuplicateStudentException {
        if (courseRepository.exists(course.getCourseId())) {
            throw new DuplicateStudentException("Khóa học với ID " + course.getCourseId() + " đã tồn tại");
        }
        courseRepository.add(course.getCourseId(), course);
        System.out.println("Đã thêm khóa học thành công");
    }

    public void enrollStudentToCourse(String studentId, String courseId)
            throws StudentNotFoundException, CourseNotFoundException {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (!studentOpt.isPresent()) {
            throw new StudentNotFoundException("Không tìm thấy sinh viên với ID: " + studentId);
        }

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new CourseNotFoundException("Không tìm thấy khóa học với ID: " + courseId);
        }

        Student student = studentOpt.get();
        Course course = courseOpt.get();

        if (student.getEnrolledCourses().contains(course)) {
            System.out.println("Sinh viên đã đăng ký khóa học này rồi");
            return;
        }

        student.enrollCourse(course);
        System.out.println("Đăng ký khóa học thành công");
    }

    public void inputScore(String studentId, String courseId, double score)
            throws StudentNotFoundException, CourseNotFoundException, InvalidScoreException {
        if (!studentRepository.exists(studentId)) {
            throw new StudentNotFoundException("Không tìm thấy sinh viên với ID: " + studentId);
        }

        if (!courseRepository.exists(courseId)) {
            throw new CourseNotFoundException("Không tìm thấy khóa học với ID: " + courseId);
        }

        if (score < 0 || score > 10) {
            throw new InvalidScoreException("Điểm phải trong khoảng 0-10");
        }

        scoreBoard.putIfAbsent(studentId, new HashMap<>());
        scoreBoard.get(studentId).put(courseId, score);
        System.out.println("Đã nhập điểm thành công");
    }

    public synchronized void safeInputScore(String studentId, String courseId, double score)
            throws StudentNotFoundException, CourseNotFoundException, InvalidScoreException {
        inputScore(studentId, courseId, score);
    }

    public void printStudentScoreBoard(String studentId) throws StudentNotFoundException {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (!studentOpt.isPresent()) {
            throw new StudentNotFoundException("Không tìm thấy sinh viên với ID: " + studentId);
        }

        Student student = studentOpt.get();
        System.out.println("===== BẢNG ĐIỂM SINH VIÊN =====");
        System.out.println("Mã SV: " + student.getId());
        System.out.println("Họ tên: " + student.getName());
        System.out.println("Loại: " + student.getRole());
        System.out.println("ĐIỂM CHI TIẾT:");

        Map<String, Double> scores = scoreBoard.get(studentId);
        if (scores == null || scores.isEmpty()) {
            System.out.println("Chưa có điểm nào");
            return;
        }

        double sum = 0.0;
        int count = 0;
        for (Course course : student.getEnrolledCourses()) {
            String courseId = course.getCourseId();
            if (scores.containsKey(courseId)) {
                double score = scores.get(courseId);
                System.out.printf("- %s (%s): %.2f",
                        course.getCourseName(), courseId, score);
                sum += score;
                count++;
            }
        }

        if (count > 0) {
            double average = sum / count;
            student.setGpa(average);
            System.out.printf("ĐIỂM TRUNG BÌNH: %.2f", average);
        }
    }

    public Optional<Student> searchStudent(String id) {
        return studentRepository.findById(id);
    }

    public List<Student> searchStudent(String name, double minGpa) {
        return studentRepository.findAll().stream()
                .filter(s -> s.getName().toLowerCase().contains(name.toLowerCase())
                        && s.getGpa() >= minGpa)
                .collect(Collectors.toList());
    }

    public double calculateTuition(Student student) {
        return student.calculateTuitionFee();
    }

    public List<Student> filterStudents(StudentFilter filter) {
        return studentRepository.findAll().stream()
                .filter(filter::filter)
                .collect(Collectors.toList());
    }

    public List<Student> sortStudentsByGpaDesc() {
        List<Student> students = studentRepository.findAll();
        students.sort((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()));
        return students;
    }

    public List<Student> sortStudentsByNameAsc() {
        List<Student> students = studentRepository.findAll();
        students.sort(Comparator.comparing(Student::getName));
        return students;
    }

    public Repository<Student> getStudentRepository() {
        return studentRepository;
    }

    public Repository<Course> getCourseRepository() {
        return courseRepository;
    }

    public Map<String, Map<String, Double>> getScoreBoard() {
        return scoreBoard;
    }
}