package com.mooo.mycoz.db;

public class Field {
	
	int type;
	String name;
	int length;
	boolean isNull;
	boolean isPrimaryKey;
	boolean isForeignKey;
	boolean isKey;
	boolean whereByEqual;
	boolean whereByLike;
	boolean whereByGreaterEqual;
	boolean whereByLessEqual;
	boolean isSave;
	boolean isUpdate;
	boolean isDelete;
	boolean isRetrieve;

	public Field (String name){
		this.type = 0;
		this.name = name;
		this.length = 0;
		this.isNull = true;
		this.isPrimaryKey = false;
		this.isForeignKey = false;
		this.isKey = false;
		this.whereByEqual = true;
		this.whereByLike = false;
		this.whereByGreaterEqual = false;
		this.whereByLessEqual = false;
		this.isSave = true;
		this.isUpdate = true;
		this.isDelete = true;
		this.isRetrieve = true;
	}
	
	public Field (String name,int type){
		this.type = type;
		this.name = name;
		this.length = 0;
		this.isNull = true;
		this.isPrimaryKey = false;
		this.isForeignKey = false;
		this.isKey = false;
		this.whereByEqual = true;
		this.whereByLike = false;
		this.whereByGreaterEqual = false;
		this.whereByLessEqual = false;
		this.isSave = true;
		this.isUpdate = true;
		this.isDelete = true;
		this.isRetrieve = true;
	}
	public Field (int type,String name,int length,
			boolean isNull,boolean isPrimaryKey,boolean isForeignKey,boolean isKey,
			boolean whereByEqual,boolean whereByLike,boolean whereByGreaterEqual,boolean whereByLessEqual,
			boolean isSave,boolean isUpdate,boolean isDelete,boolean isRetrieve,
			boolean groupBy,boolean orderBy){
		
		this.type = type;
		this.name = name;
		this.length = length;
		this.isNull = isNull;
		this.isPrimaryKey = isPrimaryKey;
		this.isForeignKey = isForeignKey;
		this.isKey = isKey;
		this.whereByEqual = whereByEqual;
		this.whereByLike = whereByLike;
		this.whereByGreaterEqual = whereByGreaterEqual;
		this.whereByLessEqual = whereByLessEqual;
		this.isSave = isSave;
		this.isUpdate = isUpdate;
		this.isDelete = isDelete;
		this.isRetrieve = isRetrieve;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	public boolean isForeignKey() {
		return isForeignKey;
	}

	public void setForeignKey(boolean isForeignKey) {
		this.isForeignKey = isForeignKey;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}

	public boolean isWhereByEqual() {
		return whereByEqual;
	}

	public void setWhereByEqual(boolean whereByEqual) {
		this.whereByEqual = whereByEqual;
	}

	public boolean isWhereByLike() {
		return whereByLike;
	}

	public void setWhereByLike(boolean whereByLike) {
		this.whereByLike = whereByLike;
	}

	public boolean isWhereByGreaterEqual() {
		return whereByGreaterEqual;
	}

	public void setWhereByGreaterEqual(boolean whereByGreaterEqual) {
		this.whereByGreaterEqual = whereByGreaterEqual;
	}

	public boolean isWhereByLessEqual() {
		return whereByLessEqual;
	}

	public void setWhereByLessEqual(boolean whereByLessEqual) {
		this.whereByLessEqual = whereByLessEqual;
	}

	public boolean isSave() {
		return isSave;
	}

	public void setSave(boolean isSave) {
		this.isSave = isSave;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public boolean isRetrieve() {
		return isRetrieve;
	}

	public void setRetrieve(boolean isRetrieve) {
		this.isRetrieve = isRetrieve;
	}

}
