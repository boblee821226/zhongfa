package nc.vo.zfpms.data;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 *   此处添加累的描述信息
 * </p>
 *  创建日期:2019-7-19
 * @author 
 * @version NCPrj ??
 */
 
public class DataDVO extends SuperVO {
	
/**
*PMS数据子三pk
*/
public String pk_zfpms_data_d;
/**
*上层单据主键
*/
public String pk_zfpms_data;
/**
*时间戳
*/
public UFDateTime ts;
    
    
/**
* 属性 pk_zfpms_data_d的Getter方法.属性名：PMS数据子三pk
*  创建日期:2019-7-19
* @return java.lang.String
*/
public String getPk_zfpms_data_d() {
return this.pk_zfpms_data_d;
} 

/**
* 属性pk_zfpms_data_d的Setter方法.属性名：PMS数据子三pk
* 创建日期:2019-7-19
* @param newPk_zfpms_data_d java.lang.String
*/
public void setPk_zfpms_data_d ( String pk_zfpms_data_d) {
this.pk_zfpms_data_d=pk_zfpms_data_d;
} 
 
/**
* 属性 生成上层主键的Getter方法.属性名：上层主键
*  创建日期:2019-7-19
* @return String
*/
public String getPk_zfpms_data(){
return this.pk_zfpms_data;
}
/**
* 属性生成上层主键的Setter方法.属性名：上层主键
* 创建日期:2019-7-19
* @param newPk_zfpms_data String
*/
public void setPk_zfpms_data(String pk_zfpms_data){
this.pk_zfpms_data=pk_zfpms_data;
} 
/**
* 属性 生成时间戳的Getter方法.属性名：时间戳
*  创建日期:2019-7-19
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getTs() {
return this.ts;
}
/**
* 属性生成时间戳的Setter方法.属性名：时间戳
* 创建日期:2019-7-19
* @param newts nc.vo.pub.lang.UFDateTime
*/
public void setTs(UFDateTime ts){
this.ts=ts;
} 
     
    @Override
    public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("zfpms.zfpms_dataDVO");
    }
   }
    