/*
 * GemarkungComboboxModel.java
 *
 * Created on 19. April 2007, 08:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.models;



import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagisEE.util.Key;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataListener;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class KeyComboboxModel extends AbstractListModel implements MutableComboBoxModel, Serializable {
    
    private Vector<Key> keys = new Vector<Key>();
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Vector<ListDataListener> listener = new Vector<ListDataListener>();
    private Object selectedObject = null;
    
    /** Creates a new instance of GemarkungComboboxModel */
    public KeyComboboxModel(Set<Key> keySet) {
        this(new Vector<Key>(keySet));
    }
    
    public KeyComboboxModel(Vector<Key> keyList) {
        if(keyList == null){
            this.keys=new Vector<Key>();
        }else {
            this.keys = keyList;
        }
    }
    
    public KeyComboboxModel() {
        
    }
    
//    public KeyComboboxModel(final Key key,final JComboBox cbo){
//        //if(key instanceof Gemarkung){
////            cbo.setEnabled(false);
////            Thread keyRetrieverThread=new Thread() {
////                public void run() {
////                    if(key instanceof FlurstueckKey){
////                        log.debug("Abrufen der Flurkeys/Flurstueckkeys vom Server Key: "+((FlurstueckKey) key).toString());
////                    } else if(key instanceof FlurKey){
////                        log.debug("Abrufen der Flurkeys/Flurstueckkeys vom Server Key: "+((FlurKey) key).toString());
////                    }
////
////                    cbo.setModel(new KeyComboboxModel(new Vector<Key>(EJBroker.getInstance().getKeysDependingOnKey(key))));
////                    cbo.setEnabled(true);
////                    cbo.requestFocus();
////                }
////            };
////            keyRetrieverThread.setPriority(Thread.NORM_PRIORITY);
////            keyRetrieverThread.start();
////        } else if(key instanceof FlurKey){
////            cbo.setEnabled(false);
////            Thread keyRetrieverThread=new Thread() {
////                public void run() {
////                    log.debug("Abrufen der Flurstuecke vom Server");
////                    cbo.setModel(new KeyComboboxModel(new Vector<Key>(EJBroker.getInstance().getKeysDependingOnKey(key))));
////                    cbo.setEnabled(true);
////                }
////            };
////            keyRetrieverThread.setPriority(Thread.NORM_PRIORITY);
////            keyRetrieverThread.start();
////        }
//    }
    
//    public void setSelectedItem(Object anItem) {
//        //log.fatal("setSelected ITEM: "+anItem);
//        selectedItem = anItem;
//    }
    
//    public Object getElementAt(int index) {
//        //log.fatal("getSelected ITEM: "+key.get(index));
//        return key.get(index);
//    }
//
//    public void removeKey(Key key){
//        int index = this.key.indexOf(key);
//        if(index >-1){
//            this.key.remove(key);
//            fireContentsChanged(this,index,index);
//        }
//    }
//
//    public int getSize() {
//        //log.fatal("getSize(): "+key.size());
//        return key.size();
//    }
//
//    public Object getSelectedItem() {
//        return selectedItem;
//    }
//
//    public void setSelectedItem(Object anItem) {
//        int index = key.indexOf(anItem);
//        if(index >-1){
//            log.debug("Objekt aus model Selektiert");
//            fireContentsChanged(this,index,index);
//        } else {
//            log.debug("keinObjekt aus der Liste selektiert");
//        }
//    }
    
    // implements javax.swing.ComboBoxModel
    /**
     * Set the value of the selected item. The selected item may be null.
     * <p>
     * @param anObject The combo box value or null for no selection.
     */
    public void setSelectedItem(Object anObject) {
        log.info("COMBOBOX SETSELECTED ITEM: "+anObject);
        if ((selectedObject != null && !selectedObject.equals( anObject )) ||
                selectedObject == null && anObject != null) {
            selectedObject = anObject;
            fireContentsChanged(this, -1, -1);
        }
    }
    
    // implements javax.swing.ComboBoxModel
    public Object getSelectedItem() {
        return selectedObject;
    }
    
    // implements javax.swing.ListModel
    public int getSize() {
        return keys.size();
    }
    
    // implements javax.swing.ListModel
    public Object getElementAt(int index) {
        if ( index >= 0 && index < keys.size() )
            return keys.elementAt(index);
        else
            return null;
    }
    
    public boolean contains(Object key){
        return keys.contains(key);
    }
    
    
    /**
     * Returns the index-position of the specified object in the list.
     *
     * @param anObject
     * @return an int representing the index position, where 0 is
     *         the first position
     */
    public int getIndexOf(Object anObject) {
        return keys.indexOf(anObject);
    }
    
    // implements javax.swing.MutableComboBoxModel
    public void addElement(Object keyToAdd) {        
        LagisBroker.warnIfThreadIsNotEDT();
        if(keyToAdd != null && keyToAdd instanceof Key){
            keys.addElement((Key)keyToAdd);
            Collections.sort(keys);
            fireContentsChanged(this,0,keys.size()-1);
            //fireIntervalAdded(this,keys.size()-1, keys.size()-1);
            if ( keys.size() == 1 && selectedObject == null && keyToAdd != null ) {
                setSelectedItem( keyToAdd );
            }
        } else {
            log.warn("Es wurde versucht ein Object != Key zu adden");
        }
    }
    
    // implements javax.swing.MutableComboBoxModel
    public void insertElementAt(Object anObject,int index) {
        keys.insertElementAt((Key)keys,index);
        fireIntervalAdded(this, index, index);
    }
    
    // implements javax.swing.MutableComboBoxModel
    public void removeElementAt(int index) {
        if ( getElementAt( index ) == selectedObject ) {
            if ( index == 0 ) {
                setSelectedItem( getSize() == 1 ? null : getElementAt( index + 1 ) );
            } else {
                setSelectedItem( getElementAt( index - 1 ) );
            }
        }
        
        keys.removeElementAt(index);
        
        fireIntervalRemoved(this, index, index);
    }
    
    // implements javax.swing.MutableComboBoxModel
    public void removeElement(Object anObject) {
        int index = keys.indexOf(anObject);
        if ( index != -1 ) {
            removeElementAt(index);
        }
    }
    
    /**
     * Empties the list.
     */
    public void removeAllElements() {
        if ( keys.size() > 0 ) {
            int firstIndex = 0;
            int lastIndex = keys.size() - 1;
            keys.removeAllElements();
            selectedObject = null;
            fireIntervalRemoved(this, firstIndex, lastIndex);
        } else {
            selectedObject = null;
        }
    }
    
    
    
}
