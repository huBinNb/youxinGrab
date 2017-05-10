package com.lidehang;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//让spring来加载该类配置  在通过第二个注解来启动swagger
@Configuration
@EnableSwagger2
public class Swagger{
	@Bean
	public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.lidehang.national.action"))
                .apis(RequestHandlerSelectors.basePackage("com.lidehang.data.collection.action"))
                .paths(PathSelectors.any())
                .build();
    }
	
	
	private ApiInfo apiInfo(){
		return new ApiInfoBuilder()
				.title("数据抓取引擎")
			    .description("接口返回采用统一格式，seccess为true:处理成功，false：处理失败；code为200：处理成功，"
			    		+ "500：系统异常，403：无访问权限，errorMsg：接口处理失败是返回的错误信息；data:处理成功后的返回内容")
//			    .termsOfServiceUrl("https://baidu.com")
			    .contact("hobn")
			    .version("0.0")
			    .build();
		
		
	}

}
