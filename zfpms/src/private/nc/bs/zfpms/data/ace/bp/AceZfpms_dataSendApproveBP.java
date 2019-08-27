package nc.bs.zfpms.data.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.zfpms.data.DataBillVO;

/**
 * 标准单据送审的BP
 */
public class AceZfpms_dataSendApproveBP {
	/**
	 * 送审动作
	 * 
	 * @param vos
	 *            单据VO数组
	 * @param script
	 *            单据动作脚本对象
	 * @return 送审后的单据VO数组
	 */

	public DataBillVO[] sendApprove(DataBillVO[] clientBills,
			DataBillVO[] originBills) {
		for (DataBillVO clientFullVO : clientBills) {
			clientFullVO.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.COMMIT.value());
			clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// 数据持久化
		DataBillVO[] returnVos = new BillUpdate<DataBillVO>().update(
				clientBills, originBills);
		return returnVos;
	}
}
