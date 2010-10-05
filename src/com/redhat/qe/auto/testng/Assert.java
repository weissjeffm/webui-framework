package com.redhat.qe.auto.testng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




/**
 * Assertion tool class. Presents assertion methods with a more natural parameter order.
 * The order is always <B>actualValue</B>, <B>expectedValue</B> [, message].
 *
 * @author <a href='mailto:the_mindstorm@evolva.ro'>Alexandru Popescu</a>
 */
public class Assert {
	protected static Logger log = Logger.getLogger(Assert.class.getName());

  /**
   * Protect constructor since it is a static only class
   */
  protected Assert() {
    // hide constructor
  }
  
    public static <T> void assertContains(Collection<T> coll, T item ){
    	if (coll == null) {
    		fail(item + " isn't part of a null list."); 
    	}
    	boolean contains = coll.contains(item);
    	String formatString = "%s is present in the list %s"; 
    	if (!contains) {
    		formatString = "%s was not present in the list %s";	
    	}
    	String message = String.format(formatString, item.toString(), Arrays.deepToString(coll.toArray()));
    	if (contains){
    		pass(message);
    	}
    	else fail(message);
    }
  
	static public void assertMatch(String actual, String regex, String descriptionOfText) {
		String msg = String.format("%s'%s' matches regex '%s'", descriptionOfText+" ", actual, regex);
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
		assertTrue(p.matcher(actual).matches(), msg); 
	}

	static public void assertMatch(String actual, String regex){
		assertMatch(actual, regex, "Text "); 
	}

	static public void assertNoMatch(String actual, String regex, String where, String msg) {
		if (msg==null) msg = String.format("%s'%s' does NOT match regex '%s'", where+" ", actual, regex);
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
		assertFalse(p.matcher(actual).matches(), msg); 
	}

	static public void assertNoMatch(String actual, String regex, String where){
		assertNoMatch(actual, regex, where, null); 
	}

	static public void assertNoMatch(String actual, String regex){
		assertNoMatch(actual, regex, ""); 
	}
	
	
	/**
	 * Assert that the actual string (which may contain multiple lines) matches the specified regex.
	 * Because a MULTILINE pattern is compiled on the actual string, the ^ and $ regex characters
	 * will match against individual lines in the actual string.  Therefore, if your regex uses ^ and/or $,
	 * multiple matches may result and will get logged as such.
	 * @param actual
	 * @param regex
	 * @param where
	 * @param msg
	 * @author jsefler
	 */
	static public void assertContainsMatch(String actual, String regex, String where, String msg) {
		if (msg==null) msg = String.format("%s'%s' contains matches to regex '%s'", where+" ", actual, regex);

		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE/* | Pattern.DOTALL*/);
		Matcher matcher = pattern.matcher(actual);
		Assert.assertTrue(matcher.find(),msg); 

		log.fine("Matches: ");
		do {
			log.fine(matcher.group());
		} while (matcher.find());
	}

	static public void assertContainsMatch(String actual, String regex, String where) {
		assertContainsMatch(actual, regex, where, null);
	}

	static public void assertContainsMatch(String actual, String regex) {
		assertContainsMatch(actual, regex, "");
	}

	static public void assertContainsNoMatch(String actual, String regex, String where, String msg) {
		if (msg==null) msg = String.format("%s'%s' does NOT match regex '%s'", where+" ", actual, regex);
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE/* | Pattern.DOTALL*/);
		assertFalse(pattern.matcher(actual).find(), msg);
	}

	static public void assertContainsNoMatch(String actual, String regex, String where) {
		assertContainsNoMatch(actual, regex, where, null);
	}

	static public void assertContainsNoMatch(String actual, String regex) {
		assertContainsNoMatch(actual, regex, "");
	}
	
	
  /**
   * Asserts that a condition is true and passes the message. If it isn't,
   * an AssertionError, with the given message, is thrown.
   * @param condition the condition to evaluate
   * @param message the assertion message
   */
  static public void assertTrue(boolean condition, String message) {
    if(!condition) {
      failNotEquals( Boolean.valueOf(condition), Boolean.TRUE, message);
    }
    else pass(message);
  }
 
  /**
   * Asserts that a condition is true. If it isn't,
   * an AssertionError is thrown.
   * @param condition the condition to evaluate
   */
  static public void assertTrue(boolean condition) {
    assertTrue(condition, null);
  }
 
  /**
   * Asserts that a condition is false and passes the message. If it isn't,  
   * an AssertionError, with the given message, is thrown.
   * @param condition the condition to evaluate
   * @param message the assertion message
   */
  static public void assertFalse(boolean condition, String message) {
    if(condition) {
      failNotEquals( Boolean.valueOf(condition), Boolean.FALSE, message); // TESTNG-81
    }
    else pass(message);

  }
 
  /**
   * Asserts that a condition is false. If it isn't,    
   * an AssertionError is thrown.
   * @param condition the condition to evaluate
   */
  static public void assertFalse(boolean condition) {
    assertFalse(condition, null);
  }
 
  /**
   * Fails a test with the given message and wrapping the original exception.
   *
   * @param message the assertion message
   * @param realCause the original exception
   */
  static public void fail(String message, Throwable realCause) {
    AssertionError ae = new AssertionError(message);
    ae.initCause(realCause);
   
    throw ae;
  }
 
  /**
   * Fails a test with the given message.
   * @param message the assertion message
   */
  static public void fail(String message) {
    throw new AssertionError(message);
  }
 
  /**
   * Fails a test with no message.
   */
  static public void fail() {
    fail(null);
  }
 
  /**
   * Asserts that two objects are equal. If they are not,
   * an AssertionError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion error message
   * @param quiet If true, print nothing if the assertion is true.
   */
  static public void assertEquals(Object actual, Object expected, String message, boolean quiet) {
    if (  ((expected == null) && (actual == null)) || ((expected != null) && expected.equals(actual)) ) {
    	if (!quiet) {
    		pass("Actual value of '" + actual + "' matches expected value" + (message == null ? "." : (": " + message)));
    	}
    }
    else failNotEquals(actual, expected, message);
  }
  
  static public void assertEquals(Object actual, Object expected, Comparator comparator, String message) {
	  compare(actual, expected, comparator, message, 0);
  }

  public static void assertLess(Object actual, Object expected, Comparator comparator, String message){
	  compare(actual, expected, comparator, message, -1);	  
  }
  
  public static void assertMore(Object actual, Object expected, Comparator comparator, String message){
	  compare(actual, expected, comparator, message, 1);	  
  }
 
  protected static void compare(Object actual, Object expected, Comparator comparator, String message, int expComp){
	  String newMessage = message + ": " + actual + comparator.toString() + expected;
	  if (comparator.compare(actual, expected) == expComp){
	    	pass(newMessage);
	  }
	  else failNotEquals(actual, expected, newMessage);
  }
  
  
  static public void assertEquals(Object actual, Object expected, String message) {
	    assertEquals(actual, expected, message, false);
  }
	 
  /**
   * Asserts that two objects are equal. If they are not,
   * an AssertionError is thrown.
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertEquals(Object actual, Object expected) {
    assertEquals(actual, expected, null, false);
  }
 
  /**
   * Asserts that two Strings are equal. If they are not,
   * an AssertionError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message Description of the actual value (where it came from)
   */
  static public void assertEquals(String actual, String expected, String message) {
    assertEquals((Object) actual, (Object) expected, message, false);
  }
 
  /**
   * Asserts that two Strings are equal. If they are not,
   * an AssertionError is thrown.
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertEquals(String actual, String expected) {
    assertEquals(actual, expected, null);
  }
 
  /**
   * Asserts that two doubles are equal concerning a delta.  If they are not,
   * an AssertionError, with the given message, is thrown.  If the expected
   * value is infinity then the delta value is ignored.
   * @param actual the actual value
   * @param expected the expected value
   * @param delta the absolute tolerate value value between the actual and expected value
   * @param message the assertion message
   */
  static public void assertEquals(double actual, double expected, double delta, String message) {
    // handle infinity specially since subtracting to infinite values gives NaN and the
    // the following test fails
    if(Double.isInfinite(expected)) {
      if(!(expected == actual)) {
        failNotEquals(new Double(actual), new Double(expected), message);
      }
      else pass(message);
    }
    else if(!(Math.abs(expected - actual) <= delta)) { // Because comparison with NaN always returns false
      failNotEquals(new Double(actual), new Double(expected), message);
    }
    else pass(message);

  }
 
  /**
   * Asserts that two doubles are equal concerning a delta. If they are not,
   * an AssertionError is thrown. If the expected value is infinity then the
   * delta value is ignored.
   * @param actual the actual value
   * @param expected the expected value
   * @param delta the absolute tolerate value value between the actual and expected value
   */
  static public void assertEquals(double actual, double expected, double delta) {
    assertEquals(actual, expected, delta, null);
  }
 
  /**
   * Asserts that two floats are equal concerning a delta. If they are not,
   * an AssertionError, with the given message, is thrown.  If the expected
   * value is infinity then the delta value is ignored.
   * @param actual the actual value
   * @param expected the expected value
   * @param delta the absolute tolerate value value between the actual and expected value
   * @param message the assertion message
   */
  static public void assertEquals(float actual, float expected, float delta, String message) {
    // handle infinity specially since subtracting to infinite values gives NaN and the
    // the following test fails
    if(Float.isInfinite(expected)) {
      if(!(expected == actual)) {
        failNotEquals(new Float(actual), new Float(expected), message);
      }
      else pass(message);

    }
    else if(!(Math.abs(expected - actual) <= delta)) {
      failNotEquals(new Float(actual), new Float(expected), message);
    }
    else pass(message);

  }
 
  /**
   * Asserts that two floats are equal concerning a delta. If they are not,
   * an AssertionError is thrown. If the expected
   * value is infinity then the delta value is ignored.
   * @param actual the actual value
   * @param expected the expected value
   * @param delta the absolute tolerate value value between the actual and expected value
   */
  static public void assertEquals(float actual, float expected, float delta) {
    assertEquals(actual, expected, delta, null);
  }
 
  /**
   * Asserts that two longs are equal. If they are not,
   * an AssertionError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion message
   */
  static public void assertEquals(long actual, long expected, String message) {
    assertEquals(new Long(actual), new Long(expected), message, false);
  }
 
  /**
   * Asserts that two longs are equal. If they are not,
   * an AssertionError is thrown.
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertEquals(long actual, long expected) {
    assertEquals(actual, expected, null);
  }
 
  /**
   * Asserts that two booleans are equal. If they are not,
   * an AssertionError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion message
   */
  static public void assertEquals(boolean actual, boolean expected, String message) {
    assertEquals( Boolean.valueOf(actual), Boolean.valueOf(expected), message, false);
  }
 
  /**
   * Asserts that two booleans are equal. If they are not,
   * an AssertionError is thrown.
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertEquals(boolean actual, boolean expected) {
    assertEquals(actual, expected, null);
  }
 
  /**
   * Asserts that two bytes are equal. If they are not,
   * an AssertionError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion message
   */
  static public void assertEquals(byte actual, byte expected, String message) {
    assertEquals(new Byte(actual), new Byte(expected), message, false);
  }
 
  /**
   * Asserts that two bytes are equal. If they are not,
   * an AssertionError is thrown.
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertEquals(byte actual, byte expected) {
    assertEquals(actual, expected, null);
  }
 
  /**
   * Asserts that two chars are equal. If they are not,
   * an AssertionFailedError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion message
   */
  static public void assertEquals(char actual, char expected, String message) {
    assertEquals(new Character(actual), new Character(expected), message, false);
  }
 
  /**
   * Asserts that two chars are equal. If they are not,
   * an AssertionError is thrown.
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertEquals(char actual, char expected) {
    assertEquals(actual, expected, null);
  }
 
  /**
   * Asserts that two shorts are equal. If they are not,
   * an AssertionFailedError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion message
   */
  static public void assertEquals(short actual, short expected, String message) {
    assertEquals(new Short(actual), new Short(expected), message, false);
  }
 
  /**
   * Asserts that two shorts are equal. If they are not,
   * an AssertionError is thrown.
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertEquals(short actual, short expected) {
    assertEquals(actual, expected, null);
  }
 
  /**
   * Asserts that two ints are equal. If they are not,
   * an AssertionFailedError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion message
   */
  static public void assertEquals(int actual,  int expected, String message) {
    assertEquals(new Integer(actual), new Integer(expected), message, false);
  }
 
  /**
   * Asserts that two ints are equal. If they are not,
   * an AssertionError is thrown.
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertEquals(int actual, int expected) {
    assertEquals(actual, expected, null);
  }
 
  /**
   * Asserts that an object isn't null. If it is,
   * an AssertionError is thrown.
   * @param object the assertion object
   */
  static public void assertNotNull(Object object) {
    assertNotNull(object, null);
  }
 
  /**
   * Asserts that an object isn't null. If it is,
   * an AssertionFailedError, with the given message, is thrown.
   * @param object the assertion object
   * @param message the assertion message
   */
  static public void assertNotNull(Object object, String message) {
    assertTrue(object != null, message);
  }
 
  /**
   * Asserts that an object is null. If it is,
   * an AssertionError, with the given message, is thrown.
   * @param object the assertion object
   */
  static public void assertNull(Object object) {
    assertNull(object, null);
  }
 
  /**
   * Asserts that an object is null.  If it is not,
   * an AssertionFailedError, with the given message, is thrown.
   * @param object the assertion object
   * @param message the assertion message
   */
  static public void assertNull(Object object, String message) {
    assertTrue(object == null, message);
  }
 
  /**
   * Asserts that two objects refer to the same object. If they do not,
   * an AssertionFailedError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion message
   */
  static public void assertSame(Object actual, Object expected, String message) {
    if(expected == actual) {
      pass(message);
    }
    failNotSame(actual, expected, message);
  }
 
  /**
   * Asserts that two objects refer to the same object. If they do not,
   * an AssertionError is thrown.
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertSame(Object actual, Object expected) {
    assertSame(actual, expected, null);
  }
 
  /**

   * Asserts that two objects do not refer to the same objects. If they do,
   * an AssertionError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion message
   */
  static public void assertNotSame(Object actual, Object expected, String message) {
    if(expected == actual) {
      failSame(actual, expected, message);
    } else {
    	pass(message);
    }
  }
 
  /**
   * Asserts that two objects do not refer to the same object. If they do,
   * an AssertionError is thrown.
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertNotSame(Object actual, Object expected) {
    assertNotSame(actual, expected, null);
  }
 
  static private void failSame(Object actual, Object expected, String message) {
    String formatted = "";
    if(message != null) {
      formatted = message + " ";
    }
    fail(formatted + "expected not same with:<" + expected +"> but was same:<" + actual + ">");
  }
 
  static private void failNotSame(Object actual, Object expected, String message) {
    String formatted = "";
    if(message != null) {
      formatted = message + " ";
    }
    fail(formatted + "expected same with:<" + expected + "> but was:<" + actual + ">");
  }
 
  static private void failNotEquals(Object actual , Object expected, String message ) {
    fail(format(actual, expected, message));
  }
 
  static String format(Object actual, Object expected, String message) {
    String formatted = "";
    if (null != message) {
      formatted = message + " ";
    }
   
    return formatted + "expected:<" + expected + "> but was:<" + actual + ">";
  }
 
  /**
   * Asserts that two collections contain the same elements in the same order. If they do not,
   * an AssertionError is thrown.
   *
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertEquals(Collection actual, Collection expected) {
    assertEquals(actual, expected, null);
  }
 
  /**
   * Asserts that two collections contain the same elements in the same order. If they do not,
   * an AssertionError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion message
   */
  static public void assertEquals(Collection actual, Collection expected, String message) {
    if(actual == expected) return;
   
    if ((actual == null && expected != null) || (actual != null && expected == null)) {
      if (message != null) fail(message);
      else fail("Arrays not equal: " + expected + " and " + actual);
    }
   
    assertEquals(actual.size(), expected.size(), message + ": lists have the same size");
   
    Iterator actIt = actual.iterator();
    Iterator expIt = expected.iterator();
    int i = -1;
    while(actIt.hasNext() && expIt.hasNext()) {
      i++;
      Object e = expIt.next();
      Object a = actIt.next();
      String errorMessage = message == null
          ? "Lists match at element [" + i + "]: " + e + " != " + a
          : message + ": Lists match at element [" + i + "]: " + e + " != " + a;
     
      assertEquals(a, e, errorMessage, true);
    }
    pass(message+": lists have the same contents in the same order");
  }
 
  /**
   * Asserts that two arrays contain the same elements in the same order. If they do not,
   * an AssertionError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion message
   */
  static public void assertEquals(Object[] actual, Object[] expected, String message) {
    if(actual == expected) return;
   
    if ((actual == null && expected != null) || (actual != null && expected == null)) {
      if (message != null) fail(message);
      else fail("Arrays not equal: " + expected + " and " + actual);
    }
    assertEquals(Arrays.asList(actual), Arrays.asList(expected), message);
  }
 
  /**
   * Asserts that two arrays contain the same elements in no particular order. If they do not,
   * an AssertionError, with the given message, is thrown.
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion message
   */
  static public void assertEqualsNoOrder(Object[] actual, Object[] expected, String message) {
    if(actual == expected) return;
   
    if ((actual == null && expected != null) || (actual != null && expected == null)) {
      failAssertNoEqual(actual, expected,
          "Arrays not equal: " + expected + " and " + actual,
          message);
    }
   
    if (actual.length != expected.length) {
      failAssertNoEqual(actual, expected,
          "Arrays do not have the same size:" + actual.length + " != " + expected.length,
          message);
    }
   
    List actualCollection = new ArrayList();
    for (Object a : actual) {
      actualCollection.add(a);
    }
    for (Object o : expected) {
      actualCollection.remove(o);
    }
    if (actualCollection.size() != 0) {
      failAssertNoEqual(actual, expected,
          "Arrays not equal: " + expected + " and " + actual,
          message);
    }
  }
 
  private static void failAssertNoEqual(Object[] actual, Object[] expected,
      String message, String defaultMessage)
  {
    if (message != null) fail(message);
    else fail(defaultMessage);
  }
 
  /**
   * Asserts that two arrays contain the same elements in the same order. If they do not,
   * an AssertionError is thrown.
   *
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertEquals(Object[] actual, Object[] expected) {
    assertEquals(actual, expected, null);
  }
 
  /**
   * Asserts that two arrays contain the same elements in no particular order. If they do not,
   * an AssertionError is thrown.
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertEqualsNoOrder(Object[] actual, Object[] expected) {
    assertEqualsNoOrder(actual, expected, null);
  }
 
  /**
   * Asserts that two arrays contain the same elements in the same order. If they do not,
   * an AssertionError is thrown.
   *
   * @param actual the actual value
   * @param expected the expected value
   */
  static public void assertEquals(final byte[] actual, final byte[] expected) {
    assertEquals(actual, expected, "");
  }
  
  protected static void pass(String message){
	  log.log(Level.INFO, "Asserted: " + message, LogMessageUtil.Style.Asserted);
  }
 
  /**
   * Asserts that two arrays contain the same elements in the same order. If they do not,
   * an AssertionError, with the given message, is thrown.
   *
   * @param actual the actual value
   * @param expected the expected value
   * @param message the assertion message
   */
  static public void assertEquals(final byte[] actual, final byte[] expected, final String message) {
    if(expected == actual) {
      return;
    }
    if(null == expected) {
      fail("expected a null array, but not null found. " + message);
    }
    if(null == actual) {
      fail("expected not null array, but null found. " + message);
    }
   
    assertEquals(actual.length, expected.length, "arrays don't have the same size. " + message);
   
    for(int i= 0; i < expected.length; i++) {
      if(expected[i] != actual[i]) {
        fail("arrays differ firstly at element [" + i +"]; "
            + "expected value is <" + expected[i] +"> but was <"
            + actual[i] + ">. "
            + message);
      }
    }
  }
  
  public static void main(String... args){
	  List<String> list = Arrays.asList(new String[] {"foo", "bar", "baz"});
	  Assert.assertContains(list, "foo");
	  Assert.assertContains(list, "quux");
  }
}


