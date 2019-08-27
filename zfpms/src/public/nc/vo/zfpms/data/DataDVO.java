package nc.vo.zfpms.data;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> �˴���Ҫ�������๦�� </b>
 * <p>
 *   �˴�����۵�������Ϣ
 * </p>
 *  ��������:2019-7-19
 * @author 
 * @version NCPrj ??
 */
 
public class DataDVO extends SuperVO {
	
/**
*PMS��������pk
*/
public String pk_zfpms_data_d;
/**
*�ϲ㵥������
*/
public String pk_zfpms_data;
/**
*ʱ���
*/
public UFDateTime ts;
    
    
/**
* ���� pk_zfpms_data_d��Getter����.��������PMS��������pk
*  ��������:2019-7-19
* @return java.lang.String
*/
public String getPk_zfpms_data_d() {
return this.pk_zfpms_data_d;
} 

/**
* ����pk_zfpms_data_d��Setter����.��������PMS��������pk
* ��������:2019-7-19
* @param newPk_zfpms_data_d java.lang.String
*/
public void setPk_zfpms_data_d ( String pk_zfpms_data_d) {
this.pk_zfpms_data_d=pk_zfpms_data_d;
} 
 
/**
* ���� �����ϲ�������Getter����.���������ϲ�����
*  ��������:2019-7-19
* @return String
*/
public String getPk_zfpms_data(){
return this.pk_zfpms_data;
}
/**
* ���������ϲ�������Setter����.���������ϲ�����
* ��������:2019-7-19
* @param newPk_zfpms_data String
*/
public void setPk_zfpms_data(String pk_zfpms_data){
this.pk_zfpms_data=pk_zfpms_data;
} 
/**
* ���� ����ʱ�����Getter����.��������ʱ���
*  ��������:2019-7-19
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getTs() {
return this.ts;
}
/**
* ��������ʱ�����Setter����.��������ʱ���
* ��������:2019-7-19
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
    