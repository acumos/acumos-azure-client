package org.acumos.azure.client;

import org.acumos.azure.client.controller.AbstractController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * http://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select() //
				.apis(RequestHandlerSelectors.basePackage(AbstractController.class.getPackage().getName())) //
				.paths(PathSelectors.any()) //
				.build() //
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		final String version = AzureClientServiceApplication.class.getPackage().getImplementationVersion();
		ApiInfo apiInfo = new ApiInfo("Cognita Azure Client REST API", 
				"Operations for Azure Deployment .", // description
				version == null ? "version not available" : version, // version
				"Terms of service", // TOS
				new Contact("Ashwin Sharma", "http://research.att.com", "ashwin.sharma@techmahindra.com"), // Contact
				"License of API", // License
				"API license URL"); // License URL
		return apiInfo;
	}
}