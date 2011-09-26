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

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class EmptyDataSource implements JRDataSource {

    //~ Instance fields --------------------------------------------------------

    private int numIterations;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EmptyDataSource object.
     *
     * @param  numIterations  DOCUMENT ME!
     */
    public EmptyDataSource(final int numIterations) {
        this.numIterations = numIterations;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean next() throws JRException {
        if (this.numIterations > 0) {
            this.numIterations--;
            return true;
        }

        return false;
    }

    @Override
    public Object getFieldValue(final JRField jrf) throws JRException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
