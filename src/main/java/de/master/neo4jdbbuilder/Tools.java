package de.master.neo4jdbbuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang3.SystemUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import scala.reflect.io.Directory;

/**
 *
 */
public class Tools {

    static long startTime;
    static long estimatedTime;
    static double seconds;
    List<String> fileList;
    static String loading_graphic = "[]";
    static double process = 0;

    static String regex = "(.*)(\\x24\\x24)(.*)";
    static Pattern pp = Pattern.compile(regex);

    static void doTestingLoading() {
        int a = 0;
        int max = 300000;
        for (int i = a; i < max; i++) {
            doLoadingProcess(i, max);
        }
    }

    static long getFileSize(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            return conn.getContentLengthLong();
        } catch (IOException e) {
            return -1;
            // Or wrap into a (custom, if desired) RuntimeException so exceptions are propagated. 
            // throw new RuntimeException(e);
            // Alternatively you can just propagate IOException, but, urgh.
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    static void estimateFILE(String sourceFileWebAddress) {
        HttpURLConnection connection = null;
        URL serverAddress = null;

        try {
            serverAddress = new URL(sourceFileWebAddress);
            //set up out communications stuff
            connection = null;

            //Set up the initial connection
            connection = (HttpURLConnection) serverAddress.openConnection();

            //HEAD request will make sure that the contents are not downloaded.
            connection.setRequestMethod("HEAD");

            connection.connect();
            System.out.println("========" + connection.getContentLength());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close the connection, set all objects to null
            connection.disconnect();

            connection = null;
        }

    }

    static int estimateFileSize(String sourceFileWebAddress) throws IOException {
        URL url = new URL(sourceFileWebAddress);
        URLConnection connection = url.openConnection();
        connection.connect();

        int fileLenth = connection.getContentLength();

        InputStream inputStream = url.openStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        return fileLenth;
    }

    static int FileSize(String url) {
        URLConnection con;
        try {
            con = new URL(url).openConnection();
            return con.getContentLength();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    static String convertOutcomeCodeToStandard(String outc) {
        switch (outc) {
            case "06":
                return "UNK";
            case "07":
                return "REC";
            case "09":
                return "NREC";
            case "11":
                return "DE";
            case "10":
                return "RECS";
            case "02":
                return "RES";
        }
        return "";
    }

    /**
     * From Canada's Seriousness Code to Standard Seriousness (NSF). 01 -> Yes
     * 02 -> No
     *
     * @param serios
     * @return
     */
    static String convertSeriousnessCodeToStandard(String serios) {
        switch (serios) {
            case "01":
                return "S";
            case "02":
                return "N";
        }
        return "";
    }

    /**
     * From Canada Patient to Fusion's Patient.
     *
     * @param gender
     * @return Gender of Patient (after fusion).
     */
    static String convertGenderToStandard(String gender) {
        switch (gender) {
            case "Female":
                return "F";
            case "Male":
                return "M";
            case "Unknown":
                return "UNK";
            case "Not specified":
                return "NS";
        }
        return "";
    }

    /**
     * From Canada Patient's Height Unit into Standard Height Unit. Centimetres
     * -> C Inches -> I
     *
     * @param ht
     * @return Height Unit (after fusion).
     */
    static String convertHeightUnitToStandard(String ht) {
        switch (ht) {
            case "Centimetres":
                return "C";
            case "Inches":
                return "I";
        }
        return "";
    }

    /**
     * /cycle -> CY /wk -> QW /month -> QM /hr -> QH /min -> QMIN /yr -> QY
     * /sec -> QS /trimester -> TSE
     *
     * @param freq
     * @return
     */
    static String convertFreqUSAtoStandard(String freq) {
        switch (freq) {
            case "/wk":
                return "QW";
            case "/month":
                return "QM";
            case "/cycle":
                return "CY";
            case "/hr":
                return "QH";
            case "/min":
                return "QMIN";
            case "/yr":
                return "QY";
            case "/sec":
                return "QS";
            case "trimester":
                return "TSE";
            case "":
                return "";
        }
        return freq;
    }

    static String regex_pattern_freq = "(|\\d*)\\x20(every)\\x20([\\d.]*)\\x20([^(]*)";

    /**
     * As required -> PRN (As needed) Once -> ON Total -> TO 1 every 1 Day(s) ->
     * QD every 1 Day(s) -> QD 1 every 1 Month(s) every 1 Month(s)
     *
     * @param freq
     * @return
     */
    static String convertFreqUnitToStandard(String freq) {
        switch (freq) {
            case "As required":
                return "PRN";
            case "Once":
                return "ON";
            case "Total":
                return "TO";
            case "Trimester":
                return "TSE";
        }

        Pattern p = Pattern.compile(regex_pattern_freq);
        Matcher m = p.matcher(freq);
        if (m.find()) {
            String numb = m.group(1);
            String p_numb = m.group(3);
            String r_unit = "";
            String unit = m.group(4);
            switch (unit) {
                case "Second":
                    r_unit = "S";
                    break;
                case "Hour":
                    r_unit = "H";
                    break;
                case "Day":
                    r_unit = "D";
                    break;
                case "Month":
                    r_unit = "M";
                    break;
                case "Minute":
                    r_unit = "MIN";
                    break;
                case "Week":
                    r_unit = "W";
                    break;
                case "Year":
                    r_unit = "Y";
                    break;
            }

            if (numb.equals("") || numb.equals("1") || numb.equals(" ")) {
                numb = "1";
            }

            if (numb.equals("1") && p_numb.equals("1")) {
                switch (r_unit) {
                    case "S":
                        return "QS";
                    case "H":
                        return "QH";
                    case "MIN":
                        return "QMIN";
                    case "D":
                        return "QD";
                    case "M":
                        return "QM";
                    case "W":
                        return "QW";
                    case "Y":
                        return "QY";
                }
            }
            return numb + "Q" + p_numb + r_unit;
        }
        return "";
    }

    /**
     *
     * Teaspoonful -> TS Milligram/Milliliters -> ML/MG Grain -> GR
     * Applicatorfull -> APF Ounce -> ON Dermatological Preparation -> DPR
     * Topical Preparation -> TPR Other -> OTH Dram -> D Centigram -> CI nci
     * (nano curie) -> NCI uCi (mikro curie) -> UCI Sachet -> S Plaster -> P UT
     * -> UT AD -> AD KU -> KU
     *
     * @param d_u
     * @return
     */
    static String convertDosageUnitToStandard(String d_u) {
        switch (d_u) {
            case "Milligram":
                return "MG";
            case "Gram":
                return "G";
            case "Microgram":
                return "UG";
            case "IU (International Unit)":
                return "IU";
            case "mL":
                return "ML";
            case "Dosage forms":
                return "DF";
            case "Nanogram":
                return "NG";
            case "Drops":
                return "GTT";
            case "Gtt":
                return "GTT";
            case "mg/kg":
                return "MG/KG";
            case "mg/m2":
                return "MG/M**2";
            case "Percent":
                return "PCT";
            case "Units":
                return "IU";
            case "International units thousands":
                return "KIU";
            case "International units millions":
                return "MIU";
            case "Teaspoonful":
                return "TS";
            case "uL":
                return "UL";
            case "Litres":
                return "L";
            case "Milliequivalents":
                return "MEQ";
            case "Milligram/Milliliters":
                return "MG/ML";
            case "Unknown":
                return "UNK";
            case "IU/kg":
                return "IU/KG";
            case "Grain":
                return "GR";
            case "ug/kg":
                return "UG/KG";
            case "Applicatorfull":
                return "APF";
            case "MBq":
                return "MBQ";
            case "Microcuries":
                return "UCI";
            case "Ounce":
                return "ON";
            case "Dermatological Preparation":
                return "DPR";
            case "Topical Preparation":
                return "TPR";
            case "Millicuries":
                return "MCI";
            case "Millimol":
                return "MMOL";
            case "Micromol":
                return "UMOL";
            case "Bq":
                return "BQ";
            case "kbq":
                return "KBQ";
            case "GBq":
                return "GBQ";
            case "Kilogram":
                return "KG";
            case "ug/m2":
                return "UG/M**2";
            case "Other":
                return "OTH";
            case "MU":
                return "MU";
            case "Dram":
                return "D";
            case "Centigram":
                return "CI";
            case "nci":
                return "NCI";
            case "uCi":
                return "UCI";
            case "Sachet":
                return "S";
            case "Plaster":
                return "P";
            case "UT":
                return "UT";
            case "AD":
                return "AD";
            case "KU":
                return "KU";
        }
        return "";
    }

    static String convertDateToStandard(String date) {
        String[] split = date.split("-");
        String day = split[0];
        String month = split[1];
        String year = split[2];
        switch (split[1]) {
            case "JAN":
                month = "01";
                break;
            case "FEB":
                month = "02";
                break;
            case "MAR":
                month = "03";
                break;
            case "APR":
                month = "04";
                break;
            case "MAY":
                month = "05";
                break;
            case "JUN":
                month = "06";
                break;
            case "JUL":
                month = "07";
                break;
            case "AUG":
                month = "08";
                break;
            case "SEP":
                month = "09";
                break;
            case "OCT":
                month = "10";
                break;
            case "NOV":
                month = "11";
                break;
            case "DEC":
                month = "12";
        }
        if (!split[2].equals("")) {
            int year_n = Integer.parseInt(year);
            if (year_n > 25 && year_n < 99) {
                year = "19" + split[2];
            } else {
                year = "20" + split[2];
            }
        }

        return year + month + day;
    }

    /**
     * From Canada's Source-Code to Standard Source-Code. report_type_cod
     * (kanada)
     *
     * @param rpsc
     * @return Source Code (after fusion).
     */
    static String convertSourceKanadaToStandard(String rpsc) {
        switch (rpsc) {
            case "7":
                return "SPO";
            case "8":
                return "SDY";
            case "5":
                return "PUB";
            case "2":
                return "SAP";
            case "11":
                return "UNK";
            case "9":
                return "SUR";
            case "10":
                return "OTH";
        }
        return "";
    }

    /**
     * From Canada's Report Reporter Type Code to Standard Report Type Code.
     *
     * @param reporter_type
     * @return Reporter Type (after fusion).
     */
    static String convertReporterTypeToStandard(String reporter_type) {
        switch (reporter_type) {
            case "Pharmacist":
                return "PH";
            case "Consumer Or Other Non Health Professional":
                return "CN";
            case "Physician":
                return "MD";
            case "Lawyer":
                return "L";
            case "Nurse":
                return "NU";
            case "Other health-professional":
                return "OT";
        }
        return "";
    }

    /**
     * From Canada's Reaction Duration Code into Standard Reaction Duration
     * Code.
     *
     * @param td_cod
     * @return Therapy Duration (after fusion).
     */
    static String convertReactionDurationCodetoStandard(String td_cod) {
        switch (td_cod) {
            case "Year(s)":
                return "YR";
            case "Month(s)":
                return "MON";
            case "Week(s)":
                return "WK";
            case "Day(s)":
                return "DAY";
            case "Hour(s)":
                return "HR";
            case "Second(s)":
                return "SEC";
            case "Minute(s)":
                return "MIN";
            case "Once":
                return "O";
            case "Unknown":
                return "UNK";
        }
        return "";
    }

    /**
     * From Canada's Therapy Duration Code into Standard Therapy Duration Code.
     *
     * @param td_cod
     * @return Therapy Duration (after fusion).
     */
    static String convertTherapyDurationCodetoStandard(String td_cod) {
        switch (td_cod) {
            case "Year(s)":
                return "YR";
            case "Month(s)":
                return "MON";
            case "Week(s)":
                return "WK";
            case "Day(s)":
                return "DAY";
            case "Hour(s)":
                return "HR";
            case "Second(s)":
                return "SEC";
            case "Minute(s)":
                return "MIN";
            case "Once":
                return "O";
            case "Same Day":
                return "SD";
            case "Not reported":
                return "NR";
            case "Unknown":
                return "UNK";
        }
        return "";
    }

    /**
     * From Age_Y of Canada's Patients into Age_Group Standard.
     *
     * @param age
     * @return Age_Group (after fusion).
     */
    static String convertAgeYearsToAgeGroup(String age) {
        if (age.equals("")) {
            return "UNK";
        } else {
            Double age_parsed = Double.parseDouble(age);
            if (age_parsed <= 0.083333) {
                return "N";
            } else if (age_parsed > 0.083333 && age_parsed < 2) {
                return "I";
            } else if (age_parsed >= 2 && age_parsed < 12) {
                return "C";
            } else if (age_parsed > 12 && age_parsed < 18) {
                return "T";
            } else if (age_parsed > 18 && age_parsed < 60) {
                return "A";
            } else {
                return "E";
            }
        }
    }

    static String convertYCS_OccpCode(String consumer, String hcp) {
        String csn = convertYCS_ConsumerYN_OccpCode(consumer);
        String hcpp = convertYCS_HCPYN_OccpCode(hcp);
        if (csn.equals("Y") && hcpp.equals("Y")) {
            // für Consumer oder OT entscheiden...
            return "CN";
        } else if (csn.equals("N") && hcpp.equals("N")) {
            return "";
        } else if (hcpp.equals("Y")) {
            return "OT";
        } else if (csn.equals("Y")) {
            return "CN";
        } else {
            return "";
        }
    }

    static String convertYCS_ConsumerYN_OccpCode(String yn) {
        switch (yn) {
            case "Y":
                return "CN";
            case "N":
                return "";
        }
        return "";
    }

    static String convertYCS_HCPYN_OccpCode(String yn) {
        switch (yn) {
            case "Y":
                return "OT";
            case "N":
                return "";
        }
        return "";
    }

    /**
     * From Canada Weight Unit to Fusion's Weight.
     *
     * @param wt
     * @return Weight Unit (after fusion).
     */
    static String convertWeightUnitToStandard(String wt) {
        switch (wt) {
            case "Kilograms":
                return "KG";
            case "Pounds":
                return "LBS";
            case "Unknown":
                return "UNK";
            case "Ounces":
                return "ON";
        }
        return "";
    }

    /**
     * From Canada Age Unit to Fusion's Age Unit.
     *
     * @param unit
     * @return Age Unit (after fusion).
     */
    static String convertAgeUnitToStandard(String unit) {
        switch (unit) {
            case "Years":
                return "YR";
            case "Hours":
                return "HR";
            case "Minutes":
                return "MIN";
            case "Decade":
                return "DEC";
            case "Days":
                return "DY";
            case "Months":
                return "MON";
        }
        return "";
    }

    private static void doLoadingProcess(int progress, int max) {
        process = (double) progress / max;
        System.out.print(process + "\t%\r");
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width) {
            return s.substring(0, width - 1) + ".";
        } else {
            return s;
        }
    }

    /**
     * Return for option (2 = 14Q2, 1 > 14Q2) the equivalent propertiy-map
     *
     * @param option
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    static HashMap<String, HashMap<String, Integer>> ConfigParserStructureGenerall(SortedSet<String> files_of_properties, int option) throws FileNotFoundException, IOException {
        String name = null;
        String desc = null;
        String source = null;
        String[] demos_generall = null;
        String[] drugs_generall = null;
        String[] reactions_generall = null;
        String[] indications_generall = null;
        String[] therapies_generall = null;
        String[] outcome_generall = null;
        String[] reportsource_generall = null;

        HashMap<String, HashMap<String, Integer>> set_all_config = new HashMap<>();

        String config_file_path = CurrenDirectory()
                + OSValidator() + Properties.config_path_dir
                + OSValidator() + Properties.config_path_file;
        File f = new File(config_file_path);
        HashMap<String, HashMap<String, HashMap<String, Integer>>> mapo = new HashMap<>();
        String properties_file_generall = CurrenDirectory() + OSValidator()
                + Properties.basic_path_database + OSValidator()
                + Properties.basic_path_us + OSValidator();

        String drug = "";
        String indi = "";
        String thera = "";
        String reac = "";
        String demo = "";
        String outc = "";
        String sources = "";

        Object[] toArray = files_of_properties.toArray();
        demo = (String) toArray[0];
        drug = (String) toArray[1];
        indi = (String) toArray[2];
        outc = (String) toArray[3];
        reac = (String) toArray[4];
        sources = (String) toArray[5];
        thera = (String) toArray[6];

        if (option == 2) {
            File file_p = new File(demo);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                demos_generall = iterator.next().split("\\$");
                br.close();
            }

            file_p = new File(thera);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                therapies_generall = iterator.next().split("\\$");
                br.close();
            }

            file_p = new File(drug);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                drugs_generall = iterator.next().split("\\$");
                br.close();
            }

            file_p = new File(indi);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                indications_generall = iterator.next().split("\\$");
                br.close();
            }

            file_p = new File(outc);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                outcome_generall = iterator.next().split("\\$");
                br.close();
            }

            file_p = new File(reac);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                reactions_generall = iterator.next().split("\\$");
                br.close();
            }

            file_p = new File(sources);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                reportsource_generall = iterator.next().split("\\$");
                br.close();
            }

            HashMap<String, Integer> props_PATEINT = receiveMatchedProperties(config_file_path, demos_generall, Properties.config_entity_patient, 2);
            HashMap<String, Integer> props_REPORT = receiveMatchedProperties(config_file_path, demos_generall, Properties.config_entity_report, 2);
            HashMap<String, Integer> props_DRUGS = receiveMatchedProperties(config_file_path, drugs_generall, Properties.config_entity_drug, 2);
            //event_dt in report belongs to REACTION.
            HashMap<String, Integer> props_REACTION = receiveMatchedProperties(config_file_path, reactions_generall, Properties.config_entity_reaction, 2);
            HashMap<String, Integer> props_INDICATION = receiveMatchedProperties(config_file_path, indications_generall, Properties.config_entity_indication, 2);
            HashMap<String, Integer> props_THERAPY = receiveMatchedProperties(config_file_path, therapies_generall, Properties.config_entity_therapy, 2);
            HashMap<String, Integer> props_OUTCOME = receiveMatchedProperties(config_file_path, outcome_generall, Properties.config_entity_outcome, 2);
            HashMap<String, Integer> props_RPSR = receiveMatchedProperties(config_file_path, reportsource_generall, Properties.config_entity_reportsource, 2);
            set_all_config.put("Patient", props_PATEINT);
            set_all_config.put("Drug", props_DRUGS);
            set_all_config.put("Report", props_REPORT);
            set_all_config.put("Reaction", props_REACTION);
            set_all_config.put("Indication", props_INDICATION);
            set_all_config.put("Therapy", props_THERAPY);
            set_all_config.put("Outcome", props_OUTCOME);
            set_all_config.put("RPSR", props_RPSR);
        }

        if (option == 1) {

            File file_p = new File(demo);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                demos_generall = iterator.next().split("\\$");
                br.close();
            }

            file_p = new File(thera);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                therapies_generall = iterator.next().split("\\$");
                br.close();
            }

            file_p = new File(drug);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                drugs_generall = iterator.next().split("\\$");
                br.close();
            }

            file_p = new File(indi);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                indications_generall = iterator.next().split("\\$");
                br.close();
            }

            file_p = new File(outc);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                outcome_generall = iterator.next().split("\\$");
                br.close();
            }

            file_p = new File(reac);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                reactions_generall = iterator.next().split("\\$");
                br.close();
            }

            file_p = new File(sources);
            try (BufferedReader br = new BufferedReader(new FileReader(file_p))) {
                Iterator<String> iterator = br.lines().iterator();
                reportsource_generall = iterator.next().split("\\$");
                br.close();
            }

            HashMap<String, Integer> props_PATEINT = receiveMatchedProperties(config_file_path, demos_generall, Properties.config_entity_patient, 1);
            HashMap<String, Integer> props_REPORT = receiveMatchedProperties(config_file_path, demos_generall, Properties.config_entity_report, 1);
            HashMap<String, Integer> props_DRUGS = receiveMatchedProperties(config_file_path, drugs_generall, Properties.config_entity_drug, 1);
            //event_dt in report belongs to REACTION.
            HashMap<String, Integer> props_REACTION = receiveMatchedProperties(config_file_path, reactions_generall, Properties.config_entity_reaction, 1);
            HashMap<String, Integer> props_INDICATION = receiveMatchedProperties(config_file_path, indications_generall, Properties.config_entity_indication, 1);
            HashMap<String, Integer> props_THERAPY = receiveMatchedProperties(config_file_path, therapies_generall, Properties.config_entity_therapy, 1);
            HashMap<String, Integer> props_OUTCOME = receiveMatchedProperties(config_file_path, outcome_generall, Properties.config_entity_outcome, 1);
            HashMap<String, Integer> props_RPSR = receiveMatchedProperties(config_file_path, reportsource_generall, Properties.config_entity_reportsource, 1);
            set_all_config.put("Patient", props_PATEINT);
            set_all_config.put("Drug", props_DRUGS);
            set_all_config.put("Report", props_REPORT);
            set_all_config.put("Reaction", props_REACTION);
            set_all_config.put("Indication", props_INDICATION);
            set_all_config.put("Therapy", props_THERAPY);
            set_all_config.put("Outcome", props_OUTCOME);
            set_all_config.put("RPSR", props_RPSR);
        }

        return set_all_config;
    }

    static HashMap<String, Integer> receiveMatchedProperties(String config_path, String[] related_file, String identifier, int option) throws FileNotFoundException, IOException {
        HashMap<String, Integer> input = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(config_path))) {
            Iterator<String> iterator = br.lines().iterator();
            String[] array = null;
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.startsWith(identifier)) {
                    while (iterator.hasNext()) {
                        String next_line = iterator.next();
                        if (!next_line.startsWith("global")) {
                            array = next_line.split("\\$");
                            if (array.length > 2) {
                                input.put(array[0], -1);
                                for (int i = 0; i < related_file.length; i++) {
                                    String string = related_file[i];
                                    if (string.matches(array[option])) {
                                        input.put(array[0], i);
                                        break;
                                    } else {
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }
                } else {
                }
            }
            br.close();
        }
        return input;
    }

    /**
     * Generates random number between 0 and max.
     *
     * @param max max number
     * @return random-number
     */
    static public int generateRandomInteger(int max) {
        double res = Math.random();
        int a = (int) (max * res);
        return a;
    }

    /**
     * Method, starting time.
     */
    static public void startTime() {
        startTime = System.nanoTime();
    }

    public static double round(int a, int max, int places) {
        double value = (double) a / max;
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return bd.doubleValue();
    }

    public static double roundDouble(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Method, stoping time.
     */
    static public void endTime() {
        estimatedTime = System.nanoTime() - startTime;
        seconds = ((double) estimatedTime / 1000000000);
    }

    /**
     * Method, print passed time. (from start to stop)
     *
     * @param what additional text
     */
    static String printPassedTime(String what) {
        endTime();
//        System.out.format("solution Time : %f Seconds", seconds);
        String res = " " + what + "\n";
//        System.out.print(res);
        return what + "\t" + seconds;
    }

    /**
     * Method for splitting a given String "a" by the character "$".
     *
     * @param a String for split
     * @return a[].
     */
    static public String[] splitOneLine(String a) {
        return a.split("\\$", -1);
    }

    /**
     * Method for splitting a given String "a" by the character "$".
     *
     * @param a String for split
     * @return a[].
     */
    static public String[] splitOneLine(String a, String by) {
        return a.split(by, -1);
    }

    /**
     * Method for splitting a given String "a" by the character "$".
     *
     * @param a String for split
     * @return a[].
     */
    static public String[] splitOneLineFIRST_TIME(String a) {
        return a.split("\\$");
    }

    /**
     * Method for splitting a given String "a" by the character "$".
     *
     * @param a String for split
     * @return a[].
     */
    static public String splitToSecondary(String a) {
        return a.split("\\$")[2];
    }

    /**
     * Method for expanding a String-Array by 1.
     *
     * @param old String-Array
     * @return String-Array length increased by 1.
     */
    static public String[] expandArray(String[] old) {
        String[] newe = new String[old.length + 1];
        System.arraycopy(old, 0, newe, 0, old.length);
        newe[old.length] = "";
        return newe;
    }

    static int ConvertPropertyToInteger(String t) {
        int x = t.getBytes().length;
        return x;
    }

    /**
     * French encoding, acent gu, acent grave, acent circumflex.
     */
    static String ConvertsString(String input) {
        String output = "";
        try {
            output = new String(input.getBytes(System.getProperty("file.encoding")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return output;
    }

    static String[] returnCanadaProperyOneLineArray(String a) {
        return a.split("\\$");
    }

    static String doExtraction(String zipFile, String outputFolder) {
        String file_path = "";
        if (existsAlready(zipFile)) {
            file_path = unZipIt(zipFile, outputFolder);
        } else {
            System.out.println("No Zip Folder..");
        }
        return file_path;
    }

    static String[] returnPropertyUK(String file) throws IOException, InvalidFormatException {
        String q = "";
        File ff = new File(file).getAbsoluteFile();
        Workbook wb = WorkbookFactory.create(ff);
        Sheet mySheet = wb.getSheetAt(0);
        Iterator<Cell> cc = mySheet.getRow(13).cellIterator();
        while (cc.hasNext()) {
            String qq = cc.next().toString();
            q = q + qq + "$";
        }
        return q.toLowerCase().split("\\$");
    }

    static LinkedList<String[]> readXLS_DATA(String file, String[] prop) throws IOException, Exception {
        LinkedList<String[]> hh = new LinkedList<>();
        POIFSFileSystem fileSystem = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook wb = new HSSFWorkbook(fileSystem);
        HSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> rows = sheet.rowIterator();
        int max_number_props = prop.length;
        while (rows.hasNext()) {
            String[] s_v = new String[max_number_props];
            HSSFRow row = (HSSFRow) rows.next();
            for (int i = 0; i < max_number_props; i++) {
                HSSFCell cell;
                if (row.getCell(i) == null || row.getCell(i).toString().equals("")) {
                    s_v[i] = "";
                } else {
                    cell = row.getCell(i);
                    String value = "";
                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC:
                            if (cell.toString().contains("-")) { // its a date... 17-June-2015
                                value = cell.toString();
                            } else {
                                value = BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
                            }
                            break;
                        case HSSFCell.CELL_TYPE_STRING:
                            value = cell.getStringCellValue();
                            break;
                    }
                    s_v[i] = value;
                }
            }
            hh.add(s_v);
        }
        return hh;
    }

    static void readData(String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(file).getAbsoluteFile()));
        Iterator<String> i = br.lines().iterator();
        while (i.hasNext()) {
            String l = i.next();
            System.out.println(l);
        }
    }

    static boolean existsAlready(String out) {
        if (new File(out).getAbsoluteFile().exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Unzip it
     *
     * @param zipFile input zip file
     * @param output zip file output folder
     */
    static String unZipItCanada(String zipFile, String outputFolder) {

        String output = "";
        String returnFileName = "";
        byte[] buffer = new byte[1024];
        try {
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }

            ZipInputStream zis
                    = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                //TODO "ascii" is a directory.. create
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                if (fileName.endsWith("/")) {
                    newFile.mkdir();
                    output = fileName;
                } else {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }
                returnFileName = newFile.getAbsoluteFile().getAbsolutePath();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
//            System.out.println("Done");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return output;
    }

    /**
     * Unzip it
     *
     * @param zipFile input zip file
     * @param output zip file output folder
     */
    static String unZipIt(String zipFile, String outputFolder) {

        String returnFileName = "";
        byte[] buffer = new byte[1024];
        try {

            //create output directory is not exists
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }

            //get the zip file content
            ZipInputStream zis
                    = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                //TODO "ascii" is a directory.. create
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                if (fileName.endsWith("/")) {
                    newFile.mkdir();
                } else {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }
                returnFileName = newFile.getAbsoluteFile().getAbsolutePath();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
//            System.out.println("Done");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return returnFileName;
    }

    static void doExtractionOfURL(String web) throws IOException {
        String url_original = web;
        String url_download_link = fetchLINKwithKEY(url_original, ".zip");
        Tools.doDownload(url_download_link, "");
    }

    static void doDownload(String website, String output) throws IOException {
        URL websiteUrl = new URL(website);
        ReadableByteChannel rbc = Channels.newChannel(websiteUrl.openStream());
        FileOutputStream fos = new FileOutputStream(output);
        long size = fos.getChannel().size();

        System.out.println(output + " --- " + size);
        if (new File(output).exists()) {

        } else {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    static HashMap<String, HashMap<String, Integer>> parsePropertiesYCS(String dir) {
        HashMap<String, HashMap<String, Integer>> infos = new HashMap<>();
        try {
            Stream<Path> list = Files.list(Paths.get(dir));
            for (Object object : list.toArray()) {
                Path p = (Path) object;
            }
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return infos;
    }

    /**
     * Return File Size of Canada Zip file.
     *
     * @param url web page, where the download link is.
     * @return file size as double
     * @throws IOException
     */
    static double checkFileSizeOfCanadaZipFile(String url) throws IOException {
        Pattern p_file_size = Pattern.compile("(ZIP Version)\\x20-\\x20(\\d{1,4}.\\d{0,2})\\x20");
        Matcher m;
        Document doc = Jsoup.connect(url).get();
        Elements allElements = doc.getAllElements();
        Iterator<Element> all_elements = allElements.iterator();
        while (all_elements.hasNext()) {
            String next_line = all_elements.next().toString();
            m = p_file_size.matcher(next_line);
            if (m.find()) {
                String group = m.group(2);
                Double d = Double.parseDouble(group);
                return d;
            }
        }
        return 0;
    }

    static void checkContent(Parser_Info parser) throws IOException {

//        HashMap<String,String> all_infos = new HashMap<>();
        ArrayList<Parser_File_Entry> all_infos_e = new ArrayList<>();

        String normal_url = "https://www.fda.gov";
        Pattern p_name = Pattern.compile("(<a href=\")([^\\\"]*)\\\">(FAERS)[\\_|\\x20](ASCII)[\\_|\\x20]([\\d]{4}[q|Q][\\d]{1})");
        Pattern p_link_only = Pattern.compile("(<a href=\")([^\\\"]*)\\\">");
        Pattern p_name_only = Pattern.compile("(FAERS)[\\_|\\x20](ASCII)[\\_|\\x20](\\d{4}[q|Q]\\d)");
        Pattern p_file_size = Pattern.compile("(\\(ZIP\\x20-\\x20)(\\d{1,2}(|\\.\\d{1,2})MB)");

        Matcher m;
        Document doc = Jsoup.connect(parser.getOrigin_url()).get();
        Elements allElements = doc.getAllElements();
        Iterator<Element> all_elements = allElements.iterator();

        while (all_elements.hasNext()) {
            String[] toString = all_elements.next().toString().split("\n");
            int length = toString.length;
            if (length == 1) {
                String origin = toString[0];
                m = p_name.matcher(origin);
                if (m.find()) {
                    Parser_File_Entry pfe = new Parser_File_Entry(m.group(5), normal_url + m.group(2));
                    m = p_file_size.matcher(origin);
                    if (m.find()) {
                        pfe.setSize(m.group(2));
                    }
                    if (pfe != null) {
                        all_infos_e.add(pfe);
                    }
                }
            }
            if (length == 4) {
                String url3 = "";
                String name = "";
                String size = "";
                for (String string : toString) {
                    m = p_link_only.matcher(string);
                    if (m.find()) {
                        url3 = m.group(2);
                    }
                    m = p_name_only.matcher(string);
                    if (m.find()) {
                        name = m.group(3);
                    }
                    m = p_file_size.matcher(string);
                    if (m.find()) {
                        size = m.group(2);
                    }
                }
                Parser_File_Entry parser_File_Entry = new Parser_File_Entry(name, url3);
                parser_File_Entry.setSize(size);
                if (parser_File_Entry != null) {
                    all_infos_e.add(parser_File_Entry);
                }
            }

        }
        parser.setAll_urls(all_infos_e);
    }

    /**
     * Extracting Canada Properties from URL.
     *
     * @param args
     * @return
     * @throws IOException
     */
    static HashMap<String, String[]> featchDATAandPROPS(String args) throws IOException {
        print("Fetching %s...", args);
        Document doc = Jsoup.connect(args).get();
        Elements tables = doc.select("table");

        int counter_file = 0;
        List<String> props = new LinkedList<>();
        HashMap<Integer, String> files = new HashMap<>();
        HashMap<Integer, String[]> omega_properties = new HashMap<>();
        HashMap<String, String[]> files_omega_props = new HashMap<>();

        for (Element header_file : doc.select("h2")) {
            if (header_file.text().endsWith(".txt")) {
                files.put(counter_file, header_file.text());
                counter_file++;
            }
        }

        counter_file = 0;

        for (Element table_file : tables) {
            props.clear();
            for (Element tbody_file : table_file.select("tbody")) {
                for (Element tr : tbody_file.select("tr")) {
                    int number = 0;
                    for (Element td : tr.select("td")) {
                        if (td.toString().contains("hc2")) {
                            props.add(td.text());
                            break;
                        } else if (number == 1) {
                            props.add(td.text());
                            break;
                        } else {
                            number++;
                        }
                    }
                }
            }
            String[] property_array = new String[props.size()];
            for (int i = 0; i < property_array.length; i++) {
                property_array[i] = props.get(i).toLowerCase();
            }
            omega_properties.put(counter_file++, property_array);
        }

        for (Integer integer : files.keySet()) {
            files_omega_props.put(files.get(integer), omega_properties.get(integer));
        }

        for (Map.Entry<String, String[]> entry : files_omega_props.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue().length);

            System.out.println("");
        }
        return files_omega_props;
    }

    static String fetchLINKwithKEY(String args, String key) throws IOException {
        String getResultAsDownloadableFile = "";
        print("Fetching %s...", args);
        Document doc = Jsoup.connect(args).get();
        Elements links = doc.select("a[href]");

        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            String temp = link.attr("abs:href");
            if (temp.endsWith(key)) {
                getResultAsDownloadableFile = temp;
                String[] getResult_Split = getResultAsDownloadableFile.split("/");
                String getResult_Last = getResult_Split[getResult_Split.length - 1];
//                output = getResult_Last;
            }
            System.out.println(temp + "\n");
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        }
        return getResultAsDownloadableFile;
    }

    /**
     * Method for splitting a given String "a" by the character "$".
     *
     * @param a String for split
     * @return a[].
     */
    static String[] splitOneLineCanada(String a) {
        String[] split = a.split("\\$");
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].replaceAll("\"", "");

        }
        return split;
    }

    /**
     * Gives SYMBOL back (/ Wind ... \ OSx ...)
     *
     * @return String
     */
    public static String OSValidator() {
        String valid = "";
        if (SystemUtils.IS_OS_WINDOWS_7) {
            valid = "\\\\";
        }
        if (SystemUtils.IS_OS_WINDOWS_8) {
            valid = "\\\\";
        }
        if (SystemUtils.IS_OS_LINUX) {
            valid = "/";
        }
        if (SystemUtils.IS_OS_MAC) {
            valid = "/";
        }
        return valid;
    }

    /**
     * Gives Current Directory.
     *
     * @return String
     */
    public static String CurrenDirectory() {
        return System.getProperty("user.dir");
    }

    static String convertPropertyStringConvential(String input) {
        String res = "";
        String first_capital = input.substring(0, 1).toUpperCase();
        String rest_string = input.substring(1, input.length()).toLowerCase();
        res = first_capital + rest_string;
        return res;
    }

    static void let_DB_rest_ALittleBit(GraphDatabaseService db, int seconds) {
        try (Transaction tx = db.beginTx(seconds, TimeUnit.SECONDS)) {
            tx.success();
        }
        db.shutdown();
    }

}
