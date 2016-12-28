package uk.ac.ebi.dogwood.datasource;

import org.biojava.servlets.dazzle.datasource.AbstractDataSource;
import org.biojava.servlets.dazzle.datasource.DazzleDataSource;
import org.biojava.servlets.dazzle.datasource.DataSourceException;
import org.biojava.servlets.dazzle.datasource.GFFSource;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.FeatureHolder;
import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.db.SequenceDBLite;
import org.biojava.bio.seq.db.IllegalIDException;
import org.biojava.bio.BioException;
import org.biojava.bio.program.das.DASSequenceDB;

import javax.servlet.ServletContext;
import java.util.*;
import java.net.URL;

/**
 * Annotation source backed by a GFF file serving ontology-based annotation.
 * Basically a wrapper for GFFOntologyReferenceSource
 *
 * @author  Antony Quinn
 * @version $id$
 * @since   1.0.2
 * @see     GFFOntologyReferenceSource
 */
public class GFFOntologyAnnotationSource
       extends AbstractDataSource
       implements DazzleDataSource, GFFSource, OntologyAwareSource {

    private static final String DATA_SOURCE_TYPE        = "GFFOntologyAnnotationSource";
    private static final String DATA_SOURCE_VERSION     = "1.0";

    private String mapMaster              = "";
    private SequenceDBLite referenceServer = null;
    private GFFOntologyReferenceSource gffOntologyReferenceSource = new GFFOntologyReferenceSource();

    /**
     * Initialises annotation source.
     *
     * @param   servletContext      Servlet context
     * @throws  DataSourceException if GFF file could not be parsed
     * @see     GFFOntologyReferenceSource#init(javax.servlet.ServletContext)
     */
    public void init(ServletContext servletContext) throws DataSourceException {
        super.init(servletContext);
        try {
            gffOntologyReferenceSource.init(servletContext);
            referenceServer = new DASSequenceDB(new URL(mapMaster)).allEntryPointsDB();
        }
        catch (Exception ex) {
            throw new DataSourceException(ex, ex.getMessage());
        }
    }

    /* OntologyAwareSource implemenation */

    public String getOntologyMapHolderID() {
        return gffOntologyReferenceSource.getOntologyMapHolderID();
    }

    public void setOntologyMapHolderID(String id) {
        gffOntologyReferenceSource.setOntologyMapHolderID(id);
    }

    /* GFFSource implemenation */

    public boolean getDotVersions() {
        return gffOntologyReferenceSource.getDotVersions();
    }

    public void setDotVersions(boolean b) {
        gffOntologyReferenceSource.setDotVersions(b);
    }

    public int getMaxCachedFeatures() {
        return gffOntologyReferenceSource.getMaxCachedFeatures();
    }

    public void setMaxCachedFeatures(int i) {
        gffOntologyReferenceSource.setMaxCachedFeatures(i);
    }

    public String getSequenceHolderID() {
        return null;
    }

    public void setSequenceHolderID(String s) {
        // not applicable
    }

    public String getIDAttribute() {
        return gffOntologyReferenceSource.getIDAttribute();
    }

    public String getPassword() {
        return gffOntologyReferenceSource.getPassword();
    }

    public void setPassword(String s) {
        gffOntologyReferenceSource.setPassword(s);
    }

    public String getUserName() {
        return gffOntologyReferenceSource.getUserName();
    }

    public void setUserName(String s) {
        gffOntologyReferenceSource.setUserName(s);
    }

    public String getUrl() {
        return gffOntologyReferenceSource.getUrl();
    }

    public void setUrl(String s) {
        gffOntologyReferenceSource.setUrl(s);
    }

    public int getRefreshInterval() {
        return gffOntologyReferenceSource.getRefreshInterval();
    }

    public void setRefreshInterval(int i) {
        gffOntologyReferenceSource.setRefreshInterval(i);
    }

    public boolean getAutoLink()   {
         return gffOntologyReferenceSource.getAutoLink();
    }

    public void setAutoLink(boolean b)   {
         gffOntologyReferenceSource.setAutoLink(b);
    }

    public String getAutoLinkUrl()   {
         return gffOntologyReferenceSource.getAutoLinkUrl();
    }

    public void setAutoLinkUrl(String url)   {
         gffOntologyReferenceSource.setAutoLinkUrl(url);
    }

    public String getLinkType()   {
         return gffOntologyReferenceSource.getLinkType();
    }

    public void setLinkType(String type)   {
         gffOntologyReferenceSource.setLinkType(type);
    }

    public int getMinLocation() {
        return gffOntologyReferenceSource.getMinLocation();
    }

    public void setMinLocation(int min) {
        gffOntologyReferenceSource.setMinLocation(min);
    }

    /* DazzleDataSource implementation */

    public String getDataSourceType() {
        return DATA_SOURCE_TYPE;
    }

    public String getDataSourceVersion() {
        return DATA_SOURCE_VERSION;
    }

    public Sequence getSequence(String id) throws DataSourceException, NoSuchElementException {
        try {
            Sequence sequence = referenceServer.getSequence(id);
            sequence.length();  // ensure fetching...
            return sequence;
        }
        catch (IllegalIDException ex) {
            throw new NoSuchElementException("Unknown reference sequence: " + id);
        }
        catch (BioException ex) {
            throw new NoSuchElementException("Unknown reference sequence: " + id);
        }
    }

    public String getMapMaster() {
        return mapMaster;
    }

    public void setMapMaster(String mapMaster) {
        this.mapMaster = mapMaster;
    }

    public String getLandmarkVersion(String s) throws DataSourceException, NoSuchElementException {
        return gffOntologyReferenceSource.getLandmarkVersion(s);
    }

    public Set getAllTypes() {
        return gffOntologyReferenceSource.getAllTypes();
    }

    public FeatureHolder getFeatures(String ref) throws NoSuchElementException, DataSourceException {
        return gffOntologyReferenceSource.getFeatures(ref);
    }

    public String getFeatureID(Feature feature) {
        return gffOntologyReferenceSource.getFeatureID(feature);
    }

    public String getFeatureLabel(Feature feature) {
        return gffOntologyReferenceSource.getFeatureLabel(feature);
    }

    public List getFeatureNotes(Feature feature) {
        return gffOntologyReferenceSource.getFeatureNotes(feature);
    }

    public List getFeatureTargets(Feature feature)  {
        return gffOntologyReferenceSource.getFeatureTargets(feature);
    }

    public Map getLinkouts(Feature feature) {
        return gffOntologyReferenceSource.getLinkouts(feature);
    }

    public String getScore(Feature f) {
        return gffOntologyReferenceSource.getScore(f);
    }

    public String getTypeDescription(String type)   {
        return gffOntologyReferenceSource.getTypeDescription(type);
    }

    public String getSourceDescription(String source)   {
        return gffOntologyReferenceSource.getSourceDescription(source);
    }

    public String toString()    {
        return gffOntologyReferenceSource.toString();
    }

}