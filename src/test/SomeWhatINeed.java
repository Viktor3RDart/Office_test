package test;


import lombok.SneakyThrows;

import java.sql.*;

public class SomeWhatINeed {
    // Вывод на экран всех сотрудников для локального тестирования
    public static void GiveEmployee() {
        try (Connection con = DriverManager.getConnection("jdbc:h2:.\\Office")) {
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("Select Employee.ID, Employee.Name,Department.Name" +
                    " as DepName from Employee join Department on Employee.DepartmentID=Department.ID");
            System.out.println("------------------------------------");
            while (rs.next()) {
                System.out.println(rs.getInt("ID") + "\t" + rs.getString("NAME") + "\t"
                        + rs.getString("DepName"));
            }
            System.out.println("------------------------------------");
        } catch (
                SQLException e) {
            System.out.println(e);
        }
    }

    // Запросить ID по наименованию департамента
    @SneakyThrows
    public static int GiveMeDepId(String DepName) {
        int num = 0;
        Connection con = DriverManager.getConnection("jdbc:h2:.\\Office");
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("Select Department.ID from Department where name ="
                + "'" + DepName + "'");
        while (rs.next()) {
            num = rs.getInt("ID");
        }
        return num;
    }

    @SneakyThrows
    public static void NewEmployee(int ID, String Name, int DepId) {
        Connection con = DriverManager.getConnection("jdbc:h2:.\\Office");
        PreparedStatement stm = con.prepareStatement("INSERT INTO Employee VALUES(?,?,?)");
        stm.setInt(1, ID);
        stm.setString(2, Name);
        stm.setInt(3, DepId);
        stm.executeUpdate();
    }

    @SneakyThrows
    public static void ResetBD() {
        Connection con = DriverManager.getConnection("jdbc:h2:.\\Office");
        Statement stm = con.createStatement();
        stm.executeUpdate("DROP TABLE Department IF EXISTS");
        stm.executeUpdate("CREATE TABLE Department(ID INT PRIMARY KEY, NAME VARCHAR(255))");
        stm.executeUpdate("INSERT INTO Department VALUES(1,'Accounting')");
        stm.executeUpdate("INSERT INTO Department VALUES(2,'IT')");
        stm.executeUpdate("INSERT INTO Department VALUES(3,'HR')");

        stm.executeUpdate("DROP TABLE Employee IF EXISTS");
        stm.executeUpdate("CREATE TABLE Employee(ID INT PRIMARY KEY, NAME VARCHAR(255), DepartmentID INT)");
        stm.executeUpdate("INSERT INTO Employee VALUES(1,'Pete',1)");
        stm.executeUpdate("INSERT INTO Employee VALUES(2,'Ann',1)");

        stm.executeUpdate("INSERT INTO Employee VALUES(3,'Liz',2)");
        stm.executeUpdate("INSERT INTO Employee VALUES(4,'Tom',2)");

        stm.executeUpdate("INSERT INTO Employee VALUES(5,'Todd',3)");
    }
}