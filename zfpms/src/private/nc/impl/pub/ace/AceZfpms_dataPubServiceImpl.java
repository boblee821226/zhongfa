package nc.impl.pub.ace;

import java.util.HashMap;

import nc.bs.zfpms.data.ace.bp.AceZfpms_dataApproveBP;
import nc.bs.zfpms.data.ace.bp.AceZfpms_dataDeleteBP;
import nc.bs.zfpms.data.ace.bp.AceZfpms_dataInsertBP;
import nc.bs.zfpms.data.ace.bp.AceZfpms_dataSendApproveBP;
import nc.bs.zfpms.data.ace.bp.AceZfpms_dataUnApproveBP;
import nc.bs.zfpms.data.ace.bp.AceZfpms_dataUnSendApproveBP;
import nc.bs.zfpms.data.ace.bp.AceZfpms_dataUpdateBP;
import nc.bs.zfpms.data.ace.bp.AceZfpms_data_BP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.zfpms.data.DataBillVO;

public abstract class AceZfpms_dataPubServiceImpl {
	// ����
	public DataBillVO[] pubinsertBills(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		try {
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<DataBillVO> transferTool = new BillTransferTool<DataBillVO>(
					clientFullVOs);
			// ����BP
			AceZfpms_dataInsertBP action = new AceZfpms_dataInsertBP();
			DataBillVO[] retvos = action.insert(clientFullVOs);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ��
	public void pubdeleteBills(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		try {
			// ����BP
			new AceZfpms_dataDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	public DataBillVO[] pubupdateBills(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<DataBillVO> transferTool = new BillTransferTool<DataBillVO>(
					clientFullVOs);
			AceZfpms_dataUpdateBP bp = new AceZfpms_dataUpdateBP();
			DataBillVO[] retvos = bp.update(clientFullVOs, originBills);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public DataBillVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		DataBillVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<DataBillVO> query = new BillLazyQuery<DataBillVO>(
					DataBillVO.class);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	/**
	 * ������ʵ�֣���ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// ��ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	}

	// �ύ
	public DataBillVO[] pubsendapprovebills(
			DataBillVO[] clientFullVOs, DataBillVO[] originBills)
			throws BusinessException {
		AceZfpms_dataSendApproveBP bp = new AceZfpms_dataSendApproveBP();
		DataBillVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// �ջ�
	public DataBillVO[] pubunsendapprovebills(
			DataBillVO[] clientFullVOs, DataBillVO[] originBills)
			throws BusinessException {
		AceZfpms_dataUnSendApproveBP bp = new AceZfpms_dataUnSendApproveBP();
		DataBillVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// ����
	public DataBillVO[] pubapprovebills(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceZfpms_dataApproveBP bp = new AceZfpms_dataApproveBP();
		DataBillVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// ����

	public DataBillVO[] pubunapprovebills(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceZfpms_dataUnApproveBP bp = new AceZfpms_dataUnApproveBP();
		DataBillVO[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}
	
	// ȡ��
	public Object qushu(HashMap<String, Object> para, Object other)
			throws BusinessException
	{
		AceZfpms_data_BP bp = new AceZfpms_data_BP();
		return bp.qushu(para, other);
	}

}