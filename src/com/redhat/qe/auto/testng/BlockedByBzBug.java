package com.redhat.qe.auto.testng;

/**
 * Use this class when you want to mark a row in a testNG DataProvider
 * as blocked by a bug in bugzilla.  The proper way to mark the 
 * row is to move the array of Objects that make up the parameters, 
 * to inside the constructor call of this class, eg
 * {a, b, c} -> new BlockedByBzBug("123456", a, b, c)
 * where "123456" is the id of the bug number that blocks this
 * test.  That may leave fewer than the required number of parameters
 * in the array for that row (from 3 Objects, to 1), but it will still
 * work because the items will be extracted before the test starts, 
 * if it turns out the bug no longer blocks the test.  Otherwise
 * the test will be skipped and the parameters won't be used anyway.
 
 * @author weissj
 *
 */
public class BlockedByBzBug extends BzBugDependency{

	/**
	 * * If you're getting IllegalArgumentException (or wrong number of arguments
	 * being thrown by TestNG, you need to use the BugzillaTestNGListener, which
	 * will unwrap the arguments before passing them to TestNG)
	 * @param bugId
	 * @param params
	 */
	public BlockedByBzBug(String bugId, Object... params) {
		this(new String[] {bugId}, params);
	}

	/**
	 * * If you're getting IllegalArgumentException (or wrong number of arguments
	 * being thrown by TestNG, you need to use the BugzillaTestNGListener, which
	 * will unwrap the arguments before passing them to TestNG)
	 * @param bugId
	 * @param params
	 */
	public BlockedByBzBug(String[] bugIds, Object... params) {
		super();
		this.bugIds = bugIds;
		this.params = params;
		this.type = Type.BlockedBy; 
	}

}
