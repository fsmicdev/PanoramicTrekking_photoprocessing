package au.net.drmic.photos.photoprocessing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import springfox.documentation.swagger2.annotations.EnableSwagger2
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.ApiInfo

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = [ "au.net.drmic" ])
class PhotoprocessingApplication {

	companion object {
		@JvmStatic fun main(args: Array<String>) {
			runApplication<PhotoprocessingApplication>(*args)
		}
	}

	@Bean
	fun api(): Docket {
		return Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				// .apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.apis(RequestHandlerSelectors
				.basePackage("au.net.drmic.photos.photoprocessing.web.controller"))
				.build()
	}

	fun apiInfo(): ApiInfo {
		return ApiInfoBuilder()
				.title("Photo Processing")
				.contact("Mic Giansiracusa")
				.version("1.0")
				.description("Micro-service for processing photos (including uploading and transformation " +
						"of photo images for different-scaled front-end presentation)")
				// .termsOfServiceUrl("https://")
				.build()
	}
}
