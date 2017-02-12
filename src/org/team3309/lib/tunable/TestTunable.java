package org.team3309.lib.tunable;

public class TestTunable implements IDashboard {

	@Override
	public String getTableName() { return "SmartDashboard"; }

	@Override
	public String getObjectName() { return "TestTunable"; }
	
	@Dashboard
	public int foo = 10;
	
	@Dashboard(tunable=true)
	public int bar = 5;
	
	@Dashboard
	public String baz = "bazybaz";

}
