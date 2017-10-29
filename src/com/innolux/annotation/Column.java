package com.innolux.annotation;  
  
import java.lang.annotation.ElementType;  
import java.lang.annotation.Retention;  
import java.lang.annotation.RetentionPolicy;  
import java.lang.annotation.Target;  
  
/** 
 * 標識數據庫字段的名稱 
 * @author 楊信 
 * 
 */  
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface Column {  
      
    /** 
     * 字段名稱 
     */  
    String value();  
      
    /** 
     * 字段的類型 
     * @return 
     */  
    Class<?> type() default String.class;  
      
    /** 
     * 字段的長度 
     * @return 
     */  
    int length() default 0;  
  
}  