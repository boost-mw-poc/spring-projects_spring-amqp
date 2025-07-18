/*
 * Copyright 2013-present the original author or authors.
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

package org.springframework.amqp.core;

/**
 * To be used with the receive-and-reply methods of {@link org.springframework.amqp.core.AmqpTemplate}
 * to determine {@link org.springframework.amqp.core.Address} for {@link org.springframework.amqp.core.Message}
 * to send at runtime.
 *
 * <p>This often as an anonymous class within a method implementation.
 *
 * @param <T> the reply type.
 * @author Artem Bilan
 * @author Gary Russell
 * @since 1.3
 */
@FunctionalInterface
public interface ReplyToAddressCallback<T> {

	Address getReplyToAddress(Message request, T reply);

}
