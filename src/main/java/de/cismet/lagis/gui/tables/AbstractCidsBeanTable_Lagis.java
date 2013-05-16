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

import de.cismet.cids.dynamics.CidsBean;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author gbaatz
 */
public abstract class AbstractCidsBeanTable_Lagis extends JXTable {

    private JButton btnAdd;
    private JButton btnRemove;
    private JToggleButton tbtnSort;

    public AbstractCidsBeanTable_Lagis() {
        initComponents();
    }

    private void initComponents() {
        btnAdd = new javax.swing.JButton();
        btnAdd.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png")));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
    }

    abstract void btnAddActionPerformed(ActionEvent evt);

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnRemove() {
        return btnRemove;
    }

    public JToggleButton getTbtnSort() {
        return tbtnSort;
    }
}
