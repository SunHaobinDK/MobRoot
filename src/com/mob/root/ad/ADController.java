package com.mob.root.ad;

public class ADController {

	private static ADController adController;

	private ADController() {
	}

	public static ADController getInstance() {
		if (null == adController) {
			synchronized (ADController.class) {
				if (null == adController) {
					adController = new ADController();
				}
			}
		}
		return adController;
	}
}
