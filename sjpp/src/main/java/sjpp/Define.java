package sjpp;

public class Define {

	private final String id;

	public Define(String id) {
		this.id = id;
	}

	public boolean doesApplyOn(String s) {
		return s.matches(".*when.*" + id + ".*");
	}
}
