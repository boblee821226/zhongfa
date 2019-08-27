package nc.vo.zfpms.data;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class DataBillVOMeta extends AbstractBillMeta{
	
	public DataBillVOMeta(){
		this.init();
	}
	
	private void init() {
		this.setParent(nc.vo.zfpms.data.DataHVO.class);
		this.addChildren(nc.vo.zfpms.data.DataBVO.class);
		this.addChildren(nc.vo.zfpms.data.DataCVO.class);
		this.addChildren(nc.vo.zfpms.data.DataDVO.class);
	}
}