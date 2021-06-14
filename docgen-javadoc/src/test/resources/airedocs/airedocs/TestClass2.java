package airedocs;

/**
 * @group Test Components
 * @component
 * *This is a pretty cool component*
 *
 * 1. markdown list element 1
 * 1. md 2
 *
 * <code lang="groovy">
 *   def myComponent = new TestClass2();
 *   add(myComponent)
 * </code>
 * <code lang="java">
 *   var myComponent = new TestClass2();
 *   add(myComponent)
 * </code>
 */
public class TestClass2 {

  /**
   * @property
   * this is a pretty cool property
   * <code lang="groovy">
   *   def myComponent = new TestClass2();
   *   add(myComponent)
   * </code>
   * <code lang="java">
   *   var myComponent = new TestClass2();
   *   add(myComponent)
   * </code>
   */
  private String attribute1;


  /**
   * @group Whatevers
   * @component
   * this is a pretty cool component, don't you think so?
   * <code lang="groovy">
   *   def myComponent = new TestClass2();
   *   add(myComponent)
   * </code>
   * <code lang="java">
   *   var myComponent = new TestClass2();
   *   add(myComponent)
   * </code>
   *
   * Idk what else to do with this
   *
   */
  public static class InnerComponent {

    /**
     * @property
     * <code lang="groovy">
     *   def myComponent = new TestClass2();
     *   add(myComponent)
     * </code>
     * <code lang="java">
     *   var myComponent = new TestClass2();
     *   add(myComponent)
     * </code>
     * <code lang="clojure">
     *   (doto (airedocs.TestComponent.)
     *      (.add (aire.Button "hello")))
     * </code>
     */
    TestClass2 parent;

  }

}