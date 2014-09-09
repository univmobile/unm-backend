package fr.univmobile.backend.sysadmin;

class Result {

	public Result(final int rowCount) {
		
		setRowCount(rowCount);
	}
	
	protected Result() {
		
	}
	
	private int rowCount = 0;

	public int getRowCount() {

		return rowCount;
	}

	protected void setRowCount(final int rowCount) {

		this.rowCount = rowCount;
	}

	protected void incRowCount() {

		++rowCount;
	}
}
