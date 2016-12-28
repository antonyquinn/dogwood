/*
 * Copyright 2005 European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
*/

package uk.ac.ebi.dogwood.datasource;

import org.biojava.servlets.dazzle.datasource.*;
import org.biojava.bio.seq.*;
import org.biojava.bio.seq.impl.SimpleSequence;
import org.biojava.bio.Annotation;
import org.biojava.bio.SmallAnnotation;
import org.biojava.bio.BioException;
import org.biojava.bio.symbol.RangeLocation;
import org.biojava.bio.symbol.DummySymbolList;
import org.biojava.bio.symbol.SymbolList;
import org.biojava.utils.ChangeVetoException;

import javax.servlet.ServletContext;
import java.util.*;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import uk.ac.ebi.dogwood.holder.OntologyMapHolder;
import uk.ac.ebi.hawthorn.OntologyMap;
import uk.ac.ebi.hawthorn.InputStreamListener;

/**
 * Reference source backed by a GFF file serving ontology-based annotation.
 * Note: Not a true reference source since sequence is obtained a remote sequence provider.
 * File format as follows:
 * <pre>
 * &lt;SEGMENT.id&gt;	&lt;METHOD.id&gt;	&lt;TYPE.id&gt;	&lt;START&gt;	&lt;STOP&gt;
 * &lt;SCORE&gt;	&lt;ORIENTATION&gt;	&lt;PHASE&gt;
 * ID "&lt;FEATURE.id&gt;" ; Name "&lt;FEATURE.label&gt;" ; Note "&lt;NOTE&gt;" ;
 * Link "&lt;LINK.href&gt;|&lt;LINK&gt;" ; Target "&lt;TARGET.id&gt;"
 * </pre>
 *
 * @author  Antony Quinn
 * @version $Id: GFFOntologyReferenceSource.java,v 1.3 2006/05/15 14:24:58 aquinn Exp $
 * @since   1.0
 * TODO:    Read GFF attribute column names from properties file (use GFF_ATTR_* constants as defaults)
 */
public class GFFOntologyReferenceSource
       extends AbstractDataSource
       implements DazzleReferenceSource, GFFSource, OntologyAwareSource {

    private static final String DATA_SOURCE_TYPE        = "GFFOntologyReferenceSource";
    private static final String DATA_SOURCE_VERSION     = "1.0";
    private static final String LANDMARK_VERSION        = "default";

    // Attributes in GFF file (compliant with GFF 3 - see http://song.sourceforge.net/gff3-jan04.shtml)
    private static final String GFF_ATTR_ID             = "ID";             // Feature identifier
    private static final String GFF_ATTR_NAME           = "Name";           // Feature label
    private static final String GFF_ATTR_TARGET         = "Target";         // Protein interaction partner(s)
    private static final String GFF_ATTR_NOTE           = "Note";
    private static final String GFF_ATTR_LINK           = "Link";
    private static final String GFF_ATTR_SEP            = "|";              // Separates eg. LINK url from role and type, eg. Link "www.sample.com|Sample|text/xml"

    // Uses composition instead of inheritance
    private GFFReferenceSource gffReferenceSource = new GFFReferenceSource();

    // Sequence
    private String ontologyMapHolderID = "";
    private OntologyMap ontologyMap    = null;

    /**
     * Parses and caches GFF file.
     *
     * @param   servletContext      Servlet context
     * @throws  DataSourceException if GFF file could not be parsed
     * @see     org.biojava.servlets.dazzle.datasource.GFFReferenceSource#init(javax.servlet.ServletContext)
     */
    public void init(ServletContext servletContext) throws DataSourceException {
        super.init(servletContext);
        try {
            gffReferenceSource.init(servletContext);
            //gffReferenceSource.setIDAttribute(GFF_ATTR_ID);
            // Load ontologies
            String id = getOntologyMapHolderID();
            OntologyMapHolder holder = (OntologyMapHolder) servletContext.getAttribute(id);
            this.ontologyMap = holder.getOntologyMap(servletContext, new InputStreamListenerImpl());

        }
        catch (Exception ex) {
            throw new DataSourceException(ex, ex.getMessage());
        }
    }

    // Read-only properties

    public String getDataSourceType() {
        return DATA_SOURCE_TYPE;
    }

    public String getDataSourceVersion() {
        return DATA_SOURCE_VERSION;
    }

    // Configurable properties

    public boolean getAutoLink()   {
         return gffReferenceSource.getAutoLink();
    }

    public void setAutoLink(boolean b)   {
         gffReferenceSource.setAutoLink(b);
    }

    public String getAutoLinkUrl()   {
         return gffReferenceSource.getAutoLinkUrl();
    }

    public void setAutoLinkUrl(String url)   {
         gffReferenceSource.setAutoLinkUrl(url);
    }

    public String getLinkType()   {
         return gffReferenceSource.getLinkType();
    }

    public void setLinkType(String type)   {
         gffReferenceSource.setLinkType(type);
    }

    /**
     * Returns true if version numbers are appended to gene/protein identifiers in the GFF file,
     * for example IPI00010349.3
     *
     * @return  <code>true</code> if version numbers are appended to the gene/protein identifier,
     *          otherwise <code>false</code>.
     */
    public boolean getDotVersions() {
        return gffReferenceSource.getDotVersions();
    }

    /**
     * Stores whether version numbers are appended to gene/protein identifiers in the GFF file,
     * for example IPI00010349.3
     *
     * @param   dotVersions         <code>true</code> if version numbers are appended to the gene/protein identifier
     */
    public void setDotVersions(boolean dotVersions) {
        gffReferenceSource.setDotVersions(dotVersions);
    }

    public String getIDAttribute() {
        return GFF_ATTR_ID;
    }

    public String getMapMaster() {
        return gffReferenceSource.getMapMaster();
    }

    public int getMaxCachedFeatures()   {
         return gffReferenceSource.getMaxCachedFeatures();
    }

    public void setMaxCachedFeatures(int max)   {
         gffReferenceSource.setMaxCachedFeatures(max);
    }

    public int getMinLocation() {
        return gffReferenceSource.getMinLocation();
    }

    public void setMinLocation(int min) {
        gffReferenceSource.setMinLocation(min);
    }

    public String getOntologyMapHolderID() {
        return ontologyMapHolderID;
    }

    public void setOntologyMapHolderID(String id) {
        this.ontologyMapHolderID = id;
    }

    public int getRefreshInterval()  {
        return gffReferenceSource.getRefreshInterval();
    }

    public void setRefreshInterval(int interval)  {
        gffReferenceSource.setRefreshInterval(interval);
    }

    public String getSequenceHolderID() {
        return gffReferenceSource.getSequenceHolderID();
    }

    public void setSequenceHolderID(String id) {
        gffReferenceSource.setSequenceHolderID(id);
    }

    public String getSegment(Feature feature) {
        return gffReferenceSource.getSegment(feature);
    }

    public String getPassword()   {
         return gffReferenceSource.getPassword();
    }

    public void setPassword(String password)   {
         gffReferenceSource.setPassword(password);
    }

    public String getUserName()   {
         return gffReferenceSource.getUserName();
    }

    public void setUserName(String name)   {
         gffReferenceSource.setUserName(name);
    }

    public Set getAllTypes() {
        return gffReferenceSource.getAllTypes();
    }

    public Set getEntryPoints() {
        return gffReferenceSource.getEntryPoints();
    }

    public FeatureHolder getFeatures(String ref) throws NoSuchElementException, DataSourceException {
        return gffReferenceSource.getFeatures(ref);
    }

    /**
     * Returns one of the following GFF attributes (checked in this order):
     * <code>Dbxref</code>
     * <code>Ontology_id</code>
     * <code>ID</code>
     *
     * @param   feature
     * @return  Dbxref, Ontology_id or ID
     */
    public String getFeatureID(Feature feature) {
        Annotation annotation = feature.getAnnotation();
        // ID
        if (hasProperty(annotation, GFF_ATTR_ID))   {
           return getProperty(annotation, GFF_ATTR_ID);
        }
        else    {
            return gffReferenceSource.getFeatureID(feature);
        }
    }

    /**
     * Returns explicit <code>Name</code> field or implicit ontology term
     *
     * @param   feature
     * @return  explicit <code>Name</code> field or implicit ontology term
     * @see     #getFeatureID(Feature)
     */
    public String getFeatureLabel(Feature feature) {
        String id    = getFeatureID(feature);
        String label = getOntologyTerm(id);
        // Do not override ontology term unless the ID is not recognised
        if (label.equals(id)) {
            // Explicit name
            Annotation annotation = feature.getAnnotation();
            if (hasProperty(annotation, GFF_ATTR_NAME)) {
                return (getProperty(annotation, GFF_ATTR_NAME));
            }
        }
        return label;
    }

    /**
     * Returns contents of <code>NOTE</code> element(s)
     *
     * @param   feature
     * @return  list of notes
     */
    public List getFeatureNotes(Feature feature) {
        Annotation annotation = feature.getAnnotation();
        if (hasProperty(annotation, GFF_ATTR_NOTE)) {
            return Collections.unmodifiableList(getPropertyList(annotation, GFF_ATTR_NOTE));
        }
        else    {
            return super.getFeatureNotes(feature);
        }
    }

    /**
     * Returns contents of <code>TARGET</code> element(s)
     *
     * @param   feature
     * @return  list of <code>Features</code> containing IDs and start/stop coordinates
     */
    public List getFeatureTargets(Feature feature)  {
        Annotation annotation = feature.getAnnotation();
        if (hasProperty(annotation, GFF_ATTR_TARGET))   {
            List targetList = new ArrayList();
            SymbolList symbolList = new DummySymbolList(DNATools.getDNA(), getMinLocation());
            List ids =  getPropertyList(annotation, GFF_ATTR_TARGET);
            for (Iterator i = ids.iterator(); i.hasNext(); ) {
                String id = (String) i.next();
                Sequence sequence = new SimpleSequence(symbolList, id, id, Annotation.EMPTY_ANNOTATION);
                Feature.Template template   = new Feature.Template();
                template.location           = new RangeLocation(getMinLocation(), getMinLocation());
                template.annotation         = new SmallAnnotation();
                List idProperty = new ArrayList();
                idProperty.add(id);
                try {
                    template.annotation.setProperty(GFF_ATTR_ID, idProperty);
                    targetList.add(sequence.createFeature(template));
                }
                catch (ChangeVetoException e)    {
                    log("Could not create target feature: " + id, e);
                }
                catch (BioException e)    {
                    log("Could not create target feature: " + id, e);
                }
            }
            return Collections.unmodifiableList(targetList);
        }
        else
            return super.getFeatureTargets(feature);
    }

    public String getLandmarkVersion(String ref) throws DataSourceException, NoSuchElementException {
        return LANDMARK_VERSION;
    }

    /**
     * Returns contents of <code>LINK</code> element(s). Links should be of the form:
     * Link "&lt;url&gt;|&lt;role&gt;|&lt;type&gt;" (role and type are optional)
     *
     * @param   feature
     * @return  links with role as key and URL as value (key is URL if role is empty)
     */
    public Map getLinkouts(Feature feature) {
        Annotation annotation = feature.getAnnotation();
        Map links = new HashMap();
        Map linkType = new HashMap();
        if (hasProperty(annotation, GFF_ATTR_LINK)) {
            List linkList = getPropertyList(annotation, GFF_ATTR_LINK);
            for (Iterator i = linkList.iterator(); i.hasNext(); ) {
                String link = (String) i.next();
                String url  = link;
                String role = url;
                // Role
                int sep = link.indexOf(GFF_ATTR_SEP);
                final int START = 0;    // Start of string (position 0)
                if (sep > START)    {
                    url = link.substring(START, sep);
                    role = link.substring(sep + 1, link.length());
                    // Type
                    int typeSep = role.indexOf(GFF_ATTR_SEP);
                    if (typeSep > START)    {
                        String type = role.substring(typeSep + 1, role.length());
                        role = role.substring(START, typeSep);
                        linkType.put(role, type);
                    }
                }
                if (linkType.size() > 0)    {
                    links.put(linkType, url);
                }
                else    {
                    links.put(role, url);
                }
            }
        }
        else    {
            links = gffReferenceSource.getLinkouts(feature);
        }
        return Collections.unmodifiableMap(links);
    }

    public Sequence getSequence(String ref) throws DataSourceException, NoSuchElementException {
        return gffReferenceSource.getSequence(ref);
    }

    public String getScore(Feature f) {
        return gffReferenceSource.getScore(f);
    }

    /**
     * Returns ontology term if found in ontology properties file, otherwise returns <code>type</code>
     *
     * @param   type    type ID
     * @return  ontology term if found in ontology properties file, otherwise returns <code>type</code>
     */
    public String getTypeDescription(String type)   {
        return getOntologyTerm(type);
    }

    /**
     * Returns ontology term if found in ontology properties file, otherwise returns <code>source</code>
     *
     * @param   source  method ID
     * @return  ontology term if found in ontology properties file, otherwise returns <code>source</code>
     */
    public String getSourceDescription(String source)   {
        return getOntologyTerm(source);
    }

    public String getUrl() {
        return gffReferenceSource.getUrl();
    }
    public void setUrl(String url) {
        gffReferenceSource.setUrl(url);
    }

    /**
     * Returns <code>OntologyMap.toString()</code> then <code>GFFReferenceSource.toString()</code>
     *
     * @return  <code>OntologyMap.toString()</code> then <code>GFFReferenceSource.toString()</code>
     * @see     GFFReferenceSource#toString
     * @see     OntologyMap#toString
     */
    public String toString()    {
        StringBuffer buf = new StringBuffer();
        buf.append(ontologyMap.toString());
        buf.append(gffReferenceSource.toString());
        buf.append(super.toString(true));
        return buf.toString();
    }

    // Private methods

    // Return simple string property
    private boolean hasProperty(Annotation annotation, String attribute)   {
        return ((annotation.containsProperty(attribute)) || (annotation.containsProperty(normalise(attribute))));
    }

    // Return simple string property
    private String getProperty(Annotation annotation, String attribute)   {
        List list = getPropertyList(annotation, attribute);
        if (list.isEmpty())
            return "";
        else
            return (String) list.get(0);
    }

    // Return property as List
    private List getPropertyList(Annotation annotation, String attribute)   {
        List list = null;
        try {
            list = (List) annotation.getProperty(attribute);
        }
        catch (NoSuchElementException e)  {
            // Ignore - just means property is not found
        }
        if (list == null || list.isEmpty()) {
            // Try case-insensitive version
            list = (List) annotation.getProperty(normalise(attribute));
        }
        return list;
    }

    private String getOntologyTerm(String id)   {
        try {
            // TODO: get __dazzle__ prefix from dazzle.jar (means the ID is autogenerated)
            if ((!id.startsWith("__dazzle__")) && (ontologyMap.isValidID(id)))  {
                return ontologyMap.getTerm(id);
            }
            else    {
                // No ontology ID specified - no need to throw an exception
                return id;
            }
        }
        catch (Exception e)    {
            log("Could not get ontology term for " + id, e);
            return id;
        }
    }

    private class InputStreamListenerImpl implements InputStreamListener    {
        public InputStream getInputStream(String uri)
               throws FileNotFoundException, IOException, MalformedURLException {
            return getServletContext().getResourceAsStream(uri);
        }
    }

    private String normalise(String s)  {
        return s.toLowerCase();
    }

}
