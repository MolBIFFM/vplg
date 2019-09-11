/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package proteinstructure;

// imports
import proteinstructure.SSE;
import tools.DP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import plcc.Main;
import plcc.Settings;


/**
 * Represents a Residue. Based on information in the PDB and DSSP file.
 * 
 * @author ts
 */
public class Residue extends Molecule implements java.io.Serializable {
    
    //public static final Integer RESIDUE_TYPE_AA = 0;
    //public static final Integer RESIDUE_TYPE_LIGAND = 1;
    //public static final Integer RESIDUE_TYPE_OTHER = 2;
    
    
    // declare class vars
                             // 3 letter name
                              // 1 letter name, AAs only
    private Integer type = null;                            // residue type: 0=AA, 1=Ligand, 2=Other
       // PDB insertion code
    
    /** The binding sites, if any, that this residue is part of (for protein residues which are part of the pocket, NOT for ligands which dock into a pocket).  */
    private List<BindingSite> partOfBindingSites;

    public List<BindingSite> getPartOfBindingSites() {
        return partOfBindingSites;
    }

    public void setPartOfBindingSites(List<BindingSite> partOfBindingSites) {
        this.partOfBindingSites = partOfBindingSites;
    }
    
    /** Assigns a new binding site the residue is part of.
     * @param s the binding site the residue s part of
     */
    public void addPartOfBindingSite(BindingSite s) {
        this.partOfBindingSites.add(s);
    }
    
    private Float phi = null;       // phi backbone angle
    private Float psi = null;       // psi backbone angle
    private Integer acc = null;     // solvent accessible surface, from DSSP file

    // see http://www.wwpdb.org/documentation/format32/sect4.html for the format of the heterogen section of PDB files
    private String ligName = null;                          // HETNAM record of PDB file (name of this hetero group)
    private String ligFormula = null;                       // FORMUL record of PDB file (chemical formula of this hetero group)
    private String ligSynonyms = null;                      // HETSYN record of PDB file (synonyms for this hetero group)

    
    /**
     * Determines the chemical properties of this AA, according to the 5 types system.
     * @return the chemical property, which is one of the constants at AminoAcid.CHEMPROP5_AA_INT_* (Example: ALA => AminoAcid.CHEMPROP5_AA_INT_SMALL_APOLAR).
     */
    public Integer getChemicalProperty5Type() {
        if(this.isAA()) {            
            return AminoAcid.getChemProp5OfAAByName3(this.Name3);
        }
        else {
            return AminoAcid.CHEMPROP5_AA_INT_UNKNOWN;            
        }
    }
    
    /**
     * Determines the chemical properties of this AA, according to the 3 types system.
     * @return the chemical property, which is one of the constants at AminoAcid.CHEMPROP3_AA_INT_* (Example: ALA => AminoAcid.CHEMPROP3_AA_INT_HYDROPHOBIC).
     */
    public Integer getChemicalProperty3Type() {
        if(this.isAA()) {            
            return AminoAcid.getChemProp3OfAAByName3(this.Name3);
        }
        else {
            return AminoAcid.CHEMPROP3_AA_INT_UNKNOWN;            
        }
    }
    
    /**
     * Determines the chemical properties of this AA in the 5 types system and returns the string.
     * @return the chemical property string, which is one of the constants at AminoAcid.CHEMPROP5_AA_STRING_* (Example: ALA => AminoAcid.CHEMPROP5_AA_SMALL_APOLAR).
     */
    public String getChemicalProperty5OneLetterString() {
        return AminoAcid.getChemProp5_OneLetterString(AminoAcid.getChemProp5OfAAByName3(this.Name3));
    }
    
    /**
     * Determines the chemical properties of this AA in the 3 types system and returns the string.
     * @return the chemical property string, which is one of the constants at AminoAcid.CHEMPROP3_AA_STRING_* (Example: ALA => CHEMPROP3_AA_STRING_HYDROPHOBIC).
     */
    public String getChemicalProperty3OneLetterString() {
        return AminoAcid.getChemProp3_OneLetterString(AminoAcid.getChemProp3OfAAByName3(this.Name3));
    }
    
    // constructor
    public Residue() { this.atoms = new ArrayList<>(); this.hydrogenatoms = new ArrayList<>(); this.partOfBindingSites = new ArrayList<>(); }

    public Residue(Molecule Mol) {
        this.atoms = Mol.getAtoms();
        this.chain = Mol.getChain();
        this.chainID = Mol.getChainID();
        this.modelID = Mol.getModelID();
        this.iCode = Mol.getiCode();
        this.pdbNum = Mol.getPdbNum();
        this.dsspNum = Mol.getDsspNum();
        this.Name3 = Mol.getName3();
        this.AAName1 = Mol.getAAName1();
        this.sseString = Mol.getSSEString();
        this.sse = Mol.getSSE();
        this.sseStringDssp = Mol.getSSEStringDssp();
        this.isPartOfDsspSse = Mol.getDsspSseState();
    }
    
    /**
     * Constructs a new residue with PDB residue number 'prn' and DSSP residue number 'drn'.
     * @param residueNumberPDB the PDB residue number
     * @param residueNumberDSSP the DSSP residue number
     */
    public Residue(Integer residueNumberPDB, Integer residueNumberDSSP) {
        atoms = new ArrayList<>();
        hydrogenatoms = new ArrayList<>();
        pdbNum = residueNumberPDB;
        dsspNum = residueNumberDSSP;
        this.partOfBindingSites = new ArrayList<>();
    }
    
    
    /**
     * Determines whether this residue is part of any binding site.
     * @return whether this residue is part of any binding site
     */
    public Boolean isBindingSiteResidue() {
        return(this.partOfBindingSites.size() > 0);
    }
    

    /**
     * Returns the C alpha atoms of this residue or null if it has none.
     * @return the alpha carbon or null 
     */
    public Atom getAlphaCarbonAtom() {
        if(this.isAA()) {
            for(Atom a : this.atoms) {
                if(a.isCalphaAtom()) {
                    return a;
                }
            }
        }
        return null;
    }
    
    
    @Override public String toString() {
        return("[Residue] PDB# " + pdbNum + ", DSSP# " + dsspNum + ", Type " + type + ", AA1 " + AAName1 + ", AA3 " + Name3 + ", Chain " + chainID + ", Model " + modelID + ", # of Atoms " + atoms.size());
    }

    public Float getPhi() { if(this.isAA()) { return(phi); } else { return(0.0f); } }
    public Float getPsi() { if(this.isAA()) { return(psi); } else { return(0.0f); } }
    public Integer getAcc() { if(this.isAA()) { return(acc); } else { return(0); } }
    

 
    /**
     * Determines whether this residue lies at the protein surface.
     * @return whether this residue lies at the protein surface.
     */
    public Boolean isSurfaceResidue() {
        if(this.acc == null) {
            return true;    // if we do not have SAS data, assume yes
        }
        return(this.acc >= 15);
    }
    
    public String getLigName() {
        if(this.isLigand()) {
            return(this.ligName);
        }
        else {
            System.out.println("WARNING: getLigName() Called for non-ligand residue '" + this.getFancyName() + "'.");
            return("NOT_A_LIGAND");
        }
    }

    public String getLigFormula() {
        if(this.isLigand()) {
            return(this.ligFormula);
        }
        else {
            System.err.println("WARNING: getLigFormula() Called for non-ligand residue '" + this.getFancyName() + "'.");
            return("NOT_A_LIGAND_SO_NO_HET_FORMULA");
        }
    }

    public String getLigSynonyms() {
        if(this.isLigand()) {
            return(this.ligSynonyms);
        }
        else {
            System.err.println("WARNING: getLigSynonyms() Called for non-ligand residue '" + this.getFancyName() + "'.");
            return("NOT_A_LIGAND_SO_NO_HET_SYNONYMS");
        }
    }

    
    //setters
    public void setLigName(String s) { ligName = s; }
    public void setiCode(String s) { iCode = s; }
    public void setLigFormula(String s) { ligFormula = s; }
    public void setLigSynonyms(String s) { ligSynonyms = s; }
    public void setPhi(Float f) { phi = f; }
    public void setPsi(Float f) { psi = f; }
    public void setAcc(Integer f) { acc = f; }
    
    

    

    
    
    
}
    
    
