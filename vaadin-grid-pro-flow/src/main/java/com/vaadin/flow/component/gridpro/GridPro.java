package com.vaadin.flow.component.gridpro;

/*
 * #%L
 * Vaadin GridPro for Vaadin 10
 * %%
 * Copyright (C) 2017 - 2018 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file license.html distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the CVALv3 along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.grid.*;
import com.vaadin.flow.component.Synchronize;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.internal.JsonSerializer;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.provider.Query;

import elemental.json.JsonArray;
import elemental.json.JsonObject;

import java.util.*;


@Tag("vaadin-grid-pro")
@HtmlImport("frontend://bower_components/vaadin-grid-pro/src/vaadin-grid-pro.html")
@HtmlImport("frontend://bower_components/vaadin-grid-pro/src/vaadin-grid-pro-edit-column.html")
/**
 * Server-side component for the {@code <vaadin-grid-pro>} element.
 *
 * @author Vaadin Ltd
 *
 * @param <T>
 *            the grid bean type
 *
 */
public class GridPro<E> extends Grid<E> {

    private Map<String, Column<E>> idToColumnMap = new HashMap<>();

    /**
     * Instantiates a new CrudGrid for the supplied bean type.
     */
    public GridPro(Class<E> beanType) {
        super(beanType);
    }

    /**
     * Creates a new instance, with page size of 50.
     */
    public GridPro() {
        super();
        setup();
    }

    /**
     * Creates a new instance, with the specified page size.
     * <p>
     * The page size influences the {@link Query#getLimit()} sent by the client,
     * but it's up to the webcomponent to determine the actual query limit,
     * based on the height of the component and scroll position. Usually the
     * limit is 3 times the page size (e.g. 150 items with a page size of 50).
     *
     * @param pageSize
     *            the page size. Must be greater than zero.
     */
    public GridPro(int pageSize) {
        super(pageSize);
        setup();
    }

    private void setup() {
        addItemPropertyChangedListener(e -> {
            EditColumn<E> column = (EditColumn<E>) this.idToColumnMap.get(e.getPath());
            column.getHandler().accept(e.getItem(), e.getPath());
        });
    }

    /**
     * Server-side component for the {@code <vaadin-grid-edit-column>} element.
     *
     * <p>
     * Every added column sends data to the client side regardless of its
     * visibility state. Don't add a new column at all or use
     * {@link GridPro#removeColumn(Column)} to avoid sending extra data.
     * </p>
     *
     * @param <T>
     *            type of the underlying grid this column is compatible with
     */
    @Tag("vaadin-grid-pro-edit-column")
    public static class EditColumn<T> extends Column<T> {

        private SerializableBiConsumer<Object, String> handler;

        /**
         * Constructs a new Column for use inside a Grid.
         *
         * @param grid
         *            the grid this column is attached to
         * @param columnId
         *            unique identifier of this column
         * @param renderer
         *            the renderer to use in this column, must not be
         *            {@code null}
         */
        public EditColumn(GridPro<T> grid, String columnId, Renderer<T> renderer) {
            super(grid, columnId, renderer);
        }

        protected <C extends Column<T>> C setHandler(SerializableBiConsumer<Object, String> handler) {
            this.handler = handler;
            return (C) this;
        }

        protected SerializableBiConsumer<Object, String> getHandler() {
            return handler;
        }

        protected EditColumn<T> setEditorType(EditorType type) {
            getElement().setProperty("editorType", type == null ? "text" : type.getTypeName());
            return this;
        }

        @Synchronize("editor-type-changed")
        protected String getEditorType() {
            return getElement().getProperty("editorType", "text");
        }

        protected EditColumn<T> setOptions(List<String> options) {
            getElement().setPropertyJson("editorOptions", JsonSerializer.toJson(options));
            return this;
        }

        @Synchronize("editor-options-changed")
        protected List<String> getOptions() {
            return JsonSerializer.toObjects(String.class,  (JsonArray) getElement().getPropertyRaw("editorOptions"));
        }

        protected EditColumn<T> setAllowEnterRowChange(Boolean allowEnterRowChange) {
            getElement().setProperty("allowEnterRowChange", allowEnterRowChange);
            return this;
        }

        @Synchronize("allow-enter-row-change-changed")
        protected Boolean getAllowEnterRowChange() {
            return getElement().getProperty("allowEnterRowChange", false);
        }

        protected EditColumn<T> setPreserveEditMode(Boolean preserveEditMode) {
            getElement().setProperty("preserveEditMode", preserveEditMode);
            return this;
        }

        @Synchronize("preserve-edit-mode-changed")
        protected Boolean getPreserveEditMode() {
            return getElement().getProperty("preserveEditMode", false);
        }
    }

    /**
     * Adds a new edit column to this {@link GridPro} with a value provider. The
     * value is converted to String when sent to the client by using
     * {@link String#valueOf(Object)}.
     * <p>
     * Every added column sends data to the client side regardless of its
     * visibility state. Don't add a new column at all or use
     * {@link GridPro#removeColumn(Column)} to avoid sending extra data.
     * </p>
     *
     * @param valueProvider
     *            the value provider
     * @return the created column
     * @see #removeColumn(Column)
     */
    public EditColumn<E> addEditColumn(ValueProvider<E, ?> valueProvider, EditColumnConfigurator columnConfigurator) {
        Objects.requireNonNull(columnConfigurator);

        EditColumn<E> column = this.addColumn(valueProvider, this::createEditColumn);

        return configureEditColumn(column, columnConfigurator);
    }

    /**
     * Adds a new edit column to this {@link GridPro} with a renderer.
     * <p>
     * See implementations of the {@link Renderer} interface for built-in
     * renderer options with type safe APIs. For a renderer using template
     * binding, use {@link TemplateRenderer#of(String)}.
     * </p>
     * <p>
     * Every added column sends data to the client side regardless of its
     * visibility state. Don't add a new column at all or use
     * {@link GridPro#removeColumn(Column)} to avoid sending extra data.
     * </p>
     *
     * @param renderer
     *            the renderer used to create the grid cell structure
     * @return the created column
     *
     * @see TemplateRenderer#of(String)
     * @see #removeColumn(Column)
     */
    public EditColumn<E> addEditColumn(Renderer<E> renderer, EditColumnConfigurator columnConfigurator) {
        Objects.requireNonNull(columnConfigurator);

        EditColumn<E> column = this.addColumn(renderer, this::createEditColumn);

        return configureEditColumn(column, columnConfigurator);
    }

    /**
     * Adds a new edit column for the given property name. The property values are
     * converted to Strings in the grid cells. The property's full name will be
     * used as the {@link Column#setKey(String) column key} and the property
     * caption will be used as the {@link Column#setHeader(String) column
     * header}.
     * <p>
     * <strong>Note:</strong> This method can only be used for a Grid created
     * from a bean type with {@link #GridPro(Class)}.
     *
     * <p>
     * Every added column sends data to the client side regardless of its
     * visibility state. Don't add a new column at all or use
     * {@link GridPro#removeColumn(Column)} to avoid sending extra data.
     * </p>
     *
     * @see #removeColumn(Column)
     *
     * @param propertyName
     *            the property name of the new column, not <code>null</code>
     * @return the created column
     */
    public EditColumn<E> addEditColumn(String propertyName, EditColumnConfigurator columnConfigurator) {
        Objects.requireNonNull(columnConfigurator);

        EditColumn<E> column = this.addColumn(propertyName, this::createEditColumn);

        return configureEditColumn(column, columnConfigurator);
    }

    private EditColumn<E> configureEditColumn(EditColumn<E> column, EditColumnConfigurator columnConfigurator) {
        column.setEditorType(columnConfigurator.getType());
        column.setHandler(columnConfigurator.getHandler());
        column.setOptions(columnConfigurator.getOptions());

        if(columnConfigurator.getAllowEnterRowChange() != null) {
            column.setAllowEnterRowChange(columnConfigurator.getAllowEnterRowChange());
        }

        if(columnConfigurator.getPreserveEditMode() != null) {
            column.setPreserveEditMode(columnConfigurator.getPreserveEditMode());
        }

        return column;
    }

    protected EditColumn<E> createEditColumn(Renderer<E> renderer, String columnId) {
        EditColumn<E> column = new EditColumn<>(this, columnId, renderer);
        idToColumnMap.put(columnId, column);
        return column;
    }
    /**
     * Event fired when the user starts to edit an existing item.
     *
     * @param <E> the bean type
     */
    @DomEvent("item-property-changed")
    public static class ItemPropertyChangedEvent<E> extends ComponentEvent<GridPro<E>> {

        private E item;
        private String path;

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param fromClient <code>true</code> if the event originated from the client
         * @param item       the item to be edited, provided in JSON as internally represented in Grid
         * @param path       item subproperty that was changed
         */
        public ItemPropertyChangedEvent(GridPro<E> source, boolean fromClient,
                                        @EventData("event.detail.item") JsonObject item,
                                        @EventData("event.detail.path") String path) {
            super(source, fromClient);
            this.item = source.getDataCommunicator()
                    .getKeyMapper().get(item.getString("key"));
            this.path = path;
        }

        public E getItem() {
            return item;
        }

        public String getPath() {
            return path;
        }
    }

    /**
     * Registers a listener to be notified when the user starts to edit an existing item.
     *
     * @param listener a listener to be notified
     * @return a handle that can be used to unregister the listener
     */
    public Registration addItemPropertyChangedListener(ComponentEventListener<ItemPropertyChangedEvent<E>> listener) {
        return ComponentUtil.addListener(this, ItemPropertyChangedEvent.class,
                (ComponentEventListener) listener);
    }
}