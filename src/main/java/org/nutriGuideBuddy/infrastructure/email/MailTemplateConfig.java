package org.nutriGuideBuddy.infrastructure.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class MailTemplateConfig {

  @Bean
  public ClassLoaderTemplateResolver emailTemplateResolver() {
    var resolver = new ClassLoaderTemplateResolver();
    resolver.setPrefix("templates/email/");
    resolver.setSuffix(".html");
    resolver.setTemplateMode("HTML");
    resolver.setCharacterEncoding("UTF-8");
    resolver.setCacheable(true);
    resolver.setOrder(1);
    return resolver;
  }

  @Bean
  public SpringTemplateEngine emailTemplateEngine(
      ClassLoaderTemplateResolver emailTemplateResolver) {
    var engine = new SpringTemplateEngine();
    engine.addTemplateResolver(emailTemplateResolver);
    return engine;
  }
}
