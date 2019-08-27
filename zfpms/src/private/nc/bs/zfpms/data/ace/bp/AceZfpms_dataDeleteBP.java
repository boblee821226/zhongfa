package nc.bs.zfpms.data.ace.bp;

import nc.bs.zfpms.data.plugin.bpplugin.Zfpms_dataPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.zfpms.data.DataBillVO;


/**
 * 标准单据删除BP
 */
public class AceZfpms_dataDeleteBP {

	public void delete(DataBillVO[] bills) {

		DeleteBPTemplate<DataBillVO> bp = new DeleteBPTemplate<DataBillVO>(
				Zfpms_dataPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<DataBillVO> processer) {
		// TODO 前规则
		IRule<DataBillVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<DataBillVO> processer) {
		// TODO 后规则

	}
}
