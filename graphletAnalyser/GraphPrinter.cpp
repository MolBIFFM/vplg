/* 
 * File:   GraphPrinter.cpp
 * Author: ben
 *
 * Created on May 11, 2015, 4:27 PM
 */

#include "GraphPrinter.h"

/*
 * Default constructor */
GraphPrinter::GraphPrinter() {
    Graph g_tmp;
    GraphService gService_tmp;
    g = g_tmp;
    service = gService_tmp;
};



GraphPrinter::GraphPrinter(const Graph& graph) {
    g = graph;
    service = GraphService(g);
};


/* Print vertices adjacent to a given vertex i to a string
 * @param int i -- the vertex id */
std::string GraphPrinter::printAdjacent(int i) {
    AdjacencyIterator first, last;
    stringstream sstream;
    
    // iterate over adjacent vertices and print their ids to a string
    sstream << "  " << setw(2) << vertex(i,g) << ": "; 
    for (tie(first, last) = adjacent_vertices(i,g); first != last; ++first) {
        sstream << setw(3) << g[*first].id << " ";
    }
    sstream << endl;
    
    return sstream.str();
};

/* Iterate over the vertices and print their adjacent vertices */
std::string GraphPrinter::printAdjacentAll() {
    stringstream sstream;
    
    sstream << "Iterate over the vertices and print their adjacent vertices:\n"; 
    for (int i = 0; i < num_vertices(g); i++) {
        sstream << printAdjacent(i);
    }
    sstream << endl;
    
    return sstream.str();
};




/* Save the graph statistics to a csv file */
void GraphPrinter::saveGraphStatistics() {
    ofstream summaryFile;
    const string summaryFileName = output_path + "graphsStatistics.csv"; // make the file
    

    // get the graph statistics, i.e. Node-Degreee-Distribution, number of vertices, number of edges
    vector<int> degDist = service.computeDegreeDist();
    int n = num_vertices(service.getGraph());
    int m = num_edges(service.getGraph());
    /* NOTE:
     * p is the ratio of (number of edges in graph) to (maximal possible number of edges given n vertices) */
    float p = 2.0 * m / (n * (n - 1.0));  
    
    // open the file
    summaryFile.open(summaryFileName.c_str(), std::ios_base::app);
    if (!summaryFile.is_open()) { // ERROR: summary file not open
        cerr << apptag << "ERROR: could not open summary file for statistics.\n";
    } else { 
        // write statistics into the file
        summaryFile << setw(5)  << n << ","
                    << setw(5)  << m << ","
                    << setw(10) << fixed << setprecision(4) << p << ","
                    << setw(5)  << degDist[0];
        for (int i = 1; i < degDist.size(); i++) {
            summaryFile << "," << setw(5) << degDist[i];
        }
        summaryFile << "\n";
        
        // close the file
        summaryFile.close();
        if( ! silent) {
            cout << apptag << "    The statistics were saved to the summary in \"" << summaryFileName << "\".\n";
        }
    }
};

/* Stores all graphs in a matlab file */
void GraphPrinter::saveAsMatlabVariable(int& number) {    
    AdjacencyIterator first, last;
    ofstream matlabFile;
    const string matlabFileName = output_path + "graphsMatlabFormat.m"; // make output file
    
    // open the file
    matlabFile.open(matlabFileName.c_str(), std::ios_base::app);
    if (!matlabFile.is_open()) { // show error message, if file is not open
        cerr << apptag << "ERROR: could not open " << matlabFileName << " file.\n";
    } else {
        int pos = matlabFile.tellp(); // the current position
        if (pos == 0) { // if the filestream is at the beginning, add the info below
            matlabFile << "% load protein graphs in matlab\n"
                      << "% by defining a structure that stores\n"
                      << "% name, adjacency matrix and adjacency list\n"
                      << "% of each protein graph\n\n";
        }
        // write the name of the graph
        matlabFile << "graphs(" << number + 1 << ").name = \'" << service.getName() << "\';\n";
        
        matlabFile << "graphs(" << number + 1 << ").am = [ ";
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

        matlabFile << "graphs(" << number + 1 << ").al = {[ ";  
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
            cout << apptag << "    The adjacency matrix and list were saved to \"" << matlabFileName << "\".\n";
        }
    }
};

/* Save the graph statistics in a matlab file, i.e. node-degree-distribution, number of vertices and edges */
void GraphPrinter::saveGraphStatisticsAsMatlabVariable() {
    // create the file    
    ofstream summaryMatlabFile;
    const string summaryMatlabFileName = output_path + "graphsStatisticsMatlabFormat.m";
    int pos;
    
    // compute graph statistics
    vector<int> degDist = service.computeDegreeDist();
    int n = num_vertices(service.getGraph());
    int m = num_edges(service.getGraph());
    /* NOTE:
     * p is the ratio of (number of edges in graph) to (maximal possible number of edges given n vertices) */
    float p = 2.0 * m / (n * (n - 1.0)); 
    
    // write to the file
    summaryMatlabFile.open(summaryMatlabFileName.c_str(), std::ios_base::app);    
    if (!summaryMatlabFile.is_open()) {
        cerr << apptag << "ERROR: could not open matlab statistics file.\n";
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
        
        summaryMatlabFile << "[" << setw(5)  << n << ","
                    << setw(5)  << m << ","
                    << setw(10) << fixed << setprecision(4) << p << ","
                    << setw(5)  << degDist[0];
        //for (int i = 1; i < degDist.size(); i++) {
        for (int i = 1; i < 12; i++) {
            if (i < degDist.size()) summaryMatlabFile << "," << setw(5) << degDist[i];
            else                    summaryMatlabFile << "," << setw(5) << 0;
        }
        summaryMatlabFile << "],\n";
        summaryMatlabFile.close();
        
        if( ! silent) {
            cout << apptag << "    The statistics were saved to \"" << summaryMatlabFileName << "\".\n";
        }
    }
};

/* Save the graph in simple format.
 * The name of the file will be of the form "simple_format_graphname.graph".
 * In simple format, the graph will be represented by its edges. */
void GraphPrinter::saveInSimpleFormat() {
    EdgeIterator ei, ei_end;
    const string outFileName = output_path + "simple_format_" + service.getName() + ".graph";
    ofstream outFile;

    outFile.open(outFileName.c_str());
    if (!outFile.is_open()) {
        cerr << apptag << "ERROR: could not open " << outFileName << " file.\n";
    } else {
        for (tie(ei, ei_end) = edges(g); ei != ei_end; ++ei) {
            outFile << g[*ei].source << " " << g[*ei].target << endl;
        }
    }
};

