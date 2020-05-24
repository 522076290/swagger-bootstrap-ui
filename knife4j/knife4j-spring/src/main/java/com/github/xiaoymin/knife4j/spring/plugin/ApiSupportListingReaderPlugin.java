/*
 * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.xiaominfo.com.
 * Developer Web Site: http://open.xiaominfo.com.
 */

package com.github.xiaoymin.knife4j.spring.plugin;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.github.xiaoymin.knife4j.spring.extension.ApiAuthorExtension;
import com.github.xiaoymin.knife4j.spring.extension.ApiOrderExtension;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingBuilderPlugin;
import springfox.documentation.spi.service.contexts.ApiListingContext;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newTreeSet;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static springfox.documentation.service.Tags.emptyTags;
import static springfox.documentation.swagger.common.SwaggerPluginSupport.pluginDoesApply;

/***
 * unused
 * @since:knife4j 1.0
 * @author <a href="mailto:xiaoymin@foxmail.com">xiaoymin@foxmail.com</a> 
 * 2020/03/31 12:48
 */
/*@Component
@Order(Ordered.HIGHEST_PRECEDENCE+101)*/
public class ApiSupportListingReaderPlugin implements ApiListingBuilderPlugin {

    @Override
    public void apply(ApiListingContext apiListingContext) {
        Optional<? extends Class<?>> controller = apiListingContext.getResourceGroup().getControllerClass();
        if (controller.isPresent()) {
            Optional<Api> apiAnnotation = fromNullable(findAnnotation(controller.get(), Api.class));
            String description = emptyToNull(apiAnnotation.transform(descriptionExtractor()).orNull());

            Set<String> tagSet = apiAnnotation.transform(tags())
                    .or(Sets.<String>newTreeSet());
            if (tagSet.isEmpty()) {
                tagSet.add(apiListingContext.getResourceGroup().getGroupName());
            }

            Optional<ApiSupport> apiSupportAnnotation = fromNullable(findAnnotation(controller.get(), ApiSupport.class));
            Set<Tag> tags=new HashSet<>();
            if (apiSupportAnnotation.isPresent()){
                ApiSupport apiSupport=apiSupportAnnotation.get();
                String author=apiSupport.author();
                if (!"".equalsIgnoreCase(author)){
                    Integer order=apiSupport.order();
                    if (tagSet.isEmpty()) {
                        Tag tag=new Tag(apiListingContext.getResourceGroup().getGroupName(),description, Lists.newArrayList(new ApiOrderExtension(order),new ApiAuthorExtension(author)));
                        tags.add(tag);
                    }
                }
            }
            apiListingContext.apiListingBuilder()
                    .description(description)
                    .tagNames(tagSet).tags(tags)
            ;
        }
    }


    private Function<Api, String> descriptionExtractor() {
        return new Function<Api, String>() {
            @Override
            public String apply(Api input) {
                return input.description();
            }
        };
    }

    private Function<Api, Set<String>> tags() {
        return new Function<Api, Set<String>>() {
            @Override
            public Set<String> apply(Api input) {
                return newTreeSet(from(newArrayList(input.tags())).filter(emptyTags()).toSet());
            }
        };
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return pluginDoesApply(delimiter);
    }
}
