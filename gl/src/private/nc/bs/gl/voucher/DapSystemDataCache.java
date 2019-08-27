package nc.bs.gl.voucher;

import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import nc.bs.logging.Logger;
import nc.vo.glcom.tools.GLPubProxy;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.sm.funcreg.ModuleVO;
/**
 * 系统模块。 创建日期：(2003-9-4 11:57:32)
 * 
 * @author：郭宝华
 */
public class DapSystemDataCache {
	public static DapSystemDataCache instance = new DapSystemDataCache();

	private static HashMap datacache = new HashMap();

	private static HashMap code_datacache = new HashMap();
	

	/**
	 * DapSystemDataCache 构造子注解。
	 */
	public DapSystemDataCache() {
		super();
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-9-4 12:01:18)
	 */
	public void clear() {
		datacache.clear();
		code_datacache.clear();

	}

	/**
	 * 得到所有的系统数据缓存。 创建日期：(2002-10-16 14:49:32)
	 *
	 * @return java.util.HashMap
	 */
	public ModuleVO[] getDapSystem() {
		if (datacache.size() == 0) {
			ModuleVO[] vos = null;
			try {
				Collection c = GLPubProxy.getRemoteIUAPQueryBS().retrieveAll(ModuleVO.class);
				vos = new ModuleVO[c.size()];
				c.toArray(vos);
				
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}


			if (vos != null) {
				datacache.put("@@@@", vos);
				for (int i = 0; i < vos.length; i++) {
					//协同凭证的typecode跟渠道拜访冲突，被覆盖，临时解决办法，直接写死
					if(vos[i].getSystypecode().trim().toUpperCase().equals("CV")){
						vos[i].setResid("M200213");
						vos[i].setSystypename("协同凭证");
						vos[i].setModuleid("2002Z313");
						vos[i].setIsaccount(UFBoolean.TRUE);
					}
					code_datacache.put(vos[i].getSystypecode(), vos[i]);
				}
				return vos;
			} else {
				return null;
			}

		} else {
			return (ModuleVO[]) datacache.get("@@@@");
		}
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-7-1 17:16:53)
	 *
	 * @return java.util.HashMap
	 */
	public ModuleVO getDapsystem(String dapsystemcode) throws Exception {
		if (dapsystemcode == null || dapsystemcode.trim().length() == 0)
			return null;
		if(code_datacache.size()==0)
			getDapSystem();
		StringTokenizer st = new StringTokenizer(dapsystemcode, ",");
		ModuleVO tmp_dapsystem = null;
		while (st.hasMoreTokens()) {
			String system = (String) st.nextToken();
			
			/**
			 * 李彬 2019年8月16日11:32:28
			 * 产品bug吗？ 为什么要全部转成 大写 或 小写去判断？？ 直接找 不就行了吗。
			 * 若 模块包含大小写，就永远匹配不到，保存正式凭证 报错（未找到来源系统）
			 */
			if("ZFpms".equals(system)) {
				tmp_dapsystem = (ModuleVO) code_datacache.get(system);
			} else {
				// 标准产品 源代码
				tmp_dapsystem = (ModuleVO) code_datacache.get(system.toUpperCase());
			}
			/***END***/
			
			if (tmp_dapsystem == null) {
				tmp_dapsystem = (ModuleVO) code_datacache.get(system.toLowerCase());
			}
			if (tmp_dapsystem == null) {
				throw new Exception( nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2002gl55","UPP2002gl55-000266")/*@res "没有对应的来源系统！"*/);
			}
		}
		return tmp_dapsystem;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-7-1 17:16:53)
	 *
	 * @return java.util.HashMap
	 */
	public String getDapsystemName(String dapsystemcode) throws Exception {
		if (dapsystemcode == null || dapsystemcode.trim().length() == 0)
			return null;
		if(code_datacache.size()==0)
			getDapSystem();
		ModuleVO tmp_dapsystem = (ModuleVO) code_datacache.get(dapsystemcode);
		if (tmp_dapsystem == null)
			throw new Exception( nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2002gl55","UPP2002gl55-000266")/*@res "没有对应的来源系统！"*/ );
		return tmp_dapsystem.getSystypename();
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-7-1 11:28:34)
	 *
	 * @return nc.ui.gl.vouchertools.VoucherDataCenter
	 */
	public static DapSystemDataCache getInstance() {
		return instance;
	}

	public static DapSystemDataCache newInstance() {
		instance = new DapSystemDataCache();
		return instance;
	}
}