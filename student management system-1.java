package college;
import java.io.Serializable;
enum Branch{
    CSE, CIVIL, ECE, MECH, IT
}
enum Residence{
    HOSTELER, DAY_SCHOLAR
}

class InvalidDataException extends Exception{
    public InvalidDataException(String message){
        super(message);
    }
}

interface UniversityRules{
    double getRemainingFee();
    void calculateCGPA();
}

abstract class Person implements Serializable{
    private String name;
    public Person(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
    public abstract void displayDetails();
}

public class Student extends Person implements UniversityRules{

    private Integer rollNumber;
    private Branch branch;
    private Residence residence;

    private Integer classesHeld;
    private Integer classesAttended;

    private Double totalFee;
    private Double hostelFee;
    private Double busFee;
    private Double feePaid;

    private double[][] semesterGrades = new double[4][2];

    private Double finalCGPA = 0.0;

    public Student(
            Integer rollNumber,
            String name,
            Branch branch,
            Residence residence,
            Integer classesHeld,
            Integer classesAttended,
            Double totalFee,
            Double hostelFee,
            Double busFee,
            Double feePaid){
        super(name);

        this.rollNumber = rollNumber;
        this.branch = branch;
        this.residence = residence;
        this.classesHeld = classesHeld;
        this.classesAttended = classesAttended;
        this.totalFee = totalFee;
        this.hostelFee = hostelFee;
        this.busFee = busFee;
        this.feePaid = feePaid;
    }
  
    public Integer getRollNumber(){
        return rollNumber;
    }

    public Branch getBranch(){
        return branch;
    }

  public void updateAttendance(
            int addedHeld,
            int addedAttended)
            throws InvalidDataException{

        if (addedHeld < 0 || addedAttended < 0){
            throw new InvalidDataException(
                    "Attendance cannot be negative.");
        }

        if (addedAttended > addedHeld){
            throw new InvalidDataException(
                    "Cannot attend more classes than held!");
        }

        classesHeld += addedHeld;
        classesAttended += addedAttended;
    }

    public double getAttendancePercentage(){
        if (classesHeld == 0){
            return 0.0;
        }
        return Math.round(
                ((double) classesAttended / classesHeld)
                        * 1000.0) / 10.0;
    }

  public void addSemesterGrade(
            int year,
            int semester,
            double gpa)
            throws InvalidDataException{

        if (year < 1 || year > 4){
            throw new InvalidDataException(
                    "Year must be between 1 and 4.");
        }

        if (semester < 1 || semester > 2){
            throw new InvalidDataException(
                    "Semester must be 1 or 2.");
        }

        if (gpa < 0 || gpa > 10){
            throw new InvalidDataException(
                    "GPA must be between 0 and 10.");
        }

        semesterGrades[year - 1][semester - 1] = gpa;
        calculateCGPA();
    }

    @Override
    public double getRemainingFee(){

        double totalAmount =
                totalFee + hostelFee + busFee;

        return Math.max(
                0.0,
                totalAmount - feePaid);
    }

    @Override
    public void calculateCGPA(){

        double total = 0;
        int count = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                if (semesterGrades[i][j] > 0) {
                    total += semesterGrades[i][j];
                    count++;
                }
            }
        }

        if (count > 0) {
            finalCGPA =
                    Math.round(
                            (total / count) * 100.0)
                            / 100.0;
        }
    }

    @Override
    public void displayDetails() {
        System.out.println();
        System.out.println("...............");
        System.out.println(
                "Roll Number   : " + rollNumber);
        System.out.println(
                "Name          : " + getName());
        System.out.println(
                "Branch        : " + branch);
        System.out.println(
                "Residence     : " + residence);
        System.out.println(
                "Attendance    : "
                + classesAttended
                + "/"
                + classesHeld
                + " ("
                + getAttendancePercentage()
                + "%)");
        System.out.println(
                "CGPA          : " + finalCGPA);
        System.out.println(
                "Academic Fee  : Rs."
                + totalFee);
        System.out.println(
                "Hostel Fee    : Rs."
                + hostelFee);
        System.out.println(
                "Bus Fee       : Rs."
                + busFee);
        System.out.println(
                "Fee Paid      : Rs."
                + feePaid);
        System.out.println(
                "Remaining Fee : Rs."
                + getRemainingFee());

        System.out.println("...............");
    }

    public String toCSV(){
        return rollNumber + ","
                + getName() + ","
                + branch + ","
                + residence + ","
                + classesHeld + ","
                + classesAttended + ","
                + totalFee + ","
                + hostelFee + ","
                + busFee + ","
                + feePaid;
    }
}
