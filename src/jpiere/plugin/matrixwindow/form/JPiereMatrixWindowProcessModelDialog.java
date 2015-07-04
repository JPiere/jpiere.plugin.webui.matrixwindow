package jpiere.plugin.matrixwindow.form;

import org.adempiere.webui.apps.ProcessModalDialog;
import org.compiere.process.ProcessInfo;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

public class JPiereMatrixWindowProcessModelDialog extends ProcessModalDialog {

	private JPiereMatrixWindow matrixWindow;

	public JPiereMatrixWindowProcessModelDialog(int WindowNo, ProcessInfo pi,
			boolean autoStart) {
		super(WindowNo, pi, autoStart);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public JPiereMatrixWindowProcessModelDialog(EventListener<Event> listener,
			int WindowNo, ProcessInfo pi, boolean autoStart) {
		super(listener, WindowNo, pi, autoStart);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public JPiereMatrixWindowProcessModelDialog(int WindowNo,
			int AD_Process_ID, int tableId, int recordId, boolean autoStart,JPiereMatrixWindow matrixWindow) {
		super(WindowNo, AD_Process_ID, tableId, recordId, autoStart );

		this.matrixWindow = matrixWindow;
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public JPiereMatrixWindowProcessModelDialog(EventListener<Event> listener,
			int WindowNo, int AD_Process_ID, int tableId, int recordId,
			boolean autoStart) {
		super(listener, WindowNo, AD_Process_ID, tableId, recordId, autoStart);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void onEvent(Event event)
	{
		super.onEvent(event);

		if(event.getName().equals("onComplete"))
		{
			try{
				matrixWindow.onEvent(event);
			}catch(Exception e){
				;
			}
		}
	}



}
