package com.gongsik.gsr.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ItemListDto implements Serializable{
	
	private String invenLClsNo;
	private String invenMClsNo;
	private String invenSClsNo;
	private String invenLClsNm;
	private String invenMClsNm;
	private String invenSClsNm;
	private int invenCnt;
	private String invenPrice;
}
