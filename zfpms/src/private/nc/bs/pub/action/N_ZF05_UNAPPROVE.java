package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UnapproveStatusCheckRule;
import nc.bs.zfpms.data.plugin.bpplugin.Zfpms_dataPluginPoint;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.zfpms.IZfpms_dataMaintain;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.zfpms.data.DataBillVO;

public class N_ZF05_UNAPPROVE extends AbstractPfAction<DataBillVO> {

	@Override
	protected CompareAroundProcesser<DataBillVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<DataBillVO> processor = new CompareAroundProcesser<DataBillVO>(
				Zfpms_dataPluginPoint.UNAPPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UnapproveStatusCheckRule());

		return processor;
	}

	@Override
	protected DataBillVO[] processBP(Object userObj,
			DataBillVO[] clientFullVOs, DataBillVO[] originBills) {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		DataBillVO[] bills = null;
		try {
			IZfpms_dataMaintain operator = NCLocator.getInstance()
					.lookup(IZfpms_dataMaintain.class);
			bills = operator.unapprove(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
