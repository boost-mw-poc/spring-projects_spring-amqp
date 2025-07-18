/*
 * Copyright 2002-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.amqp.rabbit.config;

import org.w3c.dom.Element;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;

/**
 * @author Dave Syer
 * @author Gary Russell
 *
 */
public class TopicExchangeParser extends AbstractExchangeParser {

	private static final String BINDING_PATTERN_ATTR = "pattern";

	@Override
	protected Class<?> getBeanClass(Element element) {
		return TopicExchange.class;
	}

	@Override
	protected BeanDefinitionBuilder parseBinding(String exchangeName, Element binding, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(BindingFactoryBean.class);
		parseDestination(binding, parserContext, builder);
		builder.addPropertyValue("exchange", new TypedStringValue(exchangeName));
		builder.addPropertyValue("routingKey", new TypedStringValue(binding.getAttribute(BINDING_PATTERN_ATTR)));
		return builder;
	}

}
