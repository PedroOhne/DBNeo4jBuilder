package de.master.neo4jdbbuilder;

import org.neo4j.graphdb.Label;

/**
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */
public enum Entities implements Label {

	Patient,Outcome,ReportSource, Therapy, Indication
        ,ADRInfo,Drug,Reaction,DrugProduct, DrugProductIngredients,AdverseEvent, AdverseDrug, DrugIngridients,
        Link,
        YellowCard,
        DrugRoute,
        Medicine,
        Report,
        Source
        ; // Patient, AdverseEvent, Drug.
}