package org.team3309.lib.tunable;

public class TestTunable implements ITunable {

	@Override
	public String getTableName() { return "SmartDashboard"; }

	@Override
	public String getObjectName() { return "TestTunable"; }
	
	@Tunable
	public int foo = 10;
	
	@Tunable(ReadOnly=true)
	public int bar = 5;
	
	@Tunable
	public String baz = "bazybaz";

}
