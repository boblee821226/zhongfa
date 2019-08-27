package nc.ui.zfpms.yyrb.ace.action;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.itf.zfpms.IZfpms_Tool;
import nc.itf.zfpms.IZfpms_dataMaintain;
import nc.pubitf.setting.defaultdata.OrgSettingAccessor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.tools.PubBatchEditDialog;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.pubapp.uif2app.view.ShowUpableBillListView;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.tools.PuPubVO;
import nc.ui.pub.tools.BannerDialog;

public class QuShuAction extends NCAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4092782467547441868L;
	
	public QuShuAction() {
		setBtnName("ȡ��");
		setCode("qushuAction");
	}
	private BillManageModel model;
	private ShowUpableBillForm editor;
	private ShowUpableBillListView listview;
	private Integer dataType;
	
	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public ShowUpableBillListView getListview() {
		return listview;
	}

	public void setListview(ShowUpableBillListView listview) {
		this.listview = listview;
	}

	public ShowUpableBillForm getEditor() {
		return editor;
	}

	public void setEditor(ShowUpableBillForm editor) {
		this.editor = editor;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
	
	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		
		UFDate yesterday = new UFDate().getDateBefore(1);
		String yesterday_str = yesterday.toString().substring(0,10);
		
		// ��֯
		BillItem zuzhi_item = new BillItem();
		zuzhi_item.setName("��֯");
		zuzhi_item.setKey("zuzhi");
		zuzhi_item.setDataType(IBillItem.UFREF);
		zuzhi_item.setRefType("������֯");
		zuzhi_item.setEdit(true);
		zuzhi_item.setValue( OrgSettingAccessor.getDefaultOrgUnit() );	// Ĭ��ֵ
		zuzhi_item.setNull(true);	// �Ƿ�ǿ�  false ���Ƿǿ�
		UIRefPane zuzhiRef = (UIRefPane)zuzhi_item.getComponent();
		zuzhiRef.setWhereString(" pk_financeorg in ('"+IZfpms_Tool.CORP_CHANGTAN+"','"+IZfpms_Tool.CORP_MANILA+"') ");
		// ��ʼ����
		BillItem ksrq_item = new BillItem();
		ksrq_item.setName("��ʼ����");
		ksrq_item.setKey("ksrq");
		ksrq_item.setDataType(IBillItem.UFREF);
		ksrq_item.setRefType("����");
		ksrq_item.setEdit(true);
		ksrq_item.setValue(yesterday_str);		// Ĭ��ֵ
		ksrq_item.setNull(true);	// �Ƿ�ǿ�  false ���Ƿǿ�
		// ��������
		BillItem jsrq_item = new BillItem();
		jsrq_item.setName("��������");
		jsrq_item.setKey("jsrq");
		jsrq_item.setDataType(IBillItem.UFREF);
		jsrq_item.setRefType("����");
		jsrq_item.setEdit(true);
		jsrq_item.setValue(yesterday_str);		// Ĭ��ֵ
		jsrq_item.setNull(true);	// �Ƿ�ǿ�  false ���Ƿǿ�
		// �������� ��ȫ����Ӫҵ�ձ�����
		BillItem data_type = new BillItem();
		data_type.setName("��������");
		data_type.setKey("dataType");
		data_type.setDataType(IBillItem.COMBO);
		data_type.setRefType("IX,ȫ��,�����տ���ϸ��,�ֽ���ܱ�,ǰ̨������ϸ,Ӫҵ�ձ�,���˵ױ�,AR�տ���ϸ");
		data_type.setEdit(true);
		data_type.setNull(true);	// �Ƿ�ǿ�  false ���Ƿǿ�
		data_type.setValue(this.getDataType());	// Ĭ��ֵ

		PubBatchEditDialog dlg = new PubBatchEditDialog(
				 this.getListview()
				,new BillItem[]{
					 zuzhi_item,
					 ksrq_item,
					 jsrq_item,
					 data_type,
				});
		dlg.setTitle("ѡ��");
		
		if( UIDialog.ID_OK != dlg.showModal()) return;
		
		String pk_org = PuPubVO.getString_TrimZeroLenAsNull(zuzhi_item.getValueObject());
		String ksrq  = PuPubVO.getString_TrimZeroLenAsNull(ksrq_item.getValueObject());
		String jsrq  = PuPubVO.getString_TrimZeroLenAsNull(jsrq_item.getValueObject());
		Integer dataType = PuPubVO.getInteger_NullAs(data_type.getValueObject(),0);
		
		final HashMap<String,Object> para = new HashMap<String,Object>();
		para.put("pk_orgs",new String[]{pk_org});
		para.put("ksrq",ksrq);
		para.put("jsrq",jsrq);
		para.put("dataType",dataType);
		
		Runnable checkRun =new Runnable(){
	        public void run()
	        {
		        //�̶߳Ի���ϵͳ������ʾ��
	            BannerDialog dialog = new BannerDialog(getListview());
	            String message="";
	            dialog.start();
	            
	            int resultNum = 0;
	            
	            try
	            {
					IZfpms_dataMaintain itf = NCLocator.getInstance().lookup(IZfpms_dataMaintain.class);
					Object[] result = (Object[])itf.qushu(para, null);
					resultNum = (Integer)result[1];
				
		        } catch(Exception e) {
	                e.printStackTrace();
	            } finally {
	            	// ����ϵͳ������ʾ��
	                dialog.end(); 
	                MessageDialog.showWarningDlg(getListview(), "", "������ϡ��ܹ����ɡ�"+resultNum+"���������ѯ���ݡ�");
	            }
	        }
		};
	    //�����߳�
	    new Thread(checkRun).start();

	}

}
