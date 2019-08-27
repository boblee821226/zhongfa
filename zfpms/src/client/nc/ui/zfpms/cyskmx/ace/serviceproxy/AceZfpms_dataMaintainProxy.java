package nc.ui.zfpms.cyskmx.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.zfpms.IZfpms_dataMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceZfpms_dataMaintainProxy implements IQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IZfpms_dataMaintain query = NCLocator.getInstance().lookup(
				IZfpms_dataMaintain.class);
		return query.query(queryScheme);
	}

}