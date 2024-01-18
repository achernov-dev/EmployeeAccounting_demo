package org.empacc.app.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import org.empacc.app.model.Title;
import org.empacc.app.service.AppService;
import org.empacc.app.service.GridService;

import java.sql.SQLException;
import java.util.List;

public class TitleGridView extends VerticalLayout {
    final static String COLUMN_WIDTH = "42%";

    public TitleGridView(){
        ValidationMessage nameValidationMessage = new ValidationMessage();
        ValidationMessage salaryValidationMessage = new ValidationMessage();

        Grid<Title> grid = new Grid<>(Title.class, false);
        Editor<Title> editor = grid.getEditor();

        Binder<Title> binder = new Binder<>(Title.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        List<Title> titleList = AppService.getTitles();

        Grid.Column<Title> nameColumn = grid
                .addColumn(Title::getName).setHeader("Название")
                .setWidth(COLUMN_WIDTH).setFlexGrow(0)
                .setEditorComponent(GridService.getTitleNameEditor(binder, nameValidationMessage));

        Grid.Column<Title> salaryColumn = grid
                .addColumn(Title::getSalary).setHeader("Оклад")
                .setWidth(COLUMN_WIDTH).setFlexGrow(0)
                .setEditorComponent(GridService.getSalaryEditor(binder, salaryValidationMessage));


        Grid.Column<Title> editColumn = grid.addComponentColumn(Title -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(Title);
            });
            return editButton;
        }).setWidth("10%").setFlexGrow(0);

        Grid.Column<Title> delColumn = grid.addComponentColumn(Title -> {
            Button delButton = new Button("Delete");
            delButton.addClickListener(e -> {
                try {
                    titleList.remove(Title);
                    AppService.deleteEntity(Title.getId(), "Title");
                    grid.getDataProvider().refreshAll();
                } catch (ClassNotFoundException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            return delButton;
        }).setWidth("6%").setFlexGrow(0);

        Button newButton =  new Button("Добавить");
        newButton.addClickListener(e -> {
            Title newTitle = new Title();
            titleList.add(newTitle);
            grid.getDataProvider().refreshAll();
            grid.getEditor().editItem(newTitle);
            newButton.setVisible(false);
        });

        Button saveButton = new Button("Save", e -> {
            editor.save();
            grid.setItems(titleList);
            grid.getDataProvider().refreshAll();
            newButton.setVisible(true);
        });
        editor.addSaveListener(e ->{
            final Title title = e.getItem();
            try {
                AppService.saveTitle(title);
            } catch (ClassNotFoundException | SQLException ex) {
                throw new RuntimeException(ex);
            }
            editor.refresh();
        });
        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> {
                    editor.cancel();
                    Title lastTitle = titleList.get(titleList.size() - 1);
                    if(lastTitle.isNew()){
                        titleList.remove(titleList.size() - 1);
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
            salaryValidationMessage.setText("");
        });



        grid.setItems(titleList);
        grid.setHeight("80vh");
        getThemeList().clear();
        getThemeList().add("spacing-s");
        add(grid, nameValidationMessage, salaryValidationMessage, newButton);
    }
}
