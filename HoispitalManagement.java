package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HoispitalManagement {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/hospital";

    private static final String userName = "root";
    private static final String password = "password@12345";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();

        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url,userName,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM " );
                System.out.println("1. Add Patient ");
                System.out.println("2. View Patients ");
                System.out.println("3. View Doctors ");
                System.out.println("4. Book Appointment ");
                System.out.println("5. Exit ");
                System.out.println("Enter your option : ");
                int choice = scanner.nextInt();
                switch (choice){
                    case 1 :
                        //Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2 :
                        //View Patients
                        patient.viewPatients();
                        System.out.println();
                        break;

                    case 3 :
                        //View Doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4 :
                        //Book Appointment
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 5 :
                        return;
                    default:
                        System.out.println("Enter valid choice : ");

                }

            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){
        System.out.println("Enter patient ID : ");
        int patientID = scanner.nextInt();
        System.out.println("Enter Doctor ID : ");
        int doctorID = scanner.nextInt();
        System.out.println("Enter the appointment date (YYYY-MM-DD) : ");
        String aptDate = scanner.next();
        if (patient.getPatientByID(patientID) && doctor.getDoctorByID(doctorID)){
            if (checkDoctorAvailablity(doctorID,aptDate,connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id,doctor_id,appointment_date) VALUES (?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientID);
                    preparedStatement.setInt(2,doctorID);
                    preparedStatement.setString(3,aptDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0){
                        System.out.println("Appointment Booked");
                    }else{
                        System.out.println("Not Booked");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("Doctor not available on this date...!");
            }
        }else{
            System.out.println("Either doctor or patient does'nt exist...!");
        }

    }

    private static boolean checkDoctorAvailablity(int doctorID, String aptDate,Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? ";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorID);
            preparedStatement.setString(2,aptDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                if (count == 0){
                    return true;
                }else{
                    return false;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;

    }


}
