/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package vpg;

import java.awt.Container;
import java.awt.Desktop;
import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;

/**
 *
 * @author ts
 */
public class VpgMainFrame extends javax.swing.JFrame {
           
    
    String appTag = "[VPG] ";
    
    /**
     * Creates new form VpgMainFrame
     */
    public VpgMainFrame() {
        
        System.out.println(appTag + "VPG -- A frontend for VPLG");
        System.out.println(appTag + "Loading settings...");
        
        
        // The settings are defined in the Settings class. They are loaded from the config file below and can then be overwritten
        //  by command line arguments.
        Settings.init();
        

        if(Settings.load("")) {             // Empty string means that the default file of the Settings class is used
            //System.out.println("  Settings loaded from properties file.");
        }
        else {
            System.err.println(appTag + "WARNING: Could not load settings from properties file, trying to create it.");
            if(Settings.createDefaultConfigFile()) {
                System.out.println(appTag + "  Default config file created, will use it from now on.");
            } else {
                System.err.println(appTag + "WARNING: Could not create default config file, check permissions. Using internal default settings.");
            }
            Settings.resetAll();        // init settings with internal defaults for this run
        }

        String vpgVersion = Settings.getVersion();
        System.out.println(appTag + "Starting VPG version " + vpgVersion + " ...");
        initComponents();
        
        if(this.essentialSettingsOK()) {
            this.jStatusLabel.setText("VPG version " + vpgVersion + " ready.");
        } else {
            this.jStatusLabel.setText("VPG version " + vpgVersion + " started. Essential settings missing, please use Edit => Settings to fix the configuration.");            
        }
        
        this.minorSettingsCheck();
        
    }
    
    
    /**
     * Simple helper function that determines whether basic settings are ok. Used for status bar
     * notification of broken settings.
     * @return true if the essential stuff is configured correctly, false otherwise.
     */
    public Boolean essentialSettingsOK() {
        Boolean allgood = true;
        
        File file;
        
        file = new File(Settings.get("vpg_S_input_dir"));
        if(! (file.canRead() && file.isDirectory())) {
            allgood = false;
            System.out.println("[VPG] WARNING: Input directory not set correctly.");
        }
        
        file = new File(Settings.get("vpg_S_output_dir"));
        if(! (file.canWrite() && file.isDirectory())) {
            allgood = false;
            System.out.println("[VPG] WARNING: Output directory not set correctly.");
        }
        
        file = new File(Settings.get("vpg_S_path_plcc"));
        if(! (file.canRead() && file.isFile())) {
            allgood = false;
            System.out.println("[VPG] WARNING: Path to plcc.jar not set correctly. This program is essential, please fix.");
        }
        
        return allgood;
    }
    
    
    /**
     * Simple helper function that determines whether non-essential settings are ok.
     * @return true if some non-essential stuff is configured correctly, false otherwise.
     */
    public Boolean minorSettingsCheck() {
        Boolean allgood = true;
        
        File file;                
        
        file = new File(Settings.get("vpg_S_path_splitpdb"));
        if(! (file.canRead() && file.isFile())) {
            allgood = false;
            System.out.println("[VPG] INFO: Path to splitpdb.jar not set correctly. This program is optional.");
        }
        
        file = new File(Settings.get("vpg_S_path_dssp"));
        if(! (file.canRead() && file.isFile())) {
            allgood = false;
            System.out.println("[VPG] INFO: Path to dsspcmbi not set correctly. This program is optional.");
        }
        
        return allgood;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jStatusBarPanel = new javax.swing.JPanel();
        jStatusLabel = new javax.swing.JLabel();
        jPanelMainContent = new javax.swing.JPanel();
        jLabelWelcomeText1 = new javax.swing.JLabel();
        jLabelWelcomeLogo = new javax.swing.JLabel();
        jLabelWelcomeTextWhatsup = new javax.swing.JLabel();
        jComboBoxTasks = new javax.swing.JComboBox();
        jButtonStartTask = new javax.swing.JButton();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemCreateGraphs = new javax.swing.JMenuItem();
        jMenuItemOpenImage = new javax.swing.JMenuItem();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuInput = new javax.swing.JMenu();
        jMenuItemDownloadFiles = new javax.swing.JMenuItem();
        jMenuItemGenerateDsspFile = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemSettings = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemAbout = new javax.swing.JMenuItem();
        jMenuManual = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VPG -- Frontend for Visualization of Protein Ligand Graphs");
        setMinimumSize(new java.awt.Dimension(400, 300));
        setName("VPG Main Window"); // NOI18N

        jStatusBarPanel.setBackground(new java.awt.Color(210, 210, 210));

        jStatusLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        jStatusLabel.setText("VPG ready.");

        javax.swing.GroupLayout jStatusBarPanelLayout = new javax.swing.GroupLayout(jStatusBarPanel);
        jStatusBarPanel.setLayout(jStatusBarPanelLayout);
        jStatusBarPanelLayout.setHorizontalGroup(
            jStatusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
        );
        jStatusBarPanelLayout.setVerticalGroup(
            jStatusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jStatusLabel)
        );

        jLabelWelcomeText1.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabelWelcomeText1.setText("Welcome to the Visualization of Protein Ligand Graphs software.");

        jLabelWelcomeLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelWelcomeLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/vplg_logo.png"))); // NOI18N
        jLabelWelcomeLogo.setToolTipText("VPLG logo");

        jLabelWelcomeTextWhatsup.setText("What would you like to do?");

        jComboBoxTasks.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Create a protein graph", "Download a PDB or DSSP file", "Create a DSSP file from a PDB file", "View existing graph images" }));

        jButtonStartTask.setText("Let's start!");
        jButtonStartTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartTaskActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMainContentLayout = new javax.swing.GroupLayout(jPanelMainContent);
        jPanelMainContent.setLayout(jPanelMainContentLayout);
        jPanelMainContentLayout.setHorizontalGroup(
            jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabelWelcomeTextWhatsup)
                    .addComponent(jLabelWelcomeLogo)
                    .addComponent(jLabelWelcomeText1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMainContentLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jComboBoxTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(jButtonStartTask)))
                .addContainerGap(148, Short.MAX_VALUE))
        );
        jPanelMainContentLayout.setVerticalGroup(
            jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelWelcomeText1)
                .addGap(18, 18, 18)
                .addComponent(jLabelWelcomeLogo)
                .addGap(39, 39, 39)
                .addComponent(jLabelWelcomeTextWhatsup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonStartTask)
                    .addComponent(jComboBoxTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(68, Short.MAX_VALUE))
        );

        jMenuFile.setMnemonic('f');
        jMenuFile.setText("File");

        jMenuItemCreateGraphs.setMnemonic('g');
        jMenuItemCreateGraphs.setText("Create protein graph");
        jMenuItemCreateGraphs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCreateGraphsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemCreateGraphs);

        jMenuItemOpenImage.setMnemonic('v');
        jMenuItemOpenImage.setText("Graph Image Viewer");
        jMenuItemOpenImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenImageActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpenImage);

        jMenuItemExit.setMnemonic('x');
        jMenuItemExit.setText("Exit");
        jMenuItemExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItemExitMouseClicked(evt);
            }
        });
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar.add(jMenuFile);

        jMenuInput.setMnemonic('i');
        jMenuInput.setText("Input");

        jMenuItemDownloadFiles.setMnemonic('d');
        jMenuItemDownloadFiles.setText("Download input files");
        jMenuItemDownloadFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDownloadFilesActionPerformed(evt);
            }
        });
        jMenuInput.add(jMenuItemDownloadFiles);

        jMenuItemGenerateDsspFile.setMnemonic('g');
        jMenuItemGenerateDsspFile.setText("Generate DSSP file");
        jMenuItemGenerateDsspFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateDsspFileActionPerformed(evt);
            }
        });
        jMenuInput.add(jMenuItemGenerateDsspFile);

        jMenuBar.add(jMenuInput);

        jMenuEdit.setMnemonic('e');
        jMenuEdit.setText("Edit");

        jMenuItemSettings.setMnemonic('s');
        jMenuItemSettings.setText("Settings");
        jMenuItemSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSettingsActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemSettings);

        jMenuBar.add(jMenuEdit);

        jMenuHelp.setMnemonic('h');
        jMenuHelp.setText("Help");

        jMenuItemAbout.setMnemonic('a');
        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuManual.setMnemonic('o');
        jMenuManual.setText("Online Documentation");
        jMenuManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuManualActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuManual);

        jMenuBar.add(jMenuHelp);

        setJMenuBar(jMenuBar);
        jMenuBar.getAccessibleContext().setAccessibleName("Menu bar");
        jMenuBar.getAccessibleContext().setAccessibleDescription("The menu bar of VPG.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jStatusBarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanelMainContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelMainContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStatusBarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemExitMouseClicked
        System.out.println(appTag + "Exiting.");
        this.dispose();
        System.exit(0);
    }//GEN-LAST:event_jMenuItemExitMouseClicked

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed

        System.out.println(appTag + "Exiting.");
        this.dispose();
        System.exit(0);
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuItemSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSettingsActionPerformed

        VpgSettingsFrame setFrame = new VpgSettingsFrame();
        setFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        setFrame.setVisible(true);
    }//GEN-LAST:event_jMenuItemSettingsActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed

        VpgAboutFrame abFrame = new VpgAboutFrame();
        abFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        abFrame.setVisible(true);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuManualActionPerformed
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(Settings.get("vpg_S_online_manual_url")));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "ERROR: Cannot open manual URI in browser: '" + e.getMessage() + "'.", "VPLG Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("ERROR: Cannot open manual URI in browser: '" + e.getMessage() + "'.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "ERROR: Cannot open manual in browser, Desktop API not supported on this Java VM.", "VPLG Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("ERROR: Cannot open manual in browser, Desktop API not supported on this Java VM.");
        }
    }//GEN-LAST:event_jMenuManualActionPerformed

    private void jMenuItemOpenImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenImageActionPerformed
          
        /*
        class PngFilter extends javax.swing.filechooser.FileFilter {
            
            @Override public boolean accept(File file) {
                String filename = file.getName();
                return (filename.endsWith(".png"));
            }
            
            @Override public String getDescription() {
                return "*.png";
            }
        }
        
        
        File defaultDir = new File(Settings.get("vpg_S_output_dir"));
        File selectedFile;
        JFileChooser fc = new JFileChooser(defaultDir);        
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);       
        fc.addChoosableFileFilter(new PngFilter());
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
            if(selectedFile.canRead()) {
                VpgImageFrame imgFrame = new VpgImageFrame();
                imgFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
                imgFrame.setVisible(true);
            }
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {
            fc.setVisible(false);
        }
        * 
        */
        /*
        VpgImageFrame imgFrame = new VpgImageFrame();
        JTree tree = new JTree(FileTree.addTree("."));
        imgFrame.getFiletystemPanel().add(tree);
        System.out.println(appTag + "Opened VpgImageFrame from VpgMainFrame.");
        imgFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        imgFrame.setVisible(true);
         * 
         */
        VpgGraphViewerFrame fs = new VpgGraphViewerFrame(System.getProperty("user.home"));
        fs.setDefaultCloseOperation(HIDE_ON_CLOSE);
        fs.setVisible(true);
        
        
    }//GEN-LAST:event_jMenuItemOpenImageActionPerformed

    private void jButtonStartTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartTaskActionPerformed

        String selection = (String) this.jComboBoxTasks.getSelectedItem();
        //System.out.println("Selected item was '" + selection + "'.");
        
        if(selection.equals("Create a protein graph")) {
            System.out.println("[VPG] Starting module to create protein graphs...");            
            new VpgCreateGraphFrame().setVisible(true);
        }
        else if(selection.equals("Download a PDB or DSSP file")) {
            System.out.println("[VPG] Starting module to download input files...");
            VpgDownloadFrame dlf = new VpgDownloadFrame();
            dlf.setDefaultCloseOperation(HIDE_ON_CLOSE);
            dlf.setVisible(true);
        }
        else if(selection.equals("Create a DSSP file from a PDB file")) {
            System.out.println("[VPG] Starting module to generate DSSP file using dsspcmbi...");
            new VpgGenerateDsspFileFrame().setVisible(true);
        }
        else if(selection.equals("View existing graph images")) {
            System.out.println("[VPG] Starting module to view graph images...");
            VpgGraphViewerFrame fs = new VpgGraphViewerFrame(System.getProperty("user.home"));
            fs.setDefaultCloseOperation(HIDE_ON_CLOSE);
            fs.setVisible(true);
        }        
        else {
            System.out.println("[VPG] I don't care whether you want to do that.");
        }
    }//GEN-LAST:event_jButtonStartTaskActionPerformed

    private void jMenuItemCreateGraphsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCreateGraphsActionPerformed
       
        new VpgCreateGraphFrame().setVisible(true);
    }//GEN-LAST:event_jMenuItemCreateGraphsActionPerformed

    private void jMenuItemDownloadFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDownloadFilesActionPerformed
        
        new VpgDownloadFrame().setVisible(true);
    }//GEN-LAST:event_jMenuItemDownloadFilesActionPerformed

    private void jMenuItemGenerateDsspFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateDsspFileActionPerformed

        new VpgGenerateDsspFileFrame().setVisible(true);
    }//GEN-LAST:event_jMenuItemGenerateDsspFileActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {                        
        
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VpgMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VpgMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VpgMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VpgMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new VpgMainFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonStartTask;
    private javax.swing.JComboBox jComboBoxTasks;
    private javax.swing.JLabel jLabelWelcomeLogo;
    private javax.swing.JLabel jLabelWelcomeText1;
    private javax.swing.JLabel jLabelWelcomeTextWhatsup;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenu jMenuInput;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemCreateGraphs;
    private javax.swing.JMenuItem jMenuItemDownloadFiles;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemGenerateDsspFile;
    private javax.swing.JMenuItem jMenuItemOpenImage;
    private javax.swing.JMenuItem jMenuItemSettings;
    private javax.swing.JMenuItem jMenuManual;
    private javax.swing.JPanel jPanelMainContent;
    private javax.swing.JPanel jStatusBarPanel;
    private javax.swing.JLabel jStatusLabel;
    // End of variables declaration//GEN-END:variables
}
