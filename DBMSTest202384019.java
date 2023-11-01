import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DBMSTest202384019 {
    private final DBMSInterface database = new DBMS();
    Student student1 = new Student("S12345", "John Doe", 85, 90);
    Student student2 = new Student("S2", "Olivia Martinez", 75, 22);
    Student student3 = new Student("S3", "Ethan Kim", 65, 33);
    Student student4 = new Student("S4", "Sophia Patel", 15, 55);
    Student student5 = new Student("S5", "Jackson Brown", 0, 99);
    Student student6 = new Student("S6", "Mia Nguyen", 30, 68);

    // initial the database
    void createDBMS() {
        database.insertStudent(student1);
        database.insertStudent(student2);
        database.insertStudent(student3);
        database.insertStudent(student4);
        database.insertStudent(student5);
        database.insertStudent(student6);
    }

    // Test query by score.
    // If the score have more student, output them all.
    // If no student is this score, return void List.
    @Test
    public void testQueryByScore() {
        createDBMS();
        List<Student> result1 = database.queryByScore(175);
        assertEquals(1, result1.size());
        List<Student> result2 = database.queryByScore(98);
        assertEquals(2, result2.size());
        assertTrue(result2.contains(student6));
        assertTrue(result2.contains(student3));
        List<Student> result3 = database.queryByScore(100);
        assertEquals(new ArrayList<>(), result3);
    }

    // Test query by studentNumber.
    // If searching the studentNumber is not exist, return null.
    @Test
    public void testQueryByStudentNumber() {
        createDBMS();
        Student result1 = database.queryByStudentNumber("S12345");
        assertEquals("John Doe", result1.getName());
        assertEquals(student1, result1);
        Student result2 = database.queryByStudentNumber("S99");
        assertNull(result2);
    }

    // Test delete student.
    // After delete should not be queried by queryByStudentNumber and queryByScore.
    @Test
    public void testDeleteStudent() {
        createDBMS();
        database.deleteStudent(student4);
        Student result1 = database.queryByStudentNumber("S4");
        assertNull(result1);
        database.deleteStudent(student3);
        List<Student> result2 = database.queryByScore(98);
        assertTrue(result2.contains(student6));
        assertEquals(1, result2.size());
    }

    // Test insert student.
    // After insert should be queried by queryByStudentNumber and queryByScore.
    @Test
    public void testInsertStudent() {
        createDBMS();
        Student result1 = database.queryByStudentNumber("S100");
        assertNull(result1);
        List<Student> result2 = database.queryByScore(200);
        assertEquals(result2, new ArrayList<>());
        Student student100 = new Student("S100", "YuHan Bai", 100, 100);
        database.insertStudent(student100);
        Student result3 = database.queryByStudentNumber("S100");
        assertEquals(200, result3.getOverallScore());
        List<Student> result4 = database.queryByScore(200);
        assertTrue(result4.contains(student100));
    }
}
