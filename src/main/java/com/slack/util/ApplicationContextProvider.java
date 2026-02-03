package com.slack.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
	
	private static ApplicationContext context;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("어플리케이션컨텍스트 실행완료! ");
		context = applicationContext;
	}
	public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}
