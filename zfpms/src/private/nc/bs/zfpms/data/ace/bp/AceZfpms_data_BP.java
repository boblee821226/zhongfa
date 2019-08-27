package nc.bs.zfpms.data.ace.bp;

import java.util.HashMap;

import nc.bs.zfpms.workplugin.ImportPmsDataPlugin;
import nc.vo.pub.BusinessException;

/**
 * 客开BP
 */
public class AceZfpms_data_BP {
	
	/**
	 * 取数
	 */
	public Object qushu(HashMap<String, Object> para, Object other)
			throws BusinessException
	{
		return new ImportPmsDataPlugin().executeTest(para,other);
	}
}
