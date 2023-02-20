package de.dataport.vaadin.views.employees;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import de.dataport.vaadin.data.entity.EmployeeEntity;
import de.dataport.vaadin.data.service.EmployeeEntityService;
import de.dataport.vaadin.views.MainLayout;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Employees")
@Route(value = "employees/:employeeEmployeeEntityID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class EmployeesView extends Div implements BeforeEnterObserver {

    private final String EMPLOYEEENTITY_ID = "employeeEntityID";
    private final String EMPLOYEEENTITY_EDIT_ROUTE_TEMPLATE = "employees/%s/edit";

    private final Grid<EmployeeEntity> grid = new Grid<>(EmployeeEntity.class, false);

    private TextField surname;
    private TextField name;
    private TextField locationId;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<EmployeeEntity> binder;

    private EmployeeEntity employeeEntity;

    private final EmployeeEntityService employeeEntityService;

    public EmployeesView(EmployeeEntityService employeeEntityService) {
        this.employeeEntityService = employeeEntityService;
        addClassNames("employees-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("surname").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("locationId").setAutoWidth(true);
        grid.setItems(query -> employeeEntityService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(EMPLOYEEENTITY_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(EmployeesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(EmployeeEntity.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.employeeEntity == null) {
                    this.employeeEntity = new EmployeeEntity();
                }
                binder.writeBean(this.employeeEntity);
                employeeEntityService.update(this.employeeEntity);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(EmployeesView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> employeeEntityId = event.getRouteParameters().get(EMPLOYEEENTITY_ID).map(Long::parseLong);
        if (employeeEntityId.isPresent()) {
            Optional<EmployeeEntity> employeeEntityFromBackend = employeeEntityService.get(employeeEntityId.get());
            if (employeeEntityFromBackend.isPresent()) {
                populateForm(employeeEntityFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested employeeEntity was not found, ID = %s", employeeEntityId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(EmployeesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        surname = new TextField("Surname");
        name = new TextField("Name");
        locationId = new TextField("Location Id");
        formLayout.add(surname, name, locationId);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(EmployeeEntity value) {
        this.employeeEntity = value;
        binder.readBean(this.employeeEntity);

    }
}