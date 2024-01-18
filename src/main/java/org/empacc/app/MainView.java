package org.empacc.app;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import org.empacc.app.service.AppService;
import org.empacc.app.view.DepartmentGridView;
import org.empacc.app.view.EmployeeGridView;
import org.empacc.app.view.TitleGridView;

import java.sql.SQLException;


@Route("")
@PageTitle("Employee Accounting")
@PWA(name = "Employee Accounting", shortName = "Employee Accounting", enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends Div {
    private final Tab employeeTab;
    private final Tab titleTab;
    private final Tab departmentTab;
    private final VerticalLayout content;

    private final static String EMPLOYEE_TAB = "Сотрудники";
    private final static String DEPARTMENT_TAB = "Отделы";
    private final static String TITLE_TAB = "Должности";
    public MainView() throws SQLException, ClassNotFoundException {
        AppService.fetchAll();
        employeeTab = new Tab(EMPLOYEE_TAB);
        departmentTab = new Tab(DEPARTMENT_TAB);
        titleTab = new Tab(TITLE_TAB);

        Tabs tabs = new Tabs(employeeTab, departmentTab, titleTab);
        tabs.addSelectedChangeListener(event ->
                setContent(event.getSelectedTab())
        );
        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);

        content = new VerticalLayout();
        content.setSpacing(false);

        setContent(tabs.getSelectedTab());

        add(tabs, content);
    }
    private void setContent(Tab tab) {
        content.removeAll();

        if (tab.equals(employeeTab)) {
            content.add(new EmployeeGridView());
        } else if (tab.equals(departmentTab)) {
            content.add(new DepartmentGridView());
        } else {
            content.add(new TitleGridView());
        }
    }
}
