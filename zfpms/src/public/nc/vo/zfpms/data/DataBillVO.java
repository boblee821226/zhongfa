package nc.vo.zfpms.data;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.zfpms.data.DataHVO")
public class DataBillVO extends AbstractBill {

  @Override
  public IBillMeta getMetaData() {
    IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(DataBillVOMeta.class);
    return billMeta;
  }

  @Override
  public DataHVO getParentVO() {
    return (DataHVO) this.getParent();
  }
}