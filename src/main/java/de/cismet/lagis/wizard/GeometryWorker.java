/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.wizard;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.lagis.thread.WFSByKeyWorkerThread;
import de.cismet.lagis.thread.WFSRetrieverFactory;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class GeometryWorker implements Callable<Map<FlurstueckSchluesselCustomBean, Geometry>> {

    //~ Static fields/initializers ---------------------------------------------

    private static final WFSRetrieverFactory WFS_RETR_FACTORY = WFSRetrieverFactory.getInstance();

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(GeometryWorker.class);

    //~ Instance fields --------------------------------------------------------

    private Exception lastException;
    private final List<FlurstueckSchluesselCustomBean> fsKeys;
    private final ArrayList<Runnable> preExeListeners;
    private final ArrayList<IPostExecutionListener> postExeListeners;
    private final HashMap<FlurstueckSchluesselCustomBean, Geometry> geometriesMap;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeometryWorker object.
     *
     * @param   fsKeys  joinCandidates DOCUMENT ME!
     *
     * @throws  NullPointerException      DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public GeometryWorker(final List<FlurstueckSchluesselCustomBean> fsKeys) {
        if (fsKeys == null) {
            throw new NullPointerException("Given list of FlurstueckSchluesser must not be null");
        }

        if (fsKeys.isEmpty()) {
            throw new IllegalArgumentException("Given list of FlurstueckSchluesser must be greater than zero");
        }

        this.fsKeys = new ArrayList<FlurstueckSchluesselCustomBean>(fsKeys);
        this.geometriesMap = new HashMap<FlurstueckSchluesselCustomBean, Geometry>(fsKeys.size());
        this.preExeListeners = new ArrayList<Runnable>();
        this.postExeListeners = new ArrayList<IPostExecutionListener>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   listener  DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public void addPreExecutionListener(final Runnable listener) {
        if (listener == null) {
            throw new NullPointerException();
        }

        this.preExeListeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   listener  DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public void addPostExecutionListener(final IPostExecutionListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }

        this.postExeListeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     */
    private void notifyPreExecutionListener() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("notify pre-execution listeners");
        }

        for (final Runnable listener : this.preExeListeners) {
            listener.run();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void notifyPostExecutionListenerAboutSuccess() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("notify post-execution listeners");
        }

        for (final IPostExecutionListener listener : this.postExeListeners) {
            listener.done((Map)this.geometriesMap.clone());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    private void notifyPostExecutionListenerAboutError(final Exception e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("notify post-execution listeners about error", this.lastException);
        }

        for (final IPostExecutionListener listener : this.postExeListeners) {
            listener.doneWithErrors(this.lastException);
        }
    }

    @Override
    public Map<FlurstueckSchluesselCustomBean, Geometry> call() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("GeometryWorker doInBackground");
        }

        try {
            this.notifyPreExecutionListener();

            WFSByKeyWorkerThread currentWorker;

            for (final FlurstueckSchluesselCustomBean currentKey : this.fsKeys) {
                currentWorker = (WFSByKeyWorkerThread)WFS_RETR_FACTORY.getWFSRetriever(
                        currentKey,
                        null,
                        null);

                currentWorker.execute();

                final Geometry currentGeom = currentWorker.get();
                this.geometriesMap.put(currentWorker.getFlurstueckKey(), currentGeom);
            }

            this.notifyPostExecutionListenerAboutSuccess();

            return this.geometriesMap;
        } catch (final Exception ex) {
            LOG.error("Error while checking geometry: ", ex);
            this.notifyPostExecutionListenerAboutError(ex);
            return null;
        }
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static interface IPostExecutionListener {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  geometriesMap  DOCUMENT ME!
         */
        void done(final Map<FlurstueckSchluesselCustomBean, Geometry> geometriesMap);
        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        void doneWithErrors(final Exception e);
    }
}
