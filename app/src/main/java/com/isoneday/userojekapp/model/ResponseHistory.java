package com.isoneday.userojekapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseHistory{

	@SerializedName("result")
	private String result;

	@SerializedName("msg")
	private String msg;

	@SerializedName("data")
	private List<DataHistory> data;

	public void setResult(String result){
		this.result = result;
	}

	public String getResult(){
		return result;
	}

	public void setMsg(String msg){
		this.msg = msg;
	}

	public String getMsg(){
		return msg;
	}

	public void setData(List<DataHistory> data){
		this.data = data;
	}

	public List<DataHistory> getData(){
		return data;
	}
}