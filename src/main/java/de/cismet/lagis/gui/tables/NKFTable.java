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
package de.cismet.lagis.gui.tables;

import de.cismet.cids.custom.beans.lagis.NutzungCustomBean;
import de.cismet.lagis.models.NKFTableModel;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 *
 * @author gbaatz
 */
public class NKFTable extends AbstractCidsBeanTable_Lagis {

    private final Logger LOG = org.apache.log4j.Logger.getLogger(NKFTable.class);

    @Override
    protected void addNewItem() {
        ((NKFTableModel) getModel()).addNutzung(NutzungCustomBean.createNew());
        LOG.info("New Nutzung added to Model");
    }

    @Override
    protected void removeItem(int row) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Selektierte Nutzung gefunden in Zeile: " + row + "selectedRow: "
                    + this.getSelectedRow());
        }
        //removes a Nutzung
        ((NKFTableModel) getModel()).removeCidsBean(row);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                NKFTable.this.clearSelection();
            }
        });

    }
}
