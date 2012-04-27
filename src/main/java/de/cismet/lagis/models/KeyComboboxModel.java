/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagisEE.interfaces.Key;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class KeyComboboxModel extends AbstractListModel implements MutableComboBoxModel, Serializable {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(KeyComboboxModel.class);

    //~ Instance fields --------------------------------------------------------

    private List<Key> keys = new ArrayList<Key>();
    private Key selectedKey = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KeyComboboxModel object.
     */
    public KeyComboboxModel() {
    }

    /**
     * Creates a new KeyComboboxModel object.
     *
     * @param  keys  DOCUMENT ME!
     */
    public KeyComboboxModel(final List<Key> keys) {
        if (keys == null) {
            this.keys = new ArrayList<Key>();
        } else {
            this.keys = keys;
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setSelectedItem(final Object key) {
        if ((key != null) && (key instanceof Key) && !key.equals(selectedKey)) {
            selectedKey = (Key)key;
            fireContentsChanged(this, -1, -1);
        }
    }

    @Override
    public Object getSelectedItem() {
        return selectedKey;
    }

    @Override
    public int getSize() {
        return keys.size();
    }

    @Override
    public Object getElementAt(final int index) {
        if ((index >= 0) && (index < keys.size())) {
            return keys.get(index);
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean contains(final Object key) {
        if ((key != null) && (key instanceof Key)) {
            return keys.contains((Key)key);
        } else {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getIndexOf(final Object key) {
        if ((key != null) && (key instanceof Key)) {
            return keys.indexOf((Key)key);
        } else {
            return -1;
        }
    }

    @Override
    public void addElement(final Object key) {
        LagisBroker.warnIfThreadIsNotEDT();
        if ((key != null) && (key instanceof Key)) {
            keys.add((Key)key);
            Collections.sort(keys);
            fireContentsChanged(this, 0, keys.size() - 1);
            // fireIntervalAdded(this,keys.size()-1, keys.size()-1);
            if ((keys.size() == 1) && (selectedKey == null) && (key != null)) {
                setSelectedItem(key);
            }
        } else {
            LOG.warn("Es wurde versucht ein Object != Key zu adden");
        }
    }

    @Override
    public void insertElementAt(final Object key, final int index) {
        if ((key != null) && (key instanceof Key)) {
            keys.add(index, (Key)key);
            fireIntervalAdded(this, index, index);
        } else {
            LOG.warn("Es wurde versucht ein Object != Key zu adden");
        }
    }

    @Override
    public void removeElementAt(final int index) {
        if ((index >= 0) && (index < keys.size())) {
            if (getElementAt(index) == selectedKey) {
                if (index == 0) {
                    setSelectedItem((getSize() == 1) ? null : getElementAt(index + 1));
                } else {
                    setSelectedItem(getElementAt(index - 1));
                }
            }
            keys.remove(index);
            fireIntervalRemoved(this, index, index);
        }
    }

    @Override
    public void removeElement(final Object key) {
        final int index = keys.indexOf(key);
        if (index != -1) {
            removeElementAt(index);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void removeAllElements() {
        if (keys.size() > 0) {
            final int firstIndex = 0;
            final int lastIndex = keys.size() - 1;
            keys.clear();
            selectedKey = null;
            fireIntervalRemoved(this, firstIndex, lastIndex);
        } else {
            selectedKey = null;
        }
    }
}
