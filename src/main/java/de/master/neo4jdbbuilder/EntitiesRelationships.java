package de.master.neo4jdbbuilder;

/**
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */
import org.neo4j.graphdb.RelationshipType;

public enum EntitiesRelationships implements RelationshipType {

    PATIENT_DRUG,               /*[1..n] DRUGS linked to one DEMOGRAPHIC*/
    PATIENT_REPORT_SOURCES,     /*[0..n] REPORT_SOURCES linked to one DEMOGRAPHIC*/
    PATIENT_OUTCOME,            /*[0..n] OUTCOMES linked to one DEMOGRAPHIC*/
    DRUG_THERAPY,               /*[0..n] THERAPIES linked to one DRUG*/
    DRUG_INDICATION,            /*[0..n] INDICATIONS linked to one DEMOGRAPHIC*/
    INDICATION_PRODUCT,         /*[0..n] INDICATIONS linked to one DRUGPRODUCT*/
    ADRINFO_DRUG, DRUG_ADRINFO, 
    PATIENT_REACTION,           /*[1..n] REACTIONS linked to one DEMOGRAPHIC*/
    DRUG_REACTION,              /*[0..n] INDICATIONS linked to one DEMOGRAPHIC*/
    PRODUCT_INGREDIENTS,
    PRODUCT_DRUG, PATIENT_LINK,
    ADVERSE_EVENT_REACTION,
    ADVERSE_EVENT_ADRINFO,
    ADVERSE_EVENT_DRUGROUTE,
    ADVERSE_EVENT_PATIENT,
    PATIENT_DRUGROUTE,
    
    
    YELLOWCARD_DRUG,
    YELLOWCARD_PATIENT,
    YELLOWCARD_ADVERSE_EVENT,
    
    
    MEDICINE_PATIENT,
    ADVERSE_EVENT_MEDICINE,
    MEDICINE_ADVERSE_EVENT,
    PATIENT_ADVERSE_EVENT,
    
    PROVIDES_SpR,
    DESCRIBES_RdP,
    PRESENTS_PpR,
    TAKES_PtD,
    HAS_DhI,
    HAS_PhO,
    DEPENDS_DdR
    ;

}
