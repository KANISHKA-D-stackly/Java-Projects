package college;
import java.io.*;
import java.util.*;

public class StudentManagementSystem{
    static HashMap<Integer, Student> studentMap=new HashMap<>();
    static HashSet<Integer> rollTracker=new HashSet<>();
    static Stack<Student> undoStack=new Stack<>();
    static final String FILE_NAME="students.txt";

    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        loadFromFile();
        Thread autoSave=new Thread(() -> {
            while (true){
                try {
                    Thread.sleep(30000);
                    saveToFile();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        autoSave.setDaemon(true);
        autoSave.start();
        System.out.println("STUDENT MANAGEMENT SYSTEM");
        boolean running=true;

        while (running){
            System.out.println();
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Undo Last Delete");
            System.out.println("6. Save and Exit");
            System.out.print("\nEnter your choice: ");
            try {
                int choice=sc.nextInt();
                sc.nextLine();
                switch (choice) {
                case 1:
                    addStudent(sc);
                    break;
                case 2:
                    viewAllStudents();
                    break;
                case 3:
                    updateStudent(sc);
                    break;
                case 4:
                    deleteStudent(sc);
                    break;
                case 5:
                    undoDelete();
                    break;
                case 6:
                    saveToFile();
                    running = false;
                    System.out.println("Data saved successfully.");
                    System.out.println("Thank you for using Student Management System!");
                    break;

                default:
                    System.out.println("Invalid choice. Enter 1 to 6.");
                }

            } catch (InputMismatchException e){
                System.out.println("Please enter a valid number.");
                sc.nextLine();
            }
        }
        sc.close();
    }
  
    static void addStudent(Scanner sc){
        try {
            System.out.println("\n ADD STUDENT");
            System.out.print("Enter Roll Number: ");
            int rollNumber = sc.nextInt();
            sc.nextLine();

            if (rollTracker.contains(rollNumber)){
                System.out.println("Roll number already exists!");
                return;
            }

            System.out.print("Enter Student Name: ");
            String name = sc.nextLine();

            if (name.trim().isEmpty()){
                throw new InvalidDataException(
                        "Student name cannot be empty.");
            }

            System.out.println("\nAvailable Branches:");
            System.out.println("1. CSE");
            System.out.println("2. CIVIL");
            System.out.println("3. ECE");
            System.out.println("4. MECH");
            System.out.println("5. IT");

            System.out.print("Enter Branch: ");
            int branchChoice = sc.nextInt();

            Branch branch;

            switch (branchChoice) {
            case 1:
                branch = Branch.CSE;
                break;
            case 2:
                branch = Branch.CIVIL;
                break;
            case 3:
                branch = Branch.ECE;
                break;
            case 4:
                branch = Branch.MECH;
                break;
            case 5:
                branch = Branch.IT;
                break;
            default:
                throw new InvalidDataException(
                        "Invalid branch choice.");
            }

            System.out.println("\nResidence Type:");
            System.out.println("1. HOSTELER");
            System.out.println("2. DAY_SCHOLAR");

            System.out.print("Enter Residence Type: ");
            int residenceChoice = sc.nextInt();

            Residence residence;

            if (residenceChoice == 1) {
                residence = Residence.HOSTELER;
            } else if (residenceChoice == 2) {
                residence = Residence.DAY_SCHOLAR;
            } else {
                throw new InvalidDataException(
                        "Invalid residence choice.");
            }

            System.out.print("Enter Classes Held: ");
            int classesHeld = sc.nextInt();

            System.out.print("Enter Classes Attended: ");
            int classesAttended = sc.nextInt();

            if (classesHeld < 0 || classesAttended < 0) {
                throw new InvalidDataException(
                        "Classes cannot be negative.");
            }

            if (classesAttended > classesHeld) {
                throw new InvalidDataException(
                        "Attended classes cannot be greater than held classes.");
            }

            System.out.print("Enter Academic Fee: ");
            double totalFee = sc.nextDouble();

            System.out.print("Enter Hostel Fee: ");
            double hostelFee = sc.nextDouble();

            System.out.print("Enter Bus Fee: ");
            double busFee = sc.nextDouble();

            System.out.print("Enter Fee Paid: ");
            double feePaid = sc.nextDouble();

            if (totalFee < 0 || hostelFee < 0 ||
                    busFee < 0 || feePaid < 0) {
                throw new InvalidDataException(
                        "Fee amount cannot be negative.");
            }

            Student student = new Student(
                    rollNumber,
                    name,
                    branch,
                    residence,
                    classesHeld,
                    classesAttended,
                    totalFee,
                    hostelFee,
                    busFee,
                    feePaid
            );

            studentMap.put(rollNumber, student);
            rollTracker.add(rollNumber);

            System.out.println("\nStudent added successfully!");

        }catch (InputMismatchException e){

            System.out.println(
                    "Invalid input. Please enter the correct data type.");
            sc.nextLine();

        }catch (InvalidDataException e){
            System.out.println(
                    "Error: " + e.getMessage());
        }
    }

    static void viewAllStudents() {
        System.out.println("\n ALL STUDENTS");

        if (studentMap.isEmpty()) {
            System.out.println("No students available.");
            return;
        }

        ArrayList<Student> students =
                new ArrayList<>(studentMap.values());
        for (Student student : students) {
            student.displayDetails();
        }
    }

    static void updateStudent(Scanner sc){
        System.out.println("\n UPDATE STUDENT");
        if (studentMap.isEmpty()) {
            System.out.println("No students available.");
            return;
        }

        System.out.print("Enter Roll Number: ");
        int rollNumber = sc.nextInt();
        Student student = studentMap.get(rollNumber);

        if (student == null){
            System.out.println("Student not found.");
            return;
        }

        System.out.println("\n1. Update Attendance");
        System.out.println("2. Add Semester Grade");

        System.out.print("Enter your choice: ");
        int choice = sc.nextInt();

        try{

            if (choice == 1){
                System.out.print("Enter Additional Classes Held: ");
                int held = sc.nextInt();

                System.out.print("Enter Additional Classes Attended: ");
                int attended = sc.nextInt();

                student.updateAttendance(held, attended);

                System.out.println(
                        "Attendance updated successfully.");

            }else if (choice == 2){

                System.out.print("Enter Year (1-4): ");
                int year = sc.nextInt();

                System.out.print("Enter Semester (1-2): ");
                int semester = sc.nextInt();

                System.out.print("Enter GPA (0-10): ");
                double gpa = sc.nextDouble();

                student.addSemesterGrade(
                        year,
                        semester,
                        gpa);

                System.out.println(
                        "Semester grade added successfully.");

            }else{
                System.out.println("Invalid choice.");
            }

        } catch (InvalidDataException e) {
            System.out.println(
                    "Error: " + e.getMessage());

        } catch (InputMismatchException e) {
            System.out.println("Invalid input.");
            sc.nextLine();
        }
    }

    static void deleteStudent(Scanner sc){

        System.out.println("\n DELETE STUDENT");

        if (studentMap.isEmpty()){
            System.out.println("No students available.");
            return;
        }

        System.out.print("Enter Roll Number: ");
        int rollNumber = sc.nextInt();

        Student student = studentMap.get(rollNumber);

        if (student == null){
            System.out.println("Student not found.");
            return;
        }

        studentMap.remove(rollNumber);
        rollTracker.remove(rollNumber);
        undoStack.push(student);

        System.out.println(
                "Student deleted successfully.");
        System.out.println(
                "Use option 5 to undo the deletion.");
    }

    static void undoDelete(){
        System.out.println("\n UNDO DELETE");

        if (undoStack.isEmpty()){
            System.out.println("Nothing to undo.");
            return;
        }

        Student student=undoStack.peek();
        studentMap.put(student.getRollNumber(),student);
        rollTracker.add(student.getRollNumber());undoStack.pop();
        System.out.println("Last deleted student restored successfully.");
    }

    static void saveToFile(){
        try{
            BufferedWriter writer=new BufferedWriter(new FileWriter(FILE_NAME));
            for (Student student : studentMap.values()){
                writer.write(student.toCSV());
                writer.newLine();
            }
            writer.close();
        }catch (IOException e){
            System.out.println(
                    "Error while saving data: "
                    + e.getMessage());
        }
    }

    static void loadFromFile(){
        File file = new File(FILE_NAME);
        if (!file.exists()){
            return;
        }
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length != 10) {
                    continue;
                }
                try {
                    int rollNumber =
                            Integer.parseInt(data[0]);
                    String name = data[1];
                    Branch branch =Branch.valueOf(data[2]);
                    Residence residence =Residence.valueOf(data[3]);
                    int classesHeld =Integer.parseInt(data[4]);
                    int classesAttended =Integer.parseInt(data[5]);
                    double totalFee =Double.parseDouble(data[6]);
                    double hostelFee =Double.parseDouble(data[7]);
                    double busFee =Double.parseDouble(data[8]);
                    double feePaid =Double.parseDouble(data[9]);
                    Student student = new Student(
                            rollNumber,
                            name,
                            branch,
                            residence,
                            classesHeld,
                            classesAttended,
                            totalFee,
                            hostelFee,
                            busFee,
                            feePaid
                    );

                    studentMap.put(
                            rollNumber,
                            student);

                    rollTracker.add(
                            rollNumber);

                }catch (Exception e){
                    System.out.println(
                            "Invalid record skipped.");
                }
            }

            reader.close();
        }catch (IOException e){

            System.out.println(
                    "Error while loading data: "
                    + e.getMessage());
        }
    }
}
