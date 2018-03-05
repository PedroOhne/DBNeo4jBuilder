package de.master.neo4jdbbuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */
public final class PostProcessor implements Properties {

    GraphDatabaseService db;

    public PostProcessor(File f) throws IOException {

        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        db = dbFactory.newEmbeddedDatabase(f);
        searchLinkages();
    }

    public void searchLinkages() {

        HashMap<String, HashSet<Long>> meddra_to_all_reaction_nodes = new HashMap<>();
        HashMap<String, Long> adverseevent_map = new HashMap<>();

        try (Transaction tx = db.beginTx()) {
            ResourceIterator<Node> reaction_nodes = db.findNodes(Entities.Reaction);
            reaction_nodes = db.findNodes(Entities.Reaction);
            Stream<Node> nodess = reaction_nodes.stream();
            for (Object node : nodess.toArray()) {
                Node n = (Node) node;
                Long key_id = n.getId();
                String base_pt = "";
                Map<String, Object> propps = n.getAllProperties();
                int size_of_props = propps.size();
                switch (size_of_props) {
                    case 10:
                        base_pt = n.getProperty("pt_name_eng").toString();
                        break;
                    case 3:
                        base_pt = n.getProperty("pt").toString();
                        break;
                }

                if (meddra_to_all_reaction_nodes.containsKey(base_pt)) {
                    HashSet<Long> get_set = meddra_to_all_reaction_nodes.get(base_pt);
                    get_set.add(key_id);
                    meddra_to_all_reaction_nodes.put(base_pt, get_set);
                } else {
                    HashSet<Long> new_set = new HashSet();
                    new_set.add(key_id);
                    meddra_to_all_reaction_nodes.put(base_pt, new_set);
                }
            }
            meddra_to_all_reaction_nodes.remove("");
            tx.success();
        }
        System.out.println("Size of Identifier Reaction Terms: " + meddra_to_all_reaction_nodes.size());

        /**
         * 2nd Step. Connect all Meddra-ReactionTerms with new created
         * "AdverseEvent" Nodes. Fill HashMap with the new created AdverseEvent
         * (For Step 3.)
         */
        for (Entry<String, HashSet<Long>> entry : meddra_to_all_reaction_nodes.entrySet()) {
            Tools.startTime();

            String adverse_reaction = entry.getKey();
            HashSet<Long> ids_reactions = entry.getValue();
            try (Transaction tx = db.beginTx()) {
                Node adverse_event = db.createNode(Entities.AdverseEvent);
                adverse_event.setProperty(Properties.AdverseReactionName, adverse_reaction);
                for (Long ids_reaction : ids_reactions) {
                    Node node_by_id = db.getNodeById(ids_reaction);
                    adverse_event.createRelationshipTo(node_by_id, EntitiesRelationships.ADVERSE_EVENT_REACTION);
                }
                adverseevent_map.put(adverse_reaction, adverse_event.getId());
                tx.success();
            }
            Tools.printPassedTime(adverse_reaction + " for ... " + ids_reactions.size() + " Nodes.");
        }

        /**
         * 3rd Step. Search ADRInfo. Connect AdverseEvent Nodes with ADRInfo.
         */
        int counter_adrInfo = 0;
        int matches = 0;
        try (Transaction tx = db.beginTx()) {
            ResourceIterator<Node> getAllAdrInfos = db.findNodes(Entities.ADRInfo);
            while (getAllAdrInfos.hasNext()) {
                counter_adrInfo++;
                Node adrInfo = getAllAdrInfos.next();
                String pt_name = adrInfo.getProperty(Properties.ADR_TERM.toLowerCase()).toString();
                if (adverseevent_map.containsKey(pt_name)) {
                    matches++;
                    Node adverse_event = db.getNodeById(adverseevent_map.get(pt_name));
                    adverse_event.createRelationshipTo(adrInfo, EntitiesRelationships.ADVERSE_EVENT_ADRINFO);
                } else {
                    /*No Match with ADRInfo*/
                }
            }
            tx.success();
        }
        System.out.println("ADRinfos: " + counter_adrInfo);
        System.out.println("Connections: " + matches);

        db.shutdown();
    }

}
