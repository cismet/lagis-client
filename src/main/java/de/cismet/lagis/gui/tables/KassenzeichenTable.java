/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import Sirius.navigator.connection.SessionManager;

import org.apache.log4j.Logger;

import java.sql.Timestamp;

import java.util.Collection;
import java.util.Date;

import de.cismet.cids.custom.beans.lagis.KassenzeichenCustomBean;

import de.cismet.lagis.gui.panels.KassenzeichenAddDialog;
import de.cismet.lagis.gui.panels.KassenzeichenAddDialogListener;

import de.cismet.lagis.models.KassenzeichenTableModel;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class KassenzeichenTable extends AbstractCidsBeanTable_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(KassenzeichenTable.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void addNewItem() {
        try {
            StaticSwingTools.showDialog(new KassenzeichenAddDialog(new KassenzeichenAddDialogListener() {

                        @Override
                        public void kassenzeichennummerAdded(final Integer kassenzeichennummer) {
                            addNewKassenzeichen(kassenzeichennummer);
                        }
                    }));
        } catch (Exception ex) {
            LOG.error("error creating bean for Kassenzeichen", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichennummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean addNewKassenzeichen(final Integer kassenzeichennummer) {
        if (kassenzeichennummer != null) {
            for (final KassenzeichenCustomBean kassenzeichenBean
                        : (Collection<KassenzeichenCustomBean>)((KassenzeichenTableModel)getModel()).getCidsBeans()) {
                if ((kassenzeichenBean != null)
                            && kassenzeichennummer.equals(kassenzeichenBean.getKassenzeichennummer())) {
                    return false;
                }
            }

            final KassenzeichenCustomBean tmp = KassenzeichenCustomBean.createNew();
            tmp.setKassenzeichennummer(kassenzeichennummer);
            tmp.setZugeordnet_am(new Timestamp(new Date().getTime()));
            tmp.setZugeordnet_von(SessionManager.getSession().getUser().getName());
            ((KassenzeichenTableModel)getModel()).addCidsBean(tmp);
            fireItemAdded();
            return true;
        }
        return false;
    }

    @Override
    protected void removeItem(final int modelRow) {
        ((KassenzeichenTableModel)getModel()).removeCidsBean(modelRow);
    }
}
