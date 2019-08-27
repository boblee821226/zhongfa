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
 * ֵת�������࣬��ĳ��δ֪���͵�objectת��Ϊ�ض����͵�ֵ��
 * 
 * @since 6.0
 * @version 2007-6-28 ����10:36:29
 * @author ����
 */
public class ValueUtils {
  private ValueUtils() {
    // ȱʡ���췽��
  }

  /**
   * ����Ԫ����������Ϣ�ж������������ת��ֵ������
   * 
   * @param value Ҫת����ֵ
   * @param attribute Ԫ����������Ϣ
   * @return Ԫ����������Ϣ�������������͵�ֵ
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
      String message = "��֧�ִ���ҵ������"; /*-=notranslate=-*/
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
   * ��ֵת��ΪBigDecimal����
   * 
   * @param value Ҫת����ֵ
   * @return ����ΪBigDecimal��ֵ
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
   * ��ֵת��Ϊboolean����
   * 
   * @param value Ҫת����ֵ
   * @return ����Ϊboolean��ֵ
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
        String message = "ֵ��" + value + "������ö�١�" + clazz.getName() + "����ֵ��Χ��"; /*-=notranslate=-*/
        ExceptionUtils.wrappBusinessException(message);
      }
    }
    return ret;
  }

  /**
   * ֵת��������Ĺ���������
   * 
   * @return ����ֵת���������ʵ��
   * @deprecated �þ���ת��ֵ��static�������
   */
  @Deprecated
  public static ValueUtils getInstance() {
    return new ValueUtils();
  }

  /**
   * ��ֵת��Ϊint����
   * 
   * @param value Ҫת����ֵ
   * @return ����Ϊint��ֵ
   */
  public static int getInt(Object value) {
    return ValueUtils.getInt(value, 0);
  }

  /**
   * ��ֵת��Ϊint���ͣ����������ֵΪnull���򷵻�Ĭ��ֵ
   * 
   * @param value Ҫת����ֵ
   * @param defaultValue Ĭ��ֵ
   * @return ����Ϊint��ֵ
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
   * ��ֵת��ΪInteger����
   * 
   * @param value Ҫת����ֵ
   * @return ����ΪInteger��ֵ
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
   * ��ֵת��ΪString����
   * 
   * @param value Ҫת����ֵ
   * @return ����ΪString��ֵ
   */
  public static String getString(Object value) {
    String retValue = null;
    if (value == null) {
      return null;
    }
    /**
     * ZF 2019��6��6��11:07:47
     * ȡ�� ����ʱ�� �ո�ɾ��
     */
//    retValue = value.toString().trim();
      retValue = value.toString();
      /***END***/
    return retValue;
  }

  /**
   * ��ֵת��ΪUFBoolean����
   * 
   * @param value Ҫת����ֵ
   * @return ����ΪUFBoolean��ֵ
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
   * ��ֵת��ΪUFDate����
   * 
   * @param value Ҫת����ֵ
   * @return ����ΪUFDate��ֵ
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
   * ��ֵת��ΪUFDateTime����
   * 
   * @param value Ҫת����ֵ
   * @return ����ΪUFDateTime��ֵ
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
   * ��ֵת��ΪUFDouble����
   * 
   * @param value Ҫת����ֵ
   * @return ����ΪUFDouble��ֵ
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
   * ��ֵת��ΪUFFlag����
   * 
   * @param <T> UFFlagö�ٵ�����
   * @param clazz UFFlagö�ٵ�Class
   * @param value ת����ֵ
   * @return ����ΪUFFlag��ö��ֵ
   */
  public static <T extends MDEnum> T getUFFlag(Class<T> clazz, Object value) {
    return ValueUtils.getEnum(clazz, value);
  }

  /**
   * ��ֵת��ΪUFLiteralDate����
   * 
   * @param value Ҫת����ֵ
   * @return ����ΪUFLiteralDate��ֵ
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
   * ��ֵת��ΪUFStringEnum���͵�ö��
   * 
   * @param <T> ö�ٵ�����
   * @param clazz ö�ٵ�class
   * @param value Ҫת����ֵ
   * @return ����ΪUFStringEnum��ֵ
   */
  public static <T extends MDEnum> T getUFStringEnum(Class<T> clazz,
      Object value) {
    return ValueUtils.getEnum(clazz, value);
  }

  /**
   * ��ֵת��ΪUFTime����
   * 
   * @param value Ҫת����ֵ
   * @return ����ΪUFTime��ֵ
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
