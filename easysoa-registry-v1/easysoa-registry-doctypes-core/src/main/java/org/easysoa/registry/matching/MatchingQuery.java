package org.easysoa.registry.matching;

import java.util.ArrayList;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;


/**
 * TODO isProxy & version ?!?
 * @author mdutoo
 *
 */
public class MatchingQuery {

	private ArrayList<Object> params = new ArrayList<Object>();
	private boolean firstCriteria = true;
	private StringBuffer querySbuf = new StringBuffer();
	
	public MatchingQuery(String queryString) {
		querySbuf.append(queryString);
	}

	public void addCriteria(String criteria) {
		startCriteria();
		querySbuf.append(criteria);
	}
	public void addCriteria(String xpath, Object value) {
		startCriteria();
		// match criteria :
		querySbuf.append(xpath);
		querySbuf.append("=?");
		params.add(value);
	}
	public void addCriteriaIfSet(String xpath, Object value) {
    	if (value != null) {
    		addCriteria(xpath, value);
    	}
	}
	/**
	 * Adds criteria that matches this constraint only if it is present on the looked up documents
	 * 
	 * xpath is null or or xpath=?
	 * 
	 * @param xpath
	 * @param value if String, will be wrapped by ''
	 */
	public void addConstraintMatchCriteria(String xpath, Object value) {
		startCriteria();
		querySbuf.append("(");
		
		// check that constraint is present :
		querySbuf.append(xpath);
		querySbuf.append(" IS NULL OR ");
		
		// match criteria :
		querySbuf.append(xpath);
		querySbuf.append("=");
		//String quoteIfString = (value instanceof String) ? "'" : "";
		//querySbuf.append(quoteIfString);
		querySbuf.append("?");
		//querySbuf.append(quoteIfString);
		params.add(value);
		
		querySbuf.append(")");
	}
	/**
	 * If value not null, adds criteria that matches this constraint only
	 * if it is present on the looked up documents.
	 * See addConstraintMatch.
	 * @param xpath
	 * @param value
	 */
	public void addConstraintMatchCriteriaIfSet(String xpath, Object value) {
    	if (value != null) {
    		addConstraintMatchCriteria(xpath, value);
    	}
	}

	/**
	 * Adds criteria that matches this constraint only if it, or else its
	 * alternative constraint, is present on the looked up documents
	 * 
	 * (xpath is null and (altxpath is null or altxpath=?)) or xpath=?
	 * 
	 * @param xpath
	 * @param value if String, will be wrapped by ''
	 */
	public void addConstraintMatchCriteriaWithAlt(String xpath, String altXpath, Object value) {
		startCriteria();
		querySbuf.append("((");
		
		// check that constraint is present :
		querySbuf.append(xpath);
		querySbuf.append(" IS NULL AND (");

		// check that alt constraint is present :
		querySbuf.append(altXpath);
		querySbuf.append(" IS NULL OR ");
		
		// match criteria with alt :
		querySbuf.append(altXpath);
		querySbuf.append("=");
		//String quoteIfString = (value instanceof String) ? "'" : "";
		//querySbuf.append(quoteIfString);
		querySbuf.append("?");
		//querySbuf.append(quoteIfString);
		params.add(value);
		
		// match criteria :
		querySbuf.append(")) OR ");
		querySbuf.append(xpath);
		querySbuf.append("=");
		//String quoteIfString = (value instanceof String) ? "'" : "";
		//querySbuf.append(quoteIfString);
		querySbuf.append("?");
		//querySbuf.append(quoteIfString);
		params.add(value);
		
		querySbuf.append(")");
	}
	/**
	 * If value not null, adds criteria that matches this constraint only if it, or else its
     * alternative constraint, is present on the looked up documents
     * 
	 * See addConstraintMatchCriteriaWithAlt.
	 * 
	 * @param xpath
	 * @param value
	 */
	public void addConstraintMatchCriteriaWithAltIfSet(String xpath, String altXpath, Object value) {
    	if (value != null) {
    		addConstraintMatchCriteriaWithAlt(xpath, altXpath, value);
    	}
	}
	
	
	public String build() throws ClientException {
		return NXQLQueryBuilder.getQuery(querySbuf.toString(), params.toArray(), /*false*/ true, true);
	}
	public String toString() {
		return querySbuf.toString() + " " + params; // TODO  better
	}
	
	private void startCriteria() {
		if (firstCriteria) {
			firstCriteria = false;
			querySbuf.append(" WHERE ");
		} else {
			querySbuf.append(" AND ");
		}
	}
}
