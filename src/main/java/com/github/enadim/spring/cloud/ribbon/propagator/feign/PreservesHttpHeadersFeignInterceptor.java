/*
 * Copyright (c) 2017 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.enadim.spring.cloud.ribbon.propagator.feign;

import com.github.enadim.spring.cloud.ribbon.context.ExecutionContext;
import com.github.enadim.spring.cloud.ribbon.propagator.AbstractExecutionContextCopy;
import com.github.enadim.spring.cloud.ribbon.propagator.Filter;
import com.github.enadim.spring.cloud.ribbon.propagator.PatternFilter;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Feign request interceptor that copies current {@link ExecutionContext} entries to the feign headers pre-filtering the header names using the provided {@link #filter}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class PreservesHttpHeadersFeignInterceptor extends AbstractExecutionContextCopy<RequestTemplate>
        implements RequestInterceptor {

    private final PatternFilter urlFilter;

    /**
     * Sole constructor.
     *
     * @param urlFilter          The url filter.
     * @param filter             The context entry key filter.
     * @param extraStaticEntries The extra static entries to copy.
     */
    public PreservesHttpHeadersFeignInterceptor(@NotNull PatternFilter urlFilter,
                                                @NotNull Filter<String> filter,
                                                @NotNull Map<String, String> extraStaticEntries) {
        super(filter, RequestTemplate::header, extraStaticEntries);
        this.urlFilter = urlFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(RequestTemplate template) {
        String url = template.request().url();
        if (urlFilter.accept(url)) {
            Set<Entry<String, String>> propagatedAttributes = copy(template);
            log.trace("Propagated outbound headers {} for url [{}].", propagatedAttributes, url);
        } else {
            log.trace("Propagation disabled for url [{}]", url);
        }
    }
}
