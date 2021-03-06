package com.spronq.mbt.VdekMock.config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


import static springfox.documentation.builders.PathSelectors.*;
import static com.google.common.base.Predicates.*;
/*
See http://www.baeldung.com/swagger-2-documentation-for-spring-rest-api for an example
 */
@EnableSwagger2
public class SwaggerConfiguration {


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.spronq.mbt.VdekMock.api"))
                .paths(paths())
                .build()
                .apiInfo(apiInfo());
    }

    private Predicate<String> paths() {
        return or(
            regex("/shipments.*"),
            regex("/users.*")
       );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Vdek REST API")
                .description("Vdek Mock API for MBT testing")
                .version("1.0")
                .contact(new Contact("SpronQ", "www.spronq.com", null))
                .build();
    }

    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
