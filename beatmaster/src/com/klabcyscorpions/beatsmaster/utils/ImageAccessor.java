package com.klabcyscorpions.beatsmaster.utils;

import aurelienribon.tweenengine.TweenAccessor;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ImageAccessor implements TweenAccessor<Image>
{
	public static final int position_x = 1;
    public static final int position_y = 2;
    public static final int position_xy = 3;
	

	public ImageAccessor()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getValues(Image target, int tweenType, float[] returnValues)
	{
		switch(tweenType)
		{
			case position_x: returnValues[0] = target.getX(); return 1;
			case position_y: returnValues[0] = target.getY(); return 1;	
			case position_xy: 
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				return 2;
			default: assert false; return -1;
		}
	}

	@Override
	public void setValues(Image target, int tweenType, float[] newValues)
	{
		switch (tweenType)
		{
			case position_x:
				target.setX(newValues[0]);
				break;
			case position_y:
				target.setY(newValues[0]);
				break;
			case position_xy:				
				target.setX(newValues[0]);
				target.setY(newValues[1]);
				break;			
            default: assert false; break;
        }
	}

}
