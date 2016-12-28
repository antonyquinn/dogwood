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

package uk.ac.ebi.dogwood.holder;

import uk.ac.ebi.hawthorn.OntologyMap;
import uk.ac.ebi.hawthorn.InputStreamListener;

import javax.servlet.ServletContext;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Represents a shared ontology map.
 *
 * @author  Antony Quinn
 * @version $Id: OntologyMapHolder.java,v 1.1 2005/11/23 17:50:50 aquinn Exp $
 * @since   1.0
 */
public interface OntologyMapHolder {

    /**
     * Return ID of sequence resource holder
     *
     * @return  ID of sequence resource holder
     */
    public String getOntologyMapHolderID();

    /**
     * Store ID of sequence resource holder
     *
     * @param id ID of sequence resource holder
     */
    public void setOntologyMapHolderID(String id);

    /**
     * Return properties file path
     *
     * @return  properties file path
     */
    public String getPropertiesFile();

    /**
     * Store properties file path
     *
     * @param path properties file path
     */
    public void setPropertiesFile(String path);

    /**
     * Return sequence resource
     *
     * @return sequence resource
     */
    public OntologyMap getOntologyMap(ServletContext context, 
                                      InputStreamListener inputStreamListener)
           throws ClassNotFoundException, FileNotFoundException, IOException;

    /**
     * Returns ontologyMap holder ID
     *
     * @return  ontologyMap holder ID
     */
    public String toString();

}
