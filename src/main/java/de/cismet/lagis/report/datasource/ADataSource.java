/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.report.datasource;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import java.text.DateFormat;
import java.text.DecimalFormat;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.cismet.lagis.broker.LagisBroker;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public abstract class ADataSource<T> implements JRDataSource {

    //~ Static fields/initializers ---------------------------------------------

    protected static final String YES = "Ja";
    protected static final String NO = "Nein";

    protected static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat(",##0.00");
    protected static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("#,##0");
//    protected static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("#,#00");

    protected static final LagisBroker LAGIS_BROKER = LagisBroker.getInstance();
    protected static final DateFormat DF = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);

    //~ Instance fields --------------------------------------------------------

    protected T currentItem;
    protected List<T> items;
    protected int end;
    protected int currentIndex;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ADataSource object.
     */
    public ADataSource() {
        this.init(this.retrieveData());
    }

    /**
     * Creates a new NutzungenDataSource object.
     *
     * @param  items  buchungen DOCUMENT ME!
     */
    public ADataSource(final List<T> items) {
        this.init(items);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   items  DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    private void init(final List<T> items) {
        if (items == null) {
            throw new NullPointerException();
        }

        this.items = items;
        this.end = this.items.size() - 1;
        this.currentIndex = -1;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean hasData() {
        return !this.items.isEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract List<T> retrieveData();

    @Override
    public boolean next() throws JRException {
        if (this.currentIndex == this.end) {
            return false;
        }

        this.currentIndex++;
        this.currentItem = this.items.get(this.currentIndex);
        return true;
    }

    @Override
    public Object getFieldValue(final JRField jrf) throws JRException {
        if (jrf == null) {
            throw new NullPointerException("Given JRField is null");
        }

        final String fieldName = jrf.getName();
        if (fieldName == null) {
            throw new NullPointerException("Retrieved field name is null");
        }

        return this.getFieldValue(fieldName);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   date  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String formatDate(final Date date) {
        if (date == null) {
            return null;
        }

        return DF.format(date);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   number  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String formatNumber(final Integer number) {
        if (number == null) {
            return null;
        }

        return INTEGER_FORMAT.format(number);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   number  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String formatNumber(final Double number) {
        if (number == null) {
            return null;
        }

        return DOUBLE_FORMAT.format(number);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bool  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String formatBoolean(final Boolean bool) {
        if (bool == null) {
            return null;
        }

        return bool ? YES : NO;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fieldName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  JRException  DOCUMENT ME!
     */
    protected abstract Object getFieldValue(final String fieldName) throws JRException;
}
