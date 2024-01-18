package org.empacc.app;

import org.empacc.app.model.Department;
import org.empacc.app.model.Employee;
import org.empacc.app.model.Title;
import org.empacc.app.service.AppService;
import org.junit.jupiter.api.*;


import java.sql.SQLException;
import java.sql.Statement;

import static org.empacc.app.service.AppService.getConnection;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppServiceTest {

    @BeforeAll
    public static void prepareTest() throws SQLException, ClassNotFoundException {
        AppService.isTestRun = true;

        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute("DELETE FROM Title;");
            stmt.execute("DELETE FROM Department;");
            stmt.execute("DELETE FROM Employee;");
        }

        AppService.saveTitle(new Title(
                0,
                "test Title",
                100d
        ));
        AppService.saveDepartment(new Department(
                0,
                "test dep",
                0,
                "1234",
                "test@test.test"
        ));
        AppService.saveEmployee(new Employee(
                0,
                "firstName",
                "lastName",
                0,
                0
        ));

    }

    @Test
    @Order(1)
    public void testExistedData() throws SQLException, ClassNotFoundException {
        System.out.println("===== 1");
        AppService.fetchAll();
        Assertions.assertEquals(1, AppService.getEmployees().size());
        Assertions.assertEquals(1, AppService.getDepartments().size());
        Assertions.assertEquals(1, AppService.getTitles().size());
        System.out.println("===== 2");
    }

    @Test
    @Order(2)
    public void testUpdateExistedData() throws SQLException, ClassNotFoundException {
        System.out.println("===== 3");
        Employee emp = AppService.getEmployees().get(0);
        emp.setDeparmentId( AppService.getDepartments().get(0).getId() );
        AppService.saveEmployee(emp);
        AppService.fetchAll();
        Assertions.assertTrue(AppService.getEmployee(emp.getId()).getDeparmentId() > 0);
        System.out.println("===== 4");
    }

    @Test
    @Order(3)
    public void testCreateEmployee() throws SQLException, ClassNotFoundException {
        System.out.println("===== 5");
        Employee employee = new Employee(
                0,
                "newTestF",
                "newTestL",
                AppService.getDepartments().get(0).getId(),
                AppService.getTitles().get(0).getId()
        );
        Assertions.assertTrue(employee.isNew());
        AppService.saveEmployee(employee);
        Assertions.assertFalse(employee.isNew());
        System.out.println("===== 6");
    }

    @Test
    @Order(4)
    public void testDeleteEmployee() throws SQLException, ClassNotFoundException {
        System.out.println("===== 7");
        Employee employee = AppService.getEmployees().get(0);
        AppService.deleteEntity(employee.getId(), "Employee");
        AppService.fetchAll();
        Assertions.assertTrue(AppService.getEmployees().isEmpty() || AppService.getEmployee(employee.getId()) == null);
        System.out.println("===== 8");
    }

}
