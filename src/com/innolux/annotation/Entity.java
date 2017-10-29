package com.innolux.annotation;  
  
import java.lang.annotation.ElementType;  
import java.lang.annotation.Retention;  
import java.lang.annotation.RetentionPolicy;  
import java.lang.annotation.Target;  
  
/** 
 * 數據庫表的的名稱 
 */  
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.TYPE)  
public @interface Entity {  
  
    /** 
     * 表名 
     */  
    String value();  
      
}  