package nc.bs.zfpms.data.ace.bp;

import nc.bs.zfpms.data.plugin.bpplugin.Zfpms_dataPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.zfpms.data.DataBillVO;

/**
 * 修改保存的BP
 * 
 */
public class AceZfpms_dataUpdateBP {

	public DataBillVO[] update(DataBillVO[] bills,
			DataBillVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<DataBillVO> bp = new UpdateBPTemplate<DataBillVO>(
				Zfpms_dataPluginPoint.UPDATE);
		
		String billtype = null;
		if(bills!=null && bills.length>0)
		{
			billtype = bills[0].getParentVO().getVbilltypecode();
		}
		
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser(),billtype);
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser(),billtype);
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<DataBillVO> processer,String billtype) {
		IRule<DataBillVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype(billtype);
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCodeItem("vbillcode");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processer.addAfterRule(rule);

	}

	private void addBeforeRule(CompareAroundProcesser<DataBillVO> processer,String billtype) {
		IRule<DataBillVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
		nc.impl.pubapp.pattern.rule.ICompareRule<DataBillVO> ruleCom = new nc.bs.pubapp.pub.rule.UpdateBillCodeRule();
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setCbilltype(billtype);
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setCodeItem("vbillcode");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setOrgItem("pk_org");
		processer.addBeforeRule(ruleCom);
	}

}
