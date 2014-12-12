/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

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

    private Collection<FlurstueckSchluesselCustomBean> vertrag_querverweise;
    private Collection<FlurstueckSchluesselCustomBean> mipa_querverweise;
    private Collection<FlurstueckSchluesselCustomBean> baum_querverweise;

    private Integer id;
    private String bemerkung;
    private SpielplatzCustomBean fk_spielplatz;
    private FlurstueckSchluesselCustomBean fk_flurstueck_schluessel;
    private Boolean in_stadtbesitz;
    private Collection<BaumCustomBean> ar_baeume;
    private Collection<MipaCustomBean> ar_mipas;
    private Collection<VertragCustomBean> ar_vertraege;
    private Collection<VerwaltungsbereicheEintragCustomBean> n_verwaltungsbereiche_eintraege;
    private Collection<DmsUrlCustomBean> n_dms_urls;
    private Collection<NutzungCustomBean> n_nutzungen;
    private Collection<RebeCustomBean> n_rebes;
    private String[] PROPERTY_NAMES = new String[] {
            "id",
            "bemerkung",
            "fk_spielplatz",
            "fk_flurstueck_schluessel",
            "in_stadtbesitz",
            "ar_baeume",
            "ar_mipas",
            "ar_vertraege",
            "n_verwaltungsbereiche_eintraege",
            "n_dms_urls",
            "n_nutzungen",
            "n_rebes"
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
    public Collection<VerwaltungsbereicheEintragCustomBean> getN_verwaltungsbereiche_eintraege() {
        return this.n_verwaltungsbereiche_eintraege;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VerwaltungsbereicheEintragCustomBean> getSortedVerwaltungsbereicheEintraege() {
        final List<VerwaltungsbereicheEintragCustomBean> list = new ArrayList<VerwaltungsbereicheEintragCustomBean>();
        for (final VerwaltungsbereicheEintragCustomBean eintrag : getN_verwaltungsbereiche_eintraege()) {
            list.add(eintrag);
        }
        Collections.sort(list, new Comparator<VerwaltungsbereicheEintragCustomBean>() {

                @Override
                public int compare(final VerwaltungsbereicheEintragCustomBean o1,
                        final VerwaltungsbereicheEintragCustomBean o2) {
                    if (o1.getGeaendert_am() != null) {
                        if (o2.getGeaendert_am() == null) {
                            return 1;
                        } else {
                            return o1.getGeaendert_am().compareTo(o2.getGeaendert_am());
                        }
                    } else {
                        return o1.getId().compareTo(o2.getId());
                    }
                }
            });
        return list;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  n_verwaltungsbereiche_eintraege  paramCollection DOCUMENT ME!
     */
    public void setN_verwaltungsbereiche_eintraege(
            final Collection<VerwaltungsbereicheEintragCustomBean> n_verwaltungsbereiche_eintraege) {
        final Object old = this.n_verwaltungsbereiche_eintraege;
        this.n_verwaltungsbereiche_eintraege = n_verwaltungsbereiche_eintraege;

        this.propertyChangeSupport.firePropertyChange(
            "n_verwaltungsbereiche_eintraege",
            old,
            this.n_verwaltungsbereiche_eintraege);
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
        final VerwaltungsbereicheEintragCustomBean[] v = getSortedVerwaltungsbereicheEintraege().toArray(
                new VerwaltungsbereicheEintragCustomBean[0]);
        if (v.length <= 0) {
            return null;
        } else {
            return v[v.length - 1].getN_verwaltungsbereiche();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VerwaltungsbereicheEintragCustomBean> getVerwaltungsbereicheHistorie() {
        final VerwaltungsbereicheEintragCustomBean[] v = getSortedVerwaltungsbereicheEintraege().toArray(
                new VerwaltungsbereicheEintragCustomBean[0]);
        if (v.length <= 1) {
            return Arrays.asList(new VerwaltungsbereicheEintragCustomBean[0]);
        } else {
            final VerwaltungsbereicheEintragCustomBean[] h = new VerwaltungsbereicheEintragCustomBean[v.length - 1];
            System.arraycopy(v, 0, h, 0, h.length);
            return Arrays.asList(h);
        }
    }

    @Override
    public void setVerwaltungsbereiche(final Collection<VerwaltungsbereichCustomBean> bereiche) {
        final Collection<VerwaltungsbereichCustomBean> b = getVerwaltungsbereiche();
        final VerwaltungsbereichCustomBean[] lastBereiche = (b == null)
            ? new VerwaltungsbereichCustomBean[0] : b.toArray(
                new VerwaltungsbereichCustomBean[0]);
        final VerwaltungsbereichCustomBean[] currentBereiche = bereiche.toArray(new VerwaltungsbereichCustomBean[0]);
        final int verwaltungsBereicheSize = (b == null) ? 0 : b.size();
        boolean oneOrMoreChanged = bereiche.size() != verwaltungsBereicheSize;
        if (!oneOrMoreChanged) {
            for (int index = 0; index < lastBereiche.length; index++) {
                final VerwaltungsbereichCustomBean lastBereich = lastBereiche[index];
                final VerwaltungsbereichCustomBean currentBereich = currentBereiche[index];

                if (lastBereich.getFk_verwaltende_dienststelle() != currentBereich.getFk_verwaltende_dienststelle()) {
                    oneOrMoreChanged = true;
                }
                if (lastBereich.getFk_verwaltungsgebrauch() != currentBereich.getFk_verwaltungsgebrauch()) {
                    oneOrMoreChanged = true;
                }
                if (lastBereich.getFlaeche() != null) { // null ignorieren weil vor der umstellung flaeche immer null
                                                        // ist
                    // und erst gefüllt werden muss
                    if (((lastBereich.getFlaeche() != null)
                                    && !lastBereich.getFlaeche().equals(currentBereich.getFlaeche()))
                                || ((currentBereich.getFlaeche() != null)
                                    && !currentBereich.getFlaeche().equals(lastBereich.getFlaeche()))) {
                        oneOrMoreChanged = true;
                    }
                } else {
                    lastBereich.setFlaeche(index);
                }
                final Geometry lastGeometry = lastBereich.getGeometry();
                final Geometry currentGeometry = currentBereich.getGeometry();
                if (((lastGeometry != null) && !lastGeometry.equals(currentGeometry))
                            || ((currentGeometry != null) && !currentGeometry.equals(lastGeometry))) {
                    oneOrMoreChanged = true;
                }
            }
        }

        if (oneOrMoreChanged) {
            final VerwaltungsbereicheEintragCustomBean eintrag = VerwaltungsbereicheEintragCustomBean.createNew();
            eintrag.setGeaendert_am(new Date());
            eintrag.setGeaendert_von(LagisBroker.getInstance().getAccountName());
            eintrag.getN_verwaltungsbereiche().addAll(bereiche);
            getN_verwaltungsbereiche_eintraege().add(eintrag);
        }
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
        return vertrag_querverweise;
    }

    @Override
    public void setVertraegeQuerverweise(final Collection<FlurstueckSchluesselCustomBean> val) {
        vertrag_querverweise = val;
    }

    @Override
    public Collection<FlurstueckSchluesselCustomBean> getMiPasQuerverweise() {
        return mipa_querverweise;
    }

    @Override
    public void setMiPasQuerverweise(final Collection<FlurstueckSchluesselCustomBean> val) {
        mipa_querverweise = val;
    }

    @Override
    public Collection<FlurstueckSchluesselCustomBean> getBaeumeQuerverweise() {
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
            if (getFlurstueckSchluessel().isEchterSchluessel()) {
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
            final String msg = "Eine oder mehrere Felder der Entität sind null";
            LOG.error(msg, ex);
            return msg;
        }
    }
}
