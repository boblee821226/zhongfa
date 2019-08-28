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
		 * 审核时  生成凭证
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
		 * 弃审时  删除凭证
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
	 * 生成、删除 凭证
	 */
	@Override
	public String genNCVoucherInfo(DataBillVO billVO, int flag)
			throws Exception {

		if (billVO != null) {
			
			DataHVO    hVO = billVO.getParentVO();
			DataBVO[] bVOs = (DataBVO[])billVO.getChildrenVO();
			
			String billType  = hVO.getVbilltypecode();	// 单据类型
			String vbillcode = hVO.getVbillcode();		// 单号
			
			/**
			 * 如果一张单据 需要生成多个凭证，需要在此配置。
			 * 业务上 尽量避免 生多个凭证的情况。
			 */
			String[] voucherTypes = new String[]{
				"voucher",	// 凭证
			};
			
			for(String voucherType : voucherTypes)
			{
				// 成本域+期间 要进行加锁处理
				PKLock pklock = PKLock.getInstance();
				if (!pklock.addBatchDynamicLock(new String[] {
						vbillcode })) {
					throw new BusinessException("该单据正在生成凭证，请稍后再试!");
				}
	
				// 如果是删除凭证，只需要传 主表VO，无需封装子表
				if(FipMessageVO.MESSAGETYPE_DEL==flag){
					DataBillVO billVO_final = new DataBillVO();
					billVO_final.setParent(hVO);
					sendVoucher( billVO_final , FipMessageVO.MESSAGETYPE_DEL, null );	// 删除
				}
				else if(FipMessageVO.MESSAGETYPE_ADD==flag){
				
					// 需要生成凭证的数据
					Vector<DataBVO> bVOs_v_final = new Vector<DataBVO>();
					// 借方金额 求和，当作 凭证信息的 来源金额
					UFDouble mny = null;
					/**
					 * 生凭证
					 */
					if("voucher".equals(voucherType))
					{
						for(DataBVO bVO : bVOs){
							
						}
						
						if(IZfpms_Tool.ZF01.equals(billType)) {
							mny =  _voucherType_YYRB(
									 bVOs_v_final	// 返回的数据集合
									,bVOs			// 需要处理的数据
									,null
							);
						}
						
					}
					/**END*/
					
					// 按 先借后贷  排序
					DataBVO[] bVOs_final = 
						_orderVoucher(
							bVOs_v_final,
							new String[]{"bdata01","bdata02"}
					);
					// 进行凭证传递
					DataBillVO billVO_final = new DataBillVO();
					billVO_final.setParent(hVO);
					billVO_final.setChildrenVO(bVOs_final);
					sendVoucher( billVO_final , FipMessageVO.MESSAGETYPE_ADD, mny );	// 新增
				}
			}
		}
		return null;
	}
	
	/**
	 * 生凭证 相关
	 * 对外调用 public
	 * 2019年8月26日11点31分
	 */
	public static void sendVoucher(AbstractBill billVO,int flag,UFDouble mny) throws BusinessException{
		
		if( flag==0 )
		{
			// 判断 有无生成过凭证
			FipExtendAggVO[] pz = NCLocator.getInstance().lookup(
				IFipBillQueryService.class).queryDesBillBySrc(	// 根据单据信息 查询 所生成的凭证
				 new FipRelationInfoVO[]{ constructFipRalactionInfo(billVO,mny) } 
				,null
			);
			boolean hasPZ = pz!=null && pz.length>0;	// 是否存在凭证
			if( hasPZ ) return;
//				throw new BusinessException("已生成过凭证，不能重复生成。");
			
			// 生成凭证
			sendMsgFip(
				 new AbstractBill[]{billVO}
				,FipMessageVO.MESSAGETYPE_ADD
				,mny
			);
		}
		else if( flag==1 )
		{
			// 删除凭证
			sendMsgFip(
				 new AbstractBill[]{billVO}
				,FipMessageVO.MESSAGETYPE_DEL
				,mny
			);
		}
		
	}
	/**
	 * 生凭证 相关
	 * 2019年8月5日11:55:36
	 */
	private static void sendMsgFip(AbstractBill[] billVOs,int type,UFDouble mny) throws BusinessException{
			
		FipMessageVO[] messageVOs=new FipMessageVO[billVOs.length];
		for (int i=0;i<billVOs.length;i++ ){
			FipMessageVO fipmessagevo=new FipMessageVO();	
			FipRelationInfoVO reVO =constructFipRalactionInfo(billVOs[i],mny);
			
			//0表示新增,若不赋值，默认为0
			fipmessagevo.setMessagetype(type);		
			fipmessagevo.setDirty(false);
			fipmessagevo.setAuotSum(false);
			
			//填充BillVO
			fipmessagevo.setBillVO(billVOs[i]);
			//填充FipRelationInfoVO
			fipmessagevo.setMessageinfo(reVO);
			messageVOs[i]=fipmessagevo;			
		}
		
		invokeFipMessage(messageVOs);
		
	}
	/**
	 * 生凭证 相关
	 * 2019年8月26日11点30分
	 */
	private static FipRelationInfoVO constructFipRalactionInfo(AbstractBill billVO,UFDouble mny) {	
	
		//填充消息VO
		FipRelationInfoVO relation = new FipRelationInfoVO();
		
		String sbilltype = PuPubVO.getString_TrimZeroLenAsNull(billVO.getParentVO().getAttributeValue("vbilltypecode"));
		BilltypeVO billType = PfDataCache.getBillType(sbilltype);
		
		relation.setPk_group(	// 集团
				PuPubVO.getString_TrimZeroLenAsNull(billVO.getParentVO().getAttributeValue("pk_group")) );
		relation.setPk_org(		// 组织
				PuPubVO.getString_TrimZeroLenAsNull(billVO.getParentVO().getAttributeValue("pk_org")) );
		relation.setRelationID(	// 单据ID
				billVO.getPrimaryKey() );
		relation.setPk_system(	// 来源系统
				billType.getSystemcode() );
		relation.setBusidate( 	// 业务日期
				PuPubVO.getUFDate(billVO.getParentVO().getAttributeValue("dbilldate")) );
		relation.setPk_billtype(// 单据类型
				sbilltype);							
		relation.setPk_operator(// 制单人
				InvocationInfoProxy.getInstance().getUserId());

		relation.setFreedef1(	// 单据号 
				PuPubVO.getString_TrimZeroLenAsNull(billVO.getParentVO().getAttributeValue("vbillcode")) );
		relation.setFreedef2(	// 备注信息
				PuPubVO.getString_TrimZeroLenAsNull(billVO.getParentVO().getAttributeValue("vmemo")) );
		relation.setFreedef3(	// 来源金额
				PuPubVO.getUFDouble_NullAsZero(mny).toString() );
		
		return relation;		
		
	}
	/**
	 * 生凭证 相关
	 * 2019年8月26日11点29分
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
	 * 将 传入的SuperVO数组的 某个字段求和
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
	 * 凭证数据的封装-YYRB
	 * 营业日报
	 * Vbdef02	项目编码 用于科目对照
	 * Bdata01	借方金额
	 * Bdata02	贷方金额
	 * 返回：发生金额（借）
	 */
	private static UFDouble _voucherType_YYRB(
				 Vector<DataBVO> bVOs_v_final	// 返回的数据集合
				,DataBVO[] bVOs					// 需要处理的数据
				,Object obj 
			) throws BusinessException 
	{
		
		UFDouble jie = UFDouble.ZERO_DBL;	// 借方金额
		
		try{
			// 需要首先查询出 科目对照表里 配置的信息。
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
			
			// 处理贷方
			for(DataBVO vo : bVOs) {
				
				String xmCode = PuPubVO.getString_TrimZeroLenAsNull(vo.getVbdef02());
				UFDouble dai  = PuPubVO.getUFDouble_ZeroAsNull(vo.getBdata02());
				String zhaiyao= PuPubVO.getString_TrimZeroLenAsNull(vo.getVbdef02());
				
				if( kmMAP.containsKey(xmCode) ) {
					
					if(dai!=null) {
						
						DataBVO voDai = new DataBVO();
						bVOs_v_final.add(voDai);
						voDai.setBdata02(dai);		// 贷方金额
						voDai.setVbdef02(xmCode);
						voDai.setVbmemo(zhaiyao);
						
						jie = jie.add(dai);
					}
				}
			}
			
			// 处理借方
			if(PuPubVO.getUFDouble_ZeroAsNull(jie)!=null) {
				DataBVO voJie = new DataBVO();
				bVOs_v_final.add(voJie);
				voJie.setBdata01(jie);			// 借方金额
				voJie.setVbdef02("过渡性科目");
				voJie.setVbmemo("过渡性科目");
			}
			
		}catch(Exception ex){
			throw new BusinessException(ex);
		}
		
		return jie;
	}

	/**
	 * 针对 凭证数据  排序。
	 * zhaoyaos 传 摘要的顺序。
	 * keys 传 借方、贷方 金额的字段名。
	 * 按财务的习惯是  所有借方 在前，所有贷方 在后。
	 * 相同摘要的 放一推，然后 借在前，贷在后。
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
	 * 排序 返回最终数据
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
