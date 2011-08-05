package com.mooo.mycoz.db;

public class ExtentField<T> {
	T start;
	T end;
	
	public ExtentField(T start,T end){
		this.start = start;
		this.end = end;
	}
	
	public T getStart() {
		return start;
	}

	public void setStart(T start) {
		this.start = start;
	}

	public T getEnd() {
		return end;
	}

	public void setEnd(T end) {
		this.end = end;
	}
}
