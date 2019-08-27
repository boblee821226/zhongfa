package nc.bs.zfpms.data.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.zfpms.data.DataBillVO;

/**
 * 标准单据弃审的BP
 */
public class AceZfpms_dataUnApproveBP {

	public DataBillVO[] unApprove(DataBillVO[] clientBills,
			DataBillVO[] originBills) {
		for (DataBillVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<DataBillVO> update = new BillUpdate<DataBillVO>();
		DataBillVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
