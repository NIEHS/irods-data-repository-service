package org.irods.jargon.ga4gh.dos.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-06-23T12:31:14.100Z[GMT]")
@Configuration
public class SwaggerDocumentationConfig {

    @Bean
    public Docket customImplementation(){
        return new Docket(DocumentationType.OAS_30)
                .select()
                    .apis(RequestHandlerSelectors.basePackage("io.swagger.api"))
                    .build()
                .directModelSubstitute(org.threeten.bp.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(org.threeten.bp.OffsetDateTime.class, java.util.Date.class)
                .apiInfo(apiInfo());
    }

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Data Repository Service")
            .description("No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)")
            .license("Apache 2.0")
            .licenseUrl("https://raw.githubusercontent.com/ga4gh/data-repository-service-schemas/master/LICENSE")
            .termsOfServiceUrl("")
            .version("1.2.0")
            .contact(new Contact("","", "ga4gh-cloud@ga4gh.org"))
            .build();
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Data Repository Service")
                .description("No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)")
                .termsOfService("")
                .version("1.2.0")
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://raw.githubusercontent.com/ga4gh/data-repository-service-schemas/master/LICENSE"))
                .contact(new io.swagger.v3.oas.models.info.Contact()
                    .email("ga4gh-cloud@ga4gh.org")));
    }

}
