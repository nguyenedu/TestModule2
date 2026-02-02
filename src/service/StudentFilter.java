package service;

import model.Student;

@FunctionalInterface
public interface StudentFilter {
    boolean filter(Student s);
}