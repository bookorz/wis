package com.innolux.dao;  
  
import java.util.List;  
import java.util.Map;  
  
public interface GenericDao<T> {  
      
    public void save(T t) throws Exception;  
      
    public void delete(Object id,Class<T> clazz) throws Exception;  
      
    public void update(T t) throws Exception;  
      
    public T get(Object id,Class<T> clazz) throws Exception;  
      
    /** 
     * 根據條件查詢 
     * @param sqlWhereMap key：條件字段名 value：條件字段值 
     * @param clazz 
     * @return 
     * @throws Exception 
     */  
    public List<T> findAllByConditions(Map<String,Object> sqlWhereMap,Class<T> clazz) throws Exception;  
    /** 
    * 根據條件刪除
    * @param sqlWhereMap key：條件字段名 value：條件字段值 
    * @param clazz 
    * @return 
    * @throws Exception 
    */  
    public List<T> deleteAllByConditions(Map<String,Object> sqlWhereMap,Class<T> clazz) throws Exception;      
      
}  