package nc.ui.zfpms.yyrb.ace.action;

import java.awt.event.ActionEvent;

import nc.ui.pub.link.FipBillLinkQueryCenter;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.NCAction;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.tools.PuPubVO;
import nc.vo.zfpms.data.DataBillVO;

/**
 * 联查凭证
 */
public class LinkNCVoucherAction extends NCAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6739253981231809862L;

	public LinkNCVoucherAction() {
		setBtnName("联查凭证");
		setCode("linkNCVoucherAction");
	}

	private BillManageModel model;
	private ShowUpableBillForm editor;

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public ShowUpableBillForm getEditor() {
		return editor;
	}

	public void setEditor(ShowUpableBillForm editor) {
		this.editor = editor;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		
		AggregatedValueObject aggVO = null;
		aggVO = (AggregatedValueObject) getModel().getSelectedData();

		CircularlyAccessibleValueObject hvo = aggVO.getParentVO();
		String pk = hvo.getPrimaryKey();
		String billType = PuPubVO.getString_TrimZeroLenAsNull(hvo.getAttributeValue("vbilltypecode"));
		
		FipRelationInfoVO infovo = new FipRelationInfoVO();
		infovo.setPk_billtype(billType);
		infovo.setRelationID(pk);
		
		FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(
				 getModel().getContext().getEntranceUI()
				,infovo);
	}

	@Override
	protected boolean isActionEnable() {
		DataBillVO billVO = (DataBillVO) this.getModel().getSelectedData();
		if (billVO == null) {
			return false;
		} else {
			String pk = billVO.getParentVO().getPrimaryKey();
			Integer ibillstatus = billVO.getParentVO().getIbillstatus();
			if (pk == null || "".equals(pk)||ibillstatus!=1) {
				return false;
			} else {
				return true;
			}
		}
	}
}
