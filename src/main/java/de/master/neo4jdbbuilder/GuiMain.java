/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Stream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * @author benfrenco
 */
class ParserBoxInstanzFirst {

    ArrayList<Parser_Info> alle_parser = new ArrayList();

    public ParserBoxInstanzFirst() throws FileNotFoundException, IOException {

        String sym = Tools.OSValidator();
        String rel_path = Tools.CurrenDirectory();
        String file_path = rel_path + sym + Properties.basic_path_database + sym + Properties.basic_path_parser_settings;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(file_path)))) {
            Iterator<String> iterator = br.lines().iterator();
            while (iterator.hasNext()) {
                String[] line_parser_infos = iterator.next().split("\\$");
                Parser_Info parser_Info = new Parser_Info(line_parser_infos[2], line_parser_infos[0], line_parser_infos[1]);
                if (line_parser_infos[0].equals("F.A.E.R.S Parser")) {
                    Tools.checkContent(parser_Info);
                }
                alle_parser.add(parser_Info);
            }
            br.close();
        }

    }

    public ArrayList<Parser_Info> getAlle_parser() {
        return alle_parser;
    }

    public void setAlle_parser(ArrayList<Parser_Info> alle_parser) {
        this.alle_parser = alle_parser;
    }

}

final class GuiParentMain {

    public GuiParentMain(String title) throws HeadlessException {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame jf = new JFrame(title);
                int height = 750;
                int width = 1200;
                jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                jf.setSize(width, height);

                try {
                    GuiMain gm = new GuiMain();
                    jf.add(gm);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(GuiParentMain.class.getName()).log(Level.SEVERE, null, ex);
                }

                jf.setLocationRelativeTo(jf);
                jf.setResizable(false);
                jf.setVisible(true);
            }
        });

    }
}

public class GuiMain extends javax.swing.JPanel {

    PreProcessorYCS_F pp_ycs;

    public void init() throws IOException {
        ParserBoxInstanzFirst pbif = new ParserBoxInstanzFirst();
        ArrayList<Parser_Info> alle_parser = pbif.getAlle_parser();
        DefaultComboBoxModel dmd = new DefaultComboBoxModel<Parser_Info>();
        for (int i = 0; i < alle_parser.size(); i++) {
            Parser_Info p = alle_parser.get(i);
            dmd.addElement(p);
        }

        searchComboBoxList.addElement("drugname");
        searchComboBoxList.addElement("indication");
        searchComboBoxList.addElement("reaction");

        jComboBox1.setModel(dmd);
        descriptionAREA.setLineWrap(true);
        descriptionAREA.setWrapStyleWord(true);
        pi = (Parser_Info) jComboBox1.getSelectedItem();
        descriptionAREA.setText(pi.getDesc());
        labelSOURCE.setText(pi.getOrigin_url());
        initNiceLook();
    }

    /**
     * Creates new form GuiMain
     */
    public GuiMain() throws MalformedURLException {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initComponents();
                try {
                    init();
                } catch (IOException ex) {
                    Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        NEWPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<Parser_Info>();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionAREA = new javax.swing.JTextArea();
        labelSOURCE = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        filesLISTE = new javax.swing.JList<>();
        jButton3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        overviewLIST = new javax.swing.JList<>();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        deleteOverviewBUTTON = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        progress_Info = new javax.swing.JLabel();
        progress_STEP = new javax.swing.JLabel();
        progress_STATE = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        progress_AREA = new javax.swing.JTextArea();
        redundancyBUTTON = new javax.swing.JButton();
        read_redun_BUTTON = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        field_db_name = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        pathLABEL = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        downloadLABEL = new javax.swing.JLabel();
        openEXISTED_DB_BUTTON = new javax.swing.JButton();

        setOpaque(false);

        NEWPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true), "Select Parser", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Helvetica", 1, 13))); // NOI18N

        jLabel3.setText("Parser:");

        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Source:");

        jLabel5.setText("Description:");

        descriptionAREA.setBackground(java.awt.SystemColor.window);
        descriptionAREA.setColumns(20);
        descriptionAREA.setRows(5);
        descriptionAREA.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        descriptionAREA.setEnabled(false);
        jScrollPane2.setViewportView(descriptionAREA);

        labelSOURCE.setText("Source");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 1, Short.MAX_VALUE)
                                .addComponent(labelSOURCE, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(labelSOURCE, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true), "Parser Files", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Helvetica", 1, 13)))); // NOI18N

        jLabel8.setText("Files:");

        filesLISTE.setModel(dmd_a);
        jScrollPane3.setViewportView(filesLISTE);

        jButton3.setText("Add");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3))))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true), "Overview", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Helvetica", 1, 13))); // NOI18N

        jScrollPane4.setViewportView(overviewLIST);

        jButton4.setText("Start");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setText("Download");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        deleteOverviewBUTTON.setText("Delete");
        deleteOverviewBUTTON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteOverviewBUTTONActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteOverviewBUTTON, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton6)
                    .addComponent(deleteOverviewBUTTON)))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true), "Progress", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Helvetica", 1, 13))); // NOI18N

        progress_Info.setText("Step:");

        progress_AREA.setColumns(20);
        progress_AREA.setRows(5);
        jScrollPane5.setViewportView(progress_AREA);

        redundancyBUTTON.setText("Check Redundant Files");
        redundancyBUTTON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redundancyBUTTONActionPerformed(evt);
            }
        });

        read_redun_BUTTON.setText("Delete Duplicates");
        read_redun_BUTTON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                read_redun_BUTTONActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress_Info)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(progress_STATE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(progress_STEP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(redundancyBUTTON, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(read_redun_BUTTON)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(progress_Info, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(progress_STEP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progress_STATE, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(redundancyBUTTON)
                    .addComponent(read_redun_BUTTON)))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true), "Generall Settings", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Helvetica", 1, 13))); // NOI18N

        jLabel2.setText("Name:");

        jLabel1.setText("Description:");

        field_db_name.setSize(new java.awt.Dimension(85, 26));
        field_db_name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                field_db_nameActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setBounds(new java.awt.Rectangle(0, 0, 240, 77));
        jScrollPane1.setViewportView(jTextArea1);

        pathLABEL.setText("Path");
        pathLABEL.setRequestFocusEnabled(false);

        jButton5.setText("Location");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton2.setText("Download");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        downloadLABEL.setText("Path");
        downloadLABEL.setEnabled(false);
        downloadLABEL.setFocusable(false);
        downloadLABEL.setRequestFocusEnabled(false);

        openEXISTED_DB_BUTTON.setText("Open Neo4J Database");
        openEXISTED_DB_BUTTON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openEXISTED_DB_BUTTONActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pathLABEL, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(downloadLABEL, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(field_db_name))
                            .addComponent(openEXISTED_DB_BUTTON, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(12, 12, 12))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(openEXISTED_DB_BUTTON)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(field_db_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(pathLABEL, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(downloadLABEL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        javax.swing.GroupLayout NEWPanelLayout = new javax.swing.GroupLayout(NEWPanel);
        NEWPanel.setLayout(NEWPanelLayout);
        NEWPanelLayout.setHorizontalGroup(
            NEWPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, NEWPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NEWPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, NEWPanelLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, NEWPanelLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        NEWPanelLayout.setVerticalGroup(
            NEWPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, NEWPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(NEWPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(NEWPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Management -Database-", NEWPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void openEXISTED_DB_BUTTONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openEXISTED_DB_BUTTONActionPerformed

        loaded_integrated_files.clear();
        String path_config = "";
        String db_load_name = "";

        Preferences pref = Preferences.userRoot();
        String path = pref.get("DB_PATH", "");
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Wähle Datenbank Ordner aus");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setCurrentDirectory(new File(path));
        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            pathLABEL.setText(chooser.getSelectedFile().getParent());
            System.out.println(pathLABEL.getText());
            File f = chooser.getCurrentDirectory();
            db_load_name = chooser.getSelectedFile().getName();
            pref.put("DB_PATH", f.getAbsolutePath());
            Stream<Path> list;
            try {
                list = Files.list(Paths.get(f.getAbsolutePath()));
                for (Object object : list.toArray()) {
                    Path a = (Path) object;
                    String filename = a.getFileName().toString();
                    if (filename.equals(db_load_name)) {
                        path_config = f.getAbsolutePath() + Tools.OSValidator() + db_load_name + Tools.OSValidator()
                                + Properties.config_database_internal;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        
        field_db_name.setText(db_load_name);

        try {
            BufferedReader brr = new BufferedReader(new FileReader(new File(path_config)));
            Iterator<String> iterator = brr.lines().iterator();
            String last_line = "";
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (!next.equals("") && !next.startsWith("/") && !next.startsWith("\\")) {
                    loaded_integrated_files.add(next);
                }
                last_line = next;
            }
            String path_df = last_line;
            downloadLABEL.setText(path_df);
            brr.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        GraphDatabaseFactory gf = new GraphDatabaseFactory();
        path = pathLABEL.getText() + Tools.OSValidator() + field_db_name.getText();
        gdb = gf.newEmbeddedDatabase(new File(path));
        gdb.shutdown();
    }//GEN-LAST:event_openEXISTED_DB_BUTTONActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        Preferences pref = Preferences.userRoot();
        String path = pref.get("DOWNLOAD_PATH", "");
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Wähle Pfad aus");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setCurrentDirectory(new File(path));
        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            downloadLABEL.setText(chooser.getSelectedFile().getPath());
            File f = chooser.getCurrentDirectory();
            pref.put("DOWNLOAD_PATH", f.getAbsolutePath());
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        Preferences pref = Preferences.userRoot();
        String path = pref.get("OUTPUT_PATH", "");
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Wähle Pfad aus");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setCurrentDirectory(new File(path));
        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            pathLABEL.setText(chooser.getSelectedFile().getPath());
            File f = chooser.getCurrentDirectory();
            pref.put("OUTPUT_PATH", f.getAbsolutePath());
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void field_db_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_db_nameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_field_db_nameActionPerformed

    private void read_redun_BUTTONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_read_redun_BUTTONActionPerformed
        jTextArea1.setText("");
        try {
            RedundancyChecker red_checker = new RedundancyChecker(pathLABEL.getText(), field_db_name.getText(), jTextArea1);
            HashSet<Long> readFileRed = red_checker.readFileRed();
            for (Long checkDouble : readFileRed) {
                progress_AREA.append(checkDouble + "\n");
            }
            red_checker.removeDoubles(readFileRed);

        } catch (IOException ex) {
            Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_read_redun_BUTTONActionPerformed

    private void redundancyBUTTONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redundancyBUTTONActionPerformed
        try {
            RedundancyChecker red_checker = new RedundancyChecker(pathLABEL.getText(), field_db_name.getText(), jTextArea1);
            red_checker.InitFileWriting();
            HashSet<Long> checkDoubles = red_checker.checkDoubles();
            progress_AREA.setText("...checking for redundancies...\n");
            for (Long checkDouble : checkDoubles) {
                progress_AREA.append(checkDouble + "\n");
            }
            red_checker.CloseFileWriting();
        } catch (IOException ex) {
            Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_redundancyBUTTONActionPerformed

    private void deleteOverviewBUTTONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteOverviewBUTTONActionPerformed
        dmd_o.clear();
        overviewLIST.setModel(dmd_o);
    }//GEN-LAST:event_deleteOverviewBUTTONActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // DOWNLOADING...!
        HashSet<String> downloaded_zips = new HashSet<>();
        output_folder = pathLABEL.getText();
        download_folder = downloadLABEL.getText();
        db_name = field_db_name.getText();
        db_path = output_folder + Tools.OSValidator() + db_name;
        f = new File(db_path);
        canada = false;
        ycs = false;
        usa = false;

        ListModel<Parser_File_Overview> model = overviewLIST.getModel();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            Parser_File_Overview elementAt = model.getElementAt(i);
            ArrayList<Parser_File_Entry> p_infos = elementAt.getP_infos();
            for (Parser_File_Entry p_info : p_infos) {
                String url = p_info.getUrl();
                String d_file = download_folder + Tools.OSValidator()
                        + p_info.getName() + ".zip";
                if (p_info.getUrl().contains(Properties.canada_url_check)) {
                    canada = true;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("DOWNLOAD");
                                String downloadFILE_ZIP = Tools.downloadFILE_ZIP(p_info.getDownload_url(), d_file);
                            } catch (IOException ex) {
                                Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            downloaded_zips.add(d_file);
                        }
                    });
                } else if (p_info.getUrl().contains("info.mhra")) {
                    ycs = true;
                    pycs = new PreProcessorYCS_F(f, p_info.getUrl(),
                            download_folder, output_folder);
                    try {
                        pycs.startPreProcessingAll();
                        //                        pycs.closeDB();
                    } catch (IOException ex) {
                        Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    String kind_year = p_info.getName();
                    if (!kind_year.equals("")) {
                        usa = true;
                        files_usa.add(kind_year.substring(2).toUpperCase());
                        try {
                            p_usa.downloadFileToDestination(p_info.getUrl(), d_file);
                            p_usa.extractFileAfterDownload(d_file, download_folder);
                        } catch (IOException ex) {
                            Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (String downloaded_zip : downloaded_zips) {
                    String unZipIt = Tools.unZipItCanada(downloaded_zip, download_folder);
                    folder_canada_after_extraction = unZipIt;
                }
            }
        });
    }//GEN-LAST:event_jButton6ActionPerformed

    static ConcurrentHashMap<String, String> mp;

    /**
     * Start integration.
     */
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        output_folder = pathLABEL.getText();
        download_folder = downloadLABEL.getText();
        db_name = field_db_name.getText();
        db_path = output_folder + Tools.OSValidator() + db_name;
        f = new File(db_path);
        if (!f.exists()) {
            f.mkdirs();
        }

        DrugBankConfigurator dbc = new DrugBankConfigurator();
        String db_id_path = Tools.CurrenDirectory() + Tools.OSValidator() + Properties.basic_path_database + Tools.OSValidator() + Properties.basic_file_db_id;
        try {
            mp = dbc.readDatabank(db_id_path);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(mp.size());

        /**
         * Init File For later Updating that DB
         */
        File f_config = new File(db_path + Tools.OSValidator() + Properties.config_database_internal);
        if (!f_config.exists()) {
            try {
                f_config.createNewFile();
                FileWriter fw = new FileWriter(f_config);
                ListModel<Parser_File_Overview> model = overviewLIST.getModel();
                int size = model.getSize();
                for (int i = 0; i < size; i++) {
                    Parser_File_Overview elementAt = model.getElementAt(i);
                    ArrayList<Parser_File_Entry> p_infos = elementAt.getP_infos();
                    for (Parser_File_Entry p_info : p_infos) {
                        String name = p_info.getName();
                        fw.write(name + "\n");
                        fw.flush();
                    }
                    fw.write("\n");
                    fw.flush();
                }
                fw.write(download_folder);
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                f_config.createNewFile();
                FileWriter fw = new FileWriter(f_config);
                for (String loaded_integrated_file : loaded_integrated_files) {
                    fw.write(loaded_integrated_file + "\n");
                    fw.flush();
                }
                ListModel<Parser_File_Overview> model = overviewLIST.getModel();
                int size = model.getSize();
                for (int i = 0; i < size; i++) {
                    Parser_File_Overview elementAt = model.getElementAt(i);
                    ArrayList<Parser_File_Entry> p_infos = elementAt.getP_infos();
                    for (Parser_File_Entry p_info : p_infos) {
                        String name = p_info.getName();
                        fw.write(name + "\n");
                        fw.flush();
                    }
                    fw.write("\n");
                    fw.flush();
                }
                fw.write(download_folder);
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        if (canada && !usa && !ycs) {
            try {
                System.out.println("STARTING");
                pfff = new PreProcessorCanada_F(download_folder, folder_canada_after_extraction);
                pfff.setDB_IDs(mp);
                WorkerCAN workerCan = new WorkerCAN(pfff, f);
                workerCan.execute();
            } catch (IOException ex) {
                Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (canada && usa && !ycs) {
            try {
                pfff = new PreProcessorCanada_F(download_folder, folder_canada_after_extraction);
                pfff.setDB_IDs(mp);
                WorkerUSA w_usa = new WorkerUSA(p_usa, files_usa, f);
                w_usa.setDBID(mp);
                WorkerCAN w_canaa = new WorkerCAN(pfff, f, null, w_usa);
                w_canaa.execute();
            } catch (IOException ex) {
                Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (canada && usa && ycs) {
            try {
                pfff = new PreProcessorCanada_F(download_folder, folder_canada_after_extraction);
                pfff.setDB_IDs(mp);
                WorkerUSA w_ussa = new WorkerUSA(p_usa, files_usa, f);
                w_ussa.setDBID(mp);
                pycs.setDB_ID(mp);
                WorkerCAN w_canaa = new WorkerCAN(pfff, f, pycs, w_ussa);
                w_canaa.execute();
            } catch (IOException ex) {
                Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (canada && !usa && ycs) {
            try {
                pfff = new PreProcessorCanada_F(download_folder, folder_canada_after_extraction);
                pfff.setDB_IDs(mp);
                pycs.setDB_ID(mp);
                WorkerCAN workerCan = new WorkerCAN(pfff, f, pycs);
                workerCan.execute();
            } catch (IOException ex) {
                Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (!canada && usa && !ycs) {
            try {
                WorkerUSA w_usa = new WorkerUSA(p_usa, files_usa, f);
                w_usa.setDBID(mp);
                w_usa.execute();
            } catch (IOException ex) {
                Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (!canada && !usa && ycs) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Start..");
                        pycs.setDB_ID(mp);
                        WorkerYCS w_ycs = new WorkerYCS(pycs, null);
                        w_ycs.execute();
                    } catch (IOException ex) {
                        Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

        } else if (!canada && usa && ycs) {
            try {
                WorkerUSA w_usa = new WorkerUSA(p_usa, files_usa, f);
                w_usa.setDBID(mp);
                WorkerYCS w_ycs = new WorkerYCS(pycs, w_usa);
                w_ycs.execute();
            } catch (IOException ex) {
                Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        ArrayList<Parser_File_Entry> alls = new ArrayList<>();
        List<Parser_File_Entry> selectedValuesList = filesLISTE.getSelectedValuesList();
        for (Parser_File_Entry parser_File_Entry : selectedValuesList) {
            String url = parser_File_Entry.getUrl();
            alls.add(parser_File_Entry);
        }
        Parser_File_Overview pfo = new Parser_File_Overview(pi.getName(), alls);
        dmd_o.addElement(pfo);
        overviewLIST.setModel(dmd_o);
        filesLISTE.setModel(dmd_empty);
        dmd_as = new HashSet<>();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Transfer intern FIles of Parser for Processing into filesLIST.
        filesLISTE.removeAll();
        dmd_a.clear();

        if (empty_set != null) {
            for (Parser_File_Entry all_url : empty_set.values()) {
                String name = all_url.getName();
                if (!loaded_integrated_files.contains(name)) {
                    dmd_a.addElement(all_url);
                }
            }
        }

        filesLISTE.setModel(dmd_a);
        int start = 0;
        int end = filesLISTE.getModel().getSize() - 1;
        if (end >= 0) {
            filesLISTE.setSelectionInterval(start, end);
        }

        empty_set.clear();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed

    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        parserItemChanged();
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    static String output_folder = "";
    static String download_folder = "";
    static String db_path = "";
    static String db_name = "";

    boolean canada = true;
    boolean usa = true;
    boolean ycs = true;

    ArrayList<String> files_usa = new ArrayList<>();
    PreProcessorYCS_F pycs = null;
    PreProcessorCanada_F pfff = null;
    PreProcessorUS_F p_usa = new PreProcessorUS_F();
    File f;

    static String folder_canada_after_extraction = "";
    static HashSet<String> loaded_integrated_files = new HashSet<>();

    DefaultComboBoxModel<String> searchComboBoxList = new DefaultComboBoxModel<>();
    DefaultListModel<String> searchRESULTS = new DefaultListModel<>();
    DefaultTableModel tableMODEL = new DefaultTableModel();

    GraphDatabaseService gdb;

    /**
     * SwingWorker for Integrating Canada Data Set.
     */
    public class WorkerCAN extends SwingWorker<String, String> {

        PreProcessorCanada_F p_can;
        PreProcessorYCS_F p_ycs;
        WorkerUSA w_usa;
        File f;

        public WorkerCAN(PreProcessorCanada_F p_can, File f) throws IOException {
            this.p_can = p_can;
            this.f = f;
            initBeforeStart();
        }

        public WorkerCAN(PreProcessorCanada_F p_can, File f, WorkerUSA w_usaa) throws IOException {
            this.p_can = p_can;
            this.f = f;
            this.w_usa = w_usaa;
            this.p_ycs = null;
            initBeforeStart();
        }

        public WorkerCAN(PreProcessorCanada_F p_can, File f, PreProcessorYCS_F w_ycs) throws IOException {
            this.p_can = p_can;
            this.f = f;
            this.p_ycs = w_ycs;
            initBeforeStart();
        }

        public WorkerCAN(PreProcessorCanada_F p_can, File f, PreProcessorYCS_F w_ycs, WorkerUSA w_usaa) throws IOException {
            this.p_can = p_can;
            this.f = f;
            this.w_usa = w_usaa;
            this.p_ycs = w_ycs;
            initBeforeStart();
        }

        public void initBeforeStart() throws IOException {
            p_can.CreatePreprocessingCanada();
            p_can.InitFileWriting(f);
        }

        @Override
        protected String doInBackground() throws Exception {
            HashSet<CanadaNode> returnAllEntries = p_can.returnAllEntries();
            for (CanadaNode next : returnAllEntries) {
                String processOneNode = p_can.processOneNode(next);
                publish(processOneNode);
            }
            p_can.clearAllMaps();
            p_can.closeDB();
            return null;
        }

        @Override
        protected void done() {
            if (p_ycs != null && w_usa != null) {
                try {
                    new WorkerYCS(p_ycs, w_usa).execute();
                } catch (IOException ex) {
                    Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (p_ycs != null && w_usa == null) {
                try {
                    new WorkerYCS(p_ycs, null).execute();
                } catch (IOException ex) {
                    Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (w_usa != null && p_ycs == null) {
                w_usa.execute();
            }
        }

        @Override
        protected void process(List<String> item) {
            progress_AREA.setText(item.get(item.size() - 1) + "\n");
        }

    }

    /**
     * SwingWorker for Integrating Great Britain Data Set.
     */
    public class WorkerYCS extends SwingWorker<String, String> {

        PreProcessorYCS_F ycs;
        HashMap<String, HashSet<String>> d_folders = new HashMap<>();
        WorkerUSA w_usa;

        public WorkerYCS(PreProcessorYCS_F a, WorkerUSA worker) throws IOException {
            this.ycs = a;
            this.w_usa = worker;
            d_folders = ycs.startPreProcessingAll();
            ycs.initDB();
        }

        @Override
        protected String doInBackground() throws Exception {

            //This is what's called in the .execute method
            if (ycs != null) {
                for (String string : d_folders.keySet()) {
                    String startIntegrateOneFolder = ycs.startIntegrateOneFolder(d_folders, string);
                    publish(startIntegrateOneFolder);
                }
            }
            ycs.closeDB();

            if (w_usa != null) {
                w_usa.execute();
            }

            return null;
        }

        @Override
        protected void process(List<String> item) {
            //This updates the UI
            progress_AREA.setText(item.get(item.size() - 1) + "\n");
        }
    }

    /**
     * SwingWorker for Integrating F.E.A.R.S Data Set.
     */
    public class WorkerUSA extends SwingWorker<String, String> {

        PreProcessorUS_F p_usa;
        PreProcessorCanada_F p_cana;
        ArrayList<String> files_usa;
        ConcurrentHashMap<String, String> mp_db_id;
        File f;

        void setDBID(ConcurrentHashMap<String, String> mp) {
            this.mp_db_id = mp;
        }

        public WorkerUSA(PreProcessorUS_F a, ArrayList<String> filess, File f, PreProcessorCanada_F p_c) throws IOException {
            this.p_usa = a;
            this.files_usa = filess;
            this.p_cana = p_c;
            this.f = f;
        }

        public WorkerUSA(PreProcessorUS_F a, ArrayList<String> filess, File f) throws IOException {
            this.p_usa = a;
            this.files_usa = filess;
            this.p_cana = null;
            this.f = f;
        }

        @Override
        protected String doInBackground() throws Exception {

            String next = files_usa.get(0);
            System.out.println(next);

            SortedSet<String> initializePathsAfterDownload = p_usa.initializePathsAfterDownload(download_folder
                    + Tools.OSValidator() + "ascii" + Tools.OSValidator(), next);

            p_usa.setDB_ID(mp);

            if (next.equals("14Q2")) {
                p_usa.initFile(f);
                p_usa.PreProcessorUS_F_14Q2(initializePathsAfterDownload, 2);
                HashSet<USgenerellNode> receiveMapInternNodes = p_usa.receiveMapInternNodes();
                Iterator<USgenerellNode> iterator1 = receiveMapInternNodes.iterator();
                while (iterator1.hasNext()) {
                    String ProcessOneNode = p_usa.ProcessOneNode(iterator1.next());
                    publish(ProcessOneNode);
                }
                files_usa.remove(next);

            } else {
                p_usa.initFile(f);
                p_usa.PreProcessorUS_F_14Q2(initializePathsAfterDownload, 1);
                HashSet<USgenerellNode> receiveMapInternNodes = p_usa.receiveMapInternNodes();
                Iterator<USgenerellNode> iterator1 = receiveMapInternNodes.iterator();
                while (iterator1.hasNext()) {
                    String ProcessOneNode = p_usa.ProcessOneNode(iterator1.next());
                    publish(ProcessOneNode);
                }
                files_usa.remove(next);
            }
            p_usa.closeDB();
            return null;
        }

        @Override
        protected void process(List<String> item) {
            progress_AREA.setText(item.get(item.size() - 1) + "\n");
        }

        @Override
        protected void done() {
            if (!files_usa.isEmpty()) {
                publish("DONE.");
                System.out.println(files_usa.size());
                WorkerUSA wUSA;
                try {
                    wUSA = new WorkerUSA(p_usa, files_usa, f);
                    wUSA.execute();
                } catch (IOException ex) {
                    Logger.getLogger(GuiMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                // all usa files integrated.
                progress_AREA.setText("Integration Finished.");
            }
        }

    }

    public void parserItemChanged() {
        empty_set.clear();
        Object item = jComboBox1.getSelectedItem();
        Parser_Info pp = (Parser_Info) item;
        pi = pp;
        descriptionAREA.setText(pp.getDesc());
        labelSOURCE.setText(pp.getOrigin_url());
        for (Parser_File_Entry all_url : pp.getAll_urls()) {
            if (empty_set.containsKey(all_url.getName())) {
            } else {
                empty_set.put(all_url.getName(), all_url);
            }
        }
    }

    static Parser_Info pi = null;
    static DefaultListModel dmd_empty = new DefaultListModel<Parser_File_Entry>();
    static DefaultListModel dmd_a = new DefaultListModel<Parser_File_Entry>();
    static DefaultListModel dmd_o = new DefaultListModel<Parser_File_Overview>();
    static HashSet<String> dmd_as = new HashSet<>();
    static HashMap<String, Parser_File_Entry> empty_set = new HashMap<>();

    void initNiceLook() {
        downloadLABEL.setFocusable(false);
        pathLABEL.setFocusable(false);
        parserItemChanged();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel NEWPanel;
    private javax.swing.JButton deleteOverviewBUTTON;
    private javax.swing.JTextArea descriptionAREA;
    private javax.swing.JLabel downloadLABEL;
    private javax.swing.JTextField field_db_name;
    public javax.swing.JList<Parser_File_Entry> filesLISTE;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    public javax.swing.JComboBox<Parser_Info> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel labelSOURCE;
    private javax.swing.JButton openEXISTED_DB_BUTTON;
    public javax.swing.JList<Parser_File_Overview> overviewLIST;
    private javax.swing.JLabel pathLABEL;
    private javax.swing.JTextArea progress_AREA;
    private javax.swing.JLabel progress_Info;
    public static javax.swing.JLabel progress_STATE;
    private javax.swing.JLabel progress_STEP;
    private javax.swing.JButton read_redun_BUTTON;
    private javax.swing.JButton redundancyBUTTON;
    // End of variables declaration//GEN-END:variables
}
