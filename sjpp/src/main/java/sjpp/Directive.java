package sjpp;

public enum Directive {
	REMOVE_FILE, //
	REMOVE_CURRENT_FOLDER, //
	REMOVE_FOLDER_AND_SUBFOLDERS, //
	COMMENT, //
	UNCOMMENT, //
	REVERT, //
	DONE, //
	IMPORT, //
	NONE;

	public static Directive directiveInLine(String line) {
		if (line.startsWith("import "))
			return Directive.IMPORT;

		line = line.replaceAll("\\s+", "");
		if (line.startsWith("//::") == false)
			return Directive.NONE;
		
		if (line.startsWith("//::removefilewhen"))
			return Directive.REMOVE_FILE;
		
		if (line.startsWith("//::removecurrentfolderwhen"))
			return Directive.REMOVE_CURRENT_FOLDER;
		
		if (line.startsWith("//::removefolderwhen"))
			return Directive.REMOVE_FOLDER_AND_SUBFOLDERS;
		
		if (line.startsWith("//::commentwhen"))
			return Directive.COMMENT;
		
		if (line.startsWith("//::revertwhen"))
			return Directive.REVERT;
		
		if (line.startsWith("//::uncommentwhen"))
			return Directive.UNCOMMENT;
		
		if (line.startsWith("//::done"))
			return Directive.DONE;
		
		return Directive.NONE;
	}
}
