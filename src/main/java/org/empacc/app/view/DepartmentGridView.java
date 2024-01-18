package org.empacc.app.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import org.empacc.app.model.Department;
import org.empacc.app.service.AppService;
import org.empacc.app.service.GridService;

import java.sql.SQLException;
import java.util.List;

public class DepartmentGridView extends VerticalLayout {
    final static String COLUMN_WIDTH = "21%";

    public DepartmentGridView(){
        ValidationMessage nameValidationMessage = new ValidationMessage();
        ValidationMessage phoneValidationMessage = new ValidationMessage();
        ValidationMessage emailValidationMessage = new ValidationMessage();

        Grid<Department> grid = new Grid<>(Department.class, false);
        Editor<Department> editor = grid.getEditor();

        Binder<Department> binder = new Binder<>(Department.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        List<Department> departmentList = AppService.getDepartments();

        Grid.Column<Department> nameColumn = grid
                .addColumn(Department::getName).setHeader("Название")
                .setWidth(COLUMN_WIDTH).setFlexGrow(0)
                .setEditorComponent(GridService.getDepartNameEditor(binder, nameValidationMessage));

        Grid.Column<Department> phoneColumn = grid
                .addColumn(Department::getPhone).setHeader("Телефон")
                .setWidth(COLUMN_WIDTH).setFlexGrow(0)
                .setEditorComponent(GridService.getPhoneEditor(binder, phoneValidationMessage));

        Grid.Column<Department> emailColumn = grid
                .addColumn(Department::getEmail).setHeader("Почта")
                .setWidth(COLUMN_WIDTH).setFlexGrow(0)
                .setEditorComponent(GridService.getEmailEditor(binder, emailValidationMessage));

        Grid.Column<Department> bossColumn = grid
                .addColumn(deparment -> AppService.getEmployee(deparment.getBossId()).getFullName())
                .setHeader("Начальник").setWidth(COLUMN_WIDTH).setFlexGrow(0)
                .setEditorComponent(deparment -> GridService.generateEmployeeCombobox(binder, deparment));

        Grid.Column<Department> editColumn = grid.addComponentColumn(Department -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(Department);
            });
            return editButton;
        }).setWidth("10%").setFlexGrow(0);

        Grid.Column<Department> delColumn = grid.addComponentColumn(Department -> {
            Button delButton = new Button("Delete");
            delButton.addClickListener(e -> {
                try {
                    departmentList.remove(Department);
                    AppService.deleteEntity(Department.getId(), "Department");
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

            Department newDepartment = new Department();
            departmentList.add(newDepartment);
            grid.getDataProvider().refreshAll();
            grid.getEditor().editItem(newDepartment);
            newButton.setVisible(false);
        });
        Button saveButton = new Button("Save", e -> {

            editor.save();
            grid.setItems(departmentList);
            grid.getDataProvider().refreshAll();
            newButton.setVisible(true);
        });
        editor.addSaveListener(e ->{
            final Department department = e.getItem();
            try {
                AppService.saveDepartment(department);
            } catch (ClassNotFoundException | SQLException ex) {
                throw new RuntimeException(ex);
            }
            editor.refresh();
        });
        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> {
                    editor.cancel();
                    Department lastDepartment = departmentList.get(departmentList.size() - 1);
                    if(lastDepartment.isNew()){
                        departmentList.remove(departmentList.size() - 1);
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
            nameValidationMessage.setText("");
            phoneValidationMessage.setText("");
            emailValidationMessage.setText("");
        });



        grid.setItems(departmentList);
        grid.setHeight("80vh");
        getThemeList().clear();
        getThemeList().add("spacing-s");
        add(grid, nameValidationMessage, phoneValidationMessage, emailValidationMessage, newButton);
    }
}
