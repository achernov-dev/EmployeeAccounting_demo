package org.empacc.app.service;

import org.empacc.app.model.Department;
import org.empacc.app.model.Employee;
import org.empacc.app.model.Item;
import org.empacc.app.model.Title;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppService {
    private static Connection connection;
    private final static String LOGIN_DB = "SYSDBA";
    private final static String PASS_DB = "masterkey";
    public static boolean isTestRun = false;

    private AppService(){}

    private static Map<Integer, Title> titles = new HashMap<>();
    private static Map<Integer, Employee> employees = new HashMap<>();
    private static Map<Integer, Department> departments = new HashMap<>();

    public static List<Title> getTitles() {
        return new ArrayList<>(titles.values());
    }

    public static List<Employee> getEmployees() {
        return new ArrayList<>(employees.values());
    }

    public static List<Department> getDepartments() {
        return new ArrayList<>(departments.values());
    }

    public static Employee getEmployee(int id){
        return id > 0 ? employees.get(id) : new Employee();
    }
    public static Title getTitle(int id){
        return id <= 0 ? new Title() : titles.get(id);
    }
    public static Department getDeparment(int id){
        return id <= 0 ? new Department() : departments.get(id);
    }

    /*
        * Connection to firebird database locale file.
        * AppService.isTestRun flag is used to connect to test database file for unit tests.
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if(connection == null){
            String currentDir = System.getProperty("user.dir");
            String dbURL = !AppService.isTestRun ? "jdbc:firebirdsql://localhost:3050/" + currentDir + "\\DATABASE\\EABD.fdb"
                                                    : "jdbc:firebirdsql://localhost:3050/" + currentDir + "\\src\\test\\java\\org\\empacc\\app\\EABDtest.fdb";

            Class.forName("org.firebirdsql.jdbc.FBDriver");
            connection  = DriverManager.getConnection(dbURL, LOGIN_DB, PASS_DB);
        }
        return connection;
    }

    /*
        * Collect all existed data from firebird database when page is loaded.
     */
    public static void fetchAll() throws ClassNotFoundException, SQLException {
        initTitles();
        initEmployees();
        initDepartments();
    }

    private static void initEmployees() throws ClassNotFoundException, SQLException {
        employees = new HashMap<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT id, TRIM(FIRST_NAME)fn, TRIM(LAST_NAME)ln, DEPARTMENT_ID, TITLE_ID FROM EMPLOYEE")) {
            while(rs.next()){
                Employee emp = new Employee(
                        rs.getInt("id"),
                        rs.getString("fn"),
                        rs.getString("ln"),
                        rs.getInt("DEPARTMENT_ID"),
                        rs.getInt("TITLE_ID"));
                employees.put(emp.getId(), emp);

            }
        }
    }
    private static void initDepartments() throws ClassNotFoundException, SQLException {
        departments = new HashMap<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT ID, TRIM(NAME)nm, TRIM(PHONE)ph, TRIM(EMAIL)em, BOSS_ID FROM DEPARTMENT")) {
            while(rs.next()){
                Department dep = new Department(
                        rs.getInt("ID"),
                        rs.getString("nm"),
                        rs.getInt("BOSS_ID"),
                        String.valueOf( rs.getInt("ph") ),
                        rs.getString("em")
                );
                departments.put(dep.getId(), dep);
            }
        }
    }
    private static void initTitles() throws ClassNotFoundException, SQLException {
        titles = new HashMap<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT ID, TRIM(NAME)nm, SALARY FROM TITLE")) {
            while(rs.next()){
                Title title = new Title(
                        rs.getInt("ID"),
                        rs.getString("nm"),
                        rs.getDouble("SALARY"));
                titles.put(title.getId(), title);
            }
        }
    }

    /*
        * Method to create/update Employee entity to firebird DB.
     */
    public static void saveEmployee(Employee employee)  throws ClassNotFoundException, SQLException {

        String query = employee.isNew() ? "insert into Employee (FIRST_NAME, LAST_NAME, DEPARTMENT_ID, TITLE_ID) values (?, ?, ?, ?) RETURNING ID"
                                        : "update Employee set FIRST_NAME = ?, LAST_NAME = ?, DEPARTMENT_ID = ?, TITLE_ID = ? WHERE ID = ? RETURNING ID";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)){

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            setNullableId(stmt, 3, employee.getDeparmentId());
            setNullableId(stmt, 4, employee.getTitleId());
            if(!employee.isNew()){
                stmt.setInt(5, employee.getId());
            }

            stmt.execute();
            setNewId(employee, stmt);
        }
    }

    /*
        * Method to create/update Department entity to firebird DB.
     */
    public static void saveDepartment(Department department)  throws ClassNotFoundException, SQLException {
        String query = department.isNew() ? "insert into Department (NAME, PHONE, EMAIL, BOSS_ID) values (?, ?, ?, ?) RETURNING ID"
                : "update Department set NAME = ?, PHONE = ?, EMAIL = ?, BOSS_ID = ? WHERE ID = ? RETURNING ID";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)){

            stmt.setString(1, department.getName());
            stmt.setString(2, department.getPhone());
            stmt.setString(3, department.getEmail());
            setNullableId(stmt, 4, department.getBossId());
            if(!department.isNew()){
                stmt.setInt(5, department.getId());
            }

            stmt.execute();
            setNewId(department, stmt);
        }
    }

    /*
        * Method to create/update Title entity to firebird DB.
     */
    public static void saveTitle(Title title)  throws ClassNotFoundException, SQLException {
        String query = title.isNew() ? "insert into Title (NAME, SALARY) values (?, ?) RETURNING ID"
                : "update Title set NAME = ?, SALARY = ? WHERE ID = ? RETURNING ID";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)){

            stmt.setString(1, title.getName());
            stmt.setDouble(2, title.getSalary());

            if(!title.isNew()){
                stmt.setInt(3, title.getId());
            }

            stmt.execute();
            setNewId(title, stmt);

        }
    }
    public static void deleteEntity(int entityId, String ent)  throws ClassNotFoundException, SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            String query = String.format("delete from %s WHERE ID = %d", ent, entityId);
            stmt.execute(query);
        }
    }

    /*
        * If record is created or relatioship is empty, value is stored as 0.
        * This method is supposed to set NULL value for DB in such cases.
     */
    private static void setNullableId(PreparedStatement stmt, int param, int id) throws SQLException {
        if(id == 0){
            stmt.setNull(param, Types.INTEGER);
        }
        else{
            stmt.setInt(param, id);
        }
    }

    /*
        * Retrieving new id value from DB and assign it to newly created record.
     */
    private static void setNewId(Item item, PreparedStatement stmt) throws SQLException {
        ResultSet resultSet = stmt.getResultSet();
        while (item.isNew() && resultSet.next()){
            int newId = resultSet.getInt(1);
            item.setId(newId);
        }

    }
}
