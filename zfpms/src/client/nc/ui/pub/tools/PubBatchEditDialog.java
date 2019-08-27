package nc.ui.pub.tools;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.vo.pub.tools.PuPubVO;

/***
 * 
 * <b> 小输入框 </b>
 * <p>
 *     功能:小输入框
 * </p>
 * @作者  张帅
 * @创建日期 2013-5-13
 * @项目名称 buap
 * @JAVA路径 hd.ui.hl.pub.tools.PubBatchEditDialog
 */
public class PubBatchEditDialog extends UIDialog {

	/**
	 * long
	 * serialVersionUID
	 * 2013-5-13
	 */
	private static final long serialVersionUID = 1L;

	private UIButton btnOK = null;// 确定按钮

	private UIButton btnCancel = null;// 取消按钮

	private BillItem[] m_items;

	private UILabel m_lbTitle;

	private BillCardPanel m_panel;

	private Container m_contentPane;

	public PubBatchEditDialog(Container parent, BillItem item) {
		super(parent);
		this.m_items = new BillItem[1];
		this.m_items[0] = item;
		initializeItem();
	}
	
	public PubBatchEditDialog(Container parent, BillItem[] items){
		super(parent);
		this.m_items = items ;
		initializeItem();
	}

	private void initializeItem() {
		m_lbTitle = new UILabel();
		m_lbTitle.setText(this.getTitle());
		m_lbTitle.setPreferredSize(new Dimension(100, 23));
		getBtnOK();
		getBtnCancel();

		this.m_panel = new BillCardPanel();
		this.m_panel.setPreferredSize(new Dimension(285, 210));
		this.m_panel.setBillData(getBillData());

		setSize(new Dimension(291, 275));
		setLocation(500, 400);
		setResizable(false);

		this.m_contentPane = getContentPane();
		this.m_contentPane.setLayout(new FlowLayout());
		//this.m_contentPane.add(this.m_lbTitle);
		this.m_contentPane.add(this.m_panel);
		this.m_contentPane.add(this.btnOK);
		this.m_contentPane.add(this.btnCancel);
	}

	private BillData getBillData() {
		BillData data = new BillData();
		data.setHeadItems(this.m_items);
		data.setEnabled(true);
		return data;
	}

	/**
	 * 返回确定按钮
	 * 
	 * @return UIButton
	 */
	private UIButton getBtnOK() {
		if (btnOK == null) {
			try {
				btnOK = new UIButton();
				btnOK.setName("btnOK");
				btnOK.setText("确定");
				btnOK.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(java.awt.event.ActionEvent e) {
						onBtnOK();
					}
				});
			} catch (java.lang.Throwable e) {
				handleException(e);
			}
		}
		return btnOK;
	}

	/**
	 * 返回取消按钮
	 * 
	 * @return UIButton
	 */
	private UIButton getBtnCancel() {
		if (btnCancel == null) {
			try {
				btnCancel = new UIButton();
				btnCancel.setName("btnCancel");
				btnCancel.setText("取消");
				btnCancel
						.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								onBtnCancel();
							}
						});
			} catch (java.lang.Throwable e) {
				handleException(e);
			}
		}
		return btnCancel;
	}

	/**
	 * 异常处理
	 * 
	 * @param exception
	 */
	private void handleException(java.lang.Throwable exception) {
		nc.ui.pub.beans.MessageDialog.showErrorDlg(this,
				NCLangRes.getInstance().getStrByID("6017010404",
						"UPP6017010404-000024")/* @res "错误" */,
				nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010707",
						"UPP6017010707-000045")/* @res "系统出现异常，操作失败！" */
						+ exception.getMessage());
		exception.printStackTrace();
	}

	/**
	 * 确定按钮动作处理方法
	 */
	private void onBtnOK() {
		
		/**
		 * 判断 必输项不能为空
		 */
		String errorMsg = "";
		for(BillItem item : m_items)
		{
			
			if( item.isNull() && PuPubVO.getString_TrimZeroLenAsNull(item.getValueObject())==null )
			{// 如果是必输项 并且值为空
				errorMsg += item.getName()+"，";
			}
		}
		if(errorMsg.length()>0)
		{
			MessageDialog.showErrorDlg(this, "", "以下字段不能为空：\r\n"+errorMsg);
			return;
		}
		/***END***/
		
		this.closeOK();
		this.clearPanel();
	}

	/**
	 * 取消按钮动作处理方法
	 */
	private void onBtnCancel() {
		this.closeCancel();
		this.clearPanel();
	}

	/**
	 * 清除画面信息
	 */
	private void clearPanel() {

	}

}
