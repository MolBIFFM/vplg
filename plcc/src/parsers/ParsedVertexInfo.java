/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package parsers;

import graphdrawing.IDrawableVertex;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author spirit
 */
public class ParsedVertexInfo implements IDrawableVertex {
    
    protected Map <String, String> vertexProps;
    protected Integer vertexID;
    
    public ParsedVertexInfo() {
        vertexProps = new HashMap<>();
    }
    
    public void setVertexID(Integer id) {
        this.vertexID = id;
    }
    
    public Integer getVertexID() {
        return this.vertexID;
    }
    
    public void setVertexProperty(String key, String value) {
        this.vertexProps.put(key, value);
    }
    
    public String getVertexProperty(String key) {
        return this.vertexProps.get(key);
    }

    @Override
    public String getSseFgNotation() {
        return this.getVertexProperty("sseFgNotation");
    }
    
    public Boolean verify() {
        return !(null == this.vertexID);
    }
    
}
