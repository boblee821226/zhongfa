package nc.itf.zfpms;

import java.util.HashMap;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.zfpms.data.DataBillVO;

public interface IZfpms_dataMaintain {

	public void delete(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException;

	public DataBillVO[] insert(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException;

	public DataBillVO[] update(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException;

	public DataBillVO[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public DataBillVO[] save(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException;

	public DataBillVO[] unsave(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException;

	public DataBillVO[] approve(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException;

	public DataBillVO[] unapprove(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException;
	
	/**
	 * ȡ�� ���ӿ�
	 */
	public Object qushu(HashMap<String,Object> para,Object other) throws BusinessException;
	
	/**
	 * 2019��8��26��11��23��
	 * ���� NC ƾ֤
	 * flag = 0  ����
	 * flag = 1  ɾ��
	 */
	public String genNCVoucherInfo(DataBillVO billVO,int flag) throws Exception;
}
