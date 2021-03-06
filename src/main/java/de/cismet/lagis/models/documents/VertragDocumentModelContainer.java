/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * VertragDocumentModelContainer.java
 *
 * Created on 27. April 2007, 10:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models.documents;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.text.DateFormat;
import java.text.DecimalFormat;

import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import de.cismet.cids.custom.beans.lagis.BeschlussCustomBean;
import de.cismet.cids.custom.beans.lagis.KostenCustomBean;
import de.cismet.cids.custom.beans.lagis.VertragCustomBean;
import de.cismet.cids.custom.beans.lagis.VertragsartCustomBean;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.models.BeschluesseTableModel;
import de.cismet.lagis.models.KostenTableModel;
import de.cismet.lagis.models.VertraegeTableModel;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class VertragDocumentModelContainer implements MouseListener, ActionListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(VertragDocumentModelContainer.class);

    //~ Instance fields --------------------------------------------------------

    private final DecimalFormat df = LagisBroker.getCurrencyFormatter();
    private final DateFormat dateFormatter = LagisBroker.getDateFormatter();
    private VertragCustomBean currentSelectedVertrag = null;
    private VertraegeTableModel vertraegeTableModel;
    private AmountDocumentModel kaufpreisDocumentModel;
    private AmountDocumentModel quadPreisDocumentModel;
    private SimpleDocumentModel voreigentuemerDocumentModel;
    private SimpleDocumentModel aktenzeichenDocumentModel;
    private DateDocumentModel auflassungDocumentModel;
    private DateDocumentModel eintragungDocumentModel;
    private SimpleDocumentModel bemerkungDocumentModel;
    private DefaultComboBoxModel vertragsartComboBoxModel;
    private KostenTableModel kostenTableModel;
    private BeschluesseTableModel beschluesseTableModel;

    // private Simple

    // private

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of VertragDocumentModelContainer.
     *
     * @param  vertraegeTableModel  DOCUMENT ME!
     */
    public VertragDocumentModelContainer(final VertraegeTableModel vertraegeTableModel) {
        this.vertraegeTableModel = vertraegeTableModel;
        initDocumentModels();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void initDocumentModels() {
        kaufpreisDocumentModel = new AmountDocumentModel() {

                @Override
                public void assignValue(final Double betrag) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("amount assinged");
                    }
                    if (currentSelectedVertrag != null) {
                        currentSelectedVertrag.setGesamtpreis(betrag);
                        vertraegeTableModel.fireTableDataChangedAndKeepSelection();
                    }
                }
            };

        quadPreisDocumentModel = new AmountDocumentModel() {

                @Override
                public void assignValue(final Double betrag) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("amount assinged");
                    }
                    if (currentSelectedVertrag != null) {
                        currentSelectedVertrag.setQuadratmeterpreis(betrag);
                        vertraegeTableModel.fireTableDataChangedAndKeepSelection();
                    }
                }
            };

        auflassungDocumentModel = new DateDocumentModel() {

                @Override
                public void assignValue(final Date date) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Date assinged");
                    }
                    if (currentSelectedVertrag != null) {
                        currentSelectedVertrag.setDatumAuflassung(date);
                    }
                }
            };

        eintragungDocumentModel = new DateDocumentModel() {

                @Override
                public void assignValue(final Date date) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Date assinged");
                    }
                    if (currentSelectedVertrag != null) {
                        currentSelectedVertrag.setDatumEintragung(date);
                    }
                }
            };

        voreigentuemerDocumentModel = new SimpleDocumentModel() {

                @Override
                public void assignValue(final String newValue) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("voreigentuemer assigned");
                        LOG.debug("new Value: " + newValue);
                    }
                    valueToCheck = newValue;
                    fireValidationStateChanged(this);
                    if ((currentSelectedVertrag != null) && (getStatus() == VALID)) {
                        currentSelectedVertrag.setVertragspartner(newValue);
                    }
                }
            };

        aktenzeichenDocumentModel = new SimpleDocumentModel() {

                @Override
                public void assignValue(final String newValue) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("aktenzeichen assigned");
                        LOG.debug("new Value: " + newValue);
                    }
                    valueToCheck = newValue;
                    fireValidationStateChanged(this);
                    if ((currentSelectedVertrag != null) && (getStatus() == VALID)) {
                        currentSelectedVertrag.setAktenzeichen(newValue);
                        vertraegeTableModel.fireTableDataChangedAndKeepSelection();
                    }
                }
            };

        bemerkungDocumentModel = new SimpleDocumentModel() {

                @Override
                public void assignValue(final String newValue) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Bemerkung assigned");
                        LOG.debug("new Value: " + newValue);
                    }
                    valueToCheck = newValue;
                    fireValidationStateChanged(this);
                    if ((currentSelectedVertrag != null) && (getStatus() == VALID)) {
                        currentSelectedVertrag.setBemerkung(newValue);
                    }
                }
            };

        final Collection vertragsarten = LagisBroker.getInstance().getAllVertragsarten();
        if (vertragsarten != null) {
            vertragsartComboBoxModel = new DefaultComboBoxModel(new Vector(vertragsarten));
        } else {
            vertragsartComboBoxModel = new DefaultComboBoxModel();
        }
        beschluesseTableModel = new BeschluesseTableModel();
        kostenTableModel = new KostenTableModel();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (currentSelectedVertrag != null) {
            currentSelectedVertrag.setVertragsart((VertragsartCustomBean)vertragsartComboBoxModel.getSelectedItem());
            vertraegeTableModel.fireTableDataChangedAndKeepSelection();
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        final Object source = e.getSource();
        if (LOG.isDebugEnabled()) {
            LOG.debug("source: " + source);
        }
        if (source instanceof JXTable) {
            final JXTable table = (JXTable)source;
            final int currentRow = table.getSelectedRow();
            selectRow(currentRow);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  row  DOCUMENT ME!
     */
    public void selectRow(final int row) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Row: " + row);
        }
        if (row != -1) {
            final JXTable table = (JXTable)vertraegeTableModel.getTable();
            final int currentColumn = table.getSelectedColumn();
            final int modelRow = table.convertRowIndexToModel(row);
            currentSelectedVertrag = vertraegeTableModel.getCidsBeanAtRow(modelRow);
            try {
                kaufpreisDocumentModel.clear(0, kaufpreisDocumentModel.getLength());
                if (currentSelectedVertrag.getGesamtpreis() != null) {
                    kaufpreisDocumentModel.insertString(
                        0,
                        df.format(currentSelectedVertrag.getGesamtpreis()),
                        null);
                } else {
                    kaufpreisDocumentModel.insertString(0, "", null);
                }

                auflassungDocumentModel.clear(0, auflassungDocumentModel.getLength());
                if (currentSelectedVertrag.getDatumAuflassung() != null) {
                    auflassungDocumentModel.insertString(
                        0,
                        dateFormatter.format(currentSelectedVertrag.getDatumAuflassung()),
                        null);
                } else {
                    auflassungDocumentModel.insertString(0, "", null);
                }

                eintragungDocumentModel.clear(0, eintragungDocumentModel.getLength());
                if (currentSelectedVertrag.getDatumEintragung() != null) {
                    eintragungDocumentModel.insertString(
                        0,
                        dateFormatter.format(currentSelectedVertrag.getDatumEintragung()),
                        null);
                } else {
                    eintragungDocumentModel.insertString(0, "", null);
                }

                voreigentuemerDocumentModel.clear(0, voreigentuemerDocumentModel.getLength());
                voreigentuemerDocumentModel.insertString(0, currentSelectedVertrag.getVertragspartner(), null);

                aktenzeichenDocumentModel.clear(0, aktenzeichenDocumentModel.getLength());
                aktenzeichenDocumentModel.insertString(0, currentSelectedVertrag.getAktenzeichen(), null);

                bemerkungDocumentModel.clear(0, bemerkungDocumentModel.getLength());
                bemerkungDocumentModel.insertString(0, currentSelectedVertrag.getBemerkung(), null);

                quadPreisDocumentModel.clear(0, quadPreisDocumentModel.getLength());
                if (currentSelectedVertrag.getQuadratmeterpreis() != null) {
                    quadPreisDocumentModel.insertString(
                        0,
                        df.format(currentSelectedVertrag.getQuadratmeterpreis()),
                        null);
                } else {
                    quadPreisDocumentModel.insertString(0, "", null);
                }

                vertragsartComboBoxModel.setSelectedItem(currentSelectedVertrag.getVertragsart());

                kostenTableModel.refreshTableModel(currentSelectedVertrag.getKosten());
                beschluesseTableModel.refreshTableModel(currentSelectedVertrag.getBeschluesse());
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            table.changeSelection(table.convertRowIndexToView(modelRow),
                                currentColumn,
                                false,
                                false);
                        }
                    });
            } catch (final BadLocationException ex) {
                LOG.error(ex, ex);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("nichts selektiert lösche Felder");
            }
            currentSelectedVertrag = null;
            clearComponents();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VertragCustomBean getCurrentSelectedVertrag() {
        return currentSelectedVertrag;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AmountDocumentModel getKaufpreisDocumentModel() {
        return kaufpreisDocumentModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vertraegeTableModel  DOCUMENT ME!
     */
    public void updateTableModel(final VertraegeTableModel vertraegeTableModel) {
        this.vertraegeTableModel = vertraegeTableModel;
        if (vertraegeTableModel.getRowCount() > 0) {
            selectRow(0);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void clearComponents() {
        try {
            kaufpreisDocumentModel.clear(0, kaufpreisDocumentModel.getLength());
            quadPreisDocumentModel.clear(0, quadPreisDocumentModel.getLength());
            voreigentuemerDocumentModel.clear(0, voreigentuemerDocumentModel.getLength());
            aktenzeichenDocumentModel.clear(0, aktenzeichenDocumentModel.getLength());
            auflassungDocumentModel.clear(0, auflassungDocumentModel.getLength());
            eintragungDocumentModel.clear(0, eintragungDocumentModel.getLength());
            bemerkungDocumentModel.clear(0, bemerkungDocumentModel.getLength());
            kostenTableModel.refreshTableModel(null);
            beschluesseTableModel.refreshTableModel(null);
        } catch (Exception ex) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DateDocumentModel getAuflassungDocumentModel() {
        return auflassungDocumentModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DateDocumentModel getEintragungDocumentModel() {
        return eintragungDocumentModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getVoreigentuemerDocumentModel() {
        return voreigentuemerDocumentModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getAktenzeichenDocumentModel() {
        return aktenzeichenDocumentModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getBemerkungDocumentModel() {
        return bemerkungDocumentModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AmountDocumentModel getQuadPreisDocumentModel() {
        return quadPreisDocumentModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DefaultComboBoxModel getVertragsartComboBoxModel() {
        return vertragsartComboBoxModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public KostenTableModel getKostenTableModel() {
        return kostenTableModel;
    }

    /**
     * DOCUMENT ME!
     */
    public void addNewBeschluss() {
        if (currentSelectedVertrag != null) {
            try {
                final BeschlussCustomBean beschlussBean = BeschlussCustomBean.createNew();
                beschluesseTableModel.addCidsBean(beschlussBean);
                currentSelectedVertrag.getBeschluesse().add(beschlussBean);
                beschluesseTableModel.fireTableDataChanged();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Neuer Beschluss angelegt");
                }
            } catch (Exception ex) {
                LOG.error("error creating beschluss bean", ex);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es konnte kein Beschluss angelegt werden --> currentSelected Vertrag = null");
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void addNewKosten() {
        if (currentSelectedVertrag != null) {
            final KostenCustomBean kostenBean = KostenCustomBean.createNew();
            kostenTableModel.addCidsBean(kostenBean);
            currentSelectedVertrag.getKosten().add(kostenBean);
            kostenTableModel.fireTableDataChanged();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Neue Kosten angelegt");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es konnten keine neue Kosten angelegt werden --> currentSelected Vertrag = null");
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void addNewVertrag() {
        final VertragCustomBean newVertrag = VertragCustomBean.createNew();
        // set a Vertragsart as default
        newVertrag.setVertragsart((VertragsartCustomBean)vertragsartComboBoxModel.getElementAt(0));
        vertraegeTableModel.addCidsBean(newVertrag);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kostenIndex  DOCUMENT ME!
     */
    public void removeKosten(final int kostenIndex) {
        if ((currentSelectedVertrag != null) || (kostenIndex == -1)) {
            final KostenCustomBean kostenBean = kostenTableModel.getCidsBeanAtRow(kostenIndex);
            kostenTableModel.removeCidsBean(kostenIndex);
            currentSelectedVertrag.getKosten().remove(kostenBean);
            kostenTableModel.fireTableDataChanged();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Kosten wurden entfernt");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Kosten konnten nicht entfernt werden --> currentSelected Vertrag = null oder wert = -1");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beschlussIndex  DOCUMENT ME!
     */
    public void removeBeschluss(final int beschlussIndex) {
        if ((currentSelectedVertrag != null) || (beschlussIndex == -1)) {
            final BeschlussCustomBean beschlussBean = beschluesseTableModel.getCidsBeanAtRow(beschlussIndex);
            beschluesseTableModel.removeCidsBean(beschlussIndex);
            currentSelectedVertrag.getBeschluesse().remove(beschlussBean);
            beschluesseTableModel.fireTableDataChanged();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Beschluss wurde entfernt");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Beschluss konnten nicht entfernt werden --> currentSelected Vertrag = null oder wert = -1");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isVertragSelected() {
        return currentSelectedVertrag != null;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BeschluesseTableModel getBeschluesseTableModel() {
        return beschluesseTableModel;
    }
}
