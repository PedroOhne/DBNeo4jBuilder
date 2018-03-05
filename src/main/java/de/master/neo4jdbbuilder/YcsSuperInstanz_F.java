/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import java.util.HashMap;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author maestro
 */
public class YcsSuperInstanz_F {

    /**
     * map contains all properties of the canadian data extract. 0 = patient, 1
     * =
     */
    private final HashMap<Integer, String[]> canadian_prop_map = new HashMap<>();
    GraphDatabaseService db_s;
    static String[] report = {"primaryid", "caseid", "caseversion", "i_f_code", "mfr_dt",
        "init_fda_dt", "fda_dt", "rept_cod", "auth_num", "mfr_num", "mfr_sndr", "lit_ref",
        "rept_dt", "to_mfr", "occp_cod", "reporter_country", "occr_country"};

    // auth_numb 14q2 X
    static String[] patient = {"primaryid", "caseid", "age", "age_cod", "age_grp", "age_10",
        "sex", "weight", "weight_cod", "height", "height_cod"};

    // age_grp 14q2 X
    static String[] outcome = {"outc_cod", "seriousness_cod", "death",
        "disability", "cong_abnorm", "life_t", "hosp_req", "ot_med_cond"};

    static String[] source = {"rpsr_cod", "source_cod"};

    static String[] drug = {"drug_seq", "role_cod", "drugname", "prod_ai",
        "val_vbm", "route", "dose_vbm", "cum_dose_chr", "cum_dose_unit", "dechal",
        "rechal", "lot_num", "exp_dt", "nda_num", "dose_amt", "dose_unit", "dose_form", "freq_d"};

    static String[] indication = {"drug_seq", "indi_pt"};

    static String[] reaction = {"pt", "drug_rec_act", "soc_name", "reac_seq",
        "date_adverse_event", "meddra_v", "duration", "duration_unit"};

    static String[] therapy = {"drug_seq", "start_dt", "end_dt",
        "dur", "dur_cod"};

    public YcsSuperInstanz_F() {
    }

    public String[] getTherapyProperties() {
        return therapy;
    }

    public String[] getReportProperties() {
        return report;
    }

    public String[] getOutcomeProperties() {
        return outcome;
    }

    public String[] getSourceProperties() {
        return source;
    }

    public String[] getPatientProperties() {
        return patient;
    }

    public String[] getReactionProperties() {
        return reaction;
    }

    public String[] getReportDrugProperties() {
        return drug;
    }

    public String[] getReportDrugIndicationProperties() {
        return indication;
    }
}
