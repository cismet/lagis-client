/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.test;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaCustomBean;
import de.cismet.cids.custom.beans.lagis.RebeCustomBean;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author jruiz
 */
public class TestOjectsWithGeomToJson {

    public static void main(String[] args) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final SimpleModule module = new SimpleModule();
            mapper.registerModule(module);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

            final ConnectionContext context = ConnectionContext.createDummy();

            DevelopmentTools.showSimpleLoginDialog("http://localhost:9986/callserver/binary", LagisConstants.DOMAIN_LAGIS, true, context);

            LagisBroker.getInstance().setDomain(LagisConstants.DOMAIN_LAGIS);

            final MetaClass mcFlurstueck = CidsBroker.getInstance().getLagisMetaClass(LagisMetaclassConstants.FLURSTUECK);
            final MetaClass mcAlkisFlurstueck = CidsBroker.getInstance().getLagisMetaClass("ALKIS_FLURSTUECK");

            //final List<Integer> multiVerwaltungsBereichFlurstueckIds = Arrays.asList(/*17778,20236,13469,18299,21637,23285,18082,22342,10943,2589,5819,22343,22303,18829,12289,20777,18506,18684,13116,10469,2936,2581,2633,22479,20702,19090,13051,18557,21707,8632,22139,22341,20504,1190,5718,9179,2941,2197,22728,2506,22654,18897,20779,17740,12479,21555,21329,21976,22796,9737,19766,21550,1137,22983,22299,4526,5381,5471,21818,21786,6704,21439,21253,12286,21138,12444,17649,18388,9841,19706,20607,21369,4416,8633,22138,8391,6952,8001,11612,11490,990,1166,1184,6182,9446,2126,9240,21133,7923,21456,*/13123/*,12445*/);
            final List<Integer> multiVerwaltungsBereichFlurstueckIds = Arrays.asList(22726);

            for (final Integer flurstueckId : multiVerwaltungsBereichFlurstueckIds) {                        
                final FlurstueckCustomBean flurstueck = (FlurstueckCustomBean)CidsBroker.getInstance().getMetaObject(flurstueckId, mcFlurstueck.getId(), LagisConstants.DOMAIN_LAGIS).getBean();
                final FlurstueckSchluesselCustomBean flurstueckSchluessel = flurstueck.getFlurstueckSchluessel();

                final String alkisFlurstueckQuery = String.format("SELECT %d, id FROM alkis_flurstueck WHERE fk_schluessel = %d LIMIT 1;",
                        mcAlkisFlurstueck.getID(),
                        flurstueckSchluessel.getId()
                    );

                final MetaObject[] mosAlkisFlurstueck = CidsBroker.getInstance().getLagisMetaObject(alkisFlurstueckQuery);
                final CidsBean alkisFlurstueck = mosAlkisFlurstueck != null && mosAlkisFlurstueck.length > 0 ? 
                        mosAlkisFlurstueck[0].getBean() 
                        : null;
                final Geometry flurstueckGeometrie = (Geometry)alkisFlurstueck.getProperty("geometrie");

                final Collection<MipaCustomBean> mipas = LagisBroker.getInstance().getMiPas(flurstueckGeometrie);
                final Collection<RebeCustomBean> rebes = LagisBroker.getInstance().getRechteUndBelastungen(flurstueckGeometrie);

                final String historyDot = LagisBroker.getInstance().getHistoryGraph(flurstueck, 
                                    LagisBroker.HistoryLevel.DIRECT_RELATIONS,
                                    0,
                                    LagisBroker.HistorySibblingLevel.NONE,
                                    0,
                                    LagisBroker.HistoryType.BOTH,
                                    null);

                final String flurstueckJson = flurstueck.toJSONString(false);            
                final String mipasJson = CidsBean.toJSONString(false, (Collection)mipas);
                final String rebesJson = CidsBean.toJSONString(false, (Collection)rebes);

                final Map<String, Object> flurstueckMap = mapper.readValue(
                        flurstueckJson, 
                        new TypeReference<Map<String, Object>>() {}
                );
                final List<Map> mipasMap = mapper.readValue(
                        mipasJson, 
                        new TypeReference<List<Map>>() {}
                );
                final List<Map> rebesMap = mapper.readValue(
                        rebesJson, 
                        new TypeReference<List<Map>>() {}
                );

                flurstueckMap.put("historyDot", historyDot);
                flurstueckMap.put("mipas", mipasMap);
                flurstueckMap.put("rebes", rebesMap);

                final String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(flurstueckMap);
                try (final OutputStream out = new FileOutputStream("/tmp/fs_" + flurstueckId + ".json")) {
                    IOUtils.write(prettyJson, out, "UTF-8");
                }
                System.exit(0);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}

/* -- flurstuecke mit mehreren verwaltungsbereichen
select 
	sub.flurstueck_id,
	gemarkung.bezeichnung || ' ' 
	|| flurstueck_schluessel.flur || '-' 
	|| flurstueck_schluessel.flurstueck_zaehler || '/' 
	|| flurstueck_schluessel.flurstueck_nenner,
	sub.anzahl_verwaltungsbereiche
from flurstueck_schluessel
left join gemarkung on flurstueck_schluessel.fk_gemarkung = gemarkung.id
left join (
	select 
		flurstueck.id as flurstueck_id, 
		flurstueck.fk_flurstueck_schluessel as flurstueck_schluessel_id, 
		count(*) as anzahl_verwaltungsbereiche
	from flurstueck_schluessel 
	left join flurstueck on flurstueck.fk_flurstueck_schluessel = flurstueck_schluessel.id
	left join verwaltungsbereiche_eintrag on flurstueck.id = verwaltungsbereiche_eintrag.fk_flurstueck
	left join verwaltungsbereich on verwaltungsbereiche_eintrag.id = verwaltungsbereich.fk_verwaltungsbereiche_eintrag
	group by flurstueck.id, flurstueck.fk_flurstueck_schluessel
	having count(*) > 4
) as sub on sub.flurstueck_schluessel_id = flurstueck_schluessel.id
where sub.anzahl_verwaltungsbereiche > 0
order by sub.anzahl_verwaltungsbereiche desc;

17778;"Elberfeld 30-265/0";70
20236;"Elberfeld 240-200/0";48
13469;"Nächstebreck 441-19/0";48
18299;"Vohwinkel 28-1345/0";30
21637;"Barmen 212-322/0";25
23285;"Nächstebreck 412-222/0";20
18082;"Langerfeld 473-21/0";20
22342;"Vohwinkel 29-403/0";20
10943;"Cronenberg 4-2418/839";18
2589;"Barmen 381-330/0";15
5819;"Elberfeld 351-40/0";15
22343;"Vohwinkel 28-1364/0";14
22303;"Vohwinkel 28-1363/0";13
18829;"Elberfeld 335-48/0";12
12289;"Langerfeld 473-22/0";12
20777;"Barmen 70-97/0";12
18506;"Elberfeld 217-40/0";10
18684;"Barmen 72-157/0";10
13116;"Nächstebreck 412-184/0";10
10469;"Cronenberg 11-1764/0";10
2936;"Barmen 532-9/0";9
2581;"Barmen 381-1/0";9
2633;"Barmen 384-275/0";9
22479;"Vohwinkel 28-1366/0";9
20702;"Barmen 110-67/0";9
19090;"Barmen 72-159/0";9
13051;"Nächstebreck 408-88/0";9
18557;"Langerfeld 452-85/0";9
21707;"Neukirchen 999-1999/0";9
8632;"Ronsdorf 66-115/0";9
22139;"Barmen 3-485/0";9
22341;"Vohwinkel 29-402/0";9
20504;"Ronsdorf 14-726/0";8
1190;"Barmen 220-88/0";8
5718;"Elberfeld 338-88/0";8
9179;"Vohwinkel 21-29/0";8
2941;"Barmen 5-353/0";8
2197;"Barmen 3-39/0";8
22728;"Vohwinkel 21-505/0";8
2506;"Barmen 376-88/0";8
22654;"Barmen 129-129/0";8
18897;"Barmen 74-261/0";8
20779;"Elberfeld 45-85/0";8
17740;"Barmen 5-381/0";8
12479;"Langerfeld 490-413/0";8
21555;"Vohwinkel 20-88/0";7
21329;"Elberfeld 249-104/0";7
21976;"Cronenberg 4-4299/0";7
22796;"Vohwinkel 7-3107/0";7
9737;"Vohwinkel 6-5812/0";7
19766;"Vohwinkel 28-1349/0";7
21550;"Vohwinkel 21-400/0";7
1137;"Barmen 217-194/0";6
22983;"Vohwinkel 28-1400/0";6
22299;"Barmen 201-257/0";6
4526;"Elberfeld 226-88/0";6
5381;"Elberfeld 284-55/0";6
5471;"Elberfeld 298-140/0";6
21818;"Cronenberg 4-2756/414";6
21786;"Cronenberg 4-2755/416";6
6704;"Elberfeld 443-90/0";6
21439;"Vohwinkel 29-372/0";6
21253;"Barmen 70-103/0";6
12286;"Langerfeld 473-160/0";6
21138;"Barmen 213-527/0";6
12444;"Langerfeld 489-110/0";6
17649;"Langerfeld 489-267/0";6
18388;"Elberfeld 251-88/0";6
9841;"Vohwinkel 66-2954/0";6
19706;"Vohwinkel 20-91/0";6
20607;"Elberfeld 7-1074/0";6
21369;"Vohwinkel 26-264/0";6
4416;"Elberfeld 217-23/13";6
8633;"Ronsdorf 66-116/0";6
22138;"Barmen 4-885/0";6
8391;"Ronsdorf 4-2028/0";6
6952;"Elberfeld 459-572/0";5
8001;"Ronsdorf 16-293/0";5
11612;"Cronenberg 95-48/0";5
11490;"Cronenberg 8-1982/754";5
990;"Barmen 212-129/0";5
1166;"Barmen 220-104/0";5
1184;"Barmen 220-8/0";5
6182;"Elberfeld 409-34/0";5
9446;"Vohwinkel 30-49/0";5
2126;"Barmen 335-236/0";5
9240;"Vohwinkel 26-229/0";5
21133;"Barmen 212-318/0";5
7923;"Ronsdorf 1-1382/0";5
21456;"Barmen 78-99/0";5
13123;"Nächstebreck 412-202/0";5
12445;"Langerfeld 489-143/0";5
*/

/* -- flurstuecke mit mehreren mipa
select 
	sub.flurstueck_id,
	gemarkung.bezeichnung || ' ' 
	|| flurstueck_schluessel.flur || '-' 
	|| flurstueck_schluessel.flurstueck_zaehler || '/' 
	|| flurstueck_schluessel.flurstueck_nenner,
	sub.anzahl_verwaltungsbereiche
from flurstueck_schluessel
left join gemarkung on flurstueck_schluessel.fk_gemarkung = gemarkung.id
left join (
	select 
		flurstueck.id as flurstueck_id, 
		flurstueck.fk_flurstueck_schluessel as flurstueck_schluessel_id, 
		count(*) as anzahl_verwaltungsbereiche
	from flurstueck_schluessel 
	left join flurstueck on flurstueck.fk_flurstueck_schluessel = flurstueck_schluessel.id
	left join verwaltungsbereiche_eintrag on flurstueck.id = verwaltungsbereiche_eintrag.fk_flurstueck
	left join verwaltungsbereich on verwaltungsbereiche_eintrag.id = verwaltungsbereich.fk_verwaltungsbereiche_eintrag
	group by flurstueck.id, flurstueck.fk_flurstueck_schluessel
	having count(*) > 4
) as sub on sub.flurstueck_schluessel_id = flurstueck_schluessel.id
where sub.anzahl_verwaltungsbereiche > 0
order by sub.anzahl_verwaltungsbereiche desc;

18415;"Beyenburg 13-915/0";13
21036;"Barmen 9-230/0";9
22385;"Ronsdorf 1-1497/0";8
20952;"Vohwinkel 22-114/0";6
22350;"Elberfeld 251-89/0";6
21819;"Barmen 218-127/0";6
*/

/*
select 
	sub.flurstueck_id,
	gemarkung.bezeichnung || ' ' 
	|| flurstueck_schluessel.flur || '-' 
	|| flurstueck_schluessel.flurstueck_zaehler || '/' 
	|| flurstueck_schluessel.flurstueck_nenner,
	sub.anzahl_rebe
from flurstueck_schluessel
left join gemarkung on flurstueck_schluessel.fk_gemarkung = gemarkung.id
left join (
	select 
		flurstueck.id as flurstueck_id, 
		alkis_flurstueck.fk_schluessel as flurstueck_schluessel_id, 
		count(*) as anzahl_rebe
	from 
		flurstueck left join flurstueck_schluessel on flurstueck.fk_flurstueck_schluessel = flurstueck_schluessel.id
		left join alkis_flurstueck on alkis_flurstueck.fk_schluessel = flurstueck_schluessel.id,		
		rebe left join geom as rebe_geom on rebe.fk_geom = rebe_geom.id
	where 
		st_area(alkis_flurstueck.geometrie) > 100000 AND
		st_intersects(rebe_geom.geo_field, st_buffer(alkis_flurstueck.geometrie, -1)) 
	group by flurstueck.id, alkis_flurstueck.fk_schluessel
	having count(*) > 4
) as sub on sub.flurstueck_schluessel_id = flurstueck_schluessel.id
where sub.anzahl_rebe > 0
order by sub.anzahl_rebe desc;

22726;"Vohwinkel 20-95/0";30
19130;"Cronenberg 2-4346/0";20
22404;"Ronsdorf 37-81/0";8
19269;"Cronenberg 9-792/0";8
18415;"Beyenburg 13-915/0";6
21329;"Elberfeld 249-104/0";5
10798;"Cronenberg 2-4188/0";5
*/