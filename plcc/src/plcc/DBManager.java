/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import tools.DP;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A database manager class that is used to create and maintain a connection to a PostgreSQL database server.
 * 
 * @author ts
 */
public class DBManager {

    static String dbName;
    static String dbHost;
    static Integer dbPort;       // default for PostgreSQL is 5432
    static String dbURL;
    static String dbUsername;
    static String dbPassword;
    static Statement sql;
    static DatabaseMetaData dbmd;
    static String dbDriver;
    static Connection dbc;
    // table names
    
    /** Name of the table which stores info on a PDB protein, identified by the PDB ID. */
    static String tbl_protein = "plcc_protein";
    static String tbl_chain = "plcc_chain";
    static String tbl_sse = "plcc_sse";
    static String tbl_proteingraph = "plcc_graph";
    static String tbl_foldinggraph = "plcc_foldinggraph";
    static String tbl_complexgraph = "plcc_complexgraph";
    static String tbl_graphletcount = "plcc_graphlets";
    static String tbl_nm_ssetoproteingraph = "plcc_nm_ssetoproteingraph";
    static String tbl_nm_ssetofoldinggraph = "plcc_nm_ssetofoldinggraph";
    
    /** Name of the table which stores info on SSE types, e.g., alpha-helix, beta-strand and ligand. */
    static String tbl_ssetypes = "plcc_ssetypes";
    
    /** Name of the table which stores info on intra-chain SSE contact types, e.g., parallel, anti-parallel, mixed or ligand. */
    static String tbl_contacttypes = "plcc_contacttypes";
    
    /** Name of the table which stores info on inter-chain SSE contact types, e.g., van-der-Waals or disulfide bridge. */
    static String tbl_complexcontacttypes = "plcc_complexcontacttypes";
    
    /** Name of the table which stores info on graph types, e.g., alpha-graph, beta-graph, alphabeta-graph, alphalig-graph, and so on. */
    static String tbl_graphtypes = "plcc_graphtypes";
    
    static String tbl_ssecontact = "plcc_contact";
    static String tbl_ssecontact_complexgraph = "plcc_ssecontact_complexgraph";
    static String tbl_complex_contact_stats = "plcc_complex_contact";
    
    static String view_ssecontacts = "plcc_view_ssetype_contacts";
    static String view_graphs = "plcc_view_graphs";

    /**
     * Sets the database address and the credentials to access the DB.
     * @return True if the connection could be established, false otherwise.
     */
    public static Boolean init(String db, String host, Integer port, String user, String password) {

        dbName = db;
        dbHost = host;
        dbPort = port;


        dbURL = "jdbc:postgresql://" + host + ":" + port + "/" + db;
        dbUsername = user;
        dbPassword = password;

        dbDriver = "org.postgresql.Driver";

        try {
            Class.forName(dbDriver);
        } catch (Exception e) {
            System.err.println("ERROR: Could not load JDBC driver '" + dbDriver + "'. Is the correct db driver installed at lib/postgresql-jdbc.jar?");
            System.err.println("ERROR: See the README for more info on getting the proper driver for your PostgreSQL server and Java versions.'");
            System.err.println("ERROR: Message was: '" + e.getMessage() + "'.");
            System.exit(1);
            
        }

        Boolean conOK = connect();
        return (conOK);

    }

    /**
     * Connects to the database using the DB address and credentials defined during the call to init().
     * @return Whether a connection to the DB could be established.
     */
    private static Boolean connect() {

        Boolean conOK = false;

        try {
            dbc = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
            dbmd = dbc.getMetaData();
            sql = dbc.createStatement();
            conOK = true;
        } catch (Exception e) {
            //System.err.println("ERROR: Could not connect to database at '" + dbURL + "'.");
            System.err.println("ERROR: Could not connect to database at '" + dbURL + "' with user '" + dbUsername + "'.");
            System.err.println("ERROR: The error message was: '" + e.getMessage() + "'.");
            System.exit(1);
        }

        try {
            System.out.println("Connection to " + dbmd.getDatabaseProductName() + " " + dbmd.getDatabaseProductVersion() + " successful.");
        } catch (Exception e) {
            // Something didn't work out if this failed.
            conOK = false;
        }

        return (conOK);
    }

    /**
     * Checks whether a DB connection exists. Tries to establish it if not.
     * @return: Whether a DB connection could be established in the end.
     */
    private static Boolean ensureConnection() {

        try {
            dbc.getMetaData();
        } catch (Exception e) {
            return (connect());
        }

        return (true);
    }
        

    /**
     * Determines whether the underlying DBMS supports transactions.
     * @return true if it does, false otherwise
     */
    boolean supportsTransactions() throws SQLException {

        ensureConnection();

        return (dbc.getMetaData().supportsTransactions());
    }

    /**
     * Executes the SQL insert query 'query' and returns the number of inserted rows if it succeeds, -1 otherwise.
     * WARNING: This does not do any checks on the input so do not expose this to user input.
     * @return The number of inserted rows if it succeeds, -1 otherwise.
     */
    public static int doInsertQuery(String query) {

        ensureConnection();

        PreparedStatement ps = null;
        try {
            ps = dbc.prepareStatement(query);
            return (ps.executeUpdate());          // num rows affected
        } catch (Exception e) {
            DP.getInstance().w("doInsertQuery(): SQL statement '" + query + "' failed.");
            DP.getInstance().w("The error message was: '" + e.getMessage() + "'.");
            return (-1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    DP.getInstance().w("doInsertQuery(): Could not close prepared statement.");
                    DP.getInstance().w("The error message was: '" + ex.getMessage() + "'.");
                }
            }
        }
    }

    /**
     * Executes the SQL update query 'query' and returns the number of updated rows if it succeeds, -1 otherwise.
     * WARNING: This does not do any checks on the input so do not expose this to user input.
     * @return The number of updated rows if it succeeds, -1 otherwise.
     */
    public static int doUpdateQuery(String query) {

        ensureConnection();

        PreparedStatement ps = null;
        try {
            ps = dbc.prepareStatement(query);
            return (ps.executeUpdate());          // num rows affected
        } catch (Exception e) {
            DP.getInstance().w("doUpdateQuery(): SQL statement '" + query + "' failed.");
            DP.getInstance().w("The error message was: '" + e.getMessage() + "'.");
            return (-1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    DP.getInstance().w("doUpdateQuery(): Could not close prepared statement.");
                    DP.getInstance().w("The error message was: '" + ex.getMessage() + "'.");
                }
            }
        }
    }

    /**
     * Executes the SQL delete query 'query' and returns the number of updated rows if it succeeds, -1 otherwise.
     * WARNING: This does not do any checks on the input so do not expose this to user input.
     * @return The number of deleted rows if it succeeds, -1 otherwise.
     */
    public static int doDeleteQuery(String query) {

        ensureConnection();

        PreparedStatement ps = null;
        try {
            ps = dbc.prepareStatement(query);
            return ps.executeUpdate();           // num rows affected
        } catch (Exception e) {
            DP.getInstance().w("doDeleteQuery(): SQL statement '" + query + "' failed.");
            DP.getInstance().w("The error message was: '" + e.getMessage() + "'.");
            return (-1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    DP.getInstance().w("doDeleteQuery(): Could not close prepared statement.");
                    DP.getInstance().w("The error message was: '" + ex.getMessage() + "'.");
                }
            }
        }
    }

    /**
     * Executes a select query. WARNING: This does not do any checks on the input so do not expose this to user input.
     * @param query the SQL query
     * @return the data as 2D matrix of Strings.
     */
    public static ArrayList<ArrayList<String>> doSelectQuery(String query) {

        ensureConnection();

        ResultSet rs = null;
        PreparedStatement ps = null;
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData;
        ArrayList<String> rowData = null;

        int count;

        try {
            ps = dbc.prepareStatement(query);
            rs = ps.executeQuery();
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();
            tableData = new ArrayList<ArrayList<String>>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            return (tableData);
        } catch (Exception e) {
            DP.getInstance().w("doDeleteQuery(): SQL statement '" + query + "' failed.");
            DP.getInstance().w("The error message was: '" + e.getMessage() + "'.");
            //e.printStackTrace();
            System.exit(1);
            return (null);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    DP.getInstance().w("doSelectQuery(): Could not close result set.");
                    DP.getInstance().w("The error message was: '" + ex.getMessage() + "'.");
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    DP.getInstance().w("doSelectQuery(): Could not close prepared statement.");
                    DP.getInstance().w("The error message was: '" + ex.getMessage() + "'.");
                }
            }
        }

    }

    /**
     * Closes the DB connection.
     * @return Whether the connection could be closed.
     */
    public static Boolean closeConnection() {

        if (dbc != null) {
            try {
                if (!dbc.isClosed()) {
                    if (!dbc.getAutoCommit()) {
                        dbc.commit();
                    }
                    dbc.close();
                    return (true);
                } else {
                    return (true);        // already closed
                }
            } catch (Exception e) {
                DP.getInstance().w("closeConnection(): Could not close DB connection.");
                DP.getInstance().w("The error message was: '" + e.getMessage() + "'.");
                return (false);
            }
        } else {
            // there is no connection object
            return (true);
        }
    }

    /**
     * Drops (=deletes) all statistics tables in the database.
     * @return whether it worked out
     */
    public static Boolean dropTables() {
        ensureConnection();
        Boolean res = false;

        try {
            doDeleteQuery("DROP TABLE " + tbl_protein + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_chain + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_sse + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_ssecontact + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_ssecontact_complexgraph + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_complex_contact_stats + " CASCADE;");            
            doDeleteQuery("DROP TABLE " + tbl_proteingraph + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_foldinggraph + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_complexgraph + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_graphletcount + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_nm_ssetoproteingraph + ";");
            doDeleteQuery("DROP TABLE " + tbl_nm_ssetofoldinggraph + ";");
            doDeleteQuery("DROP TABLE " + tbl_graphtypes + ";");
            doDeleteQuery("DROP TABLE " + tbl_contacttypes + ";");
            doDeleteQuery("DROP TABLE " + tbl_complexcontacttypes + ";");
            doDeleteQuery("DROP TABLE " + tbl_ssetypes + ";");

            // The indices get dropped with the tables.
            //doDeleteQuery("DROP INDEX plcc_idx_chain_insert;");
            //doDeleteQuery("DROP INDEX plcc_idx_sse_insert;");

            res = true;      // Not really, need to check all of them

        } catch (Exception e) {
            res = false;
        }

        return (res);

    }

    /**
     * Creates the statistics tables in the database.
     * Note that you have to create the DB and the DB user and set the credentials in the plcc config file.
     * 
     * To create the DB stuff, use the 'psql' shell command as the DB admin account (usually 'postgres' on UNIX).
     * 
     * postgres@srv> psql
     * psql> CREATE ROLE vplg WITH LOGIN;
     * psql> CREATE DATABASE vplg OWNER vplg;
     * psql> \q
     * postgre@srv>
     * 
     * @return Whether they could be created.
     */
    public static Boolean createTables() {


        ensureConnection();
        Boolean res = false;

        try {
            // create tables
            
            // various types encoded by integers. these tables should be removed in the future and the values stored as string directly instead.
            doInsertQuery("CREATE TABLE " + tbl_ssetypes + " (ssetype_id int not null primary key,  ssetype_text text not null);");
            doInsertQuery("CREATE TABLE " + tbl_contacttypes + " (contacttype_id int not null primary key,  contacttype_text text not null);");
            doInsertQuery("CREATE TABLE " + tbl_complexcontacttypes + " (complexcontacttype_id int not null primary key,  complexcontacttype_text text not null);");
            doInsertQuery("CREATE TABLE " + tbl_graphtypes + " (graphtype_id int not null primary key,  graphtype_text text not null);");
            
            doInsertQuery("CREATE TABLE " + tbl_protein + " (pdb_id varchar(4) primary key, header varchar(200) not null, title varchar(400) not null, experiment varchar(200) not null, keywords varchar(400) not null, resolution real not null);");
            doInsertQuery("CREATE TABLE " + tbl_chain + " (chain_id serial primary key, chain_name varchar(2) not null, mol_name varchar(200) not null, organism_scientific varchar(200) not null, organism_common varchar(200) not null, pdb_id varchar(4) not null references " + tbl_protein + " ON DELETE CASCADE);");
            doInsertQuery("CREATE TABLE " + tbl_sse + " (sse_id serial primary key, chain_id int not null references " + tbl_chain + " ON DELETE CASCADE, dssp_start int not null, dssp_end int not null, pdb_start varchar(20) not null, pdb_end varchar(20) not null, sequence varchar(2000) not null, sse_type int not null references " + tbl_ssetypes + " ON DELETE CASCADE, lig_name varchar(5), position_in_chain int);");
            doInsertQuery("CREATE TABLE " + tbl_ssecontact + " (contact_id serial primary key, sse1 int not null references " + tbl_sse + " ON DELETE CASCADE, sse2 int not null references " + tbl_sse + " ON DELETE CASCADE, contact_type int not null references " + tbl_contacttypes + " ON DELETE CASCADE, check (sse1 < sse2));");
            doInsertQuery("CREATE TABLE " + tbl_ssecontact_complexgraph + " (ssecontact_complexgraph_id serial primary key, sse1 int not null references " + tbl_sse + " ON DELETE CASCADE, sse2 int not null references " + tbl_sse + " ON DELETE CASCADE, complex_contact_type int not null references " + tbl_complexcontacttypes + " ON DELETE CASCADE check (sse1 < sse2));");            
            doInsertQuery("CREATE TABLE " + tbl_complex_contact_stats + " (complex_contact_id serial primary key, chain1 int not null references " + tbl_chain + " ON DELETE CASCADE, chain2 int not null references " + tbl_chain + " ON DELETE CASCADE, contact_num_HH int not null, contact_num_HS int not null, contact_num_HL int not null, contact_num_SS int not null, contact_num_SL int not null, contact_num_LL int not null, contact_num_DS int not null);");
            doInsertQuery("CREATE TABLE " + tbl_proteingraph + " (graph_id serial primary key, chain_id int not null references " + tbl_chain + " ON DELETE CASCADE, graph_type int not null references " + tbl_graphtypes + ", graph_string_gml text, graph_string_kavosh text, graph_string_dotlanguage text, graph_string_plcc text, graph_string_ptgl_adj text, graph_string_ptgl_red text, graph_string_ptgl_key text, graph_string_ptgl_seq text, graph_image_png text, graph_image_svg text, graph_image_adj_svg text, graph_image_adj_png text, graph_image_red_svg text, graph_image_red_png text, graph_image_key_svg text, graph_image_key_png text, graph_image_seq_svg text, graph_image_seq_png text, sse_string text);");
            doInsertQuery("CREATE TABLE " + tbl_foldinggraph + " (foldinggraph_id serial primary key, parent_graph_id int not null references " + tbl_proteingraph + " ON DELETE CASCADE, fg_number int not null, graph_string_gml text, graph_string_kavosh text, graph_string_dotlanguage text, graph_string_plcc text, graph_string_ptgl_adj text, graph_string_ptgl_red text, graph_string_ptgl_key text, graph_string_ptgl_seq text, graph_image_png text, graph_image_svg text, graph_image_adj_svg text, graph_image_adj_png text, graph_image_red_svg text, graph_image_red_png text, graph_image_key_svg text, graph_image_key_png text, graph_image_seq_svg text, graph_image_seq_png text, sse_string text);");
            doInsertQuery("CREATE TABLE " + tbl_complexgraph + " (complexgraph_id serial primary key, pdb_id varchar(4) not null references " + tbl_protein + " ON DELETE CASCADE, graph_string_gml text, graph_string_kavosh text, graph_image_svg text, graph_image_png text);");

            /**
             * The contents of the graphlet_counts[55] SQL array is as follows (from Tatianas thesis, pp. 36-37):
             *   The structure of the feature vector with the length 55:
             *   - The ﬁrst 29 entries are unlabeled graphlets in the order of their appearance on the
             *   ﬁgure 3.1 of Tatianas bachelor thesis:
             *       – 2 entries: unlabeled graphlets g1 , g2 with 3 vertices;
             *       – 6 entries: unlabeled graphlets g1 , g6 with 4 vertices;
             *       – 21 entries: unlabeled graphlets g1 , g21 with 5 vertices.
             *   - The rest 26 entries are the proposed labeled graphlets:
             *       – 4 entries: all labellings of the g1 (triangle) with 3 vertices: [HHH, HHE, HEE, EEE];
             *       – 6 entries: all labellings of the g2 (2-path) with 3 vertices: [HHH, HHE, EHE, HEH, HEE, EEE];
             *
             *       – 10 entries: all labellings of the g6 (3-path) with 4 vertices: [HHHH, HHHE, EHHE, HHEH, HHEE, EHEH, EHEE, HEEH, HEEE, EEEE];
             *       – 4 entries: biologically-motivated graphlets, which encode the structural motifs, see the illustration 3.2, in the following order:
             *          ∗ β − α − β motif: [EaHaE],
             *          ∗ β − β − β motif: [EaEaE],
             *          ∗ Greek key motif: [EaEaEaE],
             *          ∗ 4 parallel not necessarily adjacent β sheets as a part of a β-barrel structure: [EpEpEpE] with “H” standing for α-helices, “E” for β-sheets, “p” for parallel and “a” for anti-parallel orientation;
             *       – 2 entries: all labelings of the graphlet g1 with one vertex, or simply the vertices with the label “H” and the vertices with the label “E” added to get the distribution vertex labels.
             *
             */
            doInsertQuery("CREATE TABLE " + tbl_graphletcount + " (graphlet_id serial primary key, graph_id int not null references " + tbl_proteingraph + " ON DELETE CASCADE, graphlet_counts int[55] not null);");
            doInsertQuery("CREATE TABLE " + tbl_nm_ssetoproteingraph + " (ssetoproteingraph_id serial primary key, sse_id int not null references " + tbl_sse + " ON DELETE CASCADE, graph_id int not null references " + tbl_proteingraph + " ON DELETE CASCADE, position_in_graph int not null);");
            doInsertQuery("CREATE TABLE " + tbl_nm_ssetofoldinggraph + " (ssetofoldinggraph_id serial primary key, sse_id int not null references " + tbl_sse + " ON DELETE CASCADE, foldinggraph_id int not null references " + tbl_foldinggraph + " ON DELETE CASCADE, position_in_graph int not null);");
                                                

            // set constraints
            doInsertQuery("ALTER TABLE " + tbl_protein + " ADD CONSTRAINT constr_protein_uniq UNIQUE (pdb_id);");
            doInsertQuery("ALTER TABLE " + tbl_chain + " ADD CONSTRAINT constr_chain_uniq UNIQUE (chain_name, pdb_id);");
            doInsertQuery("ALTER TABLE " + tbl_sse + " ADD CONSTRAINT constr_sse_uniq UNIQUE (chain_id, dssp_start, dssp_end);");
            doInsertQuery("ALTER TABLE " + tbl_ssecontact + " ADD CONSTRAINT constr_contact_uniq UNIQUE (sse1, sse2);");
            doInsertQuery("ALTER TABLE " + tbl_complex_contact_stats + " ADD CONSTRAINT constr_complex_contact_uniq UNIQUE (chain1, chain2);");
            doInsertQuery("ALTER TABLE " + tbl_proteingraph + " ADD CONSTRAINT constr_graph_uniq UNIQUE (chain_id, graph_type);");
            doInsertQuery("ALTER TABLE " + tbl_foldinggraph + " ADD CONSTRAINT constr_foldgraph_uniq UNIQUE (parent_graph_id, fg_number);");
            doInsertQuery("ALTER TABLE " + tbl_graphletcount + " ADD CONSTRAINT constr_graphlet_uniq UNIQUE (graph_id);");
            
            // create views
            doInsertQuery("CREATE VIEW " + view_ssecontacts + " AS SELECT contact_id, least(sse1_type, sse2_type) sse1_type, greatest(sse1_type, sse2_type) sse2_type, sse1_lig_name, sse2_lig_name  FROM (SELECT k.contact_id, sse1.sse_type AS sse1_type, sse2.sse_type AS sse2_type, sse1.lig_name AS sse1_lig_name, sse2.lig_name AS sse2_lig_name FROM " + tbl_ssecontact + " k LEFT JOIN " + tbl_sse + " sse1 ON k.sse1=sse1.sse_id LEFT JOIN " + tbl_sse + " sse2 ON k.sse2=sse2.sse_id) foo;");
            doInsertQuery("CREATE VIEW " + view_graphs + " AS SELECT graph_id, pdb_id, chain_name, graph_type, graph_string_gml FROM (SELECT k.graph_id, k.graph_type, k.graph_string_gml, chain.chain_name AS chain_name, chain.pdb_id AS pdb_id FROM " + tbl_proteingraph + " k LEFT JOIN " + tbl_chain + " chain ON k.chain_id=chain.chain_id) bar;");

            // add comments for tables
            doInsertQuery("COMMENT ON TABLE " + tbl_protein + " IS 'Stores information on a whole PDB file.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_chain + " IS 'Stores information on a protein chain.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_sse + " IS 'Stores information on a secondary structure element (SSE).';");
            doInsertQuery("COMMENT ON TABLE " + tbl_ssecontact + " IS 'Stores information on a contact between a pair of SSEs which are part of the same chain.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_ssecontact_complexgraph + " IS 'Stores information on a contact between a pair of SSEs which are part of different chains of the same protein.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_complex_contact_stats + " IS 'Stores statistical information on the atom contacts of a complex contact. Does NOT include info for SSEs or AAs involved.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_proteingraph + " IS 'Stores descriptions of the protein graph of a protein chain. Multiple of these exist for a chain due to alpha, beta, alphabeta, alphalig, betalig and alphabetalig versions.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_foldinggraph + " IS 'Stores descriptions of a folding graph, which is a connected component of a protein graph.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_complexgraph + " IS 'Stores descriptions of a complex graph.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_graphletcount + " IS 'Stores the graphlet counts for the different graphlets for a certain graph defined by pdbid, chain and graph type.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_nm_ssetoproteingraph + " IS 'Assigns SSEs to protein graphs. An SSE may be part of multiple graphs, e.g., alpha, alphalig, and albe.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_nm_ssetofoldinggraph + " IS 'Assigns SSEs to folding graphs. An SSE may be part of multiple folding graphs, e.g., alpha, alphalig, and albe. It cannot be part of multiple alpha folding graphs though.';");
            
            
            doInsertQuery("COMMENT ON TABLE " + tbl_ssetypes + " IS 'Stores the names of the SSE types, e.g., 1=helix.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_contacttypes + " IS 'Stores the names of the contact types, e.g., 1=mixed.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_graphtypes + " IS 'Stores the names of the graph types, e.g., 1=alpha.';");

            // add comments for specific fields
            doInsertQuery("COMMENT ON COLUMN " + tbl_sse + ".sse_type IS '1=helix, 2=beta strand, 3=ligand, 4=other';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_ssecontact + ".contact_type IS '1=mixed, 2=parallel, 3=antiparallel, 4=ligand, 5=backbone';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_sse + ".lig_name IS 'The 3-letter ligand name from the PDB file and the RCSB ligand expo website. If this SSE is not a ligand SSE, this is the empty string.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_proteingraph + ".graph_type IS '1=alpha, 2=beta, 3=albe, 4=alphalig, 5=betalig, 6=albelig';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_proteingraph + ".graph_string_gml IS 'The graph string in GML format';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_proteingraph + ".graph_string_kavosh IS 'The graph string in Kavosh format format';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_proteingraph + ".graph_image_adj_svg IS 'The path to the SVG format file of the ADJ graph image, relative to plcc_S_graph_image_base_path';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_proteingraph + ".graph_image_adj_png IS 'The path to the PNG format file of the ADJ graph image, relative to plcc_S_graph_image_base_path';");
            
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_HH IS 'Number of helix-helix contacts between the chains.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_HS IS 'Number of helix-strand contacts between the chains.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_HL IS 'Number of helix-ligand contacts between the chains.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_SS IS 'Number of strand-strand contacts between the chains.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_SL IS 'Number of strand-ligand contacts between the chains.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_LL IS 'Number of ligand-ligand contacts between the chains.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_DS IS 'Number of disulfide bridge contacts between the chains.';");

            // add indices
            doInsertQuery("CREATE INDEX plcc_idx_chain_insert ON " + tbl_chain + " (pdb_id, chain_name);");         // for SELECTs during data insert
            doInsertQuery("CREATE INDEX plcc_idx_sse_insert ON " + tbl_sse + " (dssp_start, chain_id);");           // for SELECTs during data insert

            doInsertQuery("CREATE INDEX plcc_idx_chain_fk ON " + tbl_chain + " (pdb_id);");                          // for JOINs, ON CASCADE, etc. (foreign key, FK)
            doInsertQuery("CREATE INDEX plcc_idx_sse_fk ON " + tbl_sse + " (chain_id);");                            // FK
            doInsertQuery("CREATE INDEX plcc_idx_contact_fk1 ON " + tbl_ssecontact + " (sse1);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_contact_fk2 ON " + tbl_ssecontact + " (sse2);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_contact_complexgraph_fk1 ON " + tbl_ssecontact_complexgraph + " (sse1);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_contact_complexgraph_fk2 ON " + tbl_ssecontact_complexgraph + " (sse2);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_complex_contact_fk1 ON " + tbl_complex_contact_stats + " (chain1);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_complex_contact_fk2 ON " + tbl_complex_contact_stats + " (chain2);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_graph_fk ON " + tbl_proteingraph + " (chain_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_foldinggraph_fk ON " + tbl_foldinggraph + " (foldinggraph_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_graphlets_fk ON " + tbl_graphletcount + " (graph_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_ssetoproteingraph_fk1 ON " + tbl_nm_ssetoproteingraph + " (sse_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_ssetoproteingraph_fk2 ON " + tbl_nm_ssetoproteingraph + " (graph_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_ssetofoldinggraph_fk1 ON " + tbl_nm_ssetofoldinggraph + " (sse_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_ssetofoldinggraph_fk2 ON " + tbl_nm_ssetofoldinggraph + " (foldinggraph_id);");                       // FK
            

            // indices on PKs get created automatically
            
            // fill the type tables
            doInsertQuery("INSERT INTO " + tbl_graphtypes + " (graphtype_id, graphtype_text) VALUES (1, 'alpha');");
            doInsertQuery("INSERT INTO " + tbl_graphtypes + " (graphtype_id, graphtype_text) VALUES (2, 'beta');");
            doInsertQuery("INSERT INTO " + tbl_graphtypes + " (graphtype_id, graphtype_text) VALUES (3, 'albe');");
            doInsertQuery("INSERT INTO " + tbl_graphtypes + " (graphtype_id, graphtype_text) VALUES (4, 'alphalig');");
            doInsertQuery("INSERT INTO " + tbl_graphtypes + " (graphtype_id, graphtype_text) VALUES (5, 'betalig');");
            doInsertQuery("INSERT INTO " + tbl_graphtypes + " (graphtype_id, graphtype_text) VALUES (6, 'albelig');");
            
            doInsertQuery("INSERT INTO " + tbl_ssetypes + " (ssetype_id, ssetype_text) VALUES (1, 'helix');");
            doInsertQuery("INSERT INTO " + tbl_ssetypes + " (ssetype_id, ssetype_text) VALUES (2, 'beta strand');");
            doInsertQuery("INSERT INTO " + tbl_ssetypes + " (ssetype_id, ssetype_text) VALUES (3, 'ligand');");
            doInsertQuery("INSERT INTO " + tbl_ssetypes + " (ssetype_id, ssetype_text) VALUES (4, 'other');");
            
            doInsertQuery("INSERT INTO " + tbl_contacttypes + " (contacttype_id, contacttype_text) VALUES (1, 'mixed');");
            doInsertQuery("INSERT INTO " + tbl_contacttypes + " (contacttype_id, contacttype_text) VALUES (2, 'parallel');");
            doInsertQuery("INSERT INTO " + tbl_contacttypes + " (contacttype_id, contacttype_text) VALUES (3, 'antiparallel');");
            doInsertQuery("INSERT INTO " + tbl_contacttypes + " (contacttype_id, contacttype_text) VALUES (4, 'ligand');");
            doInsertQuery("INSERT INTO " + tbl_contacttypes + " (contacttype_id, contacttype_text) VALUES (5, 'backbone');");
            
            doInsertQuery("INSERT INTO " + tbl_complexcontacttypes + " (complexcontacttype_id, complexcontacttype_text) VALUES (1, 'van-der-Waals');");
            doInsertQuery("INSERT INTO " + tbl_complexcontacttypes + " (complexcontacttype_id, complexcontacttype_text) VALUES (2, 'disulfide');");

            res = true;      // Not really, need to check all of them.

        } catch (Exception e) { 
            System.err.println("ERROR: '" + e.getMessage() + "'.");
            res = false;
        }

        return (res);
    }

    
    
    /**
     * Writes information on a SSE to the database. Note that the protein + chain have to exist in the database already.
     */
    public static Boolean writeSSEToDB(String pdb_id, String chain_name, Integer dssp_start, Integer dssp_end, String pdb_start, String pdb_end, String sequence, Integer sse_type, String lig_name, Integer ssePositionInChain) throws SQLException {

        Long chain_id = getDBChainID(pdb_id, chain_name);

        if (chain_id < 0) {
            System.err.println("ERROR: writeSSEToDB: Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (false);
        }
      
        Boolean result = false;

        PreparedStatement statement = null;
 
        /*
        if (lig_name.length() >= 1) {
            query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type, lig_name) VALUES (" + chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type + ", '" + lig_name + "');";
        } else {
            query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type) VALUES (" + chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type + ");";
        }
         * 
         */

        String query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type, lig_name, position_in_chain) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
                // chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type + ", '" + lig_name + "');";
                
        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_id);
            statement.setInt(2, dssp_start);
            statement.setInt(3, dssp_end);
            statement.setString(4, pdb_start);
            statement.setString(5, pdb_end);
            statement.setString(6, sequence);
            statement.setInt(7, sse_type);
            statement.setString(8, lig_name);
            statement.setInt(9, ssePositionInChain);
                                
            statement.executeUpdate();
            dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeSSEToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeSSEToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeSSEToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        return(result);       
    }
    
        
    
    /**
     * Writes data on a protein to the database
     * @param pdb_id the PDB id of the protein
     * @param title the PDB title field
     * @param header the PDB header field
     * @param keywords the PDB keywords field
     * @param experiment the PDB experiment field
     * @param resolution the resolution of the structure, from the PDB headers
     * @return true if the protein was written to the DB, false otherwise
     * @throws SQLException if the DB could not be reset or closed properly
     */
    public static Boolean writeProteinToDB(String pdb_id, String title, String header, String keywords, String experiment, Double resolution) throws SQLException {
        
        if(proteinExistsInDB(pdb_id)) {
            try {
                deletePdbidFromDB(pdb_id);
            } catch (Exception e) {
                DP.getInstance().w("DB: writeProteinToDB: Protein '" + pdb_id + "' already in DB and deleting it failed.");
            }                        
        }
        
        Boolean result = false;

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_protein + " (pdb_id, title, header, keywords, experiment, resolution) VALUES (?, ?, ?, ?, ?, ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
            statement.setString(2, title);
            statement.setString(3, header);
            statement.setString(4, keywords);
            statement.setString(5, experiment);
            statement.setDouble(6, resolution);
                                
            statement.executeUpdate();
            dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeProteinToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeProteinToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeProteinToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        return(result);

    }
    

    /**
     * Deletes all entries related to the PDB ID 'pdb_id' from the plcc database tables.
     * @return The number of affected records (0 if the PDB ID was not in the database).
     */
    public static Integer deletePdbidFromDB(String pdb_id) {

        PreparedStatement statement = null;        
        ResultSetMetaData md;
        int count = 0;        
        ResultSet rs = null;
        
        
        String query = "DELETE FROM " + tbl_protein + " WHERE pdb_id = ?;";
        
        
        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
              
            
            count = statement.executeUpdate();
            dbc.commit();
            
            //md = rs.getMetaData();
            //count = md.getColumnCount();
            
            
        } catch (SQLException e) {
            System.err.println("ERROR: SQL: deletePdbidFromDB: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                dbc.setAutoCommit(true);
            } catch(Exception e) { DP.getInstance().w("DB: deletePdbidFromDB: Could not close statement and reset autocommit."); }
        }
        
        // The other tables are handled automatically via the ON DELETE CASCADE constraint.

        return (count);
    }

    
    
    /**
     * Writes data on a protein chain to the database
     * @param chain_name the chain name
     * @param pdb_id the PDB id of the protein this chain belongs to. The protein has to exist in the DB already.
     * @param molName the molName record of the respective PDB header field
     * @param orgScientific the orgScientific record of the respective PDB header field
     * @param orgCommon the orgCommon record of the respective PDB header field
     * @return
     * @throws SQLException if the DB could not be reset or closed properly
     */
    public static Boolean writeChainToDB(String chain_name, String pdb_id, String molName, String orgScientific, String orgCommon) throws SQLException {
        
        if(! proteinExistsInDB(pdb_id)) {
            return(false);
        }
        
        Boolean result = false;

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_chain + " (chain_name, pdb_id, mol_name, organism_scientific, organism_common) VALUES (?, ?, ?, ?, ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, chain_name);
            statement.setString(2, pdb_id);
            statement.setString(3, molName);
            statement.setString(4, orgScientific);
            statement.setString(5, orgCommon);
                                
            statement.executeUpdate();
            dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeChainToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeChainToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeChainToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        return(result);

    }
    
    
    /**
     * Writes information on a protein graph to the database. This includes a string representing the protein
     * graph in VPLG format. Note that the chain has to exist in the database already. You can set the path to
     * the graph images later using the updateGraphImage... functions.
     * 
     * @param pdb_id the PDB identifier of the protein
     * @param chain_name the PDB chain name of the chain represented by the graph_string
     * @param graph_type the Integer representation of the graph type. use ProtGraphs.getGraphTypeCode() to get it.
     * @param graph_string_gml the graph in GML format
     * @param graph_string_plcc the graph in plcc format
     * @param graph_string_kavosh the graph in kavosh format
     * @param graph_string_dotlanguage the graph in DOT language format
     * @param graph_string_ptgl_red the graph in PTGL RED notation
     * @param graph_string_ptgl_adj the graph in PTGL ADJ notation
     * @param graph_string_ptgl_key the graph in PTGL KEY notation
     * @param graph_string_ptgl_seq the graph in PTGL SEQ notation
     * @param sse_string the graph in SSE string notation
     * @return true if the graph was inserted, false if errors occurred
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static Boolean writeProteinGraphToDB(String pdb_id, String chain_name, Integer graph_type, String graph_string_gml, String graph_string_plcc, String graph_string_kavosh, String graph_string_dotlanguage, String graph_string_ptgl_red, String graph_string_ptgl_adj, String graph_string_ptgl_key, String graph_string_ptgl_seq, String sse_string) throws SQLException {
               
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        Boolean result = false;

        if (chain_db_id < 0) {
            System.err.println("ERROR: writeProteinGraphToDB: Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert protein graph.");
            return (false);
        }

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_proteingraph + " (chain_id, graph_type, graph_string_gml, graph_string_plcc, graph_string_kavosh, graph_string_dotlanguage, graph_string_ptgl_red, graph_string_ptgl_adj, graph_string_ptgl_key, graph_string_ptgl_seq, sse_string) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, graph_type);
            statement.setString(3, graph_string_gml);
            statement.setString(4, graph_string_plcc);
            statement.setString(5, graph_string_kavosh);
            statement.setString(6, graph_string_dotlanguage);
            statement.setString(7, graph_string_ptgl_adj);
            statement.setString(8, graph_string_ptgl_red);
            statement.setString(9, graph_string_ptgl_key);
            statement.setString(10, graph_string_ptgl_seq);
            statement.setString(11, sse_string);
                                
            statement.executeUpdate();
            dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeProteinGraphToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeProteinGraphToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeProteinGraphToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        return(result);
    }
    
    
    /**
     * Writes information on a folding graph to the database. This includes a string representing the folding
     * graph in VPLG format. Note that the chain and the parent protein graph has to exist in the database already. You can set the path to
     * the graph images later using the updateFoldingGraphImage... functions.
     * 
     * @param pdb_id the PDB identifier of the protein
     * @param chain_name the PDB chain name of the chain represented by the graph_string
     * @param graph_type the Integer representation of the graph type. use ProtGraphs.getGraphTypeCode() to get it.
     * @param fg_number the folding graph identifier number, a number starting with 1 and up to number of FGs of this graph. This was a letter in the PTGL ('A', 'B', ...) and one
     * could map this to letters, of course (1=>A, 2=>B, ...). Note that FGs have no natural ordering, these numbers are assigned rather arbitrarily when they are
     * computed,
     * @param graph_string_gml the graph in GML format
     * @param graph_string_plcc the graph in plcc format
     * @param graph_string_kavosh the graph in kavosh format
     * @param graph_string_dotlanguage the graph in DOT language format
     * @param graph_string_ptgl_red the graph in PTGL RED notation
     * @param graph_string_ptgl_adj the graph in PTGL ADJ notation
     * @param graph_string_ptgl_key the graph in PTGL KEY notation
     * @param graph_string_ptgl_seq the graph in PTGL SEQ notation
     * @param sse_string the graph in SSE string notation
     * @return the database insert ID or a value smaller than 1 if something went wrong
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static Long writeFoldingGraphToDB(String pdb_id, String chain_name, Integer graph_type, Integer fg_number, String graph_string_gml, String graph_string_plcc, String graph_string_kavosh, String graph_string_dotlanguage, String graph_string_ptgl_red, String graph_string_ptgl_adj, String graph_string_ptgl_key, String graph_string_ptgl_seq, String sse_string) throws SQLException {
               
        
        if(fg_number < 1) {
            DP.getInstance().e("writeFoldingGraphToDB", "Folding graph number must be 1 or greater, skipping.");
            return(-1L);
        }
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSet generatedKeys = null;
        Long insertID = -1L;

        if (chain_db_id < 0) {
            DP.getInstance().e("writeFoldingGraphToDB()" , "Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert folding graph.");
            return (-1L);
        }
        
        String graphTypeString = ProtGraphs.getGraphTypeString(graph_type);
        Long parent_graph_id = DBManager.getDBProteinGraphID(pdb_id, chain_name, graphTypeString);
        if(parent_graph_id <= 0) {
            DP.getInstance().e("writeFoldingGraphToDB()" , "Could not find parent " + graphTypeString + " graph with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert folding graph.");
            return (-1L);
        } 

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_foldinggraph + " (parent_graph_id, fg_number, graph_string_gml, graph_string_plcc, graph_string_kavosh, graph_string_dotlanguage, graph_string_ptgl_red, graph_string_ptgl_adj, graph_string_ptgl_key, graph_string_ptgl_seq, sse_string) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        int affectedRows = 0;
        
        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, parent_graph_id);
            statement.setInt(2, fg_number);
            statement.setString(3, graph_string_gml);
            statement.setString(4, graph_string_plcc);
            statement.setString(5, graph_string_kavosh);
            statement.setString(6, graph_string_dotlanguage);
            statement.setString(7, graph_string_ptgl_adj);
            statement.setString(8, graph_string_ptgl_red);
            statement.setString(9, graph_string_ptgl_key);
            statement.setString(10, graph_string_ptgl_seq);
            statement.setString(11, sse_string);
                                
            affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                DP.getInstance().w("Inserting folding graph into DB failed, no rows affected.");
            }
            
            // get DB insert id
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                insertID = generatedKeys.getLong(1);
            } else {
                DP.getInstance().w("Inserting folding graph into DB failed, no generated key obtained.");
            }
            dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeFoldingGraphToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeFoldingGraphToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeFoldingGraphToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        return(insertID);
    }

    
    /**
     * Determines the database field name (in the plcc_graph table) for the given image representation.
     * @param graphImageRepresentationType the graph image representation, use constants in ProtGraphs class, e.g., ProtGraphs.GRAPHIMAGE_BITMAP_REPRESENTATION_VPLG_DEFAULT.
     * @return the field name or null if the representation is invalid
     */
    public static String getFieldnameForGraphImageRepresentationType(String graphImageRepresentationType) {
        String fieldName = null;
        
        switch (graphImageRepresentationType) {
            case ProtGraphs.GRAPHIMAGE_BITMAP_REPRESENTATION_VPLG_DEFAULT:
                fieldName = "graph_image_png";
                break;
            case ProtGraphs.GRAPHIMAGE_VECTOR_REPRESENTATION_VPLG_DEFAULT:
                fieldName = "graph_image_svg";
                break;
            case ProtGraphs.GRAPHIMAGE_BITMAP_REPRESENTATION_PTGL_ADJ:
                fieldName = "graph_image_adj_png";
                break;
            case ProtGraphs.GRAPHIMAGE_VECTOR_REPRESENTATION_PTGL_ADJ:
                fieldName = "graph_image_adj_svg";
                break;
            case ProtGraphs.GRAPHIMAGE_BITMAP_REPRESENTATION_PTGL_RED:
                fieldName = "graph_image_red_png";
                break;
            case ProtGraphs.GRAPHIMAGE_VECTOR_REPRESENTATION_PTGL_RED:
                fieldName = "graph_image_red_svg";
                break;
            case ProtGraphs.GRAPHIMAGE_BITMAP_REPRESENTATION_PTGL_KEY:
                fieldName = "graph_image_key_png";
                break;
            case ProtGraphs.GRAPHIMAGE_VECTOR_REPRESENTATION_PTGL_KEY:
                fieldName = "graph_image_key_svg";
                break;
            case ProtGraphs.GRAPHIMAGE_BITMAP_REPRESENTATION_PTGL_SEQ:
                fieldName = "graph_image_seq_png";
                break;
            case ProtGraphs.GRAPHIMAGE_VECTOR_REPRESENTATION_PTGL_SEQ:
                fieldName = "graph_image_seq_svg";
                break;
        }
        
        return fieldName;
    }
    
    /**
     * Sets the image path for a specific protein graph representation in the database. The graph has to exist in the database already.
     * 
     * @param graphDatabaseID the graph ID in the database. Use the getGrapDatabaseID function if you dont know it.
     * @param graphImageRepresentationType The image representation type, defines format and image type (like PNG format and PTGL KEY notation image). Use the constants in ProtGraphs class, e.g., ProtGraphs.GRAPHIMAGE_BITMAP_REPRESENTATION_VPLG_DEFAULT.
     * @param relativeImagePath the relative image path to set in the database for the specified representation
     * @return the number of rows affected by the SQL query
     * @throws SQLException if something goes wrong with the database
     */
    public static Integer updateProteinGraphImagePathInDB(Long graphDatabaseID, String graphImageRepresentationType, String relativeImagePath) throws SQLException {
        
        PreparedStatement statement = null;
        String graphImageFieldName = DBManager.getFieldnameForGraphImageRepresentationType(graphImageRepresentationType);
        if(graphImageFieldName == null) {
            System.err.println("Invalid graph image represenation type. Cannot set protein graph image path in database.");
            return 0;
        }

        String query = "UPDATE " + tbl_proteingraph + " SET " + graphImageFieldName + " = ? WHERE graph_id = ?;";
        Integer numRowsAffected = 0;
        
        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            
            statement.setString(1, relativeImagePath);
            statement.setLong(2, graphDatabaseID);
                                
            numRowsAffected = statement.executeUpdate();
            dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: updateProteinGraphImagePathInDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: updateProteinGraphImagePathInDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: updateProteinGraphImagePathInDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        } 
       
        return numRowsAffected;
        
    }
    
    
    
    /**
     * Sets the image path for a specific folding graph representation in the database. The graph has to exist in the database already.
     * 
     * @param graphDatabaseID the graph ID in the database. Use the getGrapDatabaseID function if you dont know it.
     * @param graphImageRepresentationType The image representation type, defines format and image type (like PNG format and PTGL KEY notation image). Use the constants in ProtGraphs class, e.g., ProtGraphs.GRAPHIMAGE_BITMAP_REPRESENTATION_VPLG_DEFAULT.
     * @param relativeImagePath the relative image path to set in the database for the specified representation
     * @return the number of rows affected by the SQL query
     * @throws SQLException if something goes wrong with the database
     */
    public static Integer updateFoldingGraphImagePathInDB(Integer graphDatabaseID, String graphImageRepresentationType, String relativeImagePath) throws SQLException {
        
        PreparedStatement statement = null;
        String graphImageFieldName = DBManager.getFieldnameForGraphImageRepresentationType(graphImageRepresentationType);
        if(graphImageFieldName == null) {
            System.err.println("Invalid folding graph image represenation type. Cannot set folding graph image path in database.");
            return 0;
        }

        String query = "UPDATE " + tbl_foldinggraph + " SET " + graphImageFieldName + " = ? WHERE foldinggraph_id = ?;";
        Integer numRowsAffected = 0;
        
        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            
            statement.setString(1, relativeImagePath);
            statement.setInt(2, graphDatabaseID);
                                
            numRowsAffected = statement.executeUpdate();
            dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: updateFoldingGraphImagePathInDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: updateFoldingGraphImagePathInDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: updateFoldingGraphImagePathInDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        } 
       
        return numRowsAffected;
        
    }
    
    
    
    /**
     * Assigns the SSEs in the list to the given protein graph (identified by PDB ID, chain ID and graph type) in the database, using the order
     * of the SSEs in the input list.
     * @param sses a list of SSEs, in the order of the graph (N to C terminus on the chain, but some SSEs of the chain may be missing of course, depending on the graph type)
     * @param pdb_id the PDB ID of the graph
     * @param chain_name the chain name of the graph
     * @param graph_type the graph type. Use the integer codes in ProtGraphs class
     * @return the number of SSEs successfully assigned to the graph
     * @throws SQLException if something goes wrong with the database server
     */
    public static Integer assignSSEsToProteinGraphInOrder(ArrayList<SSE> sses, String pdb_id, String chain_name, Integer graph_type) throws SQLException {
        Integer numAssigned = 0;
        
        Long chain_id = getDBChainID(pdb_id, chain_name);
        if(chain_id <= 0) {
            DP.getInstance().e("DBManager", "assignSSEsToProteinGraphInOrder(): Chain not found in DB, cannot assign SSEs.");
            return 0;
        }
        
        ArrayList<Long> sseDBids = new ArrayList<Long>();
        for(SSE sse : sses) {
            Long sseID = DBManager.getDBSseID(sse.getStartDsspNum(), chain_id);
            if(sseID > 0) {
                sseDBids.add(sseID);
            }
            else {
                DP.getInstance().e("DBManager", "assignSSEsToProteinGraphInOrder(): SSE not found in DB, cannot assign it to graph.");
            }
        }
        
        Long graphDbID = DBManager.getDBProteinGraphID(pdb_id, chain_name, ProtGraphs.getGraphTypeString(graph_type));
        if(graphDbID > 0) {
            for(int i = 0; i < sseDBids.size(); i++) {
                numAssigned += DBManager.assignSSEtoProteinGraph(sseDBids.get(i), graphDbID, (i+1));
            }                            
        } else {
            DP.getInstance().e("DBManager", "assignSSEsToProteinGraphInOrder(): Graph not found in DB, cannot assign SSEs to it.");            
        }
        
        return numAssigned;
    }
    
    /**
     * Assigns the SSEs in the list to the given folding graph (identified by PDB ID, chain ID and graph type) in the database, using the order
     * of the SSEs in the input list.
     * @param sses a list of SSEs, in the order of the graph (N to C terminus on the chain, but some SSEs of the chain may be missing of course, depending on the graph type)
     * @param foldingGraphDbId the database ID of the folding graph
     * @return the number of SSEs successfully assigned to the graph
     * @throws SQLException if something goes wrong with the database server
     */
    public static Integer assignSSEsToFoldingGraphInOrder(ArrayList<SSE> sses, Long foldingGraphDbId) throws SQLException {
        Integer numAssigned = 0;
        
        if(foldingGraphDbId < 1) {
            DP.getInstance().e("DBManager", "assignSSEsToFoldingGraphInOrder(): Folding graph database ID must be >= 1 but is  '" + foldingGraphDbId + " ', aborting.");
            return 0;
        }
        
        Long chain_database_id = DBManager.getDBChainIDofFoldingGraph(foldingGraphDbId);
        
        if(chain_database_id < 1) {
            DP.getInstance().e("DBManager", "assignSSEsToFoldingGraphInOrder(): Could not find chain of folding graph with ID '" + foldingGraphDbId + " ' in database.");
            return 0;
        }
        
        ArrayList<Long> sseDBids = new ArrayList<Long>();
        for(SSE sse : sses) {
            Long sseID = DBManager.getDBSseID(sse.getStartDsspNum(), chain_database_id);
            if(sseID > 0) {
                sseDBids.add(sseID);
            }
            else {
                DP.getInstance().e("DBManager", "assignSSEsToFoldingGraphInOrder(): SSE not found in DB, cannot assign it to graph.");
            }
        }
        

        if(foldingGraphDbId > 0) {
            for(int i = 0; i < sseDBids.size(); i++) {
                numAssigned += DBManager.assignSSEtoFoldingGraph(sseDBids.get(i), foldingGraphDbId, (i+1));
            }                            
        } else {
            DP.getInstance().e("DBManager", "assignSSEsToFoldingGraphInOrder(): Graph not found in DB, cannot assign SSEs to it.");            
        }
        
        return numAssigned;
    }
    
    
    
    /**
     * Determines the internal database ID (primary key) of the protein chain of the given folding graph.
     * @param foldingGraphDbId the internal folding graph ID from the database
     * @return the database ID or a value smaller than zero if no such chain (or graph) exists
     * @throws SQLException 
     */
    public static Long getDBChainIDofFoldingGraph(Long foldingGraphDbId) throws SQLException {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT c.chain_id FROM " + tbl_foldinggraph + " f INNER JOIN " + tbl_proteingraph + " p ON f.parent_graph_id = p.graph_id INNER JOIN " + tbl_chain + " c ON p.chain_id = c.chain_id WHERE (f.foldinggraph_id = ?) ;";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, foldingGraphDbId);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getDBChainIDofFoldingGraph(): '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (rs != null) {
                    rs.close();
                }
                dbc.setAutoCommit(true);
            } catch(Exception e) { DP.getInstance().w("DB: getDBChainIDofFoldingGraph(): Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(Long.valueOf(tableData.get(0).get(0)));
            }
            else {
                DP.getInstance().w("DB: getDBChainIDofFoldingGraph(): Folding graph not in DB.");
                return(-1L);
            }
        }
        else {
            return(-1L);
        }        
                      
    }
    
    
    
    /**
     * Assigns an SSE to a protein graph, defining its position in it.
     * @param sseDbId the database id (primary key) of the SSE
     * @param graphDbId the database id (primary key) of the graph
     * @param ssePositionInGraph the position of the SSE in the graph. The first SSE should be 1 (NOT 0).
     * @return the number of affected rows (1 on success, 0 on error)
     */
    public static Integer assignSSEtoProteinGraph(Long sseDbId, Long graphDbId, Integer ssePositionInGraph) throws SQLException {
        if(ssePositionInGraph <= 0) {
            DP.getInstance().e("DBManager", "assignSSEToProteinGraph(): ssePositionInGraph must be > 0, skipping SSE assignment.");            
            return 0;
        }
        
        Integer numRowsAffected = 0;
        
        // assign SSE
        PreparedStatement statement = null;
        
        String query = "INSERT INTO " + tbl_nm_ssetoproteingraph + " (sse_id, graph_id, position_in_graph) VALUES (?, ?, ?);";
        
        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            
            statement.setLong(1, sseDbId);
            statement.setLong(2, graphDbId);
            statement.setInt(3, ssePositionInGraph);
                                
            numRowsAffected = statement.executeUpdate();
            dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: assignSSEToProteinGraph(): '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: assignSSEToProteinGraph(): Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: assignSSEToProteinGraph(): Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        } 
        
        return numRowsAffected;
    }
    
    
    /**
     * Assigns an SSE to a folding graph, defining its position in it.
     * @param sseDbId the database id (primary key) of the SSE
     * @param graphDbId the database id (primary key) of the graph
     * @param ssePositionInGraph the position of the SSE in the graph. The first SSE should be 1 (NOT 0).
     * @return the number of affected rows (1 on success, 0 on error)
     */
    public static Integer assignSSEtoFoldingGraph(Long sseDbId, Long graphDbId, Integer ssePositionInGraph) throws SQLException {
        if(ssePositionInGraph <= 0) {
            DP.getInstance().e("DBManager", "assignSSEToFoldingGraph(): ssePositionInGraph must be > 0, skipping SSE assignment.");            
            return 0;
        }
        
        Integer numRowsAffected = 0;
        
        // assign SSE
        PreparedStatement statement = null;
        
        String query = "INSERT INTO " + tbl_nm_ssetofoldinggraph + " (sse_id, foldinggraph_id, position_in_graph) VALUES (?, ?, ?);";
        
        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            
            statement.setLong(1, sseDbId);
            statement.setLong(2, graphDbId);
            statement.setInt(3, ssePositionInGraph);
                                
            numRowsAffected = statement.executeUpdate();
            dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: assignSSEToFoldingGraph(): '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: assignSSEToFoldingGraph(): Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: assignSSEToFoldingGraph(): Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        } 
        
        return numRowsAffected;    
    }
    
    
    /**
     * Writes information on the graphlet counts for a protein graph to the database. Used by graphlet computation
     * algorithms to store the graphlets. Currently not used because this is done in a separate C++ program.
     * 
     * @param pdb_id the PDB identifier of the protein
     * @param chain_name the PDB chain name of the chain represented by the graph_string
     * @param graph_type the Integer representation of the graph type. use ProtGraphs.getGraphTypeCode() to get it.
     * @param graphlet_counts an array holding the counts for the different graphlet types
     * @return true if the graph was inserted, false if errors occurred
     * @throws SQLException if the data could not be written or the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static Boolean writeGraphletsToDB(String pdb_id, String chain_name, Integer graph_type, Integer[] graphlet_counts) throws SQLException {

        int numReqGraphletTypes = 3;
        if(graphlet_counts.length != numReqGraphletTypes) {
            System.err.println("ERROR: writeGraphletsToDB: Invalid number of graphlet types specified (got " + graphlet_counts.length + ", required " + numReqGraphletTypes + "). Skipping.");
            return false;
        }
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        Boolean result = false;

        if (chain_db_id < 0) {
            System.err.println("ERROR: writeGraphletsToDB: Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (false);
        }

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_graphletcount + " (chain_id, graph_type, graphlet_count_000, graphlet_count_001, graphlet_count_002) VALUES (?, ?, ?, ?, ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, graph_type);
            statement.setInt(3, graphlet_counts[0]);
            statement.setInt(4, graphlet_counts[1]);
            statement.setInt(5, graphlet_counts[2]);
                                
            statement.executeUpdate();
            dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeGraphletsToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeGraphletsToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeGraphletsToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        return(result);
    }
    

    /**
     * Writes information on a SSE contact to the database.
     * @param pdb_id the PDB identifier of the protein
     * @param chain_name the PDB chain name, e.g., A
     * @param sse1_dssp_start the DSSP number of the first residue of the first SSE
     * @param sse2_dssp_start the DSSP number of the first residue of the second SSE
     * @param contact_type the contact type code
     */
    public static Boolean writeContactToDB(String pdb_id, String chain_name, Integer sse1_dssp_start, Integer sse2_dssp_start, Integer contact_type) throws SQLException {

        // Just abort if this is not a valid contact type. Note that 0 is CONTACT_NONE.
        if (contact_type <= 0) {
            return (false);
        }

        Long db_chain_id = getDBChainID(pdb_id, chain_name);

        if (db_chain_id < 0) {
            System.err.println("ERROR: DB: writeContactToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (false);
        }

        Long sse1_id = getDBSseID(sse1_dssp_start, db_chain_id);
        Long sse2_id = getDBSseID(sse2_dssp_start, db_chain_id);
        Long tmp;

        // We may need to switch the IDs to make sure the 1st of them is always lower
        if (sse1_id > sse2_id) {
            tmp = sse2_id;
            sse2_id = sse1_id;
            sse1_id = tmp;
        }

        Boolean result = false;
        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_ssecontact + " (sse1, sse2, contact_type) VALUES (?, ?, ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, sse1_id);
            statement.setLong(2, sse2_id);
            statement.setInt(3, contact_type);
                                
            statement.executeUpdate();
            dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeContactToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeContactToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeContactToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
                
        return(result);
    }
    
    /**
     * Writes information on an inter-chain contact between a pair of SSEs from two different chains of a PDB file contact to the database.
     * This stores the complex graph contacts. It is used for statistical purposes only at the moment.
     * 
     * @param pdb_id the PDB identifier of the protein
     * @param chain1_name the PDB chain name of the first chain involved in the inter-chain contact
     * @param chain2_name the PDB chain name of the second chain involved in the inter-chain contact
     * @param numContactsHH the number of helix-helix contacts
     * @param numContactsHS the number of helix-strand contacts
     * @param numContactsHL the number of helix-ligand contacts
     * @param numContactsSS the number of strand-strand contacts
     * @param numContactsSL the number of strand-ligand contacts
     * @param numContactsLL the number of ligand-ligand contacts
     * @param numContactsDS the number of disulfide contacts
     * @throws SQLException if the data could not be written or the database connection could not be closed or reset to auto commit (in the finally block)
     * @return true if the data was written to the database, false otherwise
     */
    public static Boolean writeInterchainContactsToDB(String pdb_id, String chain1_name, String chain2_name, Integer numContactsHH, Integer numContactsHS, Integer numContactsHL, Integer numContactsSS, Integer numContactsSL, Integer numContactsLL, Integer numContactsDS) throws SQLException {
        
        if(numContactsHH + numContactsHS + numContactsHL + numContactsSS + numContactsSL + numContactsLL + numContactsDS <= 0) {
            System.err.println("WARNING: Not writing interchain contacts to DB for PDB " + pdb_id + " chains " + chain1_name + " and " + chain2_name + ", sum is zero.");
            return false;
        }

        Long chain1_id = getDBChainID(pdb_id, chain1_name);
        Long chain2_id = getDBChainID(pdb_id, chain2_name);

        if (chain1_id < 0) {
            System.err.println("ERROR: DB: writeContactToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain1_name + "' in DB, could not insert complex contact.");
            return (false);
        }
        if (chain2_id < 0) {
            System.err.println("ERROR: DB: writeContactToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain2_name + "' in DB, could not insert complex contact.");
            return (false);
        }
        

        Boolean result = false;
        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_complex_contact_stats + " (chain1, chain2, contact_num_HH, contact_num_HS, contact_num_HL, contact_num_SS, contact_num_SL, contact_num_LL, contact_num_DS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);
            
            statement.setLong(1, chain1_id);
            statement.setLong(2, chain2_id);
            statement.setInt(3, numContactsHH);
            statement.setInt(4, numContactsHS);
            statement.setInt(5, numContactsHL);
            statement.setInt(6, numContactsSS);
            statement.setInt(7, numContactsSL);
            statement.setInt(8, numContactsLL);
            statement.setInt(9, numContactsDS);
                                
            statement.executeUpdate();
            dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeInterchainContactsToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeInterchainContactsToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeInterchainContactsToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
                
        return(result);
    }

    
    /**
     * Retrieves the internal database SSE ID of a SSE from the DB.
     * @param dssp_start the DSSP start residue number of the SSE
     * @param db_chain_id the DB chain ID of the chain the SSE is part of
     * @return The ID if it was found, -1 otherwise.
     */
    private static Long getDBSseID(Integer dssp_start, Long db_chain_id) {
        Long id = -1L;
        ArrayList<ArrayList<String>> rowarray = doSelectQuery("SELECT s.sse_id FROM " + tbl_sse + " s JOIN " + tbl_chain + " c ON ( s.chain_id = c.chain_id ) WHERE ( s.dssp_start = " + dssp_start + " AND c.chain_id = '" + db_chain_id + "' );");

        if (rowarray == null) {
            return (-1L);
        } else {
            try {
                id = Long.valueOf(rowarray.get(0).get(0));
                return (id);
            } catch (Exception e) {
                return (-1L);
            }
        }
    }

    
   
    
    /**
     * Retrieves the internal database chain ID of a chain (it's PK) from the DB. The chain is identified by (pdb_id, chain_name).
     * @param pdb_id the PDB ID of the chain
     * @param chain_name the PDB chain name
     * @return the internal database chain id (its primary key, e.g. '2352365175365'). This is NOT the pdb chain name.
     */
    public static Long getDBChainID(String pdb_id, String chain_name) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT chain_id FROM " + tbl_chain + " WHERE (pdb_id = ? AND chain_name = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
            statement.setString(2, chain_name);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getDBChainID: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (rs != null) {
                    rs.close();
                }
                dbc.setAutoCommit(true);
            } catch(Exception e) { DP.getInstance().w("DB: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(Long.valueOf(tableData.get(0).get(0)));
            }
            else {
                DP.getInstance().w("DB: Chain '" + chain_name + "' of PDB ID '" + pdb_id + "' not in DB.");
                return(-1L);
            }
        }
        else {
            return(-1L);
        }        
    }
    
    
    
    /**
     * Retrieves the PDB ID and the PDB chain name from the DB. The chain is identified by its PK.
     * @param pk the primary key of the chain in the db (e.g., from the graphs table)
     * @return an array of length 2 that contains the PDB ID at position 0 and the chain name at position 1. If no chain with the requested PK exists, the array has a length != 2.
     */
    public static String[] getPDBIDandChain(Integer dbChainID) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
               
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT pdb_id, chain_name FROM " + tbl_chain + " WHERE (chain_id = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setInt(1, dbChainID);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getDBChainID: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                dbc.setAutoCommit(true);
            } catch(Exception e) { DP.getInstance().w("DB: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() == 1) {
            if(tableData.get(0).size() == 2) {
                return(new String[] { tableData.get(0).get(0), tableData.get(0).get(1)});
            }
            else {
                System.err.println("ERROR: DB: getPDBIDandChain(): Result row has unexpected length.");
                return(new String[] { "" } );
            }
        }
        else {
            if(tableData.isEmpty()) {
                // no such PK, empty result list
                return(new String[] { "" } );
            } else {
                System.err.println("ERROR: DB: getPDBIDandChain(): Result table has unexpected length '" + tableData.size() + "'. Should be either 0 or 1.");
                return(new String[] { "" } );
            }
            
        }        
    }
    
    
    /**
     * Retrieves the internal database chain ID of a chain (it's PK) from the DB. The chain is identified by (pdb_id, chain_name).
     * @param pdb_id the PDB ID of the chain
     * @param chain_name the PDB chain name
     * @return the internal database chain id (its primary key, e.g. '2352365175365'). This is NOT the pdb chain name.
     */
    public static Boolean proteinExistsInDB(String pdb_id) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT pdb_id FROM " + tbl_protein + " WHERE (pdb_id = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: proteinExistsInDB:'" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                dbc.setAutoCommit(true);
            } catch(Exception e) { DP.getInstance().w("DB: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(true);
            }
            else {
                DP.getInstance().w("DB: Protein with PDB ID '" + pdb_id + "' not in DB.");
                return(false);
            }
        }
        else {
            return(false);
        }        
    }
    
    
    /**
     * Retrieves the GML format graph string for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdbid the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe". Use the constants in the ProtGraphs class.
     * @return the GML format string representation of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphStringGML(String pdb_id, String chain_name, String graph_type) throws SQLException {
        return DBManager.getGraphString(ProtGraphs.GRAPHFORMAT_GML, pdb_id, chain_name, graph_type);
    }
    
    /**
     * Retrieves the Kavosh format graph string for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdbid the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe". Use the constants in the ProtGraphs class.
     * @return the GML format string representation of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphStringKavosh(String pdb_id, String chain_name, String graph_type) throws SQLException {
        return DBManager.getGraphString(ProtGraphs.GRAPHFORMAT_KAVOSH, pdb_id, chain_name, graph_type);
    }
    
    /**
     * Retrieves the PLCC format graph string for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdbid the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe". Use the constants in the ProtGraphs class.
     * @return the GML format string representation of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphStringPLCC(String pdb_id, String chain_name, String graph_type) throws SQLException {
        return DBManager.getGraphString(ProtGraphs.GRAPHFORMAT_PLCC, pdb_id, chain_name, graph_type);
    }
    
    /**
     * Retrieves the DOT language format graph string for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdbid the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe". Use the constants in the ProtGraphs class.
     * @return the GML format string representation of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphStringDOTLanguage(String pdb_id, String chain_name, String graph_type) throws SQLException {
        return DBManager.getGraphString(ProtGraphs.GRAPHFORMAT_DOTLANGUAGE, pdb_id, chain_name, graph_type);
    }
    
    /**
     * Retrieves a graph string for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type). You need to supply the format you want. Some formats may be null or the empty string.
     * @param graph_format the requested graph format, e.g. "GML". Use the constants in ProtGraphs class, like ProtGraphs.GRAPHFORMAT_GML.
     * @param pdbid the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe". Use the constants in the ProtGraphs class.
     * @return the GML format string representation of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphString(String graph_format, String pdb_id, String chain_name, String graph_type) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            DP.getInstance().w("getGraph(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(null);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;
        
        String query_graph_format_field = "graph_string_gml";
        if(graph_format.equals(ProtGraphs.GRAPHFORMAT_GML)) {
            query_graph_format_field = "graph_string_gml";
        }
        else if(graph_format.equals(ProtGraphs.GRAPHFORMAT_KAVOSH)) {
            query_graph_format_field = "graph_string_kavosh";
        }
        else if(graph_format.equals(ProtGraphs.GRAPHFORMAT_DOTLANGUAGE)) {
            query_graph_format_field = "graph_string_dotlanguage";
        }
        else if(graph_format.equals(ProtGraphs.GRAPHFORMAT_PLCC)) {
            query_graph_format_field = "graph_string_plcc";
        }
        

        String query = "SELECT " + query_graph_format_field + " FROM " + tbl_proteingraph + " WHERE (chain_id = ? AND graph_type = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, gtc);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getGraph: Retrieval of graph string failed: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(tableData.get(0).get(0));
            }
            else {
                DP.getInstance().w("DB: No entry for graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(null);
            }
        }
        else {
            return(null);
        }        
    }
    
    
    /**
     * Determines and returns the internal database ID (primary key) of the protein graph identified by the given properties (PDB ID, chain, gt).
     * 
     * @param pdb_id the PDB identifier of the graph
     * @param chain_name the PDB chain name of the graph
     * @param graph_type the graph type, use the string constants in ProtGraphs class
     * @return the database ID of the graph or a negative number if an error occurred or no such graph exists in the db
     * @throws SQLException 
     */
    public static Long getDBProteinGraphID(String pdb_id, String chain_name, String graph_type) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            DP.getInstance().w("getGraph(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(-1L);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;
        
       

        String query = "SELECT graph_id FROM " + tbl_proteingraph + " WHERE (chain_id = ? AND graph_type = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, gtc);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getGraphDatabaseID(): Retrieval of graph failed: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                String graph_id_str = tableData.get(0).get(0);
                try {
                    Long graph_id_int = Long.parseLong(graph_id_str);
                    return graph_id_int;
                } catch(java.lang.NumberFormatException e) {
                    DP.getInstance().e("DB: getGraphDatabaseID(): Could not parse graph database ID as integer, seems invalid.");
                    return -1L;
                }
            }
            else {
                DP.getInstance().w("DB: No entry for graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(-1L);
            }
        }
        else {
            return(-1L);
        }        
    }
    
    
    /**
     * Determines and returns the internal database ID (primary key) of the folding graph identified by the given properties (PDB ID, chain, gt, fg_number).
     * 
     * @param pdb_id the PDB identifier of the graph
     * @param chain_name the PDB chain name of the graph
     * @param graph_type the graph type, use the string constants in ProtGraphs class
     * @param fg_number the folding graph number
     * @return the database ID of the graph or a negative number if an error occurred or no such graph exists in the db
     * @throws SQLException 
     */
    public static Long getDBFoldingGraphID(String pdb_id, String chain_name, String graph_type, Integer fg_number) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;
        
       Long parentGraphID = DBManager.getDBProteinGraphID(pdb_id, chain_name, graph_type);
       if (parentGraphID < 1) {
            DP.getInstance().w("getDBFoldingGraphID(): Could not find parent " + graph_type + " graph with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(-1L);
        }

        String query = "SELECT foldinggraph_id FROM " + tbl_foldinggraph + " WHERE (parent_graph_id = ? AND fg_number = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, parentGraphID);
            statement.setInt(2, fg_number);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getDBFoldingGraphID(): Retrieval of graph failed: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                String graph_id_str = tableData.get(0).get(0);
                try {
                    Long graph_id_int = Long.parseLong(graph_id_str);
                    return graph_id_int;
                } catch(java.lang.NumberFormatException e) {
                    DP.getInstance().e("DB: getDBFoldingGraphID(): Could not parse graph database ID as integer, seems invalid.");
                    return -1L;
                }
            }
            else {
                DP.getInstance().w("DB: No entry for folding graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(-1L);
            }
        }
        else {
            return(-1L);
        }        
    }
    
    
    /**
     * Retrieves the graphlet counts for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdbid the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe"
     * @return an Integer array of the graphlet counts
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static Integer[] getGraphletCounts(String pdb_id, String chain_name, String graph_type) throws SQLException {
        
        int numReqGraphletTypes = 3;
        
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            DP.getInstance().w("getGraphletCounts(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(null);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT graphlet_count_000, graphlet_count_001, graphlet_count_002 FROM " + tbl_graphletcount + " WHERE (chain_id = ? AND graph_type = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, gtc);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getGraphletCounts: Retrieval of graphlets failed: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        ArrayList<String> rowGraphlets;
        Integer[] result = new Integer[numReqGraphletTypes];        
        if(tableData.size() >= 1) {
            rowGraphlets = tableData.get(0);
            if(rowGraphlets.size() > 0) {
                if(rowGraphlets.size() == numReqGraphletTypes) {
                    for(int i = 0; i < rowGraphlets.size(); i++) {
                        try {
                            result[i] = Integer.valueOf(rowGraphlets.get(i));
                        } catch(Exception ce) {
                            DP.getInstance().w("DB: getGraphletCounts: Cast error. Could not cast entry for graphlets of graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "' to Integer.");
                            return null;
                        }
                    }
                    return(result);
                } else {
                    DP.getInstance().w("DB: getGraphletCounts: Entry for graphlets of graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "' has wrong size.");
                    return(null);
                }                                
            }
            else {
                DP.getInstance().w("DB: getGraphletCounts: No entry for graphlets of graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(null);
            }
        }
        else {
            return(null);
        }        
    }
    
    
    /**
     * Returns the requested ProtGraph object or NULL if no such graph exists in the DB (or DB errors occurred).
     * Note that the ProtGraph is created from the PLCC format graph string.
     * @param pdb_id the PDB identifier, e.g., "7TIM"
     * @param chain_name the PDB chain name, e.g., "A"
     * @param graph_type the graph type, e.g., "albe". Use the constants in ProtGraphs class.
     * @return the ProtGraph instance created from the string representation in the database
     */
    public static ProtGraph getGraph(String pdb_id, String chain_name, String graph_type) {

        String graphString = null;
        
        try { 
            graphString = DBManager.getGraphStringPLCC(pdb_id, chain_name, graph_type); 
        } catch (SQLException e) { 
            System.err.println("ERROR: SQL: Could not get graph from DB: '" + e.getMessage() + "'."); 
            return(null);            
        }
        
        if(graphString == null) {
            DP.getInstance().w("DB: getGraph: Graph '" + pdb_id + "-" + chain_name + "-" + graph_type + "' not found in database.");
            return(null);
        }
        
        return(ProtGraphs.fromPlccGraphFormatString(graphString));        
    }
    
    
    /**
     * Retrieves the SSEstring for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdbid the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe"
     * @return the SSEstring of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getSSEString(String pdb_id, String chain_name, String graph_type) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            DP.getInstance().w("getSSEString(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(null);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT sse_string FROM " + tbl_proteingraph + " WHERE (chain_id = ? AND graph_type = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, gtc);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getSSEString(): Retrieval of graph string failed: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(tableData.get(0).get(0));
            }
            else {
                DP.getInstance().w("DB: getSSEString(): No entry for graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(null);
            }
        }
        else {
            return(null);
        }        
    }
    
    
    
    /**
     * Retrieves the graph data of all protein graphs from the database.
     * @param graphType the graph type, use one of the constants SSEGraph.GRAPHTYPE_* (e.g., SSEGraph.GRAPHTYPE_ALBE) or the word 'ALL' for all types.
     * @return an ArrayList of String arrays. Each of the String arrays in the list has 3 fields. The 
     * first fields (array[0]) contains the PDB ID of the chain, the second one (array[1]) contains the chain ID, the 
     * third field contains the graph type and the fourth field contains the SSE string.
     *  position 0 := pdb id
     *  position 1 := chain id
     *  position 2 := graph type
     *  position 3 := SSE string
     *  position 4 := graph string
     */
    public static ArrayList<String[]> getAllGraphData(String graph_type) throws SQLException {
        
        ArrayList<String[]> graphData = new ArrayList<String[]>();
        
        // get a list of values pairs from the db here    
                
        Integer gtc = 1;        // graphTypeCode, e.g., 1 for alpha
        Boolean allGraphs = false;
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        if(graph_type.equals("ALL")) {
            allGraphs = true;
        }
        else {
            gtc = ProtGraphs.getGraphTypeCode(graph_type);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT sse_string, chain_id, graph_type, graph_string FROM " + tbl_proteingraph + " WHERE (graph_type = ?);";
        
        if(allGraphs) {
            query = "SELECT sse_string, chain_id, graph_type, graph_string FROM " + tbl_proteingraph + ";";
        }

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            if( ! allGraphs) {
                statement.setInt(1, gtc);
            }
            
                                
            rs = statement.executeQuery();
            dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getAllGraphData: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        String graphSSEStringDB, graphTypeDB, pdbidDB, chainNameDB, graphStringDB;
        Integer chainPK;
        String[] data;
        
        
        for(Integer i = 0; i < tableData.size(); i++) {            
            
            if(tableData.get(i).size() == 4) {
                graphSSEStringDB = tableData.get(i).get(0);
                graphTypeDB = tableData.get(i).get(2);
                graphStringDB = tableData.get(i).get(3);
                
                try {
                    chainPK = Integer.valueOf(tableData.get(i).get(1));
                } catch(Exception e) {
                    DP.getInstance().w("DB: getAllGraphData(): '" + e.getMessage() + "' Ignoring data row.");
                    continue;
                }

                // OK, now get the PDB ID and chain name
                data = getPDBIDandChain(chainPK);
                
                if(data.length != 2) {
                    DP.getInstance().w("DB: getAllGraphData(): Could not find chain with PK '" + chainPK + "' in DB, ignoring data row.");
                    continue;
                }
                else {
                    pdbidDB = data[0];
                    chainNameDB = data[1];
                    graphData.add(new String[]{pdbidDB, chainNameDB, graphTypeDB, graphSSEStringDB, graphStringDB});
                }                
            }
            else {
                DP.getInstance().w("DB: getAllGraphData(): Result row #" + i + " has unexpected length " + tableData.get(i).size() + ".");
                return(graphData);
            }
        }
        
        return(graphData);                        
    }
    
    
    /**
     * Retrieves the relative path of the graph image for the requested graph in SVG format from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdbid the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe"
     * @return the relative path to the graph image in SVG format, or null if no such graph image exists (path relative to the base directory, see Settings class).
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphImagePathSVG(String pdb_id, String chain_name, String graph_type) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            DP.getInstance().w("getGraph(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(null);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT graph_image_svg FROM " + tbl_proteingraph + " WHERE (chain_id = ? AND graph_type = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, gtc);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getGraph: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(tableData.get(0).get(0));
            }
            else {
                DP.getInstance().w("DB: No entry for graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(null);
            }
        }
        else {
            return(null);
        }        
    }
    
}
