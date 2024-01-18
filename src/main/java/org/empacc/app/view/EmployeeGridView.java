package org.empacc.app.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import org.empacc.app.model.Employee;
import org.empacc.app.service.AppService;
import org.empacc.app.service.GridService;

import java.sql.SQLException;
import java.util.List;

public class EmployeeGridView extends VerticalLayout {
    final static String COLUMN_WIDTH = "21%";

    public EmployeeGridView() {
        ValidationMessage firstNameValidationMessage = new ValidationMessage();
        ValidationMessage lastNameValidationMessage = new ValidationMessage();

        Grid<Employee> grid = new Grid<>(Employee.class, false);
        Editor<Employee> editor = grid.getEditor();

        Binder<Employee> binder = new Binder<>(Employee.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        List<Employee> employeeList = AppService.getEmployees();


        Grid.Column<Employee> firstNameColumn = grid
                .addColumn(Employee::getFirstName).setHeader("Имя")
                .setWidth(COLUMN_WIDTH).setFlexGrow(0)
                .setEditorComponent(GridService.getFirstNameEditor(binder, firstNameValidationMessage));

        Grid.Column<Employee> lastNameColumn = grid.addColumn(Employee::getLastName)
                .setHeader("Фамилия").setWidth(COLUMN_WIDTH).setFlexGrow(0)
                .setEditorComponent(GridService.getLastNameEditor(binder, lastNameValidationMessage));

        Grid.Column<Employee> departmentColumn = grid.addColumn(employee -> AppService.getDeparment(employee.getDeparmentId()).getName())
                .setHeader("Отдел").setWidth(COLUMN_WIDTH).setFlexGrow(0)
                .setEditorComponent(employee -> GridService.generateDepartmentCombobox(binder, employee));

        Grid.Column<Employee> titleColumn = grid.addColumn(employee -> AppService.getTitle(employee.getTitleId()).getName())
                .setHeader("Должность").setWidth(COLUMN_WIDTH).setFlexGrow(0)
                .setEditorComponent(employee -> GridService.generateTitleCombobox(binder, employee));

        Grid.Column<Employee> editColumn = grid.addComponentColumn(Employee -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(Employee);
            });
            return editButton;
        }).setWidth("10%").setFlexGrow(0);

        Grid.Column<Employee> delColumn = grid.addComponentColumn(Employee -> {
            Button delButton = new Button("Delete");
            delButton.addClickListener(e -> {
                try {
                    employeeList.remove(Employee);
                    AppService.deleteEntity(Employee.getId(), "Employee");
                    grid.getDataProvider().refreshAll();
                } catch (ClassNotFoundException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            return delButton;
        }).setWidth("6%").setFlexGrow(0);

        Button newButton =  new Button("Добавить");
        newButton.addClickListener(e -> {
           //addElement();

            Employee newEmployee = new Employee();
            employeeList.add(newEmployee);
            grid.getDataProvider().refreshAll();
            grid.getEditor().editItem(newEmployee);
            newButton.setVisible(false);
        });


        Button saveButton = new Button("Save", e -> {
            editor.save();
            newButton.setVisible(true);

        });
        editor.addSaveListener(e ->{
           final Employee employee = e.getItem();
            try {
                AppService.saveEmployee(employee);
            } catch (ClassNotFoundException | SQLException ex) {
                throw new RuntimeException(ex);
            }
            editor.refresh();
        });
        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> {
                    editor.cancel();
                    Employee lastEmployee = employeeList.get(employeeList.size() - 1);
                    if(lastEmployee.isNew()){
                        employeeList.remove(employeeList.size() - 1);
                        grid.getDataProvider().refreshAll();
                    }
                });
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        editor.addCancelListener(e -> {
            newButton.setVisible(true);
            firstNameValidationMessage.setText("");
            lastNameValidationMessage.setText("");
        });


        grid.setItems(employeeList);
        grid.setHeight("80vh");
        getThemeList().clear();
        getThemeList().add("spacing-s");

        add(grid, firstNameValidationMessage, lastNameValidationMessage, newButton);
    }

}