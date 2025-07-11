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

import org.jspecify.annotations.Nullable;
import org.w3c.dom.Element;

import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;

/**
 * Parser for the 'annotation-driven' element of the 'rabbit' namespace.
 *
 * @author Stephane Nicoll
 * @author Artem Bilan
 *
 * @since 1.4
 */
class AnnotationDrivenParser implements BeanDefinitionParser {

	@Override
	public @Nullable BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);

		// Register component for the surrounding <rabbit:annotation-driven> element.
		CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
		parserContext.pushContainingComponent(compDefinition);

		// Nest the concrete post-processor bean in the surrounding component.
		BeanDefinitionRegistry registry = parserContext.getRegistry();

		if (registry.containsBeanDefinition(RabbitListenerConfigUtils.RABBIT_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)) {
			parserContext.getReaderContext().error(
					"Only one RabbitListenerAnnotationBeanPostProcessor may exist within the context.", source);
		}
		else {
			BeanDefinitionBuilder builder =
					BeanDefinitionBuilder.genericBeanDefinition(RabbitListenerAnnotationBeanPostProcessor.class);
			builder.getRawBeanDefinition().setSource(source);
			String endpointRegistry = element.getAttribute("registry");
			if (StringUtils.hasText(endpointRegistry)) {
				builder.addPropertyReference("endpointRegistry", endpointRegistry);
			}
			else {
				registerDefaultEndpointRegistry(source, parserContext);
			}

			String containerFactory = element.getAttribute("container-factory");
			if (StringUtils.hasText(containerFactory)) {
				builder.addPropertyValue("containerFactoryBeanName", containerFactory);
			}

			String handlerMethodFactory = element.getAttribute("handler-method-factory");
			if (StringUtils.hasText(handlerMethodFactory)) {
				builder.addPropertyReference("messageHandlerMethodFactory", handlerMethodFactory);
			}

			registerInfrastructureBean(parserContext, builder,
					RabbitListenerConfigUtils.RABBIT_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME);
		}

		// Finally register the composite component.
		parserContext.popAndRegisterContainingComponent();

		return null;
	}

	private static void registerDefaultEndpointRegistry(@Nullable Object source, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RabbitListenerEndpointRegistry.class);
		builder.getRawBeanDefinition().setSource(source);
		registerInfrastructureBean(parserContext, builder,
				RabbitListenerConfigUtils.RABBIT_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME);
	}

	private static void registerInfrastructureBean(
			ParserContext parserContext, BeanDefinitionBuilder builder, String beanName) {

		builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		parserContext.getRegistry().registerBeanDefinition(beanName, builder.getBeanDefinition());
		BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getBeanDefinition(), beanName);
		parserContext.registerComponent(new BeanComponentDefinition(holder));
	}

}
