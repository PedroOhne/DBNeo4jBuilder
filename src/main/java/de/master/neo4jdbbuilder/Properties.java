package de.master.neo4jdbbuilder;

/**
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */
public interface Properties {

    /**
     * For Test.
     */
    int max_test = 25;

    /**
     * In General.
     */
    
    String basic_path_parser_settings = "parser/parser_info.txt";
    String information_not_available = "n/a";
    String basic_path_database = "database";
    String UNIQUE_SOURCE = "unique_source";
    String UNQIUE_DRUGBANK_ID = "db_id";
    String AdverseReactionName = "adverse reaction";
    String db_name_main = "DB_MAIN";
    String präfix_ycs = "YCS_";
    String präfix_usa = "USA_";
    String präfix_can = "CAN_";
    String config_path_dir = "config";
    String config_path_file = "c_all.txt";
    
    String config_redundancy_file = "redundancies.txt";
    String config_database_internal = "db_config.txt";
    

    String config_entity_patient = "Patient";
    String config_entity_drug = "Drug";
    String config_entity_report = "Report";
    String config_entity_outcome = "Outcome";
    String config_entity_reaction = "Reaction";
    String config_entity_therapy = "Therapy";
    String config_entity_indication = "Indication";
    String config_entity_reportsource = "Source";

    /**
     * YCS.
     */
    String ycs_database_name = "DB_YCS";
    String ycs_dir = "YCS";
    String ycs_external_links = "https://info.mhra.gov.uk/drug-analysis-profiles/data/dap_UK_EXTERNAL_json.zip";
    String ycs_basic_url = "https://info.mhra.gov.uk/drug-analysis-profiles/data";
    String ycs_basic_beginn = "UK_NON_";
    String ycs_basic_end_case = "_case.csv";
    String ycs_basic_end_drug = "_drug.csv";
    String ycs_basic_end_event = "_event.csv";
    String ycs_basic_end_info = "_info.csv";
    String ycs_basic_end_summary = "_summary.csv";
    String ycs_help_data = "mhra_idap_csv_help.txt";

    String ycs_prop_case = "CASE.csv";
    String ycs_prop_drug = "DRUG.csv";
    String ycs_prop_event = "EVENT.csv";
    String ycs_prop_info = "INFO.csv";
    String ycs_prop_summary = "SUMMARY.csv";

    String ycs_add_medicine = "medicine";
    String ycs_meddra_version = "MEDDRA_VERSION";

    /**
     * UK.
     */
    String uk_path = "CopyofFinalrepository_DLP30Jun2015_2.xls";
    String db_name_uk = "DB_EU_MEDICINE_AGENCY";
    String basic_path_uk = "UK";
    String unique_id_uk = "YCS";
    String uk_drug_id = "drug_id";
    String uk_adr_id = "adr_id";

    /**
     * USA.
     */
    String db_name_us = "DB_FEARS2Q17";
    String basic_path_us = "ascii";
    String unique_id_usa = "USA";

    String fusion_canada_height = "height";
    String fusion_canada_height_cod = "height_cod";
    String fusion_usa_weight_cod = "weight_cod";
    String fusion_usa_weight = "weight";
    String fusion_canada_seriousness = "seriousness_cod";
    String fusion_usa_date_adr = "date_adverse_event";
    String fusion_usa_age_group = "age_group";
    String fusion_canada_source_code = "source_cod";
    String fusion_canada_source = "source_eng";
    String fusion_canada_meddra_version = "meddra_v";
    String fusion_canada_duration = "duration";
    String fusion_canada_duration_unit = "duration_unit";
    String fusion_ycs_reac_seq = "reac_seq";
    String fusion_soc_name = "soc_name";

    String fusion_ycs_age_10 = "age_10";

    String fusion_o_death = "death";
    String fusion_o_dis = "disability";
    String fusion_o_congential_abn = "cong_abnorm";
    String fusion_o_life_t = "life_t";
    String fusion_o_hosp_req = "hosp_req";
    String fusion_o_ot_med_cond = "ot_med_cond";

    String fusion_drug_freq = "freq_d";

    String usa_14q2_patient_gender = "sex";

    String usa_14q2_patient_age_group = "age_grp";
    String usa_14q2_drug_reac_act = "drug_rec_act";
    String usa_14q2_drug_prod_ai = "prod_ai";
    String usa_14q1_report_lit_ref = "lit_ref";
    String usa_14q1_report_auth_numb = "auth_num";

    /**
     * Classification System.
     */
    String basic_path_cs = "clssystm";
    String db_name_classificiation_system = "DB_CS";
    String cs_adr = "ADReCS_ADR_info.xml";
    String cs_drug = "ADReCS_Drug_info.xml";
    String ADR_entry = "ADR_BADD";
    String ADR_entry_id = "ADReCS_ID";
    String ADR_entry_synonyms = "ADR_SYNONYMS";
    String ADR_entry_syn = "SYNONYM";
    String ADR_entry_description = "ADR_DESCRIPTION";
    String ADR_entry_whoARTcode = "WHO_ART_CODE";
    String ADR_entry_MEDDRAcode = "MEDDRA_CODE";
    String ADR_entry_drugs = "Drugs";
    String ADR_entry_drug = "Drug";
    String ADR_entry_drug_id = "DRUG_ID";
    String ADR_entry_drug_name = "DRUG_NAME";
    String DRUG_BADD_START_END = "Drug_BADD";
    String DRUG_ID = "DRUG_ID";
    String DRUG_NAME = "DRUGNAME";
    String DRUG_DESCRIPTION = "DESCRIPTION";
    String DRUG_ATC = "ATC";
    String DRUG_SYNS = "DRUG_SYNONYMS";
    String DRUG_SYN = "SYNONYM";  //liste
    String DRUG_INDI = "INDICATIONS";
    String DRUG_CAS = "CAS";
    String ADR_TERM = "ADR_TERM";
    String ADR_ID = "ADRECS_ID";
    String ADR_FREQ = "FREQUENCY";

    /**
     * Canada.
     */
    String outputFolder = "output";
    String property_drug_product = "DRUG_PRODUCT_ID";
    
    String canada_main_zip = "https://www.canada.ca/en/health-canada/services/drugs-health-products/medeffect-canada/"
            + "adverse-reaction-database/canada-vigilance-online-database-data-extract.html";
    
    String canada_download_url = "https://www.canada.ca/content/dam/hc-sc/migration/hc-sc/"
            + "dhp-mps/alt_formats/zip/medeff/databasdon/cvponline_extract_20170930.zip";
   
    String canada_property_url = "https://www.canada.ca/en/health-canada/"
            + "services/drugs-health-products/medeffect-canada/adverse-reaction-database/"
            + "canada-vigilance-adverse-reaction-online-database-data-structures.html";
    
    String canada_own_property_file = "pre_canada.txt";
    
    String db_name_canada = "DB_CANADA";
    String unique_id_canada = "CAN";
    
    String canada_url_check = "www.canada.ca";

}
