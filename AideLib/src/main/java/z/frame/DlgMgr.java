package z.frame;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;

public class DlgMgr implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener, ICommon {
	public HashMap<Integer, Dialog> mDlgs = new HashMap<Integer, Dialog>();
	public Context mCtx = null;
	public Dialog findDialogById(int id) {
		return mDlgs.get(id);
	}
	public void hideDlg(int id) {
		Dialog dlg = mDlgs.remove(id);
		if (dlg!=null) {
			if (dlg.isShowing())
				dlg.dismiss();
		}
	}
	public Dialog showDlg(int id, View root, boolean bCancel, int style) {
		hideDlg(id);
		if (mCtx==null) return null;
		Dialog dlg = null;
		if (style!=0) {
			dlg = new Dialog(mCtx, style);
		} else {
			dlg = new Dialog(mCtx);
		}
		LinearLayout ll = new LinearLayout(mCtx);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		ll.setLayoutParams(params);
		Util.setParent(root,ll,null);
		root.setId(id);
		root.setTag("dialogroot");
		dlg.setContentView(ll);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setCancelable(bCancel);
		dlg.setOnCancelListener(this);
		dlg.setOnDismissListener(this);
		mDlgs.put(id, dlg);
		try {
			dlg.show();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return dlg;
	}

	public void showLoading(boolean bCancel) {
		showLoading(bCancel, -1);
	}

	public void showLoading(boolean bCancel, int fullScreenStyle) {
		Dialog dlg = findDialogById(ID_Loading);
		if (dlg!=null) {
			dlg.setCancelable(bCancel);
			return;
		}
		if (mCtx != null){
			showDlg(ID_Loading, View.inflate(mCtx, app.Loading_Layout, null),
					bCancel, fullScreenStyle == -1 ? app.Loading_Style : fullScreenStyle);
		}
	}
	public void hideLoading() {
		hideDlg(ID_Loading);
	}
	public void hideAllDialog() {
		for (Map.Entry<Integer, Dialog> e: mDlgs.entrySet()) {
			Dialog dlg = e.getValue();
			try {
				if (dlg.isShowing()) dlg.dismiss();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
		mDlgs.clear();
	}
	@Override
	public void onCancel(DialogInterface dialog) {
	}
	public void enable(Context ctx) {
		mCtx = ctx;
	}
	// 加载资源
	public View inflate(int layout) {
		return mCtx!=null?View.inflate(mCtx,layout,null):null;
	}
	@Override
	public void onDismiss(DialogInterface dialog) {
		if (!(dialog instanceof Dialog)) return;
		Dialog dlg = (Dialog)dialog;
		View root = dlg.getWindow().getDecorView().findViewWithTag("dialogroot");
		if (root==null) return;
		Dialog old = mDlgs.remove(root.getId());
	}
}
