import java.util.*;

public class DBMS implements DBMSInterface {
    private MyStorageBackend<String, Student> studentNumberIndex;
    private MyStorageBackend<Integer, Student> overallScoreIndex;

    public DBMS() {
        this.studentNumberIndex = new MyStorageBackend<>(
                (s) -> hashCodeString(s)
        );
        this.overallScoreIndex = new MyStorageBackend<>(
                (s) -> s
        );
    }

    /**
     * This function is insert student information in two index structures (studentNumberIndex and overallScoreIndex).
     *
     * @param student the student is you want to add in the database.
     * @require getStudentNumber() and getOverallScore() always return and no exception thrown, no side effect.
     */
    public void insertStudent(Student student) {
        studentNumberIndex.insert(student.getStudentNumber(), student);
        overallScoreIndex.insert(student.getOverallScore(), student);
    }

    /**
     * This function is search student information by studentNumber.
     *
     * @param studentNumber is a student's Number which student you want to find.
     * @return if the studentNumber can not find the student information return NULL, else return the student information.
     * @require always return and no exception thrown, no side effect.
     */
    public Student queryByStudentNumber(String studentNumber) {
        if (studentNumberIndex.search(studentNumber).isEmpty()) {
            return null;
        }
        return studentNumberIndex.search(studentNumber).get(0);
    }

    /**
     * This function is search student information by the overallScore.
     *
     * @param score require the student's overallScore (midScore + finalScore).
     * @return a List of student information which is all match this score.
     */
    public List<Student> queryByScore(int score) {
        return overallScoreIndex.search(score);
    }

    /**
     * This function is used for delete student information from two index (studentNumberIndex and overallScoreIndex).
     *
     * @param student the student is which you want to delete from the database.
     * @require getStudentNumber() and getOverallScore() always return and no exception thrown, no side effect.
     */
    @Override
    public void deleteStudent(Student student) {
        this.studentNumberIndex.delete(student.getStudentNumber(), student);
        this.overallScoreIndex.delete(student.getOverallScore(), student);
    }

    /**
     * This function is used for calculate the hash for String.
     *
     * @param key the key is a string which want to calculate it hash number.
     * @return return a int type of number
     */
    public int hashCodeString(String key) {
        int hashCode = 0;
        for (int i = 0; i < key.length(); i++) {
            hashCode = (hashCode * 31 + key.charAt(i)) % 100;
        }
        return hashCode;
    }
}

@FunctionalInterface
interface MyHashFunction<T> {
    int hash(T key);
}

class SkipListNode<T, U> {
    T key;
    List<U> value = new ArrayList<>();
    List<SkipListNode> nextSkipListNode = new ArrayList<>();

    public SkipListNode(T key, U value, Integer level) {
        this.key = key;
        this.value.add(value);
        for (int i = 0; i < level; i++) {
            this.nextSkipListNode.add(null);
        }
    }
}

class MyStorageBackend<T, U> {

    private MyHashFunction<T> myHashFunction;

    private SkipListNode<Integer, U> head;

    private int MaxLevel = 4;

    private Random random = new Random();

    public MyStorageBackend(MyHashFunction<T> myHashFunction) {
        this.myHashFunction = myHashFunction;
        this.head = new SkipListNode<>(null, null, MaxLevel);
        for (int i = 0; i < MaxLevel; i++) {
            this.head.nextSkipListNode.set(i, null);
        }
    }

    /**
     * This function is used for insert student information into the database.
     *
     * @param key the index of key.
     * @param item the specific information in student structures.
     */
    public void insert(T key, U item) {
        Integer num = myHashFunction.hash(key);
        int level = 1;
        SkipListNode<Integer, U> current = head;
        List<SkipListNode<Integer, U>> update = new ArrayList<>();
        while (random.nextDouble() > 0.5 && level < 4) {
            level++;
        }
        for (int i = MaxLevel - 1; i >= 0; i--) {
            while (current.nextSkipListNode.get(i) != null && (int) current.nextSkipListNode.get(i).key < num) {
                current = current.nextSkipListNode.get(i);
            }
            update.add(0, current);
        }
        current = current.nextSkipListNode.get(0);
        if (current == null || !current.key.equals(num)) {
            current = new SkipListNode<>(num, item, level);
            for (int i = 0; i < level; i++) {
                current.nextSkipListNode.set(i, update.get(i).nextSkipListNode.get(i));
                update.get(i).nextSkipListNode.set(i, current);
            }
        } else {
            current.value.add(item);
        }
    }

    /**
     * This function is used for searching the student information by using key index.
     *
     * @param key input the index of student which you want to search.
     * @return return a list of value, if can not find this student return void List.
     */
    public List<U> search(T key) {
        int num = myHashFunction.hash(key);
        SkipListNode<Integer, U> current = head;
        for (int i = MaxLevel - 1; i >= 0; i--) {
            while (current.nextSkipListNode.get(i) != null && (int) current.nextSkipListNode.get(i).key < num) {
                current = current.nextSkipListNode.get(i);
            }
        }
        current = current.nextSkipListNode.get(0);
        if (current != null && current.key.equals(num)) {
            return current.value;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * This function is used for delete the student from two index (studentNumberIndex and overallScoreIndex).
     * And only it match both key and value that it will be deleted.
     *
     * @param key the index of the student.
     * @param value other information.
     */
    public void delete(T key, U value) {
        int num = myHashFunction.hash(key);
        SkipListNode<Integer, U> current = head;
        List<SkipListNode<Integer, U>> update = new ArrayList<>();
        for (int i = MaxLevel - 1; i >= 0; i--) {
            while (current.nextSkipListNode.get(i) != null && (int) current.nextSkipListNode.get(i).key < num) {
                current = current.nextSkipListNode.get(i);
            }
            update.add(0, current);
        }
        current = current.nextSkipListNode.get(0);
        if (current.key == num) {
            if (current.value.size() == 1) {
                for (int i = 0; i < MaxLevel; i++) {
                    if (update.get(i).nextSkipListNode.get(i) == current) {
                        update.get(i).nextSkipListNode.set(i, current.nextSkipListNode.get(i));
                    }
                }
            } else {
                int flag = 0;
                for (int i = 0; i < current.value.size(); i++) {
                    if (current.value.get(i) == value){
                        flag = i;
                        break;
                    }
                }
                current.value.remove(flag);
            }
        }
    }
}
