package jpiere.plugin.matrixwindow.process;

import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

public class DeleteRecordSample extends SvrProcess {

	int record_ID = 0;

	/**Target Organization(Option)*/
	private int			p_AD_Org_ID = 0;

	@Override
	protected void prepare() {
		record_ID = getRecord_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("AD_Org_ID")){
				p_AD_Org_ID = para[i].getParameterAsInt();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for

	}

	@Override
	protected String doIt() throws Exception {

//		MPatternLine pl = new MPatternLine(getCtx(),record_ID,get_TrxName());
//		pl.deleteEx(true, get_TrxName());


		return "OK";
	}

}
