package com.mooo.mycoz.db;

public class Field {
	
	public static final String WHERE_BY_AND=" AND ";
	public static final String WHERE_BY_OR=" OR ";
	
	public static final String RULE_EQUAL=" = ";
	public static final String RULE_LIKE=" LIKE ";
	public static final String RULE_GREATER_EQUAL=" >= ";
	public static final String RULE_LESS_EQUAL=" <= ";
	public static final String RULE_IN=" IN ";
	
	private String fieldName;
	private Object fieldValue;
	
	private int fieldType;
	private int fieldLength;
	
	private boolean isNull;
	private boolean isPrimaryKey;
	private boolean isForeignKey;
	private boolean isKey;

	private boolean isSave;
	private boolean isUpdate;
	private boolean isDelete;
	
	private boolean isRetrieve;
	private boolean whereByEqual;
	private boolean whereByLike;
	private boolean whereByGreaterEqual;
	private boolean whereByLessEqual;
	
	private String whereBy;
	private String whereRule;
	
	private boolean isGroupBy;
	private boolean isOrderBy;
	
	public Field (String fieldName){
		this.fieldName = fieldName;
		this.fieldValue = null;
		
		this.fieldType = 0;
		this.fieldLength = 0;
		
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
		
		this.whereBy=WHERE_BY_AND;
		
		this.whereBy = null;
		this.whereRule = null;
		
		this.isGroupBy = false;
		this.isOrderBy = false;
	}
	public Field (String fieldName,boolean isGroupBy,boolean isOrderBy){
		this.fieldName = fieldName;
		this.fieldValue = null;
		
		this.fieldType = 0;
		this.fieldLength = 0;
		
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
		
		this.whereBy=WHERE_BY_AND;
		
		this.whereBy = null;
		this.whereRule = null;
		
		this.isGroupBy = isGroupBy;
		this.isOrderBy = isOrderBy;
	}
	
	public Field (String fieldName,Object fieldValue,int fieldType,String whereBy,String whereRule,boolean isPrimaryKey){
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		
		this.fieldType = fieldType;
		this.fieldLength = 0;
		
		this.isNull = true;
		this.isPrimaryKey = isPrimaryKey;
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
		this.whereBy=whereBy;
		this.whereRule=whereRule;
	}
	/*
	public Field (int fieldType,String fieldName,int fieldLength,
			boolean isNull,boolean isPrimaryKey,boolean isForeignKey,boolean isKey,
			boolean whereByEqual,boolean whereByLike,boolean whereByGreaterEqual,boolean whereByLessEqual,
			boolean isSave,boolean isUpdate,boolean isDelete,boolean isRetrieve,
			boolean groupBy,boolean orderBy,String whereBy){
		
		this.fieldType = fieldType;
		this.fieldName = fieldName;
		this.fieldLength = fieldLength;
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
		this.whereBy=whereBy;
	}
*/
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}

	public int getFieldType() {
		return fieldType;
	}

	public void setFieldType(int fieldType) {
		this.fieldType = fieldType;
	}

	public int getFieldLength() {
		return fieldLength;
	}

	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
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

	public String getWhereBy() {
		return whereBy;
	}

	public void setWhereBy(String whereBy) {
		this.whereBy = whereBy;
	}

	public String getWhereRule() {
		return whereRule;
	}

	public void setWhereRule(String whereRule) {
		this.whereRule = whereRule;
	}

	public boolean isGroupBy() {
		return isGroupBy;
	}

	public void setGroupBy(boolean isGroupBy) {
		this.isGroupBy = isGroupBy;
	}

	public boolean isOrderBy() {
		return isOrderBy;
	}

	public void setOrderBy(boolean isOrderBy) {
		this.isOrderBy = isOrderBy;
	}

}
