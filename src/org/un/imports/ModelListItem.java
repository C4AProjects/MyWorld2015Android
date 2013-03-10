package org.un.imports;

public class ModelListItem {

	private String title, description,optionCode,mdgColor;
	private  int counter=0;
	private boolean selected;
	
	

	public ModelListItem(String title,String desc, String code,String mdgColor) {
		this.title = title;
		this.description = desc;
		this.optionCode = code;
		this.mdgColor=mdgColor;
	}
	
	public void setTitle(String title) {
		this.title=title;
	}
	public String getTitle() {
		return title;
	}
	
	public void setDescription(String desc) {
		this.description=desc;;
	}
	public String getDescription() {
		return description;
	}
	
	public void setOptionCode(String code) {
		this.optionCode=code;
	}
	public String getOptionCode() {
		return optionCode;
	}
	
	public void setOptionColor(String color) {
		this.mdgColor=color;
	}
	public String getOptionColor() {
		return mdgColor;
	}

	public boolean isSelected() {
		return selected;
	}
	
	 public void setVoteCount(int count){
		 this.counter=count;
	 }
	 
	 public int getVoteCount(){
		return this.counter;
	 }
	

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
