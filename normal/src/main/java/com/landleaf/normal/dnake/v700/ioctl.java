package com.landleaf.normal.dnake.v700;

public class ioctl {
	private static int hooter_onoff = 100;

	// 12V DO, onoff: 0->关闭12V 1->输出12V
	public static void hooter(int onoff) {
		if (hooter_onoff != onoff) {
			dmsg req = new dmsg();
			dxml p = new dxml();
			p.setInt("/params/onoff", onoff);
			req.to("/control/hooter", p.toString());
			hooter_onoff = onoff;
		}
	}
}
