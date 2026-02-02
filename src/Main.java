import exception.CourseNotFoundException;
import exception.DuplicateStudentException;
import exception.InvalidScoreException;
import exception.StudentNotFoundException;
import model.Course;
import model.FullTimeStudent;
import model.PartTimeStudent;
import model.Student;
import service.StudentManagement;
import task.ScoreInputTask;

import java.util.*;

public class Main {
    private static StudentManagement management = new StudentManagement();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("Chọn chức năng: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 0:
                        running = false;
                        System.out.println("Cảm ơn đã sử dụng hệ thống");
                        break;
                    case 1:
                        management.initSampleData();
                        break;
                    case 2:
                        addStudent();
                        break;
                    case 3:
                        addCourse();
                        break;
                    case 4:
                        enrollStudentToCourse();
                        break;
                    case 5:
                        inputScore();
                        break;
                    case 6:
                        viewStudentScoreBoard();
                        break;
                    case 7:
                        searchStudent();
                        break;
                    case 8:
                        filterAndSort();
                        break;
                    case 9:
                        calculateTuition();
                        break;
                    case 10:
                        autoInputScoresWithThreads();
                        break;
                    default:
                        System.out.println("Lựa chọn không hợp lệ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số");
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage());
            }

            System.out.println();
        }

        sc.close();
    }

    private static void printMenu() {
        System.out.println("===== STUDENT MANAGEMENT SYSTEM =====");
        System.out.println("1. Khởi tạo dữ liệu mẫu");
        System.out.println("2. Thêm sinh viên");
        System.out.println("3. Thêm khóa học");
        System.out.println("4. Đăng ký khóa học cho sinh viên");
        System.out.println("5. Nhập điểm cho sinh viên");
        System.out.println("6. Xem bảng điểm của 1 sinh viên");
        System.out.println("7. Tìm kiếm sinh viên");
        System.out.println("8. Lọc & sắp xếp sinh viên");
        System.out.println("9. Tính học phí sinh viên");
        System.out.println("10. Nhập điểm tự động bằng đa luồng");
        System.out.println("0. Thoát");
        System.out.println("=====================================");
    }

    private static void addStudent() {
        try {
            System.out.print("Nhập loại sinh viên (1-Full time, 2-Part time): ");
            int type = Integer.parseInt(sc.nextLine());

            System.out.print("Nhập mã sinh viên: ");
            String id = sc.nextLine();

            System.out.print("Nhập họ tên: ");
            String name = sc.nextLine();

            System.out.print("Nhập email: ");
            String email = sc.nextLine();

            Student student;
            if (type == 1) {
                student = new FullTimeStudent(id, name, email);
            } else if (type == 2) {
                student = new PartTimeStudent(id, name, email);
            } else {
                System.out.println("Loại sinh viên không hợp lệ");
                return;
            }

            management.addStudent(student);
        } catch (DuplicateStudentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Lỗi: Dữ liệu không hợp lệ");
        }
    }

    private static void addCourse() {
        try {
            System.out.print("Nhập mã khóa học: ");
            String id = sc.nextLine();

            System.out.print("Nhập tên khóa học: ");
            String name = sc.nextLine();

            System.out.print("Nhập số tín chỉ: ");
            int credits = Integer.parseInt(sc.nextLine());

            Course course = new Course(id, name, credits);
            management.addCourse(course);
        } catch (DuplicateStudentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Lỗi: Dữ liệu không hợp lệ");
        }
    }

    private static void enrollStudentToCourse() {
        try {
            System.out.print("Nhập mã sinh viên: ");
            String studentId = sc.nextLine();

            System.out.print("Nhập mã khóa học: ");
            String courseId = sc.nextLine();

            management.enrollStudentToCourse(studentId, courseId);
        } catch (StudentNotFoundException | CourseNotFoundException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private static void inputScore() {
        try {
            System.out.print("Nhập mã sinh viên: ");
            String studentId = sc.nextLine();

            System.out.print("Nhập mã khóa học: ");
            String courseId = sc.nextLine();

            System.out.print("Nhập điểm (0-10): ");
            double score = Double.parseDouble(sc.nextLine());

            management.inputScore(studentId, courseId, score);
        } catch (StudentNotFoundException | CourseNotFoundException | InvalidScoreException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Lỗi: Dữ liệu không hợp lệ");
        }
    }

    private static void viewStudentScoreBoard() {
        try {
            System.out.print("Nhập mã sinh viên: ");
            String studentId = sc.nextLine();

            management.printStudentScoreBoard(studentId);
        } catch (StudentNotFoundException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private static void searchStudent() {
        System.out.println("1. Tìm theo mã sinh viên");
        System.out.println("2. Tìm theo tên và GPA");
        System.out.print("Chọn: ");

        try {
            int choice = Integer.parseInt(sc.nextLine());

            if (choice == 1) {
                System.out.print("Nhập mã sinh viên: ");
                String id = sc.nextLine();

                Optional<Student> result = management.searchStudent(id);
                if (result.isPresent()) {
                    Student s = result.get();
                    System.out.println("Tìm thấy sinh viên:");
                    System.out.println("Mã: " + s.getId());
                    System.out.println("Tên: " + s.getName());
                    System.out.println("Email: " + s.getEmail());
                    System.out.println("Loại: " + s.getRole());
                    System.out.printf("GPA: %.2f", s.getGpa());
                } else {
                    System.out.println("Không tìm thấy sinh viên");
                }
            } else if (choice == 2) {
                System.out.print("Nhập tên (hoặc một phần tên): ");
                String name = sc.nextLine();

                System.out.print("Nhập GPA tối thiểu: ");
                double minGpa = Double.parseDouble(sc.nextLine());

                List<Student> results = management.searchStudent(name, minGpa);
                if (results.isEmpty()) {
                    System.out.println("Không tìm thấy sinh viên nào");
                } else {
                    System.out.println("Danh sách sinh viên tìm thấy:");
                    for (Student s : results) {
                        System.out.printf("- %s (%s) - GPA: %.2f - %s",
                                s.getName(), s.getId(), s.getGpa(), s.getRole());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi: Dữ liệu không hợp lệ");
        }
    }

    private static void filterAndSort() {
        System.out.println("1. Lọc sinh viên theo GPA > 8");
        System.out.println("2. Lọc sinh viên Full-time");
        System.out.println("3. Sắp xếp theo GPA giảm dần");
        System.out.println("4. Sắp xếp theo tên A-Z");
        System.out.print("Chọn: ");

        try {
            int choice = Integer.parseInt(sc.nextLine());
            List<Student> results = new ArrayList<>();

            switch (choice) {
                case 1:
                    results = management.filterStudents(s -> s.getGpa() > 8.0);
                    System.out.println("Sinh viên có GPA > 8:");
                    break;
                case 2:
                    results = management.filterStudents(s -> s.getRole().equals("FULL_TIME"));
                    System.out.println("Sinh viên Full-time:");
                    break;
                case 3:
                    results = management.sortStudentsByGpaDesc();
                    System.out.println("Danh sách sinh viên (GPA giảm dần):");
                    break;
                case 4:
                    results = management.sortStudentsByNameAsc();
                    System.out.println("Danh sách sinh viên (Tên A-Z):");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ");
                    return;
            }

            if (results.isEmpty()) {
                System.out.println("Không có sinh viên nào");
            } else {
                for (Student s : results) {
                    System.out.printf("- %s (%s) - GPA: %.2f - %s",
                            s.getName(), s.getId(), s.getGpa(), s.getRole());
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi: Dữ liệu không hợp lệ");
        }
    }

    private static void calculateTuition() {
        try {
            System.out.print("Nhập mã sinh viên: ");
            String studentId = sc.nextLine();

            Optional<Student> studentOpt = management.searchStudent(studentId);
            if (!studentOpt.isPresent()) {
                System.out.println("Không tìm thấy sinh viên");
                return;
            }

            Student student = studentOpt.get();
            double tuition = management.calculateTuition(student);

            System.out.println("===== THÔNG TIN HỌC PHÍ =====");
            System.out.println("Sinh viên: " + student.getName());
            System.out.println("Mã SV: " + student.getId());
            System.out.println("Loại: " + student.getRole());
            System.out.println("Số khóa đã đăng ký: " + student.getEnrolledCourses().size());
            System.out.printf("Học phí: %.2f VNĐ", tuition);
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private static void autoInputScoresWithThreads() {
        try {
            List<Student> allStudents = management.getStudentRepository().findAll();
            if (allStudents.isEmpty()) {
                System.out.println("Không có sinh viên nào trong hệ thống");
                return;
            }

            int numThreads = 3;
            int studentsPerThread = (allStudents.size() + numThreads - 1) / numThreads;

            List<Thread> threads = new ArrayList<>();

            for (int i = 0; i < numThreads; i++) {
                int start = i * studentsPerThread;
                int end = Math.min(start + studentsPerThread, allStudents.size());

                if (start >= allStudents.size()) break;

                List<String> studentIds = new ArrayList<>();
                for (int j = start; j < end; j++) {
                    studentIds.add(allStudents.get(j).getId());
                }

                List<String> courseIds = new ArrayList<>();
                for (Course course : management.getCourseRepository().findAll()) {
                    courseIds.add(course.getCourseId());
                }

                ScoreInputTask task = new ScoreInputTask(studentIds, courseIds, management);
                Thread thread = new Thread(task);
                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            System.out.println("Đã nhập điểm tự động cho tất cả sinh viên bằng " + numThreads + " luồng");
            System.out.println("Tổng số sinh viên: " + allStudents.size());
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }
}