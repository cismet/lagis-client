/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.copypaste;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;

import de.cismet.cismap.commons.gui.MappingComponent;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.main.LagisApp;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;

import de.cismet.lagisEE.entity.basic.BasicEntity;

/**
 * Clipboard implementation for copying and pasting Flurstück related information.
 *
 * @author   Benjamin Friedrich (benjamin.friedrich@cismet.de)
 * @version  1.0, 15.11.2011
 */
public final class FlurstueckInfoClipboard implements FlurstueckChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] UNUSED_COPY_OPTIONS = new String[] { "Überschreiben", "Abbrechen" };

    //~ Instance fields --------------------------------------------------------

    private final LagisApp lagisApp;
    private final JButton copyButton;
    private final JButton pasteButton;

    private FlurstueckCustomBean copyFS;
    private FlurstueckCustomBean pasteFS;

    private boolean wasCopyPastedOnce;

    private final List<Copyable> copyListeners;
    private final List<Pasteable> pasteListeners;
    private final List<BasicEntity> copiedData;

    private boolean isActive;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlurstueckInfoClipboard object.
     *
     * @param   lagisApp     Reference to LagisApp instance
     * @param   copyButton   button to trigger copying
     * @param   pasteButton  button to trigger pasting
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public FlurstueckInfoClipboard(final LagisApp lagisApp,
            final JButton copyButton,
            final JButton pasteButton) {
        if (lagisApp == null) {
            throw new NullPointerException("Given LagisApp instance must not be null");
        }

        if (copyButton == null) {
            throw new NullPointerException("Given copy button must not be null");
        }

        if (pasteButton == null) {
            throw new NullPointerException("Given paste button must not be null");
        }

        this.lagisApp = lagisApp;
        this.copyButton = copyButton;
        this.pasteButton = pasteButton;
        this.wasCopyPastedOnce = false;

        this.copyButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    FlurstueckInfoClipboard.this.copy();
                }
            });

        this.pasteButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    FlurstueckInfoClipboard.this.paste();
                }
            });

        // initial tooltip text (is changed in flurstueckChanged() anyway)
        this.copyButton.setToolTipText("Kopiere Daten aus Flurstück");
        this.pasteButton.setToolTipText("Es wurden noch keine Daten kopiert");

        this.copyListeners = new ArrayList<Copyable>();
        this.pasteListeners = new ArrayList<Pasteable>();
        this.copiedData = new ArrayList<BasicEntity>();

        this.setActive(false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  copyListener  DOCUMENT ME!
     */
    public void addCopyListener(final Copyable copyListener) {
        this.copyListeners.add(copyListener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pasteListener  DOCUMENT ME!
     */
    public void addPasteListener(final Pasteable pasteListener) {
        this.pasteListeners.add(pasteListener);
    }

    /**
     * This method is intended to be called when edit-mode is switched.
     *
     * @param  isActive  true if app is in edit-mode, false otherwise
     */
    public void setActive(final boolean isActive) {
        final boolean active = isActive && (this.copyFS != null) && !this.copyFS.equals(this.pasteFS);
        this.isActive = active;
        this.pasteButton.setEnabled(active);
    }

    /**
     * Implementation of copying mechanism.
     */
    private void copy() {
        final FlurstueckCustomBean oldCopyFS = this.copyFS;
        this.copyFS = LagisBroker.getInstance().getCurrentFlurstueck();

        if ((oldCopyFS != null) && (!this.copyFS.equals(oldCopyFS)) && !this.wasCopyPastedOnce) {
            final int answer = JOptionPane.showOptionDialog(
                    this.lagisApp,
                    "Die zuletzt kopierten Informationen zu dem Flurstück \""
                            + oldCopyFS
                            + "\" wurden nicht verwendet.\nMöchten Sie diese Daten "
                            + " trotzdem mit den Informationen aus \""
                            + this.copyFS
                            + "\" überschreiben?",
                    "Achtung",
                    JOptionPane.WARNING_MESSAGE,
                    JOptionPane.YES_NO_OPTION,
                    null,
                    UNUSED_COPY_OPTIONS,
                    UNUSED_COPY_OPTIONS[0]);

            if (answer == JOptionPane.NO_OPTION) {
                // current Flurstück in clipboard shall NOT be replaced by another one
                this.copyFS = oldCopyFS;
                return;
            }
        }

        this.wasCopyPastedOnce = false;
        this.copyFS = LagisBroker.getInstance().getCurrentFlurstueck();

        this.copiedData.clear();
        for (final Copyable listener : this.copyListeners) {
            this.copiedData.addAll(listener.getCopyData());
        }

        this.pasteButton.setToolTipText("Einfügen von Daten aus Flurstück " + this.copyFS);
    }

    /**
     * Implementation of the pasting mechanism.
     */
    private void paste() {
        // should never happen
        if ((this.pasteFS == null) || this.pasteFS.equals(this.copyFS)) {
            // do nothing
            this.pasteButton.setEnabled(false);
            return;
        }

        final FlurstueckInfoClipboardPasteWidget widget = new FlurstueckInfoClipboardPasteWidget(
                this.lagisApp,
                true,
                this);
        widget.setVisible(true);
    }

    /**
     * Returns all the data copied before.
     *
     * @return  copied data. If no data has been copied, an empty list is returned
     */
    public List<BasicEntity> getCopiedData() {
        return this.copiedData;
    }

    /**
     * Returns all registered {@link Copyable} instances. This method is needed in
     * {@link FlurstueckInfoClipboardPasteWidget}.
     *
     * @return  {@link Copyable} instances
     */
    public List<Copyable> getCopyListeners() {
        return this.copyListeners;
    }

    /**
     * Pastes the given data to all registered {@link Pasteable} instances. This method is called by
     * {@link FlurstueckInfoClipboardPasteWidget} when the user has chosen which items shall actually be pasted.
     *
     * @param  data  rebeList DOCUMENT ME!
     */
    public void performPaste(final List<BasicEntity> data) {
        for (final Pasteable listener : this.pasteListeners) {
            listener.pasteAll(data);
        }

        this.wasCopyPastedOnce = true;

        // perform "zoom tour" animation in mapping component
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final MappingComponent mappingComponent = LagisBroker.getInstance().getMappingComponent();
                    mappingComponent.zoomToFeatureCollection();
                }
            });

        // to avoid multiple pastings
        this.pasteButton.setEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        // only enable copy button, if the currently chosen Flurstück is not the one which is already copied
        this.copyButton.setToolTipText("Kopiere Daten aus Flurstück " + newFlurstueck);
        this.copyButton.setEnabled(true);

        this.pasteFS = newFlurstueck;
        if (this.isActive && (this.copyFS != null) && !this.copyFS.equals(newFlurstueck)) {
            this.pasteButton.setEnabled(true);
        }
    }
}
