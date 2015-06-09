/*
 * File:   newtestclass1.h
 * Author: ben
 *
 * Created on May 20, 2015, 2:52:19 PM
 */

#ifndef NEWTESTCLASS1_H
#define	NEWTESTCLASS1_H

#include <cppunit/extensions/HelperMacros.h>
#include "../GraphPrinter.h"

class newtestclass1 : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE(newtestclass1);
    
    CPPUNIT_TEST(test_printAdjacent);
 
    
    
    CPPUNIT_TEST_SUITE_END();

public:
    newtestclass1();
    virtual ~newtestclass1();
    void setUp();
    void tearDown();

private:
    
    Graph threeNodesGraph;
    GraphPrinter printer;
    VertexDescriptor u,v,w;
    vertex_info vi,ui,wi;
    edge_info ei,fi,gi;
    std::string testStringAdjacent;
    std::string testStringAdjacentAll; 
    
    void test_printAdjacent();


};

#endif	/* NEWTESTCLASS1_H */

