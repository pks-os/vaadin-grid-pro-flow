package com.vaadin.flow.component.gridpro;

import com.vaadin.flow.function.SerializableBiConsumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

public class GridProEditColumnTest {

    GridPro<String> grid;
    GridPro.EditColumn<String> textColumn;
    GridPro.EditColumn<String> checkboxColumn;
    GridPro.EditColumn<String> selectColumn;
    List<String> listOptions;
    SerializableBiConsumer testConsumer;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() {
        testConsumer = (obj, str) -> {
            Assert.assertNotNull(obj);
            Assert.assertNotNull(str);
        };

        grid = new GridPro<>();
        textColumn = grid.addEditColumn(str -> str, EditColumnConfigurator.text(testConsumer));
        checkboxColumn = grid.addEditColumn(str -> str, EditColumnConfigurator.checkbox(testConsumer));

        listOptions = new ArrayList<String>();
        listOptions.add("foo");
        listOptions.add("bar");
        listOptions.add("baz");
        selectColumn = grid.addEditColumn(str -> str, EditColumnConfigurator.select(testConsumer, listOptions));
    }

    @Test
    public void setKey_getByKey() {
        // Grid columns API should be available from GridPro EditColumn
        textColumn.setKey("foo");
        checkboxColumn.setKey("bar");
        Assert.assertEquals(textColumn, grid.getColumnByKey("foo"));
        Assert.assertEquals(checkboxColumn, grid.getColumnByKey("bar"));
    }

    @Test
    public void removeColumnByKey() {
        textColumn.setKey("first");
        grid.removeColumnByKey("first");
        Assert.assertNull(grid.getColumnByKey("first"));
    }

    @Test
    public void removeColumn() {
        textColumn.setKey("first");
        grid.removeColumn(textColumn);
        Assert.assertNull(grid.getColumnByKey("first"));
    }

    @Test
    public void setHandler_getHandler() {
        SerializableBiConsumer testConsumer = (obj, str) -> {};
        checkboxColumn.setHandler(testConsumer);
        Assert.assertEquals(checkboxColumn.getHandler(), testConsumer);
    }

    @Test
    public void setEditorType_getEditorType() {
        EditorType editorType = EditorType.BOOLEAN;
        textColumn.setEditorType(editorType);
        Assert.assertEquals(textColumn.getEditorType(), editorType.getTypeName());
    }

    @Test
    public void setOptions_getOptions() {
        selectColumn.setOptions(listOptions);
        Assert.assertEquals(selectColumn.getOptions(), listOptions);
    }

    @Test
    public void setAllowEnterRowChange_getAllowEnterRowChange() {
        textColumn.setAllowEnterRowChange(true);
        Assert.assertEquals(selectColumn.getAllowEnterRowChange(), true);
    }

    @Test
    public void setPreserveEditMode_getPreserveEditMode() {
        textColumn.setPreserveEditMode(true);
        Assert.assertEquals(selectColumn.getPreserveEditMode(), true);
    }

    @Test
    public void addColumn_changeEditorType() {
        GridPro<Person> grid = new GridPro<>();

        GridPro.EditColumn<Person> nameColumn = grid.addEditColumn(Person::getName, EditColumnConfigurator.text((obj, str) -> {
        }));
        nameColumn.setEditorType(EditorType.BOOLEAN);
        Assert.assertEquals(nameColumn.getEditorType(), EditorType.BOOLEAN.getTypeName());

        nameColumn.setEditorType(EditorType.SELECT);
        Assert.assertEquals(nameColumn.getEditorType(), EditorType.SELECT.getTypeName());

        nameColumn.setEditorType(EditorType.TEXT);
        Assert.assertEquals(nameColumn.getEditorType(), EditorType.TEXT.getTypeName());
    }

    @Test
    public void addEditColumn_returnsNonNullAndEditColumnType() {
        GridPro.EditColumn column = new GridPro<Person>().addEditColumn(str -> str, EditColumnConfigurator.text((obj, str) -> {
        }));
        Assert.assertNotNull(column);
        Assert.assertEquals(GridPro.EditColumn.class, column.getClass());
    }
}