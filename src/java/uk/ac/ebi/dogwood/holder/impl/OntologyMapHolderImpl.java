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

package uk.ac.ebi.dogwood.holder.impl;

import uk.ac.ebi.hawthorn.OntologyMap;
import uk.ac.ebi.hawthorn.InputStreamListener;
import uk.ac.ebi.dogwood.holder.OntologyMapHolder;

import javax.servlet.ServletContext;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

/**
 * Represents a shared ontology map.
 *
 * @author  Antony Quinn
 * @version $Id: OntologyMapHolderImpl.java,v 1.1 2005/11/23 17:43:55 aquinn Exp $
 * @since   1.0
 */
public class OntologyMapHolderImpl implements OntologyMapHolder {

    private String ontologyMapHolderID  = "";
    private OntologyMap ontologyMap     = null;
    private String propertiesFile       = "";

    public String getOntologyMapHolderID() {
        return ontologyMapHolderID;
    }

    public void setOntologyMapHolderID(String id) {
        this.ontologyMapHolderID = id;
    }

    public OntologyMap getOntologyMap(ServletContext context,
                                      InputStreamListener inputStreamListener)
           throws ClassNotFoundException, FileNotFoundException, IOException  {
        if (ontologyMap == null)    {
            String file = getPropertiesFile();
            if (file.length() == 0) {
                ontologyMap = new OntologyMap(inputStreamListener);
            }
            else    {
                InputStream inputStream = context.getResourceAsStream(file);
                if (inputStream == null)    {
                    inputStream = new FileInputStream(file);
                }
                if (inputStream == null)    {
                    throw new FileNotFoundException("Could not find properties file: " + file);
                }
                ontologyMap = new OntologyMap(inputStream, inputStreamListener);
            }
        }
        return ontologyMap;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(String path) {
        this.propertiesFile = path;
    }

    public String toString()    {
        StringBuffer buf = new StringBuffer();
        buf.append("Holder ID:\t" + getOntologyMapHolderID());
        return (buf.toString());
    }

}