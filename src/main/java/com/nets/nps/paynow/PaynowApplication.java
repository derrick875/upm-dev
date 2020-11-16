package com.nets.nps.paynow;

import java.util.Properties;

import com.nets.upos.sacrypt.hsm.TerminalCryptoHSMConfig;
import com.nets.upos.sacrypt.service.AppHSMEncryptionSvc;
import com.nets.upos.sacrypt.service.AppHSMEncryptionTokenDbServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ComponentScan(basePackages = { "com.nets" },
		// Exclude below from sacrypt, which conflicts with core
		excludeFilters = { @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
				@ComponentScan.Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { TerminalCryptoHSMConfig.class,
						AppHSMEncryptionSvc.class, AppHSMEncryptionTokenDbServiceImpl.class }) })
@ImportResource("classpath*:/spring-context.xml")
public class PaynowApplication {

	public static void main(String[] args) {
		SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(PaynowApplication.class)
				.sources(PaynowApplication.class).properties(getProperties());

		ConfigurableApplicationContext context = springApplicationBuilder.run(args);
	}

	static Properties getProperties() {
		Properties props = new Properties();
		props.put("spring.config.name", "paynow");
		props.put("spring.jpa.properties.hibernate.integration.envers.enabled", "false");
		return props;
	}
}
