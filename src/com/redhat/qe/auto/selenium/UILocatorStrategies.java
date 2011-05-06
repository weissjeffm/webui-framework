package com.redhat.qe.auto.selenium;


public class UILocatorStrategies {
    
    protected static final String NEXT_TO_XPATH_PREFIX = "//td[(normalize-space(.)='";
    
  //Locator Strategies
    public LocatorStrategy link = new LocatorTemplate("link", "//a[normalize-space(.)='$1']");
    public LocatorStrategy link_class = new LocatorTemplate("link", "//a[@class='$1']");
    public LocatorStrategy h1 = new LocatorTemplate("h1", "//h1[normalize-space(.)='$1']");
    public LocatorStrategy name = new StringSandwichLocatorStrategy("name", "name=");
    public LocatorStrategy button = new LocatorTemplate("button", "//*[@value='$1']");
    public LocatorStrategy id = new LocatorTemplate("element with id", "//*[normalize-space(@id)='$1']");
    public LocatorStrategy nav = new LocatorTemplate("nav bar", "//nav[@class='$1']");
    public LocatorStrategy href = new LocatorTemplate("href","//a[@href='$1']");
    public LocatorStrategy div = new LocatorTemplate("div","//div[@class='$1']");
    public LocatorStrategy divWithMessage = new LocatorTemplate("divWithMessage","//div[@class='$1' and normalize-space(.)='$2']");
    public LocatorStrategy table = new LocatorTemplate("table","//table[@class='$1']");
    public LocatorStrategy icon = new LocatorTemplate("icon","//input[@Class='icon' and @Name='$1']");
    public LocatorStrategy notification = new LocatorTemplate("notification","//div[@id='notification' and normalize-space(.)='$1']");
    public LocatorStrategy warning = new LocatorTemplate("warning","//div[@id='warning' and normalize-space(.)='$1']");
    public LocatorStrategy buttonWithParentHeader = new LocatorTemplate("buttonWithParentHeader","//h3[text()='$1']/following-sibling::input[@value='Launch']"); 
    public LocatorStrategy value = new LocatorTemplate("value", "//input[@value='$1']");
    public LocatorStrategy span = new LocatorTemplate("span", "//span[@class='$1']");
    public LocatorStrategy select_id = new LocatorTemplate("select_id","//select[@id='$1']");
   
    // find a particular table
    public LocatorStrategy wrappedTable = new LocatorTemplate("wrapped table","//div[@class='wrapped_table']//div[@class='title' and normalize-space(.)='$1']");
    // find a check box to the left of specified text
    public LocatorStrategy checkboxNextToText =    new StringSandwichLocatorStrategy("checkbox next to text", NEXT_TO_XPATH_PREFIX, "')]/..//input[@type='checkbox']");
    public LocatorStrategy radioButtonNextToText =    new StringSandwichLocatorStrategy("checkbox next to text", NEXT_TO_XPATH_PREFIX, "')]/..//input[@type='radio']");
    // find a cell in a table w/ specified txt
    public LocatorStrategy cellWithTextOrRequiredText =   new LocatorTemplate("cell with text","//tr/td[normalize-space(.)='$1')]") ;  // the rest can be used if the query is not exact // or normalize-space(.)='$1 *' or contains(.,'$1')]");
    public LocatorStrategy rowWithTextInColumnNumber =   new LocatorTemplate("row with text in column number","//tr[td[normalize-space(.)='$1' and position()='$2']]");
    public LocatorStrategy tableRowWithTextInColumnNumber =   new LocatorTemplate("table with row with text in column number","//table[@class='$1']//tr[td[normalize-space(.)='$2' and position()='$3']]");
    public LocatorStrategy cellInTableMatchingTwoColumns =   new LocatorTemplate("table with row with text in row matching two columns","//table[@class='$1']//tr[td[normalize-space(.)='$2' and position()='$3'] and td[normalize-space(.)='$4' and position()='$5']]");
   //End Locator Strategies
    
    public LocatorStrategy linkContains = new LocatorTemplate("contains","//a[contains(.,'$1')]");
    public LocatorStrategy linkStartswith = new LocatorTemplate("starts-with","//a[starts-with(.,'$1')]");
    
    //COMBINATIONS
    //button under a div w/ a particular id
    public LocatorStrategy id_button = new CombinedLocatorTemplate("id_button",id,button);
    public LocatorStrategy id_link = new CombinedLocatorTemplate("id_link",id,link);
    public LocatorStrategy nav_href = new CombinedLocatorTemplate("nav_href",nav,href);
    public LocatorStrategy nav_link = new CombinedLocatorTemplate("nav_link",nav,link);
    public LocatorStrategy div_id = new CombinedLocatorTemplate("div_id",div,id);
    public LocatorStrategy div_icon = new CombinedLocatorTemplate("div_icon",div,icon);
    public LocatorStrategy div_span = new CombinedLocatorTemplate("div_span",div,span);

}
