package com.redhat.qe.auto.paginate;

public interface IPager {
	
	public void nextPage();
	
	public void previousPage();
	
	public void goToPage(int page);
	
	public int getTotalNumberOfPages();

	public boolean isLastPage();
}
