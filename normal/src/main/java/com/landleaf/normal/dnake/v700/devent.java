package com.landleaf.normal.dnake.v700;

import java.util.LinkedList;
import java.util.List;

public class devent {
	private static List<devent> elist = null;
	public static Boolean boot = false;

	public String url;

	public devent(String url) {
		this.url = url;
	}

	public void process(String xml) {
	}

	public static void event(String url, String xml) {
		Boolean err = true;
		if (boot && elist != null) {
			devent e;

			for (devent anElist : elist) {
				e = anElist;
				if (url.equals(e.url)) {
					e.process(xml);
					err = false;
					break;
				}
			}
		}
		if (err)
			dmsg.ack(480, null);
	}

	public static void setup() {

		elist = new LinkedList<>();

		devent de;

		de = new devent("/exApp/io") { // For exApp mode io process
			@Override
			public void process(String body) {
				dmsg.ack(200, null);

				dxml p = new dxml();
				p.parse(body);

				int io[] = new int[8];
				for (int i = 0; i < 8; i++) {
					// IO值, 0x00:正常状态 0x01:断开 0x02:闭合 0x10:状态未改变
					io[i] = p.getInt("/params/io" + i, 0);

					System.err.println("/params/io" + i + " = " + io[i]);
				}
			}
		};
		elist.add(de);
	}
}
