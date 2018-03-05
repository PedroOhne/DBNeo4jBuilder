/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import static de.master.neo4jdbbuilder.Properties.property_drug_product;
import java.util.ArrayList;
import java.util.HashMap;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

/**
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */
 class CanadaThreadREST extends CanadaSuperInstanz implements Runnable {

    private final CanadaExtendedNode cen;

    public CanadaThreadREST(CanadaExtendedNode cen, HashMap<Integer, String[]> props, GraphDatabaseService db) {
        super(props,db);
        this.cen = cen;
    }

    @Override
    public void run() {
        Tools.startTime();
        try (Transaction tx = db_s.beginTx()) {
            Node restNode = db_s.createNode(Entities.DrugProduct);
            String[] property = cen.getProperty();
            for (int i = 0; i < property.length; i++) {
                restNode.setProperty(getDrugProductProperties()[i], Tools.ConvertsString(property[i]));
            }
            ArrayList<String> ingridients_props = cen.getProps_ingridients();
            for (String ingridients_prop : ingridients_props) {
                Node restbNode = db_s.createNode(Entities.DrugProductIngredients);
                String[] props = Tools.returnCanadaProperyOneLineArray(ingridients_prop);
                for (int i = 0; i < props.length; i++) {
                    restbNode.setProperty(getDrugProdcutIngredientsProperties()[i], Tools.ConvertsString(props[i]));
                }
                restNode.createRelationshipTo(restbNode, EntitiesRelationships.PRODUCT_INGREDIENTS);
            }

            ResourceIterator<Node> paretns = db_s.findNodes(Entities.Drug, property_drug_product, property[0]);
            while (paretns.hasNext()) {
                Node next = paretns.next();
                restNode.createRelationshipTo(next, EntitiesRelationships.PRODUCT_DRUG);
            }
            tx.success();

        }
        Tools.printPassedTime(cen.getDrug_pro_id());

    }

}
