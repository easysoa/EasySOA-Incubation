package org.easysoa.registry.utils;

import javax.el.ExpressionFactory;

import org.nuxeo.ecm.platform.el.ExpressionContext;
import org.nuxeo.ecm.platform.el.ExpressionEvaluator;

public class SimpleELEvaluator {

	private ExpressionContext expressionContext;
	private ExpressionEvaluator expressionEvaluator;

	public SimpleELEvaluator() {
		expressionContext = new ExpressionContext();
		expressionEvaluator = new ExpressionEvaluator(ExpressionFactory.newInstance());
	}
		
	public void set(String key, Object value) {
		expressionEvaluator.bindValue(expressionContext, key, value);
	}
	
	public String evaluate(String elString) {
		return expressionEvaluator.evaluateExpression(expressionContext, elString, String.class);
	}
	
}
