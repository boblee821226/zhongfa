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
	 * 取数 调接口
	 */
	public Object qushu(HashMap<String,Object> para,Object other) throws BusinessException;
	
	/**
	 * 2019年8月26日11点23分
	 * 生成 NC 凭证
	 * flag = 0  生成
	 * flag = 1  删除
	 */
	public String genNCVoucherInfo(DataBillVO billVO,int flag) throws Exception;
}
