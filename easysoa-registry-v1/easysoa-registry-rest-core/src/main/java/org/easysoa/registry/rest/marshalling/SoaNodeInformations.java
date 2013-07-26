/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.registry.rest.marshalling;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonAutoDetect;

/**
 *
 * @author jguillemotte
 */
@XmlRootElement
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE)
public class SoaNodeInformations {

    @XmlElement
    private List<SoaNodeInformation> soaNodeInformations;

    /**
     *
     */
    public SoaNodeInformations(){
        soaNodeInformations  = new ArrayList<SoaNodeInformation>();
    }

    /**
     *
     * @param soaNodeInformation
     */
    public void addSoaNodeInformation(SoaNodeInformation soaNodeInformation){
        this.soaNodeInformations.add(soaNodeInformation);
    }

    /**
     *
     * @return
     */
    public List<SoaNodeInformation> getSoaNodeInformationList() {
        return soaNodeInformations;
    }

    /**
     *
     * @param soaNodeInformationList
     */
    public void setSoaNodeInformationList(List<SoaNodeInformation> soaNodeInformationList) {
        if(soaNodeInformationList != null){
            this.soaNodeInformations = soaNodeInformationList;
        } else {
            this.soaNodeInformations = new ArrayList<SoaNodeInformation>();
        }
    }

}
