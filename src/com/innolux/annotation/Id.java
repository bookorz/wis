package com.innolux.annotation;  
  
import java.lang.annotation.ElementType;  
import java.lang.annotation.Retention;  
import java.lang.annotation.RetentionPolicy;  
import java.lang.annotation.Target;  
  
/** 
 * 標識數據庫字段的ID 
 */  
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface Id {  
  
    /** 
     * ID的名稱 
     * @return 
     */  
    String value();  
      
}  