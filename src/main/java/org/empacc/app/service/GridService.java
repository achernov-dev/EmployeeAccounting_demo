package org.empacc.app.service;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.empacc.app.model.Department;
import org.empacc.app.model.Employee;
import org.empacc.app.model.Title;
import org.empacc.app.view.ValidationMessage;


public class GridService {

    /*
        * Generates Department picklists for Employee Tab
     */
    public static Component generateDepartmentCombobox(Binder<Employee> binder, Employee employee){
        ComboBox<Department> comboBox = new ComboBox<>();
        comboBox.setItems(AppService.getDepartments());
        comboBox.setItemLabelGenerator(Department::getName);
        if(!employee.isNew()){
            comboBox.setValue( AppService.getDeparment(employee.getDeparmentId()) );
        }
        else{
            comboBox.setPlaceholder("Select department");
        }
        comboBox.setWidthFull();
        binder.forField(comboBox).bind(
                emp -> AppService.getDeparment(emp.getDeparmentId()),
                (emp, department) -> emp.setDeparmentId(department.getId()));

        return comboBox;
    }

    /*
     * Generates Title picklists for Employee Tab
     */
    public static Component generateTitleCombobox(Binder<Employee> binder, Employee employee) {
        ComboBox<Title> comboBox = new ComboBox<>();
        comboBox.setItems(AppService.getTitles());
        comboBox.setItemLabelGenerator(Title::getName);
        if(!employee.isNew()){
            comboBox.setValue( AppService.getTitle(employee.getTitleId()) );
        }
        comboBox.setWidthFull();
        binder.forField(comboBox).bind(
                emp -> AppService.getTitle(emp.getTitleId()),
                (emp, title) -> emp.setTitleId(title.getId()));
        return comboBox;
    }

    /*
     * Generates Employee picklists as Boss for Department Tab
     */
    public static Component generateEmployeeCombobox(Binder<Department> binder, Department department) {
        ComboBox<Employee> comboBox = new ComboBox<>();
        comboBox.setItems(AppService.getEmployees());
        comboBox.setItemLabelGenerator(Employee::getFullName);
        comboBox.setValue( AppService.getEmployee(department.getBossId()) );
        comboBox.setWidthFull();
        binder.forField(comboBox).bind(
                dep -> AppService.getEmployee(dep.getBossId()),
                (dep, emp) -> dep.setBossId(emp.getId()));
        return comboBox;
    }

    /*
     * Trivial input text field + validation
     */
    public static Component getFirstNameEditor(Binder<Employee> binder, ValidationMessage firstNameValidationMessage) {
        TextField firstNameField = new TextField();
        firstNameField.setWidthFull();
        binder.forField(firstNameField)
                .asRequired("First name must not be empty")
                .withStatusLabel(firstNameValidationMessage)
                .bind(Employee::getFirstName, Employee::setFirstName);
        return firstNameField;
    }

    /*
     * Trivial input text field + validation
     */
    public static Component getLastNameEditor(Binder<Employee> binder, ValidationMessage lastNameValidationMessage) {
        TextField lastNameField = new TextField();
        lastNameField.setWidthFull();
        binder.forField(lastNameField).asRequired("Last name must not be empty")
                .withStatusLabel(lastNameValidationMessage)
                .bind(Employee::getLastName, Employee::setLastName);
        return lastNameField;
    }

    /*
     * Trivial input text fields + validation
     */
    public static Component getDepartNameEditor(Binder<Department> binder, ValidationMessage nameValidationMessage) {
        TextField nameField = new TextField();
        nameField.setWidthFull();
        binder.forField(nameField).asRequired("Name must not be empty")
                .withStatusLabel(nameValidationMessage)
                .bind(Department::getName, Department::setName);
        return nameField;
    }

    public static Component getTitleNameEditor(Binder<Title> binder, ValidationMessage nameValidationMessage) {
        TextField nameField = new TextField();
        nameField.setWidthFull();
        binder.forField(nameField).asRequired("Nast name must not be empty")
                .withStatusLabel(nameValidationMessage)
                .bind(Title::getName, Title::setName);
        return nameField;
    }

    public static Component getPhoneEditor(Binder<Department> binder, ValidationMessage validationMessage) {
        TextField phoneField = new TextField();
        phoneField.setWidthFull();

        binder.forField(phoneField).bind(Department::getPhone, Department::setPhone);
        return phoneField;
    }

    public static Component getEmailEditor(Binder<Department> binder, ValidationMessage emailValidationMessage) {

        EmailField emailField = new EmailField();
        emailField.setWidthFull();
        binder.forField(emailField).asRequired("Email must not be empty")
                .withStatusLabel(emailValidationMessage)
                .bind(Department::getEmail, Department::setEmail);

        return emailField;
    }


    public static Component getSalaryEditor(Binder<Title> binder, ValidationMessage salaryValidationMessage) {
        NumberField dollarField = new NumberField();
        Div dollarPrefix = new Div();
        dollarPrefix.setText("$");
        dollarField.setPrefixComponent(dollarPrefix);
        binder.forField(dollarField).asRequired("Salary must not be empty")
                .withStatusLabel(salaryValidationMessage)
                .bind(Title::getSalary, Title::setSalary);
        return dollarField;
    }
}