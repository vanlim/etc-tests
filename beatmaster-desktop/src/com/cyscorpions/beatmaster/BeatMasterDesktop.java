package com.cyscorpions.beatmaster;

import interpolationtests.CurveTest;
import interpolationtests.InterpolationTest;
import tests.Test;
import tests.Test2;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class BeatMasterDesktop {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "beatmaster";
		cfg.useGL20 = false;
		cfg.width = 800;
		cfg.height = 480;
		
//		new LwjglApplication(new BeatMaster(), cfg);
//		new LwjglApplication(new Test(), cfg);
//		new LwjglApplication(new Test2(), cfg);
//		new LwjglApplication(new CurveTest(), cfg);
		new LwjglApplication(new InterpolationTest(), cfg);
	}
}
