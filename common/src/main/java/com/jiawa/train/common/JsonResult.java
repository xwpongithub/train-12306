package com.jiawa.train.common;

import com.alibaba.fastjson2.JSON;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor
@ToString
public class JsonResult extends LinkedHashMap<String, Object> implements Serializable {

    public static final int CODE_SUCCESS = 200;
    public static final int CODE_ERROR = 500;
    @Serial
    private static final long serialVersionUID = 29646765469935799L;


    /**
     * 构建
     * @param code 状态码
     * @param msg 信息
     * @param content 数据
     */
    public JsonResult(boolean success,int code, String msg, Object content) {
        this.setCode(code);
        this.setSuccess(success);
        this.setMessage(msg);
        this.setContent(content);
    }

    /**
     * 根据 Map 快速构建
     * @param map /
     */
    public JsonResult(Map<String, ?> map) {
        this.setMap(map);
    }

    /**
     * 获取code
     * @return code
     */
    public Integer getCode() {
        return (Integer)this.get("code");
    }
    /**
     * 获取msg
     * @return msg
     */
    public String getMessage() {
        return (String)this.get("message");
    }
    /**
     * 获取data
     * @return data
     */
    public Object getContent() {
        return this.get("content");
    }
    public Object getSuccess() {
        return this.get("success");
    }

    /**
     * 给code赋值，连缀风格
     * @param code code
     * @return 对象自身
     */
    public JsonResult setCode(int code) {
        this.put("code", code);
        return this;
    }
    /**
     * 给msg赋值，连缀风格
     * @param msg msg
     * @return 对象自身
     */
    public JsonResult setMessage(String msg) {
        this.put("message", msg);
        return this;
    }
    /**
     * 给data赋值，连缀风格
     * @param content content
     * @return 对象自身
     */
    public JsonResult setContent(Object content) {
        this.put("content", content);
        return this;
    }

    public JsonResult setSuccess(boolean success) {
        this.put("success", success);
        return this;
    }

    /**
     * 写入一个值 自定义key, 连缀风格
     * @param key key
     * @param content content
     * @return 对象自身
     */
    public JsonResult set(String key, Object content) {
        this.put(key, content);
        return this;
    }

    /**
     * 从jsonresult里获取一个值 根据自定义key
     * @param <T> 要转换为的类型
     * @param key key
     * @param cs 要转换为的类型
     * @return 值
     */
    public <T> T get(String key, Class<T> cs) {
        return getValueByType(get(key), cs);
    }

    /**
     * 写入一个Map, 连缀风格
     * @param map map
     * @return 对象自身
     */
    public JsonResult setMap(Map<String, ?> map) {
        for (String key : map.keySet()) {
            this.put(key, map.get(key));
        }
        return this;
    }


    // ============================  构建  ==================================

    // 构建成功
    public static JsonResult ok() {
        return new JsonResult(true,CODE_SUCCESS, "ok", null);
    }
    public static JsonResult ok(String msg) {
        return new JsonResult(true,CODE_SUCCESS, msg, null);
    }

    public static JsonResult content(Object content) {
        return new JsonResult(true,CODE_SUCCESS, "ok", content);
    }

    // 构建失败
    public static JsonResult error() {
        return new JsonResult(false,CODE_ERROR, "error", null);
    }
    public static JsonResult error(String msg) {
        return new JsonResult(false,CODE_ERROR, msg, null);
    }

    // 构建指定状态码
    public static JsonResult get(boolean success,int code, String msg, Object content) {
        return new JsonResult(success,code, msg, content);
    }

    /**
     * 将指定值转化为指定类型
     * @param <T> 泛型
     * @param obj 值
     * @param cs 类型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    private <T> T getValueByType(Object obj, Class<T> cs) {
        // 如果 obj 为 null 或者本来就是 cs 类型
        if(obj == null || obj.getClass().equals(cs)) {
            return (T)obj;
        }
        // 开始转换
        String obj2 = String.valueOf(obj);
        Object obj3;
        if (cs.equals(String.class)) {
            obj3 = obj2;
        } else if (cs.equals(int.class) || cs.equals(Integer.class)) {
            obj3 = Integer.valueOf(obj2);
        } else if (cs.equals(long.class) || cs.equals(Long.class)) {
            obj3 = Long.valueOf(obj2);
        } else if (cs.equals(short.class) || cs.equals(Short.class)) {
            obj3 = Short.valueOf(obj2);
        } else if (cs.equals(byte.class) || cs.equals(Byte.class)) {
            obj3 = Byte.valueOf(obj2);
        } else if (cs.equals(float.class) || cs.equals(Float.class)) {
            obj3 = Float.valueOf(obj2);
        } else if (cs.equals(double.class) || cs.equals(Double.class)) {
            obj3 = Double.valueOf(obj2);
        } else if (cs.equals(boolean.class) || cs.equals(Boolean.class)) {
            obj3 = Boolean.valueOf(obj2);
        } else if (cs.equals(char.class) || cs.equals(Character.class)) {
            obj3 = obj2.charAt(0);
        } else {
            obj3 = obj;
        }
        return (T)obj3;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
