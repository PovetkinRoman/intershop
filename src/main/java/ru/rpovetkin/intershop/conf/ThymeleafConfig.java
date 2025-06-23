package ru.rpovetkin.intershop.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class ThymeleafConfig {

    // Компонент, отвечающий за загрузку самих шаблонов
    // В данном случае загружаются .html-страницы из resources/templates
    @Bean
    public ITemplateResolver templateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/"); // Директория для хранения HTML-шаблонов
        resolver.setSuffix(".html");       // Суффикс шаблонов
        resolver.setTemplateMode("HTML");  // Режим шаблонов
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

    // Шаблонизатор Thymeleaf, который будет загружать ThymeleafView
    @Bean
    public ViewResolver viewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }
}
