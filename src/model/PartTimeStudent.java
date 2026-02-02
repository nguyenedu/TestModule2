package model;

import model.Student;

public class PartTimeStudent extends Student {
    private static final double PRICE_PER_COURSE = 500.0;

    public PartTimeStudent(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getRole() {
        return "PART_TIME";
    }

    @Override
    public double calculateTuitionFee() {
        return enrolledCourses.size() * PRICE_PER_COURSE;
    }
}