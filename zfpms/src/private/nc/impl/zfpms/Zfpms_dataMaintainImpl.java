package nc.impl.zfpms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.uap.lock.PKLock;
import nc.impl.pub.ace.AceZfpms_dataPubServiceImpl;
import nc.itf.zfpms.IZfpms_Tool;
import nc.itf.zfpms.IZfpms_dataMaintain;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.pubitf.fip.service.IFipBillQueryService;
import nc.pubitf.fip.service.IFipMessageService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipMsgResultVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.tools.PuPubVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.zfpms.data.DataBVO;
import nc.vo.zfpms.data.DataBillVO;
import nc.vo.zfpms.data.DataHVO;

public class Zfpms_dataMaintainImpl extends AceZfpms_dataPubServiceImpl
		implements IZfpms_dataMaintain {
	
	static BaseDAO dao = new BaseDAO();

	@Override
	public void delete(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public DataBillVO[] insert(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public DataBillVO[] update(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public DataBillVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public DataBillVO[] save(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public DataBillVO[] unsave(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public DataBillVO[] approve(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		
		/**
		 * ���ʱ  ����ƾ֤
		 */
		try {
			for(DataBillVO billVO : clientFullVOs){
				this.genNCVoucherInfo(billVO, FipMessageVO.MESSAGETYPE_ADD);
			}
		} catch(Exception ex) {
			throw new BusinessException(ex);
		}
		/***END***/
		
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public DataBillVO[] unapprove(DataBillVO[] clientFullVOs,
			DataBillVO[] originBills) throws BusinessException {
		
		/**
		 * ����ʱ  ɾ��ƾ֤
		 */
		try {
			for(DataBillVO billVO : clientFullVOs){
				this.genNCVoucherInfo(billVO, FipMessageVO.MESSAGETYPE_DEL);
			}
		} catch(Exception ex) {
			throw new BusinessException(ex);
		}
		/***END***/
		
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	@Override
	public Object qushu(HashMap<String, Object> para, Object other)
			throws BusinessException {
		return super.qushu(para,other);
	}

	/**
	 * ���ɡ�ɾ�� ƾ֤
	 */
	@Override
	public String genNCVoucherInfo(DataBillVO billVO, int flag)
			throws Exception {

		if (billVO != null) {
			
			DataHVO    hVO = billVO.getParentVO();
			DataBVO[] bVOs = (DataBVO[])billVO.getChildrenVO();
			
			String billType  = hVO.getVbilltypecode();	// ��������
			String vbillcode = hVO.getVbillcode();		// ����
			
			/**
			 * ���һ�ŵ��� ��Ҫ���ɶ��ƾ֤����Ҫ�ڴ����á�
			 * ҵ���� �������� �����ƾ֤�������
			 */
			String[] voucherTypes = new String[]{
				"voucher",	// ƾ֤
			};
			
			for(String voucherType : voucherTypes)
			{
				// �ɱ���+�ڼ� Ҫ���м�������
				PKLock pklock = PKLock.getInstance();
				if (!pklock.addBatchDynamicLock(new String[] {
						vbillcode })) {
					throw new BusinessException("�õ�����������ƾ֤�����Ժ�����!");
				}
	
				// �����ɾ��ƾ֤��ֻ��Ҫ�� ����VO�������װ�ӱ�
				if(FipMessageVO.MESSAGETYPE_DEL==flag){
					DataBillVO billVO_final = new DataBillVO();
					billVO_final.setParent(hVO);
					sendVoucher( billVO_final , FipMessageVO.MESSAGETYPE_DEL, null );	// ɾ��
				}
				else if(FipMessageVO.MESSAGETYPE_ADD==flag){
				
					// ��Ҫ����ƾ֤������
					Vector<DataBVO> bVOs_v_final = new Vector<DataBVO>();
					// �跽��� ��ͣ����� ƾ֤��Ϣ�� ��Դ���
					UFDouble mny = null;
					/**
					 * ��ƾ֤
					 */
					if("voucher".equals(voucherType))
					{
						for(DataBVO bVO : bVOs){
							
						}
						
						if(IZfpms_Tool.ZF01.equals(billType)) {
							mny =  _voucherType_YYRB(
									 bVOs_v_final	// ���ص����ݼ���
									,bVOs			// ��Ҫ���������
									,null
							);
						}
						
					}
					/**END*/
					
					// �� �Ƚ���  ����
					DataBVO[] bVOs_final = 
						_orderVoucher(
							bVOs_v_final,
							new String[]{"bdata01","bdata02"}
					);
					// ����ƾ֤����
					DataBillVO billVO_final = new DataBillVO();
					billVO_final.setParent(hVO);
					billVO_final.setChildrenVO(bVOs_final);
					sendVoucher( billVO_final , FipMessageVO.MESSAGETYPE_ADD, mny );	// ����
				}
			}
		}
		return null;
	}
	
	/**
	 * ��ƾ֤ ���
	 * ������� public
	 * 2019��8��26��11��31��
	 */
	public static void sendVoucher(AbstractBill billVO,int flag,UFDouble mny) throws BusinessException{
		
		if( flag==0 )
		{
			// �ж� �������ɹ�ƾ֤
			FipExtendAggVO[] pz = NCLocator.getInstance().lookup(
				IFipBillQueryService.class).queryDesBillBySrc(	// ���ݵ�����Ϣ ��ѯ �����ɵ�ƾ֤
				 new FipRelationInfoVO[]{ constructFipRalactionInfo(billVO,mny) } 
				,null
			);
			boolean hasPZ = pz!=null && pz.length>0;	// �Ƿ����ƾ֤
			if( hasPZ ) return;
//				throw new BusinessException("�����ɹ�ƾ֤�������ظ����ɡ�");
			
			// ����ƾ֤
			sendMsgFip(
				 new AbstractBill[]{billVO}
				,FipMessageVO.MESSAGETYPE_ADD
				,mny
			);
		}
		else if( flag==1 )
		{
			// ɾ��ƾ֤
			sendMsgFip(
				 new AbstractBill[]{billVO}
				,FipMessageVO.MESSAGETYPE_DEL
				,mny
			);
		}
		
	}
	/**
	 * ��ƾ֤ ���
	 * 2019��8��5��11:55:36
	 */
	private static void sendMsgFip(AbstractBill[] billVOs,int type,UFDouble mny) throws BusinessException{
			
		FipMessageVO[] messageVOs=new FipMessageVO[billVOs.length];
		for (int i=0;i<billVOs.length;i++ ){
			FipMessageVO fipmessagevo=new FipMessageVO();	
			FipRelationInfoVO reVO =constructFipRalactionInfo(billVOs[i],mny);
			
			//0��ʾ����,������ֵ��Ĭ��Ϊ0
			fipmessagevo.setMessagetype(type);		
			fipmessagevo.setDirty(false);
			fipmessagevo.setAuotSum(false);
			
			//���BillVO
			fipmessagevo.setBillVO(billVOs[i]);
			//���FipRelationInfoVO
			fipmessagevo.setMessageinfo(reVO);
			messageVOs[i]=fipmessagevo;			
		}
		
		invokeFipMessage(messageVOs);
		
	}
	/**
	 * ��ƾ֤ ���
	 * 2019��8��26��11��30��
	 */
	private static FipRelationInfoVO constructFipRalactionInfo(AbstractBill billVO,UFDouble mny) {	
	
		//�����ϢVO
		FipRelationInfoVO relation = new FipRelationInfoVO();
		
		String sbilltype = PuPubVO.getString_TrimZeroLenAsNull(billVO.getParentVO().getAttributeValue("vbilltypecode"));
		BilltypeVO billType = PfDataCache.getBillType(sbilltype);
		
		relation.setPk_group(	// ����
				PuPubVO.getString_TrimZeroLenAsNull(billVO.getParentVO().getAttributeValue("pk_group")) );
		relation.setPk_org(		// ��֯
				PuPubVO.getString_TrimZeroLenAsNull(billVO.getParentVO().getAttributeValue("pk_org")) );
		relation.setRelationID(	// ����ID
				billVO.getPrimaryKey() );
		relation.setPk_system(	// ��Դϵͳ
				billType.getSystemcode() );
		relation.setBusidate( 	// ҵ������
				PuPubVO.getUFDate(billVO.getParentVO().getAttributeValue("dbilldate")) );
		relation.setPk_billtype(// ��������
				sbilltype);							
		relation.setPk_operator(// �Ƶ���
				InvocationInfoProxy.getInstance().getUserId());

		relation.setFreedef1(	// ���ݺ� 
				PuPubVO.getString_TrimZeroLenAsNull(billVO.getParentVO().getAttributeValue("vbillcode")) );
		relation.setFreedef2(	// ��ע��Ϣ
				PuPubVO.getString_TrimZeroLenAsNull(billVO.getParentVO().getAttributeValue("vmemo")) );
		relation.setFreedef3(	// ��Դ���
				PuPubVO.getUFDouble_NullAsZero(mny).toString() );
		
		return relation;		
		
	}
	/**
	 * ��ƾ֤ ���
	 * 2019��8��26��11��29��
	 */
	private static FipMsgResultVO[] invokeFipMessage(FipMessageVO[] messageVOs) throws BusinessException {
		if (Logger.isDebugEnabled()) {
			Logger.info("sendMessage is over!");
		}
		
		FipMsgResultVO[] result = 
			NCLocator.getInstance().lookup(IFipMessageService.class)
			.sendMessages(messageVOs)
		;
		
		return result;
	}
	
	/**
	 * �� �����SuperVO����� ĳ���ֶ����
	 */
	public static UFDouble sumMny(SuperVO[] vos, String fieldName) throws BusinessException
	{
		try {
			
			UFDouble mny = UFDouble.ZERO_DBL;
			
			for( SuperVO vo : vos ) {
				UFDouble mny_temp = PuPubVO.getUFDouble_ZeroAsNull(
						vo.getAttributeValue(fieldName) );
				
				if(mny_temp!=null) {
					mny = mny.add(mny_temp);
				}
			}
			
			return mny.setScale(2, UFDouble.ROUND_HALF_UP);
			
		} catch (Exception ex) {
			throw new BusinessException(ex);
		}
	}
	
	/**
	 * ƾ֤���ݵķ�װ-YYRB
	 * Ӫҵ�ձ�
	 * Vbdef02	��Ŀ���� ���ڿ�Ŀ����
	 * Bdata01	�跽���
	 * Bdata02	�������
	 * ���أ��������裩
	 */
	private static UFDouble _voucherType_YYRB(
				 Vector<DataBVO> bVOs_v_final	// ���ص����ݼ���
				,DataBVO[] bVOs					// ��Ҫ���������
				,Object obj 
			) throws BusinessException 
	{
		
		UFDouble jie = UFDouble.ZERO_DBL;	// �跽���
		
		try{
			// ��Ҫ���Ȳ�ѯ�� ��Ŀ���ձ��� ���õ���Ϣ��
			HashMap<String,String> kmMAP = new HashMap<String,String>();
			{
				StringBuffer querySQL_km = 
				new StringBuffer(" select ")
						.append(" b.FACTORVALUE1 ")
						.append(" from ")
						.append(" FIP_DOCVIEW h ")
						.append(" inner join FIP_DOCVIEW_B b ")
						.append(" on h.PK_CLASSVIEW = b.PK_CLASSVIEW ")
						.append(" where h.dr = 0 and b.dr = 0 ")
						.append(" and h.viewcode = 'PMS-YYRB' ")
				;
				ArrayList list_km = (ArrayList)dao.executeQuery(querySQL_km.toString(), new ArrayListProcessor());
				for(Object temp : list_km) {
					Object[] value = (Object[])temp;
					String kmCode = PuPubVO.getString_TrimZeroLenAsNull(value[0]);
					kmMAP.put(kmCode, kmCode);
				}
			}
			
			// �������
			for(DataBVO vo : bVOs) {
				
				String xmCode = PuPubVO.getString_TrimZeroLenAsNull(vo.getVbdef02());
				UFDouble dai  = PuPubVO.getUFDouble_ZeroAsNull(vo.getBdata02());
				String zhaiyao= PuPubVO.getString_TrimZeroLenAsNull(vo.getVbdef02());
				
				if( kmMAP.containsKey(xmCode) ) {
					
					if(dai!=null) {
						
						DataBVO voDai = new DataBVO();
						bVOs_v_final.add(voDai);
						voDai.setBdata02(dai);		// �������
						voDai.setVbdef02(xmCode);
						voDai.setVbmemo(zhaiyao);
						
						jie = jie.add(dai);
					}
				}
			}
			
			// ����跽
			if(PuPubVO.getUFDouble_ZeroAsNull(jie)!=null) {
				DataBVO voJie = new DataBVO();
				bVOs_v_final.add(voJie);
				voJie.setBdata01(jie);			// �跽���
				voJie.setVbdef02("�����Կ�Ŀ");
				voJie.setVbmemo("�����Կ�Ŀ");
			}
			
		}catch(Exception ex){
			throw new BusinessException(ex);
		}
		
		return jie;
	}

	/**
	 * ��� ƾ֤����  ����
	 * zhaoyaos �� ժҪ��˳��
	 * keys �� �跽������ �����ֶ�����
	 * �������ϰ����  ���н跽 ��ǰ�����д��� �ں�
	 * ��ͬժҪ�� ��һ�ƣ�Ȼ�� ����ǰ�����ں�
	 */
	private static DataBVO[] _orderVoucherGroupByZY(
			Vector<DataBVO> bVOs_v
			,String[] zhaoyaos
			,String[] keys)
			throws BusinessException {
		
		try
		{
			ArrayList<DataBVO> resultList = new ArrayList<DataBVO>();
			HashMap<String,ArrayList<DataBVO>> MAP = new HashMap<String,ArrayList<DataBVO>>();
			
			for(DataBVO bVO : bVOs_v){
				
				String MAP_key = bVO.getVbmemo();
				
				if(MAP.containsKey(MAP_key)){
					MAP.get(MAP_key).add(bVO);
				}
				else{
					ArrayList<DataBVO> MAP_value = new ArrayList<DataBVO>();
					MAP_value.add(bVO);
					MAP.put(MAP_key,MAP_value);
				}
			}
			
			for(String zy : zhaoyaos){
				
				ArrayList<DataBVO> list = MAP.get(zy);
				if(list==null||list.size()<=0) continue;
				
				for(int i=0;i<keys.length;i++)
				{
					String key = keys[i];
					
					for(int voI=0;voI<list.size();voI++)
					{
						DataBVO bVO = list.get(voI);
						UFDouble mny = PuPubVO.getUFDouble_ZeroAsNull(bVO.getAttributeValue(key));
						if(mny!=null)
						{
							resultList.add(bVO);
						}
					}
				}
			}
			
			DataBVO[] resultVOs = new DataBVO[resultList.size()];
			for(int i=0;i<resultList.size();i++)
			{
				resultVOs[i] = resultList.get(i);
			}
			
			return resultVOs;
			
		}catch(Exception ex)
		{
			throw new BusinessException(ex);
		}
	}
	/**
	 * ���� ������������
	 */
	private static DataBVO[] _orderVoucher(
			Vector<DataBVO> bVOs_v
			,String[] keys)
			throws BusinessException {
		
		try
		{
			DataBVO[] resultVOs = new DataBVO[bVOs_v.size()];
			for(int i=0;i<bVOs_v.size();i++)
			{
				resultVOs[i] = bVOs_v.get(i);
			}
			
			return resultVOs;
			
		}catch(Exception ex)
		{
			throw new BusinessException(ex);
		}
	}
}
