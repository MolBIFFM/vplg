/* 
 * File:   GraphPrinter.cpp
 * Author: ben
 *
 * Created on May 11, 2015, 4:27 PM
 */

#include "GraphPrinter.h"
#include "GraphService.h"

/*
 * Default constructor */
GraphPrinter::GraphPrinter() {
    j_print= JSON_printer();

}




/* Print vertices adjacent to a given vertex i to a string
 * @param int i -- the vertex id */
std::string GraphPrinter::printAdjacent(std::vector<int> vertex_vector) const {
    std::stringstream sstream;
    
    int i = vertex_vector[0];
    vertex_vector.erase(vertex_vector.begin());
    
    // iterate over adjacent vertices and print their ids to a string
    sstream << "  " << std::setw(2) << i << ": "; 
    for (auto it = vertex_vector.begin(); it != vertex_vector.end(); it++) {
        sstream << std::setw(3) << *it << " ";
    }
    sstream << std::endl;
    
    return sstream.str();
};

/* Iterate over the vertices and print their adjacent vertices */
std::string GraphPrinter::printAdjacentAll(std::vector<std::vector<int>> vertex_vector) const {
    std::stringstream sstream;
    
    int n = vertex_vector.size();
    sstream << "Iterate over the vertices and print their adjacent vertices:\n"; 
    for (int i = 0; i < n; i++) {
        sstream << printAdjacent(vertex_vector[i]);
    }
    sstream << std::endl;
    
    return sstream.str();
};




/* Save the graph statistics to a csv file.
 * @param <vector<int>> degDist - node degree distribution
 * @param <int> n - number of vertices
 * @param <int> m - number of edges
 * @return <void> */
void GraphPrinter::saveGraphStatistics(std::vector<int> degDist, int n, int m) {
    std::ofstream summaryFile;
    const std::string summaryFileName = output_path + "graphsStatistics.csv"; // make the file
    

    // get the graph statistics, i.e. Node-Degreee-Distribution, number of vertices, number of edges
    
    /* NOTE:
     * p is the ratio of (number of edges in graph) to (maximal possible number of edges given n vertices) */
    float p = 2.0 * m / (n * (n - 1.0));  
    
    //std::cout << "Size of degdist   " << degDist.size() << "   " << std::endl;
    
    // open the file
    summaryFile.open(summaryFileName.c_str(), std::ios_base::app);
    if (!summaryFile.is_open()) { // ERROR: summary file not open
        std::cerr << apptag << "ERROR: could not open summary file for statistics.\n";
    } else { 
        // write statistics into the file
        summaryFile << std::setw(5)  << n << ","
                    << std::setw(5)  << m << ","
                    << std::setw(10) << std::fixed << std::setprecision(4) << p << ","
                    << std::setw(5)  << degDist[0];
        for (int i = 1; i < degDist.size(); i++) {
            summaryFile << "," << std::setw(5) << degDist[i];
        }
        summaryFile << "\n";
        
        // close the file
        summaryFile.close();
        if( ! silent) {
            std::cout << apptag << "    The statistics were saved to the summary in \"" << summaryFileName << "\".\n";
        }
    }
}

/* Stores one graph in a matlab file
 * @param <Graph> the graph
 * @return <void> */
void GraphPrinter::saveAsMatlabVariable(const Graph& g) {    
    AdjacencyIterator first, last;
    std::ofstream matlabFile;
    const std::string matlabFileName = output_path + "graphsMatlabFormat.m"; // make output file
    
    std::string label = g[graph_bundle].label;
    
    // open the file
    matlabFile.open(matlabFileName.c_str(), std::ios_base::app);
    if (!matlabFile.is_open()) { // show error message, if file is not open
        std::cerr << apptag << "ERROR: could not open " << matlabFileName << " file.\n";
    } else {
        int pos = matlabFile.tellp(); // the current position
        if (pos == 0) { // if the filestream is at the beginning, add the info below
            matlabFile << "% load protein graphs in matlab\n"
                      << "% by defining a structure that stores\n"
                      << "% name, adjacency matrix and adjacency list\n"
                      << "% of each protein graph\n\n";
        }
        // write the name of the graph
        matlabFile << "graph.name = \'" << label << "\';\n";
        
        matlabFile << "graph.am = [ ";
        for (int j = 0; j < num_vertices(g); j++) {
                matlabFile << (edge(0, j, g).second) << " ";
        }
        for (int i = 1; i < num_vertices(g); i++) {
            matlabFile << "; ";
            for (int j = 0; j < num_vertices(g); j++) {
                matlabFile << (edge(i, j, g).second) << " ";
            }
        }
        matlabFile << "];\n";

        matlabFile << "graph.al = {[ ";  
        for (tie(first, last) = adjacent_vertices(0, g); first != last; ++first) {
            matlabFile << g[*first].id + 1 << " ";
        }
        for (int i = 1; i < num_vertices(g); i++) {
            matlabFile << "]; [";
            for (tie(first, last) = adjacent_vertices(i, g); first != last; ++first) {
                matlabFile << " " << g[*first].id + 1;
            }
        }
        matlabFile << "]};\n\n";
        matlabFile.close();
                
        if( ! silent) {
            std::cout << apptag << "    The adjacency matrix and list were saved to \"" << matlabFileName << "\".\n";
        }
    }
};

/* Save the graph statistics in a matlab file,
 * i.e. node-degree-distribution, number of vertices and edges
 * @param <vector<int>> degDist - node degree distribution
 * @param <int> n - number of vertices
 * @param <int> m - number of edges */
void GraphPrinter::saveGraphStatisticsAsMatlabVariable(std::vector<int> degDist, int n, int m) {
    // create the file    
    std::ofstream summaryMatlabFile;
    const std::string summaryMatlabFileName = output_path + "graphsStatisticsMatlabFormat.m";
    int pos;
    
   
    /* NOTE:
     * p is the ratio of (number of edges in graph) to (maximal possible number of edges given n vertices) */
    float p = 2.0 * m / (n * (n - 1.0)); 
    
    // write to the file
    summaryMatlabFile.open(summaryMatlabFileName.c_str(), std::ios_base::app);    
    if (!summaryMatlabFile.is_open()) {
        std::cerr << apptag << "ERROR: could not open matlab statistics file.\n";
    } else {
        pos = summaryMatlabFile.tellp();
        if (pos == 0) {
            summaryMatlabFile << "statistics = ([\n";
        } else {
            /* optional TODO: overwrite last 3 characters
             *                to substitute  "]);" by "],\n"
             *                otherwise, you have to do this manually 
             */
        }
        
        summaryMatlabFile << "[" << std::setw(5)  << n << ","
                    << std::setw(5)  << m << ","
                    << std::setw(10) << std::fixed << std::setprecision(4) << p << ","
                    << std::setw(5)  << degDist[0];
        //for (int i = 1; i < degDist.size(); i++) {
        for (int i = 1; i < 12; i++) {
            if (i < degDist.size()) summaryMatlabFile << "," << std::setw(5) << degDist[i];
            else                    summaryMatlabFile << "," << std::setw(5) << 0;
        }
        summaryMatlabFile << "],\n";
        summaryMatlabFile.close();
        
        if( ! silent) {
            std::cout << apptag << "    The statistics were saved to \"" << summaryMatlabFileName << "\".\n";
        }
    }
};

/* Save the graph in simple format.
 * The name of the file will be of the form "simple_format_graphname.graph".
 * In simple format, the graph will be represented by its edges. */
void GraphPrinter::saveInSimpleFormat(Graph& g) {
    
    
    
    EdgeIterator ei, ei_end;
    const std::string outFileName = output_path + "simple_format_" + g[graph_bundle].label + ".graph";
    std::ofstream outFile;

    outFile.open(outFileName.c_str());
    if (!outFile.is_open()) {
        std::cerr << apptag << "ERROR: could not open " << outFileName << " file.\n";
    } else {
        for (tie(ei, ei_end) = edges(g); ei != ei_end; ++ei) {
            outFile << g[*ei].source << " " << g[*ei].target << std::endl;
        }
    }
};

/* Saves absolute graphlet counts to a file.
 * @param <string> graphName - name of the graph
 * @param <vector<vector<int>>> absolute counts
 * @param <vector<float>>> labeled counts
 * @param <bool> include labeled graphlets */
void GraphPrinter::saveABSGraphletCountsSummary(std::string graphName,std::vector<std::vector<int>> abs_counts, std::vector<float> labeled_counts) {
    std::ofstream summaryFile;
    const std::string summaryFileName = output_path + "counts.plain";
    

    
   

    summaryFile.open(summaryFileName.c_str(), std::ios_base::app);
    if (!summaryFile.is_open()) {
        std::cout << apptag << "ERROR: could not open summary file at '" << summaryFileName << "'.\n";
    } else {
        summaryFile << graphName;
        


        summaryFile << std::setw(6) << "[g3] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << abs_counts[1][0];
        for (int i = 1; i < abs_counts[1].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << abs_counts[1][i];
        
        summaryFile << std::setw(6) << "[g4] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << abs_counts[2][0];
        for (int i = 1; i < abs_counts[2].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << abs_counts[2][i];
        
        summaryFile << std::setw(6) << "[g5] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << abs_counts[3][0];
        for (int i = 1; i < abs_counts[3].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << abs_counts[3][i];
        
        if (!labeled_counts.empty()) {
            summaryFile << std::setw(10) << " [labeled] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << labeled_counts[0];
            for (int i = 1; i < labeled_counts.size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << labeled_counts[i];
        }

        summaryFile << "\n\n";
        summaryFile.close();
        
        if( ! silent) {
            std::cout << apptag << "    The summary over all computed counts is in \"" << summaryFileName << "\".\n";
        }
    }
    
    
}

/* Saves normalized graphlet counts to a file.
 * @param <string> graphName 
 * @param <vector<vector<float>>> normalized counts
 * @param <vector<float>>> labeled counts
 *  */
void GraphPrinter::saveNormalizedGraphletCountsSummary(std::string graphName, std::vector<std::vector<float>> norm_counts, std::vector<float> labeled_counts) {
    std::ofstream summaryFile;
    const std::string summaryFileName = output_path + "counts.plain";
    

    

    summaryFile.open(summaryFileName.c_str(), std::ios_base::app);
    if (!summaryFile.is_open()) {
        std::cout << apptag << "ERROR: could not open summary file at '" << summaryFileName << "'.\n";
    } else {
        summaryFile << graphName;
        


        summaryFile << std::setw(6) << "[g3] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[1][0];
        for (int i = 1; i < norm_counts[1].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[1][i];
        
        summaryFile << std::setw(6) << "[g4] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[2][0];
        for (int i = 1; i < norm_counts[2].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[2][i];
        
        summaryFile << std::setw(6) << "[g5] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[3][0];
        for (int i = 1; i < norm_counts[3].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[3][i];
        
        if (!labeled_counts.empty()) {
            summaryFile << std::setw(10) << " [labeled] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << labeled_counts[0];
            for (int i = 1; i < labeled_counts.size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << labeled_counts[i];
        }

        summaryFile << "\n\n";
        summaryFile.close();
        
        if( ! silent) {
            std::cout << apptag << "    The summary over all computed counts is in \"" << summaryFileName << "\".\n";
        }
    }
    
    
}

/**
 Save norm counts as csv file
 
 */
void GraphPrinter::save_norm_counts_csv(std::string pdb, std::vector<std::vector<float>> norm_counts, std::vector<float> lab_counts) {
    
    std::ofstream summaryFile;
    std::ifstream summaryFileIn;
    const std::string summaryFileName = output_path + "counts.csv";
    

    

    
    summaryFileIn.open(summaryFileName.c_str(), std::ios_base::app);
    if (!summaryFileIn.is_open()) {
        std::cout << apptag << "ERROR: could not open summary file at '" << summaryFileName << "'.\n";
    }  else {
        
        std::string header;
        // read file to see if header is already written
        std::string line;
        std::stringstream stream;
        
        /*
        while ( std::getline(summaryFileIn, line) ) {
            
           
            if (!(line.compare(0,3,"PDB") == 0)) {
                
                stream << "PDB";
                
                for (int i = 0; i < norm_counts[1].size(); i++) {
                    
                    stream << ", [g3]" << i+1;
                }
                
                for (int i = 0; i < norm_counts[2].size(); i++) {
                    stream << ", [g4]" << i+1;
                }
                
                for (int i = 0; i < norm_counts[3].size(); i++) {
                    stream << ", [g5]" << i+1;
                }
                
                for (int i = 0; i < lab_counts.size(); i++) {
                    stream << ", [lab]" << i+1;
                }
                
                header = stream.str();
                summaryFileIn.close();
                
                
                
                summaryFile.open(summaryFileName.c_str(), std::ios_base::app);
                summaryFile << header;
                
                break;
                
            } else {
                summaryFileIn.close();
                break;
            }
            
        }*/
        
        if (!summaryFile.is_open()) {
            summaryFile.open(summaryFileName.c_str(), std::ios_base::app);
        }
        
        
        summaryFile << pdb << ",";
        


        for (int i = 0; i < norm_counts[1].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[1][i];
        
        for (int i = 0; i < norm_counts[2].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[2][i];
        
        for (int i = 0; i < norm_counts[3].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[3][i];
        
        if (!lab_counts.empty()) {
            for (int i = 1; i < lab_counts.size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << lab_counts[i];
        }

        summaryFile << "\n";
        summaryFile.close();
        
        if( ! silent) {
            std::cout << apptag << "    The summary over all computed counts is in \"" << summaryFileName << "\".\n";
        }
    }
    
}

/**
 * Saves the normalized graphlet counts in MATLAB format to the MATLAB output file. If the file does already exist,
 * the data for this graph gets appended to it. Ignores 2-graphlets.
 * @param <vector<vector<float>>> unlabeled counts
 * @param <veector<float>> labeled counts
 */
void GraphPrinter::save_normalized_counts_as_matlab_variable(std::vector<std::vector<float>> unlabeled_counts, std::vector<float> labeled_counts) {    
    std::ofstream countsMatlabFile;
    const std::string countsMatlabFileName = output_path + "countsMatlabFormat.m";
    int pos;


    countsMatlabFile.open(countsMatlabFileName.c_str(), std::ios_base::app);    
    if (!countsMatlabFile.is_open()) {
        std::cout << apptag << "ERROR: could not open matlab counts file.\n";
    } else {
        pos = countsMatlabFile.tellp();
        if (pos == 0) {
            countsMatlabFile << "myCounts = ([\n";
        } else {
            /* optional TODO: overwrite last 3 characters
             *                to substitute  "]);" by "],\n"
             *                otherwise, you have to do this manually 
             */
        }
            
        countsMatlabFile << "[" << unlabeled_counts[1][0];
        for (int i = 1; i < unlabeled_counts[1].size(); i++) countsMatlabFile << ", " << unlabeled_counts[1][i];
        for (int i = 0; i < unlabeled_counts[2].size(); i++) countsMatlabFile << ", " << unlabeled_counts[2][i];
        for (int i = 0; i < unlabeled_counts[3].size(); i++) countsMatlabFile << ", " << unlabeled_counts[3][i];
        if (!labeled_counts.empty()) {
            for (int i = 0; i < labeled_counts.size(); i++) countsMatlabFile << ", " << labeled_counts[i];
        }
        
        countsMatlabFile << "],\n";
        countsMatlabFile.close();
        
        if( ! silent) {
            std::cout << apptag << "    The counts were added to the \"" << countsMatlabFileName << "\".\n";
        }
    }
}

/**
 * Saves the absolute graphlet counts in MATLAB format to the MATLAB output file. If the file does already exist,
 * the data for this graph gets appended to it. Ignores 2-graphlets.
 * @param <vector<vector<float>>> unlabeled counts
 * @param <veector<float>> labeled counts
 */
void GraphPrinter::save_absolute_counts_as_matlab_variable(std::vector<std::vector<int>> unlabeled_counts, std::vector<int> labeled_counts) {    
    std::ofstream countsMatlabFile;
    const std::string countsMatlabFileName = output_path + "countsMatlabFormat.m";
    int pos;


    countsMatlabFile.open(countsMatlabFileName.c_str(), std::ios_base::app);    
    if (!countsMatlabFile.is_open()) {
        std::cout << apptag << "ERROR: could not open matlab counts file.\n";
    } else {
        pos = countsMatlabFile.tellp();
        if (pos == 0) {
            countsMatlabFile << "myCounts = ([\n";
        } else {
            /* optional TODO: overwrite last 3 characters
             *                to substitute  "]);" by "],\n"
             *                otherwise, you have to do this manually 
             */
        }
            
        countsMatlabFile << "[" << unlabeled_counts[1][0];
        for (int i = 1; i < unlabeled_counts[1].size(); i++) countsMatlabFile << ", " << unlabeled_counts[1][i];
        for (int i = 0; i < unlabeled_counts[2].size(); i++) countsMatlabFile << ", " << unlabeled_counts[2][i];
        for (int i = 0; i < unlabeled_counts[3].size(); i++) countsMatlabFile << ", " << unlabeled_counts[3][i];
        if (!labeled_counts.empty()) {
            for (int i = 0; i < labeled_counts.size(); i++) countsMatlabFile << ", " << labeled_counts[i];
        }
        
        countsMatlabFile << "],\n";
        countsMatlabFile.close();
        
        if( ! silent) {
            std::cout << apptag << "    The counts were added to the \"" << countsMatlabFileName << "\".\n";
        }
    }
}


/* Saves the counts in NOVA format. The last vector of counts is reserved for 
 * labeled counts. The counts are saved to a file with the suffix
 * countsNovaFormat.csv. I don't really get why they should be saved this way...
 * @param <string> graphName
 * @param <vector<vector<int>>> counts - vector containing the counts
  */
void GraphPrinter::save_counts_in_nova_format(std::string graphName, std::vector<std::vector<int>> counts) {
    std::ofstream countsNovaFormatFile;
    const std::string countsNovaFormatFileName = output_path + "countsNovaFormat.csv";
    int pos;
    int numberOfGraphlets;


    countsNovaFormatFile.open(countsNovaFormatFileName.c_str(), std::ios_base::app);    
    if (!countsNovaFormatFile.is_open()) {
        std::cout << apptag << "ERROR: could not open counts file in NOVA format.\n";
    } else {
        pos = countsNovaFormatFile.tellp();
        if (pos == 0) {
            countsNovaFormatFile << "ID,Group";
            
            
           
            
            numberOfGraphlets = 0;
            
            for (auto i : counts) {
                
                std::vector<int> g_counts = i;
                
                numberOfGraphlets = g_counts.size();

            }

            for (int i = 1; i <= numberOfGraphlets; i++) {
                countsNovaFormatFile << ",Graphlet" << i;
            }        
            countsNovaFormatFile << "\n";
        }
        countsNovaFormatFile << graphName << ",A";             
       
        
        
        for (auto i : counts) {
            
            std::vector<int> g_counts = i;
            
            for (auto k : g_counts) {
                
                countsNovaFormatFile << "," << k;
                
            }
        }
        
        
        countsNovaFormatFile << "\n";
        countsNovaFormatFile.close();
    
        if( ! silent) {
            std::cout << apptag << "    The counts were added to the \"" << countsNovaFormatFileName << "\".\n"; 
        }
    }    
    
    
}

/* Saves the counts to a JSON-File */
void GraphPrinter::save_counts_as_json(std::string graphname, int num_vertices, int num_edges, std::vector<std::vector<int> > abs_counts, std::vector<std::vector<float> > rel_counts) {
    std::ofstream counts_JSON_file;
    const std::string counts_JSON_filename = output_path + graphname + "countsJSON.json";
    int pos;
    
    
    std::string json_string = j_print.print_vectors_with_info(graphname, num_vertices, num_edges, rel_counts, abs_counts);
    
    counts_JSON_file.open(counts_JSON_filename, std::ios_base::app);
    if (!counts_JSON_file.is_open()) {
        std::cerr << "ERROR: could not open JSON file." << std::endl;
    } else {
        pos = counts_JSON_file.tellp();
        if (pos == 0) {
            counts_JSON_file << json_string;
        }
        
    }
    
    counts_JSON_file.close();
    
    
}

/* Saves the labeled counts to a JSON file */
void GraphPrinter::save_labeled_counts_as_json(std::string graphname, int num_vertices, int num_edges, std::unordered_map<std::string, std::vector<int>> map) {
    std::ofstream lab_counts_JSON_file;
    const std::string lab_counts_JSON_filename = output_path + graphname + "lab_countsJSON.json";
    int pos;
    
    std::string json_string = j_print.print_labeled_counts(graphname, num_vertices, num_edges, map);
    
    lab_counts_JSON_file.open(lab_counts_JSON_filename, std::ios_base::app);
    if (! lab_counts_JSON_file.is_open()) {
        std::cerr << "ERROR: could not open JSON file." << std::endl;
        
    } else {
        pos = lab_counts_JSON_file.tellp();
        if (pos == 0) {
            lab_counts_JSON_file << json_string;
        }
    }
    
    
}



