package nc.bs.zfpms.data.ace.bp;

import java.util.HashMap;

import nc.bs.zfpms.workplugin.ImportPmsDataPlugin;
import nc.vo.pub.BusinessException;

/**
 * �Ϳ�BP
 */
public class AceZfpms_data_BP {
	
	/**
	 * ȡ��
	 */
	public Object qushu(HashMap<String, Object> para, Object other)
			throws BusinessException
	{
		return new ImportPmsDataPlugin().executeTest(para,other);
	}
}
