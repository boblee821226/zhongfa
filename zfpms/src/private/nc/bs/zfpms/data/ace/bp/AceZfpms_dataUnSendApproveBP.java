package nc.bs.zfpms.data.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.zfpms.data.DataBillVO;

/**
 * ��׼�����ջص�BP
 */
public class AceZfpms_dataUnSendApproveBP {

	public DataBillVO[] unSend(DataBillVO[] clientBills,
			DataBillVO[] originBills) {
		// ��VO�־û������ݿ���
		this.setHeadVOStatus(clientBills);
		BillUpdate<DataBillVO> update = new BillUpdate<DataBillVO>();
		DataBillVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(DataBillVO[] clientBills) {
		for (DataBillVO clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
