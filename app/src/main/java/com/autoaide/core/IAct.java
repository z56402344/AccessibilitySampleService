package com.autoaide.core;

import z.frame.ICommon;

// activity & fragment 相关基类
public interface IAct extends ICommon {
	String ID = "id";
	String DATA = "data";
	String kType = "type"; // 通用类型 区分进入界面的方式
}
