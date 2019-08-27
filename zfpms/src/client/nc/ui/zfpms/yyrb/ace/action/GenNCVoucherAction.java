package nc.ui.zfpms.yyrb.ace.action;


import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.zfpms.IZfpms_dataMaintain;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.pub.BusinessException;
import nc.vo.zfpms.data.DataBillVO;

/**
 * 生成凭证
 */
public class GenNCVoucherAction extends NCAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4591232971481831556L;

	public GenNCVoucherAction() {
		setBtnName("生成凭证");
		setCode("genNCVoucherAction");
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
		
		DataBillVO billVO = (DataBillVO) getModel().getSelectedData();
		
		if (billVO == null) {
			throw new BusinessException("请选中数据!");
		}
		
//		Object VDEF09 = billVO.getParentVO().getAttributeValue("linshi");	// 临时凭证
//		Object VDEF10 = billVO.getParentVO().getAttributeValue("zhengshi");	// 正式凭证
//		if( PuPubVO.getString_TrimZeroLenAsNull(VDEF09)!=null 
//		 || PuPubVO.getString_TrimZeroLenAsNull(VDEF10)!=null 
//		)
//		{
//			throw new BusinessException("已经生成了凭证，不能再次生成。");
//		}
		
		String pznum = "";

		pznum = getItf().genNCVoucherInfo(billVO,0);			// 返回凭证号，（ 临时凭证 没有凭证号）
		
//		billVO.getParentVO().setAttributeValue("linshi", pznum);	// 将返回的凭证号  放到 表头自定义项9
		
		this.setEnabled(false);
		
		// 提示信息
		ShowStatusBarMsgUtil.showStatusBarMsg("生成完毕!", getEditor().getModel()
				.getContext());
	}

	private IZfpms_dataMaintain _ITF = null;
	private IZfpms_dataMaintain getItf() {
		if (_ITF == null) {
			_ITF = NCLocator.getInstance().lookup(
					IZfpms_dataMaintain.class);
		}
		return _ITF;
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
