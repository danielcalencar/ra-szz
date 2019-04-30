import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.app.DiffJ;
import org.incava.ijdk.text.Location;


public class Example {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        //FileDiff a = new FileDiffChange("msg", new Location(0, 0), new Location(1, 1), new Location(0, 0), new Location(1, 1));
        //FileDiff b = a;
		String fromLabel = "S://Program Files//DiffJ//src/test//resources/diffj/codecomp//d0//Changed.java";
		String toLabel = "S://Program Files//DiffJ//src/test//resources/diffj/codecomp//d1//Changed.java";
		DiffJ diffJ = new DiffJ(true, false, true, false, fromLabel, "1.6", toLabel, "1.6");
		//diffJ.

	}

}
