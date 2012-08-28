/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package plcc;

import java.io.*;
import java.util.Properties;
import java.util.Map;
import java.util.Set;

/**
 * This class implements a static class that manages program settings. It supports loading them from and
 * saving them to a text file in 'key = value' format.
 *
 */
public class Settings {
    
    /** The settings which are currently in use. */
    static private Properties cfg;
    
    /** The default settings. */
    static private Properties def;
    
    /** The documentation for the settings. */
    static private Properties doc;
    
    static private final String defaultFile = System.getProperty("user.home") + System.getProperty("file.separator") + ".plcc_settings";
    //static private final String defaultFile = "cfg" + System.getProperty("file.separator") + "plcc_settings.cfg";
    static private String configFile;
    
    /**
     * Resets all properties.
     */
    public static void init() {
        cfg = new Properties();
        def = new Properties();
        doc = new Properties();
        
        setDefaults();
    }
    
    
   
    
    /**
     * Returns the default config file location. Note that this may or may not be in use atm. Use getConfigFile() instead if you
     * need the file that is currently in use.
     * @return the path as a String
     */
    public static String getDefaultConfigFilePath() {
        return(defaultFile);
    }
    
    
    /**
     * Returns the application tag that is printed as a prefix for all output lines.
     * @return the apptag
     */
    public static String getApptag() {
        return("[PLCC] ");
    }
    
    /**
     * Returns the version string. This is NOT guaranteed to be a number.
     * @return the PLCC version
     */
    public static String getVersion() {
        return("0.63");
    }

    /**
     * Loads the properties from the file 'file'. Should be called at the start of main to init the settings. These default
     * values could then be overwritten by command line arguments.
     * @param file the configuration file to load
     * @return whether the settings could be loaded from the specified file
     */
    public static Boolean load(String file) {

        Boolean res = false;
        
        if(file.equals("")) {
            file = defaultFile;
        }

        configFile = file;

        cfg = new Properties();

        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(configFile));
            cfg.load(stream);
            stream.close();
            res = true;
        } catch (Exception e) {
            System.err.println("WARNING: Settings: Could not load settings from properties file '" + configFile + "'." );
            res = false;
        }


        System.out.println("  Loaded " + cfg.size() + " settings from properties file '" + configFile + "'." );
        return(res);
    }


    /**
     * Deletes all currently loaded properties. Note that the settings file is NOT deleted or emptied (unless you call writeToFile() afterwards).
     */
    public static void empty() {
        cfg = new Properties();
    }
    
    
    /**
     * Deletes all default properties. Note that the settings file is NOT deleted or emptied (unless you call writeToFile() afterwards).
     */
    public static void defEmpty() {
        def = new Properties();
    }


    
    /**
     * Reloads the settings from the default settings.
     * @return always true
     */
    public static Boolean resetAll() {
        cfg = new Properties();
        setDefaults();
        
        // make a deep copy of the default settings and assign it to cfg
        for (Map.Entry<Object, Object> entry : def.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            cfg.setProperty(key, value);
        }       
        
        return(true);
    }
    
    
    /**
     * Resets all properties to the default values.
     * @return always true
     */
    public static Boolean setDefaults() {

        def = new Properties();

        // The letter at the 6th position of the setting name always indicates the data type (I=Integer, B=Boolean, F=Float, S=String). It is not
        //  used automatically though, it's just a reminder.

        // Output and performance
        defSet("plcc_I_debug_level", "0", "Debug level");
        defSet("plcc_B_debug_compareSSEContacts", "false", "Whether to compare the computed SSE level contacts to those in the geom_neo output file that is supplied.");
        defSet("plcc_S_debug_compareSSEContactsFile", "geo.dat_ptgl", "The path to the geo.dat file to use for SSE level contact comparison.");                
        defSet("plcc_B_contact_debug_dysfunct", "false", "Atom level contact debugging mode. WARNING: When this is true, plcc will abort after the first few residues and produce wrong overall results!");
        
        defSet("plcc_B_split_dsspfile_warning", "true", "Whether to show a warning about splitting the DSSP file when multiple models are detected in a PDB file.");
        
        defSet("plcc_B_print_contacts", "false", "Whether the residue contacts are printed to stdout (slower)");
        defSet("plcc_B_write_lig_geolig", "true", "Determines whether ligand contacts are included in the <pdbid>.geolig file.");
        defSet("plcc_B_graphimg_header", "true", "Determines whether the graph images contain a header line with info on the graph type, PDBID and chain.");        
        defSet("plcc_B_graphimg_footer", "true", "Determines whether the graph images contain a footer line with info on the SSEs.");        
        defSet("plcc_B_graphimg_legend", "true", "Determines whether the graph images contain a legend that explains the color codes and SSE symbols. This is part of the footer.");        
        defSet("plcc_B_graphimg_legend_always_all", "false", "Determines whether the legend contains all possible edge and vertex types, i.e., even those not occurring in the current image.");        
        defSet("plcc_I_min_fgraph_size_draw", "2", "The minimum size a folding graph must have in order to be drawn. Settings this to 1 or 0 draws all of them, including isolated vertices.");
        defSet("plcc_B_ptgl_text_output", "false", "Whether the PTGL text files (e.g., those required by the bet_neo) are written. Not writing them is faster but this program cannot replace the PTGL tool 'geom_neo' anymore if this is deactivated.");
        defSet("plcc_B_ptgl_geodat_output", "false", "Whether the PTGL text files geo.dat for SSE level contacts is written to a text file.");
        defSet("plcc_B_ramachandran_plot", "false", "Whether a Ramachandran plot is drawn to a file for each chain (slower).");
        defSet("plcc_B_strict_ptgl_behaviour", "true", "Whether plcc should try to strictly mimick the PTGL, including questionable stuff.");
        
        defSet("plcc_B_output_images_dir_tree", "false", "Whether to write output images to a sub directory structure under the output directory instead of writing them in their directly. This is useful if you want to process the whole PDB because most filesystems will get into trouble with tens of thousands of files in a single directory. The directory structure will be chosen from the meta data, i.e., PDB ID, chain, graph type, etc.");
        defSet("plcc_B_output_GML", "true", "Whether to save computed protein graphs to text files in Graph Modelling Language format (GML).");
        defSet("plcc_B_output_TGF", "true", "Whether to save computed protein graphs to text files in Trivial Graph Format (TGF).");
        defSet("plcc_B_output_DOT", "true", "Whether to save computed protein graphs to text files in DOT language Format (DOT).");
        defSet("plcc_B_output_kavosh", "true", "Whether to save computed protein graphs to text files in Kavosh format.");
        defSet("plcc_B_kavosh_format_directed", "true", "Whether to treat the graphs as directed for the Kavosh output. If set to true, each edge (a, b) will appear twice in the output file: once as (a, b) and again as (b, a).");
        
        defSet("plcc_S_temp_dir", ".", "The directory where temporary files can be created. You need write access to it, of course.");
        
        defSet("plcc_S_img_output_format", "PNG", "image output format (valid options: 'PNG', 'JPG')");
        defSet("plcc_S_img_output_fileext", ".png", "file extension of output images (should fit plcc_S_img_output_format more or less, e.g. '.png', '.jpg')");
        
        defSet("plcc_I_img_margin_left", "80", "Size of the left image margin in pixels");
        defSet("plcc_I_img_margin_top", "40", "Size of the top image margin in pixels");
        defSet("plcc_I_img_margin_right", "40", "Size of the right image margin in pixels");
        defSet("plcc_I_img_margin_bottom", "40", "Size of the bottom image margin in pixels");
        defSet("plcc_I_img_vert_dist", "50", "The distance between two consecutive vertices in the output image, in pixels");
        defSet("plcc_I_img_vert_radius", "10", "The radius of a vertex in the output image, in pixels");
        defSet("plcc_I_img_header_height", "40", "The height of the header area in the output image, in pixels");
        defSet("plcc_I_img_footer_height", "120", "The height of the footer area in the output image, in pixels");
        defSet("plcc_S_img_default_font", "TimesRoman", "The default font used in output image labels. This has to be a valid font name, of course.");
        defSet("plcc_I_img_default_font_size", "16", "The default font size used in output images.");
        defSet("plcc_I_img_legend_font_size", "16", "The legend font size used in output images.");        
        defSet("plcc_I_img_text_line_height", "40", "The vertical distance between two lines of text in the image, e.g., in the footer.");
        defSet("plcc_I_img_min_img_height", "160", "The minimum size of the image area where the graph is drawn.");
        defSet("plcc_I_img_min_arc_height", "100", "The minimum size of the arc area within the image area.");
        
        defSet("plcc_I_img_minPageWidth", "800", "The minimum image width in pixels, used in output images.");                                        
        defSet("plcc_I_img_minPageHeight", "600", "The minimum image height in pixels, used in output images.");

        // Folding graphs: "KEY", "ADJ", "RED", "SEQ
        defSet("plcc_B_folding_graphs", "false", "Determines whether folding graphs (connected components of the protein graph) are handled (calculated and drawn).");
        defSet("plcc_B_foldgraphtype_KEY", "true", "Determines whether KEY notation of folding graphs is calculated and drawn (only applies if 'plcc_B_folding_graphs' is 'true').");
        defSet("plcc_B_foldgraphtype_ADJ", "true", "Determines whether ADJ notation of folding graphs is calculated and drawn (only applies if 'plcc_B_folding_graphs' is 'true').");
        defSet("plcc_B_foldgraphtype_RED", "true", "Determines whether RED notation of folding graphs is calculated and drawn (only applies if 'plcc_B_folding_graphs' is 'true').");
        defSet("plcc_B_foldgraphtype_SEQ", "true", "Determines whether SEQ notation of folding graphs is calculated and drawn (only applies if 'plcc_B_folding_graphs' is 'true').");
        
        // SSE graphs: alpha, beta, albe (=alpha+beta), alphalig, betalig, albelig
        defSet("plcc_B_calc_draw_graphs", "true", "Whether the SSE graphs are calculated.");
        defSet("plcc_B_draw_graphs", "true", "Whether the SSE graphs are drawn and written to image files.");
        defSet("plcc_B_graphtype_albe", "true", "Determines whether alpha-beta graphs are drawn");
        defSet("plcc_B_graphtype_albelig", "true", "Determines whether alpha-beta graphs with ligands are drawn");
        defSet("plcc_B_graphtype_alpha", "true", "Determines whether alpha graphs are drawn");
        defSet("plcc_B_graphtype_alphalig", "true", "Determines whether alpha graphs with ligands are drawn");
        defSet("plcc_B_graphtype_beta", "true", "Determines whether alpha-beta graphs are drawn");
        defSet("plcc_B_graphtype_betalig", "true", "Determines whether alpha-beta graphs with ligands are drawn");
        defSet("plcc_S_output_dir", ".", "output directory");


        // Database stuff
        defSet("plcc_B_useDB", "false", "Whether to write statistics to the PostgreSQL database");
        defSet("plcc_S_db_name", "vplg", "Database name");
        defSet("plcc_S_db_host", "127.0.0.1", "Hostname or IP of the DB server");
        defSet("plcc_I_db_port", "5432", "DB server port");
        defSet("plcc_S_db_username", "vplg", "DB username");
        defSet("plcc_S_db_password", "", "DB password (empty if local is TRUST for this user)");


        // PDB and DSSP file parsing and writing
        defSet("plcc_I_defaultModel", "1", "The default model used if multiple models exist in the PDB file");
        defSet("plcc_S_ligSSECode", "L", "The SSE code used to mark a SSE of type ligand");
        defSet("plcc_B_include_coils", "false", "Whether coils (DSSP SSE type ' ') should be considered");
        defSet("plcc_S_coilSSECode", "C", "The amino acid code used to mark a coiled region residue");
        defSet("plcc_S_ligAACode", "J", "The amino acid code used to mark a ligand residue");
        defSet("plcc_B_uglySQLhacks", "false", "Whether to rewrite string data like ligand atom formulas before inserting into DB");
        defSet("plcc_I_ligSAS", "20", "The solvent accessible surface value that is written to the dssplig file for ligands (not used atm)");                
        defSet("plcc_B_force_chain", "false", "Whether to force parsing only a certain PDB chain");
        defSet("plcc_S_forced_chain_id", "A", "The forced chain ID, only used when plcc_B_force_chain is true");
        defSet("plcc_I_lig_min_atoms", "1", "The minimum number of atoms a ligand has to consist of to count as an SSE.");
        defSet("plcc_I_lig_max_atoms", "-1", "The maximum number of atoms a ligand has to consist of to count as an SSE. Set to <0 for unlimited.");

        
        // similarity stuff
        defSet("plcc_B_search_similar", "false", "Whether to activate the program mode which searches for the most similar protein");
        defSet("plcc_S_search_similar_PDBID", "8icd", "Used only when plcc_B_search_similar is true. The protein PDB ID to use as a pattern during the similarity search.");
        defSet("plcc_S_search_similar_chainID", "A", "Used only when plcc_B_search_similar is true. The protein chain ID to use as a pattern during the similarity search.");
        defSet("plcc_S_search_similar_graphtype", "albelig", "Used only when plcc_B_search_similar is true. The graph type to use as a pattern during the similarity search.");
        defSet("plcc_I_search_similar_num_results", "5", "Used only when plcc_B_search_similar is true. The number of results to print (e.g., 3 for the 3 most similar proteins in the DB).");
        defSet("plcc_S_search_similar_method", "string_sse", "Used only when plcc_B_search_similar is true. The similarity measure to use, valid settings: string_sse, graph_set, graph_compat");
        
        

        // Contact definition and computation     
        defSet("plcc_I_atom_radius", "20", "The atom radius of protein atoms in 10th part Angstroem (setting 20 here means 2A)");
        defSet("plcc_I_lig_atom_radius", "30", "The atom radius of ligand atoms in 10th part Angstroem (setting 40 here means 4A)");
        defSet("plcc_B_SSEcontactsAtom", "true", "Defines the contact level used to determine SSE contacts. If set to true, the number of atom level. contacts decides whether an SSE contact exists. If set to false, the residue level contacts are used instead.");
        defSet("plcc_I_max_contacts_per_type", "100", "The maximum number of contacts of a certain type that is counted for a residue pair. Simply set it to something very large if you don't want any limit (Integer.MAX_VALUE comes to mind). The PTGL uses a setting of 1 (so if a pair has 3 B/B cotacts and 2 C/B contacts, it is is counted as 1 B/B and 1 C/B.)");
        defSet("plcc_B_forceBackboneContacts", "false", "Whether all amino acids of a protein graph should be connected sequentially, from N to C terminus, with contacts of type backbone.");
        
        // SSE definitions
        defSet("plcc_I_min_SSE_length", "3", "the minimal length in AAs a non-ligand SSE must have to be considered (PTGL-style filtering of very short SSEs)");
        defSet("plcc_B_merge_helices", "true", "whether to merge different helix types if they are adjacent in the primary structure");
        defSet("plcc_I_merge_helices_max_dist", "0", "the maximal distance in which helices are merged (distance in residue, in the AA sequence). the default value 0 means only directly adjacent SSEs are merged.");
        
        // When spatial relations are computed via double difference, values "close to" zero mean the two SSEs are in mixed relation, i.e.,
        //  neither parallel nor anti-parallel (they may be in 90° angle or something similar). 
        //  The spatre_dd_* values determines the thresholds.
                                                     
        defSet("plcc_I_spatrel_dd_largest_antip_ee", "0", "All values <= the one given here are considered antiparallel. This is for E/E (strand/strand) interactions.");
        defSet("plcc_I_spatrel_dd_smallest_parallel_ee", "1", "All values >= the one given here are considered parallel. This is for E/E (strand/strand) interactions. Note that the range in between these 2 values is considered mixed (none in the case of E/E).");
        
        defSet("plcc_I_spatrel_dd_largest_antip_hh", "-8", "Same as above, but for H/H (helix/helix) interactions.");
        defSet("plcc_I_spatrel_dd_smallest_parallel_hh", "8", "Same as above, but for H/H (helix/helix) interactions.");
        
        defSet("plcc_I_spatrel_dd_largest_antip_he", "-6", "Same as above, but for H/E or E/H interactions.");
        defSet("plcc_I_spatrel_dd_smallest_parallel_he", "6", "Same as above, but for H/E or E/H interactions.");
        
        defSet("plcc_I_spatrel_dd_largest_antip_def", "-7", "Same as above, this is the default for other interactions (e.g., coil/helix).");
        defSet("plcc_I_spatrel_dd_smallest_parallel_def", "7", "Same as above, this is the default for other interactions.");


        return(true);
    }
    
    
    /**
     * Tries to set the key 'key' in the currently used settings to the default value.
     * @param key the key to set from the defaults
     * @return true if it worked out, i.e., such a key exists in the default settings and it was used. False if no such key exists in the default settings hashmap.
     */
    public static Boolean initSingleSettingFromDefault(String key) {
        if(defContains(key)) {
            cfg.setProperty(key, def.getProperty(key));
            return(true);
        }
        else {
            return(false);
        }
    }

    
    /**
     * Creates a new config file in the default location and fills it with the default values defined in the resetAll() function.
     * @return true if it worked out, false otherwise
     */
    public static Boolean createDefaultConfigFile() {
        if(resetAll()) {
            if(writeToFile(defaultFile)) {
                return(true);
            }
        }
        return(false);
    }


    /**
     * Tries to cast the value of the property key 'key' to Integer and return it. If this fails it is considered a fatal error.
     * @param key the key of the properties hashmap
     * @return the value of the key as an Integer
     */
    public static Integer getInteger(String key) {
        Integer i = null;
        String s = get(key);

        try {
            i = Integer.valueOf(s);
        }
        catch (Exception e) {
            System.err.println("ERROR: Settings: Could not load setting '" + key + "' from settings as an Integer, invalid format.");
            System.exit(1);
        }
        return(i);
    }
    
    
    
    /**
     * Determines whether the key 'key' in the currently used settings is at the default setting.
     * @return true if it is in default setting, false if this setting has been changed by the user (via command line or config file)
     */
    public static Boolean isAtDefaultSetting(String key) {
        if(get(key).equals(defGet(key))) {
            return(true);
        }
        return(false);        
    }

    
    /**
     * Tries to cast the value of the property key 'key' to Float and return it. If this fails it is considered a fatal error.
     * @param key the key of the properties hashmap
     * @return the value of the key as a Float
     */
    public static Float getFloat(String key) {
        Float f = null;
        String s = get(key);

        try {
            f = Float.valueOf(s);
        }
        catch (Exception e) {
            System.err.println("ERROR: Settings: Could not load setting '" + key + "' from settings as a Float, invalid format.");
            System.exit(1);
        }
        return(f);
    }


    /**
     * Tries to extract the value of the property key 'key' as a Boolean and return it. If this fails it is considered a fatal error.
     * The only accepted string representations of Booleans are "true" and "false".
     * @param key the key of the properties hashmap
     * @return the value of the key as a Boolean
     */
    public static Boolean getBoolean(String key) {
        Boolean b = null;
        String s = null;

        s = get(key);

        if(s.toLowerCase().equals("true")) {
            return(true);
        }
        else if(s.toLowerCase().equals("false")) {
            return(false);
        }
        else {
            System.err.println("ERROR: Settings: Could not load setting '" + key + "' from settings as Boolean, invalid format.");
            System.exit(1);
            return(false);      // never reached
        }
    }


    /**
     * Returns the path to the currently used config file as a String.
     * @return The config file path.
     */
    public static String getConfigFile() {
        return(configFile);

    }


    /**
     * Prints all settings to STDOUT.
     */
    public static void printAll() {
        System.out.println("Printing all " + cfg.size() + " settings.");

        for (Object key : cfg.keySet()) {
            System.out.println((String)key + "=" + cfg.get(key));
        }

        System.out.println("Printing of all " + cfg.size() + " settings done.");
    }
    
    
    /**
     * Prints all settings to STDOUT.
     */
    public static void defPrintAll() {
        System.out.println("Printing all " + def.size() + " default settings.");

        for (Object key : def.keySet()) {
            System.out.println((String)key + "=" + def.get(key));
        }

        System.out.println("Printing of all " + def.size() + " default settings done.");
    }

    
    
    /**
     * Retrieves the setting with key 'key' from the settings and returns it as a String. Note that it is considered a fatal error if no such key exists. Ask first using 'contains()' if you're not sure. :)
     * @param key the key to get
     * @return the value of the specified key
     */
    public static String get(String key) {
        
        if(cfg.containsKey(key)) {
            return((String)cfg.getProperty(key));
        }
        else {
            System.out.println("INFO: Settings: Setting '" + key + "' not defined in config file. Trying internal default.");
            
            if(initSingleSettingFromDefault(key)) {
                String s = defGet(key);
                
                System.out.println("INFO: Settings: Using internal default value '" + s + "' for setting '" + key + "'. Edit config file to override.");
                
                return(s);                
            } else {
                System.err.println("ERROR: Settings: No config file or default value for setting '" + key + "' exists, setting invalid.");
                System.exit(1);                
                return("ERROR");    // for the IDE
            }                        
        }
        
    }
    
    /**
     * Retrieves the setting with key 'key' from the default settings and returns it as a String. Note that it is considered a fatal error if no such key exists. Ask first using 'contains()' if you're not sure. :)
     * @param key the key to get
     * @return the value of the specified key
     */
    public static String defGet(String key) {

      
        if(def.containsKey(key)) {
            return((String)def.getProperty(key));
        }
        else {
            System.err.println("ERROR: Settings: Could not load default setting '" + key + "' from default settings, no such setting.");
            System.exit(1);
            return(null);        // never reached, for the IDE
        }
        
    }
    

    /**
     * Adds a setting 'key' with value 'value' to the properties object. If a settings with key 'key' already exists, its value gets overwritten.
     * @param key the key which should be set
     * @param value the value for the entry with the given key
     */
    public static void set(String key, String value) {
        cfg.setProperty(key, value);
    }
    
    
    /**
     * Adds a setting 'key' with value 'value' to the default properties object. If a settings with key 'key' already exists, its value gets overwritten.
     * @param key the key which should be set
     * @param value the value for the entry with the given key
     */
    public static void defSet(String key, String value, String documentation) {
        def.setProperty(key, value);
        doc.setProperty(key, documentation);
    }



    /**
     * Determines whether the properties object contains the key 'key'.
     * @param key the key to check for
     * @return true if it contains such a key, false otherwise
     */
    public static Boolean contains(String key) {
        return(cfg.containsKey(key));
    }
    
    /**
     * Determines whether the default properties object contains the key 'key'.
     * @param key the key to check for
     * @return true if it contains such a key, false otherwise
     */
    public static Boolean defContains(String key) {
        return(def.containsKey(key));
    }
    
    
    /**
     * Returns the documentation String for setting 'key'.
     * @param key the setting you want the documentation for
     * @return the documentation as a String if it exists, "n/a" otherwise.
     */
    public static String documentationFor(String key) {
        if(doc.containsKey(key)) {
            return((String)doc.getProperty(key));
        }
        else {            
            return("n/a");
        }        
    }


    /**
     * Saves the current properties to the file 'file' or the default file if 'file' is the empty string ("").
     * @param file the file to write to. If this is the empty String (""), the default file is used instead.
     * @return True if the file was written successfully, false if an error occurred.
     */
    public static Boolean writeToFile(String file) {
        Boolean res = false;
        
        if(file.equals("")) {
            file = defaultFile;
        }

        try {
            cfg.store(new FileOutputStream(file), "These are the settings for plcc. See the documentation for info on them.");
            res = true;
        } catch(Exception e) {
            System.err.println("WARNING: Settings: Could not write current properties to file '" + file + "'.");
            res = false;
        }


        return(res);
    }
    
    
    /**
     * Saves the current default properties to the file 'file' or the default file if 'file' is the empty string ("").
     * TODO: This function could store them sorted alphabetically, with nice comments/documentation.
     * @param file the file to write to. If this is the empty String (""), the default file is used instead.
     * @return True if the file was written successfully, false if an error occurred.
     */
    public static Boolean defWriteToFile(String file) {
        Boolean res = false;
        
        if(file.equals("")) {
            file = defaultFile;
        }

        try {
            def.store(new FileOutputStream(file), "These are the default settings for plcc. See the documentation for info on them.");
            res = true;
        } catch(Exception e) {
            System.err.println("WARNING: Settings: Could not write default settings to file '" + file + "'.");
            res = false;
        }


        return(res);
    }
    
    
    
    /**
     * This function is a kind of documentation generator only, it is not used during regular execution of the
     * software. It writes an example config file which includes documented version of all settings. All settings in the file
     * are set to their default value. This file is not meant to be parsed by the software and doing so manually is useless because
     * the settings represent the internal defaults anyway.
     * @param file the full path where to write the example config file
     * @return whether it worked out
     */
    public static Boolean writeDocumentedDefaultFile(String file) {
        
        String contents = "# This is the documented default config file for plcc.";
        
        for (Map.Entry<Object, Object> entry : def.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            
            contents += "\n\n# " + key + ": " + documentationFor(key);
            contents += "\n" + key + "=" + value;            
        }                

        return(writeStringToFile(file, contents));
    }
    
    
    
    /**
     * Writes the String 'data' into the file  at 'filePath', overwriting the file if it exists
     * and creating it if not.
     * @param filePath the target file system location for the file
     * @param data the String to write to the file
     * @return true if it worked out, false otherwise
     */
    public static Boolean writeStringToFile(String filePath, String data) {

        FileWriter fw = null;
        PrintWriter pw = null;
        Boolean res = false;

        try {
            fw = new FileWriter(filePath);
            pw = new PrintWriter(fw);
            pw.print(data);
            pw.close();
            res = true;
        }
        catch (Exception e) {
            System.err.println("WARNING: Could not write to file '" + filePath + "'.");
            res = false;
        }
       
        try {
            fw.close();
        } catch(Exception ex) {
            System.err.println("WARNING: Could not close FileWriter for file '" + filePath + "'.");
        }
        return(res);
    }                        
    
}
