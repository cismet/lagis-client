/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class SortedList<T> extends LinkedList<T> {

    //~ Instance fields --------------------------------------------------------

    Comparator<T> comparator;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SortedList object.
     *
     * @param  comparator  DOCUMENT ME!
     */
    public SortedList(final Comparator<T> comparator) {
        super();
        this.comparator = comparator;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean add(final T e) {
        final boolean result = super.add(e);
        Collections.sort(this, comparator);
        return result;
    }

    @Override
    public void add(final int index, final T element) {
        super.add(index, element);
        Collections.sort(this, comparator);
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        final boolean result = super.addAll(c);
        Collections.sort(this, comparator);
        return result;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c) {
        final boolean result = super.addAll(index, c);
        Collections.sort(this, comparator);
        return result;
    }

    @Override
    public T remove(final int index) {
        final T result = super.remove(index);
        Collections.sort(this, comparator);
        return result;
    }

    @Override
    public boolean remove(final Object o) {
        final boolean result = super.remove(o);
        Collections.sort(this, comparator);
        return result;
    }

    @Override
    public T set(final int index, final T element) {
        final T result = super.set(index, element);
        Collections.sort(this, comparator);
        return result;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        final boolean result = super.removeAll(c);
        Collections.sort(this, comparator);
        return result;
    }
}
