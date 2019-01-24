package com.vaadin.flow.component.gridpro.testbench;

/*
 * #%L
 * Vaadin GridPro Testbench API
 * %%
 * Copyright (C) 2018 Vaadin Ltd
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


import java.util.ArrayList;

/**
 * A TestBench element representing a <code>&lt;vaadin-pro-grid-edit-column&gt;</code>
 * element. This is not a TestBenchElement as polyfilled browsers are not
 * capable of finding it or handling it as a web element.
 */
public class GridProColumnElement {

    private GridProElement grid;
    private Long __generatedId;

    public GridProColumnElement(Long __generatedId, GridProElement grid) {
        this.grid = grid;
        this.__generatedId = __generatedId;
    }

    /**
     * For internal use only.
     *
     * @return the generated id for the column
     */
    protected Long get__generatedId() {
        return __generatedId;
    }

    /**
     * Gets the options List for this column.
     *
     * @return the options list
     */
    public ArrayList<String> getOptionsList() {
        ArrayList<String> editorOptions = (ArrayList<String>) execJs("return column.editorOptions");
        return editorOptions;
    }

    private Object execJs(String js) {
        return grid.getCommandExecutor()
                .executeScript("var grid = arguments[0];" //
                        + "var generatedId = arguments[1];"
                        + "var column = grid._getColumns().filter(function(column) {return column.__generatedTbId == generatedId;})[0];"
                        + js, grid, __generatedId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GridProColumnElement)) {
            return false;
        }

        return get__generatedId()
                .equals(((GridProColumnElement) obj).get__generatedId());
    }

}
