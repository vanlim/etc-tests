package com.klabcyscorpions.beatsmaster.utils;

import aurelienribon.tweenengine.TweenAccessor;

import com.badlogic.gdx.math.Vector3;

public class Vector3TweenAccessor implements TweenAccessor<Vector3>
{
	public static final int position_x = 1;
    public static final int position_y = 2;
    public static final int position_z = 3;
    public static final int position_xy = 4;
    public static final int position_xz = 5;
    public static final int position_yz = 6;
    public static final int position_xyz = 7;

	public Vector3TweenAccessor()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getValues(Vector3 target, int tweenType, float[] returnValues)
	{
		switch(tweenType)
		{
			case position_x: returnValues[0] = target.x; return 1;
			case position_y: returnValues[0] = target.y; return 1;
			case position_z: returnValues[0] = target.z; return 1;
			case position_xy:
				returnValues[0] = target.x;
				returnValues[1] = target.y;
				return 2;
			case position_xz:
				returnValues[0] = target.x;
				returnValues[1] = target.z;
				return 2;
			case position_yz:
				returnValues[0] = target.y;
				returnValues[1] = target.z;
				return 2;
			case position_xyz:
				returnValues[0] = target.x;
				returnValues[1] = target.y;
				returnValues[2] = target.z;
				return 3;				
			default: assert false; return -1;
		}
	}

	@Override
	public void setValues(Vector3 target, int tweenType, float[] newValues)
	{
		switch (tweenType)
		{
			case position_x:
				target.set(newValues[0], target.y, target.z);
				break;
			case position_y:
				target.set(target.x, newValues[0], target.z);
				break;
			case position_z:
				target.set(target.x, target.y, newValues[0]);
				break;
			case position_xy:
				target.set(newValues[0], newValues[1], target.z);
				break;
			case position_xz:
				target.set(newValues[0], target.y, newValues[1]);
				break;
			case position_yz:
				target.set(target.x, newValues[0], newValues[1]);
				break;
			case position_xyz:
				target.set(newValues[0], newValues[1], newValues[2]);
				break;
            default: assert false; break;
        }
	}
}
