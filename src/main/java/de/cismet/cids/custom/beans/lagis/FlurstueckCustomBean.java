/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import Sirius.server.middleware.types.MetaObject;

import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.Flurstueck;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FlurstueckCustomBean extends BasicEntity implements Flurstueck {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Flurstueck.class);
    public static final String TABLE = "flurstueck";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String bemerkung;
    private SpielplatzCustomBean fk_spielplatz;
    private FlurstueckSchluesselCustomBean fk_flurstueck_schluessel;
    private Boolean in_stadtbesitz;
    private Collection<BaumCustomBean> ar_baeume;
    private Collection<MipaCustomBean> ar_mipas;
    private Collection<VertragCustomBean> ar_vertraege;
    private Collection<VerwaltungsbereichCustomBean> n_verwaltungsbereiche;
    private Collection<DmsUrlCustomBean> n_dms_urls;
    private Collection<NutzungCustomBean> n_nutzungen;
    private Collection<RebeCustomBean> n_rebes;
    private MetaObject[] extension_vertrag_querverweise;
    private MetaObject[] extension_mipa_querverweise;
    private MetaObject[] extension_baum_querverweise;
    private Collection<FlurstueckSchluesselCustomBean> vertrag_querverweise;
    private Collection<FlurstueckSchluesselCustomBean> mipa_querverweise;
    private Collection<FlurstueckSchluesselCustomBean> baum_querverweise;
    private String[] PROPERTY_NAMES = new String[] {
            "id",
            "bemerkung",
            "fk_spielplatz",
            "fk_flurstueck_schluessel",
            "in_stadtbesitz",
            "ar_baeume",
            "ar_mipas",
            "ar_vertraege",
            "n_verwaltungsbereiche",
            "n_dms_urls",
            "n_nutzungen",
            "n_rebes",
            "extension_vertrag_querverweise",
            "extension_mipa_querverweise",
            "extension_baum_querverweise"
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlurstueckCustomBean object.
     */
    public FlurstueckCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static FlurstueckCustomBean createNew() {
        try {
            return (FlurstueckCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
        } catch (Exception ex) {
            LOG.error("error creating " + TABLE + " bean", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Integer getId() {
        return this.id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramInteger  DOCUMENT ME!
     */
    @Override
    public void setId(final Integer paramInteger) {
        this.id = paramInteger;

        this.propertyChangeSupport.firePropertyChange("id", null, this.id);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getBemerkung() {
        return this.bemerkung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramString  DOCUMENT ME!
     */
    @Override
    public void setBemerkung(final String paramString) {
        this.bemerkung = paramString;

        this.propertyChangeSupport.firePropertyChange("bemerkung", null, this.bemerkung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SpielplatzCustomBean getFk_spielplatz() {
        return this.fk_spielplatz;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_spielplatz(final SpielplatzCustomBean val) {
        this.fk_spielplatz = val;

        this.propertyChangeSupport.firePropertyChange("fk_spielplatz", null, this.fk_spielplatz);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckSchluesselCustomBean getFk_flurstueck_schluessel() {
        return this.fk_flurstueck_schluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_flurstueck_schluessel(final FlurstueckSchluesselCustomBean val) {
        this.fk_flurstueck_schluessel = val;

        this.propertyChangeSupport.firePropertyChange("fk_flurstueck_schluessel", null, this.fk_flurstueck_schluessel);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isIn_stadtbesitz() {
        return this.in_stadtbesitz;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getIn_stadtbesitz() {
        return this.in_stadtbesitz;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramBoolean  DOCUMENT ME!
     */
    public void setIn_stadtbesitz(final Boolean paramBoolean) {
        this.in_stadtbesitz = paramBoolean;

        this.propertyChangeSupport.firePropertyChange("in_stadtbesitz", null, this.in_stadtbesitz);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumCustomBean> getAr_baeume() {
        return this.ar_baeume;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramCollection  DOCUMENT ME!
     */
    public void setAr_baeume(final Collection<BaumCustomBean> paramCollection) {
        this.ar_baeume = paramCollection;

        this.propertyChangeSupport.firePropertyChange("ar_baeume", null, this.ar_baeume);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaCustomBean> getAr_mipas() {
        return this.ar_mipas;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramCollection  DOCUMENT ME!
     */
    public void setAr_mipas(final Collection<MipaCustomBean> paramCollection) {
        this.ar_mipas = paramCollection;

        this.propertyChangeSupport.firePropertyChange("ar_mipas", null, this.ar_mipas);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VertragCustomBean> getAr_vertraege() {
        return this.ar_vertraege;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramCollection  DOCUMENT ME!
     */
    public void setAr_vertraege(final Collection<VertragCustomBean> paramCollection) {
        this.ar_vertraege = paramCollection;

        this.propertyChangeSupport.firePropertyChange("ar_vertraege", null, this.ar_vertraege);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VerwaltungsbereichCustomBean> getN_verwaltungsbereiche() {
        return this.n_verwaltungsbereiche;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramCollection  DOCUMENT ME!
     */
    public void setN_verwaltungsbereiche(final Collection<VerwaltungsbereichCustomBean> paramCollection) {
        this.n_verwaltungsbereiche = paramCollection;

        this.propertyChangeSupport.firePropertyChange("n_verwaltungsbereiche", null, this.n_verwaltungsbereiche);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<DmsUrlCustomBean> getN_dms_urls() {
        return this.n_dms_urls;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  paramCollection DOCUMENT ME!
     */
    public void setN_dms_urls(final Collection<DmsUrlCustomBean> val) {
//        Collections.sort((List<DmsUrlCustomBean>)val, new Comparator<DmsUrlCustomBean>() {
//
//                @Override
//                public int compare(final DmsUrlCustomBean o1, final DmsUrlCustomBean o2) {
//                    return (int)(o1.getId() - o2.getId());
//                }
//            });
        this.n_dms_urls = val;

        this.propertyChangeSupport.firePropertyChange("n_dms_urls", null, this.n_dms_urls);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<NutzungCustomBean> getN_nutzungen() {
        return this.n_nutzungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  paramCollection DOCUMENT ME!
     */
    public void setN_nutzungen(final Collection<NutzungCustomBean> val) {
//        Collections.sort((List<NutzungCustomBean>)val, new Comparator<NutzungCustomBean>() {
//
//                @Override
//                public int compare(final NutzungCustomBean o1, final NutzungCustomBean o2) {
//                    return (int)(o1.getId() - o2.getId());
//                }
//            });
        this.n_nutzungen = val;

        this.propertyChangeSupport.firePropertyChange("n_nutzungen", null, this.n_nutzungen);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<RebeCustomBean> getN_rebes() {
        return this.n_rebes;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setN_rebes(final Collection<RebeCustomBean> val) {
//        Collections.sort((List<RebeCustomBean>)val, new Comparator<RebeCustomBean>() {
//
//                @Override
//                public int compare(final RebeCustomBean o1, final RebeCustomBean o2) {
//                    return (int)(o1.getId() - o2.getId());
//                }
//            });
        this.n_rebes = val;

        this.propertyChangeSupport.firePropertyChange("n_rebes", null, this.n_rebes);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject[] getExtension_vertrag_querverweise() {
        return extension_vertrag_querverweise;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setExtension_vertrag_querverweise(final MetaObject[] val) {
        this.extension_vertrag_querverweise = val;

        this.propertyChangeSupport.firePropertyChange(
            "extension_vertrag_querverweise",
            null,
            this.extension_vertrag_querverweise);

        vertrag_querverweise = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject[] getExtension_mipa_querverweise() {
        return extension_mipa_querverweise;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setExtension_mipa_querverweise(final MetaObject[] val) {
        this.extension_mipa_querverweise = val;

        this.propertyChangeSupport.firePropertyChange(
            "extension_mipa_querverweise",
            null,
            this.extension_mipa_querverweise);

        mipa_querverweise = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject[] getExtension_baum_querverweise() {
        return extension_baum_querverweise;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setExtension_baum_querverweise(final MetaObject[] val) {
        this.extension_baum_querverweise = val;

        this.propertyChangeSupport.firePropertyChange(
            "extension_baum_querverweise",
            null,
            this.extension_baum_querverweise);

        baum_querverweise = null;
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public FlurstueckSchluesselCustomBean getFlurstueckSchluessel() {
        return getFk_flurstueck_schluessel();
    }

    @Override
    public void setFlurstueckSchluessel(final FlurstueckSchluesselCustomBean val) {
        setFk_flurstueck_schluessel(val);
    }

    @Override
    public Collection<MipaCustomBean> getMiPas() {
        return getAr_mipas();
    }

    @Override
    public void setMiPas(final Collection<MipaCustomBean> val) {
        setAr_mipas(val);
    }

    @Override
    public Collection<BaumCustomBean> getBaeume() {
        return getAr_baeume();
    }

    @Override
    public void setBaeume(final Collection<BaumCustomBean> val) {
        setAr_baeume(val);
    }

    @Override
    public Collection<VerwaltungsbereichCustomBean> getVerwaltungsbereiche() {
        return getN_verwaltungsbereiche();
    }

    @Override
    public void setVerwaltungsbereiche(final Collection<VerwaltungsbereichCustomBean> val) {
        setN_verwaltungsbereiche(val);
    }

    @Override
    public Collection<DmsUrlCustomBean> getDokumente() {
        return getN_dms_urls();
    }

    @Override
    public void setDokumente(final Collection<DmsUrlCustomBean> val) {
        setN_dms_urls(val);
    }

    @Override
    public Collection<NutzungCustomBean> getNutzungen() {
        return getN_nutzungen();
    }

    @Override
    public void setNutzungen(final Collection<NutzungCustomBean> val) {
        setN_nutzungen(val);
    }

    @Override
    public Collection<RebeCustomBean> getRechteUndBelastungen() {
        return getN_rebes();
    }

    @Override
    public void setRechteUndBelastungen(final Collection<RebeCustomBean> val) {
        setN_rebes(val);
    }

    @Override
    public Collection<VertragCustomBean> getVertraege() {
        return getAr_vertraege();
    }

    @Override
    public void setVertraege(final Collection<VertragCustomBean> val) {
        setAr_vertraege(val);
    }

    @Override
    public Collection<FlurstueckSchluesselCustomBean> getVertraegeQuerverweise() {
//        if (vertrag_querverweise == null) {
//            final MetaObject[] querverweise = getExtension_vertrag_querverweise();
//            if (querverweise != null) {
//                final List<FlurstueckSchluesselCustomBean> tmpList = new ArrayList<FlurstueckSchluesselCustomBean>();
//                for (final MetaObject querverweis : querverweise) {
//                    tmpList.add((FlurstueckSchluesselCustomBean)querverweis.getBean());
//                }
//                setVertraegeQuerverweise(ObservableCollections.observableList(tmpList));
//            }
//        }
        return vertrag_querverweise;
    }

    @Override
    public void setVertraegeQuerverweise(final Collection<FlurstueckSchluesselCustomBean> val) {
        vertrag_querverweise = val;
    }

    @Override
    public Collection<FlurstueckSchluesselCustomBean> getMiPasQuerverweise() {
//        if (mipa_querverweise == null) {
//            final MetaObject[] querverweise = getExtension_mipa_querverweise();
//            if (querverweise != null) {
//                final List<FlurstueckSchluesselCustomBean> tmpList = new ArrayList<FlurstueckSchluesselCustomBean>();
//                for (final MetaObject querverweis : querverweise) {
//                    tmpList.add((FlurstueckSchluesselCustomBean)querverweis.getBean());
//                }
//                setMiPasQuerverweise(ObservableCollections.observableList(tmpList));
//            }
//        }
        return mipa_querverweise;
    }

    @Override
    public void setMiPasQuerverweise(final Collection<FlurstueckSchluesselCustomBean> val) {
        mipa_querverweise = val;
    }

    @Override
    public Collection<FlurstueckSchluesselCustomBean> getBaeumeQuerverweise() {
//        if (baum_querverweise == null) {
//            final MetaObject[] querverweise = getExtension_baum_querverweise();
//            if (querverweise != null) {
//                final List<FlurstueckSchluesselCustomBean> tmpList = new ArrayList<FlurstueckSchluesselCustomBean>();
//                for (final MetaObject querverweis : querverweise) {
//                    tmpList.add((FlurstueckSchluesselCustomBean)querverweis.getBean());
//                }
//                setBaeumeQuerverweise(ObservableCollections.observableList(tmpList));
//            }
//        }
        return baum_querverweise;
    }

    @Override
    public void setBaeumeQuerverweise(final Collection<FlurstueckSchluesselCustomBean> val) {
        baum_querverweise = val;
    }

    @Override
    public SpielplatzCustomBean getSpielplatz() {
        return getFk_spielplatz();
    }

    @Override
    public void setSpielplatz(final SpielplatzCustomBean val) {
        setFk_spielplatz(val);
    }

    @Override
    public String toString() {
        try {
            if ((getFlurstueckSchluessel().isEchterSchluessel() == null)
                        || getFlurstueckSchluessel().isEchterSchluessel()) {
                if (getFlurstueckSchluessel().getFlurstueckNenner() != null) {
                    return getFlurstueckSchluessel().getGemarkung().getBezeichnung() + " "
                                + getFlurstueckSchluessel().getFlur() + " "
                                + getFlurstueckSchluessel().getFlurstueckZaehler() + "/"
                                + getFlurstueckSchluessel().getFlurstueckNenner();
                } else {
                    return getFlurstueckSchluessel().getGemarkung().getBezeichnung() + " "
                                + getFlurstueckSchluessel().getFlur() + " "
                                + getFlurstueckSchluessel().getFlurstueckZaehler();
                }
            } else {
                return "pseudo Schluessel" + getFlurstueckSchluessel().getId();
            }
        } catch (Exception ex) {
            LOG.error("Eine oder mehrere Felder der Entität sind null", ex);
            return "Eine oder mehrere Felder der Entität sind null";
        }
    }
}
