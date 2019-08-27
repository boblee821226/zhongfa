package nc.bs.zfpms.data.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.zfpms.data.DataBillVO;

/**
 * ��׼���������BP
 */
public class AceZfpms_dataSendApproveBP {
	/**
	 * ������
	 * 
	 * @param vos
	 *            ����VO����
	 * @param script
	 *            ���ݶ����ű�����
	 * @return �����ĵ���VO����
	 */

	public DataBillVO[] sendApprove(DataBillVO[] clientBills,
			DataBillVO[] originBills) {
		for (DataBillVO clientFullVO : clientBills) {
			clientFullVO.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.COMMIT.value());
			clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// ���ݳ־û�
		DataBillVO[] returnVos = new BillUpdate<DataBillVO>().update(
				clientBills, originBills);
		return returnVos;
	}
}
