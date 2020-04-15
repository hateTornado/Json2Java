package com.dcloud.task.init;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhongziqi
 * @version 1.0.0
 * @ClassName Json2Java.java
 * @Description TODO
 * @createTime 2020年04月14日 11:47:00
 */
public class Json2Java {
    public static String fileDictoryPath = "D:\\";
    String packageStr = "com.task.domain.vo.yi";
    Map<String,String> importStr = new HashMap<>();//String importStr = "import lombok.Data;";
    Map<String,String> annotionStr = new HashMap<>();//String annotionStr = "@Data";
    String classNameStr = "Default";
    Map<String,String> paramStr = new HashMap<>();

    public Json2Java(){}
    public Json2Java(String classNameStr){this.classNameStr=classNameStr;}

    public String parse(String jsonString) throws IOException {
        assert (jsonString!=null);
        //System.out.println(jsonString);
        Object obj = JSON.parse(jsonString);
        if(obj instanceof JSONObject){
            return this.parse((JSONObject)obj);
        }else if(obj instanceof JSONArray){
            return this.parse((JSONArray)obj);
        }else{
            System.out.println("其它数据类型,抛出异常!");
            return null;
        }
    }
    public String parse(JSONObject jsonObj) throws IOException {
        assert (jsonObj!=null);
        Map json = jsonObj;
        importStr.put("import lombok.Data;","");
        annotionStr.put("@Data","");
        int i=0;
        for (Object map : json.entrySet()){//遍历类中变量
            Object key = ((Map.Entry)map).getKey();
            Object val = ((Map.Entry) map).getValue();
            //System.out.println(key);
            Class<?> clazz = checkType(val);
            if(clazz.equals(JSONObject.class)){
                Json2Java childClass = new Json2Java(this.classNameStr+"Child"+(i++));
                childClass.parse((JSONObject)val);
            }
            if(clazz.equals(JSONArray.class)){
                Json2Java childClass = new Json2Java(this.classNameStr+"Child"+(i++));
                String paramItem = childClass.parse((JSONArray) val);
                //System.out.println(key +" is arr!!!");
                if(paramItem!=null) {
                    importStr.put("import java.util.List;","");
                    paramStr.put("List<" + paramItem + "> " + key,"");
                }
            }else {//基本类型
                paramStr.put(clazz.getSimpleName()+" "+key,"");
            }
        }
        StringBuffer sb = new StringBuffer();

        sb.append("package ");
        sb.append(packageStr);
        sb.append(";\n\n");


        for ( String s:importStr.keySet()) {
            sb.append(s);
            sb.append("\n");
        }
        sb.append("\n");
        for (String s : annotionStr.keySet()) {
            sb.append(s);
            sb.append("\n");
        }

        sb.append("public class ");
        sb.append(classNameStr);
        sb.append("{\n\n");

        for (String s : paramStr.keySet()) {
            sb.append(" private ");
            sb.append(s);
            sb.append(";\n");
        }

        sb.append("\n}");
        createClassFile(sb.toString(),classNameStr);
        return classNameStr;
    }

    private String parse(JSONArray arr) throws IOException {
        if(arr.isEmpty() || arr.size()==0)return null;//空json数组无法创建子类

        Object temp = arr.iterator().next();
        Class<?> clazz = checkType(temp);
        if(clazz.equals(JSONObject.class)){
            String childClassName= this.parse((JSONObject) temp);
            return childClassName;
        }else if(clazz.equals(JSONArray.class)){
            System.out.println("数组嵌套数组 待完善");
            return null;
        }else{
            return clazz.getSimpleName();
        }
    }

    private Class<?> checkType(Object value) {
        if(value instanceof JSONObject)return JSONObject.class;
        if(value instanceof JSONArray)return JSONArray.class;
        if(value instanceof Integer){
            if((Integer)value<65535)
            return Integer.class;
            else return BigDecimal.class;
        }
        if(value instanceof Date)return Date.class;
        else return String.class;
    }
    private String createClassFile(String classContent,String className) throws IOException {
        String filePath = fileDictoryPath+className+".java";
        File file = new File(filePath);
        if(!file.exists())file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(classContent.getBytes());
        fos.close();
        System.out.println(file.getAbsolutePath());
        return "success";
    }
}
