package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.zfpms.data.plugin.bpplugin.Zfpms_dataPluginPoint;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.zfpms.IZfpms_Tool;
import nc.itf.zfpms.IZfpms_dataMaintain;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.zfpms.data.DataBillVO;
import nc.vo.zfpms.data.DataHVO;

public class N_ZF02_SAVEBASE extends AbstractPfAction<DataBillVO> {

	@Override
	protected CompareAroundProcesser<DataBillVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<DataBillVO> processor = null;
		DataBillVO[] clientFullVOs = (DataBillVO[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<DataBillVO>(
					Zfpms_dataPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<DataBillVO>(
					Zfpms_dataPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		IRule<DataBillVO> rule = null;

		return processor;
	}

	@Override
	protected DataBillVO[] processBP(Object userObj,
			DataBillVO[] clientFullVOs, DataBillVO[] originBills) {

		DataBillVO[] bills = null;
		try {
			
			/**
			 * 赋值单据类型 ZF02
			 */
			for(DataBillVO billVO : clientFullVOs){
				DataHVO hVO = billVO.getParentVO();
				hVO.setVbilltypecode(IZfpms_Tool.ZF02);
			}
			/***END***/
			
			IZfpms_dataMaintain operator = NCLocator.getInstance()
					.lookup(IZfpms_dataMaintain.class);
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
				bills = operator.update(clientFullVOs, originBills);
			} else {
				bills = operator.insert(clientFullVOs, originBills);
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}
}
