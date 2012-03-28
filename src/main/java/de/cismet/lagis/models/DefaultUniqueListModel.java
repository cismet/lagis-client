/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * DefaultHashListModel.java
 *
 * Created on 30. August 2007, 17:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractListModel;

import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckSchluesselCustomBean;
/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class DefaultUniqueListModel extends AbstractListModel {

    //~ Instance fields --------------------------------------------------------

    private Vector delegate = new Vector();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DefaultUniqueListModel object.
     */
    public DefaultUniqueListModel() {
    }

    /**
     * Creates a new DefaultUniqueListModel object.
     *
     * @param  crossRefs  DOCUMENT ME!
     */
    public DefaultUniqueListModel(final Collection<FlurstueckSchluesselCustomBean> crossRefs) {
        if ((crossRefs != null) && (crossRefs.size() > 0)) {
            delegate = new Vector(crossRefs);
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public int getSize() {
        return delegate.size();
    }

    @Override
    public Object getElementAt(final int index) {
        return delegate.elementAt(index);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  anArray  DOCUMENT ME!
     */
    public void copyInto(final Object[] anArray) {
        delegate.copyInto(anArray);
    }

    /**
     * DOCUMENT ME!
     */
    public void trimToSize() {
        delegate.trimToSize();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  minCapacity  DOCUMENT ME!
     */
    public void ensureCapacity(final int minCapacity) {
        delegate.ensureCapacity(minCapacity);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  newSize  DOCUMENT ME!
     */
    public void setSize(final int newSize) {
        final int oldSize = delegate.size();
        delegate.setSize(newSize);
        if (oldSize > newSize) {
            fireIntervalRemoved(this, newSize, oldSize - 1);
        } else if (oldSize < newSize) {
            fireIntervalAdded(this, oldSize, newSize - 1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int capacity() {
        return delegate.capacity();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int size() {
        return delegate.size();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Enumeration<?> elements() {
        return delegate.elements();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   elem  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean contains(final Object elem) {
        return delegate.contains(elem);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   elem  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int indexOf(final Object elem) {
        return delegate.indexOf(elem);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   elem   DOCUMENT ME!
     * @param   index  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int indexOf(final Object elem, final int index) {
        return delegate.indexOf(elem, index);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   elem  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int lastIndexOf(final Object elem) {
        return delegate.lastIndexOf(elem);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   elem   DOCUMENT ME!
     * @param   index  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int lastIndexOf(final Object elem, final int index) {
        return delegate.lastIndexOf(elem, index);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   index  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object elementAt(final int index) {
        return delegate.elementAt(index);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object firstElement() {
        return delegate.firstElement();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object lastElement() {
        return delegate.lastElement();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  obj    DOCUMENT ME!
     * @param  index  DOCUMENT ME!
     */
    public void setElementAt(final Object obj, final int index) {
        if (!delegate.contains(obj)) {
            delegate.setElementAt(obj, index);
            fireContentsChanged(this, index, index);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  index  DOCUMENT ME!
     */
    public void removeElementAt(final int index) {
        delegate.removeElementAt(index);
        fireIntervalRemoved(this, index, index);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  obj    DOCUMENT ME!
     * @param  index  DOCUMENT ME!
     */
    public void insertElementAt(final Object obj, final int index) {
        if (!delegate.contains(obj)) {
            delegate.insertElementAt(obj, index);
            fireIntervalAdded(this, index, index);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  obj  DOCUMENT ME!
     */
    public void addElement(final Object obj) {
        if (!delegate.contains(obj)) {
            final int index = delegate.size();
            delegate.addElement(obj);
            fireIntervalAdded(this, index, index);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeElement(final Object obj) {
        final int index = indexOf(obj);
        final boolean rv = delegate.removeElement(obj);
        if (index >= 0) {
            fireIntervalRemoved(this, index, index);
        }
        return rv;
    }

    /**
     * DOCUMENT ME!
     */
    public void removeAllElements() {
        final int index1 = delegate.size() - 1;
        delegate.removeAllElements();
        if (index1 >= 0) {
            fireIntervalRemoved(this, 0, index1);
        }
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object[] toArray() {
        final Object[] rv = new Object[delegate.size()];
        delegate.copyInto(rv);
        return rv;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   index  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object get(final int index) {
        return delegate.elementAt(index);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   index    DOCUMENT ME!
     * @param   element  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object set(final int index, final Object element) {
        if (!delegate.contains(element)) {
            final Object rv = delegate.elementAt(index);
            delegate.setElementAt(element, index);
            fireContentsChanged(this, index, index);
            return rv;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  index    DOCUMENT ME!
     * @param  element  DOCUMENT ME!
     */
    public void add(final int index, final Object element) {
        if (!delegate.contains(element)) {
            {
                delegate.insertElementAt(element, index);
                fireIntervalAdded(this, index, index);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   index  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object remove(final int index) {
        final Object rv = delegate.elementAt(index);
        delegate.removeElementAt(index);
        fireIntervalRemoved(this, index, index);
        return rv;
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        final int index1 = delegate.size() - 1;
        delegate.removeAllElements();
        if (index1 >= 0) {
            fireIntervalRemoved(this, 0, index1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fromIndex  DOCUMENT ME!
     * @param   toIndex    DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public void removeRange(final int fromIndex, final int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex must be <= toIndex");
        }
        for (int i = toIndex; i >= fromIndex; i--) {
            delegate.removeElementAt(i);
        }
        fireIntervalRemoved(this, fromIndex, toIndex);
    }
}
