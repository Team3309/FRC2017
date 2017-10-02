package team3309.pidControllers;

public class PIDControllerWithFeedForward {

	double kP, kI, kD, kV;
	double error;
	double goal;
	double prevError;
	double errorTotal;
	
	public void setConstants(double kP, double kI, double kD, double kV) {
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
		this.kV = kV;
	}
	
	public void setGoal(double goal) {
		this.goal = goal;
	}
	
	public void setError(double cur) {
		this.error = goal - cur;
	}
	
	public double getOutput() {
		double proportional = kP * error;
		double integral = kI * (error + errorTotal);
		double derivative = kD * (prevError - error);
		double feedForward = kV * goal;
		double output = proportional + integral + derivative + feedForward;
		errorTotal += error;
		prevError = error;
		return output;
	}
	
	
	
	
}
