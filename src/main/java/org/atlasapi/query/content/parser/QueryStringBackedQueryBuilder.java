/* Copyright 2009 Meta Broadcast Ltd

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You may
obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License. */

package org.atlasapi.query.content.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.atlasapi.content.criteria.AtomicQuery;
import org.atlasapi.content.criteria.AttributeQuery;
import org.atlasapi.content.criteria.BooleanAttributeQuery;
import org.atlasapi.content.criteria.ContentQuery;
import org.atlasapi.content.criteria.attribute.Attribute;
import org.atlasapi.content.criteria.attribute.Attributes;
import org.atlasapi.content.criteria.attribute.QueryFactory;
import org.atlasapi.content.criteria.attribute.StringValuedAttribute;
import org.atlasapi.content.criteria.operator.Operator;
import org.atlasapi.content.criteria.operator.Operators;
import org.atlasapi.media.entity.Content;
import org.atlasapi.media.entity.Countries;
import org.atlasapi.media.entity.Country;
import org.atlasapi.media.entity.Description;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.query.Selection.SelectionBuilder;

public class QueryStringBackedQueryBuilder {

    public static final String CALLBACK = "callback";
	
	private static final Operator DEFAULT_OPERATOR = Operators.EQUALS;
	private static final String ATTRIBUTE_OPERATOR_SEPERATOR = "-";
	private static final String OPERAND_SEPERATOR = ",";
	
	private final Set<String> ignoreParams = Sets.newHashSet(Selection.START_INDEX_REQUEST_PARAM, Selection.LIMIT_REQUEST_PARAM, CALLBACK); 

	private final DateTimeInQueryParser dateTimeParser = new DateTimeInQueryParser();
	
	private static final SelectionBuilder selectionBuilder = Selection.builder();
	private final DefaultQueryAttributesSetter defaults;

	private static final DefaultQueryAttributesSetter NO_DEFAULTS = new DefaultQueryAttributesSetter() {
		
		@Override
		public ContentQuery withDefaults() {
			return ContentQuery.MATCHES_EVERYTHING;
		}
	};
	
	public QueryStringBackedQueryBuilder() {
		this(NO_DEFAULTS);
	}
	
	public QueryStringBackedQueryBuilder(DefaultQueryAttributesSetter defaults) {
		this.defaults = defaults;
	}
	
	@SuppressWarnings("unchecked")
	public ContentQuery build(HttpServletRequest request, Class<? extends Content> context) {
		return build(request.getParameterMap(), context).copyWithSelection(selectionBuilder.build(request));
	}
	
	ContentQuery build(Map<String, String[]> params,  Class<? extends Content> context) {
		return buildFromFilteredMap(filter(params), context);
	}
	
	private Map<String, String[]> filter(Map<String, String[]> parameterMap) {
		Map<String, String[]> filtered = Maps.newHashMap();
		for (Entry<String, String[]> entry : parameterMap.entrySet()) {
			if (!ignoreParams.contains(entry.getKey())) {
				filtered.put(entry.getKey(), entry.getValue());
			}
		}
		return filtered;
	}
	
	private ContentQuery buildFromFilteredMap(Map<String, String[]> params, Class<? extends Description> context) {
		if (params.isEmpty()) {
			throw new IllegalArgumentException("No parameters specified");
		}
				
		Set<Attribute<?>> userSuppliedAttributes = Sets.newHashSet();
		List<AtomicQuery> operands = Lists.newArrayList();
		
		for (Entry<String, String[]> param : params.entrySet()) {
			String attributeName = param.getKey();
			for (String value : param.getValue()) {
				
				AttributeOperatorValues query = toQuery(attributeName, value, context);
				
				userSuppliedAttributes.add(query.attribute);
				
				if (Attributes.BROADCAST_TRANSMISSION_TIME.equals(query.attribute) && Operators.EQUALS.equals(query.op)) {
					DateTime when = Iterables.getOnlyElement(coerceListToType(query.values, DateTime.class));
					
					operands.add(Attributes.BROADCAST_TRANSMISSION_TIME.createQuery(Operators.BEFORE, ImmutableList.of(when.plusSeconds(1))));
					operands.add(Attributes.BROADCAST_TRANSMISSION_END_TIME.createQuery(Operators.AFTER, ImmutableList.of(when)));

					// hack to show unavailable broadcasts
					userSuppliedAttributes.add(Attributes.LOCATION_AVAILABLE);
					
					continue;
				}
				
				AttributeQuery<?> attributeQuery = query.toAttributeQuery();
				
				if (attributeQuery instanceof BooleanAttributeQuery && ((BooleanAttributeQuery) attributeQuery).isUnconditionallyTrue()) {
					continue;
				}

				operands.add(attributeQuery);
			}
		}
		
		for (AtomicQuery atomicQuery : defaults.withDefaults().operands()) {
			if (atomicQuery instanceof AttributeQuery<?> && userSuppliedAttributes.contains(((AttributeQuery<?>) atomicQuery).getAttribute())) {
				continue;
			}
			operands.add(atomicQuery);
		}
		
		return new ContentQuery(operands);
	}
	

	
	private class AttributeOperatorValues {
		
		private final Attribute<?> attribute;
		private final Operator op;
		private final List<String> values;
		
		public AttributeOperatorValues(Attribute<?> attribute, Operator op, List<String> values) {
			this.attribute = attribute;
			this.op = op;
			this.values = values;
		}
		public AttributeQuery<?> toAttributeQuery() {
			return attribute.createQuery(op, coerceListToType(values, attribute.requiresOperandOfType()));
		}
	}
	
	private AttributeOperatorValues toQuery(String paramKey, String paramValue, Class<? extends Description> queryContext) {
		String[] parts = paramKey.split(ATTRIBUTE_OPERATOR_SEPERATOR);
		if (parts.length > 2) {
			throw new IllegalArgumentException("Malformed attribute and operator combination");
		}
		String attributeName = parts[0];
		Attribute<?> attribute = Attributes.lookup(attributeName, queryContext);
        if (attribute == null) {
            throw new IllegalArgumentException(attributeName + " is not a valid attribute");
        }
        
		Operator op = defaultOperator(attribute);
		if (parts.length == 2) {
			op = Operators.lookup(parts[1]);
			if (op == null) {
				throw new IllegalArgumentException("Unknown operator " + parts[1]);
			}
		} 
		
		List<String> values;
		if (Boolean.class.equals(attribute.requiresOperandOfType()) && "any".equals(paramValue)) {
			values = Arrays.asList("true", "false");
		} else {
			values = Arrays.asList(paramValue.split(OPERAND_SEPERATOR));
		}
		return new AttributeOperatorValues(attribute, op, formatValues(attribute, values));
	}
	
	private Operator defaultOperator(QueryFactory<?> attribute) {
	    if (attribute instanceof StringValuedAttribute) {
	        String attributeName = ((StringValuedAttribute) attribute).javaAttributeName();
	        if ("title".equals(attributeName)) {
	            return Operators.SEARCH;
	        }
	    }
	    return DEFAULT_OPERATOR;
	}
	
	private List<String> formatValues(QueryFactory<?> attribute, List<String> values) {
	    List<String> formattedValues = Lists.newArrayList();
	    if (!(attribute instanceof StringValuedAttribute)) {
	        return values;
	    }
	    
	    if (Attributes.POLICY_AVAILABLE_COUNTRY.equals(attribute)) {
	    	Set<Country> withAll = Sets.newHashSet(Countries.fromCodes(values));
	    	withAll.add(Countries.ALL);
	    	return ImmutableList.copyOf(Countries.toCodes(withAll));
	    }
	    
	    String attributeName = ((StringValuedAttribute) attribute).javaAttributeName();
	    
	    if ("genre".equals(attributeName)) {
	        for (String value: values) {
	            if (! value.startsWith("http://")) {
	                value = "http://ref.atlasapi.org/genres/atlas/"+value;
	            }
	            formattedValues.add(value);
	        }
	    } else if ("tag".equals(attributeName)) {
	        for (String value: values) {
                if (! value.startsWith("http://")) {
                    value = "http://ref.atlasapi.org/tags/"+value;
                }
                formattedValues.add(value);
            }
	    } else {
	        formattedValues.addAll(values);
	    }
	    
	    return formattedValues;
	}


	@SuppressWarnings("unchecked")
	private <T> List<T> coerceListToType(List<String> paramValues, Class<T> requiresOperandOfType) {
		return (List) Lists.transform(paramValues, coerceToType(requiresOperandOfType));
	}

	private Function<String, Object> coerceToType(final Class<?> type) {
		return new Function<String, Object>() {
			@Override
			public Object apply(String paramValue) {
				if (String.class.equals(type)) {
					return paramValue;
				}
				if (Integer.class.equals(type)) {
					return Integer.parseInt(paramValue);
				}
				if (DateTime.class.equals(type)) {
					return dateTimeParser.parse(paramValue);
				}
				if (Enum.class.isAssignableFrom(type)) {
					return coerceToEnumValue(paramValue, type);
				}
				if (Boolean.class.equals(type)) {
					return Boolean.valueOf(paramValue);
				}
				throw new UnsupportedOperationException();
			}
		};
	}

	@SuppressWarnings("unchecked")
	private Enum<?> coerceToEnumValue(String paramValue, Class<?> type) {
		return Enum.valueOf((Class) type, paramValue.toUpperCase());
	}

	
	public QueryStringBackedQueryBuilder withIgnoreParams(String... params) {
		ignoreParams.addAll(Arrays.asList(params));
		return this;
	}
}
