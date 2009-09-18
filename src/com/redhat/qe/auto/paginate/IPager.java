package com.redhat.qe.auto.paginate;

public interface IPager {
	
	public boolean isFirstPage();
	
	public boolean isLastPage();
	
	public void goToNextPage();
	
	public void goToPriorPage();
	
	public void goToFirstPage();
	
	public void goToLastPage();
	
	public void goToPage(int page);
	
	public int getTotalNumberOfPages();

	public int getTotalItems();
}
