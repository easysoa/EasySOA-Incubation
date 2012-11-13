package org.easysoa.registry.inheritance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * 
 * @author mkalam-alami
 * 
 */
@XObject("inheritedFacet")
public class InheritedFacetDescriptor {

	@XNode("@facetName")
	public String facetName;

	@XNodeList(value = "transfer", type = ArrayList.class, componentType = TransferLogic.class)
	public ArrayList<TransferLogic> transferLogicList;

	@XObject("transfer")
	public static class TransferLogic {
		@XNode("from")
		public String from;

		@XNode("to")
		public String to;

		@XNode("selector@type")
		public String selectorType;

		@XNodeMap(value = "selector/parameter", key = "@name",
				componentType = String.class, type = HashMap.class)
		public Map<String, String> parameters;
	}
	
}