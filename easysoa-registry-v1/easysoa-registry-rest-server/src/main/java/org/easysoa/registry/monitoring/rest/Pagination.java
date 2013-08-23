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

package org.easysoa.registry.monitoring.rest;

/**
 * Utility class for pagination
 * @author jguillemotte
 */
public class Pagination {

    private int currentPage = 1;
    private int totalPageNumber = 1;
    private int nextPage = -1;
    private int previousPage = -1;
    private boolean hasNextPage = false;
    private boolean hasPreviousPage = false;

    /**
     * @return the currentPage
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * @param currentPage the currentPage to set
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * @return the totalPageNumber
     */
    public int getTotalPageNumber() {
        return totalPageNumber;
    }

    /**
     * @param totalPageNumber the totalPageNumber to set
     */
    public void setTotalPageNumber(int totalPageNumber) {
        this.totalPageNumber = totalPageNumber;
    }

    /**
     * @return the nextPage
     */
    public int getNextPage() {
        return nextPage;
    }

    /**
     * @param nextPage the nextPage to set
     */
    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    /**
     * @return the previousPage
     */
    public int getPreviousPage() {
        return previousPage;
    }

    /**
     * @param previousPage the previousPage to set
     */
    public void setPreviousPage(int previousPage) {
        this.previousPage = previousPage;
    }

    /**
     * @return the hasNextPage
     */
    public boolean hasNextPage() {
        return hasNextPage;
    }

    /**
     * @param hasNextPage the hasNextPage to set
     */
    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    /**
     * @return the hasPreviousPage
     */
    public boolean hasPreviousPage() {
        return hasPreviousPage;
    }

    /**
     * @param hasPreviousPage the hasPreviousPage to set
     */
    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

}
