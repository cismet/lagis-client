/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagisEE.crossover;

import com.vividsolutions.jts.geom.Geometry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;

import de.cismet.lagisEE.crossover.entity.WfsFlurstuecke;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class LagisCrossover {

    //~ Instance fields --------------------------------------------------------

// @PersistenceContext
// private EntityManager em;
    private final String flurstueckQuery = "SELECT\n"
                + "*\n"
                + "FROM\n"
                + "wfs_flurstuecke f\n"
                + "WHERE\n"
                + "f.hist_ab is null\n"
                + "and intersects(f.the_geom,st_buffer(st_buffer(geomFromText(?,31466), ?), 0))";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   geom    DOCUMENT ME!
     * @param   buffer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    // TODO Jean ent-ejb-en
    public Set<WfsFlurstuecke> getIntersectingFlurstuecke(final Geometry geom, final double buffer) throws Exception {
        final HashSet<WfsFlurstuecke> result = new HashSet<WfsFlurstuecke>();
//        try {
//            if (geom != null) {
//                System.out.println("Intersection geometry: " + geom.toText());
//                // ToDo Attention default Geometry; List<WfsFlurstuecke> fkeys = (List<WfsFlurstuecke>)
//                // em.createNativeQuery( //ToDo optimize //"SELECT id,geometry FROM GeomToEntityIndex WHERE
//                // envelope(geometryfromtext(?,-1)) && geometry", GeomToEntityIndex.class).setParameter(1,
//                // bb.getGeometryFromTextLineString()).getResultList(); "SELECT * FROM wfs_flurstuecke f WHERE
//                // intersects(f.the_geom,geomFromText(?,31466))", WfsFlurstuecke.class).setParameter(1,
//                // geom.toText()).getResultList();
//                System.out.println("buffer: " + buffer);
//                final List<WfsFlurstuecke> fkeys = (List<WfsFlurstuecke>)em.createNativeQuery(
//
//                            // "SELECT id,geometry FROM GeomToEntityIndex WHERE envelope(geometryfromtext(?,-1)) &&
//                            // geometry", GeomToEntityIndex.class).setParameter(1,
//                            // bb.getGeometryFromTextLineString()).getResultList();
//                            flurstueckQuery,
//                            WfsFlurstuecke.class).setParameter(1, geom.toText()).setParameter(2, buffer)
//                            .getResultList();
//                if ((fkeys != null) && (fkeys.size() > 0)) {
//                    System.out.println("There are " + fkeys.size() + " intersecting wfsFlurstuecke.");
//                    result.addAll(fkeys);
//                } else {
//                    System.out.println("There are no intersecting wfsFlurstuecke.");
//                }
//            }
//        } catch (Exception ex) {
//            System.err.print("Error while getting intersecting Flurstuecke: ");
//            ex.printStackTrace();
//        }
        return result;
    }
}
