package com.J2;
/**
 * Yanked this code from LogBlock
 * https://github.com/bootswithdefer/LogBlock.git
 ***/
public class BlockRow // start
{
	public String name;
	public int replaced, type;
	public int x, y, z;
	public String extra;

	BlockRow(String name, int replaced, int type, int x, int y, int z)
	{
		this.name = name;
		this.replaced = replaced;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.extra = null;
	}

	public void addExtra(String extra)
	{
		this.extra = extra;
	}

	public String toString()
	{
		return("name: " + name + " before type: " + replaced + " type: " + type + " x: " + x + " y: " + y + " z: " + z);
	}
}