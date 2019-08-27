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
	// 新增
	public DataBillVO[] pubinsertBills(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<DataBillVO> transferTool = new BillTransferTool<DataBillVO>(
					clientFullVOs);
			// 调用BP
			AceZfpms_dataInsertBP action = new AceZfpms_dataInsertBP();
			DataBillVO[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AceZfpms_dataDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public DataBillVO[] pubupdateBills(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<DataBillVO> transferTool = new BillTransferTool<DataBillVO>(
					clientFullVOs);
			AceZfpms_dataUpdateBP bp = new AceZfpms_dataUpdateBP();
			DataBillVO[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
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
	 * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

	// 提交
	public DataBillVO[] pubsendapprovebills(
			DataBillVO[] clientFullVOs, DataBillVO[] originBills)
			throws BusinessException {
		AceZfpms_dataSendApproveBP bp = new AceZfpms_dataSendApproveBP();
		DataBillVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public DataBillVO[] pubunsendapprovebills(
			DataBillVO[] clientFullVOs, DataBillVO[] originBills)
			throws BusinessException {
		AceZfpms_dataUnSendApproveBP bp = new AceZfpms_dataUnSendApproveBP();
		DataBillVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public DataBillVO[] pubapprovebills(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceZfpms_dataApproveBP bp = new AceZfpms_dataApproveBP();
		DataBillVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

	public DataBillVO[] pubunapprovebills(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceZfpms_dataUnApproveBP bp = new AceZfpms_dataUnApproveBP();
		DataBillVO[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}
	
	// 取数
	public Object qushu(HashMap<String, Object> para, Object other)
			throws BusinessException
	{
		AceZfpms_data_BP bp = new AceZfpms_data_BP();
		return bp.qushu(para, other);
	}

}