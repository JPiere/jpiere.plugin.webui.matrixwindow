package jpiere.plugin.matrixwindow.form;

import java.util.List;

import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.grid.WQuickEntry;
import org.zkoss.zk.ui.event.Event;

public class JPiereMatrixWindowQuickEntry extends WQuickEntry {

	private JPiereMatrixWindow matrixWindow;

	public JPiereMatrixWindowQuickEntry(int WindowNo, int AD_Window_ID,JPiereMatrixWindow mw) {
		super(WindowNo, AD_Window_ID);
		matrixWindow = mw;
	}

	public JPiereMatrixWindowQuickEntry(int AD_Window_ID) {
		super(AD_Window_ID);
		// TODO 自動生成されたコンストラクター・スタブ
	}


	public List<WEditor> getQuickEditors()
	{
		return quickEditors;
	}

	@Override
	public void onEvent(Event e) throws Exception
	{
		super.onEvent(e);

		if (e.getTarget().getId().equals("Ok"))
		{
			matrixWindow.onEvent(e);
		}


	}


}
