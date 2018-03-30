package z.media;

// 通用事件
public class Evt implements IMedia {
	public int mEvt = -1; // 事件id
	public int mArg = -1; // 参数
	public String mExtra = null; // 额外参数
	public String mSender = null; // 通知的id
	@Override
	public String toString() {
		switch (mEvt) {
		case Evt_Prepared:
			return "Evt@:Prepared(101),dur=" + mArg;
		case Evt_Complete:
			return "Evt@:Complete(102)";
		case Evt_Progress:
			return "Evt@:Progress(103)," + ((float)mArg) / 1000 + "s";
		case Evt_Error:
			return "Evt@:Error(104),err=" + mArg + ",ext=" + mExtra;
		case Evt_Buffering:
			return "Evt@:Buffering(105)," + mArg + "%";
		}
		return Evt.class.getName() + ":(" + mEvt + "," + mArg + "," + mExtra + ")" + mSender;
	}
}
