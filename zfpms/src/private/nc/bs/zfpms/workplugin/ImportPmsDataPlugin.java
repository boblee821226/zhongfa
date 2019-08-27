package nc.bs.zfpms.workplugin;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.core.service.TimeService;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.bs.uap.lock.PKLock;
import nc.itf.zfpms.IZfpms_Tool;
import nc.itf.zfpms.IZfpms_dataMaintain;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.tool.zhongfa.mysql.JDBCUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.tools.PuPubVO;
import nc.vo.pubapp.AppContext;
import nc.vo.zfpms.data.DataBVO;
import nc.vo.zfpms.data.DataBillVO;
import nc.vo.zfpms.data.DataCVO;
import nc.vo.zfpms.data.DataDVO;
import nc.vo.zfpms.data.DataHVO;

public class ImportPmsDataPlugin implements IBackgroundWorkPlugin {

	public static String Plugin_Key = "Plugin_ZFpms_data";			// ��̨����ı�ʶ
	
	public static HashMap<String,String> MAP_corp_db   = new HashMap<String,String>();// pk_corp  ��Ӧ  db
	public static HashMap<String,String> MAP_corp_dian = new HashMap<String,String>();// pk_corp  ��Ӧ  DIAN
	
	static
	{
		// pk_corp ��Ӧ  db
		MAP_corp_db.put(IZfpms_Tool.CORP_CHANGTAN	, JDBCUtils.Prefix_CHANGTAN);
		MAP_corp_db.put(IZfpms_Tool.CORP_MANILA		, JDBCUtils.Prefix_MANILA);
		// pk_corp ��Ӧ  DIAN
		MAP_corp_dian.put(IZfpms_Tool.CORP_CHANGTAN	, IZfpms_Tool.DIAN_CHANGTAN);
		MAP_corp_dian.put(IZfpms_Tool.CORP_MANILA	, IZfpms_Tool.DIAN_MANILA);
	}
	
	@Override
	public PreAlertObject executeTask(BgWorkingContext context)
			throws BusinessException {
		
		long startTime = System.currentTimeMillis();
		
		// ����
		boolean lock=PKLock.getInstance().addBatchDynamicLock(new String[]{context.getAlertTypeName()});
		if(!lock){
			throw new BusinessException("�������ڴ�����,�����ظ�������");
		}
		
		try
		{
			String[] pk_orgs = context.getPk_orgs();//��֯Ϊ���䣬���Կ϶���ֵ����ѡ������ ��ѡ��
			UFBoolean isYyrb 	= PuPubVO.getUFBoolean_NullAs(context.getKeyMap().get("isYyrb"),UFBoolean.FALSE);		// ͬ�� Ӫҵ�ձ���
			UFBoolean isJhdb 	= PuPubVO.getUFBoolean_NullAs(context.getKeyMap().get("isJhdb"),UFBoolean.FALSE);		// ͬ�� ���˵ױ�
			UFBoolean isYsyuehzb= PuPubVO.getUFBoolean_NullAs(context.getKeyMap().get("isYsyuehzb"),UFBoolean.FALSE);	// ͬ�� Ӧ�������ܱ�
			UFBoolean isQtskhzb = PuPubVO.getUFBoolean_NullAs(context.getKeyMap().get("isQtskhzb"),UFBoolean.FALSE);	// ͬ�� ǰ̨�տ���ܱ�
			UFBoolean isMxrkqjb = PuPubVO.getUFBoolean_NullAs(context.getKeyMap().get("isMxrkqjb"),UFBoolean.FALSE);	// ͬ�� ��ϸ��������

			UFDate bdate = PuPubVO.getUFDate( context.getKeyMap().get("bdate") );	// ��ʼ����
			UFDate edate = PuPubVO.getUFDate( context.getKeyMap().get("edate") );	// ��������
			
			// �������Ϊ�գ� ��Ĭ��Ϊ ��ǰ���ڵ� ǰһ��
			if(bdate==null) bdate = new UFDate().getDateBefore(1);
			if(edate==null) edate = new UFDate().getDateBefore(1);
			
			if(pk_orgs==null || pk_orgs.length<=0)
			{
				pk_orgs = new String[]{
						 IZfpms_Tool.CORP_CHANGTAN	// ��̲
						,IZfpms_Tool.CORP_MANILA	// ������
				};
			}
			
			ArrayList<String> list_date = this.getTimeDates(bdate,edate);
			
			if( isYyrb.booleanValue() )
			{// Ӫҵ�ձ�
				importYyrb(pk_orgs,list_date);	
			}
			
			if( isJhdb.booleanValue() )
			{// ���˵ױ�
				importJhdb(pk_orgs,list_date);
			}

			if( isQtskhzb.booleanValue() )
			{// �ֽ���ܱ�
				importQtskhzb(pk_orgs,list_date);
			}
//			
//			if( isMxrkqjb.booleanValue() )
//			{// ��ϸ��������
//				importMxrkqjb(pk_orgs,list_date);
//			}
			
		}catch(Exception ex)
		{
			throw new BusinessException(ex);
		}
		
		System.out.println("�������,����ʱ��"+(System.currentTimeMillis()-startTime)+"����");
	
		return null;
	}
	
	/**
	 * Ӫҵ�ձ���ZF01��
	 */
	private Object importYyrb(String[] pk_orgs,ArrayList<String> list_date) throws Exception
	{
		String BILL_NAME = "Ӫҵ�ձ�";
		String BILL_CODE = IZfpms_Tool.ZF01;
		Connection zfPms_conn=null;
		JdbcSession zfPms_session =null;
		
		BaseDAO dao = new BaseDAO();
		
		Integer resultNum = 0;	// ���سɹ�������
		String resultMsg = "";	// ���ص���Ϣ
		
		/** 
		 * ��ÿ���ҵ�����ݣ���˫��ѭ��
		 * ��ѭ��  ��  ��
		 * ��ѭ��  ��  ��
		 */
		for(int org_i=0;org_i<pk_orgs.length;org_i++)
		{// ����  �� ѭ������
			
			String pk_org 	 = pk_orgs[org_i];				// pk_corp
			String db_Prefix = MAP_corp_db.get(pk_org);		// ����Դ ǰ׺
			String dian		 = MAP_corp_dian.get(pk_org);	// dian
			
			if(db_Prefix==null) continue;	// ���û������  ������
			
			zfPms_conn=new JDBCUtils(db_Prefix).getConn(null);
			zfPms_session = new JdbcSession(zfPms_conn);
		
			for (String yw_date : list_date) 
			{// ���� �� ѭ������
				
				String vbillno = BILL_NAME + "-" + dian + "-" + yw_date;
				
				// �����ж� NC�Ƿ��Ѿ����ڵ��ݣ����� ��֯ �� ���ڣ�
				// ������� ������
				{
					String checkSQL = 
						" SELECT " +
						" pk_zfpms_data " +
						" from zfpms_data  " +
						" where dr=0" +
						" and vbilltypecode = '"+BILL_CODE+"' " +
						" and pk_org = '"+(pk_org)+"' " +
						" and substr(dbilldate,1,10) = '"+(yw_date)+"' "
					;
					
					Object check_pk = getBaseDAO().executeQuery(checkSQL.toString(),new ColumnProcessor());
					
					if(check_pk!=null) continue;	// ���������  ��������
					
				}
				
				// �� �ӿڱ���ȡ��
				String querySQL = 
						" SELECT " +
						" list_order 		AS vrowno " + 	// 0��
						",hotel_group_id 	AS vbdef06 " + 	// 1��
						",hotel_id 			AS vbdef07 " + 	// 2��
						",id 				AS vbdef01 " + 	// 3��
						",CODE 				AS vbdef02 " + 	// 4��
						",descript 			AS vbdef03 " + 	// 5��
						",descript_en 		AS vbdef04 " +  // 6��
						",DAY 				AS bdata01 " + 	// 7��
						",MONTH 			AS bdata02 " + 	// 8��
						",YEAR 				AS bdata03 " + 	// 9��
						",rebate_day 		AS bdata04 " + 	// 10��
						",rebate_month 		AS bdata05 " + 	// 11��
						",rebate_year 		AS bdata06 " + 	// 12��
						",tax_day 			AS bdata07 " + 	// 13��
						",tax_month 		AS bdata08 " + 	// 14��
						",tax_year 			AS bdata09 " + 	// 15��
						",is_show 			AS vbdef05 " +	// 16��
						" FROM rep_jour_history " + 
						" WHERE (1=1) " +
						" and SUBSTR(biz_date,1,10) = '"+yw_date+"' " +
						" ORDER BY list_order ";
				
				ArrayList<DataBVO> list = (ArrayList<DataBVO>)zfPms_session.executeQuery(querySQL, new BeanListProcessor(DataBVO.class));
				
				if(list==null||list.size()<=0) continue;	// �ӿ�����Ϊ0��������
				
				DataBillVO billVO = new DataBillVO();
				DataHVO   hVO  = new DataHVO();
				DataBVO[] bVOs = new DataBVO[list.size()];
				bVOs = list.toArray(bVOs);
				billVO.setParent(hVO);
				billVO.setChildrenVO(bVOs);
				
				//��װ��ͷVO
				hVO.setVbilltypecode( BILL_CODE );							// ��������
				hVO.setPk_group( AppContext.getInstance().getPkGroup() );	//����
				hVO.setPk_org( pk_org );		// pk_org
				hVO.setVbillcode( vbillno );	// ���ݺ�
				hVO.setIbillstatus(-1);			// ����״̬
				hVO.setCreator( AppContext.getInstance().getPkUser() );		// �Ƶ���
				hVO.setCreationtime( new UFDateTime() );					// �Ƶ�����
				hVO.setDirty( false );						// �Ƿ�ɾ��
				hVO.setDbilldate( new UFDate(yw_date) );	// ��������
				//��װ����VO
				for(int i=0;bVOs!=null&&i<bVOs.length;i++)
				{
					bVOs[i].setDirty(false);
				}
				
				IZfpms_dataMaintain itf = NCLocator.getInstance().lookup(IZfpms_dataMaintain.class);
				DataBillVO[] result = itf.insert(new DataBillVO[]{billVO}, null);
				resultNum += (result==null?0:result.length);
			}
		}
		
		return new Object[]{
				resultMsg,
				resultNum,
		};
	}
	
	/**
	 * ���˵ױ�ZF02��
	 */
	private Object importJhdb(String[] pk_orgs,ArrayList<String> list_date) throws Exception
	{
		String BILL_NAME = "���˵ױ�";
		String BILL_CODE = IZfpms_Tool.ZF02;
		Connection zfPms_conn=null;
		JdbcSession zfPms_session =null;
		
		BaseDAO dao = new BaseDAO();
		
		Integer resultNum = 0;	// ���سɹ�������
		String resultMsg = "";	// ���ص���Ϣ
		
		/** 
		 * ��ÿ���ҵ�����ݣ���˫��ѭ��
		 * ��ѭ��  ��  ��
		 * ��ѭ��  ��  ��
		 */
		for(int org_i=0;org_i<pk_orgs.length;org_i++)
		{// ����  �� ѭ������
			
			String pk_org 	 = pk_orgs[org_i];				// pk_corp
			String db_Prefix = MAP_corp_db.get(pk_org);		// ����Դ ǰ׺
			String dian		 = MAP_corp_dian.get(pk_org);	// dian
			
			if(db_Prefix==null) continue;	// ���û������  ������
			
			zfPms_conn=new JDBCUtils(db_Prefix).getConn(null);
			zfPms_session = new JdbcSession(zfPms_conn);
		
			for (String yw_date : list_date) 
			{// ���� �� ѭ������
				
				String vbillno = BILL_NAME + "-" + dian + "-" + yw_date;
				
				// �����ж� NC�Ƿ��Ѿ����ڵ��ݣ����� ��֯ �� ���ڣ�
				// ������� ������
				{
					String checkSQL = 
						" SELECT " +
						" pk_zfpms_data " +
						" from zfpms_data  " +
						" where dr=0" +
						" and vbilltypecode = '"+BILL_CODE+"' " +
						" and pk_org = '"+(pk_org)+"' " +
						" and substr(dbilldate,1,10) = '"+(yw_date)+"' "
					;
					
					Object check_pk = getBaseDAO().executeQuery(checkSQL.toString(),new ColumnProcessor());
					
					if(check_pk!=null) continue;	// ���������  ��������
					
				}
				
				// �� �ӿڱ���ȡ��(1����)
				String querySQL_b = 
						" SELECT " +
//						" list_order 		AS vrowno " + 	// 0��
						" hotel_group_id 	AS vbdef06 " + 	// 1��
						",hotel_id 			AS vbdef07 " + 	// 2��
						",id 				AS vbdef01 " + 	// 3��
						",classno 			AS vbdef02 " + 	// 4��
						",descript 			AS vbdef03 " + 	// 5��
						",day01 			AS bdata01 " + 	// 6��
						",day02 			AS bdata02 " + 	// 7��
						",day03 			AS bdata03 " + 	// 8��
						",day04	 			AS bdata04 " + 	// 9��
						",day05		 		AS bdata05 " + 	// 10��
						",day06 			AS bdata06 " + 	// 11��
						",day07 			AS bdata07 " + 	// 12��
						",day08	 			AS bdata08 " + 	// 13��
						",day09 			AS bdata09 " + 	// 14��
						",day99 			AS bdata10 " + 	// 15��
						",month01 			AS bdata11 " + 	// 16��
						",month02 			AS bdata12 " + 	// 17��
						",month03 			AS bdata13 " + 	// 18��
						",month04 			AS bdata14 " + 	// 19��
						",month05 			AS bdata15 " + 	// 20��
						",month06 			AS bdata16 " + 	// 21��
						",month07 			AS bdata17 " + 	// 22��
						",month08 			AS bdata18 " + 	// 23��
						",month09 			AS bdata19 " + 	// 24��
						",month99 			AS bdata20 " + 	// 25��
						" FROM rep_jie_history " + 
						" WHERE (1=1) " +
						" and SUBSTR(biz_date,1,10) = '"+yw_date+"' " +
						" ORDER BY id ";
				
				ArrayList<DataBVO> list_b = (ArrayList<DataBVO>)zfPms_session.executeQuery(querySQL_b, new BeanListProcessor(DataBVO.class));
				
				// �� �ӿڱ���ȡ��(2����)
				String querySQL_c = 
						" SELECT " +
//						" list_order 		AS vrowno " + 	// 0��
						" hotel_group_id 	AS vcdef06 " + 	// 1��
						",hotel_id 			AS vcdef07 " + 	// 2��
						",id 				AS vcdef01 " + 	// 3��
						",classno 			AS vcdef02 " + 	// 4��
						",descript 			AS vcdef03 " + 	// 5��
						",credit01 			AS cdata01 " + 	// 6��
						",credit02 			AS cdata02 " + 	// 7��
						",credit03 			AS cdata03 " + 	// 8��
						",credit04	 		AS cdata04 " + 	// 9��
						",credit05		 	AS cdata05 " + 	// 10��
						",credit06 			AS cdata06 " + 	// 11��
						",credit07 			AS cdata07 " + 	// 12��
						",sumcre	 		AS cdata08 " + 	// 13��
						",last_bl 			AS cdata09 " + 	// 14��
						",debit 			AS cdata10 " + 	// 15��
						",credit 			AS cdata11 " + 	// 16��
						",till_bl 			AS cdata12 " + 	// 17��
						",credit01m 		AS cdata13 " + 	// 18��
						",credit02m 		AS cdata14 " + 	// 19��
						",credit03m 		AS cdata15 " + 	// 20��
						",credit04m 		AS cdata16 " + 	// 21��
						",credit05m 		AS cdata17 " + 	// 22��
						",credit06m 		AS cdata18 " + 	// 23��
						",credit07m 		AS cdata19 " + 	// 24��
						",sumcrem 			AS cdata20 " + 	// 25��
						",last_blm 			AS vcdef17 " + 	// 26��
						",debitm 			AS vcdef18 " + 	// 27��
						",creditm 			AS vcdef19 " + 	// 28��
						",till_blm 			AS vcdef20 " + 	// 29��
						" FROM rep_dai_history " + 
						" WHERE (1=1) " +
						" and SUBSTR(biz_date,1,10) = '"+yw_date+"' " +
						" ORDER BY id ";
				
				ArrayList<DataCVO> list_c = (ArrayList<DataCVO>)zfPms_session.executeQuery(querySQL_c, new BeanListProcessor(DataCVO.class));

				// �� �ӿڱ���ȡ��(3�����)
				String querySQL_d = 
						" SELECT " +
//						" list_order 		AS vrowno " + 	// 0��
						" hotel_group_id 	AS vddef06 " + 	// 1��
						",hotel_id 			AS vddef07 " + 	// 2��
						",id 				AS vddef01 " + 	// 3��
						",classno 			AS vddef02 " + 	// 4��
						",descript 			AS vddef03 " + 	// 5��
						",last_charge 		AS ddata01 " + 	// 6��
						",last_credit 		AS ddata02 " + 	// 7��
						",charge 			AS ddata03 " + 	// 8��
						",credit	 		AS ddata04 " + 	// 9��
						",till_charge		AS ddata05 " + 	// 10��
						",till_credit 		AS ddata06 " + 	// 11��
						",last_chargem 		AS ddata07 " + 	// 12��
						",last_creditm	 	AS ddata08 " + 	// 13��
						",chargem 			AS ddata09 " + 	// 14��
						",creditm 			AS ddata10 " + 	// 15��
						",till_chargem 		AS ddata11 " + 	// 16��
						",till_creditm 		AS ddata12 " + 	// 17��
						" FROM rep_jiedai_history " + 
						" WHERE (1=1) " +
						" and SUBSTR(biz_date,1,10) = '"+yw_date+"' " +
						" ORDER BY id ";
				
				ArrayList<DataDVO> list_d = (ArrayList<DataDVO>)zfPms_session.executeQuery(querySQL_d, new BeanListProcessor(DataDVO.class));
				
				
				if(
					(list_b==null||list_b.size()<=0)
				&&	(list_c==null||list_c.size()<=0)
				&&	(list_d==null||list_d.size()<=0)
				){
					continue;	// �ӿ�����Ϊ0��������
				}
				
				DataBillVO billVO = new DataBillVO();
				DataHVO   hVO  = new DataHVO();
				billVO.setParent(hVO);
				DataBVO[] bVOs = new DataBVO[list_b.size()];
				bVOs = list_b.toArray(bVOs);
				billVO.setChildren(billVO.getMetaData().getChildren()[0], bVOs);
				DataCVO[] cVOs = new DataCVO[list_c.size()];
				cVOs = list_c.toArray(cVOs);
				billVO.setChildren(billVO.getMetaData().getChildren()[1], cVOs);
				DataDVO[] dVOs = new DataDVO[list_d.size()];
				dVOs = list_d.toArray(dVOs);
				billVO.setChildren(billVO.getMetaData().getChildren()[2], dVOs);
				
				//��װ��ͷVO
				hVO.setVbilltypecode( BILL_CODE );							// ��������
				hVO.setPk_group( AppContext.getInstance().getPkGroup() );	//����
				hVO.setPk_org( pk_org );		// pk_org
				hVO.setVbillcode( vbillno );	// ���ݺ�
				hVO.setIbillstatus(-1);			// ����״̬
				hVO.setCreator( AppContext.getInstance().getPkUser() );		// �Ƶ���
				hVO.setCreationtime( new UFDateTime() );					// �Ƶ�����
				hVO.setDirty( false );						// �Ƿ�ɾ��
				hVO.setDbilldate( new UFDate(yw_date) );	// ��������
				//��װ����VO
				for(int i=0;bVOs!=null&&i<bVOs.length;i++)
				{
					bVOs[i].setVrowno(""+(i+1));
					bVOs[i].setDirty(false);
				}
				for(int i=0;cVOs!=null&&i<cVOs.length;i++)
				{
					cVOs[i].setAttributeValue( "vrowno", ""+(i+1) );
					cVOs[i].setDirty(false);
				}
				for(int i=0;dVOs!=null&&i<dVOs.length;i++)
				{
					dVOs[i].setAttributeValue( "vrowno", ""+(i+1) );
					dVOs[i].setDirty(false);
				}
				
				IZfpms_dataMaintain itf = NCLocator.getInstance().lookup(IZfpms_dataMaintain.class);
				DataBillVO[] result = itf.insert(new DataBillVO[]{billVO}, null);
				resultNum += (result==null?0:result.length);
			}
		}
		/***END***/
		
		return new Object[]{
				resultMsg,
				resultNum,
		};
	}
	
//	/**
//	 * Ӧ�������ܱ�ZF03�������ϣ�
//	 */
//	private Object importYsyuehzb(String[] pk_orgs,ArrayList<String> list_date) throws Exception
//	{
//		return null;
//	}
	
	/**
	 * �ֽ���ܱ�ZF04��
	 */
	private Object importQtskhzb(String[] pk_orgs,ArrayList<String> list_date) throws Exception
	{
		String BILL_NAME = "�ֽ���ܱ�";
		String BILL_CODE = IZfpms_Tool.ZF04;
		Connection zfPms_conn=null;
		JdbcSession zfPms_session =null;
		
		BaseDAO dao = new BaseDAO();
		
		Integer resultNum = 0;	// ���سɹ�������
		String resultMsg = "";	// ���ص���Ϣ
		
		/** 
		 * ��ÿ���ҵ�����ݣ���˫��ѭ��
		 * ��ѭ��  ��  ��
		 * ��ѭ��  ��  ��
		 */
		for(int org_i=0;org_i<pk_orgs.length;org_i++)
		{// ����  �� ѭ������
			
			String pk_org 	 = pk_orgs[org_i];				// pk_corp
			String db_Prefix = MAP_corp_db.get(pk_org);		// ����Դ ǰ׺
			String dian		 = MAP_corp_dian.get(pk_org);	// dian
			
			if(db_Prefix==null) continue;	// ���û������  ������
			
			zfPms_conn=new JDBCUtils(db_Prefix).getConn(null);
			zfPms_session = new JdbcSession(zfPms_conn);
		
			for (String yw_date : list_date) 
			{// ���� �� ѭ������
				
				String vbillno = BILL_NAME + "-" +dian + "-" + yw_date;
				
				// �����ж� NC�Ƿ��Ѿ����ڵ��ݣ����� ��֯ �� ���ڣ�
				// ������� ������
				{
					String checkSQL = 
						" SELECT " +
						" pk_zfpms_data " +
						" from zfpms_data  " +
						" where dr=0" +
						" and vbilltypecode = '"+BILL_CODE+"' " +
						" and pk_org = '"+(pk_org)+"' " +
						" and substr(dbilldate,1,10) = '"+(yw_date)+"' "
					;
					
					Object check_pk = getBaseDAO().executeQuery(checkSQL.toString(),new ColumnProcessor());
					
					if(check_pk!=null) continue;	// ���������  ��������
					
				}
				
				// �� �ӿڱ���ȡ��
				String querySQL = 
						" SELECT " +
						" hotel_group_id 	AS vbdef06 " + 	// 0���Ƶ���id
						",hotel_id 			AS vbdef07 " + 	// 1���Ƶ�id
						",id 				AS vbdef01 " + 	// 2��ID
						",pcode 			AS vbdef02 " + 	// 3���������
						",pdescript 		AS vbdef03 " + 	// 4�������������
						",ta_code 			AS vbdef04 " +  // 5��������
						",ta_descript 		AS vbdef05 " +  // 6������
						",pay 				AS bdata01 " + 	// 7�����˽��
						",charge 			AS bdata02 " + 	// 8�����ѽ��
						",num 				AS bdata03 " + 	// 9������
						" FROM rep_account_summary " + 
						" WHERE (1=1) " +
						" and SUBSTR(biz_date,1,10) = '"+yw_date+"' " +
						" ORDER BY id ";
				
				ArrayList<DataBVO> list = (ArrayList<DataBVO>)zfPms_session.executeQuery(querySQL, new BeanListProcessor(DataBVO.class));
				
				if(list==null||list.size()<=0) continue;	// �ӿ�����Ϊ0��������
				
				DataBillVO billVO = new DataBillVO();
				DataHVO   hVO  = new DataHVO();
				DataBVO[] bVOs = new DataBVO[list.size()];
				bVOs = list.toArray(bVOs);
				billVO.setParent(hVO);
				billVO.setChildrenVO(bVOs);
				
				//��װ��ͷVO
				hVO.setVbilltypecode( BILL_CODE );							// ��������
				hVO.setPk_group( AppContext.getInstance().getPkGroup() );	//����
				hVO.setPk_org( pk_org );			// pk_org
				hVO.setVbillcode( vbillno );		// ���ݺ�
				hVO.setIbillstatus(-1);				// ����״̬
				hVO.setCreator( AppContext.getInstance().getPkUser() );		// �Ƶ���
				hVO.setCreationtime( new UFDateTime() );					// �Ƶ�����
				hVO.setDirty( false );						// �Ƿ�ɾ��
				hVO.setDbilldate( new UFDate(yw_date) );	// ��������
				//��װ����VO
				for(int i=0;bVOs!=null&&i<bVOs.length;i++)
				{
					bVOs[i].setVrowno(""+(i+1));
					bVOs[i].setDirty(false);
				}
				
				IZfpms_dataMaintain itf = NCLocator.getInstance().lookup(IZfpms_dataMaintain.class);
				DataBillVO[] result = itf.insert(new DataBillVO[]{billVO}, null);
				resultNum += (result==null?0:result.length);
			}
		}
		
		return new Object[]{
				resultMsg,
				resultNum,
		};
	}
	
	/**
	 * ��ϸ��������ǰ̨������ϸ����ZF05��
	 */
	private Object importMxrkqjb(String[] pk_orgs,ArrayList<String> list_date) throws Exception
	{
		String BILL_NAME = "ǰ̨������ϸ";
		String BILL_CODE = IZfpms_Tool.ZF05;
		Connection zfPms_conn=null;
		JdbcSession zfPms_session =null;
		
		BaseDAO dao = new BaseDAO();
		
		Integer resultNum = 0;	// ���سɹ�������
		String resultMsg = "";	// ���ص���Ϣ
		
		/** 
		 * ��ÿ���ҵ�����ݣ���˫��ѭ��
		 * ��ѭ��  ��  ��
		 * ��ѭ��  ��  ��
		 */
		for(int org_i=0;org_i<pk_orgs.length;org_i++)
		{// ����  �� ѭ������
			
			String pk_org 	 = pk_orgs[org_i];				// pk_corp
			String db_Prefix = MAP_corp_db.get(pk_org);		// ����Դ ǰ׺
			String dian		 = MAP_corp_dian.get(pk_org);	// dian
			
			if(db_Prefix==null) continue;	// ���û������  ������
			
			zfPms_conn=new JDBCUtils(db_Prefix).getConn(null);
			zfPms_session = new JdbcSession(zfPms_conn);
		
			for (String yw_date : list_date) 
			{// ���� �� ѭ������
				
				String vbillno = BILL_NAME + "-" +dian + "-" + yw_date;
				
				// �����ж� NC�Ƿ��Ѿ����ڵ��ݣ����� ��֯ �� ���ڣ�
				// ������� ������
				{
					String checkSQL = 
						" SELECT " +
						" pk_zfpms_data " +
						" from zfpms_data  " +
						" where dr=0" +
						" and vbilltypecode = '"+BILL_CODE+"' " +
						" and pk_org = '"+(pk_org)+"' " +
						" and substr(dbilldate,1,10) = '"+(yw_date)+"' "
					;
					
					Object check_pk = getBaseDAO().executeQuery(checkSQL.toString(),new ColumnProcessor());
					
					if(check_pk!=null) continue;	// ���������  ��������
					
				}
				
				// �� �ӿڱ���ȡ��
				String querySQL = 
						" SELECT " +
						" hotel_group_id 	AS vbdef06 " + 	// 0���Ƶ���id
						",hotel_id 			AS vbdef07 " + 	// 1���Ƶ�id
						",id 				AS vbdef01 " + 	// 2��ID
						",accnt 			AS vbdef02 " + 	// 3���˺�
						",accnt_type 		AS vbdef03 " + 	// 4�����
						",ta_code 			AS vbdef04 " +  // 5������
						",ta_descript		AS vbdef05 " +  // 6������
						",name1 			AS vbdef08 " +  // 7������
						",cashier	 		AS vbdef09 " +  // 8�����
						",create_user 		AS vbdef10 " +  // 9������Ա
						",create_datetime 	AS vbdef11 " +  // 10������ʱ��
						",ta_no		 		AS vbdef12 " +  // 11������
						",ta_remark 		AS vbdef13 " +  // 12����ע
						",pay 				AS bdata01 " + 	// 13�����˽��
						",charge 			AS bdata02 " + 	// 14�����ѽ��
						" FROM rep_account_detail " + 
						" WHERE (1=1) " +
						" and accnt_type = 'FO' " +
						" and SUBSTR(biz_date,1,10) = '"+yw_date+"' " +
						" ORDER BY id ";
				
				ArrayList<DataBVO> list = (ArrayList<DataBVO>)zfPms_session.executeQuery(querySQL, new BeanListProcessor(DataBVO.class));
				
				if(list==null||list.size()<=0) continue;	// �ӿ�����Ϊ0��������
				
				DataBillVO billVO = new DataBillVO();
				DataHVO   hVO  = new DataHVO();
				DataBVO[] bVOs = new DataBVO[list.size()];
				bVOs = list.toArray(bVOs);
				billVO.setParent(hVO);
				billVO.setChildrenVO(bVOs);
				
				//��װ��ͷVO
				hVO.setVbilltypecode( BILL_CODE );							// ��������
				hVO.setPk_group( AppContext.getInstance().getPkGroup() );	//����
				hVO.setPk_org( pk_org );			// pk_org
				hVO.setVbillcode( vbillno );		// ���ݺ�
				hVO.setIbillstatus(-1);				// ����״̬
				hVO.setCreator( AppContext.getInstance().getPkUser() );		// �Ƶ���
				hVO.setCreationtime( new UFDateTime() );					// �Ƶ�����
				hVO.setDirty( false );						// �Ƿ�ɾ��
				hVO.setDbilldate( new UFDate(yw_date) );	// ��������
				//��װ����VO
				for(int i=0;bVOs!=null&&i<bVOs.length;i++)
				{
					bVOs[i].setVrowno(""+(i+1));
					bVOs[i].setDirty(false);
				}
				
				IZfpms_dataMaintain itf = NCLocator.getInstance().lookup(IZfpms_dataMaintain.class);
				DataBillVO[] result = itf.insert(new DataBillVO[]{billVO}, null);
				resultNum += (result==null?0:result.length);
			}
		}
		
		return new Object[]{
				resultMsg,
				resultNum,
		};
	}
	
	/**
	 * �����տ���ϸ��ZF06��
	 */
	private Object importCyskmx(String[] pk_orgs,ArrayList<String> list_date) throws Exception
	{
		String BILL_NAME = "�����տ���ϸ";
		String BILL_CODE = IZfpms_Tool.ZF06;
		Connection zfPms_conn=null;
		JdbcSession zfPms_session =null;
		
		BaseDAO dao = new BaseDAO();
		
		Integer resultNum = 0;	// ���سɹ�������
		String resultMsg = "";	// ���ص���Ϣ
		
		/** 
		 * ��ÿ���ҵ�����ݣ���˫��ѭ��
		 * ��ѭ��  ��  ��
		 * ��ѭ��  ��  ��
		 */
		for(int org_i=0;org_i<pk_orgs.length;org_i++)
		{// ����  �� ѭ������
			
			String pk_org 	 = pk_orgs[org_i];				// pk_corp
			String db_Prefix = MAP_corp_db.get(pk_org);		// ����Դ ǰ׺
			String dian		 = MAP_corp_dian.get(pk_org);	// dian
			
			if(db_Prefix==null) continue;	// ���û������  ������
			
			zfPms_conn=new JDBCUtils(db_Prefix).getConn(null);
			zfPms_session = new JdbcSession(zfPms_conn);
		
			for (String yw_date : list_date) 
			{// ���� �� ѭ������
				
				String vbillno = BILL_NAME + "-" +dian + "-" + yw_date;
				
				// �����ж� NC�Ƿ��Ѿ����ڵ��ݣ����� ��֯ �� ���ڣ�
				// ������� ������
				{
					String checkSQL = 
						" SELECT " +
						" pk_zfpms_data " +
						" from zfpms_data  " +
						" where dr=0" +
						" and vbilltypecode = '"+BILL_CODE+"' " +
						" and pk_org = '"+(pk_org)+"' " +
						" and substr(dbilldate,1,10) = '"+(yw_date)+"' "
					;
					
					Object check_pk = getBaseDAO().executeQuery(checkSQL.toString(),new ColumnProcessor());
					
					if(check_pk!=null) continue;	// ���������  ��������
					
				}
				
				// �� �ӿڱ���ȡ��
				String querySQL = 
						" SELECT " +
						" hotel_group_id 	AS vbdef06 " + 	// 0���Ƶ���id
						",hotel_id 			AS vbdef07 " + 	// 1���Ƶ�id
						",pccode 			AS vbdef02 " + 	// 2��Ӫҵ��
						",pccode_des 		AS vbdef03 " + 	// 3��Ӫҵ������
						",accnt 			AS vbdef04 " +  // 4���˺�
						",tableno			AS vbdef05 " +  // 5������
						",tableno_des		AS vbdef08 " +  // 6����������
						",rmno		 		AS vbdef09 " +  // 7������
						",gsts		 		AS vbdef10 " +  // 8������
						",create_datetime 	AS vbdef11 " +  // 9�����ʱ��
						",market		 	AS vbdef12 " +  // 10���г���
						",type1		 		AS vbdef13 " +  // 11����������
						",source	 		AS vbdef14 " +  // 12����Դ��
						",cusno		 		AS vbdef15 " +  // 13����˾
						",haccnt	 		AS vbdef16 " +  // 14������
						",cardno	 		AS vbdef17 " +  // 15��תǰ̨/תAR/��Ա
						",cashier	 		AS vbdef18 " +  // 16������
						",shift		 		AS vbdef19 " +  // 17�����
						",empid1	 		AS vbdef20 " +  // 18������Ա
						",paycode	 		AS cfirsttypecode " +  	// 19�����ʽ
						",categroy_code 	AS vfirstbillcode " +  	// 20���������
						",categroy_descript AS cfirstbillid " +  	// 21�������������
						",descript 			AS cfirstbillbid " +  	// 22������
						",salewater 		AS vfirstrowno " +  	// 23������Ա
						",info2 			AS vsourcebillcode " +  // 24��info2
						",amount 			AS bdata01 " + 			// 25�����
						" FROM pos_pay " + 
						" WHERE (1=1) " +
						" and SUBSTR(biz_date,1,10) = '"+yw_date+"' " +
						" ORDER BY create_datetime ";
				
				ArrayList<DataBVO> list = (ArrayList<DataBVO>)zfPms_session.executeQuery(querySQL, new BeanListProcessor(DataBVO.class));
				
				if(list==null||list.size()<=0) continue;	// �ӿ�����Ϊ0��������
				
				DataBillVO billVO = new DataBillVO();
				DataHVO   hVO  = new DataHVO();
				DataBVO[] bVOs = new DataBVO[list.size()];
				bVOs = list.toArray(bVOs);
				billVO.setParent(hVO);
				billVO.setChildrenVO(bVOs);
				
				//��װ��ͷVO
				hVO.setVbilltypecode( BILL_CODE );							// ��������
				hVO.setPk_group( AppContext.getInstance().getPkGroup() );	//����
				hVO.setPk_org( pk_org );			// pk_org
				hVO.setVbillcode( vbillno );		// ���ݺ�
				hVO.setIbillstatus(-1);				// ����״̬
				hVO.setCreator( AppContext.getInstance().getPkUser() );		// �Ƶ���
				hVO.setCreationtime( new UFDateTime() );					// �Ƶ�����
				hVO.setDirty( false );						// �Ƿ�ɾ��
				hVO.setDbilldate( new UFDate(yw_date) );	// ��������
				//��װ����VO
				for(int i=0;bVOs!=null&&i<bVOs.length;i++)
				{
					bVOs[i].setVrowno(""+(i+1));
					bVOs[i].setDirty(false);
				}
				
				IZfpms_dataMaintain itf = NCLocator.getInstance().lookup(IZfpms_dataMaintain.class);
				DataBillVO[] result = itf.insert(new DataBillVO[]{billVO}, null);
				resultNum += (result==null?0:result.length);
			}
		}
		
		return new Object[]{
				resultMsg,
				resultNum,
		};
	}
	
	/**
	 * AR�տ���ϸ��ZF07��
	 */
	private Object importArskmx(String[] pk_orgs,ArrayList<String> list_date) throws Exception
	{
		String BILL_NAME = "AR�տ���ϸ";
		String BILL_CODE = IZfpms_Tool.ZF07;
		Connection zfPms_conn=null;
		JdbcSession zfPms_session =null;
		
		BaseDAO dao = new BaseDAO();
		
		Integer resultNum = 0;	// ���سɹ�������
		String resultMsg = "";	// ���ص���Ϣ
		
		/** 
		 * ��ÿ���ҵ�����ݣ���˫��ѭ��
		 * ��ѭ��  ��  ��
		 * ��ѭ��  ��  ��
		 */
		for(int org_i=0;org_i<pk_orgs.length;org_i++)
		{// ����  �� ѭ������
			
			String pk_org 	 = pk_orgs[org_i];				// pk_corp
			String db_Prefix = MAP_corp_db.get(pk_org);		// ����Դ ǰ׺
			String dian		 = MAP_corp_dian.get(pk_org);	// dian
			
			if(db_Prefix==null) continue;	// ���û������  ������
			
			zfPms_conn=new JDBCUtils(db_Prefix).getConn(null);
			zfPms_session = new JdbcSession(zfPms_conn);
		
			for (String yw_date : list_date) 
			{// ���� �� ѭ������
				
				String vbillno = BILL_NAME + "-" +dian + "-" + yw_date;
				
				// �����ж� NC�Ƿ��Ѿ����ڵ��ݣ����� ��֯ �� ���ڣ�
				// ������� ������
				{
					String checkSQL = 
						" SELECT " +
						" pk_zfpms_data " +
						" from zfpms_data  " +
						" where dr=0" +
						" and vbilltypecode = '"+BILL_CODE+"' " +
						" and pk_org = '"+(pk_org)+"' " +
						" and substr(dbilldate,1,10) = '"+(yw_date)+"' "
					;
					
					Object check_pk = getBaseDAO().executeQuery(checkSQL.toString(),new ColumnProcessor());
					
					if(check_pk!=null) continue;	// ���������  ��������
					
				}
				
				// �� �ӿڱ���ȡ��
				String querySQL = 
						" SELECT " +
						" hotel_group_id 	AS vbdef06 " + 	// 0���Ƶ���id
						",hotel_id 			AS vbdef07 " + 	// 1���Ƶ�id
						",id 				AS vbdef01 " + 	// 2��ID
						",accnt 			AS vbdef02 " + 	// 3���˺�
						",accnt_type 		AS vbdef03 " + 	// 4�����
						",ta_code 			AS vbdef04 " +  // 5������
						",ta_descript		AS vbdef05 " +  // 6������
						",name1 			AS vbdef08 " +  // 7������
						",cashier	 		AS vbdef09 " +  // 8�����
						",create_user 		AS vbdef10 " +  // 9������Ա
						",create_datetime 	AS vbdef11 " +  // 10������ʱ��
						",ta_no		 		AS vbdef12 " +  // 11������
						",ta_remark 		AS vbdef13 " +  // 12����ע
						",pay 				AS bdata01 " + 	// 13�����˽��
						",charge 			AS bdata02 " + 	// 14�����ѽ��
						" FROM rep_account_detail " + 
						" WHERE (1=1) " +
						" and accnt_type = 'AR' " +
						" and SUBSTR(biz_date,1,10) = '"+yw_date+"' " +
						" ORDER BY id ";
				
				ArrayList<DataBVO> list = (ArrayList<DataBVO>)zfPms_session.executeQuery(querySQL, new BeanListProcessor(DataBVO.class));
				
				if(list==null||list.size()<=0) continue;	// �ӿ�����Ϊ0��������
				
				DataBillVO billVO = new DataBillVO();
				DataHVO   hVO  = new DataHVO();
				DataBVO[] bVOs = new DataBVO[list.size()];
				bVOs = list.toArray(bVOs);
				billVO.setParent(hVO);
				billVO.setChildrenVO(bVOs);
				
				//��װ��ͷVO
				hVO.setVbilltypecode( BILL_CODE );							// ��������
				hVO.setPk_group( AppContext.getInstance().getPkGroup() );	//����
				hVO.setPk_org( pk_org );			// pk_org
				hVO.setVbillcode( vbillno );		// ���ݺ�
				hVO.setIbillstatus(-1);				// ����״̬
				hVO.setCreator( AppContext.getInstance().getPkUser() );		// �Ƶ���
				hVO.setCreationtime( new UFDateTime() );					// �Ƶ�����
				hVO.setDirty( false );						// �Ƿ�ɾ��
				hVO.setDbilldate( new UFDate(yw_date) );	// ��������
				//��װ����VO
				for(int i=0;bVOs!=null&&i<bVOs.length;i++)
				{
					bVOs[i].setVrowno(""+(i+1));
					bVOs[i].setDirty(false);
				}
				
				IZfpms_dataMaintain itf = NCLocator.getInstance().lookup(IZfpms_dataMaintain.class);
				DataBillVO[] result = itf.insert(new DataBillVO[]{billVO}, null);
				resultNum += (result==null?0:result.length);
			}
		}
		
		return new Object[]{
				resultMsg,
				resultNum,
		};
	}
	
	/**
	 * ����BaseDAO
	 */
	BaseDAO dao=null;
	public BaseDAO getBaseDAO(){
		if(dao==null)
			dao=new BaseDAO();
		return dao;
	}
	
	/**
	 * ���ڵĴ���, ���� ��ʼ��������, ���� Ҫÿһ��������б�
	 */
	public ArrayList<String> getTimeDates(Object beginTime,Object endTime){
		UFDate beginDate=null;
		UFDate endDate=null;
		
		UFDateTime CurrentTime = new UFDateTime(new Date(TimeService.getInstance().getTime()));
		
		if((beginTime!=null&&beginTime.toString().trim().length()>0)&&(endTime!=null&&endTime.toString().trim().length()>0)){//��ʼ��������Ϊ��
			beginDate=new UFDate(beginTime.toString());
			endDate=new UFDate(endTime.toString());
		}else if((beginTime!=null&&beginTime.toString().trim().length()>0)&&(endTime==null||endTime.toString().trim().length()==0)){//��ʼ��Ϊ�գ�����Ϊ��
			beginDate=new UFDate(beginTime.toString());
			endDate=CurrentTime.getDate().getDateBefore(1);
		}else{//��ʼ������Ϊ��//��ʼΪ�գ�������Ϊ��
			beginDate=CurrentTime.getDate().getDateBefore(1);
			endDate=beginDate;
		}
		ArrayList<String> datesList=new ArrayList<String>();
		for (int i = 0; i <=UFDate.getDaysBetween(beginDate, endDate); i++) {
			String dateStr=beginDate.getDateAfter(i).toString().substring(0,10);
			if(!datesList.contains(dateStr))
			datesList.add(dateStr);
		}
		
		return datesList;
	}
	
	/**
	 * ���ڴ������
	 * ��̨�����޷�ִ��
	 */
	public Object executeTest(HashMap<String, Object> para, Object other) throws BusinessException
	{
		String[] pk_orgs = (String[])para.get("pk_orgs");
		String ksrq = (String)para.get("ksrq");
		String jsrq = (String)para.get("jsrq");
		Integer dataType = (Integer)para.get("dataType");
		
		UFDate bdate = PuPubVO.getUFDate(ksrq);
		UFDate edate = PuPubVO.getUFDate(jsrq);
		ArrayList<String> list_date = this.getTimeDates(bdate,edate);
		
		Integer resultNum = 0;	// ���سɹ�������
		String resultMsg = "";	// ���ص���Ϣ
		
		try
		{
			// 1�������տ���ϸ��
			if(dataType==0||dataType==1)
			{
				Object[] result = (Object[])importCyskmx(pk_orgs,list_date);
				resultNum += (Integer)result[1];
			}
			// 2���ֽ���ܱ�
			if(dataType==0||dataType==2)
			{
				Object[] result = (Object[])importQtskhzb(pk_orgs,list_date);
				resultNum += (Integer)result[1];
			}
			// 3��ǰ̨������ϸ
			if(dataType==0||dataType==3)
			{
				Object[] result = (Object[])importMxrkqjb(pk_orgs,list_date);
				resultNum += (Integer)result[1];
			}
			// 4��Ӫҵ�ձ�
			if(dataType==0||dataType==4)
			{
				Object[] result = (Object[])importYyrb(pk_orgs,list_date);
				resultNum += (Integer)result[1];
			}
			// 5�����˵ױ�
			if(dataType==0||dataType==5)
			{
				Object[] result = (Object[])importJhdb(pk_orgs,list_date);
				resultNum += (Integer)result[1];
			}
			// 6��AR�տ���ϸ
			if(dataType==0||dataType==6)
			{
				Object[] result = (Object[])importArskmx(pk_orgs,list_date);
				resultNum += (Integer)result[1];
			}
			
		}catch(Exception ex)
		{
			throw new BusinessException(ex);
		}
		
		return new Object[]{
				resultMsg,
				resultNum,
		};
	}
	
}
