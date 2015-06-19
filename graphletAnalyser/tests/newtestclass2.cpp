/*
 * File:   newtestclass2.cpp
 * Author: ben
 *
 * Created on May 21, 2015, 1:55:53 PM
 */

#include "newtestclass2.h"
using namespace boost;

// TODO: take care of warning message

CPPUNIT_TEST_SUITE_REGISTRATION(newtestclass2);

newtestclass2::newtestclass2() {
}

newtestclass2::~newtestclass2() {
}

void newtestclass2::setUp() {
    
    //setting up nodes
    ti.id = 0;
    ui.id = 1;
    vi.id = 2;
    wi.id = 3;
    xi.id = 4;

    
    GraphletCounts counter();
    
    // graph for counting graphlets
    // graph will be initialized as a only with nodes
    // edges will be added in testing methods successively
    
    t = add_vertex(ti, fiveNodesGraph);
    u = add_vertex(ui, fiveNodesGraph);
    v = add_vertex(vi, fiveNodesGraph);
    w = add_vertex(wi, fiveNodesGraph);
    x = add_vertex(xi, fiveNodesGraph);
    
    ai.source = 0;
    ai.target = 1;
    
    bi.source = 1;
    bi.target = 2;
    
    ci.source = 2;
    ci.target = 3;
    
    di.source = 3;
    di.target = 4;
    
    ei.source = 4;
    ei.target = 0;
    
    fi.source = 0;
    fi.target = 3;
    
    gi.source = 0;
    gi.target = 2;
    
    hi.source = 1;
    hi.target = 4;
    
    ii.source = 1;
    ii.target = 3;
    
    ji.source = 2;
    ji.target = 4;
    
    testNormalizeVector = vector<float>();
    testNormalizeVectorInt = vector<int>();
    
    
    
        // all possible labeling of g1 graphlet (triangle) concerning the symmetry
    g1_vertex_patterns = { "HHH","HHE","HEE","EEE" };
    
    // all possible labeling of g2 graphlet (2-path) concerning the symmetry
    g2_vertex_patterns = { "HHH","HHE","EHE","HEH","HEE","EEE" };
    
    // bio-motivated labeling of graphlets
    // currently implemented are the beta-alpha-beta and beta-beta-beta motifs
    // NOTE: also check if its composing vertices are adjacent
    g2_bio_patterns    = { "EaHaE",    // beta-alpha-beta motif
                                    "EaEaE" };  // beta-beta-beta motif
    
    g0_vertex_patterns = { "HH", "EH", "HE", "EE" };
    
    /*
     * NOTE the correspondence 
     *   lcount[0..3]   := g1_vertex_patterns[0..3] 
     *   lcount[4..9]   := g2_vertex_patterns[0..6]
     *   lcount[10..11] := g2_bio_patterns[0..1]
     */
    
}

void newtestclass2::tearDown() {
    

}



void newtestclass2::test_count_connected_2_graphlets() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    
    testVector = counter.count_connected_2_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[0] == 3);
    
}

void newtestclass2::test_count_connected_3_graphlets0() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    
    testVector = counter.count_connected_3_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[0] == 1);


}

void newtestclass2::test_count_connected_3_graphlets1() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    
    testVector = counter.count_connected_3_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[1] == 1);


}

void newtestclass2::test_count_connected_4_graphlets0() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    
    testVector = counter.count_connected_4_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[0] == 1);
    
}

void newtestclass2::test_count_connected_4_graphlets1() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    
    testVector = counter.count_connected_4_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[1] == 1);
    
    

}

void newtestclass2::test_count_connected_4_graphlets2() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    
    testVector = counter.count_connected_4_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[2] == 1);
    
    

}

void newtestclass2::test_count_connected_4_graphlets3() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    
    testVector = counter.count_connected_4_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[3] == 1);
    

}

void newtestclass2::test_count_connected_4_graphlets4() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    
    testVector = counter.count_connected_4_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[4] == 1);

}

void newtestclass2::test_count_connected_4_graphlets5() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    
    testVector = counter.count_connected_4_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[5] == 1);

}
void newtestclass2::test_count_connected_5_graphlets0() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    ad = add_edge(t,u,ai,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph,false);
    
    CPPUNIT_ASSERT(testVector[0] == 1);

}

void newtestclass2::test_count_connected_5_graphlets1() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();

    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    bd = add_edge(v,w,bi,testGraph).first;
    ad = add_edge(t,u,ai,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph,false);
    
    CPPUNIT_ASSERT(testVector[1] == 1);
}

void newtestclass2::test_count_connected_5_graphlets2() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[2] == 1);
}

void newtestclass2::test_count_connected_5_graphlets3() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    ad = add_edge(t,u,ai,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[3] == 1);
}

void newtestclass2::test_count_connected_5_graphlets4() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[4] == 1);
}

void newtestclass2::test_count_connected_5_graphlets5() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[5] == 1);
}

void newtestclass2::test_count_connected_5_graphlets6() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[6] == 1);
}

void newtestclass2::test_count_connected_5_graphlets7() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    
    
    
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[7] == 1);
}

void newtestclass2::test_count_connected_5_graphlets8() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[8] == 1);
}

void newtestclass2::test_count_connected_5_graphlets9() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[9] == 1);
}

void newtestclass2::test_count_connected_5_graphlets10() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;

    
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[10] == 1);
}

void newtestclass2::test_count_connected_5_graphlets11() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;

    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[11] == 1);
}

void newtestclass2::test_count_connected_5_graphlets12() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[12] == 1);
}

void newtestclass2::test_count_connected_5_graphlets13() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    
    gd = add_edge(t,v,gi,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    fd = add_edge(t,w,fi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[13] == 1);
}

void newtestclass2::test_count_connected_5_graphlets14() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    gd = add_edge(t,v,gi,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    fd = add_edge(t,w,fi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[14] == 1);
}

void newtestclass2::test_count_connected_5_graphlets15() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    hd = add_edge(x,u,hi,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    jd = add_edge(x,v,ji,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[15] == 1);
}

void newtestclass2::test_count_connected_5_graphlets16() {
    //CHECKED
    
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,t,di,testGraph).first;
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[16] == 1);
}

void newtestclass2::test_count_connected_5_graphlets17() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph, false);
    
    CPPUNIT_ASSERT(testVector[17] == 1);
}

void newtestclass2::test_count_connected_5_graphlets18() {
    //CHECKED
    
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    fd = add_edge(t,w,fi,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    
    
    
    testVector = counter.count_connected_5_graphlets(testGraph,false);
    
    CPPUNIT_ASSERT(testVector[18] == 1);
}

void newtestclass2::test_count_connected_5_graphlets19() {
    //CHECKED
    
    
    Graph testGraph = fiveNodesGraph;
    
    ad = add_edge(t,u,ai,testGraph).first;
    ed = add_edge(x,t,ai,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    
    vector<int> testVector = vector<int>();
    
    
    
    testVector = counter.count_connected_5_graphlets(testGraph,false);
    
    CPPUNIT_ASSERT(testVector[19] == 1);
}

void newtestclass2::test_count_connected_5_graphlets20() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph,false);
    
    CPPUNIT_ASSERT(testVector[20] == 1);
}

void newtestclass2::test_normalize_counts() {
    
    testNormalizeVector.push_back(0.1);
    testNormalizeVector.push_back(0);
    testNormalizeVector.push_back(0);
    testNormalizeVector.push_back(0.3);
    testNormalizeVector.push_back(0.4);
    testNormalizeVector.push_back(0.2);
    
    testNormalizeVectorInt.push_back(1);
    testNormalizeVectorInt.push_back(0);
    testNormalizeVectorInt.push_back(0);
    testNormalizeVectorInt.push_back(3);
    testNormalizeVectorInt.push_back(4);
    testNormalizeVectorInt.push_back(2);
    
    vector<float> testVector = counter.normalize_counts(testNormalizeVectorInt, false);
    
    CPPUNIT_ASSERT(testVector == testNormalizeVector);
    
}



/*  // all possible labeling of g1 graphlet (triangle) concerning the symmetry
    string g1_vertex_patterns[] = { "HHH","HHE","HEE","EEE" };
    
    // all possible labeling of g2 graphlet (2-path) concerning the symmetry
    string g2_vertex_patterns[] = { "HHH","HHE","EHE","HEH","HEE","EEE" };
    
    // bio-motivated labeling of graphlets
    // currently implemented are the beta-alpha-beta and beta-beta-beta motifs
    // NOTE: also check if its composing vertices are adjacent
    string g2_bio_patterns[]    = { "EaHaE",    // beta-alpha-beta motif
                                    "EaEaE" }; */



void newtestclass2::test_labeled_counts0() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    
    ai.properties["sse_type"] = "H";
    bi.properties["sse_type"] = "H";
    gi.properties["sse_type"] = "H";
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    
    counter.count_connected_3_graphlets(fiveNodesGraph, true);
    testVector = counter.get_labeled_abs_counts();
    
    
    cout << endl << testVector[0] << endl << testVector[1] << endl << testVector[2] << endl << testVector[3]  << endl;
    
    CPPUNIT_ASSERT(testVector[0] == 1);
    
    
}

void newtestclass2::test_labeled_counts1() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    
    ai.properties["sse_type"] = "H";
    bi.properties["sse_type"] = "H";
    gi.properties["sse_type"] = "E";
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    
    counter.count_connected_3_graphlets(fiveNodesGraph, true);
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(testVector[1] == 1);
}

void newtestclass2::test_labeled_counts2() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    
    ai.properties["sse_type"] = "H";
    bi.properties["sse_type"] = "E";
    gi.properties["sse_type"] = "E";
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    
    counter.count_connected_3_graphlets(fiveNodesGraph, true);
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(testVector[2] == 1);
}

void newtestclass2::test_labeled_counts3() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    
    ai.properties["sse_type"] = "E";
    bi.properties["sse_type"] = "E";
    gi.properties["sse_type"] = "E";
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    
    counter.count_connected_3_graphlets(fiveNodesGraph, true);
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(testVector[3] == 1);
}

void newtestclass2::test_labeled_counts4() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts5() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts6() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts7() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts8() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts9() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts10() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts11() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts12() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts13() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts14() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts15() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts16() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts17() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts18() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts19() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts20() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts21() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts22() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts23() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts24() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}

void newtestclass2::test_labeled_counts25() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    testVector = counter.get_labeled_abs_counts();
    
    CPPUNIT_ASSERT(true);
}