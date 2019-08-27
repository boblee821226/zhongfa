package nc.vo.pubapp.pattern.data;

import java.math.BigDecimal;

import nc.vo.pub.IAttributeMeta;
import nc.vo.pub.JavaType;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.lang.UFTime;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.md.model.impl.MDEnum;

/**
 * 值转换工具类，将某个未知类型的object转换为特定类型的值。
 * 
 * @since 6.0
 * @version 2007-6-28 上午10:36:29
 * @author 钟鸣
 */
public class ValueUtils {
  private ValueUtils() {
    // 缺省构造方法
  }

  /**
   * 根据元数据属性信息中定义的数据类型转换值的类型
   * 
   * @param value 要转换的值
   * @param attribute 元数据属性信息
   * @return 元数据属性信息所定义数据类型的值
   */
  public static Object convert(Object value, IAttributeMeta attribute) {
    JavaType type = attribute.getJavaType();
    Object ret = value;
    if (type == JavaType.UFDouble) {
      ret = ValueUtils.getUFDouble(value);
    }
    else if (type == JavaType.String) {
      ret = ValueUtils.getString(value);
    }
    else if (type == JavaType.Integer) {
      ret = ValueUtils.getInteger(value);
    }
    else if (type == JavaType.UFBoolean) {
      ret = ValueUtils.getUFBoolean(value);
    }
    else if (type == JavaType.UFDate) {
      ret = ValueUtils.getUFDate(value);
    }
    else if (type == JavaType.UFDateTime) {
      ret = ValueUtils.getUFDateTime(value);
    }
    else if (type == JavaType.UFLiteralDate) {
      ret = ValueUtils.getUFLiteralDate(value);
    }
    else if (type == JavaType.UFTime) {
      ret = ValueUtils.getUFTime(value);
    }
    else if (type == JavaType.BigDecimal) {
      ret = ValueUtils.getUFDouble(value);
    }
    else if (type == JavaType.Object) {
      ret = value;
    }
    else if (type == JavaType.UFFlag) {
      ret = ValueUtils.convertUFFlag(value, attribute);
    }
    else if (type == JavaType.UFStringEnum) {
      ret = ValueUtils.convertUFStringEnum(value, attribute);
    }
    else {
      String message = "不支持此种业务，请检查"; /*-=notranslate=-*/
      throw new IllegalArgumentException(message);
    }
    return ret;
  }

  @SuppressWarnings("unchecked")
  private static Object convertUFFlag(Object value, IAttributeMeta attribute) {
    Object ret;
    if (attribute.getEnumClass() == null) {
      ret = value;
    }
    else if (value == null) {
      ret = value;
    }
    else {
      MDEnum flag = ValueUtils.getUFFlag(attribute.getEnumClass(), value);
      ret = flag.value();
    }
    ret = ValueUtils.getInteger(ret);
    return ret;
  }

  @SuppressWarnings("unchecked")
  private static Object convertUFStringEnum(Object value,
      IAttributeMeta attribute) {
    Object ret;
    if (attribute.getEnumClass() == null) {
      ret = value;
    }
    else if (value == null) {
      ret = value;
    }
    else {
      MDEnum flag = ValueUtils.getUFStringEnum(attribute.getEnumClass(), value);
      ret = flag.value();
    }
    ret = ValueUtils.getString(ret);
    return ret;
  }

  /**
   * 将值转换为BigDecimal类型
   * 
   * @param value 要转换的值
   * @return 类型为BigDecimal的值
   */
  public static BigDecimal getBigDecimal(Object value) {
    BigDecimal retValue = null;
    if (value == null) {
      return null;
    }
    if (value instanceof BigDecimal) {
      retValue = (BigDecimal) value;
    }
    else if (value instanceof UFDouble) {
      retValue = ((UFDouble) value).toBigDecimal();
    }
    else {
      String str = value.toString();
      try {
        retValue = new BigDecimal(str);
      }
      catch (NumberFormatException ex) {
        ValueUtils.throwIllegalArgumentException(value, ex);
      }
    }
    return retValue;
  }

  /**
   * 将值转换为boolean类型
   * 
   * @param value 要转换的值
   * @return 类型为boolean的值
   */
  public static boolean getBoolean(Object value) {
    UFBoolean temp = ValueUtils.getUFBoolean(value);
    boolean flag = true;
    if (temp != null) {
      flag = temp.booleanValue();
    }
    return flag;
  }

  @SuppressWarnings("unchecked")
  private static <T extends MDEnum> T getEnum(Class<T> clazz, Object value) {
    if (value == null) {
      return null;
    }
    T ret = null;
    if (clazz.isAssignableFrom(value.getClass())) {
      ret = (T) value;
    }
    else {
      String flag = ValueUtils.getString(value);
      ret = MDEnum.valueOf(clazz, flag);
      if (ret == null) {
        String message = "值【" + value + "】不在枚举【" + clazz.getName() + "】的值域范围内"; /*-=notranslate=-*/
        ExceptionUtils.wrappBusinessException(message);
      }
    }
    return ret;
  }

  /**
   * 值转换工具类的工厂方法。
   * 
   * @return 返回值转化工具类的实例
   * @deprecated 用具体转换值的static方法替代
   */
  @Deprecated
  public static ValueUtils getInstance() {
    return new ValueUtils();
  }

  /**
   * 将值转换为int类型
   * 
   * @param value 要转换的值
   * @return 类型为int的值
   */
  public static int getInt(Object value) {
    return ValueUtils.getInt(value, 0);
  }

  /**
   * 将值转换为int类型，如果传出的值为null，则返回默认值
   * 
   * @param value 要转换的值
   * @param defaultValue 默认值
   * @return 类型为int的值
   */
  public static int getInt(Object value, int defaultValue) {
    Integer temp = ValueUtils.getInteger(value);
    int ret = defaultValue;
    if (temp != null) {
      ret = temp.intValue();
    }
    return ret;
  }

  /**
   * 将值转换为Integer类型
   * 
   * @param value 要转换的值
   * @return 类型为Integer的值
   */
  public static Integer getInteger(Object value) {
    Integer retValue = null;
    if (value == null) {
      return null;
    }
    if (value instanceof Integer) {
      retValue = (Integer) value;
    }
    else {
      String str = value.toString();
      try {
        retValue = Integer.valueOf(str);
      }
      catch (NumberFormatException ex) {
        ValueUtils.throwIllegalArgumentException(value, ex);
      }
    }
    return retValue;
  }

  /**
   * 将值转换为String类型
   * 
   * @param value 要转换的值
   * @return 类型为String的值
   */
  public static String getString(Object value) {
    String retValue = null;
    if (value == null) {
      return null;
    }
    /**
     * ZF 2019年6月6日11:07:47
     * 取消 保存时的 空格删除
     */
//    retValue = value.toString().trim();
      retValue = value.toString();
      /***END***/
    return retValue;
  }

  /**
   * 将值转换为UFBoolean类型
   * 
   * @param value 要转换的值
   * @return 类型为UFBoolean的值
   */
  public static UFBoolean getUFBoolean(Object value) {
    UFBoolean retValue = null;
    if (value == null) {
      return UFBoolean.FALSE;
    }
    if (value instanceof UFBoolean) {
      retValue = (UFBoolean) value;
      retValue = retValue.booleanValue() ? UFBoolean.TRUE : UFBoolean.FALSE;
    }
    else {
      retValue = UFBoolean.valueOf(value.toString().trim());
      retValue =
          UFBoolean.TRUE.equals(retValue) ? UFBoolean.TRUE : UFBoolean.FALSE;
    }
    return retValue;
  }

  /**
   * 将值转换为UFDate类型
   * 
   * @param value 要转换的值
   * @return 类型为UFDate的值
   */
  public static UFDate getUFDate(Object value) {
    UFDate retValue = null;
    if (value == null) {
      return null;
    }

    if (value instanceof UFDate) {
      retValue = (UFDate) value;
    }
    else {
      retValue = UFDate.fromPersisted(value.toString());
    }
    return retValue;
  }

  /**
   * 将值转换为UFDateTime类型
   * 
   * @param value 要转换的值
   * @return 类型为UFDateTime的值
   */
  public static UFDateTime getUFDateTime(Object value) {
    UFDateTime retValue = null;
    if (value == null) {
      return null;
    }
    if (value instanceof UFDateTime) {
      retValue = (UFDateTime) value;
    }
    else {
      retValue = new UFDateTime(value.toString());
    }
    return retValue;
  }

  /**
   * 将值转换为UFDouble类型
   * 
   * @param value 要转换的值
   * @return 类型为UFDouble的值
   */
  public static UFDouble getUFDouble(Object value) {
    UFDouble ret = null;
    if (value == null) {
      return null;
    }

    if (value instanceof UFDouble) {
      ret = (UFDouble) value;
    }
    else if (value instanceof BigDecimal) {
      BigDecimal temp = (BigDecimal) value;
      ret = new UFDouble(temp);
    }
    else if (value instanceof Number) {
      Number number = (Number) value;
      double temp = number.doubleValue();
      ret = new UFDouble(temp);
    }
    else {
      String str = value.toString();
      try {
        ret = new UFDouble(str);
      }
      catch (Exception ex) {
        ValueUtils.throwIllegalArgumentException(value, ex);
      }
    }
    return ret;
  }

  /**
   * 将值转换为UFFlag类型
   * 
   * @param <T> UFFlag枚举的类型
   * @param clazz UFFlag枚举的Class
   * @param value 转换的值
   * @return 类型为UFFlag的枚举值
   */
  public static <T extends MDEnum> T getUFFlag(Class<T> clazz, Object value) {
    return ValueUtils.getEnum(clazz, value);
  }

  /**
   * 将值转换为UFLiteralDate类型
   * 
   * @param value 要转换的值
   * @return 类型为UFLiteralDate的值
   */
  public static UFLiteralDate getUFLiteralDate(Object value) {
    UFLiteralDate retValue = null;
    if (value == null) {
      return null;
    }
    if (value instanceof UFLiteralDate) {
      retValue = (UFLiteralDate) value;
    }
    else {
      retValue = UFLiteralDate.fromPersisted(value.toString());
    }
    return retValue;
  }

  /**
   * 将值转换为UFStringEnum类型的枚举
   * 
   * @param <T> 枚举的类型
   * @param clazz 枚举的class
   * @param value 要转换的值
   * @return 类型为UFStringEnum的值
   */
  public static <T extends MDEnum> T getUFStringEnum(Class<T> clazz,
      Object value) {
    return ValueUtils.getEnum(clazz, value);
  }

  /**
   * 将值转换为UFTime类型
   * 
   * @param value 要转换的值
   * @return 类型为UFTime的值
   */
  public static UFTime getUFTime(Object value) {
    UFTime retValue = null;
    if (value == null) {
      return null;
    }
    if (value instanceof UFTime) {
      retValue = (UFTime) value;
    }
    else {
      retValue = new UFTime(value.toString());
    }
    return retValue;
  }

  private static void throwIllegalArgumentException(Object value, Exception ex) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("the value is:");
    buffer.append(value);
    buffer.append(" the error message is :");
    buffer.append(ex.getMessage());
    throw new IllegalArgumentException(buffer.toString());
  }

}
