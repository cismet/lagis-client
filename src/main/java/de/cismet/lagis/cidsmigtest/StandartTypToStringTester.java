/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.cidsmigtest;

/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;

import java.util.Date;

/*
 * Copyright (C) 2012 cismet GmbH
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

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class StandartTypToStringTester {

    //~ Static fields/initializers ---------------------------------------------

    private static int TAB_LVL = 0;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected static String t() {
        return StringUtils.repeat("\t", TAB_LVL);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected static String tab() {
        TAB_LVL++;
        return t();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected static String untab() {
        final String t = t();
        TAB_LVL--;
        return t;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final Date object) {
        if (object == null) {
            return null;
        } else {
            final SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
            return "(Date) \"" + sd.format(object) + "\"";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final Integer object) {
        if (object == null) {
            return null;
        } else {
            return "(Integer) " + object.toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final Float object) {
        if (object == null) {
            return null;
        } else {
            return "(Float) " + object.toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final Long object) {
        if (object == null) {
            return null;
        } else {
            return "(Long) " + object.toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final Double object) {
        if (object == null) {
            return null;
        } else {
            return "(Double) " + object.toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final String object) {
        if (object == null) {
            return null;
        } else {
            return "(String) \"" + object.trim().replaceAll("\"", "\\\"") + "\"";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final Boolean object) {
        if (object == null) {
            return null;
        } else {
            return "(Boolean) " + ((object) ? "true" : "false");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final Object object) {
        if (object == null) {
            return "null";
        } else {
            return "[***" + object.getClass().getName() + " | " + object.toString() + "]";
        }
    }
}
