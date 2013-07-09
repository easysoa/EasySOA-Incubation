/**
 * EasySOA Registry
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

package org.easysoa.registry.soapui;

/**
 *
 * @author mkalam-alami
 *
 */
public class SoapUIOperation {

    String name = "";

    String outputName = "";

    String inputName = "";

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getOutputName() {
        return outputName;
    }

    /**
     *
     * @param outputName
     */
    public void setOutputName(String outputName) {
        if (outputName != null) {
            this.outputName = outputName;
        }
    }

    /**
     *
     * @return
     */
    public String getInputName() {
        return inputName;
    }

    /**
     *
     * @param inputName
     */
    public void setInputName(String inputName) {
        if (inputName != null) {
            this.inputName = inputName;
        }
    }

}
