package de.dataport.vaadin.views.locations;

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
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import de.dataport.vaadin.data.entity.LocationEntity;
import de.dataport.vaadin.data.service.LocationEntityService;
import de.dataport.vaadin.views.MainLayout;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Locations")
@Route(value = "locations/:locationLocationEntityID?/:action?(edit)", layout = MainLayout.class)
public class LocationsView extends Div implements BeforeEnterObserver {

    private final String LOCATIONENTITY_ID = "locationEntityID";
    private final String LOCATIONENTITY_EDIT_ROUTE_TEMPLATE = "locations/%s/edit";

    private final Grid<LocationEntity> grid = new Grid<>(LocationEntity.class, false);

    private TextField locationId;
    private TextField street;
    private TextField city;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<LocationEntity> binder;

    private LocationEntity locationEntity;

    private final LocationEntityService locationEntityService;

    public LocationsView(LocationEntityService locationEntityService) {
        this.locationEntityService = locationEntityService;
        addClassNames("locations-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("locationId").setAutoWidth(true);
        grid.addColumn("street").setAutoWidth(true);
        grid.addColumn("city").setAutoWidth(true);
        grid.setItems(query -> locationEntityService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(LOCATIONENTITY_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(LocationsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(LocationEntity.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.locationEntity == null) {
                    this.locationEntity = new LocationEntity();
                }
                binder.writeBean(this.locationEntity);
                locationEntityService.update(this.locationEntity);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(LocationsView.class);
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
        Optional<Long> locationEntityId = event.getRouteParameters().get(LOCATIONENTITY_ID).map(Long::parseLong);
        if (locationEntityId.isPresent()) {
            Optional<LocationEntity> locationEntityFromBackend = locationEntityService.get(locationEntityId.get());
            if (locationEntityFromBackend.isPresent()) {
                populateForm(locationEntityFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested locationEntity was not found, ID = %s", locationEntityId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(LocationsView.class);
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
        locationId = new TextField("Location Id");
        street = new TextField("Street");
        city = new TextField("City");
        formLayout.add(locationId, street, city);

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

    private void populateForm(LocationEntity value) {
        this.locationEntity = value;
        binder.readBean(this.locationEntity);

    }
}
