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

/**
 * Represents ontology-aware data source.
 *
 * @author  Antony Quinn
 * @version $Id: OntologyAwareSource.java,v 1.1 2006/05/15 14:24:59 aquinn Exp $
 * @since   1.0.2
 */
public interface OntologyAwareSource {

    /**
     * Return ID of ontology map holder
     *
     * @return  ID of ontology map holder
     */
    public String getOntologyMapHolderID();

    /**
     * Store ID of sequence resource holder
     *
     * @param id ID of ontology map holder
     */
    public void setOntologyMapHolderID(String id);

}

