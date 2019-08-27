package nc.vo.pub.tools;

import java.math.BigDecimal;

import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class PuPubVO {
	
	/**
	 * 将对象 转换为字符串， 并且去空格
	 */
	public static String getString_TrimZeroLenAsNull(Object value) {
		  if ( value==null || value.toString().trim().length()==0 ) {
		    return  null ;
		  }
		  return  value.toString().trim() ;
		}

	public static UFDate getUFDate(Object value) {
		  return getUFDate(value, true);
		}
	
	public static UFDate getUFDate(Object value, boolean isParse) {
		  if ( value == null || value.toString().trim().equals("") ){
		    return  null ;
		  }else if(value  instanceof  UFDate){
		    return  (UFDate)value ;
		  }else{
		    return  new UFDate(value.toString().trim(),isParse);
		  }
	}
	
	public static UFDouble getUFDouble_NullAsZero(Object  value) {
	  if ( value == null || value.toString().trim().equals("") ){
	    return  UFDouble.ZERO_DBL ;
	  }else if(value  instanceof  UFDouble){
	    return  (UFDouble)value ;
	  }else if(value  instanceof  BigDecimal){
	    return  new UFDouble((BigDecimal)value) ;
	  }else{
	    return  new UFDouble(value.toString().trim());
	  }
	}

	public static UFDouble getUFDouble_ValueAsValue(double  dValue) {
	  if ( dValue==0 ){
	    return  UFDouble.ZERO_DBL ;
	  }else{
	    return  new UFDouble(dValue) ;
	  }
	}

	public static UFDouble getUFDouble_ValueAsValue(Object  value) {
	  if ( value == null || value.toString().trim().equals("") ){
	    return  null ;
	  }else if(value  instanceof  UFDouble){
	    return  (UFDouble)value ;
	  }else if(value  instanceof  BigDecimal){
	    return  new UFDouble((BigDecimal)value) ;
	  }else{
	    return  new UFDouble(value.toString().trim());
	  }

	}
	
	public static UFDouble getUFDouble_ZeroAsNull(double  dValue) {
	  if ( dValue==0 ){
	    return  null ;
	  }else{
	    return  new UFDouble(dValue) ;
	  }
	}
	
	public static UFDouble getUFDouble_ZeroAsNull(Object  value) {
	  UFDouble  dValue = getUFDouble_NullAsZero(value) ;
	  if ( dValue.compareTo(UFDouble.ZERO_DBL)==0 ) {
	    return  null ;
	  }
	  return  dValue ;
	}
	
	public static final Integer ZERO_INTEGER = new Integer(0);
	public static final Integer	ONE_INTEGER = new Integer(1);
	
	public static Integer getInteger_NullAs(Object  value,Integer iDefaultValue) {
		  if ( value == null || value.toString().trim().equals("") ){
		    if (iDefaultValue.equals(PuPubVO.ZERO_INTEGER)) {
		      return  PuPubVO.ZERO_INTEGER ;
		    }else if (iDefaultValue.equals(PuPubVO.ONE_INTEGER)) {
		      return  PuPubVO.ONE_INTEGER ;
		    }else{
		      return  iDefaultValue;
		    }
		  }else if(value  instanceof  Integer){
		    return  (Integer)value ;
		  }else{
		    return  new Integer(value.toString().trim());
		  }
		}
	
	public static UFBoolean getUFBoolean_NullAs(Object value, UFBoolean bDefaultValue)
    {
        if(value == null || value.toString().trim().equals(""))
            return bDefaultValue;
        if(value instanceof UFBoolean)
            return (UFBoolean)value;
        else
            return new UFBoolean(value.toString().trim());
    }
	
}
