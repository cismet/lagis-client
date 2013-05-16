/*
 * Copyright (C) 2013 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.lagis.models;

import de.cismet.cids.dynamics.CidsBean;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author gbaatz
 */
public abstract class CidsBeanTableModel_Lagis extends AbstractTableModel {

    private List<CidsBean> cidsBeans;
    private final String[] columnNames;
    private final Class[] columnClasses;
    private boolean isInEditMode = false;

    /**
     * Creates a new CidsBeanTableModel object.
     *
     * @param columnNames DOCUMENT ME!
     * @param columnClasses DOCUMENT ME!
     */
    protected CidsBeanTableModel_Lagis(final String[] columnNames, final Class[] columnClasses) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
    }
}
