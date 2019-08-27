package nc.bs.zfpms.data.ace.bp;

import nc.bs.zfpms.data.plugin.bpplugin.Zfpms_dataPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.zfpms.data.DataBillVO;


/**
 * ��׼����ɾ��BP
 */
public class AceZfpms_dataDeleteBP {

	public void delete(DataBillVO[] bills) {

		DeleteBPTemplate<DataBillVO> bp = new DeleteBPTemplate<DataBillVO>(
				Zfpms_dataPluginPoint.DELETE);
		// ����ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<DataBillVO> processer) {
		// TODO ǰ����
		IRule<DataBillVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<DataBillVO> processer) {
		// TODO �����

	}
}
