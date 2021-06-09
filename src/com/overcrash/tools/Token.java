package com.overcrash.tools;

public class Token
{
	String vZtoken = "";
	String vZlocalIp = "";
	String vZmac = "";
	String vZmodel = "";
	String vZname = "";
	public String getvZtoken()
	{
		return vZtoken;
	}
	public void setvZtoken(String vZtoken)
	{
		this.vZtoken = vZtoken;
	}
	public String getvZlocalIp()
	{
		return vZlocalIp;
	}
	public void setvZlocalIp(String vZlocalIp)
	{
		this.vZlocalIp = vZlocalIp;
	}
	public String getvZmac()
	{
		return vZmac;
	}
	public void setvZmac(String vZmac)
	{
		this.vZmac = vZmac;
	}
	public String getvZmodel()
	{
		return vZmodel;
	}
	public void setvZmodel(String vZmodel)
	{
		this.vZmodel = vZmodel;
	}
	public String getvZname()
	{
		return vZname;
	}
	public void setvZname(String vZname)
	{
		this.vZname = vZname;
	}
	@Override
	public String toString()
	{
		return "Token [vZtoken=" + vZtoken + ", vZlocalIp=" + vZlocalIp + ", vZmac=" + vZmac + ", vZmodel=" + vZmodel + ", vZname=" + vZname + "]";
	}
}
