/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.panels;

import java.awt.Insets;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.cismet.cids.custom.beans.lagis.NutzungBuchungCustomBean;

import de.cismet.tools.gui.jbands.interfaces.BandMember;
import de.cismet.tools.gui.jbands.interfaces.BandMemberSelectable;
import de.cismet.tools.gui.jbands.interfaces.Section;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class NKFBandMember extends JLabel implements Section, BandMember, BandMemberSelectable {

    //~ Static fields/initializers ---------------------------------------------

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    //~ Instance fields --------------------------------------------------------

    private double from = 0;
    private double to = 0;
    private final Date date;
    private boolean selected = false;

    private final Collection<Listener> listeners = new ArrayList<Listener>();

    private final Border unselectedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(4, 4, 4, 4),
            BorderFactory.createBevelBorder(BevelBorder.RAISED));
    private final Border selectedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(4, 4, 4, 4),
            BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form SimpleSectionPanel.
     *
     * @param  from  DOCUMENT ME!
     * @param  to    DOCUMENT ME!
     * @param  date  DOCUMENT ME!
     */
    public NKFBandMember(final double from,
            final double to,
            final Date date) {
        this.from = from;
        this.to = to;
        this.date = date;
        if (date != null) {
            setText(DATE_FORMAT.format(date));
        } else {
            setText("Aktuelle Nutzungen");
        }
        setBorder(unselectedBorder);
        setHorizontalAlignment(JLabel.CENTER);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setSelected(final boolean selected) {
        this.selected = selected;

        if (selected) {
            setBorder(selectedBorder);
        } else {
            setBorder(unselectedBorder);
        }
        for (final Listener listener : listeners) {
            listener.memberSelected(selected);
        }
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public BandMember getBandMember() {
        return this;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addListener(final Listener listener) {
        listeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     */
    public void removeAllListeners() {
        listeners.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removeListener(final Listener listener) {
        listeners.remove(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  from  DOCUMENT ME!
     */
    public void setFrom(final double from) {
        this.from = from;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  to  DOCUMENT ME!
     */
    public void setTo(final double to) {
        this.to = to;
    }

    @Override
    public double getMax() {
        return (from < to) ? to : from;
    }

    @Override
    public double getMin() {
        return (from < to) ? from : to;
    }

    @Override
    public JComponent getBandMemberComponent() {
        return this;
    }

    @Override
    public double getFrom() {
        return from;
    }

    @Override
    public double getTo() {
        return to;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getDate() {
        return date;
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public interface Listener {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  selected  DOCUMENT ME!
         */
        void memberSelected(final boolean selected);
    }
}
