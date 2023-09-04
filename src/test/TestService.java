package test;
import junit.framework.Assert;
import lombok.SneakyThrows;
import office.Department;
import office.Employee;
import org.junit.jupiter.api.Test;
import java.sql.*;
import static office.Service.*;

// ! Покрытие модульными тестами всех методов класса Service, ответственных за исполнение требования.
public class TestService extends SomeWhatINeed {

    public TestService() throws SQLException {
    }

    //1. createDB modul_test
    @Test
    @SneakyThrows
    public void test_01_createDB() {
        // 1. Check - DROP SOME TABLE Department/Employee
        createDB();
        try {
            stm.executeUpdate("DROP TABLE Department IF EXISTS");
            ResultSet rs = stm.executeQuery("Select ID, NAME from  Department");
            ResultSetMetaData metaData = rs.getMetaData();
            if (metaData.getTableName(1).equals("DEPARTMENT")) {
                Assert.fail("Таблица не удалена");
            }
        } catch (SQLException e) {
            assert e.getMessage().equals("Таблица \"DEPARTMENT\" не найдена\n" +
                    "Table \"DEPARTMENT\" not found; SQL statement:\n" +
                    "Select ID, NAME from  Department [42102-200]") : "Таблица не удалена";
            // 2. Check - CREATE TABLE Department(ID INT PRIMARY KEY, NAME VARCHAR(255))
            createDB();
            ResultSet rs2 = stm.executeQuery("Select ID, NAME from  Department");
            ResultSetMetaData metaData2 = rs2.getMetaData();
            assert metaData2.getTableName(1).equals("DEPARTMENT") : "Наименование таблицы не совпадает Department != " + metaData2.getTableName(1);
            assert metaData2.getColumnName(1).equals("ID") : "Наименование столбца не совпадает ID != " + metaData2.getColumnName(1);
            assert metaData2.getColumnTypeName(1).equals("INTEGER") : "Тип столбца не совпадает INTEGER != " + metaData2.getColumnName(1);
            assert metaData2.getColumnName(2).equals("NAME") : "Наименование столбца не совпадает NAME != " + metaData2.getColumnName(2);
            assert metaData2.getColumnTypeName(2).equals("VARCHAR") : "Тип столбца не совпадает VARCHAR != " + metaData2.getColumnName(2);
            assert metaData2.getPrecision(2) == 255 : "Тип столбца не совпадает 255 != " + metaData2.getColumnName(2);
            //3. Check -  INSERT INTO Department/Employee VALUES
            ResultSet rs3 = stm.executeQuery("Select ID, NAME from  Department WHERE ID = 1");
            while (rs3.next()) {
                assert rs3.getString("NAME").equals("Accounting") : "INSERT INTO Department VALUES(1,'Accounting') не выполнен";
            }
            ResultSet rs4 = stm.executeQuery("Select ID, Name,DepartmentId from Employee WHERE ID = 1");
            while (rs4.next()) {
                assert rs4.getString("NAME").equals("Pete") : "INSERT INTO Employee VALUES(1,'Pete',1) не выполнен";
            }
        }
    }

    //2. addDepartment modul_test
    @Test
    @SneakyThrows
    public void test_02_addDepartment() {
        createDB();
        Department test = new Department(5, "TEST");
        addDepartment(test);
        ResultSet rs = stm.executeQuery("Select ID, NAME from  Department WHERE ID =" + test.getDepartmentID());
        // Проверяем заполнена ли таблица хоть одним значением, если с таким ID нет строк, то будет пустая таблица
        rs.first();
        int rowCount = rs.getRow();
        assert rowCount != 0 : "Добавление департамента не выполнено";
        // Проверяем соответствует ли созданное имя департамента заданному
        ResultSet rs2 = stm.executeQuery("Select ID, NAME from  Department WHERE ID =" + test.getDepartmentID());
        while (rs2.next()) {
            assert rs2.getString("NAME").equals(test.getName()) : "Название  департамента добавилось не верно верно - "
                    + test.getName() + " полученное - " + rs2.getString("NAME");
        }
        ResetBD();
        con.close();
    }

    //3. removeDepartment modul_test
    @Test
    @SneakyThrows
    public void test_03_removeDepartment() {
        createDB();
        Department test = new Department(2, "IT");
        removeDepartment(test);
        ResultSet rs = stm.executeQuery("Select ID, NAME from  Department WHERE ID =" + test.getDepartmentID());
        rs.first();
        int rowCount = rs.getRow();
        assert rowCount == 0 : "Удаление департамента не выполнено";
        ResetBD();
        con.close();
    }

    //4. addEmployee modul_test
    @Test
    @SneakyThrows
    public void test_04_addEmployee() {
        createDB();
        Employee test = new Employee(10, "Иннокентий", 1);
        addEmployee(test);
        ResultSet rs = stm.executeQuery("Select ID, NAME from  Employee WHERE ID =" + test.getEmployeeId());
        // Проверяем заполнена ли таблица хоть одним значением, если с таким ID нет строк, то будет пустая таблица
        rs.first();
        int rowCount = rs.getRow();
        assert rowCount != 0 : "Добавление Сотрудника не выполнено";
        // Проверяем соответствует ли созданное имя сотрудника заданному
        ResultSet rs2 = stm.executeQuery("Select ID, NAME from  Employee WHERE ID =" + test.getEmployeeId());
        while (rs2.next()) {
            assert rs2.getString("NAME").equals(test.getName()) : "Название  сотрудника добавилось не верно верно - "
                    + test.getName() + " полученное - " + rs2.getString("NAME");
        }
        ResetBD();
        con.close();
    }

    //5. removeEmployee modul_test
    @Test
    @SneakyThrows
    public void test_05_removeEmployee() {
        createDB();
        Employee test = new Employee(2, "Ann", 1);
        removeEmployee(test);
        ResultSet rs = stm.executeQuery("Select ID, NAME from  Employee WHERE ID =" + test.getEmployeeId());
        rs.first();
        int rowCount = rs.getRow();
        assert rowCount == 0 : "Удаление сотрудника не выполнено";
        ResetBD();
        con.close();
    }
}
