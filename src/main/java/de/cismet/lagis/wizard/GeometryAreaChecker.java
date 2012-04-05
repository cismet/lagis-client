/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.wizard;

import com.vividsolutions.jts.geom.Geometry;

import org.netbeans.spi.wizard.WizardController;

import java.awt.Component;
import java.awt.Container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public final class GeometryAreaChecker implements GeometryWorker.IPostExecutionListener, Runnable {

    //~ Instance fields --------------------------------------------------------

    private WizardController wizardCtrl;
    private JPanel panel;
    private List<FlurstueckSchluesselCustomBean> targetKeys;

    private Map<FlurstueckSchluesselCustomBean, Geometry> resultGeomsMap;
    private double sumArea;
    private double sumTargets;
    private Map<FlurstueckSchluesselCustomBean, Geometry> targetGeomsMap;

    private boolean hasProblem;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeometryAreaChecker object.
     *
     * @param  targetKeys  DOCUMENT ME!
     * @param  panel       DOCUMENT ME!
     * @param  wizardCtrl  DOCUMENT ME!
     */
    public GeometryAreaChecker(final List<FlurstueckSchluesselCustomBean> targetKeys,
            final JPanel panel,
            final WizardController wizardCtrl) {
        this.init(targetKeys, panel, wizardCtrl);
    }

    /**
     * Creates a new GeometryAreaChecker object.
     *
     * @param  targetKey   DOCUMENT ME!
     * @param  panel       DOCUMENT ME!
     * @param  wizardCtrl  DOCUMENT ME!
     */
    public GeometryAreaChecker(final FlurstueckSchluesselCustomBean targetKey,
            final JPanel panel,
            final WizardController wizardCtrl) {
        final ArrayList<FlurstueckSchluesselCustomBean> targetKeys = new ArrayList<FlurstueckSchluesselCustomBean>(1);
        targetKeys.add(targetKey);
        this.init(targetKeys, panel, wizardCtrl);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   targetKeys  DOCUMENT ME!
     * @param   panel       DOCUMENT ME!
     * @param   wizardCtrl  DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    private void init(final List<FlurstueckSchluesselCustomBean> targetKeys,
            final JPanel panel,
            final WizardController wizardCtrl) {
        if ((wizardCtrl == null) || (panel == null) || (targetKeys == null)) {
            throw new NullPointerException();
        }

        this.wizardCtrl = wizardCtrl;
        this.panel = panel;
        this.targetKeys = targetKeys;
        this.targetGeomsMap = new HashMap<FlurstueckSchluesselCustomBean, Geometry>(targetKeys.size());
        this.hasProblem = true;
        this.sumArea = 0.0;
        this.sumTargets = 0.0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean hasProblem() {
        return this.hasProblem;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getSumArea() {
        return this.sumArea;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getSumTargets() {
        return this.sumTargets;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<FlurstueckSchluesselCustomBean> getTargetFlurstueckKeys() {
        return this.targetKeys;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<FlurstueckSchluesselCustomBean, Geometry> getTargetGeometriesMap() {
        return this.targetGeomsMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<FlurstueckSchluesselCustomBean, Geometry> getResultGeometriesMap() {
        return resultGeomsMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  container  DOCUMENT ME!
     * @param  isEnabled  DOCUMENT ME!
     */
    private void enableChildren(final Container container, final boolean isEnabled) {
        // get an arry of all the components in this container
        final Component[] components = container.getComponents();
        // for each element in the container enable/disable it
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof Container) {
                enableChildren(((Container)components[i]), isEnabled);
            }
            components[i].setEnabled(isEnabled);
        }
    }

    @Override
    public void run() {
        this.wizardCtrl.setBusy(true);
        enableChildren(this.panel, false);
        this.wizardCtrl.setProblem("Prüfe Flurstücke...");
    }

    @Override
    public void done(final Map<FlurstueckSchluesselCustomBean, Geometry> geometriesMap) {
        this.resultGeomsMap = geometriesMap;

        this.wizardCtrl.setBusy(false);
        this.enableChildren(this.panel, true);
        this.wizardCtrl.setProblem(null);

        double sumTargets = 0.0;
        Geometry geom;

        for (final FlurstueckSchluesselCustomBean targetKey : this.targetKeys) {
            geom = geometriesMap.remove(targetKey);

            if (geom == null) {
                this.wizardCtrl.setProblem(String.format(
                        "Konnte Geometrie zu Flurstück %s nicht finden.",
                        targetKey.getKeyString()));
                return;
            }

            sumTargets += (geom == null) ? 0.0 : geom.getArea();

            this.targetGeomsMap.put(targetKey, geom);
        }

        this.sumTargets = sumTargets;
        double sumArea = 0.0;
        Geometry geom2;
        for (final Map.Entry<FlurstueckSchluesselCustomBean, Geometry> entry : geometriesMap.entrySet()) {
            geom2 = entry.getValue();
            if (geom2 == null) {
                this.wizardCtrl.setProblem(String.format(
                        "Konnte keine Geometrie zu Flurstück %s finden.",
                        entry.getKey().getKeyString()));
                return;
            }

            sumArea += (geom2 == null) ? 0.0 : geom2.getArea();
        }

        this.sumArea = sumArea;

        this.hasProblem = false;
    }

    @Override
    public void doneWithErrors(final Exception e) {
        this.wizardCtrl.setProblem("Fehler beim Prüfen der Geometrien");
    }

    @Override
    public String toString() {
        return "GeometryAreaChecker{" + "wizardCtrl=" + wizardCtrl + ", panel=" + panel + ", targetKeys=" + targetKeys
                    + ", sumArea=" + sumArea + ", sumTargets=" + sumTargets
                    + ", hasProblem=" + hasProblem + '}';
    }
}
