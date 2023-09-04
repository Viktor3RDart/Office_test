package test;

import lombok.SneakyThrows;
import office.Department;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static office.Service.*;


public class TestTasks extends SomeWhatINeed {

    public TestTasks() throws SQLException {
    }

    //1. Найдите ID сотрудника с именем Ann. Если такой сотрудник только один, то установите его департамент в HR.
    @Test
    @SneakyThrows
    public void test_01_aWhereAnn() {
        ResetBD();
        // Получить список сотрудников для инфо
        GiveEmployee();
        // Назначаем переменные
        String testName = "Ann";
        String testDep = "HR";
        // Ищем сотрудника
        ResultSet rs = stm.executeQuery("Select * from Employee where name =" + "'" + testName + "'");
        int count = 0;
        while (rs.next()) {
            assert rs.getString("NAME").equals(testName) : "Такого имени нет в таблице Employees";
            System.out.println("Результат поиска: ID - " + rs.getInt("ID") + "\t" + rs.getString("NAME"));
            count += 1;
        }
        // Проверяем на условие - Если такой сотрудник только один. Устанавливаем департамент искомого сотрудника - HR.
        if (count == 1) {
            PreparedStatement stm1 = con.prepareStatement("UPDATE Employee SET DepartmentID = " + GiveMeDepId(testDep)
                    + " where NAME = " + "'" + testName + "'");
            stm1.executeUpdate();
            // Проверяем выполнился ли UPDATE
            ResultSet rs2 = stm.executeQuery("Select Employee.ID, Employee.Name,Department.Name as DepName from " +
                    "Employee join Department on Employee.DepartmentID=Department.ID where Employee.Name =" + "'"
                    + testName + "'");
            while (rs2.next()) {
                assert rs2.getString("DepName").equals(testDep) : "Департамент не обновился";
                System.out.println("Результат изменения Департамента: " + rs2.getString("DepName") + "\t" + "ID - "
                        + rs2.getInt("ID") + "\t" + "ИМЯ - " + rs2.getString("NAME"));
            }
        }
        GiveEmployee();
        ResetBD();
        con.close();
    }

    // 2. Проверьте имена всех сотрудников. Если чьё-то имя написано с маленькой буквы, исправьте её на большую.
    // Выведите на экран количество исправленных имён.
    @Test
    @SneakyThrows
    public void test_02_bUpperCaseGo() {
        ResetBD();
        // Добавляем тестовые данные
        NewEmployee(6, "арина", 2);
        NewEmployee(7, "иван", 3);
        // Получить новый список сотрудников для инфо
        GiveEmployee();
        // Ищем сотрудников
        ResultSet rs = stm.executeQuery("Select * from Employee");
        int count = 0;
        // Находим чьё имя написано с маленькой буквы, перезаписываем
        while (rs.next()) {
            String name = rs.getString("NAME");
            if (name.charAt(0) != name.toUpperCase().charAt(0)) {
                PreparedStatement stm1 = con.prepareStatement("UPDATE Employee SET NAME = " + "'"
                        + name.toUpperCase().charAt(0) + name.substring(1) + "'" + " where NAME = "
                        + "'" + name + "'");
                stm1.executeUpdate();
                count += 1;
            }
        }
        // Выводим что получилось
        GiveEmployee();
        System.out.println("Количество измененных имен = " + count);
        ResetBD();
        con.close();
    }

    // 3. Выведите на экран количество сотрудников в IT-отделе
    @Test
    @SneakyThrows
    public void test_03_xCountOfEmp() {
        ResetBD();
        // Получить новый список сотрудников для инфо
        GiveEmployee();
        // Назначаем переменные
        String testDep = "IT";
        // Ищем сотрудников
        ResultSet rs = stm.executeQuery("Select * from Employee WHERE DepartmentID = " + GiveMeDepId(testDep));
        int count = 0;
        // Находим чьё имя написано с маленькой буквы, перезаписываем
        while (rs.next()) {
            count += 1;
        }
        System.out.println("Количество сотрудников работающих в " + testDep + " = " + count);
        con.close();
    }

    // 4. «При удалении отдела (Department) информация о всех сотрудниках, работающих в этом отделе, должна быть удалена».
    //
    //Проверьте, выполняется ли данное требование. Тестированию подлежит непосредственно jar-файл и приложенный
    // к нему файл БД. Выполните действия по порядку:
    //
    //Запустите приложение.
    //Удалите один из отделов.
    //Выполните проверку содержимого базы.
    @Test
    @SneakyThrows
    public void test_04_dDelDep() {
        ResetBD();
        // Получить новый список сотрудников для инфо
        GiveEmployee();
        // Назначаем переменные
        String testDep = "IT";
        Department dep = new Department(GiveMeDepId(testDep), "Delete");
        int count = 0;
        // Ищем сотрудников и собираем инфо для проверки
        Map<Integer, Integer> list = new HashMap<>();
        ResultSet rs = stm.executeQuery("Select * from Employee WHERE DepartmentID = " + GiveMeDepId(testDep));
        while (rs.next()) {
            list.put(count, rs.getInt("ID"));
            count++;
        }
        //Удаляем отдел
        removeDepartment(dep);
        // Проверяем удалились ли сотрудники из таблицы Employee
        ResultSet rs2 = stm.executeQuery("Select * from Employee");
        while (rs2.next()) {
            for (int i = 0; i < list.size(); i++) {
                if (rs2.getInt("ID") == list.get(i)) {
                    throw new AssertionError("Данный сотрудник не был удален, при удалении департамента," +
                            " ID сотрудника - " + list.get(i) + ", Имя - " + rs2.getString("NAME")
                            + ", ID департамента в таблице Employee - " + rs2.getString("DepartmentID"));
                }
            }
        }
        GiveEmployeeNoDep();
        ResetBD();
        con.close();
    }
}
