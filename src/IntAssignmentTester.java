
public class IntAssignmentTester {
	public double lat;
	public double lon;
	public IntAssignmentTester(double l1, double l2) {
		this.lat = l1;
		this.lon = l2;
	}
public static void main(String[] args)
{
	int var1 = 17;
	int var2 = var1+1;
	var1 = var2+1;
	System.out.println("var1: "+var1 + " var2: "+var2);
	IntAssignmentTester obj = new IntAssignmentTester(12.1,11.1);
	IntAssignmentTester obj1 = new IntAssignmentTester(13.1,14.1);
	obj = obj1;
	obj.lat= 15.1;
	System.out.println(obj1.lat +  " "+obj1.lon);
}
}
