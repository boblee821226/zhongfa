package nc.bs.zfpms.data.ace.bp;

import nc.bs.zfpms.data.plugin.bpplugin.Zfpms_dataPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.zfpms.data.DataBillVO;

/**
 * 标准单据新增BP
 */
public class AceZfpms_dataInsertBP {

	public DataBillVO[] insert(DataBillVO[] bills) {

		InsertBPTemplate<DataBillVO> bp = 
				new InsertBPTemplate<DataBillVO>(Zfpms_dataPluginPoint.INSERT);
		
		String billtype = null;
		if(bills!=null && bills.length>0)
		{
			billtype = bills[0].getParentVO().getVbilltypecode();
		}
		
		this.addBeforeRule(bp.getAroundProcesser(),billtype);
		this.addAfterRule(bp.getAroundProcesser(),billtype);
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<DataBillVO> processor,String billtype) {
		IRule<DataBillVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype(billtype);
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCodeItem("vbillcode");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processor.addAfterRule(rule);
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<DataBillVO> processer,String billtype) {
		IRule<DataBillVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCbilltype(billtype);
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCodeItem("vbillcode");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem("pk_org");
		processer.addBeforeRule(rule);
	}
}
